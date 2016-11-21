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
-- Schema upgrade from 4.4.2 to 4.4.2-exo-1;
--;

-- DB cleanup
-- https://github.com/apache/cloudstack/pull/1466

-- --- Duplicate PRIMARY KEY
ALTER TABLE `cloud`.`user_vm` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`domain_router` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vm_instance` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`account_vlan_map` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`account_vnet_map` DROP INDEX IF EXISTS `id` ;

ALTER TABLE `cloud`.`cluster` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`conditions` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`counter` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`data_center` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`dc_storage_network_ip_range` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`dedicated_resources` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`host_pod_ref` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`iam_group` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`iam_policy` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`iam_policy_permission` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`image_store_details` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`instance_group` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`netapp_lun` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`netapp_pool` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`netapp_volume` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`network_acl_item_cidrs` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`network_offerings` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`nic_secondary_ips` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`nics` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_ha_work` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_host` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_host_transfer` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_networks` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_nwgrp_work` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_vm_ruleset_log` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`op_vpc_distributed_router_sequence_no` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`pod_vlan_map` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`portable_ip_address` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`portable_ip_range` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`region` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`remote_access_vpn` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`sequence` DROP INDEX IF EXISTS `name` ;
ALTER TABLE `cloud`.`snapshot_details` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`snapshots` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`storage_pool` DROP INDEX IF EXISTS `id`, DROP INDEX IF EXISTS `id_2` ;
ALTER TABLE `cloud`.`storage_pool_details` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`storage_pool_work` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`user_ip_address` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`user_ipv6_address` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`user_statistics` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`version` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vlan` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vm_disk_statistics` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vm_snapshot_details` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vm_work_job` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vpc_gateways` DROP INDEX IF EXISTS `id` ;
ALTER TABLE `cloud`.`vpn_users` DROP INDEX IF EXISTS `id` ;


-- --- Missing indexes (Add indexes to avoid full table scans)
ALTER TABLE `cloud`.`vm_network_map` ADD INDEX IF NOT EXISTS `i_vm_id` (`vm_id` ASC);
ALTER TABLE `cloud`.`user_vm_details` ADD INDEX IF NOT EXISTS `i_name_vm_id` (`vm_id` ASC, `name` ASC);


-- Fix Snapshots size column
UPDATE `cloud`.`snapshot_store_ref` SET `physical_size` = `size`
WHERE `physical_size` = 0 AND `store_role` = 'Image' AND `size` > 0;



-- 2016.05.30 Update URL fields to 2048 bits
ALTER TABLE `cloud`.`volume_host_ref` MODIFY COLUMN IF EXISTS `url` varchar(2048);
ALTER TABLE `cloud`.`object_datastore_ref` MODIFY COLUMN IF EXISTS `url` varchar(2048);
ALTER TABLE `cloud`.`image_store` MODIFY COLUMN IF EXISTS `url` varchar(2048);
ALTER TABLE `cloud`.`template_store_ref` MODIFY COLUMN IF EXISTS `url` varchar(2048);
ALTER TABLE `cloud`.`volume_store_ref` MODIFY COLUMN IF EXISTS `url` varchar(2048);
ALTER TABLE `cloud`.`volume_store_ref` MODIFY COLUMN IF EXISTS `download_url` varchar(2048);
ALTER TABLE `cloud`.`upload` MODIFY COLUMN IF EXISTS `url` varchar(2048);


