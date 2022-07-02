package com.example.diamondcare.Model;

import java.io.Serializable;

public class ServicesData implements Serializable {
    private String serviceTitle, serviceDescription, price;
    private Integer serviceImage;

    //Metodo construtor dos serviços
    public ServicesData(String serviceTitle, String serviceDescription, Integer serviceImage, String price) {
        this.serviceTitle = serviceTitle;
        this.serviceDescription = serviceDescription;
        this.serviceImage = serviceImage;
        this.price = price;
    }

    public String getServiceTitle() {
        return serviceTitle;
    }

    public void setServiceTitle(String serviceTitle) {
        this.serviceTitle = serviceTitle;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }

    public Integer getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(Integer serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
