package com.itgowo.httpclient.httpclient;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URLDecoder;

import static com.itgowo.httpclient.httpclient.RequestClient.FILE_BUFFER;

public class DownloadFile {
    /**
     * 文件流
     */
    private BufferedInputStream fileStream;
    private String originFileName;
    private File file;
    private int fileLength;
    private HttpResponse httpResponse;
    private onCallbackListener listener;

    public DownloadFile(HttpURLConnection httpURLConnection, HttpResponse httpResponse, onCallbackListener listener) throws IOException {
        this.fileLength = httpURLConnection.getContentLength();
        this.httpResponse = httpResponse;
        this.listener = listener;
        // 文件名
        this.originFileName = getHeaderFileName();
        if (this.originFileName == null || this.originFileName.equals("")) {
            String filePathUrl = httpURLConnection.getURL().getPath();
            filePathUrl = URLDecoder.decode(filePathUrl, "utf-8");
            this.originFileName = filePathUrl.substring(filePathUrl.lastIndexOf(File.separatorChar) + 1);
        }
        this.fileStream = new BufferedInputStream(httpURLConnection.getInputStream());

    }

    public String getOriginFileName() {
        return originFileName;
    }

    /**
     * 必须在saveToFile()之后获取才能得到正确值，推荐使用saveToFile()方法的返回值。
     *
     * @return
     */
    @Deprecated
    public File getFile() {
        return file;
    }

    public int getFileLength() {
        return fileLength;
    }

    /**
     * 指定存储位置，文件名以url的路径为准，如果文件已存在则自动拼接时间戳。
     *
     * @param downloadDir
     * @throws IOException
     */
    public File saveToFile(String downloadDir) throws IOException {
        return saveToFile(downloadDir, this.originFileName);
    }

    /**
     * 指定存储位置，并指定文件名，如果文件已存在则自动拼接时间戳。
     *
     * @param downloadDir
     * @param fileName
     * @throws IOException
     */
    public File saveToFile(String downloadDir, String fileName) throws IOException {
        File dir = new File(downloadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            file = new File(file.getParent(), System.currentTimeMillis() + "--" + file.getName());
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
        while ((size = fileStream.read(buf)) != -1) {
            len += size;
            out.write(buf, 0, size);
            try {
                listener.onProcess(file, fileLength, len);
            } catch (Exception e) {
                listener.onError(httpResponse.setSuccess(false), e);
            }
        }
        fileStream.close();
        out.close();
        this.file = file;
        return file;
    }

    /**
     * 从header中解析出文件信息
     *
     * @return
     */
    private String getHeaderFileName() {
        if (httpResponse.getHeaders()==null){
            return "";
        }
        String dispositionHeader = httpResponse.getHeaders().get("Content-Disposition");
        if (dispositionHeader != null && dispositionHeader.trim().length() > 0) {
            dispositionHeader.replace("attachment;filename=", "");
            dispositionHeader.replace("filename*=utf-8", "");
            String[] strings = dispositionHeader.split("; ");
            if (strings.length > 1) {
                dispositionHeader = strings[1].replace("filename=", "");
                dispositionHeader = dispositionHeader.replace("\"", "");
                return dispositionHeader;
            }
            return "";
        }
        return "";
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DownloadFile{");
        sb.append("originFileName='").append(originFileName).append('\'');
        sb.append(", file=").append(file);
        sb.append(", fileLength=").append(fileLength);
        sb.append('}');
        return sb.toString();
    }
}
