package com.example.administrator.jsip;

public class Personal {
    private String nickname;
    private int id;
    //private String password;
    private int image_id;

    public Personal(String nickname,int id,int image_id){
        this.nickname = nickname;
        this.id = id;
        this.image_id = image_id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getId() {
        return id;
    }

    public int getImage_id() {
        return image_id;
    }

}
