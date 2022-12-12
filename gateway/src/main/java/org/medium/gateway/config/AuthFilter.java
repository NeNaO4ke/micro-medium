package org.medium.gateway.config;

import org.medium.gateway.dto.UserDTO;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthFilter extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthFilter(WebClient.Builder webClientBuilder) {
        super(Config.class);
        this.webClientBuilder = webClientBuilder;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing authorization information");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);

            String[] parts = authHeader.split(" ");

            if (parts.length != 2 || !"Bearer".equals(parts[0])) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect authorization structure");
            }

            return webClientBuilder.build()
                    .get()
                    .uri("http://auth-service/auth/validateToken?token=" + parts[1])
                    .exchangeToMono(clientResponse -> {
                        if (clientResponse.statusCode().isError()) {
                            return clientResponse.bodyToMono(String.class).map(message -> {
                                throw new ResponseStatusException(clientResponse.statusCode(), message);
                            });
                        } else {
                            return clientResponse.bodyToMono(UserDTO.class)
                                    .map(userDto -> {
                                        exchange.getRequest()
                                                .mutate()
                                                .header("X-auth-user-id", String.valueOf(userDto.getId()));
     //                                           .header("X-auth-user-roles", String.join(" ", userDto.getRoles()));
                                        return exchange;
                                    }).flatMap(chain::filter);
                        }
                    });
        };
    }

    public static class Config {
    }
}
