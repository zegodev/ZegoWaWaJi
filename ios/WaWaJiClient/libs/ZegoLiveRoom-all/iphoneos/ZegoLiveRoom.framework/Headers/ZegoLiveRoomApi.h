//
//  ZegoLiveRoomApi.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZegoLiveRoomApiDefines.h"
#import "ZegoLiveRoomApiDefines-IM.h"

@protocol ZegoRoomDelegate;
@protocol ZegoLiveEventDelegate;
@protocol ZegoDeviceEventDelegate;
@protocol ZegoAVEngineDelegate;

typedef void(^ZegoLoginCompletionBlock)(int errorCode, NSArray<ZegoStream*> *streamList);
typedef void(^ZegoResponseBlock)(int result, NSString *fromUserID, NSString *fromUserName);
typedef void(^ZegoCustomCommandBlock)(int errorCode, NSString *roomID);

@interface ZegoLiveRoomApi : NSObject

/**
 ZegoLiveRoom SDK 版本号
 
 @return 版本号
 @discussion 开发者使用本 API 获取 SDK 版本号
 */
+ (NSString *)version;

/**
 版本号2
 
 @return 版本号2
 */
+ (NSString *)version2;

/**
 是否启用测试环境
 
 @param useTestEnv 是否启用测试环境，true 启用，false 不启用。默认为 false
 @discussion 建议在初始化 SDK 前调用。建议开发者在开发阶段设置为测试环境，使用由 Zego 提供的测试环境。上线前需切换为正式环境运营
 */
+ (void)setUseTestEnv:(bool)useTestEnv;

/**
 是否打开调试信息
 
 @param bOnVerbose 是否打开调试信息，true 打开，false 不打开。默认为 false
 @discussion 建议在初始化 SDK 前调用。建议在调试阶段打开此开关，方便调试
 */
+ (void)setVerbose:(bool)bOnVerbose;

/**
 设置业务类型
 
 @param type 业务类型，取值 0（直播类型）或 2（实时音视频类型）。默认为 0
 @discussion 确保在创建接口对象前调用
 */
+ (void)setBusinessType:(int)type;

/**
 是否使用聊天室功能
 
 @param bChatRoom 是否使用聊天室功能，true 使用，false 不使用。默认为 false
 @discussion zegoliveroom 自带 IM 功能，随 SDK 初始化。如果要额外使用聊天室，需要启用聊天室功能
 */
+ (void)setUseChatRoom:(bool)bChatRoom;

/**
 上报日志
 
 @discussion 上传日志到后台便于分析问题
 */
+ (void)uploadLog;

/**
 设置用户 ID 及用户名
 
 @param userID 用户 ID，不可为空
 @param userName 用户名，不可为空
 @return true 成功，false 失败
 @discussion 确保在 loginRoom 前设置成功。userID 和 userName 由业务方自己控制
 */
+ (bool)setUserID:(NSString *)userID userName:(NSString *)userName;

/**
 初始化 SDK
 
 @param appID  Zego 派发的数字 ID, 开发者的唯一标识
 @param appSignature  Zego 派发的签名, 用来校验对应 appID 的合法性
 @return SDK 实例，nil 表示初始化失败
 @discussion 初始化 SDK 时调用。初始化 SDK 失败可能导致 SDK 功能异常
 */
- (instancetype)initWithAppID:(unsigned int)appID appSignature:(NSData*)appSignature;

/**
 设置 room 代理对象
 
 @param roomDelegate 遵循 ZegoRoomDelegate 协议的代理对象
 @return true 成功，false 失败
 @discussion 使用 room 功能，初始化相关视图控制器时需要设置代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (bool)setRoomDelegate:(id<ZegoRoomDelegate>) roomDelegate;

/**
 设置房间配置信息
 
 @param audienceCreateRoom 观众是否可以创建房间。true 可以，false 不可以。默认 true
 @param userStateUpdate 用户状态（用户进入、退出房间）是否广播。true 广播，false 不广播。默认 false
 @discussion 在 userStateUpdate 为 true 的情况下，用户进入、退出房间会触发 [ZegoLiveRoomApi (IM) -onUserUpdate:updateType:] 回调
 */
- (void)setRoomConfig:(bool)audienceCreateRoom userStateUpdate:(bool)userStateUpdate;

