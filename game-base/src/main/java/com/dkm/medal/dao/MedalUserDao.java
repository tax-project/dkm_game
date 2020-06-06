package com.dkm.medal.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.medal.entity.MedalUserEntity;
import org.springframework.stereotype.Repository;

/**
 * @description: 用户勋章dao
 * @author: zhd
 * @create: 2020-06-06 11:26
 **/
@Repository
public interface MedalUserDao extends BaseMapper<MedalUserEntity> {
}