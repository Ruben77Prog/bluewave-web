package com.rps.bluewave.filter;

import java.io.IOException;

import com.rps.bluewave.Views;
import com.ruben.bluewave.model.Cliente;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/** Requires an authenticated customer for every resource under /private. */
public class AuthenticationFilter implements Filter {
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		Cliente cliente = null;
		if (httpRequest.getSession(false) != null) {
			cliente = (Cliente) httpRequest.getSession(false).getAttribute("usuario");
		}

		if (cliente == null) {
			httpResponse.sendRedirect(httpRequest.getContextPath() + Views.CLIENTE_LOGIN);
			return;
		}

		httpResponse.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
		httpResponse.setHeader("Pragma", "no-cache");
		chain.doFilter(request, response);
	}
}
