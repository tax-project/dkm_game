package com.dkm.mallEvents.service;

import com.dkm.mallEvents.entities.data.CancerGiftBoxInfoVo;
import com.dkm.mallEvents.entities.data.LuckyGiftVo;
import com.dkm.mallEvents.entities.data.RechargeVo;


public interface ICancerService {
    /**
     * 获取单笔充值满30领取豪华礼包的接口
     */
    RechargeVo getSingleTopUp(Long id);

    Boolean getSingleTopUpInfoCheck(Long userId, Integer id);

    /**
     * 累计充值/消费大返利的累计充值查询
     */
    RechargeVo getCumulativeRecharge(Long id);

    Boolean receiveTopUpReward(Long userId, Integer itemId);

    /**
     * 累计充值/消费大返利的累计消费查询
     */
    RechargeVo getConsumerRecharge(Long id);

    Boolean receiveConsumerReward(Long id, Integer itemId);

    Boolean consumptionKingReward(Long id, Integer id1);

    /**
     * 消耗有礼查询 [金星星]
     */
    RechargeVo consumptionKing(Long id);

    /**
     * 消耗有礼查询 [紫星星]
     */
    RechargeVo consumptionPurple(Long id);

    Boolean consumptionKingPurple(Long id, Integer id1);

    /**
     * 消耗有礼查询 [蓝劵]
     */
    RechargeVo consumptionBlue(Long id);

    Boolean consumptionKingBlue(Long id, Integer id1);

    /**
     * 消耗有礼查询 [紫劵]
     */
    RechargeVo consumptionPurpleOffer(Long id);

    Boolean consumptionKingPurpleOffer(Long id, Integer id1);

    Boolean consumptionKingOrange(Long id, Integer id1);

    /**
     * 消耗有礼查询 [橙劵]
     */
    RechargeVo consumptionOrange(Long id);

    /**
     * 幸运礼物
     */
    LuckyGiftVo getLuckyGiftInfo();

    /**
     * 巨蟹礼盒
     */
    CancerGiftBoxInfoVo getCancerGiftBoxInfo();

    RechargeVo getRechargeTheBlueRoll(Long id);

    Boolean rechargeTheBlueRollCheck(Long id, Integer id1);
}
