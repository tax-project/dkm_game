package com.dkm.seed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.IBaseMapper.IBaseMapper;
import com.dkm.land.entity.Land;
import com.dkm.land.entity.vo.Message;
import com.dkm.seed.entity.LandSeed;
import com.dkm.seed.entity.Seed;
import com.dkm.seed.entity.vo.LandSeedVo;
import com.dkm.seed.entity.vo.SeedUnlock;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: dkm_game
 * @DESCRIPTION:
 * @USER: 刘梦祺
 * @DATE: 2020/5/11 16:20
 */
@Service
public interface SeedMapper extends BaseMapper<Seed> {
    /**
     * 根据用户id得到种子（是否解锁）
     */
    List<Seed> queryUserIdSeed(long userId);

    /**
     * 根据用户id修改金币和声望
     *
     */
    int uploadUnlockMoneyAndPrestige(Integer unlockmoeny,Integer prestige,long userId);
    /**
     * 修改碎片的当前进度
     *
     */
    int uploadPresentUnlock(Integer seedid);

    /**
     * 种植植物
     *
     */
    int addPlant(LandSeed landSeed);

    /**
     * 查询已种植的植物
     *
     */
    List<LandSeedVo> queryAlreadyPlantSd(long userId);



    /**
     * 操作表：种子解锁表
     * 根据用户id查询种子解锁表
     *
     */
    List<SeedUnlock> queryIsById(long userId);

    /**
     * 修改当前解锁进度
     * @param userId 用户id
     * @param seedId 种子id
     * @return
     */
    int updateSeedPresentUnlock(long userId,Integer seedId,Integer seedPresentUnlock);

    /**
     *
     * @param list
     * @return
     */
    int insertSeedUnlock(List<SeedUnlock> list);
}
