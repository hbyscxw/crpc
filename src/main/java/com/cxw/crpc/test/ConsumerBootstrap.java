package com.cxw.crpc.test;


import com.cxw.crpc.consumer.Consumer;
import com.cxw.crpc.test.api.Calc;

/**
 * @author chengxuwei
 * @date 2020-05-25 09:56
 * @description
 */
public class ConsumerBootstrap {
    public static void main(String[] args) {
        Consumer consumer = new Consumer();
        consumer.initConnect("localhost",8000);
        for(;;) {
            try {
                Calc calc = (Calc) consumer.getBean(Calc.class);
                System.out.println(calc.add(1, 2));
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }
}