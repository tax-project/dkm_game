package com.dkm.gift.entity.dto;

import com.dkm.gift.entity.GiftRankingEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @program: game_project
 * @description:
 * @author: zhd
 * @create: 2020-06-18 14:57
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class GiftRankingDto extends GiftRankingEntity {
    private String weChatNickName;
    private String weChatHeadImgUrl;
    private Integer renown;
    private Integer medal;
    private Integer userSex;
    private Integer userGrade;
}
