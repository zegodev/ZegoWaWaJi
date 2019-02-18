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

typedef void(^ZegoInitSDKCompletionBlock)(int errorCode);
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
 初始化 SDK
 
 @param appID  Zego 派发的数字 ID, 开发者的唯一标识
 @param appSignature  Zego 派发的签名, 用来校验对应 appID 的合法性
 @param blk 回调 block
 @return SDK 实例，nil 表示初始化失败
 @discussion 初始化 SDK 时调用。初始化 SDK 失败可能导致 SDK 功能异常
 */
- (instancetype)initWithAppID:(unsigned int)appID appSignature:(NSData*)appSignature completionBlock:(ZegoInitSDKCompletionBlock)blk;

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
 设置自定义token信息
 
 @param thirdPartyToken 第三方传入的token
 @discussion 使用此方法验证登录时用户的合法性，登录房间前调用，token的生成规则请联系即构。若不需要验证用户合法性，不需要调用此函数。
 */
- (void)setCustomToken:(NSString *)thirdPartyToken;

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

/**
 设置音视频设备错误回调代理对象
 
 @param deviceEventDelegate 遵循 ZegoDeviceEventDelegate 协议的代理对象
 @return true 成功，false 失败
 @discussion 开发者获取音视频设备错误，需要先设置此代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (bool)setDeviceEventDelegate:(id<ZegoDeviceEventDelegate>)deviceEventDelegate;


#if TARGET_OS_IPHONE
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

/**
 设置是否允许SDK使用麦克风设备
 
 @param enable YES 表示允许使用麦克风，NO 表示禁止使用麦克风，此时如果SDK在占用麦克风则会立即释放。
 @return YES 调用成功 NO 调用失败
 @discussion 调用时机为引擎创建后的任意时刻。
 @note 接口由于涉及对设备的操作，极为耗时，不建议随便调用，只在真正需要让出麦克风给其他应用的时候才调用
 */
- (BOOL)enableMicDevice:(BOOL)enable;
#endif

#if TARGET_OS_OSX

/**
 设置视频设备
 
 @param deviceId 设备 Id
 @return true 成功，false 失败
 @discussion 本接口用于 Mac PC 端的业务开发
 */
+ (bool)setVideoDevice:(NSString *)deviceId;

/**
 设置音频设备
 
 @param deviceId 设备 Id
 @param deviceType 设备类型
 @return true 成功，false 失败
 @discussion 本接口用于 Mac PC 端的业务开发
 */
+ (bool)setAudioDevice:(NSString *)deviceId type:(ZegoAPIAudioDeviceType)deviceType;

/**
 获取音频设备列表
 
 @param deviceType 设备类型
 @return 设备信息列表
 */
- (NSArray<ZegoAPIDeviceInfo *> *)getAudioDeviceList:(ZegoAPIAudioDeviceType)deviceType;

/**
 获取视频设备列表
 
 @return 设备信息列表
 */
- (NSArray<ZegoAPIDeviceInfo *> *)getVideoDeviceList;

/**
 系统声卡声音采集开关
 
 @param enable 是否打开
 */
- (void)enableMixSystemPlayout:(bool)enable;

/**
 获取麦克风音量
 
 @param deviceId 设备ID
 @return -1: 获取失败 0 ~100 麦克风音量
 @discussion 切换麦克风后需要重新获取麦克风音量
 */
- (int)getMicDeviceVolume:(NSString *)deviceId;

/**
 设置麦克风音量
 
 @param deviceId 设备ID
 @param volume 音量 0 ~ 100
 */
- (void)setMicDevice:(NSString *)deviceId volume:(int)volume;

/**
 获取扬声器音量
 
 @param deviceId 设备ID
 @return -1: 获取失败 0 ~100 音量
 */
- (int)getSpeakerDeviceVolume:(NSString *)deviceId;

/**
 设置扬声器音量
 
 @param deviceId 设备ID
 @param volume 音量 0 ~ 100
 */
- (void)setSpeakerDevice:(NSString *)deviceId volume:(int)volume;

/**
 获取app中扬声器音量
 
 @param deviceId 设备ID
 @return -1: 获取失败 0 ~100 音量
 */
- (int)getSpeakerSimpleVolume:(NSString *)deviceId;

/**
 设置app中扬声器音量
 
 @param deviceId 设备ID
 @param volume 音量 0 ~ 100
 */
- (void)setSpeaker:(NSString *)deviceId simpleVolume:(int)volume;

/**
 获取扬声器是否静音
 
 @param deviceId 设备ID
 @return true 静音 false 非静音
 */
- (bool)getSpeakerDeviceMute:(NSString *)deviceId;

/**
 设置扬声器静音
 
 @param deviceId 设备ID
 @param mute 是否静音
 */
- (void)setSpeakerDevice:(NSString *)deviceId mute:(bool)mute;

/**
 获取麦克风是否静音
 
 @param deviceId 设备ID
 @return true 静音 false 非静音
 */
- (bool)getMicDeviceMute:(NSString *)deviceId;

/**
 设置麦克风静音
 
 @param deviceId 设备ID
 @param mute 是否静音
 */
- (void)setMicDevice:(NSString *)deviceId mute:(bool)mute;

/**
 获取app中扬声器是否静音
 
 @param deviceId 设备ID
 @return true 静音 false 非静音
 */
- (bool)getSpeakerSimpleMute:(NSString *)deviceId;

/**
 设置app中扬声器是否静音
 
 @param deviceId 设备ID
 @param mute 是否静音
 */
- (void)setSpeaker:(NSString *)deviceId simpleMute:(bool)mute;

/**
 获取默认的视频设备
 
 @return deviceId
 */
- (NSString *)getDefaultVideoDeviceId;

