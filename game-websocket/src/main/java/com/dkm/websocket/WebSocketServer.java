package com.dkm.websocket;

import com.dkm.websocket.handle.*;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author qf
 * @date 2020/3/11
 * @vesion 1.0
 **/
@Slf4j
@Component
public class WebSocketServer implements CommandLineRunner {


   /**
    * 创建主从线程池
    */
   private static final EventLoopGroup MASTER_GROUP = new NioEventLoopGroup();
   private static final EventLoopGroup SLAVE_GROUP = new NioEventLoopGroup();

   private Channel channel;

   private final Integer port = 8820;

   @Autowired
   private TextWebSocketHandle textWebSocketHandle;

   @Autowired
   private ConnectWebSocketHandle connectWebSocketHandle;

   @Autowired
   private HeartWebSocketHandle heartWebSocketHandle;

   @Autowired
   private OneChatWebSocketHandle oneChatWebSocketHandle;


   @Autowired
   private ManyChatWebSocketHandle manyChatWebSocketHandle;

   @Autowired
   private FamilyWebSocketHandle familyWebSocketHandle;

   @Autowired
   private FileWebSocketHandle fileWebSocketHandle;

   private ChannelFuture init() {

      ServerBootstrap bootstrap = new ServerBootstrap();

      bootstrap.group(MASTER_GROUP,SLAVE_GROUP)
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer() {
               @Override
               protected void initChannel(Channel channel) throws Exception {
                  ChannelPipeline pipeline = channel.pipeline();

                  pipeline.addLast(new HttpServerCodec());
                  pipeline.addLast(new HttpObjectAggregator(512 * 1024));
                  pipeline.addLast(new WebSocketServerProtocolHandler("/game",null,true,65536 * 5));
                  pipeline.addLast(new ReadTimeoutHandler(999999999, TimeUnit.SECONDS));
                  pipeline.addLast(new WriteTimeoutHandler(3,TimeUnit.SECONDS));
                  pipeline.addLast(new StringDecoder());
                  // 二进制文件加密传输
                  pipeline.addLast(new ObjectEncoder());

                  //文本帧  传输协议
                  pipeline.addLast(textWebSocketHandle);
                  pipeline.addLast(connectWebSocketHandle);
                  pipeline.addLast(heartWebSocketHandle);
                  pipeline.addLast(oneChatWebSocketHandle);
                  pipeline.addLast(manyChatWebSocketHandle);
                  pipeline.addLast(familyWebSocketHandle);
                  //文件
                  pipeline.addLast(fileWebSocketHandle);
               }
            });
      ChannelFuture future = bootstrap.bind(port);

      future.addListener(new ChannelFutureListener() {
         @Override
         public void operationComplete(ChannelFuture channelFuture) throws Exception {

            if (channelFuture.isSuccess()) {
               //开启连接
               log.info("WebSocket服务已经启动，端口为："+ port);
               channel = future.channel();
            } else {
               //关闭连接
               destory ();
               Throwable e = channelFuture.cause();
               e.printStackTrace();
            }
         }
      });
      return future;
   }
   /**
    * 销毁线程
    * 关闭连接
    */
   public void destory () {
      if (channel != null && channel.isActive()) {
         channel.close();
      }
      MASTER_GROUP.shutdownGracefully();
      SLAVE_GROUP.shutdownGracefully();
   }
   @Override
   public void run(String... args) throws Exception {
      ChannelFuture channelFuture = init();
      Runtime.getRuntime().addShutdownHook(new Thread(){
         @Override
         public void run() {
            destory();
         }
      });
      //同步堵塞
      channelFuture.channel().closeFuture().syncUninterruptibly();
   }
}