/**
 登录房间
 
 @param roomID 房间 ID，长度不可超过 255 byte
 @param role 成员角色，可取值为 ZEGO_ANCHOR（主播），ZEGO_AUDIENCE（观众），详见 ZegoRole 定义
 @param blk 回调 block
 @return true 成功，false 失败
 @discussion 登录房间成功，才能开始直播。观众登录房间成功后，会在 blk 中返回当前房间的流信息
 */
- (bool)loginRoom:(NSString *)roomID role:(int)role withCompletionBlock:(ZegoLoginCompletionBlock)blk;

/**
 登录房间
 
 @param roomID 房间 ID，长度不可超过 255 byte
 @param roomName 房间名称，可选，长度不可超过 255 byte
 @param role 成员角色，可取值为 ZEGO_ANCHOR（主播），ZEGO_AUDIENCE（观众），详见 ZegoRole 定义
 @param blk 回调 block
 @return true 成功，false 失败
 @discussion 登录房间成功，才能开始直播。观众登录房间成功后，会在 blk 中返回当前房间的流信息
 */
- (bool)loginRoom:(NSString *)roomID roomName:(NSString *)roomName role:(int)role withCompletionBlock:(ZegoLoginCompletionBlock)blk;

/**
 退出房间
 
 @return true 成功，false 失败
 @discussion 连麦情况下，要 stop 所有的 stream 后，才能执行 logoutRoom。
 */
- (bool)logoutRoom;

/**
 发送自定义信令
 
 @param memberList 发送对象列表
 @param content 消息内容。长度不超过 1024 字节
 @param block 消息发送结果
 @return true 成功，false 失败
 @discussion 信令内容由用户自定义。发送结果通过 block 回调
 */
- (bool)sendCustomCommand:(NSArray<ZegoUser*> *)memberList content:(NSString *)content completion:(ZegoCustomCommandBlock)block;

/**
 设置直播事件代理对象
 
 @param liveEventDelegate 遵循 ZegoLiveEventDelegate 协议的代理对象
 @return true 成功，false 失败
 @discussion 设置代理对象成功后，在 [ZegoLiveEventDelegate -zego_onLiveEvent:info:] 中获取直播状态，状态参考 ZegoLiveEvent 定义。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (bool)setLiveEventDelegate:(id<ZegoLiveEventDelegate>)liveEventDelegate;


#if TARGET_OS_IPHONE
/**
 设置音视频设备错误回调代理对象
 
 @param deviceEventDelegate 遵循 ZegoDeviceEventDelegate 协议的代理对象
 @return true 成功，false 失败
 @discussion 开发者获取音视频设备错误，需要先设置此代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (bool)setDeviceEventDelegate:(id<ZegoDeviceEventDelegate>)deviceEventDelegate;

/**
 暂停模块
 
 @param moduleType 模块类型，参考 ZegoAPIModuleType 定义
 @discussion 用于需要暂停指定模块的场合，例如来电时暂定音频模块。暂停指定模块后，注意在合适时机下恢复模块
 */
- (void)pauseModule:(int)moduleType;

/**
 恢复模块
 
 @param moduleType 模块类型，参考 ZegoAPIModuleType 定义
 @discussion 用于需要恢复指定模块的场合，例如来电结束后恢复音频模块。暂停指定模块后，注意在合适时机下恢复模块
 */
- (void)resumeModule:(int)moduleType;
#endif

#if TARGET_OS_OSX

/**
 设置视频设备
 
 @param deviceId 设备 Id
 @return true 成功，false 失败
 @discussion 本接口用于 Mac PC 端的业务开发
 */
+ (bool)setVideoDevice:(NSString *)deviceId;

#endif

/**
 设置 AVEngine 代理对象
 @return true 成功, false 失败
 */
- (bool)setAVEngineDelegate:(id<ZegoAVEngineDelegate>)avEngineDelegate;

/**
 设置配置信息
 
 @param config 配置信息
 @discussion   确保在 InitSDK 前调用，但开启拉流加速(config为“prefer_play_ultra_source=1”)可在 InitSDK 之后，拉流之前调用
 */
+ (void)setConfig:(NSString *)config;

@end


@protocol ZegoRoomDelegate <NSObject>

@optional

/**
 因为使用同一个 UserId 登录，用户被挤出聊天室
 
 @param reason 被踢出原因
 @param roomID 房间 ID
 @discussion 可在该回调中处理用户被踢出房间后的下一步处理（例如报错、重新登录提示等）
 */
- (void)onKickOut:(int)reason roomID:(NSString *)roomID;

