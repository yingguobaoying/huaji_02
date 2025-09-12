package com.huaji.galgamebyhuaji.service;


import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileServlet {
	/**
	 * 上传文件（支持多种文件类型）。
	 *
	 * @param file       上传的文件
	 * @param fileType   文件类型 当前支持:"jpeg"(范围为  {@link  com.huaji.galgamebyhuaji.constant.Constant#JPEG_MIN}-
	 *                   {@link  com.huaji.galgamebyhuaji.constant.Constant#JPEG_MAX}
	 *                   ),"zip"(范围{@link  com.huaji.galgamebyhuaji.constant.Constant#RAT_MIN}-{@link
	 *                   com.huaji.galgamebyhuaji.constant.Constant#RAR_MAX})
	 * @param usersToken 上传的用户令牌（用于权限验证和记录）
	 * @param name       指定的文件名称(可为空,为空时:指定为用户Id+时间戳)
	 * @return 返回文件存储路径或 URL
	 * @throws WriteError  数据库读写错误(小概率)
	 * @throws IOException 文件读写错误
	 */
	ReturnResult<String> uploadFile(MultipartFile file, String fileType, String usersToken, String name) throws IOException, BestException;
	
	/**
	 * 生成下载
	 *
	 * @param fileName   目标文件名称
	 * @param downName   给用户的文件名称
	 * @param usersToken 下载用户
	 * @param rId        请求下载的资源id,可以为空,但是存在时必须合法
	 * @return 下载连接
	 * @throws WriteError  数据库读写错误(小概率)
	 * @throws IOException 读写错误
	 */
	ReturnResult<ResponseEntity<InputStreamResource>> dowFile(String fileName, String downName, String usersToken, Integer rId) throws IOException, BestException;
	
	ReturnResult<ResponseEntity<InputStreamResource>> dowFile(String fileName, String downName, String usersToken, Integer rId, String type) throws IOException, BestException;
	
	ReturnResult<String> deleteFile(String fileName, String fileUrl);
}
