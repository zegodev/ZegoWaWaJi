//
//  ZegoLiveRoomApi-Publisher.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//


#import "ZegoLiveRoomApi.h"
#import "ZegoLiveRoomApiDefines-Publisher.h"
#import "ZegoVideoCapture.h"

@interface ZegoLiveRoomApi (DuoPublisher)

/**
 设置本地预览视图
 
 @param view 用于渲染本地预览视频的视图
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 建议本地预览结束后，调用该 API 设置预览视图为 nil
 */
- (bool)setPreviewView:(ZEGOView *)view channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 启动本地预览
 
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 启动本地预览前，要调用 [ZegoLiveRoomApi (Publisher) -setPreviewView:] 设置本地预览视图
 */
- (bool)startPreview:(ZegoAPIPublishChannelIndex)index;

/**
 结束本地预览
 
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 建议停止推流，或本地预览结束后，调用该 API 停止本地预览
 */
- (bool)stopPreview:(ZegoAPIPublishChannelIndex)index;

/**
 开始发布直播
 
 @param streamID 流 ID
 @param title 直播名称，可选，默认为主播用户名
 @param flag 直播属性，参考 ZegoApiPublishFlag 定义
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 发布直播成功后，等待 [ZegoLivePublisherDelegate -onPublishStateUpdate:streamID:streamInfo:] 通知
 */
- (bool)startPublishing2:(NSString *)streamID title:(NSString *)title flag:(int)flag channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 开始发布直播
 
 @param streamID 流 ID
 @param title 直播名称，可选，默认为主播用户名
 @param flag 直播属性，参考 ZegoApiPublishFlag 定义
 @param extraInfo 流附加信息
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 发布直播成功后，等待 [ZegoLivePublisherDelegate -onPublishStateUpdate:streamID:streamInfo:] 通知
 */
- (bool)startPublishing2:(NSString *)streamID title:(NSString *)title flag:(int)flag extraInfo:(NSString *)extraInfo channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 开始发布直播
 
 @param streamID 流 ID
 @param title 直播名称，可选，默认为主播用户名
 @param flag 直播属性，参考 ZegoApiPublishFlag 定义
 @param extraInfo 流附加信息
 @param params 推流参数
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 发布直播成功后，等待 [ZegoLivePublisherDelegate -onPublishStateUpdate:streamID:streamInfo:] 通知
 */
- (bool)startPublishing2:(NSString *)streamID title:(NSString *)title flag:(int)flag extraInfo:(NSString *)extraInfo params:(NSString *)params channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 更新流附加信息
 
 @param extraInfo 流附加信息
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 通常在主播方的 [ZegoLivePublisherDelegate -onPublishStateUpdate:streamID:streamInfo:] 通知中，或其他需更新流附加信息的场合下调用。更新流附加信息成功后，除调用方外，同一房间内的其他人会收到 [ZegoLiveRoomDelegate -onStreamExtraInfoUpdated:roomID] 通知
 */
- (bool)updateStreamExtraInfo:(NSString *)extraInfo channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 停止直播
 
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 注意混流结束后，要先调用 [-updateMixInputStreams] 将流列表清空结束混流，然后调用 stopPublishing 结束直播
 */
- (bool)stopPublishing:(ZegoAPIPublishChannelIndex)index;

/**
 自定义推流配置
 
 @param config 配置信息 key-value，目前 key 仅支持 kPublishCustomTarget ，value 为用户自定义的转推 RTMP 地址。参考 ZegoLiveRoomApiDefines.h 中相关定义
 @param index 推流 channel Index
 @discussion 开发者如果使用自定义转推功能，推流开始前，必须调用此接口设置转推 RTMP 地址（SDK 推流方式必须为 UDP，转推地址必须为 RTMP），否则可能导致转推失败。
 */
- (void)setPublishConfig:(NSDictionary *)config channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置视频配置
 
 @param config 配置参数（视频编码输出分辨率、视频采集分辨率、视频帧率、视频码率），参考 ZegoAVConfig 定义
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流开始前调用本 API 进行视频采集参数配置
 */
- (bool)setAVConfig:(ZegoAVConfig *)config channelIndex:(ZegoAPIPublishChannelIndex)index;

#if TARGET_OS_IPHONE
/**
 设置手机方向
 
 @param orientation 手机方向
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 本设置用于校正主播输出视频朝向
 */
- (bool)setAppOrientation:(UIInterfaceOrientation)orientation channelIndex:(ZegoAPIPublishChannelIndex)index;
#endif

/**
 主播方开启美颜功能
 
 @param feature 美颜特性，参考 ZegoBeautifyFeature 定义。默认无美颜
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)enableBeautifying:(int)feature channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置美颜磨皮的采样步长
 
 @param step 采样步长，取值范围[1,16]。默认 4.0
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置。设置时需确保对应美颜特性开启
 */
- (bool)setPolishStep:(float)step channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置美颜采样颜色阈值
 
 @param factor 采样颜色阈值，取值范围[0,16]。默认 4.0
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置。设置时需确保对应美颜特性开启
 
 */
