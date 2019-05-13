package com.github.duc010298.web_api.component;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.repository.AppRoleRepository;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.services.TokenAuthenticationService;

@Component
public class AuthChannelInterceptorImpl implements ChannelInterceptor {

	private final AppUserRepository appUserRepository;
	private final AppRoleRepository appRoleRepository;

	@Autowired
	public AuthChannelInterceptorImpl(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository) {
		this.appUserRepository = appUserRepository;
		this.appRoleRepository = appRoleRepository;
	}

	@Override
	public Message<?> preSend(final Message<?> message, final MessageChannel channel) throws AuthenticationException {
		final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

		if (StompCommand.CONNECT == accessor.getCommand()) {
			String token = accessor.getFirstNativeHeader("Authorization");

			AppUser userInfoInToken = TokenAuthenticationService.getUserInfoFromToken(token);
			if (userInfoInToken == null)
				throw new UsernameNotFoundException("User not found.");

			AppUser userInfoOnDB = appUserRepository.findByUserName(userInfoInToken.getUserName());
			if (userInfoOnDB == null)
				throw new UsernameNotFoundException("User not found.");
			if (userInfoOnDB.getTokenActiveAfter().before(userInfoInToken.getTokenActiveAfter())) {
				List<String> roleNames = this.appRoleRepository.getRoleNames(userInfoOnDB.getUserId());
				List<GrantedAuthority> grantList = new ArrayList<>();
				if (roleNames != null) {
					for (String role : roleNames) {
						GrantedAuthority authority = new SimpleGrantedAuthority(role);
						grantList.add(authority);
					}
				}
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userInfoInToken.getUserName(), null,
						grantList);
				accessor.setUser(authentication);
			}
		}
		return message;
	}
}

