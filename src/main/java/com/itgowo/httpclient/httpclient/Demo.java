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
        testDownloadFile();
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
        headers.put("sign", "aaaaabbbbcccc");
        HttpClient.Request(url, HttpMethod.POST, headers, null, null, new onSimpleCallbackListener() {
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

    public static void testDownloadFile() {
        String downloadUrl = "http://file.itgowo.com/itgowo/RemoteDataController/web_app.zip";
        String url = "http://127.0.0.1:12111/app.js";
        HttpClient.RequestGetFile(downloadUrl, null, null, new onSimpleCallbackListener() {
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
            public void onSuccess(HttpResponse httpResponse, File file) throws Exception {
                System.out.println("httpResponse = [" + httpResponse + "], file = [" + file + "]");
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
