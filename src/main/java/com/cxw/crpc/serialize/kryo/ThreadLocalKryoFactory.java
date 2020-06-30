package com.cxw.crpc.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import io.netty.util.concurrent.FastThreadLocal;

/**
 * @author chengxuwei
 * @date 2020-06-30 11:13
 * @description
 */
public class ThreadLocalKryoFactory extends KryoFactory {

    private final FastThreadLocal<Kryo> holder  = new FastThreadLocal<Kryo>() {
        @Override
        protected Kryo initialValue() {
            return createKryo();
        }
    };

    public Kryo getKryo() {
        return holder.get();
    }
}
