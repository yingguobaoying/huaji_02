package com.huaji.galgamebyhuaji.enumPackage;

import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;

@Getter
public enum JurisdictionLevel {
	ADMIN_JURISDICTION(6, "用户管理员"),
	NOT_VALIDATED(2, "未激活邮箱的用户"),
	RESOURCES_ADMIN_JURISDICTION(5, "资源管理员"),
	RESOURCES_SHARE_JURISDICTION(4, "资源分享者"),
	ROOT_JURISDICTION(114514, "root用户"),
	SYSTEM_ADMIN_JURISDICTION(100000, "超级管理员"),
	TOURIST_JURISDICTION(0, "游客"),
	USERS_JURISDICTION(3, "一般用户");
	
	private static final JurisdictionLevel[] VALUES;
	private static final int MIN_JURISDICTION;
	
	static {
		JurisdictionLevel[] values = JurisdictionLevel.values();
		// 获取所有权限的最小等级
		int min = Integer.MAX_VALUE;
		for (JurisdictionLevel value : values) {
			if (value.getLevel() < min) {
				min = value.getLevel();
			}
		}
		MIN_JURISDICTION = min;
	}
	
	static {
		// 初始化时进行排序，确保枚举项按照 level 排序
		VALUES = values();
		Arrays.sort(VALUES, Comparator.comparingInt(JurisdictionLevel::getLevel).reversed());
	}
	
	// 需要的等级
	private final int level;
	private final String name;
	
	JurisdictionLevel(int level, String name) {
		this.level = level;
		this.name = name;
	}
	
	// 使用二分查找来获取权限
	public static JurisdictionLevel getJurisdiction(int level) {
		if (level < MIN_JURISDICTION) throw new RuntimeException("错误!不存在的权限等级信息!!");
		int left = 0;
		int right = VALUES.length - 1;
		
		while (left <= right) {
			int mid = left + (right - left) / 2;
			JurisdictionLevel midEnum = VALUES[mid];
			
			// 完全匹配的权限
			if (midEnum.getLevel() == level) {
				return midEnum;
			} else if (midEnum.getLevel() < level) {
				right = mid - 1;
			} else {
				left = mid + 1;
			}
		}
		
		// 如果没有完全匹配的权限，返回第一个小于等于权限级别的权限
		if (left < VALUES.length) {
			return VALUES[left - 1];
		} else {
			// 如果超出了最高权限（ROOT），则抛出异常
			throw new RuntimeException("错误!不存在的权限等级信息!!");
		}
		
	}
	
	
	public static String[] getNeedJurisdiction(int level) {
		return Arrays.stream(VALUES)
				.filter(v -> v.getLevel() >= level)
				.map(Enum::name) // 注意这里用 name(), 与 hasAnyRole 对应
				.toArray(String[]::new);
	}
	
	public static String[] getOwnedJurisdictions(int userLevel) {
		return Arrays.stream(VALUES)
				.filter(v -> v.getLevel() <= userLevel) //获取用户等级及以下的所有权限
				.map(Enum::name)
				.toArray(String[]::new);
	}
}
