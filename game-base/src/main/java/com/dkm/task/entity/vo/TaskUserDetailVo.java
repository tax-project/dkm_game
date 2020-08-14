package com.dkm.task.entity.vo;

import com.dkm.task.entity.TaskEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.List;

/**
 * @program: game_project
 * @description: 用户任务表
 * @author: zhd
 * @create: 2020-06-08 16:08
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskUserDetailVo extends TaskEntity {

    /**
     * 用户当前进度
     */
    private Integer tuProcess;
    /**
     * 1为完成
     */
    private Integer complete;

    @JsonIgnore
    private LocalDate time;

    /**
     * 物品奖励
     */
    private List<GoodListImg> goodListImg;

}
