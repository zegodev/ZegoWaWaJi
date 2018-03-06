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
