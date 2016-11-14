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

