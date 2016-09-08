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
package org.apache.cloudstack.api.command.user.vm;

import com.cloud.vm.VirtualMachine;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseAccountAndDomainResourcesCmd;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ResponseObject.ResponseView;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.UsageUserVmResponse;
import org.apache.log4j.Logger;

import java.util.List;


@APICommand(name = "listVirtualMachinesForUsage", description = "List the virtual machines owned by the account for usage.", responseObject = UsageUserVmResponse.class, responseView = ResponseView.Restricted, entityType = {VirtualMachine.class},
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = true)
public class ListVMsForUsageCmd extends BaseAccountAndDomainResourcesCmd {
    public static final Logger s_logger = Logger.getLogger(ListVMsForUsageCmd.class.getName());

    private static final String s_name = "listvirtualmachinesforusageresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = BaseCmd.CommandType.UUID, entityType = UsageUserVmResponse.class, description = "the ID of the virtual machine")
    private Long id;

    @Parameter(name=ApiConstants.IDS, type= BaseCmd.CommandType.LIST, collectionType= BaseCmd.CommandType.UUID, entityType=UsageUserVmResponse.class, description="the IDs of the virtual machines, mutually exclusive with id", since = "4.4")
    private List<Long> ids;

    @Parameter(name = ApiConstants.STATE, type = BaseCmd.CommandType.STRING, description = "state of the virtual machine")
    private String state;


    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public List<Long> getIds() {
        return ids;
    }

    public String getState() {
        return state;
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
        ListResponse<UsageUserVmResponse> response = _queryService.searchForUserVMs(this);
        response.setResponseName(getCommandName());
        setResponseObject(response);
    }
}
