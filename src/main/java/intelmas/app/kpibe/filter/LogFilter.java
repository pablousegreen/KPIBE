package intelmas.app.kpibe.filter;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import intelmas.app.kpibe.tools.Utils;


@Component
public class LogFilter implements Filter{

	private static final Logger LOG = LoggerFactory.getLogger(LogFilter.class);
	
	public LogFilter() {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

		if(!(req instanceof HttpServletRequest) || !(res instanceof HttpServletResponse)) 
			throw new ServletException("Request or Response type is unsupported");
		
		ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper((HttpServletRequest) req);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper((HttpServletResponse) res);

	    responseWrapper.setHeader("Access-Control-Allow-Origin", "*");
	    responseWrapper.setHeader("Access-Control-Allow-Credentials", "true");
	    responseWrapper.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE");
	    responseWrapper.setHeader("Access-Control-Max-Age", "3600");
	    responseWrapper.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, Authorization");
	    
	    try{
	    	chain.doFilter(requestWrapper, responseWrapper);
	    } finally {
	    	String httpMethod = requestWrapper.getMethod();
	    	Enumeration<String> headerNames = requestWrapper.getHeaderNames();
	    	StringBuilder headerSb = new StringBuilder();
	    	while(headerNames.hasMoreElements()){
	    		String name = headerNames.nextElement();
	    		String value = requestWrapper.getHeader(name);
	    		headerSb.append("[").append(name).append(":").append(value).append("]");
	    	}
	    	
	    	if(StringUtils.equalsIgnoreCase(httpMethod, "POST")){
	    		String requestBody = new String(requestWrapper.getContentAsByteArray());
		        LOG.info("==> Receiving Request [Method:{}][Headers:{}][Path:{}][Body:{}]", 
		        		httpMethod, headerSb.toString(), requestWrapper.getRequestURI(), Utils.removeNewLine(requestBody));
	    	}else {
	    		String queryString = requestWrapper.getQueryString();
	    		LOG.info("==> Receiving Request [Method:{}][Headers:{}][Path:{}][QueryString:{}]", 
	    				httpMethod, headerSb.toString(), requestWrapper.getRequestURI(), queryString);
	    	}
	    	
	    	String responseBody = new String(responseWrapper.getContentAsByteArray());
	        LOG.info("<== Sending Response [Body:{}]", responseBody);
			
	    	responseWrapper.copyBodyToResponse();	    	
	    }
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
}
