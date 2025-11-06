package com.huaji.galgamebyhuaji.AOP;

import com.huaji.galgamebyhuaji.dao.UserDownloadMapper;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.UserDownload;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.service.FileAccessService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

@Aspect
@Component
@RequiredArgsConstructor
public class DownServiceAspect {
	
	final UsersMapper usersMapper;
	final UserDownloadMapper userDownloadMapper;
	
	/**
	 * 匹配 downloadFile 方法，参数为: String, FileCategory, String, Integer, int
	 * 接口:ReturnResult<ResponseEntity<InputStreamResource>>  downloadFile(String fileName, FileCategory type, String downloadName, Integer users,int rId)
	 * throws IOException, OperationException;
	 */
	@Pointcut("execution(* com.huaji.galgamebyhuaji.service.FileAccessService.downloadFile(String, com.huaji.galgamebyhuaji.enumPackage.FileCategory, String, Integer, int))")
	public void downloadMethod() {}  // 修改方法名更符合实际功能
	
	/**
	 * 环绕通知 - 记录下载操作
	 */
	@Around("downloadMethod()")
	public Object logDownloadOperation(ProceedingJoinPoint joinPoint) throws Throwable {
		// 执行目标方法
		Object result = joinPoint.proceed();
		try {
			// 能执行到这里说明下载操作已完成，记录下载日志
			recordDownloadLog(joinPoint.getArgs());
		} catch (Exception e) {
			// 记录日志失败不应该影响主业务流程
			MyLogUtil.error(FileAccessService.class, "记录下载日志失败，但不影响文件下载", e);
		}
		
		return result;
	}
	
	/**
	 * 记录下载日志
	 */
	private void recordDownloadLog(Object[] args) {
		if (args == null || args.length < 5) {
			MyLogUtil.info(FileAccessService.class, "下载方法参数不足，无法记录下载日志");
			return;
		}
		
		try {
			// 安全地获取参数
			String fileName = getStringArg(args);
			Integer userId = getIdArg(args, 3);
			Integer resourceId = getIdArg(args, 4);
			
			// 创建下载记录
			UserDownload userDownload = new UserDownload();
			userDownload.setTime(new Date());
			userDownload.setrId(resourceId);
			userDownload.setdType("服务器下载");
			userDownload.setFileName(fileName);
			userDownload.setUserId(userId);  // 确保设置了userId
			
			// 记录下载日志
			int insertResult = userDownloadMapper.insert(userDownload);
			WriteError.tryWrite(insertResult);
			
		} catch (Exception e) {
			MyLogUtil.error(FileAccessService.class, "构建下载记录时发生错误", e);
			throw new RuntimeException("下载日志记录失败", e);
		}
	}
	
	/**
	 * 安全获取字符串参数
	 */
	private String getStringArg(Object[] args) {
		if (args[0] == null) {
			return null;
		}
		return args[0].toString();
	}
	
	
	/**
	 * 安全获取Integer参数
	 */
	private Integer getIdArg(Object[] args, int index) {
		if (index >= args.length || args[index] == null) {
			return null;
		}
		// 处理 int 和 Integer 两种情况
		if (args[index] instanceof Integer) {
			return (Integer) args[index];
		} else if (args[index] instanceof Number) {
			return ((Number) args[index]).intValue();
		}
		return null;
	}
	
}
