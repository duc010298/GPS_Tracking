package com.github.duc010298.web_api.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.github.duc010298.web_api.entity.AppUser;
import com.github.duc010298.web_api.repository.AppRoleRepository;
import com.github.duc010298.web_api.repository.AppUserRepository;
import com.github.duc010298.web_api.services.TokenAuthenticationService;

public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {
	
	private final AppRoleRepository appRoleRepository;
	private final AppUserRepository appUserRepository;
	
	public JWTLoginFilter(String url, AuthenticationManager authManager, AppUserRepository appUserRepository, AppRoleRepository appRoleRepository) {
        super(new AntPathRequestMatcher(url));
        setAuthenticationManager(authManager);
        this.appUserRepository = appUserRepository;
        this.appRoleRepository = appRoleRepository;
    }
 
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        AppUser appUserEntity = appUserRepository.findByUserName(username);
		if (appUserEntity == null) return null;
        List<String> roleNames = this.appRoleRepository.getRoleNames(appUserEntity.getUserId());

		List<GrantedAuthority> grantList = new ArrayList<>();
		if (roleNames != null) {
			for (String role : roleNames) {
				GrantedAuthority authority = new SimpleGrantedAuthority(role);
				grantList.add(authority);
			}
		}
		
        return getAuthenticationManager()
        		.authenticate((Authentication) new UsernamePasswordAuthenticationToken(username, password, grantList));
    }
 
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
            Authentication authResult) throws IOException, ServletException {
        TokenAuthenticationService.addAuthentication(response, authResult.getName());
    }

}

