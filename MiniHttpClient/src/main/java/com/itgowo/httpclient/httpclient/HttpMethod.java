package com.itgowo.httpclient.httpclient;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class HttpMethod {
    private String method = "POST";
    private static final String[] methods = new String[]{"GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"};
    public static final HttpMethod GET = new HttpMethod(methods[0]);
    public static final HttpMethod POST = new HttpMethod(methods[1]);
    public static final HttpMethod HEAD = new HttpMethod(methods[2]);
    public static final HttpMethod OPTIONS = new HttpMethod(methods[3]);
    public static final HttpMethod PUT = new HttpMethod(methods[4]);
    public static final HttpMethod DELETE = new HttpMethod(methods[5]);
    public static final HttpMethod TRACE = new HttpMethod(methods[6]);

    public static HttpMethod parse(String method) {
        if (methods[0].equalsIgnoreCase(method)) {
            return GET;
        }
        if (methods[1].equalsIgnoreCase(method)) {
            return POST;
        }
        if (methods[2].equalsIgnoreCase(method)) {
            return HEAD;
        }
        if (methods[3].equalsIgnoreCase(method)) {
            return OPTIONS;
        }
        if (methods[4].equalsIgnoreCase(method)) {
            return PUT;
        }
        if (methods[5].equalsIgnoreCase(method)) {
            return DELETE;
        }
        if (methods[6].equalsIgnoreCase(method)) {
            return TRACE;
        }
        return new HttpMethod(method.toUpperCase());
    }

    private HttpMethod(String method) {
        this.method = method;
    }

    public boolean equalsIgnoreCase(String method) {
        return this.method.equalsIgnoreCase(method);
    }

    public String getMethod() {
        return method;
    }
}