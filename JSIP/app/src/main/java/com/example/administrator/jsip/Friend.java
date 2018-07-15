package com.example.administrator.jsip;

public class Friend {
    public int ID;
    public String name;
    public int imageId;
    public int state;

    public Friend(int ID,String name,int imageId,int state){
        this.ID=ID;
        this.name=name;
        this.imageId=imageId;
        this.state=state;
    }

    public int getID(){
        return ID;
    }

    public String getName(){
        return name;
    }

    public int getImageId(){
        return imageId;
    }

    public int getState(){
        return state;
    }
}

