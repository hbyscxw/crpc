package com.cxw.crpc.common;

import java.io.Serializable;

/**
 * @author chengxuwei
 * @date 2020-05-25 09:55
 * @description
 */
public class ResponseMsg implements Serializable {

    private static final long serialVersionUID = 8936049508396193060L;

    private int code;
    private String msg;
    private Object data;

    public ResponseMsg() {
    }

    public ResponseMsg(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static ResponseMsg success(){
        return new ResponseMsg(0,"success",null);
    }
    public static ResponseMsg success(String msg){
        return new ResponseMsg(0,msg,null);
    }
    public static ResponseMsg success(String msg,Object data){
        return new ResponseMsg(0,msg,data);
    }
    public static ResponseMsg error(){
        return new ResponseMsg(1,"error",null);
    }
    public static ResponseMsg error(String msg){
        return new ResponseMsg(1,msg,null);
    }
    public static ResponseMsg error(String msg,Object data){
        return new ResponseMsg(1,msg,data);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}