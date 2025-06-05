package com.playdata.gatewayservice.filter;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class GlobalFilter
    // 제네릭에도 우리가 선언한 정적 클래스를 넣으면
        extends AbstractGatewayFilterFactory<GlobalFilter.Config> {

    // Filter 객체가 생성되서 Bean으로 등록 될때, 부모 클래스의 생성자에게
    // 이미 정적(static)으로 세팅된 특정 설정값을 전달합니다.
    public GlobalFilter() {

        super(Config.class);

    }

    @Override
    // override하는 메소드의 매개변수에 우리가 선언한 정적 클래스를 사용할 수 있음.
    public GatewayFilter apply(Config config) {
        // 부모는 전달받은 설정값을 필터가 동작할 때 (apply가 호출될 때)
        // 사용할 수 있도록 매개값으로 전달해줌.
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();

            log.info("Golbal Filter active! baseMessage = {}", config.getBaseMessage());
            if(config.isPreLogger()){
                log.info("Global Pre Filter active! {}", response.getStatusCode());
            }
            return chain.filter(exchange).then(Mono.fromRunnable(()->{
                if(config.isPostLogger()){
                    log.info("Global Post Filter active!", response.getStatusCode());
                }
            }));
        };
    }

    @Getter @Setter
    @ToString @NoArgsConstructor
    @AllArgsConstructor
    public static class Config {
        // 필터 동작을 동적으로 변경하거나 설정하기 위해 사용함 (선택사항)
        // 필터 동작을 YAML 설정 파일을 통해 외부에서 동적으로 설정할 수 있음.
        private String baseMessage;
        private boolean preLogger;
        private boolean postLogger;

    }
}
