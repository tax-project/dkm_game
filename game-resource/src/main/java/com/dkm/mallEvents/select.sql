SELECT a.id                        id,
       b.size                      size,
       if(isnull(c.user_id), 0, 2) status,
       d.name                      name,
       d.url                       url,
       d.good_money * size         money
FROM tb_mall_reward AS a
         join tb_mall_reward_goods as b on a.id = b.mall_reward_id
         LEFT JOIN tb_mall_reward_history as c ON a.id = c.mall_reward_id AND user_id = '730467828188221440'
         JOIN tb_goods d on b.goods_id = d.id
         JOIN tb_mall_reward_info e on a.type = e.id
WHERE e.name = '充值返利';



SELECT goods_id id, size sizs
from tb_mall_reward_goods
where mall_reward_id = 1
;
select *
from tb_mall_lucky_gift;

select a.id                                                                                   id,
       b.gi_name                                                                              name,
       b.gi_url                                                                               url,
       (select option_value from tb_game_options WHERE option_key = 'GIFT_NEXT_REFRESH_DATE') nextRefreshDate
from tb_mall_lucky_gift a
         JOIN tb_gift b on a.gift_id = b.gi_id;


SELECT @dev := 26;
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('3', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('1', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('707431394650138541', '100', @dev);
SELECT @dev := @dev + 1;
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('3', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('1', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('707431394650138541', '100', @dev);
SELECT @dev := @dev + 1;
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('3', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('1', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('707431394650138541', '100', @dev);
SELECT @dev := @dev + 1;
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('3', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('1', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('707431394650138541', '100', @dev);
SELECT @dev := @dev + 1;
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('3', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('1', '100', @dev);
INSERT INTO `dkm_game`.`tb_mall_reward_goods` (`goods_id`, `size`, `mall_reward_id`)
VALUES ('707431394650138541', '100', @dev);