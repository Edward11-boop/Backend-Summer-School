package com.itsmartsystems.bookyourseat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestClient restClient() {
        // FIX: RestClient.create() fara configurare foloseste implicit un
        // ClientHttpRequestFactory bazat pe java.net.http.HttpClient (JDK),
        // care are un bug cunoscut: daca instanta HttpClient nu e tinuta cu o
        // referinta puternica corect gestionata, cereri asincrone scurte pot fi
        // intrerupte in mijlocul executiei ("Request was interrupted: null"),
        // exact simptomul observat la trimiterea catre webhook-urile n8n.
        // SimpleClientHttpRequestFactory (bazat pe HttpURLConnection) e stabil
        // si nu are aceasta problema.
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        return RestClient.builder()
                .requestFactory(factory)
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}