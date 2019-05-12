package com.github.duc010298.web_api.services;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.duc010298.web_api.entity.AppUser;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class TokenAuthenticationService {

	static final String SECRET = "01021998";

	static final String TOKEN_PREFIX = "Bearer";

	static final String HEADER_STRING = "Authorization";

	public static void addAuthentication(HttpServletResponse res, String username) {
		String JWT = Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.signWith(SignatureAlgorithm.HS512, SECRET).compact();
		res.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + JWT);
	}

	public static AppUser getUserInfoFromRequest(HttpServletRequest request) {
		String token = request.getHeader(HEADER_STRING);
		try {
			if (token != null) {
				Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody();
				String user = claims.getSubject();
				if (user == null)
					return null;
				Date tokenActiveAfter = claims.getIssuedAt();
				AppUser userInfoInToken = new AppUser();
				userInfoInToken.setUserName(user);
				userInfoInToken.setTokenActiveAfter(tokenActiveAfter);
				return userInfoInToken;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	public static AppUser getUserInfoFromToken(String token) {
		try {
			if (token != null) {
				Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody();
				String user = claims.getSubject();
				if (user == null)
					return null;
				Date tokenActiveAfter = claims.getIssuedAt();
				AppUser userInfoInToken = new AppUser();
				userInfoInToken.setUserName(user);
				userInfoInToken.setTokenActiveAfter(tokenActiveAfter);
				return userInfoInToken;
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}
}
