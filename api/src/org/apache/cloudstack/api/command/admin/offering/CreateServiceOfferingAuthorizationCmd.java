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
package org.apache.cloudstack.api.command.admin.offering;

import com.cloud.exception.InvalidParameterValueException;
import com.cloud.offering.ServiceOfferingAuthorization;
import com.cloud.user.Account;
import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.AccountResponse;
import org.apache.cloudstack.api.response.DomainResponse;
import org.apache.cloudstack.api.response.ServiceOfferingAuthorizationResponse;
import org.apache.cloudstack.api.response.ServiceOfferingResponse;

@APICommand(name = "createServiceOfferingAuthorization", description = "Creates a service offering authorization for a domain or an account to use a restricted service offering. You cannot set a domain and an account at the same time.", responseObject = ServiceOfferingAuthorizationResponse.class,
        requestHasSensitiveInfo = false, responseHasSensitiveInfo = false)
public class CreateServiceOfferingAuthorizationCmd extends BaseCmd {
    private static final String s_name = "createserviceofferingauthorizationresponse";

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.SERVICE_OFFERING_ID, type = CommandType.UUID, entityType = ServiceOfferingResponse.class, description = "the ID of the service offering for which to set the authorization", required = true)
    private Long serviceOfferingId;

    @Parameter(name = ApiConstants.DOMAIN_ID, type = CommandType.UUID, entityType = DomainResponse.class, description = "the ID of the domain authorized to use the service offering (mutually exclusive with the account id)")
    private Long domainId;

    @Parameter(name = ApiConstants.ACCOUNT_ID, type = CommandType.UUID, entityType = AccountResponse.class, description = "the ID of the account authorized to use the service offering (mutually exclusive with the domain id)")
    private Long accountId;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getServiceOfferingId() {
        return serviceOfferingId;
    }

    public void setServiceOfferingId(Long serviceOfferingId) {
        this.serviceOfferingId = serviceOfferingId;
    }

    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    @Override
    public void execute() {
        if (getDomainId() != null && getAccountId() != null) {
            throw new InvalidParameterValueException("You cannot specify both a domain and an account");
        }
        ServiceOfferingAuthorization result = _configService.createServiceOfferingAuthorization(this);
        if (result != null) {

            ServiceOfferingAuthorizationResponse response = _responseGenerator.createServiceOfferingAuthorizationResponse(result);
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } else {
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, "Failed to add service offering authorization");
        }
    }
}
