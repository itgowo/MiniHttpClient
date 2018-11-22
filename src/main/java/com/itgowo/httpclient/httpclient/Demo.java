package com.itgowo.httpclient.httpclient;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        String url = "http://127.0.0.1:12111/app.js";
        String url1 = "http://file.itgowo.com/itgowo/MiniHttpServer/version";
        for (int i = 0; i < 100; i++) {
            testDownloadFile();
        }
    }

    public static void testUploadFile() {
        String url = "http://127.0.0.1:12111/app.js";
        List<File> files = new ArrayList<>();
        File file = new File("/Users/lujianchao/Desktop/RDC1.png");
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

    public static void testRequest() {
        String url = "http://127.0.0.1:12111/app.js";
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        String httpBody = "{\"name\":\"张三\"}";
        HttpClient.Request(url, HttpMethod.POST, headers, null, httpBody, new onSimpleCallbackListener() {
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
    public static void testRequestSync() {
        String url = "http://127.0.0.1:12111/app.js";
        Map<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/json");
        String httpBody = "{\"name\":\"张三\"}";
        try {
          HttpResponse response=  HttpClient.RequestSync(url, HttpMethod.POST, headers,   httpBody);
            System.out.println(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testDownloadFile() {
        String downloadUrl = "http://file.itgowo.com/itgowo/RemoteDataController/web_app.zip";
        String downloadDir = "temp";
        HttpClient.RequestGetFile(downloadUrl, null, downloadDir, new onSimpleCallbackListener() {
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
            HttpResponse response = HttpClient.RequestSync(url, HttpMethod.GET, null, null);
            System.out.println(response.getDownloadFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
