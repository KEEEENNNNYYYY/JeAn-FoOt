package com.fifa.app.Configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Getter
@Configuration
public class ChampionshipClient {

    private WebClient webClient;

    public ChampionshipClient() {
        this.webClient = WebClient.create("http://localhost:8080/");
    }

}
