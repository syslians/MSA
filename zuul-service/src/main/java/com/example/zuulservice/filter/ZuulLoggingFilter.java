package com.example.zuulservice.filter;

import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@Component
public class ZuulLoggingFilter extends ZuulFilter {

    // Zuul 필터가 수행하는 로직을 정의하는 메서드.사용자가 요청할때마다 실행 된다.
    @Override
    public Object run() throws ZuulException {
        log.info("***************** printing logs: ");

        // 현재 요청에 대한 정보를 가져오기
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        log.info("***************** " + request.getRequestURI());

        // 실제 필터 로직이 수행된 후 반환하는 값 (여기서는 사용하지 않으므로 null 반환)
        return null;
    }

    // 필터의 종류를 정의하는 메서드 ("pre"는 사전 필터를 의미)
    @Override
    public String filterType() {
        return "pre";
    }

    // 필터의 실행 순서를 지정하는 메서드 (숫자가 낮을수록 먼저 실행)
    @Override
    public int filterOrder() {
        return 1;
    }

    // 필터를 실행할지 여부를 결정하는 메서드 (true일 경우 실행, false일 경우 무시)
    @Override
    public boolean shouldFilter() {
        return true;
    }

}
