package com.leyou.item.service.impl;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LyException;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Autowired
    private SpecGroupMapper groupDao;

    @Autowired
    private SpecParamMapper paramDao;

    @Override
    public List<SpecGroup> queryGroupByCid(Long cid) {
        SpecGroup group = new SpecGroup();
        group.setCid(cid);
        List<SpecGroup> list = groupDao.select(group);
        if (CollectionUtils.isEmpty(list)) {
            // 没查到
            throw new LyException(ExceptionEnum.SPEC_GROUP_NOT_FOND);
        }
        return list;
    }

    @Override
    public List<SpecParam> queryParamList(Long gid, Long cid, Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setSearching(searching);
        List<SpecParam> list = paramDao.select(param);
        if (CollectionUtils.isEmpty(list)) {
            // 没查到
            throw new LyException(ExceptionEnum.SPEC_PARAM_NOT_FOND);
        }
        return list;
    }
}
