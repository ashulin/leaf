# RestTemplate

## 执行流程

1.  构建URI；
2.  获取RequestFactory；
3.  构建ClientHttpRequest，获取拦截器；
4.  使用MessageConvert处理请求体；
5.  执行拦截器；
6.  执行请求；
7.  判断状态码；
8.  处理错误；
9.  使用MessageConvert包装Response；

## 配置类

```java
RestTemplateBuilderpublic class RestTemplateBuilder {
    private final boolean detectRequestFactory;
    private final String rootUri;
    // 消息转换器
    private final Set<HttpMessageConverter<?>> messageConverters;
    // 请求工厂
    private final Supplier<ClientHttpRequestFactory> requestFactorySupplier;
    // URI模板处理器
    private final UriTemplateHandler uriTemplateHandler;
    // HTTP错误处理器
    private final ResponseErrorHandler errorHandler;
    private final BasicAuthenticationInterceptor basicAuthentication;
    private final Set<RestTemplateCustomizer> restTemplateCustomizers;
    private final RestTemplateBuilder.RequestFactoryCustomizer requestFactoryCustomizer;
    // 拦截器链
    private final Set<ClientHttpRequestInterceptor> interceptors;
}
```

一般而言，我们会

-   自定义拦截器打印请求&响应日志；
-   自定义错误码处理；
-   自定义消息转换；
-   因为需要多次获取response.getBody()；而默认是一次性流，还需要自定义请求工厂，包裹请求；

### HttpMessageConverter

```

```

### ClientHttpRequestFactory

```java
    private final BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
```