//
//  ZegoVideoCapture.h
//  zegoavkit
//
//  Copyright © 2016 Zego. All rights reserved.
//

#ifndef ZegoVideoCapture_h
#define ZegoVideoCapture_h


#import "zego-api-external-video-capture-oc.h"
#import "zego-api-external-video-filter-oc.h"


/** 视频外部采集接口 */
@protocol ZegoSupportsVideoCapture

@optional
/**
 设置视频采样帧率回调

 @param framerate 帧率
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setFrameRate:(int)framerate;

/**
 设置视频采集分辨率回调

 @param width 宽
 @param height 高
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setWidth:(int)width andHeight:(int)height;

/**
 切换前后摄像头回调

 @param bFront true 表示前摄像头，false 表示后摄像头
 @return 0 切换成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setFrontCam:(int)bFront;

#if TARGET_OS_IPHONE
/**
 设置采集使用载体视图回调，移动端使用

 @param view 载体视图
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setView:(UIView* _Nullable )view;
#elif TARGET_OS_OSX

/**
 设置采集使用载体视图回调，PC 端使用

 @param view 载体视图
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setView:(NSView* _Nullable )view;
#endif

/**
 设置采集预览的模式回调

 @param mode 预览模式
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setViewMode:(int)mode;

/**
 设置采集预览的逆时针旋转角度回调

 @param rotation 旋转角度
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setViewRotation:(int)rotation;

/**
 设置采集 buffer 的顺时针旋转角度回调

 @param rotaion 旋转角度
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_setCaptureRotation:(int)rotaion;

/**
 启动预览回调

 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_startPreview;

/**
 停止预览回调

 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_stopPreview;

/**
 开启手电筒回调

 @param enable true 开启，false 不开启
 @return 0 设置成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_enableTorch:(bool)enable;

/**
 对采集预览进行截图回调

 @return 0 截图成功，其他值失败
 @discussion 调用 SDK 相关接口设置成功后，会通过此 API 通知调用者
 */
- (int)zego_takeSnapshot;

/**
 
 @warning Deprecated
 */
- (int)zego_setPowerlineFreq:(unsigned int)freq __attribute__ ((deprecated));

@end

#endif
