package com.example.monko.foreach;

/**
 * Created by amran on 4/24/2017.
 */

public class Comment {

    private String comment;
    private String username;

    public Comment(){

    }

    public Comment(String comment) {
        this.comment = comment;
        this.username=username;

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String desc) {
        this.comment = desc;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}
