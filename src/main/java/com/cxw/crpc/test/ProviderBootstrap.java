package com.cxw.crpc.test;

import com.cxw.crpc.provider.Provider;


/**
 * @author chengxuwei
 * @date 2020-05-25 09:42
 * @description
 */
public class ProviderBootstrap {
    public static void main(String[] args) {
        Provider provider = new Provider();
        provider.initBind(8000);
    }
}