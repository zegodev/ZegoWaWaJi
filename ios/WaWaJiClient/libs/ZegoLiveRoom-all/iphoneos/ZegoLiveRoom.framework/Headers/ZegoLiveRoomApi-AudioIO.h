//
//  ZegoLiveRoomApi-AudioIO.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#ifndef ZegoLiveRoomApi_AudioIO_h
#define ZegoLiveRoomApi_AudioIO_h

#import "ZegoLiveRoomApi.h"
#import "ZegoAudioCapture.h"

@interface ZegoLiveRoomApi (AudioIO)

/**
 启用外部音频采集
 
 @param enable true 启用，false 不启用。默认 false
 @discussion 必须在初始化 SDK 前调用
 */
+ (void)enableExternalAudioDevice:(bool)enable;

/**
 获取 IAudioDataInOutput 对象
 
 @return IAudioDataInOutput 对象
 */
- (AVE::IAudioDataInOutput *)getIAudioDataInOutput;

/**
 耳机插入状态下是否使用回声消除
 
 @param enable true 使用，false 不使用。默认 false
 @discussion 初始化 SDK 后，推流之前调用
 */
- (void)enableAECWhenHeadsetDetected:(bool)enable;

/**
 设置音频前处理函数
 
 @warning Deprecated，请使用 [ZegoLiveRoomApi (AudioIO) setAudioPrep2:dataCallback:]
 */
+ (void)setAudioPrep:(void(*)(const short* inData, int inSamples, int sampleRate, short *outData))prep;

/**
 设置音频前处理函数
 
 @param prepSet 预处理的采样率等参数设置
 @param callback 采样数据回调
 @discussion 必须在初始化 SDK 前调用。inFrame 为采集的音频数据，outFrame 为处理后返回给SDK的数据
 */
+ (void)setAudioPrep2:(AVE::ExtPrepSet)prepSet dataCallback:(void(*)(const AVE::AudioFrame& inFrame, AVE::AudioFrame& outFrame))callback;

@end

#endif /* ZegoLiveRoomApi_AudioIO_h */
