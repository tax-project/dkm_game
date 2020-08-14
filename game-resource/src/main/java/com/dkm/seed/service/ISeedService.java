package com.dkm.seed.service;

import com.dkm.data.Result;
import com.dkm.entity.bo.UserInfoQueryBo;
import com.dkm.land.entity.vo.Message;
import com.dkm.seed.entity.LandSeed;
import com.dkm.seed.entity.Seed;
import com.dkm.seed.entity.bo.SendCollectBO;
import com.dkm.seed.entity.bo.SendPlantBO;
import com.dkm.seed.entity.vo.*;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author MQ
 * @PROJECT_NAME: game_project
 * @DESCRIPTION:
 * @DATE: 2020/5/11 16:17
 */
public interface ISeedService {
    /**
     * 根据用户id得到种子信息
     */
    List<SeedPlantUnlock> queryUserIdSeed(Long userId);

    /**
     * 解锁植物
     * @return
     */
    Message unlockPlant(SeedVo seedVo);


    /**
     *  种植
     * @param sendPlantBO 种植参数
     */
    void queryAlreadyPlantSeed(SendPlantBO sendPlantBO);

    /**
     * 根据种子查询种子
     *
     */
    SeedDetailsVo querySeedById(Long seeId);

    /**
     * 根据用户id查询已解锁的种子
     * @return
     */
    List<SeedUnlockVo> queryAreUnlocked(Long userId);

    /**
     * 根据用户id查询所有用户信息
     * @return
     */
    Map<String,Object> queryUserAll();

    /**
     * 查询已经种植的种子
     */
    List<LandYesVo> queryAlreadyPlantSd();

    /**
     * 批量修改种子状态
     */
    int updateSeedStatus(List<Long> id);

    /**
     *  收取
     * @param sendCollectBO 种子收取参数
     */
    void collectSeed (SendCollectBO sendCollectBO);

    /**
     * 查看自己个人经验信息
     */
    Map<String,Object> personalExperience();


}
