package com.itgowo.httpclient.httpclient;

import java.io.File;

public interface onCallbackListener {
    /**
     * 各种异常回调，包括http 状态码不是200系列
     *
     * @param httpResponse
     * @param e
     */
    void onError(HttpResponse httpResponse, Exception e);

    /**
     * 请求结果，不包括文件下载场景
     *
     * @param httpResponse
     * @throws Exception
     */
    void onSuccess(HttpResponse httpResponse) throws Exception;

    /**
     * 请求网络数据成功，文件下载
     *
     * @param httpResponse 请求结果，文件下载场景body为null
     * @param file         如果是文件下载会返回文件位置
     * @throws Exception
     */
    void onSuccess(HttpResponse httpResponse, DownloadFile file) throws Exception;

    /**
     * 文件表单上传还是文件下载场景都会触发进度，当文件过大时，触发频率将降低
     *
     * @param file
     * @param countBytes
     * @param processBytes
     * @throws Exception
     */
    void onProcess(File file, int countBytes, int processBytes) throws Exception;
}
