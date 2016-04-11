package com.spotcheck.entity;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by User on 4/11/2016.
 */

@Entity
public class Spot {
    @Id
    Long id;
    String name;
    String address;

    public Spot(){}

    public long getId(){return id;}
    public void setId(Long _id){this.id = _id;}

    public String getName(){return name;}
    public void setName(String _name){this.name = _name;}

    public String getAddress() {return address;}
    public void setAddress(String _address) {this.address = _address;}



}