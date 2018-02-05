//
//  ZegoLiveRoomApi-Player.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#import "ZegoLiveRoomApi.h"
#import "ZegoLiveRoomApiDefines.h"

@protocol ZegoLivePlayerDelegate;
@protocol ZegoLiveApiRenderDelegate;
@protocol ZegoLiveApiAudioRecordDelegate;


@interface ZegoLiveRoomApi (Player)

/**
 设置 Player 代理对象
 
 @param playerDelegate 遵循 ZegoLivePlayerDelegate 协议的代理对象
 @return true 成功，false 失败
 @discussion 使用 Player 功能，初始化相关视图控制器时需要设置代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (bool)setPlayerDelegate:(id<ZegoLivePlayerDelegate>)playerDelegate;

/**
 播放直播流
 
 @param streamID 流 ID，该参数仅能传入流 ID，不可在流 ID 后添加播放参数。如果想指定播放参数，请使用 [ZegoLiveRoomApi (Player) startPlayingStream:inView:params:] 播放流
 @param view 用来渲染播放视频的视图
 @return true 成功，false 失败
 @discussion 播放直播流调用此 API。播放成功后，等待 [ZegoLivePlayerDelegate -onPlayStateUpdate:streamID:] 回调
 */
- (bool)startPlayingStream:(NSString *)streamID inView:(ZEGOView *)view;

/**
 指定播放参数，播放直播流

 @param streamID 流 ID，该参数仅能传入流 ID，不可在流 ID 后添加播放参数。
 @param view 用来渲染播放视频的视图
 @param params 播放参数
 @return true 成功，false 失败
 @discussion 播放直播流调用此 API。播放成功后，等待 [ZegoLivePlayerDelegate -onPlayStateUpdate:streamID:] 回调
 */
- (bool)startPlayingStream:(NSString *)streamID inView:(ZEGOView *)view params:(NSString *)params;


/**
 指定播放参数，播放直播流

 @param streamID 流 ID，该参数仅能传入流 ID，不可在流 ID 后添加播放参数
 @param view 用来渲染播放视频的视图
 @param info 多媒体流附加信息
 @return 成功，false 失败
 @discussion 播放直播流调用此 API。播放成功后，等待 [ZegoLivePlayerDelegate -onPlayStateUpdate:streamID:] 回调
 */
- (bool)startPlayingStream:(NSString *)streamID inView:(ZEGOView *)view extraInfo:(ZegoAPIStreamExtraPlayInfo*)info;

/**
 更新播放视图
 
 @param view 播放视图
 @param streamID 流 ID
 @return true 成功，false 失败
 @discussion 调用 [self -startPlayingStream:inView:] 或 [self -startPlayingStream:inView:params:] 播放流成功以后，如果要切换流播放 View 或者停止显示流画面，调用该 API 变更
 */
- (bool)updatePlayView:(ZEGOView *)view ofStream:(NSString *)streamID;

/**
 停止播放流
 
 @param streamID 流 ID
 @return true 成功，false 失败
 @discussion 主播停止流推后，会通过 [ZegoRoomDelegate -onStreamUpdated:streams:roomID:] 通知房间内用户流删除，用户需要调用此 API 停止播放流。停止播放流后，注意移除相关的UI控件
 */
- (bool)stopPlayingStream:(NSString *)streamID;

/**
 请求连麦
 
 @param blk 回调 block
 @return true 成功，false 失败
 @discussion 观众请求连麦后，主播会收到 [ZegoLivePublisherDelegate -onJoinLiveRequest:fromUserID:fromUserName:roomID:] 回调。连麦成功后，结果通过 blk 回调
 */
- (bool)requestJoinLive:(ZegoResponseBlock)blk;

/**
 回应主播端的邀请连麦请求
 
 @param seq 连麦请求序列号，标识当次连麦请求
 @param rspResult 回应，0 为同意
 @return true 成功，false 失败
 @discussion 一般在 [ZegoLivePlayerDelegate -onInviteJoinLiveRequest:fromUserID:fromUserName:roomID:] 中调用本 API 回应邀请连麦请求
 */
- (bool)respondInviteJoinLiveReq:(int)seq result:(int)rspResult;

/**
 开关硬件解码
 
 @param bRequire true 打开，false 关闭。默认 false
 @return true 成功，false 失败
 @discussion 如果要打开，需要在拉流前设置。打开硬编硬解开关需后台可控，避免碰到版本升级或者硬件升级时出现硬编硬解失败的问题
 */
+ (bool)requireHardwareDecoder:(bool)bRequire;

