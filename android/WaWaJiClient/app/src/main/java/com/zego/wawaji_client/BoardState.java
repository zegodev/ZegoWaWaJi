package com.zego.wawaji_client;

/**
 * Copyright © 2017 Zego. All rights reserved.
 */

public enum BoardState {

    Ended(0),

    Applying(1),

    WaitingBoard(2),

    ConfirmBoard(3),

    Boarding(4),

    WaitingGameResult(5);

    int mCode;

    BoardState(int code){
        mCode = code;
    }
}
