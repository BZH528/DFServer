package com.runone.config;


import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EsConfig {

    @Value("${spring.data.elasticsearch.cluster-nodes}")
    private String hosts;

    @Value("${spring.data.elasticsearch.port}")
    private int port;

    public String getHosts() {
        return hosts;
    }

    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Bean
    public RestHighLevelClient getClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost(this.getHosts(), this.getPort(), "http")));
    }

}
