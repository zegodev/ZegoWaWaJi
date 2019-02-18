//
//  ZegoLiveRoomApiDefines.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#ifndef ZegoLiveRoomApiDefines_h
#define ZegoLiveRoomApiDefines_h

#if TARGET_OS_IPHONE
#import <UIKit/UIKit.h>
#define ZEGOView UIView
#define ZEGOImage UIImage
#elif TARGET_OS_OSX
#import <AppKit/AppKit.h>
#define ZEGOView NSView
#define ZEGOImage NSImage
#endif

#import "zego-api-defines-oc.h"

#ifdef __cplusplus
#define ZEGO_EXTERN     extern "C"
#else
#define ZEGO_EXTERN     extern
#endif


/** 混流配置项，调用 [ZegoLiveRoomApi (Publisher) -setMixStreamConfig:] 设置 */
/** 混流ID，值为 NSString */
ZEGO_EXTERN NSString *const kZegoMixStreamIDKey;
/** 混流输出大小，值为 NSValue */
ZEGO_EXTERN NSString *const kZegoMixStreamResolution;

/** 成员角色 */
typedef enum
{
    /** 主播 */
    ZEGO_ANCHOR = 1,
    /** 观众 */
    ZEGO_AUDIENCE = 2,
} ZegoRole;

/** 流变更类型 */
typedef enum
{
    /** 新增流 */
    ZEGO_STREAM_ADD     = 2001,
    /** 删除流 */
    ZEGO_STREAM_DELETE  = 2002,
} ZegoStreamType;

/**
 如何确定错误码
 
 1. 先通过错误掩码 ZegoErrorMask 来判断是什么类型的错误（应该从 ROOM_SERVER_ERROR_MASK 开始判断）
 2. 如果符合房间服务错误掩码 ROOM_SERVER_ERROR_MASK，用 ROOM_SERVER_ERROR_MASK 异或原始错误码，再对应房间错误码 ZegoRoomError 查看是什么错误
 */

/** 房间错误码 */
typedef enum ZegoRoomError
{
    /** HTTP 连接错误 */
    LOGIN_NETWORK_ERROR     = 101,
    /** TCP 连接错误 */
    LOGIN_PUSH_ERROR        = 102,
    /** 服务器错误 */
    LOGIN_SERVER_ERROR      = 103,
    /** 网络切换临时状态，网络恢复后会自动重连 */
    LOGIN_NET_CHANGE_ERROR  = 104,
    /** 用户没有登录 */
    NOT_LOGIN_ERROR         = 105,
    /** 请求参数错误 */
    REQUEST_PARAM_ERROR     = 106,
    
    /** 会话错误 */
    SESSION_ERROR           = 141,
    
    /** 答题服务故障 */
    DATI_COMMIT_ERROR       = 3001,
    /** 答题时间已过 */
    DATI_TIMEOUT_ERROR      = 3002,
    /** 重复答题 */
    DATI_REPEAT_ERROR       = 3003,
};

/** 错误掩码 */
typedef enum ZegoErrorMask
{
    /** 网络连接错误掩码 */
    NETWORK_ERROR_MASK      = 0x1000,
    /** SDK重登录错误掩码 */
    RELOGIN_ERROR_MASK      = 0x10000,
    /** 房间服务错误掩码 */
    ROOM_SERVER_ERROR_MASK  = 0x100000,
};

/** 流信息 */
@interface ZegoStream : NSObject

/** 用户 ID */
@property (nonatomic, copy) NSString *userID;
/** 用户名 */
@property (nonatomic, copy) NSString *userName;
/** 流 ID */
@property (nonatomic, copy) NSString *streamID;
/** 流附加信息 */
@property (nonatomic, copy) NSString *extraInfo;
@end

typedef void(^ZegoSnapshotCompletionBlock)(ZEGOImage* img);



#endif /* ZegoLiveRoomApiDefines_h */
