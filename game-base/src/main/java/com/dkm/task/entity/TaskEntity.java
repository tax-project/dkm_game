package com.dkm.task.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.List;

/**
 * @program: game_project
 * @description: 任务表
 * @author: zhd
 * @create: 2020-06-08 15:03
 **/
@Data
@TableName("tb_task")
public class TaskEntity {

    /**
     * 任务id
     */
    @TableId
    private Long taskId;
    /**
     * 任务id
     */
    private Integer taskProcess;
    /**
     * 任务id
     */
    private String taskDetail;
    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 标题
     */
    private String taskTitle;
    /**
     * 标题
     */
    private String taskUrl;

    /**
     * 挑战页面类型
     */
    private Integer taskUrlType;
    /**
     * 挑战奖励物品id、数量
     */
    @JsonIgnore
    private String goodList;

}
