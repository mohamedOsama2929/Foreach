package com.example.monko.foreach;

public class Post {

    private String desc;
    private String image;
    private String username;
    private String counter;

    public Post(){

    }

    public Post(String desc, String image,String counter) {
        this.desc = desc;
        this.image = image;
        this.username = username;
        this.counter = counter;

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

    public String getCounter() {
        return counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }
}
