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

/** 流信息列表项 */
/** rtmp 播放 url 列表，值为 <NSArrayNSString *> */
ZEGO_EXTERN NSString *const kZegoRtmpUrlListKey;
/** hls 播放 url 列表，值为 <NSArrayNSString *> */
ZEGO_EXTERN NSString *const kZegoHlsUrlListKey;
/** flv 播放 url 列表，值为 <NSArrayNSString *> */
ZEGO_EXTERN NSString *const kZegoFlvUrlListKey;

/** 混流不存在的流名，值为 NSString* */
ZEGO_EXTERN NSString *const kZegoMixNonExistsStreamIDKey;
/** 混流请求 seq，值为 @(int) */
ZEGO_EXTERN NSString *const kZegoMixStreamReqSeqKey;

/** 混流配置项，调用 [ZegoLiveRoomApi (Publisher) -setMixStreamConfig:] 设置 */
/** 混流ID，值为 NSString */
ZEGO_EXTERN NSString *const kZegoMixStreamIDKey;
/** 混流输出大小，值为 NSValue */
ZEGO_EXTERN NSString *const kZegoMixStreamResolution;


/** 自定义推流配置项，调用 [ZegoLiveRoomApi (Publisher) -setPublishConfig:] 设置 */
/** 自定义转推 RTMP 地址 */
ZEGO_EXTERN NSString *const kPublishCustomTarget;

/** 设备项 */
/** 摄像头设备 */
ZEGO_EXTERN NSString *const kZegoDeviceCameraName;
/** 麦克风设备 */
ZEGO_EXTERN NSString *const kZegoDeviceMicrophoneName;

/** AudioSession相关配置信息的key, 值为 NSString */
ZEGO_EXTERN NSString *const kZegoConfigKeepAudioSesionActive;

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


/** 发布直播模式 */
enum ZegoApiPublishFlag
{
    /** 连麦模式 */
    ZEGO_JOIN_PUBLISH   = 0,
    /** 混流模式 */
    ZEGO_MIX_STREAM     = 1 << 1,
    /** 单主播模式 */
    ZEGO_SINGLE_ANCHOR  = 1 << 2,
};

/** 发布直播质量 */
typedef struct
{
    /** 视频帧率 */
    double fps;
    /** 视频码率(kb/s) */
    double kbps;
    /** 音频码率(kb/s) */
    double akbps;
    /** 延时(ms) */
    int rtt;
    /** 丢包率(0~255) */
    int pktLostRate;
    /** 直播质量(0~3) */
    int quality;
    
} ZegoApiPublishQuality;

typedef ZegoApiPublishQuality ZegoApiPlayQuality;

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
