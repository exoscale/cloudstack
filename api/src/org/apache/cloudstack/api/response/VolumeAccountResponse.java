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

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseResponse;
import org.apache.cloudstack.api.EntityReference;

import com.cloud.serializer.Param;
import com.cloud.storage.Volume;
import com.google.gson.annotations.SerializedName;

@EntityReference(value = Volume.class)
@SuppressWarnings("unused")
public class VolumeAccountResponse extends BaseResponse {
    @SerializedName(ApiConstants.ID)
    @Param(description = "ID of the disk volume")
    private String id;

    @SerializedName(ApiConstants.SIZE)
    @Param(description = "size of the disk volume")
    private Long size;

    @SerializedName(ApiConstants.ACCOUNT)
    @Param(description = "the account associated with the disk volume")
    private String accountName;

    @SerializedName(ApiConstants.ACCOUNT_ID)
    @Param(description = "the ID of the account associated with the disk volume")
    private String accountId;

    @SerializedName(ApiConstants.DOMAIN_ID)
    @Param(description = "the ID of the domain associated with the disk volume")
    private String domainId;

    @SerializedName(ApiConstants.DOMAIN)
    @Param(description = "the domain associated with the disk volume")
    private String domainName;

    @SerializedName(ApiConstants.STATE)
    @Param(description = "the state of the disk volume")
    private String state;

    @SerializedName(ApiConstants.TYPE)
    @Param(description = "the type of the disk volume")
    private String type;

    @Override
    public String getObjectId() {
        return this.getId();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setType(String type) {
        this.type = type;
    }
}