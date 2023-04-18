package com.atguigu.yygh.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.atguigu.yygh.common.utils.Result;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

//@Component
public class UnLoginFilter implements GlobalFilter, Ordered {
    private AntPathMatcher antPathMatcher = new AntPathMatcher();
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        String path = request.getURI().getPath();

        if(!antPathMatcher.match("/**/login",path)){
            //非登录，校验请求头参数
            List<String> stringList = request.getHeaders().get("X-Token");
            if (stringList == null || stringList.size() == 0){
                //拦截
               return out(exchange);
            }
        }

        return chain.filter(exchange);
    }

    //拦截
    private Mono<Void> out(ServerWebExchange exchange) {
        Result result = Result.error().message("请您先登录后再操作");
        ServerHttpResponse response = exchange.getResponse();

        byte[] bits = JSON.toJSONString(result).getBytes(StandardCharsets.UTF_8);
        DataBuffer buffer = response.bufferFactory().wrap(bits);
        //指定编码，否则在浏览器中会中文乱码
        response.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        return response.writeWith(Mono.just(buffer));
    }


    //优先级
    @Override
    public int getOrder() {
        return 0;
    }
}
