package com.itgowo.httpclient.httpclient;

import java.io.File;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        String url="http://127.0.0.1:12111";
        String url1="http://file.itgowo.com/itgowo/MiniHttpServer/version";
        HttpClient.RequestGet(url1,null,null,new onSimpleCallbackListener(){
            @Override
            public void onError(HttpResponse response, Exception e) {
                System.out.println("response = [" + response + "], e = [" + e + "]");
            }

            @Override
            public void onSuccess(HttpResponse response) {
                System.out.println("response = [" + response .toString()+ "]");
            }

            @Override
            public void onSuccess(HttpResponse httpResponse,File file) {
                System.out.println("httpResponse = [" + httpResponse + "], file = [" + file + "]");
            }

            @Override
            public void onProcess(File file, int countBytes, int processBytes) {
                System.out.println("file = [" + file + "], countBytes = [" + countBytes + "], processBytes = [" + processBytes + "]");
            }
        });

    }
}
