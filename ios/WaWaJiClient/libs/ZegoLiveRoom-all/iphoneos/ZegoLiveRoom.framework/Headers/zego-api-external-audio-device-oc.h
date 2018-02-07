#ifndef zego_api_external_audio_device_oc_h
#define zego_api_external_audio_device_oc_h

#import <Foundation/Foundation.h>

#import "ZegoAudioCapture.h"

@interface ZegoExternalAudioDevice : NSObject

/**
 设置外部音频设备，包括外部音频采集和外部音频渲染
 
 @param enable true 开启，false 关闭
 @discussion 必须在InitSDK之前设置；
 @discussion 开发者采用外部采集和渲染后，SDK 内部不负责声音增强、噪音抑制、回音消除等功能，需要用户自己实现。
 */
+ (void)enableExternalAudioDevice:(bool)enable;

/**
 获取 IAudioDataInOutput 对象
 
 @return IAudioDataInOutput 对象，参考 IAudioDataInOutput 类型
 @discussion 必须在InitSDK之后调用。
 */
+ (AVE::IAudioDataInOutput *)getIAudioDataInOutput;
 
@end

#endif /* zego_api_external_audio_device_oc_h */

