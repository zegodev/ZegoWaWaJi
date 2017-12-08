//
//  zego-api-external-video-filter-oc.h
//  zegoavkit
//
//  Created by Randy Qiu on 2017/8/30.
//  Copyright © 2017年 Zego. All rights reserved.
//

#ifndef zego_api_external_video_filter_oc_h
#define zego_api_external_video_filter_oc_h

#import <Foundation/Foundation.h>
//#import <UIKit/UIKit.h>
#import <AVFoundation/AVFoundation.h>
#import "zego-api-defines-oc.h"

/** 视频缓冲区类型 */
typedef NS_ENUM(NSInteger, ZegoVideoBufferType) {
    /** 未知 */
    ZegoVideoBufferTypeUnknown = 0,
    /** 异步 */
    ZegoVideoBufferTypeAsyncPixelBuffer = 1 << 1,
    /** 同步 */
    ZegoVideoBufferTypeSyncPixelBuffer = 1 << 2,
    /** 异步I420 */
    ZegoVideoBufferTypeAsyncI420PixelBuffer = 1 << 7,
};

/** 外部滤镜内存池协议（用于 SDK 与开发者间相互传递外部滤镜数据） */
@protocol ZegoVideoBufferPool <NSObject>

/**
 SDK 获取 CVPixelBufferRef 对象
 
 @param width 高度
 @param height 宽度
 @param stride 视频帧数据每一行字节数
 @return CVPixelBufferRef CVPixelBufferRef 对象
 @discussion 开发者调用此 API 向 SDK 返回 CVPixelBufferRef 对象，用于保存视频帧数据
 */
- (nullable CVPixelBufferRef)dequeueInputBuffer:(int)width height:(int)height stride:(int)stride;

/**
 异步处理视频帧数据
 
 @param pixel_buffer 视频帧数据
 @param timestamp_100n 当前时间戳
 @discussion 开发者在此 API 中获取采集的视频帧数据
 */
- (void)queueInputBuffer:(nonnull CVPixelBufferRef)pixel_buffer timestamp:(unsigned long long)timestamp_100n;

@end


/** 外部滤镜同步回调 */
@protocol ZegoVideoFilterDelegate <NSObject>
/**
 同步处理视频帧数据
 
 @param pixel_buffer 视频帧数据
 @param timestamp_100 当前时间戳
 */
- (void)onProcess:(nonnull CVPixelBufferRef)pixel_buffer withTimeStatmp:(unsigned long long)timestamp_100;

@end


/** 外部滤镜客户端接口 */
@protocol ZegoVideoFilterClient <NSObject>

/**
 销毁外部滤镜客户端
 */
- (void)destroy;
@end


/** 外部滤镜 */
@protocol ZegoVideoFilter

@required

/**
 初始化外部滤镜使用的资源
 
 @param client 外部滤镜客户端，主要用于向 SDK 传递数据
 */
- (void)zego_allocateAndStart:(nonnull id<ZegoVideoFilterClient>) client;

/**
 停止并释放外部滤镜占用的资源
 */
- (void)zego_stopAndDeAllocate;

/**
 支持的 buffer 类型
 
 @return buffer 类型，参考 ZegoVideoBufferType 定义
 */
- (ZegoVideoBufferType)supportBufferType;

@end


/** 外部滤镜工厂接口 */
@protocol ZegoVideoFilterFactory <NSObject>

@required
/**
 创建外部滤镜
 
 @return 外部滤镜实例
 */
- (nonnull id<ZegoVideoFilter>)zego_create;

/**
 销毁外部滤镜
 
 @param filter 外部滤镜
 */
- (void)zego_destroy:(nonnull id<ZegoVideoFilter>)filter;

@end


@interface ZegoExternalVideoFilter : NSObject

/**
 设置外部滤镜模块

 @param factory 工厂对象
 @param idx 必须在 Init 前调用，并且不能置空
 */
+ (void)setVideoFilterFactory:(nullable id<ZegoVideoFilterFactory>)factory channelIndex:(ZegoAPIPublishChannelIndex)idx;

@end


@interface ZegoDefaultVideoFilterFactory : NSObject<ZegoVideoFilterFactory>

/**
 获取滤镜工厂单例

 @return 单例对象
 */
+ (nonnull instancetype)sharedInstance;

/**
 设置滤镜实现

 @param filter 滤镜实现
 */
- (void)setFilter:(nullable id<ZegoVideoFilter>)filter;

/**
 是否启用默认滤镜工厂

 @param enable true 启用，false 关闭
 @discussion 必须在创建主 SDK 接口对象前，或销毁主 SDK 接口对象后调用
 */
- (void)enableFilter:(bool)enable;

@end;


@interface ZegoExternalVideoFilterUtils : NSObject

+ (bool)createPixelBufferPool:(_Nonnull CVPixelBufferPoolRef * _Nonnull)pool width:(int)width height:(int)height;
+ (void)destroyPixelBufferPool:(_Nonnull CVPixelBufferPoolRef * _Nonnull)pool;
+ (bool)copyPixelBufferFrom:(_Nonnull CVPixelBufferRef)src to:(_Nonnull CVPixelBufferRef)dst;

@end

#endif /* zego_api_external_video_filter_oc_h */
