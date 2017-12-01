package com.zego.zegowawaji_server;

/**
 * 信令通信指令格式定义。
 *
 * v1: 基本通信指令协议
 * v2: 增加获取游戏信息指令替代更新流扩展信息
 *
 * <p>Copyright © 2017 Zego. All rights reserved.</p>
 *
 * @author realuei on 30/10/2017.
 */

public class Constants {
    static public class Command {
        /**
         * {
         *     "seq": 1,
         *     "cmd": 257,
         *     "data": {
         *         "total": 0,
         *         "queue": [
         *             {
         *                 "id": "userId",
         *                 "name": "userName"
         *             },
         *         ],
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_USER_UPDATE = 0x101;    // 房间成员更新（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 258,
         *     "data": {
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "game_time": 30,
         *         "time_stamp'": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_GAME_READY = 0x102;     // 通知某人准备上机, 此时用户可使用 CMD_ABANDON_PLAY 放弃游戏（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 260,
         *     "data": {
         *         "result": 1,     # 1: 中奖，0: 未中奖
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_GAME_RESULT = 0x104;    // 通知游戏结果（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 272,
         *     "data": {
         *         "result": 1, # 0: 成功, 1: 失败
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "index": 1,
         *         "time_stamp": 12345,
         *         "seq": 1
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_APPOINTMENT_REPLY = 0x110;  // 回复收到预约申请，并告知预约结果（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 273,
         *     "data": {
         *         "time_stamp": 12345,
         *         "seq": 1
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_START_OR_ABANDON_GAME_REPLY = 0x111;    // 回复收到确认上机或者放弃玩游戏指令（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 274,
         *     "data": {
         *         "time_stamp": 12345,
         *         "seq": 1
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_CANCEL_APPOINTMENT_REPLY = 0x112;   // 回复收到取消预约指令（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 275,
         *     "data": {
         *         "total": 0,
         *         "queue": [
         *             {
         *                 "id": "userId",
         *                 "name": "userName"
         *             },
         *         ],
         *         "player": {
         *             "id": "userId",
         *             "name": "userName",
         *             "left_time":10
         *         },
         *         "game_time":30,
         *         "time_stamp":12345,
         *         "seq": 1
         *     }
         * }
         */
        static final public int CMD_GET_GAME_INFO_REPLY = 0x113;    // 回复获取房间信息指令，包括总人数，排队列表，当前谁在玩，单局游戏时长（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 513,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_APPOINTMENT = 0x201;          // 预约上机申请（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 514,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_CANCEL_APPOINTMENT = 0x202;   // 取消预约(暂时不用)（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 515,
         *     "data": {
         *         "confirm": 1,    # 1: YES, 0: NO
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_START_OR_ABANDON_GAME = 0x203;    // 确认上机或者放弃玩游戏，仅在正式开始玩之前发送此指令有效，即在收到服务端的 CMD_GAME_READY 指令时，通过该指令告诉服务端开始玩还是放弃（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 516,
         *     "data": {
         *         "time_stamp'": 12345,
         *         "seq": 1
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_GAME_READY_REPLY = 0x204; // 收到回复上机指令（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 517,
         *     "data": {
         *         "time_stamp": 12345,
         *         "seq":1
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_GAME_RESULT_REPLY = 0x205;  // 回复收到游戏结果（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 518,
         *     "data": {
         *         "time_stamp": 12345
         *     }
         *     "session_data": {}
         * }
         */
        static final public int CMD_GET_GAME_INFO = 0x206;  // 获取游戏信息，包括总人数，排队列表，当前谁在玩，单局游戏时长（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 528,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_MOVE_LEFT = 0x210;      // 左移指令（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 529,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_MOVE_RIGHT = 0x211;     // 右移指令（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 530,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_MOVE_FORWARD = 0x212;       // 前移指令（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 531,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_MOVE_BACKWARD = 0x213;    // 后移指令（Client-->Server）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 532,
         *     "data": {
         *         "time_stamp": 12345
         *     },
         *     "session_data": {}
         * }
         */
        static final public int CMD_GRAB = 0x214;           // 抓指令（Client-->Server）
    }


    static public class JsonKey {
        static final public String KEY_SEQ = "seq"; // 每条 广播/消息 的序号，回复时需要带上原 seq
        static final public String KEY_CMD = "cmd";
        static final public String KEY_DATA = "data";
        static final public String KEY_SESSION_DATA = "session_data";    // Session 数据，用于回传给发送端

        static final public String KEY_PLAYER = "player";   // 用户
        static final public String KEY_USER_ID = "id";    // 用户 ID
        static final public String KEY_USER_NAME = "name";    // 用户名

        static final public String KEY_USER_TOTAL = "total";    // 当前总人数
        static final public String KEY_ORDER_INDEX = "index";  // 当前排在第几位

        static final public String KEY_USER_QUEUE = "queue";    // 当前排队人员列表
        static final public String KEY_QUEUE_NUMBER = "queue_number";   // 当前排队人数
        static final public String KEY_GAME_TIME = "game_time";     // 单局游戏时长（单位：秒）
        static final public String KEY_LEFT_TIME = "left_time";     // 某人剩余游戏时长（单位：秒，主要用于H5断开重连时可以继续上一次游戏）

        static final public String KEY_RESULT = "result";  // 0: OK； 1： Failed
        static final public String KEY_CONFIRM = "confirm"; // 0: 确认；1：拒绝/放弃

        static final public String KEY_TIME_STAMP = "time_stamp";   // 时间戳 System.currentMillSeconds()

    }
}
