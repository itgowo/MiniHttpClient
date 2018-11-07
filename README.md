# MiniHttpClient
##### A simple http client framework based on HttpURLConnection wrapper

[最新版本](https://bintray.com/itgowo/maven/MiniHttpClient)

 *  Github:https://github.com/hnsugar
 *  Github:https://github.com/itgowo
 *  website:http://itgowo.com
 *  QQ:1264957104
### 开发环境
    Mac OS 10、Java 1.8、IDEA（Gradle工程）

### 介绍
        基于Java HttpURLConnection实现Http客户端，支持普通接口请求和表单上传文件及文件下载等。
### 特点
    
* 纯Java API实现，没有引入依赖。
* 支持同步和异步两种方式。
* 独立线程发起请求。
* 支持POST表单数据和文件上传。
* 可以扩展，继承BaseRequestSyncClient，在prepare()中操作header等。

### 引入
1. Maven
```
<dependency>
  <groupId>com.itgowo</groupId>
  <artifactId>MiniHttpClient</artifactId>
  <version>0.0.8</version>
  <type>pom</type>
</dependency>
```

2. Gradle
```
implementation 'com.itgowo:MiniHttpClient:0.0.8'
```

### 简单使用(发布到仓库的Jar中有Demo类，可以参考)
1. 回调接口
    
```
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
            void onSuccess(HttpResponse httpResponse, File file) throws Exception;
        
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
```
    
    
2. 请求方式

##### 分为同步请求和异步请求，功能全部封装到同步模块，异步调用只是继承封装一层，此库比较简单，按需继承改造。



### 情景

#### 模拟表单上传，POST方式
```
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
```
#### 提交数据

```
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
```
#### 文件下载，Android使用注意必须设置下载目录，Java默认目录为"/"
```
    public static void testDownloadFile() {
        String downloadUrl = "http://file.itgowo.com/itgowo/RemoteDataController/web_app.zip";
         String downloadDir = "/temp";
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
            public void onSuccess(HttpResponse httpResponse, File file) throws Exception {
                System.out.println("httpResponse = [" + httpResponse + "], file = [" + file + "]");
            }

            @Override
            public void onProcess(File file, int countBytes, int processBytes) throws Exception {
                System.out.println("file = [" + file + "], countBytes = [" + countBytes + "], processBytes = [" + processBytes + "]");
            }
        });
    }
```
#### 同步方式下载文件，文件对象放在HttpResponse中的downloadFile里
```
    public static void testSyncDownloadFile() {
        try {
            String url = "http://127.0.0.1:12111/app.js";
            HttpResponse response = HttpClient.RequestSync(url, HttpMethod.GET, null, null);
            System.out.println(response.getDownloadFile());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
```