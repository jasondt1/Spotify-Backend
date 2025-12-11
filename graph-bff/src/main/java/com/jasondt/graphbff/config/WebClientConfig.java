package com.jasondt.graphbff.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .filter((request, next) ->
                        Mono.deferContextual(ctxView -> {
                            String auth = ctxView.hasKey("authToken") ? ctxView.get("authToken") : null;

                            ClientRequest newRequest = ClientRequest.from(request)
                                    .headers(headers -> {
                                        if (auth != null) {
                                            headers.set("Authorization", auth);
                                        }
                                    })
                                    .build();

                            return next.exchange(newRequest);
                        })
                )
                .build();
    }
}
