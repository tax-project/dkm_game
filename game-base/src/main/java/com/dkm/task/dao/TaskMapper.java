package com.dkm.task.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.task.entity.TaskEntity;
import com.dkm.task.entity.vo.TaskUserDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @description: 任务
 * @author: zhd
 * @create: 2020-06-08 15:27
 **/
@Repository
public interface TaskMapper extends BaseMapper<TaskEntity> {


    /**
     * 查询用户任务进度
     * @param type
     * @param userId
     * @return
     */
    @Select("SELECT t.*,IFNULL(tu.tu_process,0) as tu_process,IFNULL(tu.complete,0) as complete,tu.time FROM ( " +
            "SELECT*FROM tb_task WHERE task_type=#{type}) t LEFT JOIN ( " +
            "SELECT * FROM tb_task_user WHERE user_id=#{userId}) tu ON t.task_id=tu.task_id")
    List<TaskUserDetailVo> selectUserTask(@Param("type") Integer type,@Param("userId") Long userId);

    @Update("update tb_user_info set user_info_gold = user_info_gold+#{gold},user_info_now_experience=user_info_now_experience+#{exp}")
    Integer updateUserInfo(@Param("gold") Integer gold,@Param("exp") Integer exp);
}
