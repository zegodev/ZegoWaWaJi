#ifndef zego_api_mix_stream_oc_h
#define zego_api_mix_stream_oc_h

#import <Foundation/Foundation.h>
#include "zego-api-defines-oc.h"


/** 混流不存在的流名，值为 NSArray<NSString*>* */
ZEGO_EXTERN NSString *const kZegoMixNonExistsStreamIDKey;
/** 混流请求 seq，值为 @(int) */
ZEGO_EXTERN NSString *const kZegoMixStreamReqSeqKey;


@protocol ZegoMixStreamDelegate <NSObject>

@optional
/**
 混流配置更新结果回调
 
 @param errorCode 错误码，0 表示没有错误
 @param mixStreamID 混流ID
 @param info 混流播放信息
 @discussion 调用 [ZegoStreamMixer -mixStream:seq:] 更新混流配置后，通过此 API 通知调用方
 @note 常见错误码及其含义如下：
 errorCode = 150，混流的输入流不存在。
 errorCode = 151，混流失败。
 errorCode = 152，停止混流失败。
 errorCode = 153，输入参数错误。
 errorCode = 154，输出参数错误。
 errorCode = 155，输入分辨率格式错误。
 errorCode = 156，输出分辨率格式错误。
 errorCode = 157，混流没开。
 */
- (void)onMixStreamConfigUpdate:(int)errorCode mixStream:(NSString *)mixStreamID streamInfo:(NSDictionary *)info;

@end


@interface ZegoStreamMixer : NSObject

- (void)setDelegate:(id<ZegoMixStreamDelegate>)delegate;

/**
 开始混流
 
 @param completeMixConfig 混流配置
 @param seq 请求序号，回调会带回次 seq
 @return true 成功，等待回调，false 失败
 @discussion 每次需要更新混流配置时，都可以调用此接口；如果需要多次调用，可以通过传入不同的 seq 区分回调
 */
- (bool)mixStream:(ZegoCompleteMixStreamConfig *)completeMixConfig seq:(int)seq;

@end

#endif /* zego_api_mix_stream_oc_h */
