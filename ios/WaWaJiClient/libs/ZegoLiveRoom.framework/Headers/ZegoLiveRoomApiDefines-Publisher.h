//
//  ZegoLiveRoomApiDefines-Publisher.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#ifndef ZegoLiveRoomApiDefines_Publisher_h
#define ZegoLiveRoomApiDefines_Publisher_h

/** 预设直播配置 */
typedef enum {
    /** 超低质量 */
    ZegoAVConfigPreset_Verylow  = 0,
    /** 低质量 */
    ZegoAVConfigPreset_Low      = 1,
    /** 标准质量 */
    ZegoAVConfigPreset_Generic  = 2,
    /** 高质量，手机端直播建议使用High配置，效果最优 */
    ZegoAVConfigPreset_High     = 3,
    /** 超高质量 */
    ZegoAVConfigPreset_Veryhigh = 4,
    /**极高质量 */
    ZegoAVConfigPreset_Superhigh = 5
} ZegoAVConfigPreset;

/** 视频帧率 */
typedef enum {
    /**  超低质量下的视频帧率 */
    ZegoAVConfigVideoFps_Verylow    = 5,
    /**  低质量下的视频帧率 */
    ZegoAVConfigVideoFps_Low        = 10,
    /**  标准质量下的视频帧率 */
    ZegoAVConfigVideoFps_Generic    = 15,
    /**  高质量下的视频帧率 */
    ZegoAVConfigVideoFps_High       = 20,
    /**  超高质量下的视频帧率 */
    ZegoAVConfigVideoFps_Veryhigh   = 25,
    /**  极高质量下的视频帧率 */
    ZegoAVConfigVideoFps_Superhigh  = 30
} ZegoAVConfigVideoFps;

/** 视频码率 */
typedef enum {
    /**  超低质量下的视频码率 */
    ZegoAVConfigVideoBitrate_Verylow    = 300*1000,
    /**  低质量下的视频码率 */
    ZegoAVConfigVideoBitrate_Low        = 400*1000,
    /**  标准质量下的视频码率 */
    ZegoAVConfigVideoBitrate_Generic    = 600*1000,
    /**  高质量下的视频码率 */
    ZegoAVConfigVideoBitrate_High       = 1200*1000,
    /**  超高质量下的视频码率 */
    ZegoAVConfigVideoBitrate_Veryhigh   = 1500*1000,
    /**  极高质量下的视频码率 */
    ZegoAVConfigVideoBitrate_Superhigh  = 3000*1000
} ZegoAVConfigVideoBitrate;

/** 直播配置 */
@interface ZegoAVConfig : NSObject

/**
 获取不同质量的直播配置实例
 
 @param preset 预设直播质量
 @return ZegoAVConfig 实例
 @discussion 直播前要预设直播配置
 */
+ (instancetype)presetConfigOf:(ZegoAVConfigPreset)preset;

/**  视频编码输出分辨率 */
@property (assign) CGSize videoEncodeResolution;
/**  视频采集分辨率 */
@property (assign) CGSize videoCaptureResolution;
/**  视频帧率 */
@property (assign) int fps;
/**  视频码率 */
@property (assign) int bitrate;

@end

/** 视频采集缩放时机 */
typedef enum : NSUInteger {
    /** 采集后立即进行缩放，默认 */
    ZEGOAPI_CAPTURE_PIPELINE_SCALE_MODE_PRE = 0,
    /** 编码时进行缩放 */
    ZEGOAPI_CAPTURE_PIPELINE_SCALE_MODE_POST = 1,
} ZegoAPICapturePipelineScaleMode;

#endif /* ZegoLiveRoomApiDefines_Publisher_h */
