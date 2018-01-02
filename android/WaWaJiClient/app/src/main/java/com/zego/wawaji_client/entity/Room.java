package com.zego.wawaji_client.entity;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

public class Room implements Serializable {

    public String roomName;

    public String roomID;

    public ArrayList<String> streamList = new ArrayList<>();

    public int roomIcon;

}
