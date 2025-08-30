package com.huaji.galgamebyhuaji.service;

import com.huaji.galgamebyhuaji.entity.UserToken;
import com.huaji.galgamebyhuaji.enumPackage.TokenType;
import com.huaji.galgamebyhuaji.exceptions.SessionExceptions;
import com.huaji.galgamebyhuaji.model.jwtToken.OnlineUser;
import io.micrometer.common.lang.Nullable;

/**
 * 令牌服务接口
 * <p>
 * 负责管理用户认证令牌的创建、验证和失效等操作。
 * 支持多种令牌类型，并提供了令牌自动刷新机制。
 * </p>
 */
public interface TokenService {
    /**
     * 验证令牌并可选地延长其有效时间
     * <p>
     * 此方法会验证令牌的有效性，包括：
     * 1. 令牌是否已过期
     * 2. 令牌是否被篡改
     * 3. 令牌是否与用户ID和类型匹配
     * 4. 令牌是否与IP地址匹配（如果提供）
     * </p>
     *
     * @param token      要验证的JWT令牌
     * @param userId     用户ID
     * @param type       令牌类型，如果为null则使用默认类型
     * @param ip         用户IP地址，用于验证令牌是否在允许的IP范围内（可选）
     * @param needUpdate 如果为true且令牌即将过期，则自动延长令牌有效期
     * @return 验证通过的用户令牌信息
     * @throws SessionExceptions 如果令牌验证失败或已过期
     */
    UserToken verifyToken(String token, int userId, TokenType type, @Nullable String ip, boolean needUpdate) throws SessionExceptions;
    
    /**
     * 验证并解析用户,请注意此方法不支持自动续期
     * @param token 需要解析和验证的令牌
     * @param userId 用户ID
     * @param type 类型
     * @param ip 用户IP
     * @return 通过解析的用户
     * @throws SessionExceptions 如果令牌验证失败或已过期
     */
    OnlineUser VerifyAndParse (String token, int userId, TokenType type,
                               @Nullable String ip)
            throws SessionExceptions;
    
    /**
     * 验证默认类型的令牌
     * <p>
     * 此方法是{@link #verifyToken(String, int, TokenType, String, boolean)}的简化版本，
     * 使用默认的令牌类型({@code TokenType.default_status})。
     * </p>
     *
     * @param token      要验证的JWT令牌
     * @param userId     用户ID
     * @param ip         用户IP地址（可选）
     * @param needUpdate 如果为true且令牌即将过期，则自动延长令牌有效期
     * @return 验证通过的用户令牌信息
     * @throws SessionExceptions 如果令牌验证失败或已过期
     * @see #verifyToken(String, int, TokenType, String, boolean)
     */
    UserToken verifyToken(String token, int userId, @Nullable String ip, boolean needUpdate) throws SessionExceptions;

    /**
     * 使指定类型的令牌失效
     * <p>
     * 此方法会将指定的令牌标记为已过期，使用户无法再使用该令牌进行认证。
     * 通常用于用户登出或需要强制终止会话的场景。
     * </p>
     *
     * @param token  要失效的令牌
     * @param userId 用户ID
     * @param type   令牌类型
     * @return 已失效的令牌信息
     * @throws SessionExceptions 如果令牌不存在或已经失效
     */
    UserToken invalidateToken(String token, int userId, TokenType type) throws SessionExceptions;

    /**
     * 使默认类型的令牌失效
     * <p>
     * 此方法是{@link #invalidateToken(String, int, TokenType)}的简化版本，
     * 使用默认的令牌类型({@code TokenType.default_status})。
     * </p>
     *
     * @param token  要失效的令牌
     * @param userId 用户ID
     * @return 已失效的令牌信息
     * @throws SessionExceptions 如果令牌不存在或已经失效
     * @see #invalidateToken(String, int, TokenType)
     */
    UserToken invalidateToken(String token, int userId) throws SessionExceptions;

    /**
     * 创建新令牌
     * <p>
     * 为指定用户创建新的认证令牌。如果用户已存在同类型的有效令牌，
     * 则会更新现有令牌而不是创建新令牌。
     * </p>
     *
     * @param user 包含用户信息的在线用户对象
     * @param type 令牌类型，如果为null则使用默认类型
     * @param time 令牌有效时间（毫秒）。如果小于等于0或类型为默认类型，则使用系统默认值
     * @return 新创建或更新的用户令牌
     * @throws SessionExceptions 如果令牌创建失败或用户信息不完整
     */
    UserToken insertToken(OnlineUser user, TokenType type, @Nullable long time) throws SessionExceptions;
}