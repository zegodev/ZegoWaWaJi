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
    static final public int DEFAULT_GAME_TIME_IN_SECONDS = 30;    // seconds

    static public class Command {
        /**
         * {
         *     "seq": 13,
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
         *     }
         * }
         */
        static final public int CMD_USER_UPDATE = 0x101;    // 游戏信息更新（Server-->Client）

        /**
         * {
         *     "seq": 3,
         *     "cmd": 258,
         *     "session_id": "xxx",
         *     "data": {
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "game_time": 30,
         *         "time_stamp'": 12345
         *     }
         * }
         */
        static final public int CMD_GAME_READY = 0x102;     // 通知某人准备上机, 此时用户可使用 CMD_ABANDON_PLAY 放弃游戏（Server-->Client）

        /**
         * {
         *     "seq": 11,
         *     "cmd": 260,
         *     "session_id": "xxx",
         *     "data": {
         *         "result": 1,     // 1: 中奖，0: 未中奖
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "time_stamp": 12345,
         *         "encrypted_result": "xxx"    // 结果校验串
         *     }
         * }
         */
        static final public int CMD_GAME_RESULT = 0x104;    // 通知游戏结果（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 272,
         *     "data": {
         *         "result": 1, // 0: 成功, 1: 失败, 2: 读取初始化信息错误
         *         "player": {
         *             "id": "userId",
         *             "name": "userName"
         *         },
         *         "index": 1,
         *         "time_stamp": 12345,
         *         "session_id": "xxx" // 此次抓娃娃的 session_id，后续指定都要带上这个 session_id
         *     }
         * }
         */
        static final public int CMD_APPOINTMENT_REPLY = 0x110;  // 回复收到预约申请，并告知预约结果（Server-->Client）

        /**
         * {
         *     "seq": 4,
         *     "cmd": 273,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_START_OR_ABANDON_GAME_REPLY = 0x111;    // 回复收到确认上机或者放弃玩游戏指令（Server-->Client）

        /**
         * {
         *     "seq": 2,
         *     "cmd": 274,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_CANCEL_APPOINTMENT_REPLY = 0x112;   // 回复收到取消预约指令（Server-->Client）

        /**
         * {
         *     "seq": 12,
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
         *             "left_time": 10
         *         },
         *         "game_time": 30,
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_GET_GAME_INFO_REPLY = 0x113;    // 回复获取房间信息指令，包括总人数，排队列表，当前谁在玩，单局游戏时长（Server-->Client）

        /**
         * {
         *     "seq": 1,
         *     "cmd": 513,
         *     "session_id": "xxx",   // 当为接着玩时，需要把上一局游戏时的 session_id 带上来，默认为空
         *     "data": {
         *         "time_stamp": 12345,
         *         "config": "encrypted-by-shared-secret-key",    // 娃娃机初始化配置信息
         *         "continue": 1   // 1: 接着玩; 0: 放弃，默认为 0
         *     }
         * }
         */
        static final public int CMD_APPOINTMENT = 0x201;          // 预约上机申请（Client-->Server）

        /**
         * {
         *     "seq": 2,
         *     "cmd": 514,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_CANCEL_APPOINTMENT = 0x202;   // 取消预约(暂时不用)（Client-->Server）

        /**
         * {
         *     "seq": 4,
         *     "cmd": 515,
         *     "session_id": "xxx",
         *     "data": {
         *         "confirm": 1,    // 1: 确认上机, 0: 放弃
         *         "config": "encrypted-by-shared-secret-key",    // 娃娃机初始化配置信息
         *         "time_stamp": 12345
         *     }
         * }
         *
         * 其中 config 的明文格式：
         * {
         *    "game_config": {
         *        "game_time": 30,        // 游戏总时长
         *        "claw_power_grab": 67,  // 表示抓起爪力(1—100)，指下爪时，抓住娃娃的爪力，建议这个值设置大一点
         *        "claw_power_up": 33,    // 表示到顶爪力(1—100)，指天车提起娃娃到 up_height 指定的高度后将使用该爪力值直至天车到达顶部
         *        "claw_power_move": 21,  // 表示移动爪力(1—100)，指天车到达顶部后，移动过程中的爪力
         *        "up_height": 7          // 抓起高度（0–10）底部到顶部分成10份，爪子到达该值指定的高度时就会将爪力减小至到顶爪力
         *     },
         *     "authority_info": {
         *        "session_id": "xxx",    // 同信令中 session_id 值
         *        "confirm": 1,           // 同信令中 confirm 值
         *        "time_stamp": 1515508734166,    // 同信令中 time_stamp 值
         *        "custom_token": "xxx"   // 业务侧自定义鉴权信息，会在游戏结果加密串中带回，用于实现自定义校验, 如支付信息等，该字段长度不要超过 300 个字符
         *     }
         * }
         */
        static final public int CMD_START_OR_ABANDON_GAME = 0x203;    // 确认上机或者放弃玩游戏，仅在正式开始玩之前发送此指令有效，即在收到服务端的 CMD_GAME_READY 指令时，通过该指令告诉服务端开始玩还是放弃（Client-->Server）

        /**
         * {
         *     "seq": 3,
         *     "cmd": 516,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp'": 12345
         *     }
         * }
         */
        static final public int CMD_GAME_READY_REPLY = 0x204; // 收到回复上机指令（Client-->Server）

        /**
         * {
         *     "seq": 11,
         *     "cmd": 517,
         *     "session_id": "xxx",
         *     "data": {
         *         "continue": 1,   // 1: 接着玩; 0: 放弃
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_GAME_RESULT_REPLY = 0x205;  // 回复收到游戏结果（Client-->Server）

        /**
         * {
         *     "seq": 12,
         *     "cmd": 518,
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_GET_GAME_INFO = 0x206;  // 获取游戏信息，包括总人数，排队列表，当前谁在玩，单局游戏时长（Client-->Server）

        /**
         * {
         *     "seq": 5,
         *     "cmd": 528,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_MOVE_LEFT = 0x210;      // 左移指令（Client-->Server）

        /**
         * {
         *     "seq": 6,
         *     "cmd": 529,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_MOVE_RIGHT = 0x211;     // 右移指令（Client-->Server）

        /**
         * {
         *     "seq": 7,
         *     "cmd": 530,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_MOVE_FORWARD = 0x212;       // 前移指令（Client-->Server）

        /**
         * {
         *     "seq": 8,
         *     "cmd": 531,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_MOVE_BACKWARD = 0x213;    // 后移指令（Client-->Server）

        /**
         * {
         *     "seq": 9,
         *     "cmd": 532,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_GRAB = 0x214;           // 抓指令（Client-->Server）

        /**
         * {
         *     "seq": 10,
         *     "cmd": 533,
         *     "session_id": "xxx",
         *     "data": {
         *         "time_stamp": 12345
         *     }
         * }
         */
        static final public int CMD_STOP = 0x215;           // 停止指令（Client-->Server）
    }


    static public class JsonKey {
        static final public String KEY_SEQ = "seq"; // 每条 广播/消息 的序号，回复时需要带上原 seq
        static final public String KEY_CMD = "cmd";
        static final public String KEY_DATA = "data";
        static final public String KEY_SESSION_ID = "session_id";    // Session 数据，用于标识一次会话

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
        static final public String KEY_CONTINUE = "continue"; // 0: 放弃；1：继续玩

        static final public String KEY_CONFIG = "config";   // 上机初始化配置
        static final public String KEY_GAME_CONFIG = "game_config"; // 游戏初始化配置
        static final public String KEY_AUTHORITY_INFO = "authority_info";   // 付费鉴权信息
        static final public String KEY_ENCRYPTED_RESULT = "encrypted_result";   // 结果校验串
        static final public String KEY_CUSTOM_TOKEN = "custom_token";   // 预约时带上来的 token, 在游戏结果中透传给 Client App
        static final public String KEY_ROOM_ID = "room_id";         // 所进入房间信息，用于业务服务器校验与用户申请上机的房间是否一致

        static final public String KEY_TIME_STAMP = "time_stamp";   // 时间戳 System.currentMillSeconds()
    }

    public enum WawajiState {
        /**
         * 空闲状态
         */
        Idle(0),
        /**
         * 游戏准备状态
         */
        GameReady(1),
        /**
         * 等待开始状态
         */
        WaitStart(2),
        /**
         * 确认上机状态
         */
        ConfirmStartGame(3),
        /**
         * 游戏操作中状态
         */
        Operating(4),
        /**
         * 等待机器返回游戏结果状态
         */
        WaitGrabResult(5),
        /**
         * 等待玩家确认结果状态
         */
        WaitGameResultConfirm(6),
        /**
         * 等待用户重复玩,再来一局
         */
        WaitReAppointment(7),
        /**
         * 等待通知玩家游戏
         */
        WaitGameReady(8);

        // 定义私有变量
        private int nCode;

        // 构造函数，枚举类型只能为私有
        private WawajiState(int _nCode) {
            this.nCode = _nCode;
        }
    }
}
