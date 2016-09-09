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

import com.google.gson.annotations.SerializedName;

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseResponse;
import org.apache.cloudstack.api.EntityReference;

import com.cloud.serializer.Param;
import com.cloud.storage.Snapshot;

@EntityReference(value = Snapshot.class)
public class SnapshotAccountResponse extends BaseResponse {
    @SerializedName(ApiConstants.ID)
    @Param(description = "ID of the snapshot")
    private String id;

    @SerializedName(ApiConstants.ACCOUNT)
    @Param(description = "the account associated with the snapshot")
    private String accountName;

    @SerializedName(ApiConstants.ACCOUNT_ID)
    @Param(description = "the ID of the account associated with the snapshot")
    private String accountId;

    @SerializedName(ApiConstants.DOMAIN_ID)
    @Param(description = "the ID of the domain associated with the disk volume")
    private String domainId;

    @SerializedName(ApiConstants.DOMAIN)
    @Param(description = "the domain associated with the disk volume")
    private String domainName;

    @SerializedName(ApiConstants.SIZE)
    @Param(description = "the size of original volume")
    private Long size;

    @SerializedName(ApiConstants.PHYSICAL_SIZE)
    @Param(description = "the physical size of the snapshot")
    private Long physicalSize;

    @Override
    public String getObjectId() { return this.getId(); }

    private String getId() { return id; }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setAccountId(String accountId) { this.accountId = accountId; }

    public void setDomainId(String domainId) { this.domainId = domainId; }

    public void setDomainName(String domainName) { this.domainName = domainName; }

    public void setSize(long size) {
        this.size = new Long(size);
    }

    public void setPhysicalSize(long physicalSize) {
        this.physicalSize = physicalSize;
    }
}
