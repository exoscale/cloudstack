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
package org.apache.cloudstack.api.command.user.volume;

import org.apache.cloudstack.api.BaseAccountAndDomainResourcesCmd;
import org.apache.cloudstack.api.response.VolumeAccountResponse;
import org.apache.log4j.Logger;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiCommandJobType;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ResponseObject.ResponseView;
import org.apache.cloudstack.api.response.HostResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.VolumeResponse;

import com.cloud.storage.Volume;

@APICommand(name = "listVolumesForUsage",
        description = "Lists all volume information relevant for usage.",
        responseObject = VolumeAccountResponse.class,
        responseView = ResponseView.Restricted,
        entityType = {Volume.class},
        requestHasSensitiveInfo = false,
        responseHasSensitiveInfo = false)
public class ListVolumesForUsageCmd extends BaseAccountAndDomainResourcesCmd {
    public static final Logger s_logger = Logger.getLogger(ListVolumesForUsageCmd.class.getName());

    private static final String s_name = "listvolumesforusageresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.HOST_ID, type = CommandType.UUID, entityType = HostResponse.class, description = "list volumes on specified host")
    private Long hostId;

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = VolumeResponse.class, description = "the ID of the disk volume")
    private Long id;

    @Parameter(name = ApiConstants.NAME, type = CommandType.STRING, description = "the name of the disk volume")
    private String volumeName;

    @Parameter(name = ApiConstants.TYPE, type = CommandType.STRING, description = "the type of disk volume")
    private String type;

    @Parameter(name = ApiConstants.STATE, type = CommandType.STRING, description = "the state of disk volume")
    private String state;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getHostId() {
        return hostId;
    }

    public Long getId() {
        return id;
    }

    public String getVolumeName() {
        return volumeName;
    }

    public String getType() { return type; }

    public String getState() { return state; }

    public Long getProjectId() { return null; }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    public ApiCommandJobType getInstanceType() {
        return ApiCommandJobType.Volume;
    }

    @Override
    public void execute() {
        ListResponse<VolumeAccountResponse> response = _queryService.searchForVolumes(this);
        response.setResponseName(getCommandName());
        setResponseObject(response);
    }
}