/**
 获取默认的音频设备
 
 @param deviceType 音频类型
 @return deviceId
 */
- (NSString *)getDefaultAudioDeviceId:(ZegoAPIAudioDeviceType)deviceType;

/**
 监听设备的音量变化
 
 @param deviceId 设备ID
 @param deviceType 设备类型
 @return 设置是否成功
 @discussion 设置后如果有音量变化（包括app音量）通过ZegoDeviceEventDelegate::zego_onDevice:error:回调
 */
- (bool)setAudioVolumeNotify:(NSString *)deviceId type:(ZegoAPIAudioDeviceType)deviceType;

/**
 停止监听设备的音量变化
 
 @param deviceId 设备ID
 @param deviceType 设备类型
 @return 设置是否成功
 */
- (bool)stopAudioVolumeNotify:(NSString *)deviceId type:(ZegoAPIAudioDeviceType)deviceType;

#endif

/**
 设置 AVEngine 代理对象
 @return true 成功, false 失败
 */
- (bool)setAVEngineDelegate:(id<ZegoAVEngineDelegate>)avEngineDelegate;

/**
 设置配置信息，如果没有特殊说明，必须确保在 InitSDK 前调用
 
 @param config 配置信息，如"keep_audio_session_active=true", 等号后面值的类型要看下面每一项的定义
 
 @discussion "prefer_play_ultra_source", int value(1/0), default: 0. 可在 InitSDK 之后，拉流之前调用
 @discussion "keep_audio_session_active", bool value, default: false. if set true, app need to set the session inactive yourself
 @discussion "enforce_audio_loopback_in_sync", bool value, default: false. enforce audio loopback in synchronous method
 @discussion "audio_session_mix_with_others", bool value, default: true. set AVAudioSessionCategoryOptionMixWithOthers
 @discussion "support_general_mode_below_ios9", bool value, default: false. support general mode below ios 9.0
 */
+ (void)setConfig:(NSString *)config;

@end


@protocol ZegoRoomDelegate <NSObject>

@optional

/**
 用户被踢出房间
 
 @param reason 被踢出原因，16777219 表示该账户多点登录被踢出，16777220 表示该账户是被手动踢出，16777221 表示房间会话错误被踢出。
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
 @discussion 房间内增加流、删除流，均会触发此更新。主播推流，自己不会收到此回调，房间内其他成员会收到。建议对流增加和流删除分别采取不同的处理。
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
 @param info 信息
 @discussion 调用 [ZegoLiveRoomApi -setLiveEventDelegate] 设置直播事件代理对象后，在此回调中获取直播事件状态
 */
- (void)zego_onLiveEvent:(ZegoLiveEvent)event info:(NSDictionary<NSString*, NSString*>*)info;

@end

@protocol ZegoDeviceEventDelegate <NSObject>

/**
 设备事件回调
 
 @param deviceName 设备名，支持摄像头和麦克风设备，参考 zego-api-defines-oc.h 中定义
 @param errorCode 错误码。设备无错误不会回调，目前没有权限的错误码为-3，其他错误情况的错误码均为-1
 @discussion 调用 [ZegoLiveRoomApi -setDeviceEventDelegate] 设置设备事件代理对象后，在此回调中获取设备状态或错误
 */
- (void)zego_onDevice:(NSString *)deviceName error:(int)errorCode;

#if TARGET_OS_OSX

@optional

/**
 音频设备改变状态的回调
 
 @param deviceId 设备ID
 @param deviceName 设备名
 @param deviceType 设备类型，参考 zego-api-defines-oc.h 中 ZegoAPIAudioDeviceType 的定义
 @param state   设备状态，参考 zego-api-defines-oc.h 中 ZegoAPIDeviceState 的定义
 @discussion 调用 [ZegoLiveRoomApi -setDeviceEventDelegate] 设置设备事件代理对象后，在此回调中获取音频设备改变状态的信息
 */
- (void)zego_onAudioDevice:(NSString *)deviceId deviceName:(NSString *)deviceName deviceType:(ZegoAPIAudioDeviceType)deviceType changeState:(ZegoAPIDeviceState)state;

/**
 音频设备音量变化的回调
 
 @param deviceId 设备ID
 @param deviceType 设备类型，参考 zego-api-defines-oc.h 中 ZegoAPIAudioDeviceType 的定义
 @param volume 音量，有效值 0 ~ 100
 @param volumeType  音量类型，参考 zego-api-defines-oc.h 中 ZegoAPIVolumeType 的定义
 @discussion 调用 [ZegoLiveRoomApi -setDeviceEventDelegate] 设置设备事件代理对象后，在此回调中获取音频设备音量变化的信息
 */
- (void)zego_onAudioDevice:(NSString *)deviceId deviceType:(ZegoAPIAudioDeviceType)deviceType changeVolume:(uint32_t)volume volumeType:(ZegoAPIVolumeType)volumeType mute:(bool)mute;

/**
 视频设备改变状态的回调
 
 @param deviceId 设备ID
 @param deviceName 设备名
 @param state   设备状态，参考 zego-api-defines-oc.h 中 ZegoAPIDeviceState 的定义
 @discussion 调用 [ZegoLiveRoomApi -setDeviceEventDelegate] 设置设备事件代理对象后，在此回调中获取视频设备改变状态的信息
 */
- (void)zego_onVideoDevice:(NSString *)deviceId deviceName:(NSString *)deviceName changeState:(ZegoAPIDeviceState)deviceState;

#endif

@end

@protocol ZegoAVEngineDelegate <NSObject>

/**
 音视频引擎开始时回调
 */
- (void)onAVEngineStart;

/**
 音视频引擎停止时回调
 */
- (void)onAVEngineStop;

@end
