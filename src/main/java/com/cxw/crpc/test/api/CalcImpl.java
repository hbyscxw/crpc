package com.cxw.crpc.test.api;

import com.cxw.crpc.annotation.ProviderService;

/**
 * @author chengxuwei
 * @date 2020-05-25 19:32
 * @description xx
 */
@ProviderService
public class CalcImpl implements Calc{
    @Override
    public int add(int a, int b){
        return a+b;
    }
}