package com.dkm.task.service;

import com.dkm.task.entity.vo.TaskUserDetailVo;

import java.util.List;

/**
 * @program: game_project
 * @description: 任务
 * @author: zhd
 * @create: 2020-06-08 14:58
 **/
public interface TaskService {


    List<TaskUserDetailVo> selectUserTask(Long userId,Integer type);

    void getTaskReward(Long userId,Long taskId);

    void setTaskProcess(Long userId,Long taskId);
}
