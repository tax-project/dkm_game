package com.dkm.task.dao;

import com.dkm.IBaseMapper.IBaseMapper;
import com.dkm.task.entity.TaskUserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description:
 * @author: zhd
 * @create: 2020-08-13 09:38
 **/
@Mapper
public interface TaskUserMapper extends IBaseMapper<TaskUserEntity> {
}
