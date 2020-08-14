package com.dkm.utils.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
public final class ResultVo {
   private boolean status;
   @NotNull
   private Object data;


}
