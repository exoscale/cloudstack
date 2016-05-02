-- DB cleanup
-- https://github.com/apache/cloudstack/pull/1466

----- Duplicate PRIMARY KEY
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


----- Missing indexes (Add indexes to avoid full table scans)
ALTER TABLE `cloud`.`vm_network_map` ADD INDEX `i_vm_id` (`vm_id` ASC);
ALTER TABLE `cloud`.`user_vm_details` ADD INDEX `i_name_vm_id` (`vm_id` ASC, `name` ASC);
