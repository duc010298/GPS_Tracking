package com.github.duc010298.web_api.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.repository.AppRoleRepository;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.services.TokenAuthenticationService;

public class JWTAuthenticationFilter extends GenericFilterBean {
	
	private final AppUserRepository appUserRepository;
	private final AppRoleRepository appRoleRepository;
	
	public JWTAuthenticationFilter(AppUserRepository appUserRepository, AppRoleRepository appRoleRepository) 
    {
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
    }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		AppUser userInfoInToken = TokenAuthenticationService.getUserInfoFromRequest((HttpServletRequest) request);
		if (userInfoInToken == null) {
			chain.doFilter(request, response);
			return;
		}
		
		AppUser userInfoOnDB = appUserRepository.findByUserName(userInfoInToken.getUserName());
		if (userInfoOnDB == null) {
			String[] temp = ((HttpServletRequest)request).getRequestURL().toString().split("/");
			if(temp[temp.length-1].equalsIgnoreCase("testToken")) {
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			chain.doFilter(request, response);
			return;
		}
			
    	if(userInfoOnDB.getTokenActiveAfter().before(userInfoInToken.getTokenActiveAfter())) {
    		List<String> roleNames = this.appRoleRepository.getRoleNames(userInfoOnDB.getUserId());
    		List<GrantedAuthority> grantList = new ArrayList<>();
    		if (roleNames != null) {
    			for (String role : roleNames) {
    				GrantedAuthority authority = new SimpleGrantedAuthority(role);
    				grantList.add(authority);
    			}
    		}
    		Authentication authentication = new UsernamePasswordAuthenticationToken(userInfoInToken.getUserName(), null, grantList);
    		SecurityContextHolder.getContext().setAuthentication(authentication);
    	}
        
        chain.doFilter(request, response);
	}
}
