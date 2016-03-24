package com.cloud.hypervisor.kvm.resource;

import org.libvirt.Connect;
import org.libvirt.Domain;
import org.libvirt.LibvirtException;

import java.util.concurrent.Callable;

public class MigrateKVMAsync implements Callable<Domain> {
    Domain dm = null;
    Connect dconn = null;
    String dxml = "";
    String vmName = "";
    long flags = 0L;
    int migrationSpeed = 0;

    MigrateKVMAsync(Domain dm, Connect dconn, String dxml, String vmName, long flags, int migrationSpeed) {
        this.dm = dm;
        this.dconn = dconn;
        this.dxml = dxml;
        this.vmName = vmName;
        this.flags = flags;
        this.migrationSpeed = migrationSpeed;
    }

    @Override
    public Domain call() throws LibvirtException {
        // For version of libvirt that don't support compression, remove the flag
        if (dconn.getLibVirVersion() < 1003000) {
            flags = flags & (0 << 11);
        }
        return dm.migrate(dconn, flags, dxml, vmName, null, migrationSpeed);
    }
}

