package com.itgowo.httpclient.httpclient;

import java.io.File;

public interface onCallbackListener {
    void onError(HttpResponse httpResponse, Exception e);

    void onSuccess(HttpResponse httpResponse);

    void onSuccess(HttpResponse httpResponse, File file);

    void onProcess(File file, int countBytes, int processBytes);
}
