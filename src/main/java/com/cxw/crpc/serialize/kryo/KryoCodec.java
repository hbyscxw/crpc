package com.cxw.crpc.serialize.kryo;

import com.cxw.crpc.common.RequestMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;

/**
 * @author chengxuwei
 * @date 2020-06-30 11:16
 * @description
 */
public class KryoCodec extends ByteToMessageCodec<Object> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        KryoSerializer.serialize(msg, out);
        ctx.flush();
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        Object obj = KryoSerializer.deserialize(in);
        out.add(obj);
    }
}