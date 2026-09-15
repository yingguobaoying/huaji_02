package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.SessionMapper;
import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.SessionExample;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.enumPackage.ErrorEnum;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import com.huaji.galgamebyhuaji.myUtil.MyStringUtil;
import com.huaji.galgamebyhuaji.service.SessionService;
import com.huaji.galgamebyhuaji.service.TokenService;
import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Transactional
@RequiredArgsConstructor
public class SessionServiceIMPL implements SessionService {
    private final SessionMapper sessionMapper;
    private final TokenService tokenService;
    
    
    //由于此服务类是仅由用户服务类调用,因此不需要考虑线程安全
    @Override
    public UserToken UpdateUserLoginTime(Integer user, long time, String loginIP) throws SessionExceptions {
        Session session = getSession(user);
        boolean isFirstLogin = false;
        if (session == null) {
            //不存在会话时新建
            session = new Session();
            isFirstLogin = true;
            session.setUserId(user);
        }
        if (!isFirstLogin) {//非第一次登录时检查之前令牌状态
            if (session.getStatus()) {//ture为当前在线
                if (!MyStringUtil.isNull(session.getLastLoginIp()) &&
                    !session.getLastLoginIp().equals(loginIP))//上次登录地点不为空,并且不匹配
                    throw new SessionExceptions("错误!您的账号已经在其他地点登陆了登录ip为:%s".formatted(session.getLastLoginIp()), ErrorEnum.SESSION_DIFFERENT_ERROR);
            }
        }
        OnlineUser onlineUser = new OnlineUser();
        onlineUser.setUserId(user);
        onlineUser.setIp(loginIP);
        onlineUser.setTokenType(TokenType.DEFAULT_STATUS);
        UserToken userToken = tokenService.insertToken(onlineUser, TokenType.DEFAULT_STATUS, time);
        session.setLastLoginIp(loginIP);
        session.setLastLoginTime(new Date());
        session.setStatus(true);
        if (isFirstLogin)
            WriteError.tryWrite(sessionMapper.insertSelective(session));
        else
            WriteError.tryWrite(sessionMapper.updateByPrimaryKeySelective(session));
        return userToken;
    }
    
    @Override
    public Session testLoginTime(Integer usersId) throws SessionExceptions {
        Session session = getSession(usersId);
        if (session == null || session.getSessionId() == null)
            throw new OperationException("登录信息验证失败!请重新登录!");
        //验证用户状态和是否在线
        if (!session.getStatus()) throw new OperationException("您的登录已过期,请重新登录!");
        return session;
    }
    
    @Override
    @Nullable
    public Session getSession(Integer user) throws SessionExceptions {
        if (user == null)
            throw new SessionExceptions("用户不存在", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
        List<Session> sessionList = sessionMapper.getSessionByUser(user);
        if (sessionList == null || sessionList.isEmpty()) {
            //不存在会话时返回空
            return null;
        } else if (sessionList.size() == 1) {
            //存在一个会话时
            return sessionList.getFirst();
        }
        throw new SessionExceptions("错误!存在多个会话信息,请联系管理员进行删除", ErrorEnum.SESSION_REPEAT_ERROR);
    }
    
    @Override
    public int manbaOut() {
        //仅建议在服务器关闭时调用一次,因为该方法会将所有用户设置为离线状态
        return sessionMapper.helicopterAirCrash();
    }
    
    @Override
    public String exitLogin(Integer usersId, boolean isAutomaticOut, String token) throws SessionExceptions {
        if (usersId == null) throw new OperationException("用户不存在");
        ReentrantLock lockForUser = GlobalLock.getLockForUser(usersId);
        try {
            lockForUser.lock();
            if (isAutomaticOut) {
                sessionMapper.exitLogin(usersId);
                return "已经成功退出";
            }
            Session session = getSession(usersId);
            if (session == null || session.getSessionId() == null)
                throw new SessionExceptions("错误!您的会话信息不存在,无法退出登录!", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
            SessionExample sessionExample = new SessionExample();
            if (!session.getStatus())
                throw new SessionExceptions("错误!您已经退出登录了,无法再次退出登录!", ErrorEnum.SESSION_NOT_AVAILABLE_ERROR);
            session.setStatus(false);
            tokenService.invalidateToken(token, usersId);
            sessionExample.createCriteria().andSessionIdEqualTo(session.getSessionId());
            WriteError.tryWrite(sessionMapper.updateByExampleSelective(session, sessionExample));
            return "您已经成功退出登录,我们期待您的再次访问和使用";
        } finally {
            GlobalLock.unlockForUser(lockForUser, usersId);
        }
    }
}
