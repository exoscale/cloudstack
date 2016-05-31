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
package org.apache.cloudstack.api.response;

import com.cloud.offering.ServiceOfferingAuthorization;
import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseResponse;
import org.apache.cloudstack.api.EntityReference;

@EntityReference(value = ServiceOfferingAuthorization.class)
public class ServiceOfferingAuthorizationResponse extends BaseResponse {

    @SerializedName(ApiConstants.ID)
    @Param(description = "the id of the service offering authorization")
    private String id;

    @SerializedName(ApiConstants.SERVICE_OFFERING_ID)
    @Param(description = "the id of the service offering")
    private String serviceOfferingId;

    @SerializedName(ApiConstants.DOMAIN_ID)
    @Param(description = "the domain id which can use the service offering")
    private String domainId;

    @SerializedName(ApiConstants.ACCOUNT_ID)
    @Param(description = "the account id which can use the service offering")
    private String accountId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getServiceOfferingId() {
        return serviceOfferingId;
    }

    public void setServiceOfferingId(String serviceOfferingId) {
        this.serviceOfferingId = serviceOfferingId;
    }

    public String getDomainId() {
        return domainId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}
