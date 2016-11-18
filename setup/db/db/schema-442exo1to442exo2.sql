-- Licensed to the Apache Software Foundation (ASF) under one
-- or more contributor license agreements.  See the NOTICE file
-- distributed with this work for additional information
-- regarding copyright ownership.  The ASF licenses this file
-- to you under the Apache License, Version 2.0 (the
-- "License"); you may not use this file except in compliance
-- with the License.  You may obtain a copy of the License at
--
--   http://www.apache.org/licenses/LICENSE-2.0
--
-- Unless required by applicable law or agreed to in writing,
-- software distributed under the License is distributed on an
-- "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
-- KIND, either express or implied.  See the License for the
-- specific language governing permissions and limitations
-- under the License.

--;
-- Schema upgrade from 4.4.2-exo-1 to 4.4.2-exo-2;
--;


-- 2016.10.14 Add associated field to user_ip_address
ALTER TABLE user_ip_address ADD IF NOT EXISTS associated datetime DEFAULT NULL COMMENT 'Date this ip was associated to the account',
                            ADD IF NOT EXISTS is_elastic tinyint(1) NOT NULL DEFAULT 0 COMMENT 'True if the ip address is used as an elastic ip';

-- 2016.11.14 Add limits on EIP
CREATE OR REPLACE VIEW `account_view`
AS SELECT
   `account`.`id` AS `id`,
   `account`.`uuid` AS `uuid`,
   `account`.`account_name` AS `account_name`,
   `account`.`type` AS `type`,
   `account`.`state` AS `state`,
   `account`.`removed` AS `removed`,
   `account`.`cleanup_needed` AS `cleanup_needed`,
   `account`.`network_domain` AS `network_domain`,
   `account`.`default` AS `default`,
   `domain`.`id` AS `domain_id`,
   `domain`.`uuid` AS `domain_uuid`,
   `domain`.`name` AS `domain_name`,
   `domain`.`path` AS `domain_path`,
   `data_center`.`id` AS `data_center_id`,
   `data_center`.`uuid` AS `data_center_uuid`,
   `data_center`.`name` AS `data_center_name`,
   `account_netstats_view`.`bytesReceived` AS `bytesReceived`,
   `account_netstats_view`.`bytesSent` AS `bytesSent`,
   `vmlimit`.`max` AS `vmLimit`,
   `vmcount`.`count` AS `vmTotal`,
   `runningvm`.`vmcount` AS `runningVms`,
   `stoppedvm`.`vmcount` AS `stoppedVms`,
   `eiplimit`.`max` AS `eipLimit`,
   `iplimit`.`max` AS `ipLimit`,
   `ipcount`.`count` AS `ipTotal`,
   `free_ip_view`.`free_ip` AS `ipFree`,
   `volumelimit`.`max` AS `volumeLimit`,
   `volumecount`.`count` AS `volumeTotal`,
   `snapshotlimit`.`max` AS `snapshotLimit`,
   `snapshotcount`.`count` AS `snapshotTotal`,
   `templatelimit`.`max` AS `templateLimit`,
   `templatecount`.`count` AS `templateTotal`,
   `vpclimit`.`max` AS `vpcLimit`,
   `vpccount`.`count` AS `vpcTotal`,
   `projectlimit`.`max` AS `projectLimit`,
   `projectcount`.`count` AS `projectTotal`,
   `networklimit`.`max` AS `networkLimit`,
   `networkcount`.`count` AS `networkTotal`,
   `cpulimit`.`max` AS `cpuLimit`,
   `cpucount`.`count` AS `cpuTotal`,
   `memorylimit`.`max` AS `memoryLimit`,
   `memorycount`.`count` AS `memoryTotal`,
   `primary_storage_limit`.`max` AS `primaryStorageLimit`,
   `primary_storage_count`.`count` AS `primaryStorageTotal`,
   `secondary_storage_limit`.`max` AS `secondaryStorageLimit`,
   `secondary_storage_count`.`count` AS `secondaryStorageTotal`,
   `async_job`.`id` AS `job_id`,
   `async_job`.`uuid` AS `job_uuid`,
   `async_job`.`job_status` AS `job_status`,
   `async_job`.`account_id` AS `job_account_id`
