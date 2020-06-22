package com.dkm.produce.entity.vo;

import lombok.Data;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @DATE: 2020/5/20 9:08
 */
@Data
public class AttendantImgVo {
   /**
    *跟班图片
    */
    private String weChatHeadImgUrl;

    /**
     *跟班id
     */
    private Integer attendantId;

}