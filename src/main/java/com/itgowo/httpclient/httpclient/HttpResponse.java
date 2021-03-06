package com.itgowo.httpclient.httpclient;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class HttpResponse {
    public static final String JSON = "application/json";
    public static final String HTML = "text/html";
    public static final String TEXT = "text/plain";
    public static final String XML = "text/xml";
    public static final String JS = "application/javascript";
    public static final String CSS = "text/css";
    public static final String OBJECT = "application/octet-stream";
    private boolean isSuccess = true;
    private int responseCode = 200;
    private String responseMessage = "";
    private String contentType = "";
    private int contentLength;
    private byte[] body;
    private Map<String, String> headers = new HashMap<>();
    private String method;
    private boolean isDownloadFile;
    private DownloadFile downloadFile;
    private URL url;
    private HttpURLConnection httpURLConnection;
    private Map<String, String> parms = new HashMap<>();

    public DownloadFile getDownloadFile() {
        return downloadFile;
    }

    public HttpResponse setDownloadFile(DownloadFile downloadFile) {
        this.downloadFile = downloadFile;
        return this;
    }

    public boolean isDownloadFile() {
        return isDownloadFile;
    }

    public boolean isDownloadFile(String contentType) {
        if (contentType == null || contentType.trim().length() == 0) {
            return false;
        }
        if (contentType.contains(JSON) || contentType.contains(HTML) || contentType.contains(TEXT) || contentType.contains(XML)) {
            return false;
        }
        return true;
    }

    public HttpResponse setIsDownloadFile(String contentType) {
        isDownloadFile = isDownloadFile(contentType);
        return this;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public HttpResponse setSuccess(boolean success) {
        isSuccess = success;
        return this;
    }

    public Map<String, String> getParms() {
        return parms;
    }

    protected void parse(HttpURLConnection urlConnection) throws IOException {
        responseCode = urlConnection.getResponseCode();
        responseMessage = urlConnection.getResponseMessage();
        contentType = urlConnection.getContentType();
        method = urlConnection.getRequestMethod();
        contentLength = urlConnection.getContentLength();
        url = urlConnection.getURL();
        httpURLConnection = urlConnection;
        if (responseCode >= 200 && responseCode <= 202) {
            setSuccess(true);
        } else {
            setSuccess(false);
        }
        for (Map.Entry<String, List<String>> stringListEntry : urlConnection.getHeaderFields().entrySet()) {
            String value = stringListEntry.getValue() == null || stringListEntry.getValue().isEmpty() ? "" : stringListEntry.getValue().get(0);
            headers.put(stringListEntry.getKey(), value);
        }
        parms = parseParms(url.getQuery());
    }

    public URL getUrl() {
        return url;
    }

    public HttpURLConnection getHttpURLConnection() {
        return httpURLConnection;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodyStr() {
        return body == null ? "" : new String(body);
    }

    public HttpResponse setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpResponse setBody(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return this;
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int count;
        while ((count = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, count);
        }
        this.body = outputStream.toByteArray();
        if (!isSuccess) {
            this.responseMessage = new String(this.body);
        }
        return this;
    }

    public String getMethod() {
        return method;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public String getContentType() {
        return contentType;
    }

    public int getContentLength() {
        return contentLength;
    }

    public Map<String, String> parseParms(String uriParms) {
        Map<String, String> map = new HashMap<>();
        if (uriParms == null) {
            return map;
        }
        StringTokenizer parms = new StringTokenizer(uriParms, "&");
        while (parms.hasMoreTokens()) {
            String e = parms.nextToken();
            int sep = e.indexOf('=');
            if (sep >= 0) {
                map.put(e.substring(0, sep).trim(), e.substring(sep + 1));
            } else {
                map.put(e.trim(), "");
            }
        }
        return map;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpResponse{");
        sb.append("isSuccess=").append(isSuccess);
        sb.append(", responseCode=").append(responseCode);
        sb.append(", responseMessage='").append(responseMessage).append('\'');
        sb.append(", contentType='").append(contentType).append('\'');
        sb.append(", contentLength=").append(contentLength);
        sb.append(", isDownloadFile=").append(isDownloadFile);
        sb.append(", downloadFile=").append(downloadFile);
        sb.append(", method='").append(method).append('\'');
        sb.append(", headers=").append(headers);
        sb.append(", body=").append(body == null ? "null" : new String(body));
        sb.append('}');
        return sb.toString();
    }
}