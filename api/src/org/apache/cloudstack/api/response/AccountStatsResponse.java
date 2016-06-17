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

import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.BaseResponse;

public class AccountStatsResponse extends BaseResponse {
    @SerializedName(ApiConstants.TOTAL)
    @Param(description = "Total number of account")
    private Long accountTotal;

    @SerializedName(ApiConstants.ENABLED)
    @Param(description = "Total number of enabled account")
    private Long accountEnabledTotal;

    @SerializedName(ApiConstants.DISABLED)
    @Param(description = "Total number of disabled account")
    private Long accountDisabledTotal;

    public Long getAccountTotal() {
        return accountTotal;
    }

    public void setAccountTotal(Long accountTotal) {
        this.accountTotal = accountTotal;
    }

    public Long getAccountEnabledTotal() {
        return accountEnabledTotal;
    }

    public void setAccountEnabledTotal(Long accountEnabledTotal) {
        this.accountEnabledTotal = accountEnabledTotal;
    }

    public Long getAccountDisabledTotal() {
        return accountDisabledTotal;
    }

    public void setAccountDisabledTotal(Long accountDisabledTotal) {
        this.accountDisabledTotal = accountDisabledTotal;
    }
}
