//
//  zego_camera_ios.h
//  zegoavkit
//
//  Created by summeryxia on 2018/04/11.
//  Copyright © 2018年 Zego. All rights reserved.
//

#ifndef zego_api_camera_oc_h
#define zego_api_camera_oc_h

#import <Foundation/Foundation.h>
#import "zego-api-defines-oc.h"

@interface ZegoCamera : NSObject

/**
 设置曝光点
 
 @param point，x 和 y 的取值范围均为 [0.0, 1.0]
 @param index 推流 channel Index
 @discussion 必须在初始化 SDK 后调用
 @discussion 屏幕左上角坐标为 (0.0,0.0)，右下角坐标为(1.0,1.0)
 */
+ (BOOL)setCamExposurePoint:(CGPoint)point channelIndex:(ZegoAPIPublishChannelIndex)index;

@end

#endif
