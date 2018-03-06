//
//  zego-api-defines-oc.h
//  zegoavkit
//
//  Copyright © 2017年 Zego. All rights reserved.
//

#ifndef zego_api_defines_oc_h
#define zego_api_defines_oc_h

#import <Foundation/Foundation.h>
#import <CoreGraphics/CoreGraphics.h>

#ifdef __cplusplus
#define ZEGO_EXTERN     extern "C"
#else
#define ZEGO_EXTERN     extern
#endif

ZEGO_EXTERN NSString *const kZegoStreamIDKey;           ///< 流ID，值为 NSString
ZEGO_EXTERN NSString *const kZegoMixStreamIDKey;        ///< 混流ID，值为 NSString

/** 流信息列表项 */
/** rtmp 播放 url 列表，值为 <NSArrayNSString *> */
ZEGO_EXTERN NSString *const kZegoRtmpUrlListKey;
/** hls 播放 url 列表，值为 <NSArrayNSString *> */
ZEGO_EXTERN NSString *const kZegoHlsUrlListKey;
/** flv 播放 url 列表，值为 <NSArrayNSString *> */
ZEGO_EXTERN NSString *const kZegoFlvUrlListKey;

/** 设备项 */
/** 摄像头设备 */
ZEGO_EXTERN NSString *const kZegoDeviceCameraName;
/** 麦克风设备 */
ZEGO_EXTERN NSString *const kZegoDeviceMicrophoneName;

ZEGO_EXTERN NSString *const kMixStreamAudioOutputFormat; ///< 混流输出格式，值为 NSNumber，可选 {0, 1}

/** 自定义转推 RTMP 地址 */
ZEGO_EXTERN NSString *const kPublishCustomTarget;

/** AudioSession相关配置信息的key, 值为 NSString */
ZEGO_EXTERN NSString *const kZegoConfigKeepAudioSesionActive;

typedef unsigned int	uint32;

/** 配置返回错误类型 */
typedef enum {
    /** 分辨率 */
    FLAG_RESOLUTION = 0x1,
    /** 帧率 */
    FLAG_FPS = 0x2,
    /** 比特率 */
    FLAG_BITRATE = 0x4
} SetConfigReturnType;

/** 本地预览视频视图的模式 */
typedef enum {
    /** 等比缩放，可能有黑边 */
    ZegoVideoViewModeScaleAspectFit     = 0,
    /** 等比缩放填充整View，可能有部分被裁减 */
    ZegoVideoViewModeScaleAspectFill    = 1,
    /** 填充整个View */
    ZegoVideoViewModeScaleToFill        = 2,
} ZegoVideoViewMode;

/** 采集旋转角度，逆时针旋转 */
typedef enum {
    /** 旋转 0 度 */
    CAPTURE_ROTATE_0    = 0,
    /** 旋转 90 度 */
    CAPTURE_ROTATE_90   = 90,
    /** 旋转 180 度 */
    CAPTURE_ROTATE_180  = 180,
    /** 旋转 270 度 */
    CAPTURE_ROTATE_270  = 270
} CAPTURE_ROTATE;

/** 远程视图序号 */
typedef enum {
    /** 第一个远程视图 */
    RemoteViewIndex_First = 0,
    /** 第二个远程视图 */
    RemoteViewIndex_Second = 1,
    /** 第三个远程视图 */
    RemoteViewIndex_Third = 2
} RemoteViewIndex;

/** 滤镜特性 */
typedef enum : NSUInteger {
    /**  不使用滤镜 */
    ZEGO_FILTER_NONE        = 0,
    /**  简洁 */
    ZEGO_FILTER_LOMO        = 1,
    /**  黑白 */
    ZEGO_FILTER_BLACKWHITE  = 2,
    /**  老化 */
    ZEGO_FILTER_OLDSTYLE    = 3,
    /**  哥特 */
    ZEGO_FILTER_GOTHIC      = 4,
    /**  锐色 */
    ZEGO_FILTER_SHARPCOLOR  = 5,
    /**  淡雅 */
    ZEGO_FILTER_FADE        = 6,
    /**  酒红 */
    ZEGO_FILTER_WINE        = 7,
    /**  青柠 */
    ZEGO_FILTER_LIME        = 8,
    /**  浪漫 */
    ZEGO_FILTER_ROMANTIC    = 9,
    /**  光晕 */
    ZEGO_FILTER_HALO        = 10,
    /**  蓝调 */
    ZEGO_FILTER_BLUE        = 11,
    /**  梦幻 */
    ZEGO_FILTER_ILLUSION    = 12,
    /**  夜色 */
    ZEGO_FILTER_DARK        = 13
} ZegoFilter;

