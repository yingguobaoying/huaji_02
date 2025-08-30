package com.huaji.galgamebyhuaji.exceptions;


import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 错误描述信息,横坐标:等级errorLevel,纵坐标:错误代码errorNun
 * <table>
 *     <tr>
 *         <td></td>
 *     </tr>
 * </table>
 *
 * @author 滑稽/因果报应
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {
	/**
	 * 处理操作异常
	 */
	@ExceptionHandler(OperationException.class)
	public ReturnResult<Exception> handleOperationException (OperationException ex, HttpServletRequest request) {
		ReturnResult<Exception> r = ReturnResult.isFalse(ex.getMsg());
		return addSystemMsg(r, request);
	}
	
	@ExceptionHandler(Exception.class)
	public ReturnResult<Exception> handleException (Exception ex, HttpServletRequest request) {
		//操作错误时,获取错误信息返回给前端
		if ( ex instanceof OperationException )
			return handleOperationException((OperationException) ex, request);
		ex.printStackTrace();//todo 仅在开发时使用,上线时清除
		MyLogUtil.error(getClass(), ex);
		ReturnResult<Exception> error = ReturnResult.isError(ex.getMessage(), ex);
		return addSystemMsg(error, request);
	}
	
	public ReturnResult<Exception> addSystemMsg (ReturnResult<Exception> ex, HttpServletRequest request) {
		String attribute = (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
		if ( MyStringUtil.isNull(attribute) ) {
			ex.addMap(SystemConstant.SYSTEM_MSG, attribute);
		}
		return ex;
	}
}