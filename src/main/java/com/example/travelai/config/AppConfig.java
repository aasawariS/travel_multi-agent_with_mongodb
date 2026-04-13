package com.example.travelai.config;

import java.time.Clock;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

    @Bean
    MongoClient mongoClient(@Value("${travel.mongodb.uri}") String mongoUri) {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .build();
        return MongoClients.create(settings);
    }

    @Bean
    Clock systemClock() {
        return Clock.systemUTC();
    }

    @Bean
    WebClient voyageWebClient(WebClient.Builder builder,
                              @Value("${voyage.api.base-url:https://api.voyageai.com}") String baseUrl) {
        return builder.baseUrl(baseUrl).build();
    }
}
