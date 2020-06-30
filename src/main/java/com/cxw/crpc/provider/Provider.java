package com.cxw.crpc.provider;

import com.cxw.crpc.annotation.ProviderService;
import com.cxw.crpc.serialize.kryo.KryoCodec;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.logging.LoggingHandler;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chengxuwei
 * @date 2020-05-25 10:00
 * @description
 */
public class Provider {

    private static List<Class<?>> providerServiceList = new CopyOnWriteArrayList<>();
    private static Map<String,Object> providerServiceMap = new ConcurrentHashMap<>();

    public Provider() {
        URL resource = this.getClass().getResource("/");
        File f = new File(resource.getPath());
        scanClass(f,f.getAbsolutePath());
        registerClass();
    }

    private void registerClass() {
        for (Class<?> clazz : providerServiceList) {
            if(!clazz.isInterface()){
                try {
                    Object o = clazz.newInstance();
                    providerServiceMap.put(clazz.getInterfaces()[0].getName(),o);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void scanClass(File f,String rootPath) {
        File[] files = f.listFiles();
        for (File childFile : files) {
            if (childFile.isDirectory()) {
                scanClass(childFile,rootPath);
            } else {
                if(childFile.getName().endsWith(".class")) {
                    String className = childFile.getName().replace(".class", "");
                    try {
                        String absolutePath = f.getAbsolutePath();
                        String packageName = absolutePath.replaceAll(rootPath,"");
                        if(packageName.startsWith("/")){
                            packageName = packageName.replaceFirst("/","");
                        }
                        packageName = packageName.replaceAll("/","\\.");
                        String fullClassName;
                        if(!"".equals(packageName)){
                            fullClassName =  packageName +"." + className;
                        }else{
                            fullClassName = className;
                        }
                        Class<?> clazz = Class.forName(fullClassName);
                        if (clazz.getAnnotation(ProviderService.class) != null) {
                            providerServiceList.add(clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void initBind(int port){
        //Group：群组，Loop：循环，Event：事件，这几个东西联在一起，相比大家也大概明白它的用途了。
        //Netty内部都是通过线程在处理各种数据，EventLoopGroup就是用来管理调度他们的，注册Channel，管理他们的生命周期。
        //NioEventLoopGroup是一个处理I/O操作的多线程事件循环
        //bossGroup作为boss,接收传入连接
        //因为bossGroup仅接收客户端连接，不做复杂的逻辑处理，为了尽可能减少资源的占用，取值越小越好
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        //workerGroup作为worker，处理boss接收的连接的流量和将接收的连接注册进入这个worker
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //ServerBootstrap负责建立服务端
            //你可以直接使用Channel去建立服务端，但是大多数情况下你无需做这种乏味的事情
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    //指定使用NioServerSocketChannel产生一个Channel用来接收连接
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                    .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                    .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    //ChannelInitializer用于配置一个新的Channel
                    //用于向你的Channel当中添加ChannelInboundHandler的实现
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
//                            // 进行长度字段解码，这里也会对数据进行粘包和拆包处理
//                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(10*1024, 0, 2, 0, 2));
//                            // LengthFieldPrepender是一个编码器，主要是在响应字节数据前面添加字节长度字段
//                            ch.pipeline().addLast(new LengthFieldPrepender(2));
//                            // object解码
//                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
//                            // object编码
//                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new KryoCodec());
                            //ChannelPipeline用于存放管理ChannelHandel
                            //ChannelHandler用于处理请求响应的业务逻辑相关代码
                            ch.pipeline().addLast(new ProviderHandler());
                        }
                    })
//                    //对Channel进行一些配置
//                    //注意以下是socket的标准参数
//                    //BACKLOG用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。如果未设置或所设置的值小于1，Java将使用默认值50。
//                    //Option是为了NioServerSocketChannel设置的，用来接收传入连接的
//                    .option(ChannelOption.SO_BACKLOG, 128)
//                    //是否启用心跳保活机制。在双方TCP套接字建立连接后（即都进入ESTABLISHED状态）并且在两个小时左右上层没有任何数据传输的情况下，这套机制才会被激活。
//                    //childOption是用来给父级ServerChannel之下的Channels设置参数的
//                    .childOption(ChannelOption.SO_KEEPALIVE, true)
            ;
            // Bind and start to accept incoming connections.
            ChannelFuture f = b.bind(port).sync();
            System.out.println("服务提供方已启动！");
            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            //sync()会同步等待连接操作结果，用户线程将在此wait()，直到连接操作完成之后，线程被notify(),用户代码继续执行
            //closeFuture()当Channel关闭时返回一个ChannelFuture,用于链路检测
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //资源优雅释放
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static Map<String,Object> getProviderServiceMap(){
        return providerServiceMap;
    }
}