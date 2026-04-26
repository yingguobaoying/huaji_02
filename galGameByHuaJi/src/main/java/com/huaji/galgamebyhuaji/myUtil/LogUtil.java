package com.huaji.galgamebyhuaji.myUtil;

import com.huaji.galgamebyhuaji.entity.Users;
import org.slf4j.Logger;

public class LogUtil {
	public static void UserBehaviorLog (Logger log, String s, Users u) {
		log.info("用户%d:{%s}进行了%s".formatted(u.getUserId(), u.getUserName(), s));
	}
	public static void RootBehaviorLog (Logger log, String s, Users u) {
		log.info("管理员%d:{%s}进行了%s".formatted(u.getUserId(), u.getUserName(), s));
	}
}