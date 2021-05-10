package com.example.foodrescue.model;

public class Post {
    private int id;
    private byte[] image;
    private String title;
    private String description;
    private String date;
    private String time;
    private int quantity;
    private String location;
    private String email;

    public Post(int id, byte[] image, String title, String description, String date, String time, int quantity, String location, String email) {
        this.id = id;
        this.email = email;
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.location = location;
    }

    public Post(int id, byte[] image, String title, String description, String date, String time, int quantity, String location) {
        this.id = id;
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.location = location;
    }

    public Post(byte[] image, String title, String description, String date, String time, int quantity, String location, String email) {
        this.email = email;
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.location = location;
    }

    public Post(byte[] image, String title, String description, String date, String time, int quantity, String location) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.quantity = quantity;
        this.location = location;
    }

    public Post(String email) {
        this.email = email;
    }

    public Post() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
