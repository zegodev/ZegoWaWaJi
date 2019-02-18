//
//  zego-api-external-video-render-oc.h
//  zegoavkit
//
//  Copyright © 2017年 Zego. All rights reserved.
//

#ifndef zego_api_external_video_render_oc_h
#define zego_api_external_video_render_oc_h

#include <Foundation/Foundation.h>
#include <AVFoundation/AVFoundation.h>
#include "zego-api-defines-oc.h"


@protocol ZegoExternalVideoRenderDelegate <NSObject>

/**
 SDK 从用户端获取 PixelBuffer 地址
 
 @param width 视频宽度
 @param height 视频高度
 @param cvPixelFormatType format type，用来创建CVPixelBufferRef对象
 @return CVPixelBufferRef 对象
 @discussion 开启外部渲染，设置外部渲染代理对象成功后，SDK 通过此 API 从用户端获取 PixelBuffer 地址。SDK 获取到用户指定的 PixelBuffer 后，将采集的视频源数据拷贝进去
 */
- (CVPixelBufferRef)onCreateInputBufferWithWidth:(int)width height:(int)height cvPixelFormatType:(OSType)cvPixelFormatType;

/**
 SDK 拷贝视频数据完成通知
 
 @param pixelBuffer 拷贝完成的 PixelBuffer 地址
 @param streamID 流名
 @discussion SDK 通过此回调通知用户数据拷贝完成。当外部渲染拉流数据，streamID 为拉流流名；当外部渲染推流数据，streamID 为常量 kZegoVideoDataMainPublishingStream 时表示第一路推流数据；streamID 为常量 kZegoVideoDataAuxPublishingStream 时表示第二路推流数据
 */
- (void)onPixelBufferCopyed:(CVPixelBufferRef)pixelBuffer streamID:(NSString *)streamID;

@end

/**
 视频外部渲染类型
 
 - VideoExternalRenderTypeDecodeRgbSeries: 当外部视频渲染回调时，抛解码后数据(kCVPixelFormatType_32BGRA)，SDK内部渲染无效
 - VideoExternalRenderTypeDecodeRender: 当外部视频渲染回调时，抛解码后数据(kCVPixelFormatType_32BGRA)，SDK内部渲染有效
 */
typedef NS_ENUM(NSInteger, VideoExternalRenderType) {
    
    VideoExternalRenderTypeDecodeRgbSeries = 0,
    VideoExternalRenderTypeDecodeRender = 3
};

@interface ZegoExternalVideoRender : NSObject


/**
 获取ZegoExternalVideoRender 单例

 @return ZegoExternalVideoRender 单例对象
 */
+ (instancetype)sharedInstance;

/**
 设置外部渲染回调

 @param delegate 外部渲染回调代理
 @discussion 未设置代理，不会有视频数据回调
 */
- (void)setExternalVideoRenderDelegate:(id<ZegoExternalVideoRenderDelegate>)delegate;


/**
 设置是否开启外部渲染

 @param enable 开启
 @param type 视频外部渲染类型
 */
+ (void)enableExternalVideoRender:(BOOL)enable type:(VideoExternalRenderType)type;

/**
 设置当VideoExternalRenderType为DECODE_RENDER时，是否开启外部视频渲染（拉流）
 
 @param enable true 开启， false 不开启，默认为不开启
 @param streamID 流ID
 @note 只要当VideoExternalRenderType设置为DECODE_RENDER，该接口才有效
 */
+ (bool)enableVideoRender:(BOOL)enable streamID:(NSString *)streamID;

/**
 设置当VideoExternalRenderType为DECODE_RENDER时，是否开启外部视频渲染（推流预览）
 
 @param enable true 开启， false 不开启，默认为不开启
 @param channelIndex 推流通道，默认为主通道
 @note 只要当VideoExternalRenderType设置为DECODE_RENDER，该接口才有效
 */
+ (bool)enableVideoPreview:(BOOL)enable channelIndex:(ZegoAPIPublishChannelIndex)channelIndex;

@end

#endif /* zego_api_external_video_render_oc_h */
