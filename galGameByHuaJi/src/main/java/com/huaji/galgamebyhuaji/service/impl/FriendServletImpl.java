package com.huaji.galgamebyhuaji.service.impl;

import com.huaji.galgamebyhuaji.constant.GlobalLock;
import com.huaji.galgamebyhuaji.dao.FriendMapMapper;
import com.huaji.galgamebyhuaji.entity.FriendMap;
import com.huaji.galgamebyhuaji.entity.FriendMapExample;
import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.exceptions.OperationException;
import com.huaji.galgamebyhuaji.exceptions.WriteError;
import com.huaji.galgamebyhuaji.model.ReturnResult;
import com.huaji.galgamebyhuaji.service.FriendServlet;
import com.huaji.galgamebyhuaji.service.UserMxgServlet;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FriendServletImpl implements FriendServlet {
    private final FriendMapMapper friendMapMapper;
    private final UserMxgServlet userMxgServlet;
    
    /**
     * 拒绝好友申请
     *
     * @param starter  操作者
     * @param receiver 被拒绝的的
     *
     * @return 被拒绝的人的部分公开信息
     */
    @Override
    public Users rejectFriendRequests(@NotNull Integer starter, @NotNull Integer receiver) {
        //保证用户有序以实现用户锁
        final int userid = Math.max(starter, receiver);
        final int addFriend = Math.min(starter, receiver);
        int lockKey = Objects.hash(userid, addFriend);
        GlobalLock.safeOperation(() -> {
            FriendMapExample friendMapExample = new FriendMapExample();
            friendMapExample.createCriteria()
                    .andUserIdEqualTo(userid)
                    .andFriendIdEqualTo(addFriend);
            List<FriendMap> friendMaps = friendMapMapper.selectByExample(friendMapExample);
            if (friendMaps.isEmpty())
                throw new OperationException("好友拒绝失败,因为该好友请求不存在或者是对方取消了好友申请!", true);
            FriendMap friendMap = friendMaps.getFirst();
            if (friendMap.getFriendConfirmation())
                throw new OperationException("好友拒绝失败,因为该用户已经是您的好友了!");
            WriteError.tryWrite(friendMapMapper.deleteByPrimaryKey(friendMap));
        }, lockKey);
        return userMxgServlet.getUserListMsg(receiver);
    }
    
    /**
     * 同意好友申请
     *
     * @param starter  操作者
     * @param receiver 同意的
     *
     * @return 关注的人的部分公开信息
     */
    @Override
    public Users agreeFriendRequests(Integer starter, Integer receiver) {
        
        //保证用户有序以实现用户锁
        final int userid = Math.max(starter, receiver);
        final int addFriend = Math.min(starter, receiver);
        int lockKey = Objects.hash(userid, addFriend);
        GlobalLock.safeOperation(() -> {
            FriendMapExample friendMapExample = new FriendMapExample();
            friendMapExample.createCriteria()
                    .andUserIdEqualTo(userid)
                    .andFriendIdEqualTo(addFriend);
            //由于两个是复合主键,所以应该是唯一的
            List<FriendMap> friendMaps = friendMapMapper.selectByExample(friendMapExample);
            if (friendMaps.isEmpty())
                throw new OperationException("好友添加失败,因为好友请求不存在或者是对方取消了好友申请!");
            FriendMap friendMap = friendMaps.getFirst();
            if (friendMap.getFriendConfirmation()) {
                OperationException operationException = new OperationException("好友添加失败,因为该用户已经是您的好友了!");
                operationException.setCanIntercept(true);
                throw operationException;
            }
            if (starter.equals(friendMap.getInitiateUser())) {
                throw new OperationException("好友添加失败!因为对方还没有做出回应!");
            }
            friendMap.setFriendConfirmation(true);
            WriteError.tryWrite(friendMapMapper.updateByPrimaryKey(friendMap));
        }, lockKey);
        return userMxgServlet.getUserListMsg(receiver);
    }
    
    /**
     * 添加其他用户好友
     *
     * @param starter  操作者
     * @param receiver 被添加的
     *
     * @return 关注的人的部分公开信息
     */
    @Override
    public ReturnResult<Users> addFriend(Integer starter, Integer receiver) {
        
        //保证用户有序以实现用户锁
        final int userid = Math.max(starter, receiver);
        final int addFriend = Math.min(starter, receiver);
        int lockKey = Objects.hash(userid, addFriend);
        final ReturnResult<Users> returnResult = new ReturnResult<>();
        GlobalLock.safeOperation(() -> {
            Users users = null;
            try {
                users = agreeFriendRequests(userid, addFriend);
                //如果没有操作失败,那么就是好友申请成功了
                returnResult.operationTrue("好友添加成功,ta同意了你的请求!", users);
            } catch (OperationException e) {
                if (!e.isCanIntercept()) throw e;
                //如果没有操作失败,那么就是好友申请成功了
                returnResult.operationTrue("好友添加成功,ta同意了你的请求!", users);
                return;
            }
            //到此说明对方应该没有申请过
            FriendMap friendMap = new FriendMap();
            friendMap.setUserId(userid);
            friendMap.setFriendId(addFriend);
            friendMap.setFriendConfirmation(false);
            friendMap.setInitiateUser(starter);
            WriteError.tryWrite(friendMapMapper.insert(friendMap));
            returnResult.operationTrue("好友申请成功!请等待对方同意", userMxgServlet.getUserListMsg(receiver));
        }, lockKey);
        return returnResult;
    }
    
    /**
     * 获取好友列表
     *
     * @param starter   获取的用户
     * @param isRequest
     *
     * @return 关注列表/申请列表
     */
    @Override
    public List<Users> getUserFrendList(Integer starter, boolean isRequest) {
        return List.of();
    }
}
