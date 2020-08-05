package com.leonardorick.olx_clone.model;


import java.util.List;

public class Advert {
    private String id;
    private String state;
    private String category;
    private String title;
    private String desc;
    private String value;
    private String phone;
    private List<String> images;

    public Advert() { }

    public Advert(String id, String state, String category, String title, String desc, String value, String phone) {
        this.id = id;
        this.state = state;
        this.category = category;
        this.title = title;
        this.desc = desc;
        this.value = value;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

}
