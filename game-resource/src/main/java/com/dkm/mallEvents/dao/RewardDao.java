package com.dkm.mallEvents.dao;

import com.dkm.mallEvents.entities.data.GoodsVo;
import com.dkm.mallEvents.entities.data.LuckyGiftVo;
import com.dkm.mallEvents.entities.data.RechargeItemVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RewardDao {

    List<RechargeItemVo> selectInfoByTypeAndUserId(@Param("typeName") String type, @Param("userId") Long userId);

    Integer selectItemSizeByItemIdAndType(Integer itemId, int type);

    List<GoodsVo> selectItemByItemIdAndType(Integer itemId);

    Integer selectHistorySizeByItemIdAndUserId(@Param("itemId") Integer itemId, @Param("userId")Long userId);

    void addHistory(Long userId, Integer itemId);

    LuckyGiftVo getLuckyGiftInfo();

    void saveNextRefreshDate(String s);
}
