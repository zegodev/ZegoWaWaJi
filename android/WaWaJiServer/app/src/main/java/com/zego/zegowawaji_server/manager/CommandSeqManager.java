package com.zego.zegowawaji_server.manager;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 02/11/2017.
 */

public class CommandSeqManager {
    static private CommandSeqManager sInstance = new CommandSeqManager();

    static private int sSequenceNumber = 1;

    private CommandSeqManager() {

    }

    static public CommandSeqManager getInstance() {
        return sInstance;
    }

    public synchronized int getAndIncreaseSequence() {
        if (sSequenceNumber >= Integer.MAX_VALUE) {
            sSequenceNumber = 1;
        }
        return sSequenceNumber++;
    }
}
