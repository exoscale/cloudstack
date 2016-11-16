//
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
//

package com.cloud.upgrade;

import com.cloud.upgrade.dao.DbUpgrade;
import com.cloud.upgrade.dao.Upgrade410to420;
import com.cloud.upgrade.dao.Upgrade420to421;
import com.cloud.upgrade.dao.Upgrade421to430;
import com.cloud.upgrade.dao.Upgrade430to440;
import com.cloud.upgrade.dao.Upgrade440to441;
import com.cloud.upgrade.dao.Upgrade441to442;
import com.cloud.utils.CloudStackVersion;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DatabaseUpgradeCheckerTest {

    @Test
    public void testCalculateUpgradePath440to441() {

        final CloudStackVersion dbVersion = CloudStackVersion.parse("4.4.0");
        assertNotNull(dbVersion);

        final CloudStackVersion currentVersion = CloudStackVersion.parse("4.4.1");
        assertNotNull(currentVersion);

        final DatabaseUpgradeChecker checker = new DatabaseUpgradeChecker();
        final DbUpgrade[] upgrades = checker.calculateUpgradePath(dbVersion, currentVersion);

        assertNotNull(upgrades);
        assertEquals(5, upgrades.length);
        assertTrue(upgrades[0] instanceof Upgrade440to441);
        assertTrue(upgrades[1] instanceof Upgrade441to442);
    }

    @Test
    public void testCalculateUpgradePath440to4420() {

        final CloudStackVersion dbVersion = CloudStackVersion.parse("4.4.0");
        assertNotNull(dbVersion);

        final CloudStackVersion currentVersion = CloudStackVersion.parse("4.4.2");
        assertNotNull(currentVersion);

        final DatabaseUpgradeChecker checker = new DatabaseUpgradeChecker();
        final DbUpgrade[] upgrades = checker.calculateUpgradePath(dbVersion, currentVersion);

        assertNotNull(upgrades);
        assertEquals(5, upgrades.length);

        assertTrue(upgrades[0] instanceof Upgrade440to441);

        assertTrue(Arrays.equals(new String[] { "4.4.1", currentVersion.toString()}, upgrades[1].getUpgradableVersionRange()));
        assertEquals(currentVersion.toString(), upgrades[1].getUpgradedVersion());

    }

    @Test
    public void testCalculateUpgradePath441to4420() {

        final CloudStackVersion dbVersion = CloudStackVersion.parse("4.4.1");
        assertNotNull(dbVersion);

        final CloudStackVersion currentVersion = CloudStackVersion.parse("4.4.2");
        assertNotNull(currentVersion);

        final DatabaseUpgradeChecker checker = new DatabaseUpgradeChecker();
        final DbUpgrade[] upgrades = checker.calculateUpgradePath(dbVersion, currentVersion);

        assertNotNull(upgrades);
        assertEquals(4, upgrades.length);

        assertTrue(Arrays.equals(new String[] { "4.4.1", currentVersion.toString()}, upgrades[0].getUpgradableVersionRange()));
        assertEquals(currentVersion.toString(), upgrades[0].getUpgradedVersion());

    }

    @Test
    public void testFindUpgradePath430to441() {

        final CloudStackVersion dbVersion = CloudStackVersion.parse("4.3.0");
        assertNotNull(dbVersion);

        final CloudStackVersion currentVersion = CloudStackVersion.parse("4.4.1");
        assertNotNull(currentVersion);

        final DatabaseUpgradeChecker checker = new DatabaseUpgradeChecker();
        final DbUpgrade[] upgrades = checker.calculateUpgradePath(dbVersion, currentVersion);

        assertNotNull(upgrades);
        assertEquals(6, upgrades.length);

        assertTrue(upgrades[0] instanceof Upgrade430to440);
        assertTrue(upgrades[1] instanceof Upgrade440to441);
        assertTrue(upgrades[2] instanceof Upgrade441to442);

    }

    @Test
    public void testFindUpgradePath410to4420() {

        final CloudStackVersion dbVersion = CloudStackVersion.parse("4.1.0");
        assertNotNull(dbVersion);

        final CloudStackVersion currentVersion = CloudStackVersion.parse("4.4.2");
        assertNotNull(currentVersion);

        final DatabaseUpgradeChecker checker = new DatabaseUpgradeChecker();
        final DbUpgrade[] upgrades = checker.calculateUpgradePath(dbVersion, currentVersion);

        assertNotNull(upgrades);
        assertEquals(9, upgrades.length);

        assertTrue(upgrades[0] instanceof Upgrade410to420);
        assertTrue(upgrades[1] instanceof Upgrade420to421);
        assertTrue(upgrades[2] instanceof Upgrade421to430);
        assertTrue(upgrades[3] instanceof Upgrade430to440);
        assertTrue(upgrades[4] instanceof Upgrade440to441);
        assertTrue(upgrades[5] instanceof Upgrade441to442);

        assertTrue(Arrays.equals(new String[] { "4.4.1", currentVersion.toString()}, upgrades[5].getUpgradableVersionRange()));
        assertEquals(currentVersion.toString(), upgrades[5].getUpgradedVersion());

    }
}
