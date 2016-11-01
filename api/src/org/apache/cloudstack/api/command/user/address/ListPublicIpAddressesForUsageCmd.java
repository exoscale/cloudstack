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
package org.apache.cloudstack.api.command.user.address;

import com.cloud.network.IpAddress;
import com.cloud.utils.Pair;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseAccountAndDomainResourcesCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ResponseObject.ResponseView;
import org.apache.cloudstack.api.response.IPAddressResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.NetworkResponse;
import org.apache.cloudstack.api.response.PhysicalNetworkResponse;
import org.apache.cloudstack.api.response.ZoneResponse;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@APICommand(name = "listPublicIpAddressesForUsage", description = "Lists all public ip addresses for usage", responseObject = IPAddressResponse.class, responseView = ResponseView.Restricted,
 requestHasSensitiveInfo = false, responseHasSensitiveInfo = false, entityType = { IpAddress.class })
public class ListPublicIpAddressesForUsageCmd extends BaseAccountAndDomainResourcesCmd {
    public static final Logger s_logger = Logger.getLogger(ListPublicIpAddressesForUsageCmd.class.getName());

    private static final String s_name = "listpublicipaddressesforusageresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ALLOCATED_ONLY, type = CommandType.BOOLEAN, description = "limits search results to allocated public IP addresses")
    private Boolean allocatedOnly;

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = IPAddressResponse.class, description = "lists ip address by id")
    private Long id;

    @Parameter(name = ApiConstants.IP_ADDRESS, type = CommandType.STRING, description = "lists the specified IP address")
    private String ipAddress;

    @Parameter(name = ApiConstants.ZONE_ID, type = CommandType.UUID, entityType = ZoneResponse.class, description = "lists all public IP addresses by Zone ID")
    private Long zoneId;

    @Parameter(name = ApiConstants.PHYSICAL_NETWORK_ID,
               type = CommandType.UUID,
               entityType = PhysicalNetworkResponse.class,
               description = "lists all public IP addresses by physical network id")
    private Long physicalNetworkId;

    @Parameter(name = ApiConstants.ASSOCIATED_NETWORK_ID,
               type = CommandType.UUID,
               entityType = NetworkResponse.class,
               description = "lists all public IP addresses associated to the network specified")
    private Long associatedNetworkId;

    @Parameter(name = ApiConstants.IS_ELASTIC, type = CommandType.BOOLEAN, description = "list only elastic ip addresses")
    private Boolean isElastic;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////
    public Long getId() {
        return id;
    }

    public Boolean isAllocatedOnly() {
        return allocatedOnly;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Long getZoneId() {
        return zoneId;
    }

    public Long getPhysicalNetworkId() {
        return physicalNetworkId;
    }

    public Long getAssociatedNetworkId() {
        return associatedNetworkId;
    }

    public Boolean getIsElastic() {
        return isElastic;
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
        Pair<List<? extends IpAddress>, Integer> result = _mgr.searchForIPAddresses(this);
        ListResponse<IPAddressResponse> response = new ListResponse<IPAddressResponse>();
        List<IPAddressResponse> ipAddrResponses = new ArrayList<IPAddressResponse>();
        for (IpAddress ipAddress : result.first()) {
            IPAddressResponse ipResponse = _responseGenerator.createIPAddressResponse(ResponseView.Restricted, ipAddress);
            ipResponse.setObjectName("publicipaddress");
            ipAddrResponses.add(ipResponse);
        }

        response.setResponses(ipAddrResponses, result.second());
        response.setResponseName(getCommandName());
        setResponseObject(response);
    }
}
