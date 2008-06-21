package com.digitalenergyinc.festival.server;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This is a cache filter to cache GWT cache files in the container.
 * Taken from the book Google Web ToolKit Applications.  Requires
 * web.xml changes too!
 * 
 * <p>Title: Cache Filter for GWT.</p>
 * @author Ryan Dewsbury
 * @version 1.0
 */ 
public class CacheFilter implements Filter
{
    private FilterConfig filterConfig;

    /**
     * Executes filter and caches all .CACHE files for a long, long time.
     */
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain filterChain) throws IOException, ServletException 
    {
        HttpServletRequest httpRequest = (HttpServletRequest)request;

        String requestURI = httpRequest.getRequestURI();
        if (requestURI.contains(".cache."))
        {
            HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setHeader("Cache-Control", "max-age=31536000");
        }
        filterChain.doFilter(request, response);
    }
    
    /**
     * Constructor to initialize filter.
     */
    public void init(FilterConfig filterConfig) throws ServletException
    {
        this.filterConfig = filterConfig;
    }
    
    /**
     * Destructor to destroy filter.
     */
    public void destroy()
    {
        this.filterConfig = null;
    }
}
