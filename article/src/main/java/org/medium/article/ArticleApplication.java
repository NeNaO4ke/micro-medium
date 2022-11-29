package org.medium.article;

import io.sentry.Sentry;
import io.sentry.SentryLevel;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableDiscoveryClient
@EnableReactiveFeignClients
public class ArticleApplication {

	public static void main(String[] args) {
		Sentry.captureMessage("Article started", SentryLevel.DEBUG);
		SpringApplication.run(ArticleApplication.class, args);
	}

}
