package com.seccertificate.api.dto;

public class CustomerResponse {

    private Long id;
    private String name;
    private String apiKey;

    public CustomerResponse() {}

    public CustomerResponse(Long id, String name, String apiKey) {
        this.id = id;
        this.name = name;
        this.apiKey = apiKey;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
}
