package com.cxw.crpc.provider;

import com.cxw.crpc.common.RequestMsg;
import com.cxw.crpc.common.ResponseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.ReferenceCountUtil;

import java.lang.reflect.Method;

/**
 * @author chengxuwei
 * @date 2020-05-25 10:01
 * @description
 */
public class ProviderHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object req)
            throws Exception {
        try {
            RequestMsg requestMsg = (RequestMsg)req;
            Object obj = Provider.getProviderServiceMap().get(requestMsg.getClassName());
            if(obj==null){
                channelHandlerContext.writeAndFlush(ResponseMsg.error("未找到该接口的服务类！"));
            }

            Method method = obj.getClass().getMethod(requestMsg.getMethodName(), requestMsg.getParams());
            Object res = method.invoke(obj,requestMsg.getParamValues());
            channelHandlerContext.writeAndFlush(ResponseMsg.success("success",res));
        } catch (Exception e) {
            e.printStackTrace();
            channelHandlerContext.writeAndFlush(ResponseMsg.error("执行失败！"));
        }finally {
            ReferenceCountUtil.release(req);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
