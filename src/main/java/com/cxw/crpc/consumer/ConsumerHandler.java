package com.cxw.crpc.consumer;

import com.cxw.crpc.annotation.ProviderService;
import com.cxw.crpc.common.RequestMsg;
import com.cxw.crpc.common.ResponseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * @author chengxuwei
 * @date 2020-05-25 10:01
 * @description
 */
public class ConsumerHandler extends ChannelInboundHandlerAdapter implements Callable<ResponseMsg> {

    private ChannelHandlerContext ctx;
    private RequestMsg requestMsg;
    //private ResponseMsg responseMsg;
    private CompletableFuture<ResponseMsg> future;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("active!");
        this.ctx = ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object obj)
            throws Exception {
        try {
            System.out.println("read"+obj.toString());
            ResponseMsg responseMsg = (ResponseMsg) obj;
            future.complete(new ResponseMsg(responseMsg.getCode(),responseMsg.getMsg(),responseMsg.getData()));
            //notify(); 必须要 synchronized
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            ReferenceCountUtil.release(obj);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public ResponseMsg call() throws Exception {
        future = new CompletableFuture<>();
        System.out.println("call"+requestMsg.toString());
        ctx.writeAndFlush(requestMsg);
        //wait();  必须要 synchronized
        return future.get(5, TimeUnit.SECONDS);
    }

    public void sentRequestMsg(RequestMsg requestMsg){
        System.out.println("send msg");
        this.requestMsg = requestMsg;
    }

}
