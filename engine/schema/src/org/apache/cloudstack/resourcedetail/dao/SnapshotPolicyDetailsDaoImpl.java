package org.apache.cloudstack.resourcedetail.dao;


import org.springframework.stereotype.Component;

import org.apache.cloudstack.resourcedetail.ResourceDetailsDaoBase;
import org.apache.cloudstack.resourcedetail.SnapshotPolicyDetailVO;

@Component
public class SnapshotPolicyDetailsDaoImpl extends ResourceDetailsDaoBase<SnapshotPolicyDetailVO> implements SnapshotPolicyDetailsDao {

    @Override
    public void addDetail(long resourceId, String key, String value, boolean display) {
        super.addDetail(new SnapshotPolicyDetailVO(resourceId, key, value));
    }
}