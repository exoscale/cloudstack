package com.cloud.service;

import com.cloud.offering.ServiceOfferingAuthorization;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "service_offering_authorizations")
public class ServiceOfferingAuthorizationVO implements ServiceOfferingAuthorization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "service_offering_id")
    long resourceId;

    @Column(name = "domain_id")
    Long domainId;

    @Column(name = "account_id")
    Long accountId;

    public ServiceOfferingAuthorizationVO() {
        this.uuid = UUID.randomUUID().toString();
    }

    public ServiceOfferingAuthorizationVO(long resourceId, Long domainId, Long accountId) {
        this.resourceId = resourceId;
        this.domainId = domainId;
        this.accountId = accountId;
        this.uuid = UUID.randomUUID().toString();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    @Override
    public long getResourceId() {
        return resourceId;
    }

    public void setResourceId(long resourceId) {
        this.resourceId = resourceId;
    }

    @Override
    public Long getDomainId() {
        return domainId;
    }

    public void setDomainId(Long domainId) {
        this.domainId = domainId;
    }

    @Override
    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
}
