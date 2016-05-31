package com.cloud.service.dao;

import com.cloud.service.ServiceOfferingAuthorizationVO;
import com.cloud.utils.db.GenericDao;

public interface ServiceOfferingAuthorizationDao extends GenericDao<ServiceOfferingAuthorizationVO, Long> {
    ServiceOfferingAuthorizationVO findOneByDomain(long serviceOfferingId, long domainId);
    ServiceOfferingAuthorizationVO findOneByAccount(long serviceOfferingId, long accountId);
    int count(Long serviceOfferingId, Long domainId, Long accountId);
}
