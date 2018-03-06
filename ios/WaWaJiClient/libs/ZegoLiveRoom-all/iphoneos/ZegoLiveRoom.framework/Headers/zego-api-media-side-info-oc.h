#ifndef zego_api_media_side_info_oc_h
#define zego_api_media_side_info_oc_h

#import <Foundation/Foundation.h>
#include "zego-api-defines-oc.h"

@protocol ZegoMediaSideDelegate <NSObject>
@required
/**
 媒体次要信息回调
 
 @param  data：接收到的信息数据（具体内容参考官网对应文档中的格式说明）
 @param  streamID：流ID，标记当前回调的信息属于哪条流
 */
- (void)onRecvMediaSideInfo:(NSData *)data ofStream:(NSString *)streamID;

@end

@interface ZegoMediaSideInfo : NSObject

/**
 发送媒体次要信息开关
 
 @param start true 开启, false 关闭
 @param onlyAudioPublish true 纯音频直播，不传输视频数据, false 音视频直播，传输视频数据
 @param index 推流 channel Index
 @discussion 必须在InitSDK之后、推流之前，设置
 @discussion onlyAudioPublish 开关在 start 开关开启时才生效
 */
- (void)setMediaSideFlags:(bool)start onlyAudioPublish:(bool)onlyAudioPublish channelIndex:(ZegoAPIPublishChannelIndex)index;

/**
 发送媒体次要信息
 
 @param data 媒体次要信息数据
 @param packet 是否外部已经打包好包头，true 已打包, false 未打包
 @param index 推流 channel Index
 @discussion 必须在推流成功后调用
 */
- (void)sendMediaSideInfo:(NSData *)data packet:(bool)packet channelIndex:(ZegoAPIPublishChannelIndex)index;


/**
 设置回调, 接收媒体次要信息
 
 @param delegate 媒体次要信息回调
 @discussion InitSDK之后、开始拉流前，设置。当不需要接收数据时，必须将 delegate 置空，避免内存泄漏
 */
- (void)setMediaSideDelegate:(id<ZegoMediaSideDelegate>)delegate;

@end

#endif
