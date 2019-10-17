package com.hieufirebase.model;

import java.util.ArrayList;
import java.util.List;

public class Post {
    private String name;
    private String content;
    private List <String> image ;

    public Post(String name, String content, List <String> image) {
        this.name = name;
        this.content = content;
        this.image = image;
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

    public List <String> getList() {
        return image;
    }

    public void setList(List <String> image) {
        this.image = image;
    }
}
