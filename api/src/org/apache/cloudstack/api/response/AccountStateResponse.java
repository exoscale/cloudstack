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
import com.cloud.user.Account;

@SuppressWarnings("unused")
@EntityReference(value = Account.class)
public class AccountStateResponse extends BaseResponse {
    @SerializedName(ApiConstants.NAME)
    @Param(description = "the name of the account")
    private String name;

    @SerializedName(ApiConstants.STATE)
    @Param(description = "the state of the account")
    private String state;

    @SerializedName(ApiConstants.ID)
    @Param(description = "the id of the account")
    private String id;

    public void setName(String name) {
        this.name = name;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setId(String id) { this.id = id; }

    @Override
    public String getObjectId() { return id; }
}