/** 美颜特性 */
typedef enum : NSUInteger {
    /**  无美颜 */
    ZEGO_BEAUTIFY_NONE          = 0,
    /**  磨皮 */
    ZEGO_BEAUTIFY_POLISH        = 1,
    /**  全屏美白，一般与磨皮结合使用：ZEGO_BEAUTIFY_POLISH | ZEGO_BEAUTIFY_WHITEN */
    ZEGO_BEAUTIFY_WHITEN        = 1 << 1,
    /**  皮肤美白 */
    ZEGO_BEAUTIFY_SKINWHITEN    = 1 << 2,
    /**  锐化 */
    ZEGO_BEAUTIFY_SHARPEN       = 1 << 3
} ZegoBeautifyFeature;


/** 混流图层信息，原点在左上角 */
@interface ZegoMixStreamInfo : NSObject

/** 要混流的单流ID */
@property (copy) NSString *streamID;
/** 混流图层左上角坐标的第二个值 */
@property int top;
/** 混流图层左上角坐标的第一个值，即左上角坐标为 (left, top) */
@property int left;
/** 混流图层右下角坐标的第二个值 */
@property int bottom;
/** 混流图层左上角坐标的第一个值，即右下角坐标为 (right, bottom) */
@property int right;

/**
 *  原点在左上角，top/bottom/left/right 定义如下：
 *
 *  (left, top)-----------------------
 *  |                                |
 *  |                                |
 *  |                                |
 *  |                                |
 *  -------------------(right, bottom)
 */

@end


/** 混流配置 */
@interface ZegoCompleteMixStreamConfig : NSObject

/**  outputIsUrl 为 YES，则此值为 Url；否则为流名 */
@property (copy) NSString *outputStream;
/**  输出为流名，或 Url */
@property BOOL outputIsUrl;
/**  输出帧率 */
@property int outputFps;
/**  输出码率 */
@property int outputBitrate;
/**  输出分辨率 */
@property CGSize outputResolution;
/**  音频编码，默认为 0 */
@property int outputAudioConfig;
/**  输入流列表 */
@property (strong) NSMutableArray<ZegoMixStreamInfo*> *inputStreamList;
/** 用户自定义数据 */
@property NSData* userData;
/** 混流声道数，默认为单声道*/
@property int channels;
/** 混流背景颜色，前三个字节为 RGB，即 0xRRGGBBxx */
@property int outputBackgroundColor;
/** 混流背景图，支持预设图片，如 (preset-id://xxx) */
@property (copy) NSString *outputBackgroundImage;

@end

/** 发布直播的模式 */
enum ZegoAPIVideoEncoderRateControlStrategy
{
    /** 恒定码率 */
    ZEGOAPI_RC_ABR = 0,
    /** 恒定码率 */
    ZEGOAPI_RC_CBR,
    /** 恒定质量 */
    ZEGOAPI_RC_VBR,
    /** 恒定质量 */
    ZEGOAPI_RC_CRF,
};

/** 发布直播的模式 */
enum ZegoAPIPublishFlag
{
    /**  连麦模式 */
    ZEGOAPI_JOIN_PUBLISH    = 0,
    ZEGO_JOIN_PUBLISH       = ZEGOAPI_JOIN_PUBLISH,
    /**  混流模式 */
    ZEGOAPI_MIX_STREAM      = 1 << 1,
    ZEGO_MIX_STREAM         = ZEGOAPI_MIX_STREAM,
    /**  单主播模式 */
    ZEGOAPI_SINGLE_ANCHOR   = 1 << 2,
    ZEGO_SINGLE_ANCHOR      = ZEGOAPI_SINGLE_ANCHOR,
};

typedef enum ZegoAPIPublishFlag ZegoApiPublishFlag;

/** 设备模块类型 */
enum ZegoAPIModuleType
{
    /** 音频采集播放设备 */
    ZEGOAPI_MODULE_AUDIO            = 0x4 | 0x8,
};

