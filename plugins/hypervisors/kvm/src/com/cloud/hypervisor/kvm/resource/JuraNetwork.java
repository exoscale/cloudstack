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
package com.cloud.hypervisor.kvm.resource;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class JuraNetwork {
    @SerializedName("DomainUUID") private UUID domainUUID;
    @SerializedName("DomainName") private String domainName;
    @SerializedName("MAC") private String mac;
    @SerializedName("Peers") private String[] peers;
    @SerializedName("Gateways") private String[] gateways;
    @SerializedName("Firewall") private JuraFirewallRule rules;

    public UUID getDomainUUID() {
        return domainUUID;
    }

    public void setDomainUUID(UUID domainUUID) {
        this.domainUUID = domainUUID;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public String[] getPeers() {
        return peers;
    }

    public void setPeers(String[] peers) {
        this.peers = peers;
    }

    public String[] getGateways() {
        return gateways;
    }

    public void setGateways(String[] gateways) {
        this.gateways = gateways;
    }

    public JuraFirewallRule getRules() {
        return rules;
    }

    public void setRules(JuraFirewallRule rules) {
        this.rules = rules;
    }
}
