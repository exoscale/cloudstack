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
package org.apache.cloudstack.storage.snapshot;

import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreManager;
import org.apache.cloudstack.engine.subsystem.api.storage.SnapshotDataFactory;
import org.apache.cloudstack.engine.subsystem.api.storage.SnapshotInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.StrategyPriority;
import org.apache.cloudstack.engine.subsystem.api.storage.VolumeInfo;
import org.apache.cloudstack.engine.subsystem.api.storage.VolumeService;
import org.apache.cloudstack.engine.subsystem.api.storage.ObjectInDataStoreStateMachine.Event;
import org.apache.cloudstack.storage.datastore.db.PrimaryDataStoreDao;
import org.apache.cloudstack.storage.datastore.db.SnapshotDataStoreDao;
import org.apache.cloudstack.storage.datastore.db.SnapshotDataStoreVO;


import com.cloud.agent.AgentManager;
import com.cloud.host.dao.HostDao;
import com.cloud.server.ManagementService;
import com.cloud.storage.DataStoreRole;
import com.cloud.storage.Snapshot;
import com.cloud.storage.SnapshotVO;
import com.cloud.storage.Storage.ImageFormat;
import com.cloud.storage.StoragePool;
import com.cloud.storage.StoragePoolStatus;
import com.cloud.storage.Volume;
import com.cloud.storage.VolumeVO;
import com.cloud.storage.dao.SnapshotDao;
import com.cloud.storage.dao.SnapshotDetailsDao;
import com.cloud.storage.dao.VolumeDao;
import com.cloud.utils.db.DB;
import com.cloud.utils.exception.CloudRuntimeException;
import com.cloud.vm.dao.VMInstanceDao;

@Component
public class StorageSystemSnapshotStrategy extends SnapshotStrategyBase {
    private static final Logger s_logger = Logger.getLogger(StorageSystemSnapshotStrategy.class);

    @Inject private AgentManager _agentMgr;
    @Inject private DataStoreManager _dataStoreMgr;
    @Inject private HostDao _hostDao;
    @Inject private ManagementService _mgr;
    @Inject private PrimaryDataStoreDao _storagePoolDao;
    @Inject private SnapshotDao _snapshotDao;
    @Inject private SnapshotDataFactory _snapshotDataFactory;
    @Inject private SnapshotDataStoreDao _snapshotStoreDao;
    @Inject private SnapshotDetailsDao _snapshotDetailsDao;
    @Inject private VMInstanceDao _vmInstanceDao;
    @Inject private VolumeDao _volumeDao;
    @Inject private VolumeService _volService;

    @Override
    public SnapshotInfo backupSnapshot(SnapshotInfo snapshotInfo) {
        return snapshotInfo;
    }

    @Override
    public boolean deleteSnapshot(Long snapshotId) {
        return false;
    }

    @Override
    @DB
    public SnapshotInfo takeSnapshot(SnapshotInfo snapshotInfo) {
        return null;
    }

    @Override
    public boolean revertSnapshot(SnapshotInfo snapshot) {
        if (canHandle(snapshot,SnapshotOperation.REVERT) == StrategyPriority.CANT_HANDLE) {
            throw new UnsupportedOperationException("Reverting not supported. Create a template or volume based on the snapshot instead.");
        }

        SnapshotVO snapshotVO = _snapshotDao.acquireInLockTable(snapshot.getId());
        if (snapshotVO == null) {
            throw new CloudRuntimeException("Failed to get lock on snapshot:" + snapshot.getId());
        }

        try {
            VolumeInfo volumeInfo = snapshot.getBaseVolume();
            StoragePool store = (StoragePool)volumeInfo.getDataStore();
            if (store != null && store.getStatus() != StoragePoolStatus.Up) {
                snapshot.processEvent(Event.OperationFailed);
                throw new CloudRuntimeException("store is not in up state");
            }
            volumeInfo.stateTransit(Volume.Event.RevertSnapshotRequested);
            boolean result = false;
            try {
                result =  snapshotSvr.revertSnapshot(snapshot);
                if (! result) {
                    s_logger.debug("Failed to revert snapshot: " + snapshot.getId());
                    throw new CloudRuntimeException("Failed to revert snapshot: " + snapshot.getId());
                }
            } finally {
                if (result) {
                    volumeInfo.stateTransit(Volume.Event.OperationSucceeded);
                } else {
                    volumeInfo.stateTransit(Volume.Event.OperationFailed);
                }
            }
            return result;
        } finally {
            if (snapshotVO != null) {
                _snapshotDao.releaseFromLockTable(snapshot.getId());
            }
        }
    }


    @Override
    public StrategyPriority canHandle(Snapshot snapshot, SnapshotOperation op) {
        long volumeId = snapshot.getVolumeId();
        VolumeVO volumeVO = _volumeDao.findById(volumeId);

        long storagePoolId;

        if (volumeVO == null) {
            SnapshotDataStoreVO snapshotStore = _snapshotStoreDao.findBySnapshot(snapshot.getId(), DataStoreRole.Primary);

            if (snapshotStore != null) {
                storagePoolId = snapshotStore.getDataStoreId();
            }
            else {
                throw new CloudRuntimeException("Unable to determine the storage pool of the snapshot");
            }
        }
        else {
            storagePoolId = volumeVO.getPoolId();
        }

        if (SnapshotOperation.REVERT.equals(op)) {
            if (volumeVO != null && ImageFormat.QCOW2.equals(volumeVO.getFormat()))
                return StrategyPriority.DEFAULT;
            else
                return StrategyPriority.CANT_HANDLE;
        }

        return StrategyPriority.CANT_HANDLE;
    }
}
