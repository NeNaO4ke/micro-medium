package org.medium.gateway;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.medium.gateway.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WebFluxTest {


    @LoadBalanced
    public WebClient.Builder loadBalancedWebClientBuilder() {
        return WebClient.builder();
    }


    private final WebClient webClient = loadBalancedWebClientBuilder()
            .baseUrl("http://localhost:8080")
            .build();


    private final String USER_ID = "c5aee489-350c-4a36-92f7-820301acc7d4";
    private final String ARTICLE_ID = "b6991094-9b80-4d61-bd03-d92d3219253a";

    @Test
    void getToken() throws Exception {


        Mono<UserDTO> userDTOMono = webClient.post()
                .uri("/auth/login/" + USER_ID)
                .retrieve()
                .bodyToMono(UserDTO.class);
        UserDTO block = userDTOMono
                .block();

        System.out.println("Token " + block.getToken());

        System.out.println("Before upvote");
        webClient.get().uri("/api/article/" + ARTICLE_ID)
                .header("Authorization", "Bearer " + block.getToken())
                .retrieve()
                .bodyToMono(Article.class)
                .subscribe(System.out::println);

        Thread.sleep(300);

        webClient.post().uri("/api/article/upvote/" + ARTICLE_ID)
                .header("Authorization", "Bearer " + block.getToken())
                .retrieve()
                .bodyToMono(Article.class)
                .subscribe(System.out::println);

        Thread.sleep(300);

        System.out.println("After upvote");
        webClient.get().uri("/api/article/" + ARTICLE_ID)
                .header("Authorization", "Bearer " + block.getToken())
                .retrieve()
                .bodyToMono(Article.class)
                .subscribe(System.out::println);



    }

}
