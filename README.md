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
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```

2. Gradle
```
implementation 'com.itgowo:MiniHttpClient:0.0.1'
```

### 简单使用(发布到仓库的Jar中有Demo类，可以参考)
1. 接口请求
    
`MiniHttpServer httpNioServer = new MiniHttpServer();` 
    
    
2. 设置初始信息

 ```
 public void init(boolean isBlocking, InetSocketAddress inetSocketAddress, String webDir, onHttpListener onHttpListener)
 ```
 
|    参数    |       推荐值       |      说明     |
|---|---|---|
|   isBlocking  |     false    |是否用阻塞模式，推荐false，Nio特点就是非阻塞|
|inetSocketAddress|InetSocketAddress(port)|服务使用哪个端口|
|webDir|"/web"|服务器静态目录，file和temp目录会在webDir中|
|onHttpListener|new 实现类|服务器接收Http请求回调，如果是文件则FileList中有文件信息|


 
3. onHttpListener类

`public void onError(Throwable throwable)`

`public void onHandler(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception`



### 情景