package com.cloud.api.query.vo;

import com.cloud.utils.db.StateMachine;
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
import java.util.Date;

@Entity
@Table(name = "vm_instance")
@SecondaryTables({
        @SecondaryTable(name = "account", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "account_id", referencedColumnName = "id")}),
        @SecondaryTable(name = "domain", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "domain_id", referencedColumnName = "id")}),
        @SecondaryTable(name = "vm_template", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "vm_template_id", referencedColumnName = "id")}),
        @SecondaryTable(name = "disk_offering", pkJoinColumns = {@PrimaryKeyJoinColumn(name = "service_offering_id", referencedColumnName = "id")})})
public class VMInstanceUsageVO implements ControlledViewEntity {
    @Id
    @Column(name = "id")
    protected long id;

    @Enumerated(value = EnumType.STRING)
    @StateMachine(state = VirtualMachine.State.class, event = VirtualMachine.Event.class)
    @Column(name = "state", updatable = true, nullable = false, length = 32)
    protected VirtualMachine.State state = null;

    @Column(name = "uuid")
    protected String uuid;

    @Column(name = "created")
    protected Date created;

    @Column(name = "type")
    @Enumerated(value = EnumType.STRING)
    protected VirtualMachine.Type type;

    @Column(name = "uuid", table = "vm_template")
    protected String templateUuid;

    @Column(name = "uuid", table = "disk_offering")
    protected String serviceOfferingUuid;

    @Column(name = "id", table = "account")
    protected long accountId;

    @Column(name = "uuid", table = "account")
    protected String accountUuid;

    @Column(name = "account_name", table = "account")
    protected String accountName;

    @Column(name = "type", table = "account")
    protected short accountType;

    @Column(name = "id", table = "domain")
    protected long domainId;

    @Column(name = "uuid", table = "domain")
    protected String domainUuid;

    @Column(name = "name", table = "domain")
    protected String domainName;

    @Override
    public long getId() {
        return id;
    }

    protected void setId(long id) {
        this.id = id;
    }

    public VirtualMachine.State getState() {
        return state;
    }

    protected void setState(VirtualMachine.State state) {
        this.state = state;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    protected void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Date getCreated(){
        return created;
    }

    protected void setCreated(Date created) {
        this.created = created;
    }

    public VirtualMachine.Type getType() {
        return type;
    }

    public void setType(VirtualMachine.Type type) {
        this.type = type;
    }

    public String getTemplateUuid() {
        return templateUuid;
    }

    protected void setTemplateUuid(String templateUuid) {
        this.templateUuid = templateUuid;
    }

    public String getServiceOfferingUuid() {
        return serviceOfferingUuid;
    }

    protected void setServiceOfferingUuid(String serviceOfferingUuid) {
        this.serviceOfferingUuid = serviceOfferingUuid;
    }

    public String getAccountUuid() {
        return accountUuid;
    }

    public void setAccountUuid(String accountUuid) {
        this.accountUuid = accountUuid;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public short getAccountType() {
        return accountType;
    }

    protected void setAccountType(short accountType) {
        this.accountType = accountType;
    }

    public String getDomainUuid() {
        return domainUuid;
    }

    public void setDomainUuid(String domainUuid) {
        this.domainUuid = domainUuid;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    @Override
    public Class<?> getEntityType() {
        return null;
    }

    protected void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    @Override
    public long getAccountId() {
        return accountId;
    }

    protected void setDomainId(long domainId) {
        this.domainId = domainId;
    }

    @Override
    public long getDomainId() {
        return domainId;
    }

    @Override
    public String getDomainPath() {
        return null;
    }

    @Override
    public String getProjectUuid() {
        return null;
    }

    @Override
    public String getProjectName() {
        return null;
    }

}
