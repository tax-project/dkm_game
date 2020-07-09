package com.dkm.knapsack.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dkm.constanct.CodeType;
import com.dkm.exception.ApplicationException;
import com.dkm.jwt.contain.LocalUser;
import com.dkm.knapsack.dao.TbBoxMapper;
import com.dkm.knapsack.dao.TbEquipmentKnapsackMapper;
import com.dkm.knapsack.dao.TbEquipmentMapper;
import com.dkm.knapsack.domain.TbBox;
import com.dkm.knapsack.domain.TbEquipment;
import com.dkm.knapsack.domain.TbKnapsack;
import com.dkm.knapsack.domain.vo.TbEquipmentKnapsackVo;
import com.dkm.knapsack.domain.vo.TbEquipmentVo;
import com.dkm.knapsack.service.ITbBoxService;
import com.dkm.knapsack.service.ITbEquipmentKnapsackService;
import com.dkm.knapsack.service.ITbEquipmentService;
import com.dkm.knapsack.service.ITbKnapsackService;
import com.dkm.utils.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * <p>
 * 宝箱表 服务实现类
 * </p>
 *
 * @author zy
 * @since 2020-05-14
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TbBoxServiceImpl  implements ITbBoxService {
    @Autowired
    private IdGenerator idGenerator;
    @Autowired
    TbBoxMapper tbBoxMapper;
    @Autowired
    TbEquipmentKnapsackMapper tbEquipmentKnapsackMapper;
    @Autowired
    ITbKnapsackService tbKnapsackService;
    @Autowired
    private LocalUser localUser;
    @Autowired
    ITbEquipmentService tbEquipmentService;
    @Autowired
    ITbEquipmentKnapsackService tbEquipmentKnapsackService;
    @Autowired
    TbEquipmentMapper tbEquipmentMapper;
    @Override
    public void addTbBox(TbBox tbBox) {
        tbBox.setBoxId(idGenerator.getNumberId());
        int rows=tbBoxMapper.insert(tbBox);
        if(rows <= 0){
            //如果失败将回滚
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "增加失败");
        }
    }

    @Override
    public TbEquipmentVo selectByBoxId(String boxId) {
        if(StringUtils.isEmpty(boxId)){
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        return tbBoxMapper.selectByBoxId(Long.valueOf(boxId));
    }

    @Override
    public List<TbBox> selectAll() {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.orderByAsc("box_type");
        return tbBoxMapper.selectList(queryWrapper);
    }

    @Override
    public List<Map> selectByBoxIdTwo(String boxId) {
        if( boxId==null &&"".equals(boxId) ){
            //如果失败将回滚
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        String[] sList = boxId.split(",");
        List<TbEquipmentVo> list=new ArrayList<>();
        for (String aLong : sList) {
            TbEquipmentVo tbEquipmentVo=tbBoxMapper.selectByBoxId(Long.valueOf(aLong));
            list.add(tbEquipmentVo);
        }
        List<Map> listMap=new ArrayList<>();

            for (int i=0;i<list.size();i++) {
                TbEquipmentVo tbEquipmentVo=list.get(i);
                QueryWrapper<TbEquipment> queryWrapper=new QueryWrapper();
                queryWrapper.eq("equipment_id",tbEquipmentVo.getEquipmentId());
                List<TbEquipment> listTwo=tbEquipmentMapper.selectList(queryWrapper);
                for (int j=0;j<listTwo.size();j++) {
                    TbEquipment tbEquipment=listTwo.get(j);
                    //得到当前用户的id然后查询出背包的主键 localUser.getUser().getId()
                    TbKnapsack tbKnapsack=new TbKnapsack();

                    tbKnapsack.setUserId(localUser.getUser().getId());
                    List<TbKnapsack> list1=tbKnapsackService.findById(tbKnapsack);

                    for (int k=0;k<list1.size();k++) {
                        TbKnapsack knapsack=list1.get(k);
                        //传入当前用户背包的外键和装备编号
                        TbEquipmentKnapsackVo tbEquipmentKnapsack=new TbEquipmentKnapsackVo();
                        tbEquipmentKnapsack.setExp1(tbEquipment.getExp1());
                        tbEquipmentKnapsack.setKnapsackId(knapsack.getKnapsackId());
                        int count=tbEquipmentKnapsackMapper.selectCountMy(tbEquipmentKnapsack);
                        if(count>0){
                            //查询为装备上的装备数据
                            TbEquipmentVo list3=tbEquipmentService.selectByEquipmentIdTwo(tbEquipment.getExp1());
                            TbEquipmentKnapsackVo tbEquipmentKnapsackVo=new TbEquipmentKnapsackVo();
                            tbEquipmentKnapsackVo.setExp1(tbEquipment.getExp1());
                            tbEquipmentKnapsackVo.setKnapsackId(knapsack.getKnapsackId());
                            //查询已经装备上了的装备数据
                            List<TbEquipmentKnapsackVo> list2=tbEquipmentKnapsackService.selectAll(tbEquipmentKnapsackVo);
                            Map<String,Object> map=new HashMap<>();
                            map.put("code",3);
                            map.put("msg","此装备已经装备上了");
                            map.put("dataOne",list3);
                            map.put("dataTwo",list2);
                            listMap.add(map);
                        }else{
                            Map<String,Object> map=new HashMap<>();
                            //查询为装备上的装备数据
                            List<TbEquipmentVo> list3=tbEquipmentService.selectByEquipmentId(tbEquipmentVo.getEquipmentId());
                            map.put("code",2);
                            map.put("dataThree",list3);
                            map.put("msg","此装备没有装备上过");
                            listMap.add(map);
                        }
                    }
                }

            }

        return listMap;
    }

    @Override
    public List<TbEquipmentVo> selectByBoxIdThree(String boxId) {
        if( boxId==null &&"".equals(boxId) ){
            //如果失败将回滚
            throw new ApplicationException(CodeType.PARAMETER_ERROR, "参数不能为空");
        }
        String[] sList = boxId.split(",");
        List<TbEquipmentVo> list=new ArrayList<>();
        for (String aLong : sList) {
            TbEquipmentVo tbEquipmentVo=tbBoxMapper.selectByBoxId(Long.valueOf(aLong));
            list.add(tbEquipmentVo);
        }
        if(list.size()!=0&&list!=null){
            return list;
        }else{
            return null;
        }
    }
}
