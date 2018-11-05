package com.itgowo.httpclient.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public abstract class BaseRequestSyncClient implements Callable<HttpResponse> {
    private static final int FILE_BUFFER = 8192;
    protected URL url;
    protected String reqestStr;
    protected String requestMethod = "POST";
    protected String boundary = "---------------------7a8b9c0d1e2f3g";
    protected int timeout = 15000;
    protected HttpResponse httpResponse = new HttpResponse();
    protected String downloadDir = new File("").getAbsolutePath();
    protected Map<String, String> headers = new HashMap<>();
    protected onCallbackListener listener;
    protected List<File> uploadFiles;
    protected HttpURLConnection httpURLConnection;

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

    public BaseRequestSyncClient setUploadFiles(List<File> uploadFiles) {
        this.uploadFiles = uploadFiles;
        headers.put("Content-Type", "multipart/form-data;boundary=" + boundary);
        return this;
    }

    public void setKeepAlive() {
        headers.put("Connection", "Keep-Alive");
    }

    public void setContentType() {
        headers.put("Content-Type", "application/json");
    }

    public void setDownloadDir(String dir) {
        if (dir != null && dir.trim().length() > 0)
            this.downloadDir = dir;
    }

    protected HttpResponse request() throws IOException {
        reqestStr = prepare(this);
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
            DataOutputStream outputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            for (int i = 0; i < uploadFiles.size(); i++) {
                File uploadFile = uploadFiles.get(i);
                String filename = uploadFile.getName();
                File file = new File(filename);
                outputStream.writeBytes(twoHyphens + boundary + end);
                outputStream.writeBytes("Content-Disposition: form-data; " + "name=\"file" + i + "\";filename=\"" + file.getName() + "\"" + end);
                outputStream.writeBytes("Content-Type: application/octet-stream" + end);
                outputStream.writeBytes(end);
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
                    outputStream.write(buffer, 0, length);
                    process += length;
                    try {
                        listener.onProcess(uploadFile, count, process);
                    } catch (Exception e) {
                        listener.onError(httpResponse.setSuccess(false), e);
                    }
                }
                outputStream.writeBytes(end);
                /* close streams */
                fStream.close();
            }
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + end);
            /* close streams */
            outputStream.flush();
        } else if (reqestStr != null && !requestMethod.equalsIgnoreCase("GET")) {
            //建立输入流，向指向的URL传入参数
            bos = new BufferedWriter(new OutputStreamWriter(httpURLConnection.getOutputStream()));
            bos.write(reqestStr);
            bos.flush();
            bos.close();
        }
        //获得响应状态
        httpResponse.parse(httpURLConnection);
        File file = null;
        if (httpResponse.isSuccess()) {
            String ContentType = httpURLConnection.getHeaderField("Content-Type");
            if (ContentType != null && ContentType.contains("application/octet-stream")) {
                int fileLength = httpURLConnection.getContentLength();
                httpResponse.setIsDownloadFile(true);
                // 文件名
                String filePathUrl = httpURLConnection.getURL().getFile();
                String fileFullName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
                BufferedInputStream bin = new BufferedInputStream(httpURLConnection.getInputStream());
                String path = downloadDir + File.separatorChar + fileFullName;
                file = new File(path);
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                OutputStream out = new FileOutputStream(file);
                int size = 0;
                int len = 0;
                int bufferSize = FILE_BUFFER;
                if (fileLength > FILE_BUFFER * 100) {
                    bufferSize = fileLength / 100;
                    if (bufferSize > FILE_BUFFER * 100) {
                        bufferSize = FILE_BUFFER * 100;
                    }
                }
                byte[] buf = new byte[bufferSize];
                while ((size = bin.read(buf)) != -1) {
                    len += size;
                    out.write(buf, 0, size);
                    try {
                        listener.onProcess(file, fileLength, len);
                    } catch (Exception e) {
                        listener.onError(httpResponse.setSuccess(false), e);
                    }
                }
                bin.close();
                out.close();
                httpResponse.setDownloadFile(file);
            } else {
                httpResponse.setBody(httpURLConnection.getInputStream());
            }
        } else {
            //错误
            httpResponse.setBody(httpURLConnection.getErrorStream());
        }

        return httpResponse;
    }

    public BaseRequestSyncClient addHeader(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public BaseRequestSyncClient addHeaders(Map<String, String> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public BaseRequestSyncClient setReqestData(String data) {
        this.reqestStr = data;
        return this;
    }

    protected abstract String prepare(BaseRequestSyncClient baseRequestSyncClient);

    @Deprecated
    public HttpURLConnection getHttpURLConnection() {
        return httpURLConnection;
    }

    @Override
    public HttpResponse call() throws Exception {
        return request();
    }
}
