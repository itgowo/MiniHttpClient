package com.itgowo.httpclient.httpclient;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        String url = "http://127.0.0.1:12111/app.js";
        String url1 = "http://file.itgowo.com/itgowo/MiniHttpServer/version";
        try {
          String s=  URLEncoder.encode("奥奥 (3).xml","utf-8");
            System.out.println(s);
           s= URLDecoder.decode(s,"utf-8");
            System.out.println(s);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步上传文件
     */
    public static void testUploadFile() {
        String url = "http://127.0.0.1:16670/";
        List<File> files = new ArrayList<>();
        File file = new File("/Users/lujianchao/Downloads/单独.xlsx");
        files.add(file);
        HttpClient.RequestUploadFiles(url, null, files, new onSimpleCallbackListener() {
            @Override
            public void onError(HttpResponse httpResponse, Exception e) {
                System.out.println("httpResponse = [" + httpResponse + "], e = [" + e + "]");
            }

            @Override
            public void onProcess(File file, int countBytes, int processBytes) throws Exception {
                System.out.println("file = [" + file + "], countBytes = [" + countBytes + "], processBytes = [" + processBytes + "]");
            }

            @Override
            public void onSuccess(HttpResponse httpResponse) throws Exception {
                System.out.println("httpResponse = [" + httpResponse + "]");
            }
        });
    }

    /**
     * 异步请求
     */
    public static void testRequest() {
        String url = "http://127.0.0.1:12111/app.js";
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        String httpBody = "{\"name\":\"张三\"}";
        HttpClient.Request(url, HttpMethod.POST, headers, httpBody, new onSimpleCallbackListener() {
            @Override
            public void onError(HttpResponse response, Exception e) {
                e.printStackTrace();
                System.out.println("response = [" + response + "], e = [" + e + "]");
            }

            @Override
            public void onSuccess(HttpResponse response) throws Exception {
                System.out.println("response = [" + response + "]");
            }

        });
    }

    /**
     * 同步请求
     */
    public static void testRequestSync() {
        String url = "http://127.0.0.1:12111/app.js";
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        String httpBody = "{\"name\":\"张三\"}";
        try {
            HttpResponse response = HttpClient.RequestSync(url, HttpMethod.POST, headers, null, httpBody);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 异步文件下载
     */
    public static void testDownloadFile() {
        String downloadUrl = "http://127.0.0.1:16670/upload/%E5%8D%95%E7%8B%AC+%284%29.xlsx";
        String downloadDir = "temp";
        HttpClient.RequestGetFile(downloadUrl, null, new onSimpleCallbackListener() {
            @Override
            public void onError(HttpResponse response, Exception e) {
                e.printStackTrace();
                System.out.println("response = [" + response + "], e = [" + e + "]");
            }

            @Override
            public void onSuccess(HttpResponse response) throws Exception {
                //下载文件不会触发
                System.out.println("response = [" + response + "]");
            }

            @Override
            public void onSuccess(HttpResponse httpResponse, DownloadFile file) throws Exception {
                file.saveToFile(downloadDir);
                System.out.println("httpResponse = [" + httpResponse + "], file = [" + file.getFile() + "]");
            }

            @Override
            public void onProcess(File file, int countBytes, int processBytes) throws Exception {
                System.out.println("file = [" + file + "], countBytes = [" + countBytes + "], processBytes = [" + processBytes + "]");
            }
        });
    }

    public static void testSyncDownloadFile() {
        try {
            String url = "http://127.0.0.1:12111/app.js";
            HttpResponse response = HttpClient.RequestSync(url, HttpMethod.GET, null, null, null);
            System.out.println(response.getDownloadFile().getOriginFileName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 同步上传文件
     * @throws Exception
     */
    public static void testSyncUploadFile() throws Exception {
        String url = "http://127.0.0.1:12111/app.js";
        List<File> files = new ArrayList<>();
        File file = new File("/Users/lujianchao/Desktop/RDC1.png");
        files.add(file);
        HttpResponse httpResponse = HttpClient.RequestSync(url, HttpMethod.POST, null, files, null);
        if (httpResponse.isSuccess()) {
            System.out.println("上传成功");
        }
    }
}
