package com.example.administrator.jsip;

public class LocalMessage {
    private String time;
    private String nickname;
    private String content;
    private int state;
    private int isMine;
    private String Id;
    private String Origin_Id;

    public LocalMessage(String time,String nickname,String content,int state,int isMine,String Id,String Origin_Id){
        this.time=time;
        this.nickname=nickname;
        this.content=content;
        this.state=state;
        this.isMine=isMine;
        this.Id=Id;
        this.Origin_Id=Origin_Id;
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

    public String getId() {
        return Id;
    }

    public String getOrigin_Id() {
        return Origin_Id;
    }
}
