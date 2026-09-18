package com.huaji.galgamebyhuaji.model;


import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.BestRuntimeException;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 这个是打包的统一返回结果集
 *
 * @param <T> 返回的类型
 *
 * @author 滑稽/因果报应
 */
@Setter
@Getter
public class ReturnResult<T> {
    /**
     * 单个的返回值
     */
    private T returnResult;
    /**
     * 返回结果(仅包含当前页)
     */
    private List<T> resultList;
    /**
     * 大部分时候代表本条记录中包含的数据条数,偶尔代表总共记录数量,偶尔代表时会特别说明
     */
    private int all;
    /**
     * 拓展字段
     */
    private Map<String, Object> map;
    
    public static ReturnResult<Exception> isError(Exception ex) {
        ReturnResult<Exception> error = isError(ex.getMessage());
        if (ex instanceof BestException e) {
            error.setErrorNum(e.getErrorType() * 100 + e.getErrorNum());
        } else if (ex instanceof BestRuntimeException e) {
            error.setErrorNum(e.getErrorType() * 100 + e.getErrorNum());
        }
        return error;
    }
    
    public static <T> ReturnResult<T> isTrue(String mxg) {
        return ReturnResult.isTrue(mxg, null);
    }
    
    
    public void addMap(String key, Object value) {
        if (map == null) map = new HashMap<>();
        map.put(key, value);
    }
    
    /**
     * 操作结果
     */
    private boolean operationResult;
    /**
     * 返回信息
     */
    private String msg;
    /**
     * 操作是否出现错误/异常
     */
    private boolean hasError;
    
    /**
     * 返回时间
     */
    private Date retrunDate;
    
    /**
     * 错误代码
     * -1 代表没有错误,其他数字:比如1002表示错误类别1,错误类别号为2
     */
    private int errorNum;
    
    
    public ReturnResult() {
        errorNum = -1;
        hasError = false;
    }
    
    /**
     * 返回错误
     *
     * @param errorMxg 错误信息
     *
     * @return 打包好的错误信息
     */
    public ReturnResult<T> operationError(String errorMxg) {
        this.operationFalse(errorMxg);
        setHasError(true);
        return this;
    }
    
    
    /**
     * 返回错误
     *
     * @param errorMxg 错误信息
     * @param e        错误
     * @param errorNum 错误代码
     *                 错误代码:描述
     *                 <li>null/0/-1:未设置</li>
     *                 <li>1:未知错误</li>
     *
     * @return 打包好的错误信息
     */
    public ReturnResult<T> operationError(String errorMxg, T e, int errorNum) {
        this.operationFalse(errorMxg);//因为出现错误时可能是操作失败,所以先设置为操作失败再设置为出错
        setReturnResult(e);
        setErrorNum(errorNum);
        setHasError(true);
        return this;
    }
    
    /**
     * 自动打包返回操作失败信息(打包的操作结果为false)
     *
     * @param mxg 反馈信息
     */
    public ReturnResult<T> operationFalse(String mxg) {
        setMsg(mxg);
        setOperationResult(false);
        setResultList(null);
        setReturnResult(null);
        setAll(0);
        setHasError(false);
        retrunDate = new Date();
        return this;
    }
    
    /**
     * 自动打包返回结果(仅一条信息时使用,打包的操作结果为true)
     *
     * @param mxg 反馈信息
     * @param val 结果
     */
    public ReturnResult<T> operationTrue(String mxg, T val) {
        setMsg(mxg);
        setOperationResult(true);
        setResultList(null);
        setReturnResult(val);
        setHasError(false);
        if (val == null) {setAll(0);} else {setAll(1);}
        retrunDate = new Date();
        return this;
    }
    
    
    /**
     * 自动打包返回结果(多条信息时使用,打包的操作结果为true)
     *
     * @param mxg 反馈信息
     * @param val 结果
     * @param all 总共记录条数为-1时将自动设置
     */
    public ReturnResult<T> operationTrue(String mxg, List<T> val, int all) {
        setMsg(mxg);
        setOperationResult(true);
        setResultList(val);
        if (all <= 0) {
            if (val == null) {
                setAll(0);
            } else {
                setAll(val.size());
            }
        } else {
            setAll(all);
        }
        retrunDate = new Date();
        return this;
    }
    
    @Override
    public String toString() {
        return "ReturnResult{" +
               "return_result=" + returnResult +
               ", resultList=" + resultList +
               ", all=" + all +
               ", operationResult=" + operationResult +
               ", Mxg='" + msg + '\'' +
               ", hasError=" + hasError +
               ", date=" + retrunDate +
               ", errorNum=" + errorNum +
               '}';
    }
    
    /**
     * 自动打包返回结果(多条信息时使用,打包的操作结果为true)
     *
     * @param mxg 反馈信息
     * @param val 结果
     */
    public static <T> ReturnResult<T> isTrue(String mxg, T val) {
        return new ReturnResult<T>().operationTrue(mxg, val);
    }
    
    /**
     * 自动打包返回结果(多条信息时使用,打包的操作结果为true)
     *
     * @param mxg 反馈信息
     * @param val 结果
     * @param all 总共记录条数为-1或者null时将自动设置
     */
    public static <T> ReturnResult<T> isTrue(String mxg, List<T> val, Integer all) {
        return new ReturnResult<T>().operationTrue(mxg, val, all == null ? -1 : all);
    }
    
    public static <T> ReturnResult<T> isTrue(String mxg, List<T> val) {
        return new ReturnResult<T>().operationTrue(mxg, val, 0);
    }
    
    /**
     * 自动打包返回错误结果
     *
     * @param mxg 反馈信息
     */
    public static <T> ReturnResult<T> isError(String mxg) {
        return new ReturnResult<T>().operationError(mxg);
    }
    
    public static <T> ReturnResult<T> isFalse(String mxg) {
        return new ReturnResult<T>().operationFalse(mxg);
    }
    
}
