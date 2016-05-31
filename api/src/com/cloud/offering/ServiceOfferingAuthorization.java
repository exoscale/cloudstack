package com.cloud.offering;

import org.apache.cloudstack.api.Identity;
import org.apache.cloudstack.api.InternalIdentity;

public interface ServiceOfferingAuthorization extends InternalIdentity, Identity {
    long getResourceId();
    Long getDomainId();
    Long getAccountId();
}
