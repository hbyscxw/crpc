package com.cxw.crpc.common;

import java.io.Serializable;
import java.util.Arrays;

/**
 * @author chengxuwei
 * @date 2020-05-25 09:55
 * @description 请求的msg
 */
public class RequestMsg implements Serializable {

    private static final long serialVersionUID = 601310500306672522L;

    private String className;
    private String methodName;
    private Class<?>[] params;
    private Object[] paramValues;

    public RequestMsg(){}

    public RequestMsg(String className, String methodName, Class<?>[] params, Object[] paramValues) {
        this.className = className;
        this.methodName = methodName;
        this.params = params;
        this.paramValues = paramValues;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Class<?>[] getParams() {
        return params;
    }

    public void setParams(Class<?>[] params) {
        this.params = params;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }

    @Override
    public String toString() {
        return "RequestMsg{" +
                "className='" + className + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.toString(params) +
                ", paramValues=" + Arrays.toString(paramValues) +
                '}';
    }
}