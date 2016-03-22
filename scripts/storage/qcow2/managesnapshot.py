#!/usr/bin/python
# -*- coding: utf-8 -*-
# Loic Lambiel, exoscale  ©

import argparse
import logging
import logging.handlers
import time
import os
import shutil
import socket
import json
import subprocess

import libvirt
import facter
import bernhard

from lxml import etree
from raven import Client


def getargs():
    parser = argparse.ArgumentParser(description='This script performs snapshot operations for a domain disk using libvirt. Requirements are QEMU >= 2.0, libvirt >= 1.2.9')
    group = parser.add_mutually_exclusive_group()
    parser.add_argument('-version', action='version', version='%(prog)s 0.3, Loic Lambiel exoscale')
    group.add_argument('-c', help='Create Snapshot', required=False, action='store_true', dest='createarg')
    group.add_argument('-d', help='Destroy Snapshot', required=False, action='store_true', dest='destroyarg')
    group.add_argument('-b', help='Backup Snapshot', required=False, action='store_true', dest='backuparg')
    group.add_argument('-v', help='Revert Snapshot', required=False, action='store_true', dest='revertarg')
    parser.add_argument('-p', help='Destination directory for the snapshot. Mandatory with -b', required=False, type=str, dest='backupdir')
    parser.add_argument('-t', help='Destination file name. Mandatory with -b', required=False, type=str, dest='backup_file_name')
    parser.add_argument('-s', help='Backuped snapshot file path. Mandatory with -v', required=False, type=str, dest='backupedsnapshot_path')
    parser.add_argument('-diskpath', help='Path of the disk to snapshot', required=True, type=str, dest='disk_path')
    parser.add_argument('-domain', help='The name of the VM', required=True, type=str, dest='domain')
    args = vars(parser.parse_args())
    return args


def create_snapshot(disk_path, domain, snapshot_file_path):

    logging.info('Create snapshot request with arguments: -c -diskpath %s -domain %s', disk_path, domain)

    # Check if the running binary is supported
    logging.info('Checking QEMU version for domain %s', domain)
    qemuversion = qemu_version_check(domain)

    if qemuversion < 2:
        logging.error('Running QEMU version is not supported for domain %s', domain)
        raise Exception('Running QEMU version is not supported for this domain')

    # Checking if domain already have a snapshot
    if os.path.exists(snapshot_file_path):
        logging.error('A snapshot already exists for domain %s', domain)
        raise Exception('A snapshot already exists for this domain')

    logging.info('Snapshot file will be %s', snapshot_file_path)

    snap_flags = (libvirt.VIR_DOMAIN_SNAPSHOT_CREATE_DISK_ONLY |
                  libvirt.VIR_DOMAIN_SNAPSHOT_CREATE_NO_METADATA)

    try:
        conn = libvirt.open('qemu:///system')
    except Exception as e:
        logging.error('Libvirt failed to connect to qemu')
        logging.error('%s', e)
        raise

    try:
        dom0 = conn.lookupByName(domain)
    except Exception as e:
        logging.error('%s domain was not found on this hypervisor', domain)
        logging.error('%s', e)
        raise

    currentdatetime = time.strftime("%Y-%m-%d %H:%M:%S")

    # Generate XML file
    root = etree.Element("domainsnapshot")
    name = etree.SubElement(root, "name")
    name.text = "%s" % (domain)
    description = etree.SubElement(root, "description")
    description.text = "%s" % (currentdatetime)
    disks = etree.SubElement(root, "disks")
    disk = etree.SubElement(disks, "disk")
    disk.set('name', disk_path)
    source = etree.SubElement(disk, "source")
    source.set('file', snapshot_file_path)
    driver = etree.SubElement(disk, "driver")
    driver.text = "type='qcow2'"
    s = etree.tostring(root, pretty_print=True)

    # Create the snapshot
    try:
        logging.info('Creating snapshot %s for vm %s', snapshot_file_path, domain)
        dom0.snapshotCreateXML(s, snap_flags)
        logging.info('Successfully created snapshot %s for vm %s', snapshot_file_path, domain)
    except Exception as e:
        logging.error('Failed to create snapshot %s for vm %s', snapshot_file_path, domain)
        logging.error('%s', e)
        logging.error('Destroying snapshot')
        destroy_snapshot(disk_path, domain, snapshot_file_path)
        raise