-- 2016.09.29: Fix for user_view
CREATE OR REPLACE VIEW `cloud`.`user_vm_view` AS
SELECT `vm_instance`.`id` AS `id`,
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
       `disk_offering`.`id` AS `disk_offering_id`,
       (CASE
            WHEN isnull(`service_offering`.`cpu`) THEN `custom_cpu`.`value`
            ELSE `service_offering`.`cpu`
        END) AS `cpu`,
       (CASE
            WHEN isnull(`service_offering`.`speed`) THEN `custom_speed`.`value`
            ELSE `service_offering`.`speed`
        END) AS `speed`,
       (CASE
            WHEN isnull(`service_offering`.`ram_size`) THEN `custom_ram_size`.`value`
            ELSE `service_offering`.`ram_size`
        END) AS `ram_size`,
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
    FROM
        (((((((((((((((((((((((((((((((((`user_vm`
        JOIN `vm_instance` on(((`vm_instance`.`id` = `user_vm`.`id`)
            AND isnull(`vm_instance`.`removed`))))
        JOIN `account` on((`vm_instance`.`account_id` = `account`.`id`)))
        JOIN `domain` on((`vm_instance`.`domain_id` = `domain`.`id`)))
        LEFT JOIN `guest_os` on((`vm_instance`.`guest_os_id` = `guest_os`.`id`)))
        LEFT JOIN `host_pod_ref` on((`vm_instance`.`pod_id` = `host_pod_ref`.`id`)))
        LEFT JOIN `projects` on((`projects`.`project_account_id` = `account`.`id`)))
        LEFT JOIN `instance_group_vm_map` on((`vm_instance`.`id` = `instance_group_vm_map`.`instance_id`)))
        LEFT JOIN `instance_group` on((`instance_group_vm_map`.`group_id` = `instance_group`.`id`)))
        LEFT JOIN `data_center` on((`vm_instance`.`data_center_id` = `data_center`.`id`)))
        LEFT JOIN `host` on((`vm_instance`.`host_id` = `host`.`id`)))
        LEFT JOIN `vm_template` on((`vm_instance`.`vm_template_id` = `vm_template`.`id`)))
        LEFT JOIN `vm_template` `iso` on((`iso`.`id` = `user_vm`.`iso_id`)))
        LEFT JOIN `service_offering` on((`vm_instance`.`service_offering_id` = `service_offering`.`id`)))
        LEFT JOIN `disk_offering` `svc_disk_offering` on((`vm_instance`.`service_offering_id` = `svc_disk_offering`.`id`)))
        LEFT JOIN `disk_offering` on((`vm_instance`.`disk_offering_id` = `disk_offering`.`id`)))
        LEFT JOIN `volumes` on((`vm_instance`.`id` = `volumes`.`instance_id`)))
        LEFT JOIN `storage_pool` on((`volumes`.`pool_id` = `storage_pool`.`id`)))
        LEFT JOIN `security_group_vm_map` on((`vm_instance`.`id` = `security_group_vm_map`.`instance_id`)))
        LEFT JOIN `security_group` on((`security_group_vm_map`.`security_group_id` = `security_group`.`id`)))
        LEFT JOIN `nics` on(((`vm_instance`.`id` = `nics`.`instance_id`)
            AND isnull(`nics`.`removed`))))
        LEFT JOIN `networks` on((`nics`.`network_id` = `networks`.`id`)))
        LEFT JOIN `vpc` on(((`networks`.`vpc_id` = `vpc`.`id`)
            AND isnull(`vpc`.`removed`))))
        LEFT JOIN `user_ip_address` on((`user_ip_address`.`vm_id` = `vm_instance`.`id`)))
        LEFT JOIN `user_vm_details` `ssh_details` on(((`ssh_details`.`vm_id` = `vm_instance`.`id`)
            AND (`ssh_details`.`name` = 'SSH.PublicKey'))))
        LEFT JOIN `ssh_keypairs` on((`ssh_keypairs`.`public_key` = `ssh_details`.`value`)
            AND (`ssh_keypairs`.`account_id` = `account`.`id`)))
        LEFT JOIN `resource_tags` on(((`resource_tags`.`resource_id` = `vm_instance`.`id`)
            AND (`resource_tags`.`resource_type` = 'UserVm'))))
        LEFT JOIN `async_job` on(((`async_job`.`instance_id` = `vm_instance`.`id`)
            AND (`async_job`.`instance_type` = 'VirtualMachine')
            AND (`async_job`.`job_status` = 0))))
        LEFT JOIN `affinity_group_vm_map` on((`vm_instance`.`id` = `affinity_group_vm_map`.`instance_id`)))
        LEFT JOIN `affinity_group` on((`affinity_group_vm_map`.`affinity_group_id` = `affinity_group`.`id`)))
        LEFT JOIN `user_vm_details` `all_details` on((`all_details`.`vm_id` = `vm_instance`.`id`)))
        LEFT JOIN `user_vm_details` `custom_cpu` on(((`custom_cpu`.`vm_id` = `vm_instance`.`id`)
            AND (`custom_cpu`.`name` = 'CpuNumber'))))
        LEFT JOIN `user_vm_details` `custom_speed` on(((`custom_speed`.`vm_id` = `vm_instance`.`id`)
            AND (`custom_speed`.`name` = 'CpuSpeed'))))
        LEFT JOIN `user_vm_details` `custom_ram_size` on(((`custom_ram_size`.`vm_id` = `vm_instance`.`id`)
            AND (`custom_ram_size`.`name` = 'memory'))));


-- 2016.05.20 - Restrictions for service offering
-- Add restricted column for disk offering
ALTER TABLE `cloud`.`disk_offering` ADD COLUMN IF NOT EXISTS `restricted` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT 'Indicates whether the offering is restricted to a list of domains';

