package com.dkm.goldMine.service;

import com.dkm.goldMine.bean.vo.*;

public interface IMineService {
    GoldMineVo getFamilyGoldMine(Long familyIdInt);
    void createNewMineByFamilyId(Long familyId);
    MineItemFightVo getGoldMineItemInfo(Long familyId, Long goldItemId);

    FightVo fight(Long id, Long familyId, Long goldItemId);

    FightKillVo fightKill(Long familyId, Long goldItemId, Long userId);
}