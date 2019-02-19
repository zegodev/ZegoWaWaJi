package com.zego.wawaji_client.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 24/10/2017.
 */

public class TimeUtil {
    static final private SimpleDateFormat sFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
    static final private SimpleDateFormat sLogFormat = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");

    static public String getNowTimeStr() {
        return sFormat.format(new Date());
    }

    static public String getLogStr() {
        return sLogFormat.format(new Date());
    }
}
