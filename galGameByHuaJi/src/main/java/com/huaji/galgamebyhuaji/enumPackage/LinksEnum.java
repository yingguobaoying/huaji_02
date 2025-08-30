package com.huaji.galgamebyhuaji.enumPackage;

public enum LinksEnum {
    ok("正常", "ok"),
    unstable("不稳定", "instable"),
    die("不可用", "die");

    //'百度网盘','夸克网盘','微软云盘','第三方资源站','从服务器下载','请填写'
    // 构造方法，只用于设置初始值
    private LinksEnum(String name, String value)     {
        this.name = name;
        this.value = value;
    }

    private String value;
    private String name;

    public String getValue() {
        return value;
    }

    static public LinksEnum getEnum(String value) {
        for (LinksEnum e : LinksEnum.values()) {
            if (e.value.equals(value)) {
                return e;
            }
        }
        for (LinksEnum e : LinksEnum.values()) {
            if (e.getName().equals(value)) {
                return e;
            }
        }
        throw new RuntimeException("类型错误!在修改链接状态时使用了未定义或已经弃用的状态信息");
    }

    public String getName() {
        return name;
    }

}