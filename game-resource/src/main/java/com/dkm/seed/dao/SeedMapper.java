package com.dkm.seed.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dkm.seed.entity.LandSeed;
import com.dkm.seed.entity.Seed;
import com.dkm.seed.entity.SeedUnlock;
import com.dkm.seed.entity.vo.*;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author 刘梦祺
 * @PROJECT_NAME: dkm_game
 * @DESCRIPTION:
 * @USER: 刘梦祺
 * @DATE: 2020/5/11 16:20
 */
@Component
public interface SeedMapper extends BaseMapper<Seed> {
    /**
     * 根据用户id得到种子（是否解锁）
     */
    List<SeedPlantUnlock> queryUserIdSeed(Long userId);

    /**
     * 根据用户id修改金币和声望
     *
     */
    int uploadUnlockMoneyAndPrestige(@Param("unlockmoeny") Integer unlockmoeny,@Param("prestige")Integer prestige,@Param("userId") long userId);
    /**
     * 修改碎片的当前进度
     *
     */
    int uploadPresentUnlock(Integer seedid);

    /**
     * 种植植物
     *
     */
    int addPlant(List<LandSeed> list);

    /**
     * 查询已种植的植物
     *
     */
    List<LandYesVo> queryAlreadyPlantSd(long userId);



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
    int updateSeedPresentUnlock(@Param("userId") long userId,@Param("seedId")Integer seedId,@Param("seedPresentUnlock")Integer seedPresentUnlock,@Param("seedStatus")Integer seedStatus);

    /**
     *
     * @param list
     * @return
     */
    int insertSeedUnlock(List<SeedUnlock> list);

    /**
     * 修改用户信息
     */
    int updateUser(SeedPlantVo seedPlantVo);

    /**
     * 修改用户信息
     */
    int updateUsers(SeedPlantVo seedPlantVo);

    /**
     * 收取种子后 删除土地种子表中对应的数据
     */
    int deleteLandSeed(Long userId);

    /**
     * 根据用户id查询已解锁的种子
     * @param userId
     * @return
     */
    List<SeedUnlockVo> queryAreUnlocked(Long userId);

    SeedDetailsVo querySeedById(@Param("seedId") Long seedId ,@Param("userId")Long userId);

    List<CountIdVo> queryStatus(@Param("userId") Long userId,@Param("list")List<Integer> list);

    /**
     * 批量修改种植状态时间
     * @param list
     * @return
     */
    int updateSeedStatus(@Param("list") List<Long> list);

    /**
     *  批量修改状态和时间
     * @param time 时间
     * @param userId 用户Id
     * @param seedId 种子id
     * @return 返回结果
     */
    Integer updateTimeAndStatus (@Param("time") LocalDateTime time,
                                 @Param("userId") Long userId,
                                 @Param("seedId") Long seedId);




}

