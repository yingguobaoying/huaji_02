package com.huaji.galgamebyhuaji.controller;

import com.huaji.galgamebyhuaji.dto.DTOResources;
import com.huaji.galgamebyhuaji.dto.FileUpImg;
import com.huaji.galgamebyhuaji.dto.FileUpRar;
import com.huaji.galgamebyhuaji.entity.Resources;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.entity.Tag;
import com.huaji.galgamebyhuaji.service.ResourcesFileService;
import com.huaji.galgamebyhuaji.service.ResourcesService;
import com.huaji.galgamebyhuaji.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 由于此类之后应该是只有我在用所以这里返回值化简,也不管架构如何了,怎么简单怎么来 XD
 */
@Controller
@RequestMapping("/api/user")
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class ResourcesController extends BaseController {
    private final ResourcesService resourcesService;
    private final ResourcesFileService resourcesFileService;
    
    @PostMapping("/resources/add")
    @PreAuthorize("hasRole('RESOURCES_SHARE_JURISDICTION')")
    public ReturnResult<Resources> addResources(@Valid @RequestBody DTOResources resources, BindingResult testResult) {
        if (testResult.hasErrors())
            if (testResult.getFieldError() != null)
                return ReturnResult.isFalse(testResult.getFieldError().getField());
            else
                return ReturnResult.isFalse("未知错误，请稍后重试。");
        Users loginUser = getLoginUser();
        Resources resources1 = resources.getResourcesMsg();
        resources1.setrId(null);
        log.info("管理员{}{}开始添加资源信息:{},tagId信息:{}", loginUser.getUserId(), loginUser.getUserName(), resources1, resources.getTags())
        ;
        ReturnResult<Resources> resourcesReturnResult = resourcesService.addResources(resources1, resources.getTags());
        log.info("管理员{}{}添加资源信息:{},tagId信息:{}", loginUser.getUserId(), loginUser.getUserName(), resources1, resources1.getTags())
        ;
        return resourcesReturnResult;
    }
    
    @PostMapping("/resources/update")
    @PreAuthorize("hasRole('RESOURCES_SHARE_JURISDICTION')")
    public ReturnResult<String> updateResources(@Valid @RequestBody DTOResources resources, BindingResult testResult) {
        if (testResult.hasErrors())
            if (testResult.getFieldError() != null)
                return ReturnResult.isFalse(testResult.getFieldError().getField());
            else
                return ReturnResult.isFalse("未知错误，请稍后重试。");
        Users loginUser = getLoginUser();
        if (resources.getRId() == null || resources.getRId() < 0)
            return ReturnResult.isFalse("错误,不存在的ID");
        Resources resources1 = resources.getResourcesMsg();
        log.info("管理员{}{}尝试修改添加资源信息", loginUser.getUserId(), loginUser.getUserName());
        resourcesService.updateResources(resources1, resources.getTags());
        log.info("管理员{}{}修改资源信息:{},tagId信息:{}", loginUser.getUserId(), loginUser.getUserName(), resources1, resources1.getTags());
        return ReturnResult.isTrue("资源修改成功!", "资源修改成功");
    }
    
    @GetMapping("/resources/delete/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN_JURISDICTION')")
    public ReturnResult<Resources> delResources(@PathVariable int id) {
        Users loginUser = getLoginUser();
        Resources resource = resourcesService.getResource(id);
        if (resource == null || resource.getrId() == null || id < 0)
            return ReturnResult.isFalse("资源不存在");
        log.info("管理员{}{}尝试删除资源{}{}信息", loginUser.getUserId(), loginUser.getUserName(), resource.getrId(), resource.getrName());
        ReturnResult<Resources> resourcesReturnResult = resourcesService.deleteResources(id);
        log.info("管理员{}{}删除了资源{}{}信息", loginUser.getUserId(), loginUser.getUserName(), resource.getrId(), resource);
        return resourcesReturnResult;
    }
    
    @PostMapping("/resources/updateFile/img")
    @PreAuthorize("hasRole('RESOURCES_SHARE_JURISDICTION')")
    public ReturnResult<String> updateFileResourcesImg(@ModelAttribute FileUpImg fileUpMsg) throws IOException {
        if (fileUpMsg.getFileList() == null || fileUpMsg.getFileList().isEmpty() || fileUpMsg.getFileSize() <= 0)
            return ReturnResult.isFalse("上传至少一个文件!");
        List<MultipartFile> fileList = fileUpMsg.getFileList();
        if (fileList.size() != fileUpMsg.getFileSize())
            return ReturnResult.isFalse("错误!文件数量校验错误,期望接收{%d}个,实际为:{%d}个".formatted(fileUpMsg.getFileSize(), fileUpMsg.getFileList().size()));
        for (MultipartFile f : fileList) {
            if (f.getSize() >= 1024L * 1024L * 1024L * 1024L * 2L) {
                return ReturnResult.isFalse("对不起服务器太烂了,不支持这么大的文件上传捏~~文件大小最大为2GB");
            }
        }
        if (fileUpMsg.isUpdateFirstImg()) {
            if (fileList.size() != 1) {//将首尾文件交换,方便接下来操作
                Collections.swap(fileList, 0, fileList.size() - 1);
            }
        }
        Users loginUser = getLoginUser();
        log.info("管理员{}{}尝试更新上传文件,数量{}", loginUser.getUserId(), loginUser.getUserName(), fileUpMsg.getFileSize());
        return ReturnResult.isTrue(resourcesFileService.updateResourceImg(fileUpMsg.getFileList(), fileUpMsg.getAtResource(), fileUpMsg.isUpdateFirstImg()), "操作成功");
    }
    
    @PostMapping("/resources/addFile/img")
    @PreAuthorize("hasRole('RESOURCES_SHARE_JURISDICTION')")
    public ReturnResult<String> addFileResourcesImg(@ModelAttribute FileUpImg fileUpMsg) throws IOException {
        if (fileUpMsg.getFileList() == null || fileUpMsg.getFileList().isEmpty() || fileUpMsg.getFileSize() <= 0)
            return ReturnResult.isFalse("上传至少一个文件!");
        List<MultipartFile> fileList = fileUpMsg.getFileList();
        if (fileList.size() != fileUpMsg.getFileSize())
            return ReturnResult.isFalse("错误!文件数量校验错误,期望接收{}个,实际为:{}个".formatted(fileUpMsg.getFileSize(), fileUpMsg.getFileList().size()));
        for (MultipartFile f : fileList) {
            if (f.getSize() >= 1024L * 1024L * 1024L * 1024L * 2L) {
                return ReturnResult.isFalse("对不起服务器太烂了,不支持这么大的文件上传捏~~文件大小最大为2GB");
            }
        }
        if (fileUpMsg.isUpdateFirstImg()) {
            if (fileList.size() != 1) {//将首尾文件交换,方便接下来操作
                Collections.swap(fileList, 0, fileList.size() - 1);
            }
        }
        Users loginUser = getLoginUser();
        log.info("用户{}:{}尝试上传文件,数量{}", loginUser.getUserId(), loginUser.getUserName(), fileUpMsg.getFileSize());
        return ReturnResult.isTrue(resourcesFileService.addResourceImg(fileUpMsg.getFileList(), fileUpMsg.getAtResource(), fileUpMsg.isUpdateFirstImg()), "操作成功");
    }
    
    @PostMapping("/resources/addFile/rar")
    @PreAuthorize("hasRole('RESOURCES_SHARE_JURISDICTION')")
    public ReturnResult<String> addFileResourcesRar(@ModelAttribute FileUpRar fileUpMsg) throws IOException {
        if (fileUpMsg.getFileList() == null || fileUpMsg.getFileList().isEmpty() || fileUpMsg.getFileSize() <= 0)
            return ReturnResult.isFalse("上传至少一个文件!");
        List<MultipartFile> fileList = fileUpMsg.getFileList();
        if (fileList.size() != fileUpMsg.getFileSize())
            return ReturnResult.isFalse("错误!文件数量校验错误,期望接收{%d}个,实际为:{%d}个".formatted(fileUpMsg.getFileSize(), fileUpMsg.getFileList().size()));
        Users loginUser = getLoginUser();
        MyLogUtil.UserBehaviorLog(log, "开始上传文件", loginUser);
        String string = resourcesFileService.addResourceFile(fileList, fileUpMsg.getAtResource(), fileUpMsg.getFileName(), fileUpMsg.getNotes());
        MyLogUtil.UserBehaviorLog(log, "上传文件完成", loginUser);
        return ReturnResult.isTrue(string, string);
    }
    
    // ==================== Tag 管理 ====================
    final TagService tagService;
    
    /**
     * 新增标签
     */
    @PostMapping("/tag/add")
    @PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
    public ReturnResult addTag(@RequestBody Tag tag) {
        Users loginUser = getLoginUser();
        if (tag.getTagName() == null || tag.getTagName().isBlank())
            return ReturnResult.isFalse("标签名称不可为空");
        log.info("管理员{}{}添加标签:{}", loginUser.getUserId(), loginUser.getUserName(), tag);
        return tagService.addTag(tag);
    }
    
    /**
     * 修改标签
     */
    @PostMapping("/tag/update")
    @PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
    public ReturnResult updateTag(@RequestBody Tag tag) {
        Users loginUser = getLoginUser();
        if (tag.getTagId() == null || tag.getTagId() < 0)
            return ReturnResult.isFalse("错误!请提供有效的标签ID");
        if (tag.getTagName() == null || tag.getTagName().isBlank())
            return ReturnResult.isFalse("标签名称不可为空");
        log.info("管理员{}{}更新标签:{}", loginUser.getUserId(), loginUser.getUserName(), tag);
        return tagService.updateTag(tag);
    }
    
    /**
     * 删除标签
     */
    @PostMapping("/tag/delete/{id}")
    @PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
    public ReturnResult deleteTag(@PathVariable("id") Integer id) {
        Users loginUser = getLoginUser();
        if (id == null || id < 0)
            return ReturnResult.isFalse("错误!请提供有效的标签ID");
        log.info("管理员{}{}删除标签,ID:{}", loginUser.getUserId(), loginUser.getUserName(), id);
        return tagService.deleteTag(id);
    }
    
    /**
     * 获取全部标签列表
     */
    @GetMapping("/tag/list")
    @PreAuthorize("hasRole('RESOURCES_ADMIN_JURISDICTION')")
    public ReturnResult getTagList() {
        return ReturnResult.isTrue("标签列表获取成功", tagService.getTagMap().values());
    }
    
}
