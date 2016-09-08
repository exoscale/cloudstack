package com.cloud.api.query.dao;

import com.cloud.api.query.vo.VMInstanceUsageVO;
import com.cloud.utils.db.GenericDaoBase;
import org.springframework.stereotype.Component;

@Component
public class VMInstanceUsageDaoImpl extends GenericDaoBase<VMInstanceUsageVO, Long> implements VMInstanceUsageDao {
}
