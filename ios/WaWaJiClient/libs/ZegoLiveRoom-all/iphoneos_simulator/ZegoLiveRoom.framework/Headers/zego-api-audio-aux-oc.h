//
//  zego-api-audio-aux-oc.h
//

#import <Foundation/Foundation.h>
@protocol ZegoAudioAuxDelgate <NSObject>

@optional

/**
 混音数据输入回调
 
 @param pData 混音数据
 <p><b>注意：</b>
 1. 最大支持 48k 采样率、双声道、16位深的 PCM 音频数据；<br>
 2. 实际数据长度应根据当前音频数据的采样率及声道数决定；<br>
 3. 为确保混音效果，请不要在此 API 中执行耗时操作</p>
 @param pDataLen pDataLen既是输入参数也是输出参数；
                 作为输入参数，SDK会提供好长度值，用户按照这个长度写入数据即可，数据充足的情况下，无需更改*pDataLen的值
                 作为输出参数，如果填写的数据不足SDK提供的长度值，则*pDataLen = 0,
                 或者最后的尾音不足 SDK提供的长度值，可以用静音数据补齐。
 @param pSampleRate 混音数据采样率，支持16k、32k、44.1k、48k
 @param pChannelCount 混音数据声道数，支持1、2
 @discussion 用户调用该 API 将混音数据传递给 SDK。混音数据 bit depth 必须为 16
 */
- (void)onAuxCallback:(void *)pData dataLen:(int *)pDataLen sampleRate:(int *)pSampleRate channelCount:(int *)pChannelCount;

/** 混音音频数据输入回调。
当开启混音后，通过此回调获取混音音频数据。
发送媒体次要信息前需要调用setMediaSideFlags设置媒体次要信息开关
此接口带媒体次要信息时必须要带有混音音频数据才能发送媒体次要信息
如果不带混音音频数据则会丢掉媒体次要信息
<p><b>注意：</b>
   1. 最大支持 48k 采样率、双声道、16位深的 PCM 音频数据；<br>
   2. 实际数据长度应根据当前音频数据的采样率及声道数决定；<br>
   3. 为确保混音效果，请不要在此 API 中执行耗时操作</p>
 @param pDataLen pDataLen既是输入参数也是输出参数；
                 作为输入参数，SDK会提供好长度值，用户按照这个长度写入数据即可，数据充足的情况下，无需更改*pDataLen的值
                 作为输出参数，如果填写的数据不足SDK提供的长度值，则*pDataLen = 0,
                 或者最后的尾音不足 SDK提供的长度值，可以用静音数据补齐。
 @param pSampleRate 混音数据采样率，支持16k、32k、44.1k、48k
 @param pNumChannels 混音数据声道数，支持1、2
 @param pSideInfo 媒体次要信息数据缓冲区地址
 @param pSideInfoLength 媒体次要信息数据缓冲区长度
 @param bPacket  媒体次要信息数据是否外部已经打包好包头
 @attention 用户调用该 API 将混音数据传递给 SDK
 @note 混音数据 bit depth 必须为 16
 */
- (void)onAuxCallback:(void *)pData dataLen:(int *)pDataLen sampleRate:(int *)pSampleRate channelCount:(int *)pChannelCount sideInfo:(unsigned char*)pSideInfo sideInfoLen:(int*) pSideInfoLength packet:(bool*)pPacket;

@end

@interface ZegoAudioAux : NSObject

/**
 设置混音代理
 @param delegate 混音代理
 */
- (void)setDelegate:(id<ZegoAudioAuxDelgate>) delegate;

/**
 混音开关
 
 @param enable true 启用混音输入，false 关闭混音输入。默认 false
 @return true 成功，false 失败
 @discussion 主播端开启混音后，SDK 在 [ZegoAudioAuxDelgate -onAuxCallback:dataLen:sampleRate:channelCount:] 中获取混音输入数据
 */
- (bool)enableAux:(BOOL)enable;

@end
