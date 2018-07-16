package com.example.administrator.jsip;

public class LocalMessage {
    private String time;
    private String nickname;
    private String content;
    private int state;
    private int isMine;

    public LocalMessage(String time,String nickname,String content,int state,int isMine){
        this.time=time;
        this.nickname=nickname;
        this.content=content;
        this.state=state;
        this.isMine=isMine;
    }

    public String getNickname() {
        return nickname;
    }

    public int getIsMine() {
        return isMine;
    }

    public int getState() {
        return state;
    }

    public String getContent() {
        return content;
    }

    public String getTime() {
        return time;
    }

}
