package com.itgowo.httpclient.httpclient;

public abstract class RequestAsyncClient extends RequestClient implements Runnable {
    public RequestAsyncClient(String url, String method, int timeout, onCallbackListener listener) {
        super(url, method, timeout, listener);
    }

    @Override
    public void run() {
        try {
            request();
            if (httpResponse.isSuccess()) {
                if (httpResponse.isDownloadFile()) {
                    listener.onSuccess(httpResponse, httpResponse.getDownloadFile());
                } else {
                    listener.onSuccess(httpResponse);
                }
            } else {
                listener.onError(httpResponse, new Exception("code:" + httpResponse.getResponseCode() + "  msg:" + httpResponse.getResponseMessage()));
            }
        } catch (Exception e) {
            listener.onError(httpResponse.setSuccess(false), e);
        }
    }
}