/**
 （声音输出）静音开关
 
 @param bEnable true 不静音，false 静音。默认 true
 @return true 成功，false 失败
 @discussion 设置为关闭后，内置扬声器和耳机均无声音输出
 */
- (bool)enableSpeaker:(bool) bEnable;

/**
 手机内置扬声器开关
 
 @param bOn true 打开，false 关闭。默认 true
 @return true 成功，false 失败
 @discussion 设置为关闭后，扬声器无声音，耳机仍有声音输出
 */
- (bool)setBuiltInSpeakerOn:(bool)bOn;

/**
 统一设置所有拉流的播放音量
 
 @param volume 音量取值范围为(0, 100)，数值越大，音量越大。默认 100
 @return true 成功, false 失败
 @discussion 直播时通过此 API 软件调整音量
 */
- (bool)setPlayVolume:(int)volume;

/**
 设置指定拉流的播放音量
 
 @param volume 音量取值范围为(0, 100)，数值越大，音量越大。默认 100
 @streamID  流ID. ID为空时, 统一设置所有拉流的播放音量
 @return true 成功, false 失败
 @discussion 直播时通过此 API 软件调整音量
 */
- (bool)setPlayVolume:(int)volume ofStream:(NSString *)streamID;

/**
 获取当前播放视频的音量
 
 @param streamID 播放流 ID
 @return 视频的音量值
 @discussion 直播时通过此 API 获取当前音量。音量变更也会受硬件音量键的影响。
 */
- (float)getSoundLevelOfStream:(NSString *)streamID;

/**
 设置观看直播的View的模式
 
 @param mode 模式，参考 ZegoVideoViewMode 定义。默认 ZegoVideoViewModeScaleAspectFill
 @param streamID 播放流 ID
 @return true 成功，false 失败
 @note 必须在拉流后调用才有效
 @discussion 一般在流播放、流新增、全屏切换等其他流尺寸可能变化的场合时调用
 */
- (bool)setViewMode:(ZegoVideoViewMode)mode ofStream:(NSString *)streamID;

/**
 设置播放渲染朝向
 
 @param rotate 逆时针旋转角度(0/90/180/270)。默认 0
 @param streamID 播放流 ID
 @return true 成功，false 失败
 @discussion 一般用于全屏切换、旋转设备时调用，调整播放方向
 */
- (bool)setViewRotation:(int)rotate ofStream:(NSString *)streamID;

/**
 对观看直播视图进行截图
 
 @param streamID 流 ID
 @param blk 成功的截图通过 blk 返回
 @return true 成功，false 失败
 @discussion 直播时调用此 API 可获取当前画面截图
 */
- (bool)takeSnapshotOfStream:(NSString *)streamID withCompletionBlock:(ZegoSnapshotCompletionBlock)blk;

/**
 拉流是否接收音频数据

 @param streamID 播放流 ID
 @param active true 接收，false 不接收
 @return 0 成功，否则失败
 @discussion 仅拉 UDP 流有效
 */
- (int)activateAudioPlayStream:(NSString *)streamID active:(bool)active;

/**
 拉流是否接收视频数据
 
 @param streamID 播放流 ID
 @param active true 接收，false 不接收
 @return 0 成功，否则失败
 @discussion 仅拉 UDP 流有效
 */
- (int)activateVedioPlayStream:(NSString *)streamID active:(bool)active;

/**
 设置拉流质量监控周期
 
 @param timeInMS 时间周期，单位为毫秒，取值范围为(500, 60000)。默认为 3000
 @discussion 该设置会影响 [ZegoLivePlayerDelegate -onPlayQualityUpdate:stream:videoFPS:videoBitrate:] 的回调频率
 */
+ (void)setPlayQualityMonitorCycle:(unsigned int)timeInMS;

/**
 设置外部渲染
 
 @param bEnable 是否外部渲染，true 是，false 不是。默认 false
 @discussion 必须在初始化 SDK 前调用。启用外部渲染后，需要设置外部渲染回调代理对象。SDK 提供给用户外部渲染的源数据格式为 BGRA32
 */
+ (void)enableExternalRender:(BOOL)bEnable;

