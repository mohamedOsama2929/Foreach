package com.example.monko.foreach;

public class Post {

    private String desc;
    private String image;
    private String username;

    public Post(){

    }

    public Post(String desc, String image) {
        this.desc = desc;
        this.image = image;
        this.username = username;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
