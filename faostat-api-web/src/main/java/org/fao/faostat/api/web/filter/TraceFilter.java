package org.fao.faostat.api.web.filter;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
import org.fao.faostat.api.core.constants.OUTPUTTYPE;

import java.io.*;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

public class TraceFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(TraceFilter.class);

    @Override
    public void destroy() {

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // http://stackoverflow.com/questions/25638462/how-can-i-get-request-in-filter
       // RequestWrapper req = new RequestWrapper((HttpServletRequest) request);
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest r = (HttpServletRequest) request;

        // logging varibales
        String referer = r.getHeader("referer") != null? r.getHeader("referer") : "";
        String path = r.getPathInfo();
        String method = r.getMethod();

        // TODO: solve this issue with the payload.
        // POST /data/ is not retrieved in the request.getParameterMap()
        // but it has to be parsed in the RequestWrapper to be logged.
        // the output used by RequestWrapper payload is not optimal for the other requests
        if (request.getParameterMap().isEmpty()) {
            RequestWrapper req = new RequestWrapper((HttpServletRequest) request);
            String payload = req.getPayload();

            String s = path + ";" + referer + ";" + payload;
            this.log(path, s);

            try {
                chain.doFilter(req, resp);
            } catch (Exception ex) {
                // TODO: handle error
                //request.setAttribute("errorMessage", ex);
                //request.getRequestDispatcher("/WEB-INF/views/jsp/error.jsp").forward(request, response);
            }
        }else{

            ObjectWriter ow = new ObjectMapper().writer();
            String payload = ow.writeValueAsString(request.getParameterMap());

            String s = path + ";" + referer + ";" + payload;
            this.log(path, s);

            try {
                chain.doFilter(r, resp);
            } catch (Exception ex) {
                // TODO: handle error
                //request.setAttribute("errorMessage", ex);
                //request.getRequestDispatcher("/WEB-INF/views/jsp/error.jsp").forward(request, response);
            }
        }

    }

    private void log(String path, String message) {

        if(path.contains("/data/")) {
            // logging message
            LOGGER.info(message);
        }
    }
}