package com.dkm.familyMine.service;


public interface IFamilyMineService {

    Long getMineBattleFieldId(long id);

    Long createBattleFieldByFamilyId(long familyId);
}