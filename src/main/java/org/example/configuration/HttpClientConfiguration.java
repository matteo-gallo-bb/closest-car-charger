package org.example.configuration;

import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.http.HttpClient;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class HttpClientConfiguration {

    private long connectionTimeoutInMillis = 5000;

    @Bean
    public HttpClient httpClient(Executor httpClientThreadPoolExecutor) {
        return HttpClient.newBuilder()
                .connectTimeout(Duration.of(connectionTimeoutInMillis, ChronoUnit.MILLIS))
                .executor(httpClientThreadPoolExecutor)
                .build();
    }

    @Bean
    public Executor httpClientThreadPoolExecutor() {
        return Executors.newFixedThreadPool(4);
    }

    @Bean
    public JSONParser jsonParser() {
        return new JSONParser();
    }

    public long getConnectionTimeoutInMillis() {
        return connectionTimeoutInMillis;
    }

    @Value("${httpclient.connection.timeout:5000}")
    public void setConnectionTimeoutInMillis(long connectionTimeoutInMillis) {
        this.connectionTimeoutInMillis = connectionTimeoutInMillis;
    }
}
