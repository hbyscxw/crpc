package com.cxw.crpc.consumer;

import com.cxw.crpc.common.RequestMsg;
import com.cxw.crpc.common.ResponseMsg;
import com.cxw.crpc.thread.ThreadUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author chengxuwei
 * @date 2020-05-25 10:00
 * @description
 */
public class Consumer {

    private ConsumerHandler handler;

    public void initConnect(String host,int port){
        handler = new ConsumerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 3000)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            // 进行长度字段解码，这里也会对数据进行粘包和拆包处理
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(10*1024, 0, 2, 0, 2));
                            // LengthFieldPrepender是一个编码器，主要是在响应字节数据前面添加字节长度字段
                            ch.pipeline().addLast(new LengthFieldPrepender(2));
                            // object解码
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
                            // object编码
                            ch.pipeline().addLast(new ObjectEncoder());
                            //ChannelPipeline用于存放管理ChannelHandel
                            //ChannelHandler用于处理请求响应的业务逻辑相关代码
                            ch.pipeline().addLast(handler);
                        }
                    })
                     ;
            // 客户端开启
            b.connect(host,port).sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getBean(Class<?> clazz){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if(Object.class.equals(method.getDeclaringClass())){
                    return method.invoke(this,args);
                }
                return rpcInvoke(clazz,method,args);
            }
        });
    }
    private Object rpcInvoke(Class<?> clazz, Method method, Object[] args) {
        RequestMsg msg = new RequestMsg(clazz.getName(),method.getName(),method.getParameterTypes(),args);
        try {
            ResponseMsg res = rpc(msg);
            if(res!=null){
                return res.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    private ResponseMsg rpc(RequestMsg msg) throws Exception {
        handler.sentRequestMsg(msg);
        Future<ResponseMsg> f = ThreadUtils.getStandardExecutorInstance().submit(handler);
        ResponseMsg res =  f.get(5, TimeUnit.SECONDS);
        if(res == null){
            res = ResponseMsg.error("rpc time out!");
        }
        return res;
    }
}