- (bool)setPolishFactor:(float)factor channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置美颜美白的亮度修正参数
 
 @param factor 亮度修正参数，取值范围[0,1]，值越大亮度越暗。默认 0.5
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置。设置时需确保对应美颜特性开启
 */
- (bool)setWhitenFactor:(float)factor channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置锐化参数
 
 @param factor 锐化参数，取值范围[0,2]，值越大锐化越强。默认 0.2
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置。设置时需确保对应美颜特性开启
 */
- (bool)setSharpenFactor:(float)factor channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置滤镜
 
 @param filter 滤镜种类，参考 ZegoFilter 定义。默认不使用滤镜
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)setFilter:(ZegoFilter)filter channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置本地预览视频视图的模式
 
 @param mode 模式，参考 ZegoVideoViewMode 定义。默认 ZegoVideoViewModeScaleAspectFill
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流开始前调用本 API 进行参数配置
 */
- (bool)setPreviewViewMode:(ZegoVideoViewMode)mode channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置预览渲染朝向
 
 @param rotate 旋转角度。默认 0
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置。使用 setAppOrientation 替代
 */
- (bool)setPreviewRotation:(int)rotate channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 是否启用预览镜像
 
 @param enable true 启用，false 不启用。默认 true
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)enablePreviewMirror:(bool)enable channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 是否启用摄像头采集结果镜像
 
 @param enable true 启用，false 不启用。默认 false
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)enableCaptureMirror:(bool)enable channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 是否开启码率控制
 
 @param enable true 启用，false 不启用。默认不启用
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 开启后，在带宽不足的情况下码率自动适应当前带宽
 */
- (bool)enableRateControl:(bool)enable channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 是否使用前置摄像头
 
 @param bFront true 使用，false 不使用。默认 true
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)setFrontCam:(bool)bFront channelIndex:(ZegoAPIPublishChannelIndex)index;


/**
 开启视频采集
 
 @param bEnable true 打开，false 关闭。默认 true
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)enableCamera:(bool)bEnable channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 开关手电筒
 
 @param bEnable true 打开，false 关闭。默认 false
 @param index 推流 channel Index
 @return true 成功，false 失败
 @discussion 推流时可调用本 API 进行参数配置
 */
- (bool)enableTorch:(bool)bEnable channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 预览截图
 
 @param blk 截图结果通过 blk 回调
 @param index 推流 channel Index
 @return true 成功，false 失败
 */
- (bool)takePreviewSnapshot:(ZegoSnapshotCompletionBlock)blk channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置水印的图片路径
 
 @param filePath 图片路径。如果是完整路径则添加 file: 前缀，如：@"file:/var/image.png"；资产则添加 asset: 前缀，如：@"asset:watermark"
 @param index 推流 channel Index
 @discussion 推流开始前调用本 API 进行参数配置
 */
- (void)setWaterMarkImagePath:(NSString *)filePath channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置水印在采集视频中的位置
 
 @param waterMarkRect 水印的位置与尺寸
 @param index 推流 channel Index
 @discussion 推流开始前调用本 API 进行参数配置。左上角为坐标系原点，区域不能超过编码分辨率设置的大小
 */
- (void)setPublishWaterMarkRect:(CGRect)waterMarkRect channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置水印在预览视频中的位置
 
 @param waterMarkRect 水印的位置与尺寸
 @param index 推流 channel Index
 @discussion 推流开始前调用本 API 进行参数配置。左上角为坐标系原点，区域不能超过预览视图的大小
 */
- (void)setPreviewWaterMarkRect:(CGRect)waterMarkRect channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置外部采集模块
 
 @param factory 工厂对象，遵循 ZegoVideoCaptureFactory 协议的对象
 @param index 推流 channel Index
 @discussion 必须在 InitSDK 前调用，并且不能置空
 */
+ (void)setVideoCaptureFactory:(id<ZegoVideoCaptureFactory>)factory channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 设置外部滤镜模块
 
 @param factory 工厂对象，遵循 ZegoVideoFilterFactory 协议的对象
 @param index 推流 channel Index
 @discussion 必须在 Init 前调用，并且不能置空
 */
+ (void)setVideoFilterFactory:(id<ZegoVideoFilterFactory>)factory channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 发送媒体次要信息开关
 
 @param start true 开启, false 关闭
 @param onlyAudioPublish true 纯音频直播，不传输视频数据, false 音视频直播，传输视频数据
 @param index 推流 channel Index
 @discussion onlyAudioPublish 开关在 start 开关开启时才生效
 */
- (void)setMediaSideFlags:(bool)start onlyAudioPublish:(bool)onlyAudioPublish channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 发送媒体次要信息
 
 @param inData 媒体次要信息数据
 @param dataLen 数据长度
 @param packet 是否外部已经打包好包头，true 已打包, false 未打包
 @param index 推流 channel Index
 @discussion 主播端开启媒体次要信息开关，并调用此 API 发送媒体次要信息后，观众端在 [ZegoLiveRoomApi (Player) -setMediaSideCallback:] 设置的回调中获取媒体次要信息
 */
- (void)sendMediaSideInfo:(const unsigned char *)inData dataLen:(int)dataLen packet:(bool)packet channelIndex:(ZegoAPIPublishChannelIndex)index;

@end

