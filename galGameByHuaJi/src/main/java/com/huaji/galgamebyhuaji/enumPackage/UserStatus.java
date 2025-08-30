package com.huaji.galgamebyhuaji.enumPackage;


public enum UserStatus {
    OK("ok"),
    BLACKLIST("blacklist"),
    FROZEN("frozen"),
    BANNED("banned"),
    IS_DISABLED("is disabled"),
    NOT_AUTHENTICATED("Not authenticated");

    private String value;

    // 构造方法，只用于设置初始值
    private UserStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserStatus testEnumValue(String value) {
        for (UserStatus userStatus : UserStatus.values()) {
            if (userStatus.getValue().equalsIgnoreCase(value)) {
                return userStatus;
            }
        }
        throw new RuntimeException("用户状态错误: 在修改用户状态时使用了未定义或已经弃用的状态信息");
    }
}