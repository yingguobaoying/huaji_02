package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.entity.Session;
import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.exceptions.BestException;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;

/**
 * 会话服务接口，负责管理用户会话相关的操作
 * <p>
 * 使用此接口的前提: 用户已通过验证
 */
public interface SessionService {
    /**
     * 更新用户登录时间并生成新的令牌
     *
     * @param user    用户对象，包含用户ID等信息
     * @param time    会话有效时间（毫秒）
     * @param loginIP 登录IP地址，用于验证登录地点
     * @return 新生成的用户令牌
     * @throws BestException 如果用户已在其他地方登录或会话操作失败时抛出
     * @throws SessionExceptions 如果会话状态异常或存在多个会话时抛出
     */
    UserToken UpdateUserLoginTime(Integer user, long time, String loginIP) throws BestException;

    /**
     * 验证用户的在线状态
     *
     * @param usersId 需要检查的用户ID
     * @return 用户的会话信息
     * @throws SessionExceptions 如果用户会话不存在或已过期时抛出
     */
    Session testLoginTime(Integer usersId) throws SessionExceptions;

    /**
     * 强制使所有在线用户离线
     * <p>
     * 注意：此方法通常仅在服务器关闭时调用，会将所有用户设置为离线状态
     *
     * @return 被强制下线的用户数量
     */
    int manbaOut();

    /**
     * 用户退出登录
     * <p>
     * 此方法会将用户状态设置为离线，并使其令牌失效
     *
     * @param usersId        要退出的用户ID
     * @param isAutomaticOut 是否为自动退出（如超时自动退出）
     * @param token          要失效的访问令牌
     * @return 退出操作的提示信息
     * @throws BestException 如果用户不存在或退出操作失败时抛出
     */
    String exitLogin(Integer usersId, boolean isAutomaticOut, String token) throws BestException;

    /**
     * 获取指定用户的会话信息
     *
     * @param userId 用户ID
     * @return 用户的会话信息
     * @throws SessionExceptions 如果会话不存在或获取失败时抛出
     */
    Session getSession(Integer userId) throws SessionExceptions;
}