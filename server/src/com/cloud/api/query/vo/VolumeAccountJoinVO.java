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
package com.cloud.api.query.vo;

import com.cloud.storage.Volume;
import com.cloud.vm.VirtualMachine;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.SecondaryTable;
import javax.persistence.SecondaryTables;
import javax.persistence.Table;

@Entity
@Table(name = "volumes")
@SecondaryTables({
        @SecondaryTable(name = "account", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "account_id", referencedColumnName = "id")}),
        @SecondaryTable(name = "domain", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "domain_id", referencedColumnName = "id")}),
        @SecondaryTable(name = "vm_instance", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "instance_id", referencedColumnName = "id")})})
public class VolumeAccountJoinVO extends BaseViewVO implements ControlledViewEntity {

    @Id
    @Column(name = "id")
    protected long id;

    @Column(name = "uuid")
    protected String uuid;

    @Column(name = "size")
    protected long size;

    @Column(name = "volume_type")
    protected Volume.Type type;

    @Column(name = "state")
    @Enumerated(value = EnumType.STRING)
    protected Volume.State state;

    @Column(name = "id", table="account")
    protected long accountId;

    @Column(name = "uuid", table="account")
    protected String accountUuid;

    @Column(name = "type", table = "account")
    protected short accountType;

    @Column(name = "account_name", table="account")
    protected String accountName;

    @Column(name = "id", table="domain")
    protected long domainId;

    @Column(name = "uuid", table="domain")
    protected String domainUuid;

    @Column(name = "name", table="domain")
    protected String domainName;

    @Column(name = "path", table="domain")
    protected String domainPath;

    @Column(name = "type", table = "vm_instance")
    @Enumerated(value = EnumType.STRING)
    protected VirtualMachine.Type vmType;

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    @Override
    public String getAccountUuid() {
        return accountUuid;
    }

    @Override
    public String getAccountName() {
        return accountName;
    }

    @Override
    public String getDomainUuid() {
        return domainUuid;
    }

    @Override
    public long getDomainId() {
        return domainId;
    }

    @Override
    public String getDomainName() {
        return domainName;
    }

    @Override
    public String getProjectUuid() {
        return null;
    }

    @Override
    public String getProjectName() {
        return null;
    }

    @Override
    public String getDomainPath() {
        return domainPath;
    }

    @Override
    public short getAccountType() {
        return accountType;
    }

    public long getSize() { return size; }

    public Volume.Type getType() { return type; }

    public Volume.State getState() {
        return state;
    }

    public VirtualMachine.Type getVmType() {
        return vmType;
    }

    @Override
    public Class<?> getEntityType() {
        return Volume.class;
    }
}
