package com.github.duc010298.web_api.services;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.repository.AppRoleRepository;
import com.github.duc010298.web_api.repository.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private AppRoleRepository appRoleRepository;
	private AppUserRepository appUserRepository;

	@Autowired
	public UserDetailsServiceImpl(AppRoleRepository appRoleRepository, AppUserRepository appUserRepository) {
		this.appRoleRepository = appRoleRepository;
		this.appUserRepository = appUserRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		AppUser appUserEntity = appUserRepository.findByUserName(userName);
		if (appUserEntity == null)
			throw new UsernameNotFoundException("User not found.");
		List<String> roleNames = this.appRoleRepository.getRoleNames(appUserEntity.getUserId());

		List<GrantedAuthority> grantList = new ArrayList<>();
		if (roleNames != null) {
			for (String role : roleNames) {
				GrantedAuthority authority = new SimpleGrantedAuthority(role);
				grantList.add(authority);
			}
		}

		UserDetails userDetails = new User(appUserEntity.getUserName(), appUserEntity.getEncryptedPassword(), grantList);

		return userDetails;
	}
}