-- Change views
CREATE OR REPLACE VIEW `cloud`.`disk_offering_view` AS
SELECT
   `disk_offering`.`id`,
   `disk_offering`.`uuid`,
   `disk_offering`.`name`,
   `disk_offering`.`display_text`,
   `disk_offering`.`disk_size`,
   `disk_offering`.`min_iops`,
   `disk_offering`.`max_iops`,
   `disk_offering`.`created`,
   `disk_offering`.`tags`,
   `disk_offering`.`customized`,
   `disk_offering`.`customized_iops`,
   `disk_offering`.`removed`,
   `disk_offering`.`use_local_storage`,
   `disk_offering`.`system_use`,
   `disk_offering`.`hv_ss_reserve`,
   `disk_offering`.`bytes_read_rate`,
   `disk_offering`.`bytes_write_rate`,
   `disk_offering`.`iops_read_rate`,
   `disk_offering`.`iops_write_rate`,
   `disk_offering`.`cache_mode`,
   `disk_offering`.`sort_key`,
   `disk_offering`.`type`,
   `disk_offering`.`display_offering`,
   `disk_offering`.`restricted`,
   `domain`.`id` AS `domain_id`,
   `domain`.`uuid` AS `domain_uuid`,
   `domain`.`name` AS `domain_name`,
   `domain`.`path` AS `domain_path`
FROM `disk_offering` LEFT JOIN `domain` ON `disk_offering`.`domain_id` = `domain`.`id`
WHERE `disk_offering`.`state` = 'ACTIVE';

CREATE OR REPLACE VIEW `cloud`.`service_offering_view`
AS SELECT
   `service_offering`.`id`,
   `disk_offering`.`uuid`,
   `disk_offering`.`name`,
   `disk_offering`.`display_text`,
   `disk_offering`.`created`,
   `disk_offering`.`tags`,
   `disk_offering`.`removed`,
   `disk_offering`.`use_local_storage`,
   `disk_offering`.`system_use`,
   `disk_offering`.`customized_iops`,
   `disk_offering`.`min_iops`,
   `disk_offering`.`max_iops`,
   `disk_offering`.`hv_ss_reserve`,
   `disk_offering`.`bytes_read_rate`,
   `disk_offering`.`bytes_write_rate`,
   `disk_offering`.`iops_read_rate`,
   `disk_offering`.`iops_write_rate`,
   `disk_offering`.`cache_mode`,
   `disk_offering`.`restricted`,
   `service_offering`.`cpu`,
   `service_offering`.`speed`,
   `service_offering`.`ram_size`,
   `service_offering`.`nw_rate`,
   `service_offering`.`mc_rate`,
   `service_offering`.`ha_enabled`,
   `service_offering`.`limit_cpu_use`,
   `service_offering`.`host_tag`,
   `service_offering`.`default_use`,
   `service_offering`.`vm_type`,
   `service_offering`.`sort_key`,
   `service_offering`.`is_volatile`,
   `service_offering`.`deployment_planner`,
   `domain`.`id` AS `domain_id`,
   `domain`.`uuid` AS `domain_uuid`,
   `domain`.`name` AS `domain_name`,
   `domain`.`path` AS `domain_path`
FROM
    `service_offering` INNER JOIN `disk_offering` ON `service_offering`.`id` = `disk_offering`.`id`
    LEFT JOIN `domain` ON `disk_offering`.`domain_id` = `domain`.`id`
WHERE (`disk_offering`.`state` = 'Active');

CREATE TABLE IF NOT EXISTS `cloud`.`service_offering_authorizations` (
  `id` bigint unsigned UNIQUE NOT NULL AUTO_INCREMENT,
  `uuid` varchar(40) DEFAULT NULL,
  `service_offering_id` bigint unsigned NOT NULL,
  `account_id` bigint unsigned NULL,
  `domain_id` bigint unsigned NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uc_service_offering_authorizations__uuid` (`uuid`),
  UNIQUE KEY `uc_service_offering_authorizations__domain` (`service_offering_id`, `domain_id`),
  UNIQUE KEY `uc_service_offering_authorizations__account` (`service_offering_id`, `account_id`),
  KEY `i_service_offering_authorizations__service_offering_id` (`service_offering_id`),
  CONSTRAINT `fk_service_offering_authorizations_join_map__service_offering_id` FOREIGN KEY (`service_offering_id`) REFERENCES `service_offering` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_service_offering_authorizations_join_map__account_id` FOREIGN KEY (`account_id`) REFERENCES `account` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_service_offering_authorizations_join_map__domain_id` FOREIGN KEY (`domain_id`) REFERENCES `domain` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

