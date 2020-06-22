package com.dkm.apparel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dkm.apparel.dao.ApparelMapper;
import com.dkm.apparel.dao.ApparelMarketMapper;
import com.dkm.apparel.dao.ApparelOrderMapper;
import com.dkm.apparel.dao.ApparelUserDao;
import com.dkm.apparel.entity.ApparelMarketEntity;
import com.dkm.apparel.entity.vo.ApparelMarketDetailVo;
import com.dkm.apparel.entity.vo.ApparelOrderVo;
import com.dkm.apparel.entity.vo.ApparelPutVo;
import com.dkm.apparel.entity.ApparelUserEntity;
import com.dkm.apparel.service.IApparelMarketService;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.utils.IdGenerator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @program: game_project
 * @description:
 * @author: zhd
 * @create: 2020-06-19 15:28
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class ApparelMarketImpl implements IApparelMarketService {

    @Resource
    private ApparelMapper apparelMapper;

    @Resource
    private ApparelOrderMapper apparelOrderMapper;

    @Resource
    private ApparelUserDao apparelUserDao;

    @Resource
    private ApparelMarketMapper apparelMarketMapper;

    @Resource
    private IdGenerator idGenerator;
    @Override
    public void putOnSell(Long userId, ApparelPutVo apparelPutVo) {
        //获取用户服饰状态
        ApparelUserEntity apparelUserEntity = apparelUserDao.selectOne(new LambdaQueryWrapper<ApparelUserEntity>().eq(ApparelUserEntity::getUserId, userId).eq(ApparelUserEntity::getApparelDetailId, apparelPutVo.getApparelId()));
        if(apparelUserEntity==null) throw new ApplicationException(CodeType.SERVICE_ERROR,"你未拥有该服饰");
        if(apparelUserEntity.getIsEquip()==2) throw new ApplicationException(CodeType.SERVICE_ERROR,"该服饰已上架");
        if(apparelUserEntity.getIsEquip()==1) throw new ApplicationException(CodeType.SERVICE_ERROR,"改服饰正在装备，不能上架");
        //更新记录
        ApparelMarketEntity marketEntity = new ApparelMarketEntity();
        marketEntity.setApparelMarketId(idGenerator.getNumberId());
        marketEntity.setUserId(userId);
        marketEntity.setApparelDetailId(apparelUserEntity.getApparelDetailId());
        marketEntity.setMaturityTime(LocalDateTime.now().minusHours(-(24*3)));
        marketEntity.setGold(apparelPutVo.getGold());
        int insert = apparelMarketMapper.insert(marketEntity);
        apparelUserEntity.setIsEquip(2);
        int i = apparelUserDao.updateById(apparelUserEntity);
        if(i!=1||insert!=1)throw new ApplicationException(CodeType.SERVICE_ERROR,"上架失败");
    }

    @Override
    public void downApparel(Long apparelMarketId) {
        ApparelMarketEntity marketEntity = apparelMarketMapper.selectOne(new LambdaQueryWrapper<ApparelMarketEntity>().eq(ApparelMarketEntity::getApparelMarketId, apparelMarketId));
        if(marketEntity==null)throw new ApplicationException(CodeType.SERVICE_ERROR,"该商品未上架");
        int i = apparelMarketMapper.deleteById(apparelMarketId);
        ApparelUserEntity apparelUserEntity = apparelUserDao.selectOne(new LambdaQueryWrapper<ApparelUserEntity>().eq(ApparelUserEntity::getUserId, marketEntity.getUserId()).eq(ApparelUserEntity::getApparelDetailId, marketEntity.getApparelDetailId()));
        apparelUserEntity.setIsEquip(0);
        int i1 = apparelUserDao.updateById(apparelUserEntity);
        if(i!=1||i1!=1)throw new ApplicationException(CodeType.SERVICE_ERROR,"下架失败");
    }

    @Override
    public List<ApparelMarketDetailVo> apparelMarket(Long userId, Integer type) {
        return apparelUserDao.getApparelMarket(userId,type);
    }

    @Override
    public List<ApparelMarketDetailVo> puttingApparel(Long userId) {
        return apparelMarketMapper.getPuttingApparel(userId);
    }

    @Override
    public List<ApparelOrderVo> getApparelOrders(Long userId) {
        return apparelOrderMapper.getApparelOrders(userId);
    }
}
