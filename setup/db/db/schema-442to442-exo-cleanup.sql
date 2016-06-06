-- DB cleanup
-- https://github.com/apache/cloudstack/pull/1466

-- --- Duplicate PRIMARY KEY
ALTER TABLE `cloud`.`user_vm` DROP INDEX `id` ;
ALTER TABLE `cloud`.`domain_router` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vm_instance` DROP INDEX `id` ;
ALTER TABLE `cloud`.`account_vlan_map` DROP INDEX `id` ;
ALTER TABLE `cloud`.`account_vnet_map` DROP INDEX `id` ;

ALTER TABLE `cloud`.`cluster` DROP INDEX `id` ;
ALTER TABLE `cloud`.`conditions` DROP INDEX `id` ;
ALTER TABLE `cloud`.`counter` DROP INDEX `id` ;
ALTER TABLE `cloud`.`data_center` DROP INDEX `id` ;
ALTER TABLE `cloud`.`dc_storage_network_ip_range` DROP INDEX `id` ;
ALTER TABLE `cloud`.`dedicated_resources` DROP INDEX `id` ;
ALTER TABLE `cloud`.`host_pod_ref` DROP INDEX `id` ;
ALTER TABLE `cloud`.`iam_group` DROP INDEX `id` ;
ALTER TABLE `cloud`.`iam_policy` DROP INDEX `id` ;
ALTER TABLE `cloud`.`iam_policy_permission` DROP INDEX `id` ;
ALTER TABLE `cloud`.`image_store_details` DROP INDEX `id` ;
ALTER TABLE `cloud`.`instance_group` DROP INDEX `id` ;
ALTER TABLE `cloud`.`netapp_lun` DROP INDEX `id` ;
ALTER TABLE `cloud`.`netapp_pool` DROP INDEX `id` ;
ALTER TABLE `cloud`.`netapp_volume` DROP INDEX `id` ;
ALTER TABLE `cloud`.`network_acl_item_cidrs` DROP INDEX `id` ;
ALTER TABLE `cloud`.`network_offerings` DROP INDEX `id` ;
ALTER TABLE `cloud`.`nic_secondary_ips` DROP INDEX `id` ;
ALTER TABLE `cloud`.`nics` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_ha_work` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_host` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_host_transfer` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_networks` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_nwgrp_work` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_vm_ruleset_log` DROP INDEX `id` ;
ALTER TABLE `cloud`.`op_vpc_distributed_router_sequence_no` DROP INDEX `id` ;
ALTER TABLE `cloud`.`pod_vlan_map` DROP INDEX `id` ;
ALTER TABLE `cloud`.`portable_ip_address` DROP INDEX `id` ;
ALTER TABLE `cloud`.`portable_ip_range` DROP INDEX `id` ;
ALTER TABLE `cloud`.`region` DROP INDEX `id` ;
ALTER TABLE `cloud`.`remote_access_vpn` DROP INDEX `id` ;
ALTER TABLE `cloud`.`sequence` DROP INDEX `name` ;
ALTER TABLE `cloud`.`snapshot_details` DROP INDEX `id` ;
ALTER TABLE `cloud`.`snapshots` DROP INDEX `id` ;
ALTER TABLE `cloud`.`storage_pool` DROP INDEX `id`, DROP INDEX `id_2` ;
ALTER TABLE `cloud`.`storage_pool_details` DROP INDEX `id` ;
ALTER TABLE `cloud`.`storage_pool_work` DROP INDEX `id` ;
ALTER TABLE `cloud`.`user_ip_address` DROP INDEX `id` ;
ALTER TABLE `cloud`.`user_ipv6_address` DROP INDEX `id` ;
ALTER TABLE `cloud`.`user_statistics` DROP INDEX `id` ;
ALTER TABLE `cloud`.`version` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vlan` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vm_disk_statistics` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vm_snapshot_details` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vm_work_job` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vpc_gateways` DROP INDEX `id` ;
ALTER TABLE `cloud`.`vpn_users` DROP INDEX `id` ;


-- --- Missing indexes (Add indexes to avoid full table scans)
ALTER TABLE `cloud`.`vm_network_map` ADD INDEX `i_vm_id` (`vm_id` ASC);
ALTER TABLE `cloud`.`user_vm_details` ADD INDEX `i_name_vm_id` (`vm_id` ASC, `name` ASC);


-- Fix Snapshots size column
UPDATE `cloud`.`snapshot_store_ref` SET `physical_size` = `size`
WHERE `physical_size` = 0 AND `store_role` = 'Image' AND `size` > 0;


-- 2016.05.20 - Restrictions for service offering
-- Add restricted column for disk offering
ALTER TABLE `cloud`.`disk_offering` ADD COLUMN `restricted` tinyint(1) unsigned NOT NULL DEFAULT '0' COMMENT 'Indicates whether the offering is restricted to a list of domains';
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

DROP TABLE IF EXISTS `cloud`.`service_offering_authorizations`;
CREATE TABLE `cloud`.`service_offering_authorizations` (
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


-- 2016.05.30 Update URL fields to 2048 bits
ALTER TABLE `cloud`.`volume_host_ref` MODIFY COLUMN `url` varchar(2048);
ALTER TABLE `cloud`.`object_datastore_ref` MODIFY COLUMN `url` varchar(2048);
ALTER TABLE `cloud`.`image_store` MODIFY COLUMN `url` varchar(2048);
ALTER TABLE `cloud`.`template_store_ref` MODIFY COLUMN `url` varchar(2048);
ALTER TABLE `cloud`.`volume_store_ref` MODIFY COLUMN `url` varchar(2048);
ALTER TABLE `cloud`.`volume_store_ref` MODIFY COLUMN `download_url` varchar(2048);
ALTER TABLE `cloud`.`upload` MODIFY COLUMN `url` varchar(2048);
