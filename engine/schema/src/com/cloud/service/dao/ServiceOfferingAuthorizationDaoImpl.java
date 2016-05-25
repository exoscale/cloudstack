package com.cloud.service.dao;

import com.cloud.service.ServiceOfferingAuthorizationVO;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;

public class ServiceOfferingAuthorizationDaoImpl extends GenericDaoBase<ServiceOfferingAuthorizationVO, Long> implements ServiceOfferingAuthorizationDao {

    protected final SearchBuilder<ServiceOfferingAuthorizationVO> UniqueFindByDomain;
    protected final SearchBuilder<ServiceOfferingAuthorizationVO> UniqueFindByAccount;

    public ServiceOfferingAuthorizationDaoImpl() {
        super();

        UniqueFindByDomain = createSearchBuilder();
        UniqueFindByDomain.and("resourceId", UniqueFindByDomain.entity().getResourceId(), SearchCriteria.Op.EQ);
        UniqueFindByDomain.and("domainId", UniqueFindByDomain.entity().getDomainId(), SearchCriteria.Op.EQ);
        UniqueFindByDomain.done();

        UniqueFindByAccount = createSearchBuilder();
        UniqueFindByAccount.and("resourceId", UniqueFindByAccount.entity().getResourceId(), SearchCriteria.Op.EQ);
        UniqueFindByAccount.and("accountId", UniqueFindByAccount.entity().getAccountId(), SearchCriteria.Op.EQ);
        UniqueFindByAccount.done();
    }

    @Override
    public ServiceOfferingAuthorizationVO findOneByDomain(long serviceOfferingId, long domainId) {
        SearchCriteria<ServiceOfferingAuthorizationVO> sc = UniqueFindByDomain.create();
        sc.setParameters("resourceId", serviceOfferingId);
        sc.setParameters("domainId", domainId);
        return findOneBy(sc);
    }

    @Override
    public ServiceOfferingAuthorizationVO findOneByAccount(long serviceOfferingId, long accountId) {
        SearchCriteria<ServiceOfferingAuthorizationVO> sc = UniqueFindByAccount.create();
        sc.setParameters("resourceId", serviceOfferingId);
        sc.setParameters("accountId", accountId);
        return  findOneBy(sc);
    }

    @Override
    public int count(Long serviceOfferingId, Long domainId, Long accountId) {
        SearchCriteria<ServiceOfferingAuthorizationVO> sc = createSearchCriteria();

        if (serviceOfferingId != null) {
            sc.addAnd("resourceId", SearchCriteria.Op.EQ, serviceOfferingId);
        }

        if (domainId != null) {
            sc.addAnd("domainId", SearchCriteria.Op.EQ, domainId);
        }

        if (accountId != null) {
            sc.addAnd("accountId", SearchCriteria.Op.EQ, accountId);
        }

        Integer result = getCount(sc);
        return (result == null ? 0 : result.intValue());
    }
}
