package org.fenixedu.bennu.core.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

/**
 * Filter that adds caching headers to static resources.
 * 
 * These headers tell the browsers to locally cache resources for 12 hours.
 * 
 * @author João Carvalho (joao.pedro.carvalho@tecnico.ulisboa.pt)
 *
 */
@WebFilter(urlPatterns = { "*.css", "*.js", "*.gif", "*.png", "*.jpg", "*.jpeg", "*.woff", "*.svg" })
public class StaticCacheFilter implements Filter {

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        final HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setHeader("Cache-Control", "max-age=43200");
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}