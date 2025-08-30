package com.huaji.galgamebyhuaji.model;

import com.huaji.galgamebyhuaji.entity.Users;
import com.huaji.galgamebyhuaji.entity.UsersWithBLOBs;
import com.huaji.galgamebyhuaji.enumPackage.JurisdictionLevel;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;

public class LoginUserDetails implements UserDetails {
	private final Users user;
	
	public LoginUserDetails (Users user) {
		this.user = user;
	}
	
	public LoginUserDetails (UsersWithBLOBs user) {
		this.user = new Users(user);
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities () {
		// 获取用户等级
		JurisdictionLevel userLevel = JurisdictionLevel.getJurisdiction(user.getJurisdiction());
		
		// 获取所有权限 >= 用户等级（匹配 SecurityConfig 中 hasAnyRole）
		String[] roles = JurisdictionLevel.getNeedJurisdiction(userLevel.getLevel());
		
		// 转换为 GrantedAuthority
		return Arrays.stream(roles)
				.map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Spring Security 要求 ROLE_ 前缀
				.toList();
	}
	
	@Override
	public String getUsername () {return user.getUserId() + ":" + user.getUserName();}
	
	/**
	 * 当前系统未使用密码认证
	 * @return null
	 */
	@Override
	public String getPassword () {return null;}
	
	@Override
	public boolean isAccountNonExpired () {return true;}
	
	@Override
	public boolean isAccountNonLocked () {return true;}
	
	@Override
	public boolean isCredentialsNonExpired () {return true;}
	
	@Override
	public boolean isEnabled () {return true;}
	
	public Users getUser () {return user;}
}