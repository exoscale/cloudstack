// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.cloud.upgrade.dao;

import com.cloud.utils.exception.CloudRuntimeException;
import com.cloud.utils.script.Script;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Upgrade442exo1to442exo2 implements DbUpgrade {
    final static Logger s_logger = Logger.getLogger(Upgrade442exo1to442exo2.class);

    final static String DB_SCRIPT = "db/schema-442exo1to442exo2.sql";
    final static String DB_CLEANUP_SCRIPT = null; //"db/schema-442exo1to442exo2-cleanup.sql";

    @Override
    public String[] getUpgradableVersionRange() {
        return new String[] {"4.4.2.1", "4.4.2.2"};
    }

    @Override
    public String getUpgradedVersion() {
        return "4.4.2.2";
    }

    @Override
    public boolean supportsRollingUpgrade() {
        return false;
    }

    @Override
    public File[] getPrepareScripts() {
        String script = Script.findScript("", DB_SCRIPT);
        if (script == null) {
            throw new CloudRuntimeException("Unable to find " + DB_SCRIPT);
        }

        return new File[] {new File(script)};
    }

    @Override
    public void performDataMigration(Connection conn) {
        validateUserDataInBase64(conn);
    }

    private void validateUserDataInBase64(Connection conn) {
        try (final PreparedStatement selectStatement = conn.prepareStatement("SELECT `id`, `user_data` FROM `cloud`.`user_vm` WHERE `user_data` IS NOT NULL;");
             final ResultSet selectResultSet = selectStatement.executeQuery()) {
            while (selectResultSet.next()) {
                final Long userVmId = selectResultSet.getLong(1);
                final String userData = selectResultSet.getString(2);
                if (Base64.isBase64(userData)) {
                    final String newUserData = Base64.encodeBase64String(Base64.decodeBase64(userData.getBytes()));
                    if (!userData.equals(newUserData)) {
                        try (final PreparedStatement updateStatement = conn.prepareStatement("UPDATE `cloud`.`user_vm` SET `user_data` = ? WHERE `id` = ? ;")) {
                            updateStatement.setString(1, newUserData);
                            updateStatement.setLong(2, userVmId);
                            updateStatement.executeUpdate();
                        } catch (SQLException e) {
                            s_logger.error("Failed to update cloud.user_vm user_data for id:" + userVmId + " with exception: " + e.getMessage());
                            throw new CloudRuntimeException("Exception while updating cloud.user_vm for id " + userVmId, e);
                        }
                    }
                } else {
                    // Update to NULL since it's invalid
                    s_logger.warn("Removing user_data for vm id " + userVmId + " because it's invalid");
                    s_logger.warn("Removed data was: " + userData);
                    try (final PreparedStatement updateStatement = conn.prepareStatement("UPDATE `cloud`.`user_vm` SET `user_data` = NULL WHERE `id` = ? ;")) {
                        updateStatement.setLong(1, userVmId);
                        updateStatement.executeUpdate();
                    } catch (SQLException e) {
                        s_logger.error("Failed to update cloud.user_vm user_data for id:" + userVmId + " to NULL with exception: " + e.getMessage());
                        throw new CloudRuntimeException("Exception while updating cloud.user_vm for id " + userVmId + " to NULL", e);
                    }
                }
            }
        } catch (SQLException e) {
            throw new CloudRuntimeException("Exception while validating existing user_vm table's user_data column to be base64 valid with padding", e);
        }
        s_logger.debug("Done validating base64 content of user data");
    }

    @Override
    public File[] getCleanupScripts() {
        if (DB_CLEANUP_SCRIPT != null) {
            String script = Script.findScript("", DB_CLEANUP_SCRIPT);
            if (script == null) {
                throw new CloudRuntimeException("Unable to find " + DB_CLEANUP_SCRIPT);
            }

            return new File[]{new File(script)};
        } else {
            return null;
        }
    }

}
