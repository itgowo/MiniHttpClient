package com.itgowo.httpclient.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class BaseRequestSyncClient implements Callable<HttpResponse> {
    protected URL url;
    protected String reqestStr;
    protected String requestMethod = "POST";
    protected String boundary = "---------------------7a8b9c0d1e2f3g";
    protected int timeout = 15000;
    protected HttpResponse httpResponse = new HttpResponse();
    protected String downloadDir = new File("").getAbsolutePath();
    protected Map<String, String> headers = new HashMap<>();
    protected onCallbackListener listener;
    protected File uploadFile;

    public BaseRequestSyncClient(String url, String method, int timeout, onCallbackListener listener) {
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

    public BaseRequestSyncClient setUploadFile(File uploadFile) {
        this.uploadFile = uploadFile;
        headers.put( "Content-Type", "multipart/form-data;boundary=" + boundary);
        return this;
    }

    public void setKeepAlive() {
        headers.put("Connection", "Keep-Alive");
    }

    public void setContentType() {
        headers.put("Content-Type", "application/json");
    }

    public void setDownloadDir(String dir) {
        this.downloadDir = dir;
    }

    protected HttpResponse request() throws IOException {
        reqestStr = prepare(headers);
        HttpURLConnection httpConn = null;
        httpConn = (HttpURLConnection) url.openConnection();

        //设置参数
        httpConn.setDoOutput(true);     //需要输出
        httpConn.setDoInput(true);      //需要输入
        httpConn.setUseCaches(false);   //不允许缓存
        httpConn.setRequestMethod(requestMethod);      //设置POST方式连接
        httpConn.setReadTimeout(timeout);

        //设置请求属性
        httpConn.setRequestProperty("Charset", "UTF-8");

        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            httpConn.setRequestProperty(header.getKey(), header.getValue());
        }

        //连接,也可以不用明文connect，使用下面的httpConn.getOutputStream()会自动connect
        httpConn.connect();

        BufferedWriter bos = null;
        if (reqestStr != null && !requestMethod.equalsIgnoreCase("GET")) {
            //建立输入流，向指向的URL传入参数
            bos = new BufferedWriter(new OutputStreamWriter(httpConn.getOutputStream()));
            bos.write(reqestStr);
            bos.flush();
            bos.close();
        }
        //获得响应状态
        httpResponse.parse(httpConn);
        File file = null;
        if (httpResponse.isSuccess()) {
            String ContentType = httpConn.getHeaderField("Content-Type");
            if (ContentType != null && ContentType.contains("application/octet-stream")) {
                int fileLength = httpConn.getContentLength();
                httpResponse.setIsDownloadFile(true);
                // 文件名
                String filePathUrl = httpConn.getURL().getFile();
                String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
                BufferedInputStream bin = new BufferedInputStream(httpConn.getInputStream());
                String path = downloadDir + File.separatorChar + fileFullName;
                file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                OutputStream out = new FileOutputStream(file);
                int size = 0;
                int len = 0;
                byte[] buf = new byte[8192];
                while ((size = bin.read(buf)) != -1) {
                    len += size;
                    out.write(buf, 0, size);
                    listener.onProcess(file, fileLength, len);
                }
                bin.close();
                out.close();
                httpResponse.setDownloadFile(file);
            } else {
                httpResponse.setBody(httpConn.getInputStream());
            }
        } else {
            //错误
            httpResponse.setBody(httpConn.getErrorStream());
        }

        return httpResponse;
    }

    protected abstract String prepare(Map<String, String> headers);

    @Override
    public HttpResponse call() throws Exception {
        return request();
    }
}
