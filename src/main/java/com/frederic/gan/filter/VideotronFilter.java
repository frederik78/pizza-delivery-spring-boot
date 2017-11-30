package com.frederic.gan.filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.ProcessEngine;
import org.camunda.bpm.webapp.impl.security.auth.Authentication;
import org.camunda.bpm.webapp.impl.security.auth.Authentications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

public class VideotronFilter implements Filter {

	private static String CAM_AUTH_SESSION_KEY = "authenticatedUser";

	@Autowired
	ProcessEngine engine;

	@Autowired
	private ResourceLoader resourceLoader;

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

	private String getWebResourceContents(String name) throws IOException {
		InputStream is = null;

		try {
			Resource resource = resourceLoader.getResource("classpath:" + name);
			is = resource.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(is));

			StringWriter writer = new StringWriter();
			String line = null;

			while ((line = reader.readLine()) != null) {
				writer.write(line);
				writer.append("\n");
			}

			return writer.toString();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
