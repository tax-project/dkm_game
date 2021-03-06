package com.dkm.mounts.service.impl;

import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.mounts.dao.MountsMapper;
import com.dkm.mounts.entity.MountsDetailEntity;
import com.dkm.mounts.entity.UserCenterMountsVo;
import com.dkm.mounts.entity.dto.MountsDetailDto;
import com.dkm.mounts.entity.dto.UserInfoDto;
import com.dkm.mounts.service.MountService;
import com.dkm.utils.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhd
 * @date 2020/5/11 17:26
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class MountServiceImpl implements MountService {

    @Resource
    private MountsMapper mountsMapper;

    @Resource
    private IdGenerator idGenerator;

    @Override
    public List<MountsDetailDto> getAll(Long userId) {
        return mountsMapper.getAll(userId);
    }

    @Override
    public List<MountsDetailDto> haveMounts(Long userId) {
        return mountsMapper.haveMounts(userId);
    }

    @Override
    public void equipMount(Long id,Long userId) {
        //取消正在装备的
        mountsMapper.cancelEquip(userId);
        //更新要装备 的
        mountsMapper.updateEquip(id);
    }

    @Override
    public void buyMount(Long mountId, Long userId, Integer gold, Integer diamond) {
        UserInfoDto userInfo = mountsMapper.getUserInfo(userId);
        if(userInfo.getUserInfoGold()<gold||userInfo.getUserInfoDiamonds()<diamond){
            throw new ApplicationException(CodeType.SERVICE_ERROR,"金币或钻石不足！");
        }
        //更新用户金币等
        Integer updateUser = mountsMapper.updateUser(gold,diamond,userId);
        //添加用户座驾记录
        Integer updateMount = mountsMapper.insetOne(idGenerator.getNumberId(), mountId, userId, 0);
        if(updateUser<1||updateMount<1){
            throw new ApplicationException(CodeType.SERVICE_ERROR);
        }
    }

    @Override
    public UserInfoDto getUserInfo(Long userId) {
        return mountsMapper.getUserInfo(userId);
    }

    @Override
    public UserCenterMountsVo getUserCenterMounts(Long userId) {
        UserCenterMountsVo userCenterMountsVo = new UserCenterMountsVo();
        //用户座驾数量
        userCenterMountsVo.setMountsNumber(mountsMapper.getMountNumber(userId));
        //用户当前座驾图片
        userCenterMountsVo.setMountsUrl(mountsMapper.getMountImg(userId));
        return userCenterMountsVo;
    }
}
