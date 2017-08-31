package com.bozhong.document.filter;


import com.bozhong.common.util.ResultMessageBuilder;
import com.bozhong.document.common.DocumentLogger;
import com.bozhong.document.util.DocumentException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


public class ExceptionFilter implements Filter {
    public void doFilter(ServletRequest sReq, ServletResponse sRes, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) sReq;
        HttpServletResponse response = (HttpServletResponse) sRes;
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-Headers", "Cache-Control");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setContentType("text/json;charset=utf-8");
        try {
            chain.doFilter(sReq, sRes);
        } catch (Exception e) {
            DocumentLogger.getSysLogger().error(e.getMessage());
            if (e instanceof DocumentException) {
                DocumentException documentException = (DocumentException) e;
                PrintWriter writer = response.getWriter();
                writer.write(ResultMessageBuilder.build(false,
                        documentException.getErrorCode(), documentException.getErrorMessage()).toJSONString());
            }

        }
    }

    public void init(FilterConfig arg0) throws ServletException {

    }

    public void destroy() {

    }
}
