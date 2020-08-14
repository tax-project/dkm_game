package com.dkm.admin.entities;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("tb_admin_users")
public class AdminUserEntity {
   @TableId
   private  int id;
   private long userId;

}
