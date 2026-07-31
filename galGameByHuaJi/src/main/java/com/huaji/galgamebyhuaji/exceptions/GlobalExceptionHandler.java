package com.huaji.galgamebyhuaji.exceptions;


import com.huaji.galgamebyhuaji.constant.SystemConstant;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
@Slf4j
public class GlobalExceptionHandler {
	/**
	 * 处理操作异常
	 */
	@ExceptionHandler(OperationException.class)
	public ReturnResult<Exception> handleOperationException (OperationException ex, HttpServletRequest request) {
		ReturnResult<Exception> r = ReturnResult.isFalse(ex.getMsg());
		return addSystemMsg(r, request);
	}
	
	@ExceptionHandler(value = {
			AccessDeniedException.class,
			AuthenticationException.class,
			InsufficientAuthenticationException.class
	})
	public void handleSecurityException (Exception ex) throws Exception {
		// 不处理，重新抛出，让Spring Security的ExceptionTranslationFilter处理
		throw ex;
	}
	
	@ExceptionHandler(NoResourceFoundException.class)
	public ReturnResult<?> handleResourceNotFound (NoResourceFoundException ex, HttpServletRequest request, HttpServletResponse response) {
		// 只返回路径信息，避免序列化复杂对象
		response.setStatus(HttpStatus.NOT_FOUND.value());
		return ReturnResult.isFalse("您请求的资源: {" + ex.getResourcePath() + "}不存在捏~");
	}
	
	@ExceptionHandler(Exception.class)
	public ReturnResult<Exception> handleException (Exception ex, HttpServletRequest request) throws Exception {
		//操作错误时,获取错误信息返回给前端
		if ( ex instanceof OperationException )
			return handleOperationException((OperationException) ex, request);
		if ( ex instanceof AccessDeniedException
				|| ex instanceof AuthenticationException
		) handleSecurityException(ex);
		log.error("错误{}", ex.getMessage());
		ReturnResult<Exception> error = ReturnResult.isError(ex.getMessage());
		return addSystemMsg(error, request);
	}
	
	public ReturnResult<Exception> addSystemMsg (ReturnResult<Exception> ex, HttpServletRequest request) {
		String attribute = (String) request.getAttribute(SystemConstant.SYSTEM_MSG);
		if ( !MyStringUtil.isNull(attribute) ) {
			ex.addMap(SystemConstant.SYSTEM_MSG, attribute);
		}
		return ex;
	}
}