FROM (`free_ip_view`
   join (((((((((((((((((((((((((((((((
   `account` join `domain` on((`account`.`domain_id` = `domain`.`id`)))
   left join `data_center` on((`account`.`default_zone_id` = `data_center`.`id`)))
   left join `account_netstats_view` on((`account`.`id` = `account_netstats_view`.`account_id`)))
   left join `resource_limit` `vmlimit` on(((`account`.`id` = `vmlimit`.`account_id`) and (`vmlimit`.`type` = 'user_vm'))))
   left join `resource_count` `vmcount` on(((`account`.`id` = `vmcount`.`account_id`) and (`vmcount`.`type` = 'user_vm'))))
   left join `account_vmstats_view` `runningvm` on(((`account`.`id` = `runningvm`.`account_id`) and (`runningvm`.`state` = 'Running'))))
   left join `account_vmstats_view` `stoppedvm` on(((`account`.`id` = `stoppedvm`.`account_id`) and (`stoppedvm`.`state` = 'Stopped'))))
   left join `resource_limit` `eiplimit` on(((`account`.`id` = `eiplimit`.`account_id`) and (`eiplimit`.`type` = 'public_elastic_ip'))))
   left join `resource_limit` `iplimit` on(((`account`.`id` = `iplimit`.`account_id`) and (`iplimit`.`type` = 'public_ip'))))
   left join `resource_count` `ipcount` on(((`account`.`id` = `ipcount`.`account_id`) and (`ipcount`.`type` = 'public_ip'))))
   left join `resource_limit` `volumelimit` on(((`account`.`id` = `volumelimit`.`account_id`) and (`volumelimit`.`type` = 'volume'))))
   left join `resource_count` `volumecount` on(((`account`.`id` = `volumecount`.`account_id`) and (`volumecount`.`type` = 'volume'))))
   left join `resource_limit` `snapshotlimit` on(((`account`.`id` = `snapshotlimit`.`account_id`) and (`snapshotlimit`.`type` = 'snapshot'))))
   left join `resource_count` `snapshotcount` on(((`account`.`id` = `snapshotcount`.`account_id`) and (`snapshotcount`.`type` = 'snapshot'))))
   left join `resource_limit` `templatelimit` on(((`account`.`id` = `templatelimit`.`account_id`) and (`templatelimit`.`type` = 'template'))))
   left join `resource_count` `templatecount` on(((`account`.`id` = `templatecount`.`account_id`) and (`templatecount`.`type` = 'template'))))
   left join `resource_limit` `vpclimit` on(((`account`.`id` = `vpclimit`.`account_id`) and (`vpclimit`.`type` = 'vpc'))))
   left join `resource_count` `vpccount` on(((`account`.`id` = `vpccount`.`account_id`) and (`vpccount`.`type` = 'vpc'))))
   left join `resource_limit` `projectlimit` on(((`account`.`id` = `projectlimit`.`account_id`) and (`projectlimit`.`type` = 'project'))))
   left join `resource_count` `projectcount` on(((`account`.`id` = `projectcount`.`account_id`) and (`projectcount`.`type` = 'project'))))
   left join `resource_limit` `networklimit` on(((`account`.`id` = `networklimit`.`account_id`) and (`networklimit`.`type` = 'network'))))
   left join `resource_count` `networkcount` on(((`account`.`id` = `networkcount`.`account_id`) and (`networkcount`.`type` = 'network'))))
   left join `resource_limit` `cpulimit` on(((`account`.`id` = `cpulimit`.`account_id`) and (`cpulimit`.`type` = 'cpu'))))
   left join `resource_count` `cpucount` on(((`account`.`id` = `cpucount`.`account_id`) and (`cpucount`.`type` = 'cpu'))))
   left join `resource_limit` `memorylimit` on(((`account`.`id` = `memorylimit`.`account_id`) and (`memorylimit`.`type` = 'memory'))))
   left join `resource_count` `memorycount` on(((`account`.`id` = `memorycount`.`account_id`) and (`memorycount`.`type` = 'memory'))))
   left join `resource_limit` `primary_storage_limit` on(((`account`.`id` = `primary_storage_limit`.`account_id`) and (`primary_storage_limit`.`type` = 'primary_storage'))))
   left join `resource_count` `primary_storage_count` on(((`account`.`id` = `primary_storage_count`.`account_id`) and (`primary_storage_count`.`type` = 'primary_storage'))))
   left join `resource_limit` `secondary_storage_limit` on(((`account`.`id` = `secondary_storage_limit`.`account_id`) and (`secondary_storage_limit`.`type` = 'secondary_storage'))))
   left join `resource_count` `secondary_storage_count` on(((`account`.`id` = `secondary_storage_count`.`account_id`) and (`secondary_storage_count`.`type` = 'secondary_storage'))))
   left join `async_job` on(((`async_job`.`instance_id` = `account`.`id`) and (`async_job`.`instance_type` = 'Account') and (`async_job`.`job_status` = 0)))));


