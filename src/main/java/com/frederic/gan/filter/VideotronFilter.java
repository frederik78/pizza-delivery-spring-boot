package com.frederic.gan.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.webapp.impl.security.auth.Authentication;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.springframework.beans.factory.annotation.Autowired;

public class VideotronFilter implements Filter {

	private static String CAM_AUTH_SESSION_KEY = "authenticatedUser";

	@Autowired
	ProcessEngine engine;

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (null == findAuthentifiedUser((HttpServletRequest) request)) {
			((HttpServletResponse) response).sendRedirect("/app/welcome/default/#/login");
		} else {
			chain.doFilter(request, response);
		}
	}

	private String findAuthentifiedUser(HttpServletRequest request) {
		final Authentications authentications = (Authentications) request.getSession()
				.getAttribute(CAM_AUTH_SESSION_KEY);
		if (null == authentications) {
			return null;
		}
		final List<Authentication> list = authentications.getAuthentications();
		return list.iterator().hasNext() ? list.iterator().next().getIdentityId() : null;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
