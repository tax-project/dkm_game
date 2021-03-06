package com.dkm.config;


import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 家族接收信息的mq
 * @author qf
 * @date 2020/6/1
 * @vesion 1.0
 **/
@Component
public class RabbitMqConfig {

//   @Bean
//   public FanoutExchange getFamilyFanoutExchange () {
//      return new FanoutExchange("game_family_FanoutExchange");
//   }

   @Bean
   public Queue getFamilyWithQueue() {
      return new Queue("game_family_queue_",false);
   }

   /**
    *  传输家族的消息队列
    * @return
    */
   @Bean
   public Queue getFamilyQueue () {
      return new Queue("game_family_info_queue",false);
   }

}
