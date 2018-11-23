##### A simple http client framework based on HttpURLConnection wrapper

[MiniHttpClient](https://github.com/itgowo/MiniHttpClient)

### 一：开发环境
Mac OS 10、Java 1.8、IDEA（Gradle工程）

### 二：介绍
基于Java HttpURLConnection实现Http客户端，支持普通接口请求和表单上传文件及文件下载等。使用线程池实现同步Future和异步请求。
### 三：特点
    
* 纯Java API实现，轻巧。
* 支持同步和异步两种方式。
* 独立线程发起请求，支持线程池管理。
* 支持POST表单数据和文件上传。
* 方便扩展，自定义功能。

### 四：引入([最新版本](https://bintray.com/itgowo/maven/MiniHttpClient))
1. Maven
```
<dependency>
  <groupId>com.itgowo</groupId>
  <artifactId>MiniHttpClient</artifactId>
  <version>0.0.26</version>
  <type>pom</type>
</dependency>
```

2. Gradle
```
implementation 'com.itgowo:MiniHttpClient:0.0.26'
```

### 五：简单使用(库Jar中有Demo类，可以参考)
[Demo.java](https://github.com/itgowo/MiniHttpClient/blob/master/src/main/java/com/itgowo/httpclient/httpclient/Demo.java)

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

### 六：说明
#### 1. 回调接口***onCallbackListener***

* ***public void onError(HttpResponse response, Exception e)***

|发生错误时回调||
|---|---|
|HttpResponse|请求结果，当http状态码不是200系列时，body可能包含错误信息|
|Exception|异常类|

* ***public void onSuccess(HttpResponse response) throws Exception***

|非文件下载请求成功回调||
|---|---|
|HttpRespons|请求结果，当http状态码是200系列时返回|

* ***public void onSuccess(HttpResponse httpResponse, DownloadFile file) throws Exception***

|文件下载请求成功回调||
|---|---|
|HttpResponse|请求结果，downloadFile为下载文件类，同第二个参数|
|DownloadFile|下载文件类，此时只读取了header信息，需要方法saveToFile(downloadDir)去保存文件|

* ***public void onProcess(File file, int countBytes, int processBytes) throws Exception***

|下载文件时：saveToFile(downloadDir)方法执行后，触发下载进度；上传文件时：触发上传进度||
|---|---|
|File|下载文件或上传文件|
|countBytes|文件大小|
|processBytes|文件大小|

#### 2. 请求方式

##### 分为同步请求和异步请求，功能全部封装到同步模块，异步调用只是继承封装一层，此库比较简单，按需继承改造。

### 七：原理解析

1. 以HttpURLConnection为基础开发，需要学会简单使用HttpURLConnection才能更好理解这个库。
2. 最核心请求类是BaseRequestSyncClient，实现了Callable接口，可以被线程池submit()调用，实现同步操作。BaseRequestSyncClient封装了request()方法开始请求，request()执行先触发prepare(BaseRequestSyncClient baseRequestSyncClient)方法，初始化请求参数。例如：
```
 public static void Request(String url, HttpMethod method, Map<String, String> headers1, String downloadDir1, String requestJson, onCallbackListener listener) {
        executorService.execute(new BaseRequestAsyncClient(url, method.getMethod(), timeout, listener) {
            @Override
            protected String prepare(BaseRequestSyncClient baseRequestSyncClient) {
                if (headers1 != null) {
                    baseRequestSyncClient.addHeaders(headers1);
                }
                setDownloadDir(downloadDir1);
                return requestJson;
            }

        });
    }
```
3. 通过prepare()可以设置header和上传文件和下载目录等。返回值是String类型，就是请求的Body部分，通常的接口请求json文本就是放到这里，请求json文本就不要设置上传文件了。如果是文件上传，需要这样定义：
```
           protected String prepare(BaseRequestSyncClient baseRequestSyncClient) {
                if (headers1 != null) {
                    baseRequestSyncClient.addHeaders(headers1);
                }
                baseRequestSyncClient.setUploadFiles(uploadFiles1);
                return null;
            }
```

4. 如果是文件下载，请求的body需不需要看你们接口定义，一般静态资源请求是GET方法，body为null，可以这样定义：
```
            protected String prepare(BaseRequestSyncClient baseRequestSyncClient) {
                if (headers1 != null) {
                    baseRequestSyncClient.addHeaders(headers1);
                }
                setDownloadDir(downloadDir1);
                return null;
            }
```

5. 如果用同步方法，只需要线程池submit就行，用FutureTask接收即可，如下：

```
FutureTask futureTask = (FutureTask) executorService.submit(new BaseRequestSyncClient(url, method.getMethod(), timeout, new onSimpleCallbackListener() {}
futureTask.get();
```
6. 说说异步类BaseRequestAsyncClient，其实只是实现了runnable接口，在run()方法中触发request(),然后判断执行回调，代码如下：

```
public abstract class BaseRequestAsyncClient extends BaseRequestSyncClient implements Runnable {
    public BaseRequestAsyncClient(String url, String method, int timeout, onCallbackListener listener) {
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
```

7. 具体请求内容请看代码，其中有各种判断解析，文件下载只认application/octet-stream，这点算是不足。文件下载还需要注意，执行success()回调不代表文件下载完了，在返回的DownloadFile对象中封装了输入流，需要执行saveToFile(下载目录)才能真正保存到文件，saveToFile()方法也会触发onProcess()进度回调。

### 八：小期待

以下项目都是我围绕远程控制写的项目和子项目。都给star一遍吧。😍

|项目(Github)|语言|其他地址|运行环境|项目说明|
|---|---|---|---|---|
|[RemoteDataControllerForWeb](https://github.com/itgowo/RemoteDataControllerForWeb)|JavaScript|[简书](https://www.jianshu.com/p/75747ff4667f)|浏览器|远程数据调试控制台Web端|
|[RemoteDataControllerForAndroid](https://github.com/itgowo/RemoteDataControllerForAndroid)|Java|[简书](https://www.jianshu.com/p/eb692f5709e3)|Android设备|远程数据调试Android端|
|[RemoteDataControllerForServer](https://github.com/itgowo/RemoteDataControllerForServer)|Java|[简书](https://www.jianshu.com/p/3858c7e26a98)|运行Java的设备|远程数据调试Server端|
|[MiniHttpClient](https://github.com/itgowo/MiniHttpClient)|Java|[简书](https://www.jianshu.com/p/41b0917271d3)|运行Java的设备|精简的HttpClient|
|[MiniHttpServer](https://github.com/itgowo/MiniHttpServer)|Java|[简书](https://www.jianshu.com/p/de98fa07140d)|运行Java的设备|支持部分Http协议的Server|
|[MiniLongConnectionServer](https://github.com/itgowo/MiniLongConnectionServer)|Java|[简书](https://www.jianshu.com/p/4b993100eae5)|运行Java的设备|TCP长连接库，支持粘包拆包处理|
|[PackageMessage](https://github.com/itgowo/PackageMessage)|Java|[简书](https://www.jianshu.com/p/8a4a0ba2f54a)|运行Java的设备|TCP粘包与半包解决方案|
|[ByteBuffer](https://github.com/itgowo/ByteBuffer)|Java|[简书](https://www.jianshu.com/p/ba68224f30e4)|运行Java的设备|二进制处理工具类|
|[DataTables.AltEditor](https://github.com/itgowo/DataTables.AltEditor)|JavaScript|[简书](https://www.jianshu.com/p/a28d5a4c333b)|浏览器|Web端表格编辑组件|

[我的小站：IT狗窝](http://itgowo.com)
技术联系QQ:1264957104
