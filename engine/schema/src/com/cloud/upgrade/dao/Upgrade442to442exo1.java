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
import org.apache.log4j.Logger;

import java.io.File;
import java.sql.Connection;

public class Upgrade442to442exo1 implements DbUpgrade {
    final static Logger s_logger = Logger.getLogger(Upgrade442to442exo1.class);

    final static String DB_SCRIPT = "db/schema-442to442exo1.sql";
    final static String DB_CLEANUP_SCRIPT = "db/schema-442to442exo1-cleanup.sql";

    @Override
    public String[] getUpgradableVersionRange() {
        return new String[] {"4.4.2", "4.4.2.1"};
    }

    @Override
    public String getUpgradedVersion() {
        return "4.4.2.1";
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