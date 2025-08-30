package com.huaji.galgamebyhuaji.exceptions;

import java.util.List;

/**
 * @author 滑稽/因果报应
 *
 */
public class BestRuntimeException extends RuntimeException {
    /**
     * 错误代码
     */
    protected Integer errorNum;
    /**
     * 错误类别(继承此类的子类应重新设置)
     * 比如
     * protected Integer errorType = 1;表示用户异常
     */
    protected Integer errorType = 0;

    public Integer getErrorType() {
        return errorType;
    }

    public void setErrorType(Integer errorType) {
        this.errorType = errorType;
    }

    public BestRuntimeException() {
        errorNum = 0;
    }

    public BestRuntimeException(Integer errorNum) {
        this.errorNum = errorNum;
    }

    public BestRuntimeException(String msg, Integer errorNum) {
        super(msg);
        this.errorNum = errorNum;
    }

    public BestRuntimeException(String msg) {
        super(msg);
        this.errorNum = 0;
    }

    public Integer getErrorNum() {
        return errorNum;
    }

    public void setErrorNum(Integer errorNum) {
        this.errorNum = errorNum;
    }

    // 静态方法，用于构建异常的详细信息
    public <T> String buildErrorMessage(String message, List<T> objList) {
        StringBuilder sb = new StringBuilder(message);
        sb.append(": 出现异常的对象列表如下\n");
        for (T r : objList) {
            sb.append(r.toString())
                    .append("\n");
        }
        return sb.toString();
    }

}