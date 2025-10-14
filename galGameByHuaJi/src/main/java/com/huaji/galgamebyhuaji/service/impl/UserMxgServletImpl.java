package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.dto.UserMxgWithOldUserMxg;
import com.huaji.galgamebyhuaji.dto.UserNewMsg;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersExample;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.FileCategory;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.myUtil.FileUtil;
import com.huaji.galgamebyhuaji.myUtil.MyLogUtil;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.myUtil.PasswordEncryptionUtil;
import com.huaji.galgamebyhuaji.myUtil.TimeUtil;
import com.huaji.galgamebyhuaji.service.FileAccessService;
import com.huaji.galgamebyhuaji.service.FileUploadService;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.RedisMemoryService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class UserMxgServletImpl implements UserMxgServlet {
	final UsersMapper usersMapper;
	final RedisMemoryService redisMemoryService;
	final PasswordEncryptionUtil passwordEncryptionUtil;
	final LoginService loginService;
	final FileAccessService fileAccessService;
	final FileUploadService fileUploadService;
	
	@Override
	public UsersWithBLOBs updateUsers(UserMxgWithOldUserMxg userMxgWithOldUserMxg) {
		UserNewMsg users = userMxgWithOldUserMxg.getUsers();
		if (users.getUserId() < 0)
			throw new OperationException("用户不存在!");
		UsersWithBLOBs oldUserMxg = usersMapper.selectByPrimaryKey(users.getUserId());
		//检查邮箱是否变更
		boolean mailboxChange = !users.getMailbox().equals(oldUserMxg.getMailbox());
		//检查旧密码
		if (!passwordEncryptionUtil.verifyPassword(
				userMxgWithOldUserMxg.getOldPassWord(),
				oldUserMxg.getUserPassword()))
			throw new OperationException("旧密码错误!");
		//将未设置的值置空
		users = MyStringUtil.setNull(users);
		assert users != null;
		users.setUserPassword(passwordEncryptionUtil.hashPassword(users.getUserPassword()));//加密
		if (mailboxChange) users.setJurisdiction(JurisdictionLevel.NOT_VALIDATED.getLevel());
		UsersWithBLOBs row = users.passUser();
		
		final Integer userId = users.getUserId();
		row.setUserId(userId);
		return GlobalLock.safeExecute(users.getUserId(), () -> {
			//检查信息是否被使用
			loginService.testRegisterMxg(row, userId);
			//更新
			WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(row));
			//清空密码
			row.setUserPassword(null);
			Users u = new Users();
			u.setUserId(userId);
			u.setUserName(row.getUserName());
			u.setUserHeadPortraitUrl(row.getUserHeadPortraitUrl());
			redisMemoryService.saveData(row);
			return row;
		});
	}
	
	/**
	 * 更新用户头像
	 *
	 * @param croppedImage 图片
	 * @param userId       更改头像的用户用于信息验证
	 * @param userToken    用户token
	 *
	 * @return 更新结果信息
	 */
	@Override
	public String updateUserHeadPortraitUrl(MultipartFile croppedImage, int userId, boolean banHeadPortrait, String userToken) throws BestException, IOException {
		if (userId < 0)
			throw new OperationException("用户不存在!");
		Users users = usersMapper.selectByPrimaryKey(userId);
		if (users == null || users.getUserId() == null || !users.getUserId().equals(userId))
			throw new OperationException("用户不存在!");
		return GlobalLock.safeExecute(userId, () -> {
			if (banHeadPortrait) {
				WriteError.tryWrite(usersMapper.setUserHeadPortraitUrl(userId, null));
				if (!MyStringUtil.isNull(users.getUserHeadPortraitUrl()))
					fileAccessService.deleteFiles(users.getUserHeadPortraitUrl(),
					                              FileUtil.formatUrl(Constant.getRESOURCE_SAVE_PATH(),
					                                                 FileCategory.IMG.getFILE_SAVE_URL(),
					                                                 "user"));
				Users u = new Users();
				u.setUserId(userId);
				u.setUserName(users.getUserName());
				u.setUserHeadPortraitUrl(null);
				redisMemoryService.saveData(users);
				return "头像已设置为禁用!";
			}
			//非禁用时如果没发现文件就认为错误
			if (croppedImage == null) throw new OperationException("错误!未发现需要设置为头像的文件!");
			String name = userId + TimeUtil.getNowTime();
			ReturnResult<String> jpeg;
			try {
				jpeg = fileUploadService.uploadFile(croppedImage, FileCategory.IMG,
				                                    userId + TimeUtil.getNowTime(), "user");
			} catch (IOException e) {
				throw new RuntimeException("上传头像失败", e);
			}
			if (jpeg.isOperationResult()) {
				try {
					WriteError.tryWrite(usersMapper.setUserHeadPortraitUrl(userId, FileUtil.toRelativeUrl(jpeg.getReturnResult(), FileCategory.IMG)));
				} catch (WriteError e) {
					MyLogUtil.error(UserMxgServletImpl.class, e);
					fileAccessService.deleteFiles(name, String.valueOf(new File(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL())));
					return "头像设置失败!这可能是我们服务器抽风了,请稍后再试一次,如果您多次遇到这个错误请联系管理员!";
				}
			}
			//删除原头像
			if (!MyStringUtil.isNull(users.getUserHeadPortraitUrl()))
				fileAccessService.deleteFiles(users.getUserHeadPortraitUrl(), String.valueOf(new File(Constant.getRESOURCE_SAVE_PATH(), FileCategory.IMG.getFILE_SAVE_URL())));
			return FileUtil.toRelativeUrl(jpeg.getReturnResult(), FileCategory.IMG);
		});
	}
	
	@Override
	public ReturnResult<Users> deleteUsers(int rootId, int usersId, String userToken) throws BestException {
		if (rootId < 0) throw new OperationException("用户不存在!");
		if (usersId < 0) throw new OperationException("被操作用户不存在!");
		Users root = loginService.testLogin(userToken, JurisdictionLevel.ADMIN_JURISDICTION.getLevel());
		boolean canDle = root.getUserId().equals(0) || root.getUserId().equals(1);
		UsersWithBLOBs user = usersMapper.selectByPrimaryKey(usersId);
		if (user == null || user.getUserId() == null || !user.getUserId().equals(usersId))
			throw new OperationException("被删除的用户不存在!");
		if (!canDle) {
			//其他情况,非特殊用户进行检查
			if (root.getUserId().equals(usersId)) throw new OperationException("错误!你不能删除自己!");
			if (usersId == 1 || usersId == 0) throw new OperationException("错误!您的权限不足!无法删除:特殊root用户");
			if (root.getJurisdiction() < user.getJurisdiction())
				throw new OperationException("错误!您的权限不足!无法删除比您权限更高级的[id:%s , name:%s]为".formatted(user.getUserId(), user.getUserName()));
		}
		WriteError.tryWrite(usersMapper.deleteByPrimaryKey(usersId));
		redisMemoryService.deleteKey(usersId, Users.class);
		return ReturnResult.isTrue("删除成功!", user);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UsersWithBLOBs selectUsers(Integer usersId) {
		return usersMapper.getUserPublic(usersId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public void getAllUserListMxg() {
		Map<Integer, Users> allUserListMxg = usersMapper.getAllUserListMxg();
		redisMemoryService.setKey(allUserListMxg);
	}
	
	@Override
	@Transactional(readOnly = true)
	public UsersWithBLOBs getItselfMxg(Integer usersId) {
		if (usersId == null) throw new OperationException("用户不存在!");
		UsersExample usersExample = new UsersExample();
		usersExample.createCriteria().andUserIdEqualTo(usersId);
		List<UsersWithBLOBs> users = usersMapper.selectByExampleWithBLOBs(usersExample);
		if (users == null || users.isEmpty())
			throw new OperationException("用户不存在!");
		UsersWithBLOBs first = users.getFirst();
		first.setUserPassword(null);
		return first;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Users getUserListMsg(Integer usersId) {
		if (usersId == null) throw new OperationException("用户不存在!");
		Users users;
		users = redisMemoryService.getData(usersId, Users.class);
		if (users == null || users.getUserId() == null) {
			UsersWithBLOBs userPublic = usersMapper.getUserPublic(usersId);
			users = new Users();
			users.setUserId(userPublic.getUserId());
			users.setUserName(userPublic.getUserName());
			users.setUserHeadPortraitUrl(userPublic.getUserHeadPortraitUrl());
			redisMemoryService.saveData(users);
		}
		if (users.getUserId() == null) throw new OperationException("用户不存在!");
		return users;
	}
}