def destroy_snapshot(disk_path, domain, snapshot_file_path):

    logging.info('Destroy snapshot request with arguments: -d -diskpath %s -domain %s', disk_path, domain)

    try:
        conn = libvirt.open('qemu:///system')
    except Exception as e:
        logging.error('Libvirt failed to connect to qemu')
        logging.error('%s', e)
        raise

    try:
        dom0 = conn.lookupByName(domain)
    except Exception as e:
        logging.error('%s domain was not found on this hypervisor', domain)
        logging.error('%s', e)
        raise

    # get the device name from domain xml
    dev = getdev(dom0, snapshot_file_path)

    bandwidth = 0
    commit_flags = (libvirt.VIR_DOMAIN_BLOCK_COMMIT_ACTIVE | libvirt.VIR_DOMAIN_BLOCK_COMMIT_RELATIVE)

    commit_disk = dev
    commit_base = disk_path
    commit_top = snapshot_file_path

    # Destory the snapshot using blockcommit
    try:
        logging.info('Starting blockcommit %s for vm %s', snapshot_file_path, domain)
        dom0.blockCommit(commit_disk, commit_base, commit_top, bandwidth, commit_flags)
        logging.info('Blockcommit successfully started')
    except Exception as e:
        logging.error('An error occured during snapshot blockcommit %s for vm %s', snapshot_file_path, domain)
        logging.error('%s', e)
        raise

    # Wait until blockcommit is completed
    logging.info('Waiting for blockCommit job completion')
    while wait_for_block_job(dom0, dev, abort_on_error=True):
        time.sleep(1)
    logging.info('BlockCommit job completed')

    # We can now pivot to base disk
    try:
        # sleep to try to workaround this bug: https://gitlab.com/libvirt/libvirt/commit/eae59247c59aa02147b2b4a50177e8e877fdb218
        # until libvirt get updated to 1.2.18 at least
        time.sleep(2)
        logging.info('Starting disk pivot from %s to base %s for vm %s', snapshot_file_path, disk_path, domain)
        dom0.blockJobAbort(dev, libvirt.VIR_DOMAIN_BLOCK_JOB_ABORT_PIVOT)
        logging.info('Disk pivot to base successfully completed from %s to %s for vm %s', snapshot_file_path, disk_path, domain)
    except Exception as e:
        logging.error('An error occured during the pivot of snapshot %s to %s for domain %s', snapshot_file_path, disk_path, domain)
        logging.error('%s', e)
        raise

    # Removing old snapshot file
    try:
        logging.info('Removing old snapshot file %s', snapshot_file_path)
        os.remove(snapshot_file_path)
        logging.info('Successfully removed snapshot file %s', snapshot_file_path)
    except Exception as e:
        logging.error('An error occured during the removal of snapshot file %s', snapshot_file_path)
        logging.error('%s', e)
        raise


def backup_snapshot(disk_path, domain, backupdir, backup_file_name, snapshot_file_path):
    src = disk_path
    dst = backupdir + "/" + backup_file_name
    logging.info('Backup snapshot request with arguments: -b -diskpath %s -domain %s -p %s -t %s', disk_path, domain, backupdir, backup_file_name)

    try:
        logging.info('Backuping snapshot %s to %s', src, dst)
        if not os.path.isdir(backupdir):
            os.makedirs(backupdir, 0777)
        shutil.copyfile(src, dst)
        logging.info('Snapshot backup completed %s to %s', src, dst)
    except Exception as e:
        logging.error('An error occured during snapshot backup')
        logging.error('%s', e)
        logging.error('Destroying snapshot')
        destroy_snapshot(disk_path, domain, snapshot_file_path)
        raise


def revert_snapshot(disk_path, domain, backupedsnapshot_path):
    src = backupedsnapshot_path
    dst = disk_path
    logging.info('Revert snapshot request with arguments: -v -diskpath %s -domain %s -s %s', disk_path, domain, backupedsnapshot_path)

    try:
        logging.info('Reverting snapshot %s to %s', src, dst)
        shutil.copyfile(src, dst)
        logging.info('Snapshot revert completed %s to %s', src, dst)
    except Exception as e:
        logging.error('An error occured during snapshot revert')
        logging.error('%s', e)
        raise


