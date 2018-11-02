package com.itgowo.httpclient.httpclient;

import java.util.Map;
import java.util.concurrent.*;

/**
 * @author lujianchao
 * 2018-10-15
 * Github：https://github.com/hnsugar
 * WebSite: http://itgowo.com
 * QQ:1264957104
 */
public class HttpClient {
    private static final String TAG = "itgowo-HttpClient";
    private static int timeout = 15000;
    private static ExecutorService executorService = new ThreadPoolExecutor(2, 15, 60 * 1000 * 3, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(TAG);
            return thread;
        }
    });

    public static void setTimeout(int timeout1) {
        timeout = timeout1;
    }

    public static void RequestGet(String url, Map<String, String> headers, String requestJson, onCallbackListener listener) {
        Request(url, HttpMethod.GET, headers, requestJson, listener);
    }

    public static void RequestPOST(String url, Map<String, String> headers, String requestJson, onCallbackListener listener) {
        Request(url, HttpMethod.POST, headers, requestJson, listener);
    }

    public static void Request(String url, HttpMethod method, Map<String, String> headers1, String requestJson, onCallbackListener listener) {
        executorService.execute(new BaseRequestAsyncClient(url, method.getMethod(), timeout, listener) {
            @Override
            protected String prepare(Map<String, String> headers) {
                if (headers1 != null) {
                    headers.putAll(headers1);
                }
                return requestJson;
            }
        });
    }

    public static HttpResponse RequestSync(String url, HttpMethod method, Map<String, String> headers1, String requestJson) throws Exception {
        FutureTask futureTask = (FutureTask) executorService.submit(new BaseRequestSyncClient(url, method.getMethod(),   timeout ,new onSimpleCallbackListener(){}) {
            @Override
            protected String prepare(Map<String, String> headers) {
                if (headers1 != null) {
                    headers.putAll(headers1);
                }
                return requestJson;
            }
        });
        return (HttpResponse) futureTask.get();
    }

}
