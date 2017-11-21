//
//  ZegoLiveRoomApi-ReplayLive.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#import "ZegoLiveRoomApi.h"
#import <ReplayKit/ReplayKit.h>

@interface ZegoLiveRoomApi (ReplayLive)

/**
 初始化 ReplayLive
 
 @discussion 必须在 InitSDK 前调用
 */
+ (void)prepareReplayLiveCapture;

/**
 处理视频数据
 
 @param sampleBuffer ReplayLiveKit 返回的视频数据
 */
- (void)handleVideoInputSampleBuffer:(CMSampleBufferRef)sampleBuffer;

/**
 处理音频数据
 
 @param sampleBuffer ReplayLiveKit 返回的音频数据
 @param sampleBufferType 类型 RPSampleBufferTypeAudioApp, RPSampleBufferTypeAudioMic
 */
- (void)handleAudioInputSampleBuffer:(CMSampleBufferRef)sampleBuffer withType:(RPSampleBufferType)sampleBufferType;

@end
