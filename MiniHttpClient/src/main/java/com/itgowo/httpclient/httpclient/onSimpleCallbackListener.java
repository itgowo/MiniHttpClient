package com.itgowo.httpclient.httpclient;

import java.io.File;

public class onSimpleCallbackListener implements onCallbackListener {
    @Override
    public void onError(HttpResponse httpResponse, Exception e) {

    }

    @Override
    public void onSuccess(HttpResponse httpResponse) {

    }

    @Override
    public void onSuccess(HttpResponse httpResponse, File file) {

    }

    @Override
    public void onProcess(File file, int countBytes, int processBytes) {

    }
}
