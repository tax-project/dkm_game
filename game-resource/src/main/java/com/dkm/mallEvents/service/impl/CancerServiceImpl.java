package com.dkm.mallEvents.service.impl;

import com.dkm.backpack.entity.bo.AddGoodsInfo;
import com.dkm.backpack.service.IBackpackService;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.mallEvents.dao.RewardDao;
import com.dkm.mallEvents.entities.data.CancerGiftBoxInfoVo;
import com.dkm.mallEvents.entities.data.GoodsVo;
import com.dkm.mallEvents.entities.data.LuckyGiftVo;
import com.dkm.mallEvents.entities.data.RechargeVo;
import com.dkm.mallEvents.service.ICancerService;
import com.dkm.utils.DateUtils;
import lombok.val;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * 注意，由于接口未对接，不存在历史记录的问题
 */
@Service
public class CancerServiceImpl implements ICancerService {

    @Resource
    private IBackpackService backpackService;

    /**
     * 注意啦，这个没有判断是否充值完成的，因为暂时不存在充值模块
     */

    @Resource
    private RewardDao rewardDao;

    @Override
    public RechargeVo getSingleTopUp(Long userId) {
        return getInfo(userId, "豪华礼包");
    }

    @Override
    public Boolean getSingleTopUpInfoCheck(Long userId, Integer id) {
        return receive(userId, id);
    }


    @Override
    public RechargeVo getCumulativeRecharge(Long userId) {
        return getInfo(userId, "累计充值");
    }

    @Override
    public Boolean receiveTopUpReward(Long userId, Integer itemId) {
        return receive(userId, itemId);
    }

    @Override
    public RechargeVo getConsumerRecharge(Long id) {
        return getInfo(id, "累计消耗");
    }

    @Override
    public Boolean receiveConsumerReward(Long id, Integer itemId) {
        return receive(id, itemId);
    }

    @Override
    public Boolean consumptionKingReward(Long id, Integer id1) {
        return receive(id, id1);
    }

    @Override
    public RechargeVo consumptionKing(Long id) {
        return getInfo(id, "消耗有礼-金星");
    }

    @Override
    public RechargeVo consumptionPurple(Long id) {
        return getInfo(id, "消耗有礼-紫卷");
    }

    @Override
    public Boolean consumptionKingPurple(Long id, Integer id1) {
        return receive(id, id1);
    }

    @Override
    public RechargeVo consumptionBlue(Long id) {
        return getInfo(id, "消耗有礼-蓝卷");
    }

    @Override
    public Boolean consumptionKingBlue(Long id, Integer id1) {
        return receive(id, id1);
    }

    @Override
    public RechargeVo consumptionPurpleOffer(Long id) {
        return getInfo(id, "消耗有礼-紫卷");
    }

    @Override
    public Boolean consumptionKingPurpleOffer(Long id, Integer id1) {
        return receive(id, id1);
    }

    @Override
    public Boolean consumptionKingOrange(Long id, Integer id1) {
        return receive(id, id1);
    }

    @Override
    public RechargeVo consumptionOrange(Long id) {
        return getInfo(id, "消耗有礼-橙卷");
    }

    @Override
    public RechargeVo getRechargeTheBlueRoll(Long id) {
        return getInfo(id, "充值送蓝卷");
    }

    @Override
    public Boolean rechargeTheBlueRollCheck(Long id, Integer id1) {
        return receive(id, id1);
    }

    @Override
    public LuckyGiftVo getLuckyGiftInfo() {
        val luckyGiftInfo = rewardDao.getLuckyGiftInfo();
        val localDateTime = DateUtils.parseDateTime(luckyGiftInfo.getNextRefreshDate());
        if (localDateTime.isBefore(LocalDateTime.now())) {
            val s = DateUtils.formatDateTime(localDateTime.plusDays(1));
            luckyGiftInfo.setNextRefreshDate(s);
            rewardDao.saveNextRefreshDate(s);
        }
        luckyGiftInfo.setLastLuckyItem(luckyGiftInfo.getLuckyItems().get(0));
        return luckyGiftInfo;
    }

    @Override
    public CancerGiftBoxInfoVo getCancerGiftBoxInfo() {
        return null;
    }

    /**
     * 搜索累计充值
     * <p>
     * 注意啦，这个没有判断是否充值完成的，因为暂时不存在充值模块
     */
    private RechargeVo getInfo(Long userId, String typeName) {
        val rechargeItemVo = rewardDao.selectInfoByTypeAndUserId(typeName, userId);
        val rechargeVo = new RechargeVo();
        rechargeVo.setPrizes(rechargeItemVo);
        return rechargeVo;
    }

    private Boolean receive(Long userId, Integer itemId) {
        val goodsList = rewardDao.selectItemByItemIdAndType(itemId);
        if (goodsList == null || goodsList.size() == 0) {
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "未找到此数据");
        }
        if (rewardDao.selectHistorySizeByItemIdAndUserId(itemId, userId) != 0) {
            throw new ApplicationException(CodeType.RESOURCES_EXISTING, "你已经领取过啦.");
        }
        for (GoodsVo goodsVo : goodsList) {
            AddGoodsInfo addGoodsInfo = new AddGoodsInfo();
            addGoodsInfo.setUserId(userId);
            addGoodsInfo.setGoodId(goodsVo.getId());
            addGoodsInfo.setNumber(goodsVo.getSize());
            backpackService.addBackpackGoods(addGoodsInfo);
        }
        rewardDao.addHistory(userId, itemId);
        return true;
    }
}
