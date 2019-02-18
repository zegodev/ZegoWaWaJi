//
//  zego-api-audio-frame-oc.h
//  ZegoLiveRoom
//

#import <Foundation/Foundation.h>

/** 音频帧类型 */
typedef enum ZegoAPIAudioFrameType
{
    kZegoAPIAudioFrameTypePCM          = 0x1001,   /** PCM 数据 */
    kZegoAPIAudioFrameTypeAACStream    = 0x1003,   /** AAC 编码数据流 */
} ZegoAPIAudioFrameType;

/**
 音频帧结构
 */
@interface ZegoAPIAudioFrame : NSObject

/**
 设置采集配置

 @param sampleRate 采样率
 @param channels 通道数，支持 1(单声道) 或 2(立体声)
 @return 参考 enum ZegoAPIErrorCode
 */
- (int)setSampleRate:(int)sampleRate channels:(int)channels;

/**
 设置采集到的数据

 @param data 采集到的数据，注意：内部不会拷贝数据，需要由调用方管理其生命周期
 @param samples 采样数
 @return 参考 enum ZegoAPIErrorCode
 @attention 仅支持 16 比特量化深度
 */
- (int)setCapturedData:(unsigned char*)data samples:(int)samples;

/**
 设置帧数据类型

 @param type 帧数据类型，参考 enum ZegoAudioFrameType，默认 kZegoAudioFrameTypePCM
 @return 参考 enum ZegoAPIErrorCode
 */
- (int)setFrameType:(ZegoAPIAudioFrameType)type;

/**
 设置 AAC 编码数据属性

 @param timestamp 时间戳，如果数据中只有 AAC 配置信息，填 0
 @param dataLength 总数据长度，注意 dataLength = AAC编码结果长度 + specialConfigLength
 @param specialConfigLength AAC 配置信息长度
 @return 参考 enum ZegoAPIErrorCode
 */
- (int)setAacProperties:(double)timestamp dataLength:(int)dataLength specialConfigLength:(int)specialConfigLength;

@end

