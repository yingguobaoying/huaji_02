//package com.huaji.galgamebyhuaji.model;
//
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentMap;
//
///**
// * 全局用户 AI 上下文持有者。
// * 在 aiChat/aiChatByStream 入口设置，供 Advisor 和 HTTP 拦截器跨线程安全读取。
// */
//public class UserAiContextHolder {
//
//    private static final ConcurrentMap<Integer, UserAiContext> CONTEXT_MAP = new ConcurrentHashMap<>();
//
//    private UserAiContextHolder() {}
//
//    public static void set(int userId, UserAiContext ctx) {
//        CONTEXT_MAP.put(userId, ctx);
//    }
//
//    public static UserAiContext get(int userId) {
//        return CONTEXT_MAP.get(userId);
//    }
//
//    public static void remove(int userId) {
//        CONTEXT_MAP.remove(userId);
//    }
//
//    /** 清理所有超过指定分钟数的上下文（由定时任务调用） */
//    public static void cleanupStale(long timeoutMinutes) {
//        long threshold = System.currentTimeMillis() - timeoutMinutes * 60_000;
//        CONTEXT_MAP.entrySet().removeIf(entry -> {
//            UserAiContext ctx = entry.getValue();
//            return ctx != null && ctx.getCreatedAt() < threshold;
//        });
//    }
//}