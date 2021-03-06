package com.dkm.diggings.service;


import com.dkm.diggings.bean.vo.DiggingsStatusVO;
import com.dkm.diggings.bean.vo.DiggingsVo;
import com.dkm.diggings.bean.vo.MineDetailVo;

/**
 * @author OpenE
 */
public interface IDiggingsService {
    /**
     * 得到矿区的所有信息
     *
     * @param userId   用户 ID
     * @param familyId 家族 ID
     * @return 矿区的全部信息
     */
    DiggingsVo getAllInfo(Long userId, Long familyId);

    /**
     * 查询用户剩余的挖矿次数
     *
     * @param userId 用户ID
     * @return 次数
     */
    int getOccupationSize(Long userId);

    /**
     * 查看矿山的详情信息
     *
     * @param battleId 矿山ID
     * @param userId
     * @return 详情信息
     */
    MineDetailVo detail(long battleId, Long userId);

    DiggingsStatusVO getStatus(Long userId, Long familyId);
}
