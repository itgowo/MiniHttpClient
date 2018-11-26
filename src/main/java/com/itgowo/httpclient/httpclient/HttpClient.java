package com.itgowo.httpclient.httpclient;

import java.io.File;
import java.util.List;
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
    private static final String TAG = "MiniHttpClient";
    private static int timeout = 30000;
    private static ExecutorService executorService = new ThreadPoolExecutor(0, 10, 30, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(), new ThreadFactory() {
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

    public static void RequestGet(String url, Map<String, String> headers, onCallbackListener listener) {
        RequestGetFile(url, headers, listener);
    }

    public static void RequestGetFile(String url, Map<String, String> headers, onCallbackListener listener) {
        Request(url, HttpMethod.GET, headers, null, listener);
    }

    public static void RequestPOST(String url, Map<String, String> headers, String requestJson, onCallbackListener listener) {
        Request(url, HttpMethod.POST, headers, requestJson, listener);
    }

    public static void Request(String url, HttpMethod method, Map<String, String> headers1, String requestJson, onCallbackListener listener) {
        executorService.execute(new RequestAsyncClient(url, method.getMethod(), timeout, listener) {
            @Override
            protected String prepare(RequestClient requestClient) {
                if (headers1 != null) {
                    requestClient.addHeaders(headers1);
                }
                return requestJson;
            }
        });
    }

    public static void RequestUploadFiles(String url, Map<String, String> headers1, List<File> uploadFiles1, onCallbackListener listener) {
        executorService.execute(new RequestAsyncClient(url, HttpMethod.POST.getMethod(), timeout, listener) {
            @Override
            protected String prepare(RequestClient requestClient) {
                if (headers1 != null) {
                    requestClient.addHeaders(headers1);
                }
                requestClient.setUploadFiles(uploadFiles1);
                return null;
            }
        });
    }

    public static HttpResponse RequestSync(String url, HttpMethod method, Map<String, String> headers1, List<File> uploadFiles1, String requestJson) throws Exception {
        RequestClient requestClient = new RequestClient(url, method.getMethod(), timeout, new onSimpleCallbackListener()) {
            @Override
            protected String prepare(RequestClient requestClient) {
                if (headers1 != null) {
                    requestClient.addHeaders(headers1);
                }
                requestClient.setUploadFiles(uploadFiles1);
                return requestJson;
            }
        };
        return requestClient.request();
    }

}