/**
 设置外部渲染回调对象
 
 @param renderDelegate 遵循 ZegoLiveApiRenderDelegate 协议的代理对象
 @discussion 使用外部渲染功能，需要设置代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (void)setRenderDelegate:(id<ZegoLiveApiRenderDelegate>)renderDelegate;

/**
 音频录制开关
 
 @param enable 开启音频录制。true 开启，false 关闭。默认 false
 @return true 成功，false 失败
 @discussion 初始化 SDK 后调用。开启音频录制后，调用方需要设置音频录制回调代理对象，并通过 [ZegoLiveRoomApi (Player) -onAudioRecord:sampleRate:numOfChannels:bitDepth:type:] 获取 SDK 录制的数据。使用此接口开启音频录制，相当于调用 enableSelectedAudioRecord:(ZegoAPIAudioRecordConfig)config，且 config 中的参数默认值为：ZEGO_AUDIO_RECORD_MIX、44100、单声道。
 */
- (bool)enableAudioRecord:(BOOL)enable;

/**
 音频录制开关
 
 @warning Deprecated，请使用 enableSelectedAudioRecord:
 */
- (bool)enableSelectedAudioRecord:(unsigned int)mask sampleRate:(int)sampleRate;

/**
 音频录制开关
 
 @param config 配置信息, 参考 ZegoAPIAudioRecordConfig
 @return true 成功，false 失败
 @discussion 初始化 SDK 后调用。开启音频录制后，调用方需要设置音频录制回调代理对象，并通过 [ZegoLiveRoomApi (Player) -onAudioRecord:sampleRate:numOfChannels:bitDepth:type:] 获取 SDK 录制的数据
 */
- (bool)enableSelectedAudioRecord:(ZegoAPIAudioRecordConfig)config;

/**
 设置音频录制回调代理对象
 
 @param audioRecordDelegate 遵循 ZegoLiveApiAudioRecordDelegate 协议的代理对象
 @discussion 开启音频录制功能，需要设置代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (void)setAudioRecordDelegate:(id<ZegoLiveApiAudioRecordDelegate>)audioRecordDelegate;

/**
 获取 SDK 支持的最大同时播放流数
 
 @return 最大支持播放流数
 */
+ (int)getMaxPlayChannelCount;

/**
 设置回调, 接收媒体次要信息
 
 @param onMediaSideCallback 回调函数指针, pszStreamID：流ID，标记当前回调的信息属于哪条流， buf：接收到的信息数据（具体内容参考官网对应文档中的格式说明）, dataLen：buf 总长度
 @discussion 开始拉流前调用。观众端在此 API 设置的回调中获取主播端发送的次要信息（要求主播端开启发送媒体次要信息开关，并调用 [ZegoLiveRoomApi (Publisher) -sendMediaSideInfo:dataLen:packet:] 发送次要信息）。当不需要接收信息时，需将 onMediaSideCallback 置空，避免内存泄漏
 */
- (void)setMediaSideCallback:(void(*)(const char *pszStreamID, const unsigned char* buf, int dataLen))onMediaSideCallback;

/**
 帧顺序检测开关
 
 @param enable true 检测帧顺序，不支持B帧； false 不检测帧顺序，支持B帧，可能出现短暂花屏
 @discussion 必须在初始化 SDK 前调用
 */
+ (void)enableCheckPoc:(bool)enable;

@end


@protocol ZegoLivePlayerDelegate <NSObject>

/**
 播放流事件
 
 @param stateCode 播放状态码，0 表示拉流成功
 @param streamID 流 ID
 @discussion 观众调用 [ZegoLiveRoomApi (Player) -startPlayingStream:inView:] 或 [ZegoLiveRoomApi (Player) -startPlayingStream:inView:params:] 拉流成功后，通过该 API 通知
 @note 拉流状态码及其含义如下:
 stateCode = 0，直播开始。
 stateCode = 3，直播遇到严重问题（如出现，请联系 ZEGO 技术支持）。
 stateCode = 4，创建直播流失败。
 stateCode = 5，获取流信息失败。
 stateCode = 6，无流信息。
 stateCode = 7，媒体服务器连接失败（请确认推流端是否正常推流、正式环境和测试环境是否设置同一个、网络是否正常）。
 stateCode = 8，DNS 解析失败。
 stateCode = 9，未登录就直接拉流。
 stateCode = 10，逻辑服务器网络错误(网络断开时间过长时容易出现此错误)。
 */
- (void)onPlayStateUpdate:(int)stateCode streamID:(NSString *)streamID;

@optional

/**
 收到主播端的邀请连麦请求
 
 @param seq 连麦请求序列号，标识当次连麦请求
 @param userId 来源用户 ID
 @param userName 来源用户名
 @param roomID 房间 ID
 @discussion 主播端调用 [ZegoLiveRoomApi (Publisher) -inviteJoinLive:responseBlock:] 邀请观众连麦后，观众端会收到此通知。建议在此通知中，调用 [ZegoLiveRoomApi (Player) -respondInviteJoinLiveReq:result:] 回应邀请连麦请求
 */
