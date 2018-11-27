package com.itgowo.httpclient.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class RequestClient {
    protected static final int FILE_BUFFER = 8192;
    protected URL url;
    protected String requestStr;
    protected String requestMethod = "POST";
    protected String boundary = "---------------------7a8b9c0d1e2f3g";
    protected int timeout = 15000;
    protected HttpResponse httpResponse = new HttpResponse();
    protected Map<String, String> headers = new HashMap<>();
    protected onCallbackListener listener;
    protected List<File> uploadFiles;
    protected HttpURLConnection httpURLConnection;

    public RequestClient(String url, String method, int timeout, onCallbackListener listener) {
        this.requestMethod = method;
        this.timeout = timeout;
        this.listener = listener;
        if (headers != null) {
            this.headers.putAll(headers);
        }
        if (url != null) {
            if (!url.startsWith("http://") & !url.startsWith("https://")) {
                url = "http://" + url;
            }
        }
        try {
            this.url = new URL(url);
        } catch (MalformedURLException e) {
            listener.onError(httpResponse.setSuccess(false), e);
        }
    }

    public RequestClient setUploadFiles(List<File> uploadFiles) {
        if (uploadFiles != null && !uploadFiles.isEmpty()) {
            this.uploadFiles = uploadFiles;
            headers.put("Content-Type", "multipart/form-data;boundary=" + boundary);
        }
        return this;
    }

    public void setKeepAlive() {
        headers.put("Connection", "Keep-Alive");
    }

    public void setContentType() {
        headers.put("Content-Type", "application/json");
    }

    protected HttpResponse request() throws IOException {
        requestStr = prepare(this);
        httpURLConnection = (HttpURLConnection) url.openConnection();

        //设置参数
        httpURLConnection.setDoOutput(!"GET".equalsIgnoreCase(requestMethod));     //需要输出
        httpURLConnection.setDoInput(true);      //需要输入
        httpURLConnection.setUseCaches(false);   //不允许缓存
        httpURLConnection.setRequestMethod(requestMethod);
        httpURLConnection.setReadTimeout(timeout);
        httpURLConnection.setRequestProperty("Accept", "text/html,application/json,application/octet-stream");

        //设置请求属性
        httpURLConnection.setRequestProperty("Charset", "UTF-8");

        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            httpURLConnection.setRequestProperty(header.getKey(), header.getValue());
        }

        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpURLConnection.connect();

        BufferedWriter bos = null;
        if (HttpMethod.POST.equalsIgnoreCase(requestMethod) && uploadFiles != null && !uploadFiles.isEmpty()) {
            String end = "\r\n";
            String twoHyphens = "--";
            OutputStream outputStream = httpURLConnection.getOutputStream() ;
            for (int i = 0; i < uploadFiles.size(); i++) {
                File uploadFile = uploadFiles.get(i);
                outputStream.write((twoHyphens + boundary + end).getBytes());
                outputStream.write(("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + uploadFile.getName()+"\"" + end).getBytes());
                outputStream.write(("Content-Type: application/octet-stream" + end).getBytes());
                outputStream.write(end.getBytes());
                FileInputStream fStream = new FileInputStream(uploadFile);
                int count = fStream.available();
                int bufferSize = FILE_BUFFER;
                if (count > FILE_BUFFER * 100) {
                    bufferSize = count / 100;
                    if (bufferSize > FILE_BUFFER * 100) {
                        bufferSize = FILE_BUFFER * 100;
                    }
                }
                int process = 0;
                byte[] buffer = new byte[bufferSize];
                int length = -1;
                while ((length = fStream.read(buffer)) != -1) {
                    httpURLConnection.getOutputStream().write(buffer, 0, length);
                    process += length;
                    try {
                        listener.onProcess(uploadFile, count, process);
                    } catch (Exception e) {
                        listener.onError(httpResponse.setSuccess(false), e);
                    }
                }
                outputStream.write(end.getBytes());
                /* close streams */
                fStream.close();
            }
            outputStream.write((twoHyphens + boundary + twoHyphens + end).getBytes());
            /* close streams */
            outputStream.flush();
        } else if (requestStr != null && requestStr.length() != 0 && !requestMethod.equalsIgnoreCase("GET")) {
            //建立输入流，向指向的URL传入参数
            bos = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            bos.write(requestStr);
            bos.flush();
            bos.close();
        }
        //获得响应状态
        httpResponse.parse(httpURLConnection);
        if (httpResponse.isSuccess()) {
            String ContentType = httpURLConnection.getHeaderField("Content-Type");
            if (ContentType != null && ContentType.contains("application/octet-stream")) {
                DownloadFile downloadFile = new DownloadFile(httpURLConnection, httpResponse, listener);
                httpResponse.setIsDownloadFile(true);

                httpResponse.setDownloadFile(downloadFile);
            } else {
                httpResponse.setBody(httpURLConnection.getInputStream());
            }
        } else {
            //错误
            httpResponse.setBody(httpURLConnection.getErrorStream());
        }
        return httpResponse;
    }

    public RequestClient addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public RequestClient addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public RequestClient setReqestData(String data) {
        this.requestStr = data;
        return this;
    }

    protected abstract String prepare(RequestClient requestClient);

    @Deprecated
    public HttpURLConnection getHttpURLConnection() {
        return httpURLConnection;
    }

}
