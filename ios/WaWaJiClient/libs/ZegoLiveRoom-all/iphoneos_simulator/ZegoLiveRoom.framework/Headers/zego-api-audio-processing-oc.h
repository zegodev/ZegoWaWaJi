#ifndef zego_api_audio_processing_oc_h
#define zego_api_audio_processing_oc_h

#import <Foundation/Foundation.h>

/** 音频混响模式 */
typedef enum : NSUInteger
{
    ZEGOAPI_AUDIO_REVERB_MODE_SOFT_ROOM = 0,
    ZEGOAPI_AUDIO_REVERB_MODE_WARM_CLUB = 1,
    ZEGOAPI_AUDIO_REVERB_MODE_CONCERT_HALL = 2,
    ZEGOAPI_AUDIO_REVERB_MODE_LARGE_AUDITORIUM = 3,
    
} ZegoAPIAudioReverbMode;

@interface ZegoAudioProcessing : NSObject

/**
 设置虚拟立体声
 
 @param enable true 开启，false 关闭
 @param angle 虚拟立体声中声源的角度，范围为0～180，90为正前方，0和180分别对应最右边和最左边
 @return true 成功，false 失败
 @discussion 必须在初始化 SDK 后调用，并且需要设置双声道(参考 setAudioChannelCount)，
             推流成功后动态设置不同的 angle 都会生效
 */
+ (bool)enableVirtualStereo:(bool)enable angle:(int)angle;

/**
 设置音频混响
 
 @param enable true 开启，false 关闭
 @param mode 混响模式，参考 ZegoAPIAudioReverbMode
 @return true 成功，false 失败
 @discussion 必须在初始化 SDK 后调用，推流成功后动态设置不同的 mode 都会生效
 */
+ (bool)enableReverb:(bool)enable mode:(ZegoAPIAudioReverbMode)mode;

@end

#endif /* zego_api_audio_processing_oc_h */