/**
 与 server 断开通知
 
 @param errorCode 错误码，0 表示无错误
 @param roomID 房间 ID
 @discussion 建议开发者在此通知中进行重新登录、推/拉流、报错、友好性提示等其他恢复逻辑。与 server 断开连接后，SDK 会进行重试，重试失败抛出此错误。请注意，此时 SDK 与服务器的所有连接均会断开
 */
- (void)onDisconnect:(int)errorCode roomID:(NSString *)roomID;

/**
 与 server 重连成功通知
 
 @param errorCode 错误码，0 表示无错误
 @param roomID 房间 ID
 */
- (void)onReconnect:(int)errorCode roomID:(NSString *)roomID;

/**
 与 server 连接中断通知，SDK会尝试自动重连
 
 @param errorCode 错误码，0 表示无错误
 @param roomID 房间 ID
 */
- (void)onTempBroken:(int)errorCode roomID:(NSString *)roomID;

/**
 流信息更新
 
 @param type 更新类型，详见 ZegoStreamType 定义
 @param streamList 直播流列表，列表中包含的是变更流的信息，非房间全部流信息
 @param roomID 房间 ID
 @discussion 房间内增加流、删除流，均会触发此更新。建议对流增加和流删除分别采取不同的处理
 */
- (void)onStreamUpdated:(int)type streams:(NSArray<ZegoStream*> *)streamList roomID:(NSString *)roomID;

/**
 流附加信息更新
 
 @param streamList 附加信息更新的流列表
 @param roomID 房间 ID
 @discussion 主播推流成功后调用 [ZegoLiveRoomApi (Publisher) -updateStreamExtraInfo:] 更新附加信息，在此回调中通知房间内其他成员。调用 [ZegoLiveRoomApi (Publisher) -updateStreamExtraInfo:] 更新信息的调用方，不会收到此回调
 */
- (void)onStreamExtraInfoUpdated:(NSArray<ZegoStream *> *)streamList roomID:(NSString *)roomID;

/**
 收到自定义消息
 
 @param fromUserID 消息来源 UserID
 @param fromUserName 消息来源 UserName
 @param content 消息内容
 @param roomID 房间 ID
 @discussion 调用 [ZegoLiveRoomApi -sendCustomCommand:content:completion:] 发送自定义消息后，消息列表中的用户会收到此通知
 */
- (void)onReceiveCustomCommand:(NSString *)fromUserID userName:(NSString *)fromUserName content:(NSString*)content roomID:(NSString *)roomID;

@end

/** 直播事件状态 */
typedef enum : NSUInteger {
    /** 播放直播开始重试 */
    Play_BeginRetry = 1,
    /** 播放直播重试成功 */
    Play_RetrySuccess = 2,
    
    /** 发布直播开始重试 */
    Publish_BeginRetry = 3,
    /** 发布直播重试成功 */
    Publish_RetrySuccess = 4,
    
    /** 拉流临时中断 */
    Play_TempDisconnected = 5,
    /** 推流临时中断 */
    Publish_TempDisconnected = 6,
    
    /** 拉流视频卡顿 */
    Play_VideoBreak = 7,
} ZegoLiveEvent;


@protocol ZegoLiveEventDelegate <NSObject>

/**
 直播事件回调
 
 @param event 直播事件状态，参考 ZegoLiveEvent 定义
 @param info 信息，目前为空
 @discussion 调用 [ZegoLiveRoomApi -setLiveEventDelegate] 设置直播事件代理对象后，在此回调中获取直播事件状态
 */
- (void)zego_onLiveEvent:(ZegoLiveEvent)event info:(NSDictionary<NSString*, NSString*>*)info;

@end

@protocol ZegoDeviceEventDelegate <NSObject>

/**
 设备事件回调
 
 @param deviceName 设备名，支持摄像头和麦克风设备，参考 ZegoLiveRoomApiDefines.h 中定义
 @param errorCode 错误码。设备无错误不会回调，目前出错后的错误码均为 -1
 @discussion 调用 [ZegoLiveRoomApi -setDeviceEventDelegate] 设置设备事件代理对象后，在此回调中获取设备状态或错误
 */
- (void)zego_onDevice:(NSString *)deviceName error:(int)errorCode;

@end

@protocol ZegoAVEngineDelegate <NSObject>

/**
 音视频引擎停止时回调
 */
- (void)onAVEngineStop;

@end