- (void)onInviteJoinLiveRequest:(int)seq fromUserID:(NSString *)userId fromUserName:(NSString *)userName roomID:(NSString *)roomID;


/**
 收到结束连麦信令
 
 @param fromUserId 来源用户 ID
 @param fromUserName 来源用户名
 @param roomID 房间 ID
 */
- (void)onEndJoinLiveCommad:(NSString *)fromUserId userName:(NSString *)fromUserName roomID:(NSString *)roomID;

/**
 视频宽高变化通知
 
 @param size 视频大小
 @param streamID 流的唯一标识
 @discussion startPlay 后，以下情况下，播放端会收到该通知：1. SDK 在获取到第一帧数据后 2. 直播过程中视频宽高发生变化。从播放第一条流，到获得第一帧数据，中间可能出现一个短暂的时间差（具体时长取决于当前的网络状态），推荐在进入直播页面时加载一张预览图以提升用户体验，然后在本回调中去掉预览图
 */
- (void)onVideoSizeChangedTo:(CGSize)size ofStream:(NSString *)streamID;

/**
 观看质量更新
 
 @param quality 0 ~ 3 分别对应优、良、中、差
 @param streamID 观看流ID
 @param fps 帧率(frame rate)
 @param kbs 码率(bit rate) kb/s
 @discussion startPlay 后，该回调会被多次调用，调用周期取决于 [ZegoLiveRoomApi (Player) setPlayQualityMonitorCycle] 设置的周期。开发者可以在该回调中获取当前的视频质量数据，加以处理
 */
- (void)onPlayQualityUpdate:(int)quality stream:(NSString *)streamID videoFPS:(double)fps videoBitrate:(double)kbs;


/**
 观看质量更新
 
 @param streamID 观看流ID
 @param quality quality 参考ZegoApiPlayQuality定义
 */
- (void)onPlayQualityUpate:(NSString *)streamID quality:(ZegoApiPlayQuality)quality;

@end


@protocol ZegoLiveApiRenderDelegate <NSObject>

/**
 SDK 从用户端获取 PixelBuffer 地址
 
 @param width 视频宽度
 @param height 视频高度
 @param stride 视频帧数据每一行字节数
 @return CVPixelBufferRef 对象
 @discussion 开启外部渲染，设置外部渲染代理对象成功后，SDK 通过此 API 从用户端获取 PixelBuffer 地址。SDK 获取到用户指定的 PixelBuffer 后，将采集的视频源数据拷贝进去
 */
- (CVPixelBufferRef)onCreateInputBufferWithWidth:(int)width height:(int)height stride:(int)stride;

/**
 SDK 拷贝视频数据完成通知
 
 @param pixelBuffer 拷贝完成的 PixelBuffer 地址
 @param streamID 流名
 @discussion SDK 通过此回调通知用户数据拷贝完成
 */
- (void)onPixelBufferCopyed:(CVPixelBufferRef)pixelBuffer ofStream:(NSString *)streamID;

@end


@protocol ZegoLiveApiAudioRecordDelegate <NSObject>

@optional

/**
 音频录制回调
 
 @param audioData SDK 录制的音频源数据
 @param sampleRate 采样率，与 [ZegoLiveRoomApi (Player) enableSelectedAudioRecord] 中设置的值一致
 @param numOfChannels 通道数量，单通道
 @param bitDepth 位深度，16 bit
 @param type 音源类型，参考 ZegoAPIAudioRecordMask
 @discussion 开启音频录制并设置成功代理对象后，用户调用此 API 获取 SDK 录制的音频数据。用户可自行对数据进行处理，例如：存储等。SDK 发送音频数据的周期为 20ms。存储数据时注意取 sampleRate、numOfChannels、bitDepth 参数写包头信息。退出房间后或停止录制后，该回调不再被调用
 */
- (void)onAudioRecord:(NSData *)audioData sampleRate:(int)sampleRate numOfChannels:(int)numOfChannels bitDepth:(int)bitDepth type:(unsigned int)type;

/**
 音频录制回调
 
 @warning Deprecated，请使用 onAudioRecord:sampleRate:numOfChannels:bitDepth:type:
 */
- (void)onAudioRecord:(NSData *)audioData sampleRate:(int)sampleRate numOfChannels:(int)numOfChannels bitDepth:(int)bitDepth __attribute__ ((deprecated));
@end