typedef struct
{
    /** 视频帧率 */
    double fps;
    /** 视频码率(kb/s) */
    double kbps;
    /** 音频码率(kb/s) */
    double akbps;
    /** 延时(ms) */
    int rtt;
    /** 丢包率(0~255) */
    int pktLostRate;
    /** 质量(0~3) */
    int quality;
    
} ZegoAPIPublishQuality;

typedef ZegoAPIPublishQuality ZegoApiPublishQuality;


/** 拉流质量 */
typedef struct
{
    /** 视频帧率 */
    double fps;
    /** 视频码率(kb/s) */
    double kbps;
    /** 音频码率(kb/s) */
    double akbps;
    /** 音频卡顿率(次/min) */
    double audioBreakRate;
    /** 延时(ms) */
    int rtt;
    /** 丢包率(0~255) */
    int pktLostRate;
    /** 直播质量(0~3) */
    int quality;
    
} ZegoAPIPlayQuality;

typedef ZegoAPIPlayQuality ZegoApiPlayQuality;

/** 延迟模式 */
typedef enum : NSUInteger {
    /** 普通延迟模式 */
    ZEGOAPI_LATENCY_MODE_NORMAL = 0,
    /** 低延迟模式，无法用于 RTMP 流 */
    ZEGOAPI_LATENCY_MODE_LOW,
    /** 普通延迟模式，最高码率可达192K */
    ZEGOAPI_LATENCY_MODE_NORMAL2,
    /** 低延迟模式，无法用于 RTMP 流。相对于 ZEGO_LATENCY_MODE_LOW 而言，CPU 开销稍低 */
    ZEGOAPI_LATENCY_MODE_LOW2,
} ZegoAPILatencyMode;

/** 流量控制属性 */
typedef enum : NSUInteger {
    /** 无*/
    ZEGOAPI_TRAFFIC_NONE = 0,
    /** 帧率*/
    ZEGOAPI_TRAFFIC_FPS = 1,
    /** 分辨率*/
    ZEGOAPI_TRAFFIC_RESOLUTION = 1 << 1,
} ZegoAPITrafficControlProperty;

/** 音频设备模式 */
typedef enum : NSUInteger {
    /** 通话模式, 开启硬件回声消除 */
    ZEGOAPI_AUDIO_DEVICE_MODE_COMMUNICATION = 1,
    /** 普通模式, 关闭硬件回声消除 */
    ZEGOAPI_AUDIO_DEVICE_MODE_GENERAL = 2,
    /** 自动模式, 根据场景选择是否开启硬件回声消除 */
    ZEGOAPI_AUDIO_DEVICE_MODE_AUTO = 3
} ZegoAPIAudioDeviceMode;

/** 音频录制时，指定音源类型 */
enum ZegoAPIAudioRecordMask
{
    /** 关闭音频录制 */
    ZEGOAPI_AUDIO_RECORD_NONE      = 0x0,
    /** 打开采集录制 */
    ZEGOAPI_AUDIO_RECORD_CAP       = 0x01,
    /** 打开渲染录制 */
    ZEGOAPI_AUDIO_RECORD_RENDER    = 0x02,
    /** 打开采集和渲染混音结果录制 */
    ZEGOAPI_AUDIO_RECORD_MIX       = 0x04
};

/** 音频录制配置信息 */
typedef struct
{
    /** 启用音频源选择，参考 ZegoAVAPIAudioRecordMask */
    unsigned int mask;
    /** 采样率 8000, 16000, 22050, 24000, 32000, 44100, 48000 */
    int sampleRate;
    /** 声道数 1(单声道) 或 2(双声道) */
    int channels;
    
} ZegoAPIAudioRecordConfig;

/** 推流通道 */
typedef enum :  NSUInteger {
    /** 主推流通道，默认*/
    ZEGOAPI_CHN_MAIN        =   0,
    /** 第二路推流通道, 无法推出声音*/
    ZEGOAPI_CHN_AUX,
} ZegoAPIPublishChannelIndex;


/**
 多媒体流附加信息
 */
@interface ZegoAPIStreamExtraPlayInfo : NSObject

/** 流参数 */
@property (copy) NSString* params;
/** rtmp 地址 */
@property (strong) NSArray<NSString*>* rtmpUrls;
/** flv 地址 */
@property (strong) NSArray<NSString*>* flvUrls;

@end


#endif /* zego_api_defines_oc_h */