-- Extend the view to add the flag on secondary ip
CREATE OR REPLACE VIEW `user_vm_view`
AS SELECT
   `vm_instance`.`id` AS `id`,
   `vm_instance`.`name` AS `name`,
   `user_vm`.`display_name` AS `display_name`,
   `user_vm`.`user_data` AS `user_data`,
   `account`.`id` AS `account_id`,
   `account`.`uuid` AS `account_uuid`,
   `account`.`account_name` AS `account_name`,
   `account`.`type` AS `account_type`,
   `domain`.`id` AS `domain_id`,
   `domain`.`uuid` AS `domain_uuid`,
   `domain`.`name` AS `domain_name`,
   `domain`.`path` AS `domain_path`,
   `projects`.`id` AS `project_id`,
   `projects`.`uuid` AS `project_uuid`,
   `projects`.`name` AS `project_name`,
   `instance_group`.`id` AS `instance_group_id`,
   `instance_group`.`uuid` AS `instance_group_uuid`,
   `instance_group`.`name` AS `instance_group_name`,
   `vm_instance`.`uuid` AS `uuid`,
   `vm_instance`.`last_host_id` AS `last_host_id`,
   `vm_instance`.`vm_type` AS `type`,
   `vm_instance`.`limit_cpu_use` AS `limit_cpu_use`,
   `vm_instance`.`created` AS `created`,
   `vm_instance`.`state` AS `state`,
   `vm_instance`.`removed` AS `removed`,
   `vm_instance`.`ha_enabled` AS `ha_enabled`,
   `vm_instance`.`hypervisor_type` AS `hypervisor_type`,
   `vm_instance`.`instance_name` AS `instance_name`,
   `vm_instance`.`guest_os_id` AS `guest_os_id`,
   `vm_instance`.`display_vm` AS `display_vm`,
   `guest_os`.`uuid` AS `guest_os_uuid`,
   `vm_instance`.`pod_id` AS `pod_id`,
   `host_pod_ref`.`uuid` AS `pod_uuid`,
   `vm_instance`.`private_ip_address` AS `private_ip_address`,
   `vm_instance`.`private_mac_address` AS `private_mac_address`,
   `vm_instance`.`vm_type` AS `vm_type`,
   `data_center`.`id` AS `data_center_id`,
   `data_center`.`uuid` AS `data_center_uuid`,
   `data_center`.`name` AS `data_center_name`,
   `data_center`.`is_security_group_enabled` AS `security_group_enabled`,
   `data_center`.`networktype` AS `data_center_type`,
   `host`.`id` AS `host_id`,
   `host`.`uuid` AS `host_uuid`,
   `host`.`name` AS `host_name`,
   `vm_template`.`id` AS `template_id`,
   `vm_template`.`uuid` AS `template_uuid`,
   `vm_template`.`name` AS `template_name`,
   `vm_template`.`display_text` AS `template_display_text`,
   `vm_template`.`enable_password` AS `password_enabled`,
   `iso`.`id` AS `iso_id`,
   `iso`.`uuid` AS `iso_uuid`,
   `iso`.`name` AS `iso_name`,
   `iso`.`display_text` AS `iso_display_text`,
   `service_offering`.`id` AS `service_offering_id`,
   `svc_disk_offering`.`uuid` AS `service_offering_uuid`,
   `disk_offering`.`uuid` AS `disk_offering_uuid`,
   `disk_offering`.`id` AS `disk_offering_id`,(case when isnull(`service_offering`.`cpu`) then `custom_cpu`.`value` else `service_offering`.`cpu` end) AS `cpu`,(case when isnull(`service_offering`.`speed`) then `custom_speed`.`value` else `service_offering`.`speed` end) AS `speed`,(case when isnull(`service_offering`.`ram_size`) then `custom_ram_size`.`value` else `service_offering`.`ram_size` end) AS `ram_size`,
   `svc_disk_offering`.`name` AS `service_offering_name`,
   `disk_offering`.`name` AS `disk_offering_name`,
   `storage_pool`.`id` AS `pool_id`,
   `storage_pool`.`uuid` AS `pool_uuid`,
   `storage_pool`.`pool_type` AS `pool_type`,
   `volumes`.`id` AS `volume_id`,
   `volumes`.`uuid` AS `volume_uuid`,
   `volumes`.`device_id` AS `volume_device_id`,
   `volumes`.`volume_type` AS `volume_type`,
   `security_group`.`id` AS `security_group_id`,
   `security_group`.`uuid` AS `security_group_uuid`,
   `security_group`.`name` AS `security_group_name`,
   `security_group`.`description` AS `security_group_description`,
   `nics`.`id` AS `nic_id`,
   `nics`.`uuid` AS `nic_uuid`,
   `nics`.`network_id` AS `network_id`,
   `nics`.`ip4_address` AS `ip_address`,
   `nics`.`ip6_address` AS `ip6_address`,
   `nics`.`ip6_gateway` AS `ip6_gateway`,
   `nics`.`ip6_cidr` AS `ip6_cidr`,
   `nics`.`default_nic` AS `is_default_nic`,
   `nics`.`gateway` AS `gateway`,
   `nics`.`netmask` AS `netmask`,
   `nics`.`mac_address` AS `mac_address`,
   `nics`.`broadcast_uri` AS `broadcast_uri`,
   `nics`.`isolation_uri` AS `isolation_uri`,
   `nics`.`secondary_ip` AS `has_secondary_ip`,
   `vpc`.`id` AS `vpc_id`,
   `vpc`.`uuid` AS `vpc_uuid`,
   `networks`.`uuid` AS `network_uuid`,
   `networks`.`name` AS `network_name`,
   `networks`.`traffic_type` AS `traffic_type`,
   `networks`.`guest_type` AS `guest_type`,
   `user_ip_address`.`id` AS `public_ip_id`,
   `user_ip_address`.`uuid` AS `public_ip_uuid`,
   `user_ip_address`.`public_ip_address` AS `public_ip_address`,
   `ssh_keypairs`.`keypair_name` AS `keypair_name`,
   `resource_tags`.`id` AS `tag_id`,
   `resource_tags`.`uuid` AS `tag_uuid`,
   `resource_tags`.`key` AS `tag_key`,
   `resource_tags`.`value` AS `tag_value`,
   `resource_tags`.`domain_id` AS `tag_domain_id`,
   `resource_tags`.`account_id` AS `tag_account_id`,
   `resource_tags`.`resource_id` AS `tag_resource_id`,
   `resource_tags`.`resource_uuid` AS `tag_resource_uuid`,
   `resource_tags`.`resource_type` AS `tag_resource_type`,
   `resource_tags`.`customer` AS `tag_customer`,
   `async_job`.`id` AS `job_id`,
   `async_job`.`uuid` AS `job_uuid`,
   `async_job`.`job_status` AS `job_status`,
   `async_job`.`account_id` AS `job_account_id`,
   `affinity_group`.`id` AS `affinity_group_id`,
   `affinity_group`.`uuid` AS `affinity_group_uuid`,
   `affinity_group`.`name` AS `affinity_group_name`,
   `affinity_group`.`description` AS `affinity_group_description`,
   `vm_instance`.`dynamically_scalable` AS `dynamically_scalable`,
   `all_details`.`name` AS `detail_name`,
   `all_details`.`value` AS `detail_value`
