package com.giacomozanatta.nmbr;

import java.io.Serializable;

/**
 * Created by giaco on 8/25/2017.
 */

public class Category implements Serializable{
    private String id;
    private String name;
    public Category(String id, String name){
        this.id=id;
        this.name=name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
