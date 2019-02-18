#ifndef zego_api_external_audio_device_oc_h
#define zego_api_external_audio_device_oc_h

#import <Foundation/Foundation.h>

#import "ZegoAudioCapture.h"
#import "zego-api-defines-oc.h"
#import "zego-api-audio-frame-oc.h"


/** 辅助推流通道音频采集源类别 */
typedef enum ZegoAPIAuxPublishChannelAudioSrcType
{
    kZegoAPIAuxPublishChannelAudioSrcTypeNone                      = -1,   /**< 无声 */
    kZegoAPIAuxPublishChannelAudioSrcTypeSameAsMainPublishChannel  = 0,    /**< 和主推流通道一样 */
    kZegoAPIAuxPublishChannelAudioSrcTypeExternalCapture           = 1,    /*** 使用外部采集 */
} ZegoAPIAuxPublishChannelAudioSrcType;

@interface ZegoExternalAudioDevice : NSObject

/**
 设置外部音频设备，包括外部音频采集和外部音频渲染
 
 @param enable true 开启，false 关闭
 @discussion 必须在InitSDK之前设置；
 @discussion 开发者采用外部采集和渲染后，SDK 内部不负责声音增强、噪音抑制、回音消除等功能，需要用户自己实现。
 */
+ (void)enableExternalAudioDevice:(bool)enable;

/**
 选择辅助推流通道的音频采集源

 @param type 辅助推流通道音频采集源类别，参考 enum ZegoAPIAuxPublishChannelAudioSrcType
 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)setAudioSrcForAuxiliaryPublishChannel:(ZegoAPIAuxPublishChannelAudioSrcType)type;

/**
 开始外部音频采集（通知 SDK）

 @param publishChannel 选定推流通道
 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)startCapture:(ZegoAPIPublishChannelIndex)publishChannel;

/**
 结束外部音频采集（通知 SDK）

 @param publishChannel 选定推流通道
 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)stopCapture:(ZegoAPIPublishChannelIndex)publishChannel;

/**
 把采集到的音频数据塞进 SDK

 @param audioFrame 采集到的音频帧
 @param publishChannel 选定推流通道
 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)onRecordAudioFrame:(ZegoAPIAudioFrame*)audioFrame channel:(ZegoAPIPublishChannelIndex)publishChannel;

/**
 开始外部音频播放（通知 SDK）

 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)startRender;

/**
 结束外部音频播放（通知 SDK）

 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)stopRender;

/**
 从 SDK 取音频渲染数据

 @param audioFrame 得到的音频数据
 @return 详见 enum ZegoAPIErrorCode
 */
+ (int)onPlaybackAudioFrame:(ZegoAPIAudioFrame*)audioFrame;


/**
 获取 IAudioDataInOutput 对象
 
 @return IAudioDataInOutput 对象，参考 IAudioDataInOutput 类型
 @discussion 必须在InitSDK之后调用。
 @warning Deprecated 不建议使用，请使用 ZegoExternalAudioDevice 其它方法。
 */
+ (AVE::IAudioDataInOutput *)getIAudioDataInOutput;
 
@end

#endif /* zego_api_external_audio_device_oc_h */