FROM ((((((((((((((((((((((((((((((((
   (`user_vm` join `vm_instance` on(((`vm_instance`.`id` = `user_vm`.`id`) and isnull(`vm_instance`.`removed`)))) join `account` on((`vm_instance`.`account_id` = `account`.`id`))) join `domain` on((`vm_instance`.`domain_id` = `domain`.`id`)))
   left join `guest_os` on((`vm_instance`.`guest_os_id` = `guest_os`.`id`)))
   left join `host_pod_ref` on((`vm_instance`.`pod_id` = `host_pod_ref`.`id`)))
   left join `projects` on((`projects`.`project_account_id` = `account`.`id`)))
   left join `instance_group_vm_map` on((`vm_instance`.`id` = `instance_group_vm_map`.`instance_id`)))
   left join `instance_group` on((`instance_group_vm_map`.`group_id` = `instance_group`.`id`)))
   left join `data_center` on((`vm_instance`.`data_center_id` = `data_center`.`id`)))
   left join `host` on((`vm_instance`.`host_id` = `host`.`id`)))
   left join `vm_template` on((`vm_instance`.`vm_template_id` = `vm_template`.`id`)))
   left join `vm_template` `iso` on((`iso`.`id` = `user_vm`.`iso_id`)))
   left join `service_offering` on((`vm_instance`.`service_offering_id` = `service_offering`.`id`)))
   left join `disk_offering` `svc_disk_offering` on((`vm_instance`.`service_offering_id` = `svc_disk_offering`.`id`)))
   left join `disk_offering` on((`vm_instance`.`disk_offering_id` = `disk_offering`.`id`)))
   left join `volumes` on((`vm_instance`.`id` = `volumes`.`instance_id`)))
   left join `storage_pool` on((`volumes`.`pool_id` = `storage_pool`.`id`)))
   left join `security_group_vm_map` on((`vm_instance`.`id` = `security_group_vm_map`.`instance_id`)))
   left join `security_group` on((`security_group_vm_map`.`security_group_id` = `security_group`.`id`)))
   left join `nics` on(((`vm_instance`.`id` = `nics`.`instance_id`) and isnull(`nics`.`removed`))))
   left join `networks` on((`nics`.`network_id` = `networks`.`id`)))
   left join `vpc` on(((`networks`.`vpc_id` = `vpc`.`id`) and isnull(`vpc`.`removed`))))
   left join `user_ip_address` on((`user_ip_address`.`vm_id` = `vm_instance`.`id`)))
   left join `user_vm_details` `ssh_details` on(((`ssh_details`.`vm_id` = `vm_instance`.`id`) and (`ssh_details`.`name` = 'SSH.PublicKey'))))
   left join `ssh_keypairs` on(((`ssh_keypairs`.`public_key` = `ssh_details`.`value`) and (`ssh_keypairs`.`account_id` = `account`.`id`))))
   left join `resource_tags` on(((`resource_tags`.`resource_id` = `vm_instance`.`id`) and (`resource_tags`.`resource_type` = 'UserVm'))))
   left join `async_job` on(((`async_job`.`instance_id` = `vm_instance`.`id`) and (`async_job`.`instance_type` = 'VirtualMachine') and (`async_job`.`job_status` = 0))))
   left join `affinity_group_vm_map` on((`vm_instance`.`id` = `affinity_group_vm_map`.`instance_id`)))
   left join `affinity_group` on((`affinity_group_vm_map`.`affinity_group_id` = `affinity_group`.`id`)))
   left join `user_vm_details` `all_details` on((`all_details`.`vm_id` = `vm_instance`.`id`)))
   left join `user_vm_details` `custom_cpu` on(((`custom_cpu`.`vm_id` = `vm_instance`.`id`) and (`custom_cpu`.`name` = 'CpuNumber'))))
   left join `user_vm_details` `custom_speed` on(((`custom_speed`.`vm_id` = `vm_instance`.`id`) and (`custom_speed`.`name` = 'CpuSpeed'))))
   left join `user_vm_details` `custom_ram_size` on(((`custom_ram_size`.`vm_id` = `vm_instance`.`id`) and (`custom_ram_size`.`name` = 'memory'))));