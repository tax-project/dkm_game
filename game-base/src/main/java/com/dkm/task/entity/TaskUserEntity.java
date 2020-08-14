package com.dkm.task.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @program: game_project
 * @description:
 * @author: zhd
 * @create: 2020-08-13 09:35
 **/
@Data
@TableName("tb_task_user")
public class TaskUserEntity {
    /**
     * id
     */
    @TableId
    private Long  tuId;

    /**
     * userId
     */
    private Long userId;
    /**
     * 当前进度
     */
    private Integer tuProcess;
    /**
     * 任务id
     */
    private Long taskId;
    /**
     * 1为已完成任务
     */
    private Integer complete;

    /**
     * 记录时间
     */
    private LocalDate time;
}
