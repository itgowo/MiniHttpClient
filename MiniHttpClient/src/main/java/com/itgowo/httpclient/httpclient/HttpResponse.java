package com.itgowo.httpclient.httpclient;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class HttpResponse {
    private boolean isSuccess = true;
    private int responseCode = 200;
    private String responseMessage = "";
    private String contentType = "";
    private int contentLength;
    private byte[] body;
    private Map<String, String> headers = new HashMap<>();
    private String method;
    private boolean isDownloadFile;
    private File downloadFile;

    public File getDownloadFile() {
        return downloadFile;
    }

    public HttpResponse setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
        return this;
    }

    public boolean isDownloadFile() {
        return isDownloadFile;
    }

    public HttpResponse setIsDownloadFile(boolean downloadFile) {
        isDownloadFile = downloadFile;
        return this;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public HttpResponse setSuccess(boolean success) {
        isSuccess = success;
        return this;
    }

    protected void parse(HttpURLConnection urlConnection) throws IOException {
        responseCode = urlConnection.getResponseCode();
        responseMessage = urlConnection.getResponseMessage();
        contentType = urlConnection.getContentType();
        method = urlConnection.getRequestMethod();
        contentLength = urlConnection.getContentLength();
        if (responseCode >= 200 && responseCode <= 202) {
            setSuccess(true);
        } else {
            setSuccess(false);
        }
        for (Map.Entry<String, List<String>> stringListEntry : urlConnection.getHeaderFields().entrySet()) {
            String value = stringListEntry.getValue() == null || stringListEntry.getValue().isEmpty() ? "" : stringListEntry.getValue().get(0);
            headers.put(stringListEntry.getKey(), value);
        }
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public String getBodaStr() {
        return body == null ? "" : new String(body);
    }

    public HttpResponse setBody(byte[] body) {
        this.body = body;
        return this;
    }

    public HttpResponse setBody(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int count;
        while ((count = inputStream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, count);
        }
        this.body = outputStream.toByteArray();
        return this;
    }

    public String getMethod() {
        return method;
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
        sb.append(", body=").append(body==null?"null":new String(body));
        sb.append('}');
        return sb.toString();
    }
}