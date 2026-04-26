package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.Constant;
import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.SessionMapper;
import com.huaji.galgamebyhuaji.dao.UsersMapper;
import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.enumPackage.UserStatus;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.myUtil.*;
import com.huaji.galgamebyhuaji.service.LoginService;
import com.huaji.galgamebyhuaji.service.RootServlet;
import com.huaji.galgamebyhuaji.service.SessionService;
import com.huaji.galgamebyhuaji.service.TokenService;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class RootServletImpl implements RootServlet {
	private final UsersMapper usersMapper;
	private final TokenService tokenService;
	private final PasswordEncryptionUtil passwordEncryptionUtil;
	private final SessionMapper sessionMapper;
	private final SessionService sessionService;
	private final UserMxgServlet userMxgServlet;
	
	@Override
	public String RootEditUserHeadPortrait (int usersId, int rootId, MultipartFile jpeg) throws WriteError, IOException {
		Users root = usersMapper.getUserListMxg(rootId);
		Users user = usersMapper.getUserListMxg(usersId);
		MyLogUtil.info(RootServlet.class, "管理员%d{%s}尝试强制修改%d{%s}的头像信息".formatted(
				root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName()
		                                                                                      ));
		boolean banHead = (jpeg == null || jpeg.isEmpty());
		boolean isTrue = true;
		try {
			if ( banHead )
				return userMxgServlet.updateUserHeadPortraitUrl(null, usersId, true);
			else
				return userMxgServlet.updateUserHeadPortraitUrl(jpeg, user.getUserId(), false);
		} catch ( Exception e ) {
			isTrue = false;
			MyLogUtil.info(RootServlet.class, "管理员%d{%s}尝试强制修改%d{%s}的头像信息失败了,因为%s".formatted(
					root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(), e
			                                                                                                   ));
			throw e;
		} finally {
			MyLogUtil.info(RootServlet.class, "管理员%d{%s}强制修改%d{%s}的头像信息结果:{%s}".formatted(
					root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(), isTrue ? "成功" : "失败"
			                                                                                           ));
		}
	}
	
	@Override
	public String RootEditUserMxg (UsersWithBLOBs user, int rootId) throws WriteError {
		Users root = usersMapper.getUserListMxg(rootId);
		MyLogUtil.info(RootServlet.class, "管理员%d{%s}尝试强制修改%d{%s}的信息".formatted(
				root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName()
		                                                                                  ));
		boolean isTrue = true;
		try {
			if ( !(rootId == 1 || rootId == 0) ) {//特殊用户保护
				if ( user.getUserId().equals(1) || user.getUserId().equals(0) )
					throw new OperationException("禁止修改特殊用户的用户信息!");
			}
			WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(user));
			return "修改完成";
		} catch ( Exception e ) {
			isTrue = false;
			MyLogUtil.info(RootServlet.class, "管理员%d{%s}尝试强制修改%d{%s}的信息失败了,因为%s".formatted(
					root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(), e
			                                                                                               ));
			throw e;
		} finally {
			MyLogUtil.info(RootServlet.class, "管理员%d{%s}强制修改%d{%s}的信息结果:{%s}".formatted(
					root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(), isTrue ? "成功" : "失败"
			                                                                                       ));
		}
	}
	
	@Override
	public String RootUpdateUSerStatus (int usersId, int rootId, UserStatus userStatus) throws WriteError {
		Users root = usersMapper.getUserListMxg(rootId);
		Users user = usersMapper.getUserListMxg(usersId);
		MyLogUtil.info(RootServlet.class, "管理员%d{%s}尝试强制修改%d{%s}的状态信息,状态变更{%s}->{%s}".formatted(
				root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(),
				UserStatus.testEnumValue(user.getStatus()).getName(), userStatus.getName()
		                                                                                                         ));
		boolean isTrue = true;
		try {
			if ( !(rootId == 1 || rootId == 0) ) {//特殊用户保护
				if ( user.getUserId().equals(1) || user.getUserId().equals(0) )
					throw new OperationException("禁止修改特殊用户的用户状态信息!");
			}
			UsersWithBLOBs usersWithBLOBs = new UsersWithBLOBs();
			usersWithBLOBs.setUserId(usersId);
			usersWithBLOBs.setStatus(userStatus.getValue());
			WriteError.tryWrite(usersMapper.updateByPrimaryKeySelective(usersWithBLOBs));
			return "修改完成";
		} catch ( Exception e ) {
			isTrue = false;
			MyLogUtil.info(RootServlet.class, "管理员%d{%s}尝试强制修改%d{%s}的状态信息失败了,因为%s".formatted(
					root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(), e
			                                                                                                   ));
			throw e;
		} finally {
			MyLogUtil.info(RootServlet.class, "管理员%d{%s}强制修改%d{%s}的状态信息结果:{%s}".formatted(
					root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName(), isTrue ? "成功" : "失败"
			                                                                                           ));
		}
	}
	
	@Override
	public UsersWithBLOBs RootSelectUserById (int usersId, int rootId) {
		Users root = usersMapper.getUserListMxg(rootId);
		Users user = usersMapper.getUserListMxg(usersId);
		MyLogUtil.info(RootServlet.class, "管理员%d{%s}查询%d{%s}的信息".formatted(
				root.getUserId(), root.getUserName(), user.getUserId(), user.getUserName()
		                                                                          ));
		return usersMapper.selectByPrimaryKey(usersId);
	}
	
	@Override
	public List<UsersWithBLOBs> RootSelectUserByName (String usersName, int root) {
		Users r = usersMapper.getUserListMxg(root);
		MyLogUtil.info(RootServlet.class, "管理员%d{%s}查询用户{%s}的信息".formatted(
				r.getUserId(), r.getUserName(), usersName
		                                                                            ));
		return usersMapper.selectUserByName(usersName);
	}
	
	@Override
	public boolean isIPWhiteList (String ip) {
		for ( String whiteIp : IP_WHITELIST ) {
			if ( ElseUtil.equalsIp(ip, whiteIp) ) {
				return true;
			}
		}
		return false;
	}
	
	private static final String[] IP_WHITELIST = new String[]{
			// IPv4
			"127.0.0.1",
			"localhost",
			// 其他 IPv4
			"127.0.0.2",
			"127.0.1.1",
			// IPv6
			"::1",
			"0:0:0:0:0:0:0:1"
	};
	
	
	@Override
	@Transactional
	public UserToken rootLogin (String token, HttpServletRequest request) {
		ReentrantLock lock = null;
		ReentrantLock lock1 = null;
		UserToken userToken;
		String ip = ElseUtil.getClientIp(request);
		try {
			MyLogUtil.info(LoginService.class, "ip:{%s}正在登录特殊root用户".formatted(ip));
			if ( !MyStringUtil.isNull(token) && token.length() >= 20 ) {
				lock = GlobalLock.getLockForUser(0);
				lock1 = GlobalLock.getLockForUser(1);
				lock.lock();
				lock1.lock();
				//手动校验ip
				boolean b = isIPWhiteList(ElseUtil.getClientIp(request));
				if ( !b ) {
					MyLogUtil.info(LoginService.class, "ip:{%s}正在登录特殊root用户时失败了,因为ip不在白名单之内".formatted(ip));
					throw new OperationException("您使用的ip被防火墙隔离了,请换个ip试试");
				}
				//不自动校验ip
				userToken = tokenService.verifyToken(token, -1, null, true);
				return userToken;
			} else {
				MyLogUtil.error(LoginService.class,
						"ip:{%s}登录特殊root用户时失败了,因为输入长度不达标".formatted(ip));
			}
		} catch ( Exception e ) {
			MyLogUtil.error(LoginService.class,
					"ip:{%s}正在登录特殊root用户时失败了,因为:".formatted(ip) + e.getMessage());
		} finally {
			if ( lock != null ) {GlobalLock.unlockForUser(lock, 0);}
			if ( lock1 != null ) {GlobalLock.unlockForUser(lock1, 1);}
		}
		passwordEncryptionUtil.applyRandomDelay(10, 500);
		throw new OperationException("账号或密码不正确，请检查后重试。");
	}
	
	
	@Override
	public List<Users> getUserList (PageUtil page) {
		return usersMapper.getUserListMxgByRootList(page);
	}
	
	@Override
	public Integer getUserSize () {
		return usersMapper.getUserSize();
	}
	
	@Override
	public void rootUserInit () throws SessionExceptions {
		UsersWithBLOBs root0 = usersMapper.selectByPrimaryKey(0);
		if ( root0 == null || root0.getUserId() == null ) {
			UsersWithBLOBs users = new UsersWithBLOBs();
			users.setUserId(0);
			users.setStatus(UserStatus.OK.getValue());
			users.setUserNameLogin("hongdouchu");
			users.setJurisdiction(JurisdictionLevel.ROOT_JURISDICTION.getLevel());
			users.setUserHeadPortraitUrl(Constant.DEFAULT_HEAD_PORTRAIT);
			users.setCoin(114514);
			users.setUserName("一只滑稽/因果报应");
			users.setMailbox("hongdouchu@hongdouchu.com");
			users.setUserPassword("红豆可爱滴捏");//反正是密码的占位,也不可能解析的上
			users.setSex("武装直升机");
			users.setBio("一只红豆厨");
			usersMapper.insert(users);
		}
		UsersWithBLOBs root1 = usersMapper.selectByPrimaryKey(1);
		if ( root1 == null || root1.getUserId() == null ) {
			UsersWithBLOBs users = new UsersWithBLOBs();
			users.setUserId(1);
			users.setStatus(UserStatus.OK.getValue());
			users.setJurisdiction(JurisdictionLevel.ROOT_JURISDICTION.getLevel());
			users.setUserHeadPortraitUrl(Constant.DEFAULT_HEAD_PORTRAIT);
			users.setCoin(999999);
			users.setUserName("红豆");
			users.setUserPassword("红豆可爱滴捏");
			users.setBio("罗德岛先锋干员红豆，握紧长枪，准备着进入战场。\n" +
					"\n" +
					"她很清楚，在需要全身心投入这一点上，战争和摇滚别无二致。萨卡兹少女，代号红豆，身高142cm。出生于卡兹戴尔，但自幼随父母在哥伦比亚城市中生活。作为萨卡兹人，她经历了动荡不安的童年和频繁的城市冲突，这使她形成了坚韧不拔、绝不妥协的性格。\n" +
					"\n" +
					"红豆热爱音乐，尤其是摇滚，并用省吃俭用的资金购买了她的第一把电吉他。她相信音乐能够打破歧视和隔阂，传递内心的呐喊。红豆不仅是一名技艺高超的吉他手，也是罗德岛小队的先锋人员，在战术突袭和开辟战场方面表现出色。\n" +
					"\n" +
					"尽管感染了矿石病，红豆依然积极向上，努力锻炼自己并改造她的武器。她坚信，人生的目标应靠自己的努力去达成，不管遇到什么困难，她都会坚持自己的信念。\n" +
					"\n" +
					"在罗德岛，红豆不仅是一名优秀的战士，更是一个充满活力和激情的年轻人。她用自己的热情和决心感染着身边的每一个人，无论是战斗还是生活，她都用心去面对每一个挑战。");
			users.setMailbox("Vigna_BABIE_Arknights.com");
			users.setUserNameLogin("Vigna");
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			LocalDate localDate = LocalDate.parse("2019-04-30", formatter);
			users.setBirthday(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
			usersMapper.insert(users);
		}
		//设置token
		OnlineUser onlineUser0 = new OnlineUser();
		onlineUser0.setIp(null);
		onlineUser0.setUserId(0);
		onlineUser0.setTokenType(TokenType.DEFAULT_STATUS);
		OnlineUser onlineUser1 = new OnlineUser();
		onlineUser1.setIp(null);
		onlineUser1.setUserId(1);
		onlineUser1.setTokenType(TokenType.DEFAULT_STATUS);
		UserToken userToken0 = tokenService.insertToken(onlineUser0, TokenType.DEFAULT_STATUS,
				1000L * 60 * 60 * 24 * 100);
		UserToken userToken1 = tokenService.insertToken(onlineUser1, TokenType.DEFAULT_STATUS,
				1000L * 60 * 60 * 24 * 100);
		//todo: 注意后续把这玩意扔到vault里面
		MyLogUtil.info(LoginService.class, "***********************************************************");
		MyLogUtil.info(LoginService.class, "***********************root登录令牌更新**********************");
		MyLogUtil.info(LoginService.class, "***********************************************************");
		MyLogUtil.info(LoginService.class, "0号root用户登录令牌:" + userToken0.getToken());
		System.out.println("0号root用户登录令牌:" + userToken0.getToken());
		System.out.println("1号root用户登录令牌:" + userToken1.getToken());
		MyLogUtil.info(LoginService.class, "1号root用户登录令牌:" + userToken1.getToken());
		MyLogUtil.info(LoginService.class, "***********************************************************");
		MyLogUtil.info(LoginService.class, "***********此令牌仅本次服务器启动时有效,服务器关闭后失效************");
		MyLogUtil.info(LoginService.class, "***********************************************************");
		//将两个root设置为在线
		Session user0Session = sessionService.getSession(0);
		boolean user0IsNull = user0Session == null;
		Session user1Session = sessionService.getSession(1);
		boolean user1IsNull = user1Session == null;
		if ( user0IsNull ) {
			user0Session = new Session();
			user0Session.setUserId(0);
			user0Session.setLastLoginTime(new Date());
		}
		if ( user1IsNull ) {
			user1Session = new Session();
			user1Session.setUserId(1);
			user1Session.setLastLoginTime(new Date());
		}
		user0Session.setLastLoginIp("null");//置为空,因为手动接管了ip校验
		user0Session.setStatus(true);//设置为在线
		user1Session.setLastLoginIp("null");
		user1Session.setStatus(true);
		//更新/插入
		if ( !user0IsNull )
			WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(user0Session));
		else
			WriteError.tryWrite(sessionMapper.insert(user0Session));
		if ( !user1IsNull )
			WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(user1Session));
		else
			WriteError.tryWrite(sessionMapper.insert(user1Session));
	}
}