def getdev(dom0, snapshot_file_path):
    xmldesc = dom0.XMLDesc(0)
    root = etree.fromstring(xmldesc)
    for i in root.findall('./devices/disk'):
        device = {}
        if 'disk' in i.attrib.values():
            child = i.getchildren()
            for childitems in child:
                if childitems.tag == 'source':
                    device['source'] = (childitems.get('file'))
                elif childitems.tag == 'target':
                    device['dev'] = (childitems.get('dev'))
        if snapshot_file_path in device.values():
            dev = device.get('dev')
            return dev


def wait_for_block_job(dom0, dev, abort_on_error=False,
                       wait_for_job_clean=False):
    """Wait for libvirt block job to complete.
    Libvirt may return either cur==end or an empty dict when
    the job is complete, depending on whether the job has been
    cleaned up by libvirt yet, or not.
    :returns: True if still in progress
              False if completed
    """

    status = dom0.blockJobInfo(dev, 0)
    if status == -1 and abort_on_error:
        logging.error('libvirt error while requesting blockjob info')
        raise libvirt.VIR_ERR_OPERATION_FAILED('libvirt error while requesting blockjob info')
    try:
        cur = status.get('cur', 0)
        end = status.get('end', 0)
    except Exception:
        return False

    if wait_for_job_clean:
        job_ended = not status
    else:
        job_ended = cur == end and cur != 0 and end != 0

    return not job_ended


def qemu_version_check(domain):
    virshpath = '/usr/bin/virsh'
    virsharg1 = 'qemu-monitor-command'
    virsharg2 = domain
    virsharg3 = '{"execute":"query-version"}'

    try:
        execute = subprocess.check_output([virshpath, virsharg1, virsharg2, virsharg3])
        execute = json.loads(execute)
        qemuversion = execute["return"]["qemu"]["major"]
    except Exception as e:
        logging.error('%s', e)
        raise

    if qemuversion is not None:
        return qemuversion
    else:
        logging.error('Failed to get running qemu version for domain %s', domain)
        raise ValueError('Failed to get running qemu version')

if __name__ == "__main__":
    args = getargs()
    createarg = args['createarg']
    destroyarg = args['destroyarg']
    backuparg = args['backuparg']
    revertarg = args['revertarg']
    backupdir = args['backupdir']
    backup_file_name = args['backup_file_name']
    backupedsnapshot_path = args['backupedsnapshot_path']
    disk_path = args['disk_path']
    domain = args['domain']

    snapshot_file_name = disk_path.split('/')[-1] + "-snap.qcow2"
    path = disk_path.split('/')
    del path[-1]
    path = '/'.join(path)
    snapshot_file_path = path + "/" + snapshot_file_name

    logfile = "/var/log/cloudstack/agent/snapshot.log"
    logging.basicConfig(format='%(asctime)s %(pathname)s %(levelname)s:%(message)s', level=logging.DEBUG, filename=logfile)

    facts = facter.Facter(facter_path="/usr/local/bin/facter")
    riemannserver = facts["riemannserver"]
    sentryapikey = facts["snapshotsentrykey"]

    bclient = bernhard.Client(host=riemannserver)
    host = socket.gethostname()

    maintenancefilepath = '/etc/cloudstack/agent/snapshot-maintenance'

    try:
        # Checking if snapshot is admin disabled
        if os.path.exists(maintenancefilepath):
            logging.error('Snapshotting is admin disabled on this hypervisor, you may enable it back by deleting %s', maintenancefilepath)
            raise Exception('Snapshotting is admin disabled on this hypervisor')
        if createarg:
            create_snapshot(disk_path, domain, snapshot_file_path)
        elif destroyarg:
            destroy_snapshot(disk_path, domain, snapshot_file_path)
        elif backuparg:
            backup_snapshot(disk_path, domain, backupdir, backup_file_name, snapshot_file_path)
        elif revertarg:
            revert_snapshot(disk_path, domain, backupedsnapshot_path)

        # No error, we send an ok riemann event
        service = "Cloudstack-Snapshot-%s" % (domain)
        bclient.send({'host': host,
                      'service': service,
                      'state': 'ok',
                      'tags': ['Cloudstack-Snapshot'],
                      'ttl': 3800,
                      'metric': 0})
    except Exception as e:
        client = Client(dsn=sentryapikey)
        client.captureException()
        s = str(e)
        service = "Cloudstack-Snapshot-%s" % (domain)
        bclient.send({'host': host,
                      'service': service,
                      'description': s,
                      'state': 'warning',
                      'tags': ['Cloudstack-Snapshot'],
                      'ttl': 3800,
                      'metric': 1})
        raise
