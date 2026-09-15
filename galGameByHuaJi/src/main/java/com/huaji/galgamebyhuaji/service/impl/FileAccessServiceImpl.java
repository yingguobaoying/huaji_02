package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.FileAccessService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileAccessServiceImpl implements FileAccessService {
    private final UsersMapper usersMapper;
    protected String basePath = Constant.getRESOURCE_SAVE_PATH();
    protected static final String[] PROTECTED_NAME = new String[]{
            "default.jpeg", "error.jpeg", "ZhenZhanTu.jpg",
            "default", "error", "ZhenZhanTu"
    };
    
    @Override
    public ReturnResult<ResponseEntity<InputStreamResource>> downloadFile(String fileName, FileCategory type, String downloadName, Integer users, int rId) throws IOException, OperationException {
        if (MyStringUtil.isNull(basePath))
            basePath = Constant.getRESOURCE_SAVE_PATH();
        File file;
        if (type == null)
            file = Paths.get(basePath, fileName).toFile();
        else
            file = Paths.get(basePath, type.getFILE_SAVE_URL(), fileName).toFile();
        if (!file.exists())
            throw new OperationException("下载失败!因为指定的文件" + fileName + "不存在!");
        //获取用户信息进行记录的同时进行日志记录
        Users userListMsg = usersMapper.getUserListMxg(users);
        file = Paths.get(basePath, type != null ? type.getFILE_SAVE_URL() : "", fileName).normalize().toFile();
        String canonicalBase = new File(basePath).getCanonicalPath();
        if (!file.getCanonicalPath().startsWith(canonicalBase)) {
            MyLogUtil.error(FileAccessService.class, "警告:用户[ID{%s}:{%s}]尝试下载文件[{%s}]，路径={%s}".formatted(
                    userListMsg.getUserId(), userListMsg.getUserName(), fileName, file
            ));
            throw new OperationException("下载失败!因为指定的文件" + fileName + "不存在!");
        }
        String safeDownloadName = URLEncoder.encode(downloadName, StandardCharsets.UTF_8);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData(safeDownloadName, file.getName());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        InputStreamResource resource = new InputStreamResource(Files.newInputStream(file.toPath()));
        ResponseEntity<InputStreamResource> entity = ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length())
                .body(resource);
        MyLogUtil.info(FileAccessServiceImpl.class, "用户[ID{%s}:{%s}]下载文件[{%s}]，大小={%s}".formatted(
                userListMsg.getUserId(), userListMsg.getUserName(), fileName, FileUtil.formatFileSize(file.length())
        ));
        return new ReturnResult<ResponseEntity<InputStreamResource>>().operationTrue("下载请求成功!", entity);
    }
    
    protected static boolean isProtected(String fileName) {
        fileName = new File(fileName).getName();
        for (String s : PROTECTED_NAME) {
            if (fileName.equals(s)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public ReturnResult<String> deleteFiles(String fileName, String fileUrl) throws OperationException {
        if (MyStringUtil.isNull(basePath))
            basePath = Constant.getRESOURCE_SAVE_PATH();
        if (MyStringUtil.isNull(fileUrl)) {
            if (FileUtil.isAbsolutePath(fileName))
                fileUrl = null;
            else
                fileUrl = basePath;
        }
        File target = fileUrl == null ?
                Paths.get(fileName).normalize().toFile()
                : Paths.get(fileUrl, fileName).normalize().toFile();
        MyLogUtil.info(FileAccessService.class, "开始尝试删除文件:文件名/文件路径{%s},文件路径{%s}".formatted(fileUrl, fileName));
        if (!target.getAbsolutePath().startsWith(basePath))
            throw new OperationException("非法路径");
        if (!target.exists())
            throw new OperationException("文件不存在");
        if (!target.isFile())
            throw new OperationException("目标不是文件");
        // 保护文件,这里以成功删除作为返回,方便其他地方调用时保证逻辑的连贯
        if (isProtected(fileName))
            return ReturnResult.isTrue("文件为保护资源!无法删除!", null);
        if (!target.delete())
            throw new OperationException("文件删除失败");
        return ReturnResult.isTrue("文件删除成功", null);
    }
    
    @Override
    public ReturnResult<String> deleteFiles(List<String> fileNames, String type) throws OperationException {
        if (fileNames == null || fileNames.isEmpty()) {
            return ReturnResult.isFalse("未提供要删除的文件列表");
        }
        
        List<String> successList = new ArrayList<>();
        List<String> failList = new ArrayList<>();
        
        for (String name : fileNames) {
            if (MyStringUtil.isNull(name)) continue;
            try {
                deleteFiles(name, type);
                successList.add(name);
            } catch (OperationException e) {
                failList.add(name + "（原因：" + e.getMessage() + "）");
            }
        }
        
        if (failList.isEmpty()) {
            return ReturnResult.isTrue("以下文件已全部删除", successList, null);
        } else if (successList.isEmpty()) {
            throw new OperationException("所有文件删除失败：");
        } else {
            return ReturnResult.isTrue("部分文件删除成功,以下为成功删除的名单,预期删除{%d}个,实际删除{%d}个".formatted(fileNames.size(), successList.size()), successList, null);
        }
    }
}
