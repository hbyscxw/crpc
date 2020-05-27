package com.cxw.crpc.consumer;

import com.cxw.crpc.annotation.ProviderService;
import com.cxw.crpc.common.RequestMsg;
import com.cxw.crpc.common.ResponseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author chengxuwei
 * @date 2020-05-25 10:01
 * @description
 */
public class ConsumerHandler extends ChannelInboundHandlerAdapter implements Callable<ResponseMsg> {

    private ChannelHandlerContext ctx;
    private RequestMsg requestMsg;
    private ResponseMsg responseMsg;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active!");
        this.ctx = ctx;
    }

    @Override
    public synchronized void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        System.out.println("read"+obj.toString());
        ResponseMsg responseMsg = (ResponseMsg) obj;
        this.responseMsg = responseMsg;
        notify();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public synchronized ResponseMsg call() throws Exception {
        System.out.println("call"+requestMsg.toString());
        ctx.writeAndFlush(requestMsg);
        wait();
        return responseMsg;
    }

    public void sentRequestMsg(RequestMsg requestMsg){
        System.out.println("send msg");
        this.requestMsg = requestMsg;
    }
}
