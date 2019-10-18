package com.hieufirebase.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String name;
    private String content;


    public Post(String name, String content) {
        this.name = name;
        this.content = content;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


}
