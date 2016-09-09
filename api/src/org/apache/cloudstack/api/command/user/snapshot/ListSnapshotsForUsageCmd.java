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
package org.apache.cloudstack.api.command.user.snapshot;

import org.apache.cloudstack.api.BaseAccountAndDomainResourcesCmd;
import org.apache.cloudstack.api.response.SnapshotAccountResponse;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.SnapshotResponse;

import com.cloud.storage.Snapshot;

@APICommand(name = "listSnapshotsForUsage",
        description = "Lists all available snapshots for the account.",
        responseObject = SnapshotAccountResponse.class,
        entityType = {Snapshot.class},
        requestHasSensitiveInfo = false)
public class ListSnapshotsForUsageCmd extends BaseAccountAndDomainResourcesCmd {
    public static final Logger s_logger = Logger.getLogger(ListSnapshotsForUsageCmd.class.getName());

    private static final String s_name = "listsnapshotsforusageresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = SnapshotResponse.class, description = "lists snapshot by snapshot ID")
    private Long id;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public void execute() {
        ListResponse<SnapshotAccountResponse> response = _queryService.searchForSnapshots(this);
        response.setResponseName(getCommandName());
        setResponseObject(response);
    }
}
