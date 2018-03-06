//
//  zego-api-external-video-capture-oc.h
//  zegoavkit
//
//  Created by Randy Qiu on 2017/8/18.
//  Copyright © 2017年 Zego. All rights reserved.
//

#ifndef zego_api_external_video_capture_oc_h
#define zego_api_external_video_capture_oc_h

#import <Foundation/Foundation.h>
#import <AVFoundation/AVFoundation.h>
#if TARGET_OS_IPHONE
#import <UIKit/UIKit.h>
#elif TARGET_OS_OSX
#import <AppKit/AppKit.h>
#endif

#import "zego-api-defines-oc.h"

/** 视频填充模式 */
typedef enum : NSUInteger {
    /** 等比缩放，可能有黑边 */
    ZegoVideoFillModeBlackBar,
    /** 裁剪 */
    ZegoVideoFillModeCrop,
    /** 拉伸 */
    ZegoVideoFillModeStretch,
} ZegoVideoFillMode;

/** 视频采集设备输出格式 */
typedef enum : NSUInteger {
    /** CVImageBufferRef */
    ZegoVideoCaptureDeviceOutputBufferTypeCVPixelBuffer,
    /** GLuint */
    ZegoVideoCaptureDeviceOutputBufferTypeGlTexture2D,
    /** EncodedFrame */
    ZegoVideoCaptureDeviceOutputBufferTypeEncodedFrame,
} ZegoVideoCaptureDeviceOutputBufferType;

/** 视频编码器格式 */
typedef enum : NSUInteger {
    /** AVC_AVCC 格式 */
    ZegoVideoCodecTypeAVCAVCC    = 0,
    /** AVC_ANNEXB 格式 */
    ZegoVideoCodecTypeAVCANNEXB  = 1,
} ZegoVideoCodecType;

/** 视频编码配置 */
typedef struct {
    /** 编码宽度 */
    int width;
    /** 编码高度 */
    int height;
    /** 编码格式 */
    ZegoVideoCodecType codecType;
} ZegoVideoCodecConfig;

/** 视频外部采集代理 */
@protocol ZegoVideoCaptureDelegate <NSObject>

/**
 接收视频帧数据
 
 @param image 采集到的视频数据
 @param time 采集时间戳
 @discussion 设置成功视频外部采集对象，并启动采集后，在此通知中获取视频帧数据
 */
- (void)onIncomingCapturedData:(nonnull CVImageBufferRef)image withPresentationTimeStamp:(CMTime)time;

/**
 接收视频帧数据
 
 @param textureID texture ID, 可以通过 CVOpenGLESTextureGetName(texture) 取得
 @param width 帧宽
 @param height 帧高
 @param time 采集时间戳
 */
- (void)onIncomingCapturedData:(GLuint)textureID width:(int)width height:(int)height withPresentationTimeStamp:(CMTime)time;

/**
 接受已编码的视频帧数据

 @param data 已编码数据
 @param config 编码配置，请参考 ZegoVideoCodecConfig 定义
 @param bKeyframe 是否为关键帧
 @param time 采集到该帧的时间戳，用于音画同步，如果采集实现是摄像头，最好使用系统采集回调的原始时间戳。如果不是，最好是生成该帧的UTC时间戳
 */
- (void)onEncodedFrame:(nonnull NSData *)data config:(ZegoVideoCodecConfig)config bKeyframe:(bool)bKeyframe withPresentationTimeStamp:(CMTime)time;

@optional

/**
 
 @warning Deprecated
 */
- (void)onTakeSnapshot:(nonnull CGImageRef)image __attribute__ ((deprecated));

@end


/** 视频外部采集客户端代理 */
@protocol ZegoVideoCaptureClientDelegate <NSObject, ZegoVideoCaptureDelegate>

/**
 销毁
 
 @discussion 调用者需要在此 API 中进行相关的销毁操作
 */
- (void)destroy;

/**
 错误信息
 
 @param reason 错误原因
 */
- (void)onError:(nullable NSString*)reason;

- (void)setFillMode:(ZegoVideoFillMode)mode;

@end


@protocol ZegoSupportsVideoCapture;

/** 视频外部采集设备接口 */
@protocol ZegoVideoCaptureDevice <NSObject, ZegoSupportsVideoCapture>

@required

/**
 初始化采集使用的资源（例如启动线程等）回调
 
 @param client SDK 实现回调的对象，一定要保存
 @discussion 第一次调用开始预览／推流／拉流时调用
 */
- (void)zego_allocateAndStart:(nonnull id<ZegoVideoCaptureClientDelegate>) client;

/**
 停止并且释放采集占用的资源
 
 @discussion 在此之后，不能再调用 client 对象的接口
 */
- (void)zego_stopAndDeAllocate;

/**
 启动采集，采集的数据通过 [client -onIncomingCapturedData:withPresentationTimeStamp:] 通知 SDK
 
 @return 0 表示成功，其他是错误
 @discussion 一定要实现，不要做丢帧逻辑，SDK内部已经包含了丢帧策略
 */
- (int)zego_startCapture;

/**
 停止采集
 
 @return 0 表示成功，其它是错误
 @discussion 一定要实现
 */
- (int)zego_stopCapture;


@optional
/**
 支持的 buffer 类型
 
 @return 支持的 buffer 类型
 @discussion 如果不实现，则为 ZegoVideoCaptureDeviceOutputBufferTypeCVPixelBuffer
 */
- (ZegoVideoCaptureDeviceOutputBufferType)zego_supportBufferType;

@end

/** 视频外部采集工厂接口 */
@protocol ZegoVideoCaptureFactory <NSObject>

@required

/**
 创建采集设备
 
 @param deviceId 设备 Id
 @return 采集设备实例
 @discussion 一定要实现
 */
- (nonnull id<ZegoVideoCaptureDevice>)zego_create:(nonnull NSString*)deviceId;

/**
 销毁采集设备
 
 @param device zego_create返回的采集设备实例
 @discussion 一定要实现
 */
- (void)zego_destroy:(nonnull id<ZegoVideoCaptureDevice>)device;

@end

@interface ZegoExternalVideoCapture : NSObject

+ (void)setVideoCaptureFactory:(nullable id<ZegoVideoCaptureFactory>)factory channelIndex:(ZegoAPIPublishChannelIndex)idx;

@end

#endif /* zego_api_external_video_capture_oc_h */
