package com.aractakip.mailcheck.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import javax.ws.rs.WebApplicationException;

import org.apache.commons.io.*;
import org.apache.log4j.Logger;

@WebFilter("/")
public class IPAccessFilter implements Filter {


    private static final Logger requestLogger = Logger.getLogger("requestLogger");
    private static final Logger responseLogger = Logger.getLogger("responseLogger");

    private String allowedIPs;

    private boolean isIPFilterOn;

    public void init(FilterConfig config) throws ServletException {

        //Gets allowedIPs parameter
        this.allowedIPs = config.getInitParameter("allowedIPs");
        this.isIPFilterOn = Boolean.valueOf(config.getInitParameter("isIPFilterOn"));

        //Prints the allowedIPs
        System.out.println("allowedIPs: " + this.allowedIPs);
        System.out.println("isIPFilterOn: " + this.isIPFilterOn);
    }

    public void destroy() {
      
    }


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException, WebApplicationException {

        List<String> allowedIPList = getAllowedIPList();

        //Unique identifier for logging the request and its response
        UUID uniqueId = UUID.randomUUID();

        //Creates new request and response to read values into given request and response
        ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest((HttpServletRequest) request);
        HttpServletResponseCopier responseCopier = new HttpServletResponseCopier((HttpServletResponse) response);

        if(this.isIPFilterOn && (allowedIPList == null || allowedIPList.isEmpty())){
            String msg = "allowedIps in ip-filter.properties not found. For security purposes, you are not allowed to access the application. Please contact system admin to resolve this issue.";
            requestLogger.warn(msg);
            //System.out.println(msg);
            responseCopier.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
        }else{
            String ipAddress =  ((HttpServletRequest) request).getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            }
            if(this.isIPFilterOn){
                if(!allowedIPList.contains(ipAddress)){
                    //Prevent access
                    String msg = "[" + ipAddress + "] is not found in the allowed ip addresses. Please contact system admin to resolve this issue.";
                    requestLogger.info(msg);
                    //System.out.println(msg);
                    responseCopier.sendError(HttpServletResponse.SC_UNAUTHORIZED, msg);
                }
            }


            String requestURL = getURL(wrappedRequest);
            //this.requestLogger(uniqueId,ipAddress, requestURL, wrappedRequest, response, chain);

            if (response.getCharacterEncoding() == null) {
                response.setCharacterEncoding("UTF-8"); // Or whatever default. UTF-8 is good for World Domination.
            }

            responseCopier.flushBuffer();
            if(responseCopier.getStatus() != HttpServletResponse.SC_UNAUTHORIZED){
                chain.doFilter(wrappedRequest, responseCopier);
            }

            //this.responseLogger(uniqueId, ipAddress, requestURL, responseCopier, wrappedRequest, response, chain);
        }
    }


    private ResettableStreamHttpServletRequest requestLogger(UUID uniqueId, String ipAddress, String requestURL, ResettableStreamHttpServletRequest wrappedRequest, ServletResponse response, FilterChain chain)  throws IOException, ServletException{
        String requestParams = "";
        Enumeration<String> params = wrappedRequest.getParameterNames();
        while(params.hasMoreElements()){
            String name = params.nextElement();
            String value = wrappedRequest.getParameter(name);
            requestParams +=" {"+name+"="+value+"},";
        }

        String formattedString = String.format("[%s] - IP: [%s] to URL: [%s], payload: [%s] - requestParams: [%s]" + System.getProperty( "line.separator" ),
                uniqueId.toString(),
                ipAddress,
                requestURL,
                getPayload(wrappedRequest),
                requestParams);

        requestLogger.info(formattedString);

        wrappedRequest.resetInputStream();

        return wrappedRequest;
    }

    private void responseLogger(UUID uniqueId, String ipAddress, String requestURL, HttpServletResponseCopier responseCopier,ServletRequest request,  ServletResponse response, FilterChain chain) throws UnsupportedEncodingException{
        byte[] copy = responseCopier.getCopy();
        String res = new String(copy, response.getCharacterEncoding());

        String formattedString = String.format("[%s] - IP: [%s] to URL: [%s],  status code [%d] , error message:  [%s], returned data: [%s]" + System.getProperty( "line.separator" ),
                uniqueId.toString(),
                ipAddress,
                requestURL,
                responseCopier.getStatus(),
                responseCopier.getErrorMessage() == null ? "" : responseCopier.getErrorMessage(),
                res);

        responseLogger.info(formattedString);
    }

    private String getPayload(HttpServletRequest req) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = req.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
            body = stringBuilder.toString();
            return body;
        } catch (IOException ex) {
            throw ex;
        }



    }

    private String getURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }


    public List<String> getAllowedIPList(){
        if(this.allowedIPs == null){
            return null;
        }
        return Arrays.asList(allowedIPs.split(";"));
    }

    public String getAllowedIPs() {
        return allowedIPs;
    }

    public void setAllowedIPs(String allowedIPs) {
        this.allowedIPs = allowedIPs;
    }


    /**********************ResettableStreamHttpServletRequest*****************/

    private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

        private byte[] rawData;
        private HttpServletRequest request;
        private ResettableServletInputStream servletStream;

        public ResettableStreamHttpServletRequest(HttpServletRequest request) {
            super(request);
            this.request = request;
            this.servletStream = new ResettableServletInputStream();
        }


        public void resetInputStream() {
            servletStream.stream = new ByteArrayInputStream(rawData);
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return servletStream;
        }


        @Override
        public BufferedReader getReader() throws IOException {
            if (rawData == null) {
                rawData = IOUtils.toByteArray(this.request.getReader());
                servletStream.stream = new ByteArrayInputStream(rawData);
            }
            return new BufferedReader(new InputStreamReader(servletStream));
        }

        private class ResettableServletInputStream extends ServletInputStream {

            private InputStream stream;

            @Override
            public int read() throws IOException {
                return stream.read();
            }
        }
    }

    /*****************************HttpServletResponseCopier********************************************/

    public class HttpServletResponseCopier extends HttpServletResponseWrapper {

        private ServletOutputStream outputStream;
        private PrintWriter writer;
        private ServletOutputStreamCopier copier;
        private int httpStatus;
        private String errorMessage;

        public HttpServletResponseCopier(HttpServletResponse response) throws IOException {
            super(response);
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            if (writer != null) {
                throw new IllegalStateException("getWriter() has already been called on this response.");
            }

            if (outputStream == null) {
                outputStream = getResponse().getOutputStream();
                copier = new ServletOutputStreamCopier(outputStream);
            }

            return copier;
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            if (outputStream != null) {
                throw new IllegalStateException("getOutputStream() has already been called on this response.");
            }

            if (writer == null) {
                copier = new ServletOutputStreamCopier(getResponse().getOutputStream());
                writer = new PrintWriter(new OutputStreamWriter(copier, getResponse().getCharacterEncoding()), true);
            }

            return writer;
        }

        @Override
        public void flushBuffer() throws IOException {
            if (writer != null) {
                writer.flush();
            } else if (outputStream != null) {
                copier.flush();
            }
        }

        public byte[] getCopy() {
            if (copier != null) {
                return copier.getCopy();
            } else {
                return new byte[0];
            }
        }

        @Override
        public void setStatus(int sc) {
            httpStatus = sc;
            super.setStatus(sc);
        }

        public int getStatus() {
            return httpStatus;
        }

        @Override
        public void sendError(int sc) throws IOException {
            httpStatus = sc;
            super.sendError(sc);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            httpStatus = sc;
            errorMessage = msg;
            super.sendError(sc, msg);
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }


    }

    public class ServletOutputStreamCopier extends ServletOutputStream {

        private OutputStream outputStream;
        private ByteArrayOutputStream copy;

        public ServletOutputStreamCopier(OutputStream outputStream) {
            this.outputStream = outputStream;
            this.copy = new ByteArrayOutputStream(1024);
        }

        @Override
        public void write(int b) throws IOException {
            outputStream.write(b);
            copy.write(b);
        }

        public byte[] getCopy() {
            return copy.toByteArray();
        }

    }

}
