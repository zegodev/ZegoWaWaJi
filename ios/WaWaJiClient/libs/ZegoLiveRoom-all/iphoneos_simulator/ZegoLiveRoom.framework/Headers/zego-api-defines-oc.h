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


/** 接口调用返回错误码 */
typedef enum ZegoAPIErrorCode
{
    kZegoAPIErrorCodeOK = 0,    /**< 没有错误 */
    kZegoAPIErrorCodeInvalidParameter = 1,  /** 调用输入参数错误 */
    
    // * 外部音频设备
    kZegoAPIErrorCodeExternalAudioDeviceWasNotEnabled = 5101, /** 没有启用外部音频设备 */
    kZegoAPIErrorCodeExternalAudioDeviceEngineError = 5102, /** 处理音频数据异常 */
} ZegoAPIErrorCode;


/** 流ID，值为 NSString */
ZEGO_EXTERN NSString *const kZegoStreamIDKey;
/** 混流ID，值为 NSString */
ZEGO_EXTERN NSString *const kZegoMixStreamIDKey;

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
/** 混流输出格式，值为 NSNumber，可选 {0, 1} */
ZEGO_EXTERN NSString *const kMixStreamAudioOutputFormat;

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

typedef enum {
    /** 预览启用镜像，推流不启用镜像 */
    ZegoVideoMirrorModePreviewMirrorPublishNoMirror = 0,
    /** 预览启用镜像，推流启用镜像 */
    ZegoVideoMirrorModePreviewCaptureBothMirror = 1,
    /** 预览不启用镜像，推流不启用镜像 */
    ZegoVideoMirrorModePreviewCaptureBothNoMirror = 2,
    /** 预览不启用镜像，推流启用镜像 */
    ZegoVideoMirrorModePreviewNoMirrorPublishMirror = 3
}ZegoVideoMirrorMode;

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

/** 分层编码 */
typedef enum {
    /** 不支持分层编码 */
    VIDEO_CODEC_DEFAULT = 0,
    /** 分层编码 要达到和VIDEO_CODEC_DEFAULT相同的编码质量，建议码率和VIDEO_CODEC_DEFAULT相比增加20%左右 */
    VIDEO_CODEC_MULTILAYER = 1
} ZegoVideoCodecAvc;

/** 视频分层类型 */
typedef enum {
    /**< 根据网络状态选择图层  */
    VideoStreamLayer_Auto = -1,
    /**< 指定拉基本层（小分辨率） */
    VideoStreamLayer_BaseLayer = 0,
    /**< 指定拉扩展层（大分辨率)  */
    VideoStreamLayer_ExtendLayer = 1
} VideoStreamLayer;

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
/** 音浪ID，用于标识用户，注意大小是32位无符号数 */
@property unsigned int soundLevelID;
/** 推流内容控制， 0表示音视频都要， 1表示只要音频， 2表示只要视频。default：0。*/
@property int contentControl;

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
/**  输出音频码率 */
@property int outputAudioBitrate;
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
/** 是否开启音浪。true：开启，false：关闭 */
@property BOOL withSoundLevel;
/** 扩展信息 **/
@property int extra;
@end

/** 视频编码码率控制策略 */
typedef enum
{
    /** 恒定码率 */
    ZEGOAPI_RC_ABR = 0,
    /** 恒定码率 */
    ZEGOAPI_RC_CBR,
    /** 恒定质量,仅用于研究目的 */
    ZEGOAPI_RC_VBR,
    /** 恒定质量 */
    ZEGOAPI_RC_CRF,
} ZegoAPIVideoEncoderRateControlStrategy;

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
    /** 视频帧率(采集) */
    double cfps;
    /** 视频帧率(编码) */
    double vencFps;
    /** 视频帧率(网络发送) */
    double fps;
    /** 视频码率(kb/s) */
    double kbps;
    
    /** 音频帧率(采集) */
    double acapFps;
    /** 音频帧率(网络发送) */
    double afps;
    /** 音频码率(kb/s) */
    double akbps;
    
    /** 延时(ms) */
    int rtt;
    /** 丢包率(0~255) */
    int pktLostRate;
    /** 质量(0~3) */
    int quality;
    
    /** 是否硬编 */
    bool isHardwareVenc;
    /** 视频宽度 */
    int width;
    /** 视频高度 */
    int height;
    
} ZegoAPIPublishQuality;

typedef ZegoAPIPublishQuality ZegoApiPublishQuality;


/** 拉流质量 */
typedef struct
{
    /** 视频帧率(网络接收) */
    double fps;
    /** 视频帧率(dejitter) */
    double vdjFps;
    /** 视频帧率(解码) */
    double vdecFps;
    /** 视频帧率(渲染) */
    double vrndFps;
    /** 视频码率(kb/s) */
    double kbps;
    
    /** 音频帧率(网络接收) */
    double afps;
    /** 音频帧率(dejitter) */
    double adjFps;
    /** 音频帧率(解码) */
    double adecFps;
    /** 音频帧率(渲染) */
    double arndFps;
    /** 音频码率(kb/s) */
    double akbps;
    /** 音频卡顿次数 */
    double audioBreakRate;
    /** 视频卡顿次数 */
    double videoBreakRate;
    
    /** 延时(ms) */
    int rtt;
    /** 丢包率(0~255) */
    int pktLostRate;
    /** 直播质量(0~3) */
    int quality;
    /** 语音延时(ms) */
    int delay;
    
    /** 是否硬解 */
    bool isHardwareVdec;
    /** 视频宽度 */
    int width;
    /** 视频高度 */
    int height;
    
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
    /** 低延迟模式，无法用于 RTMP 流。支持WebRTC必须使用此模式 */
    ZEGOAPI_LATENCY_MODE_LOW3,
    /**< 普通延迟模式，使用此模式前先咨询即构技术支持 */
    ZEGOAPI_LATENCY_MODE_NORMAL3,
} ZegoAPILatencyMode;

/** 流量控制属性 */
typedef enum : NSUInteger {
    /**< 基本流量控制，只有码率控制，不带自适应帧率和分辨率 */
    ZEGOAPI_TRAFFIC_CONTROL_BASIC = 0,
    /**< 自适应帧率 */
    ZEGOAPI_TRAFFIC_CONTROL_ADAPTIVE_FPS = 1,
    /** 自适应分辨率*/
    ZEGOAPI_TRAFFIC_CONTROL_ADAPTIVE_RESOLUTION = 1 << 1,
    
    /**< 废弃 */
    ZEGOAPI_TRAFFIC_NONE = ZEGOAPI_TRAFFIC_CONTROL_BASIC,
    ZEGOAPI_TRAFFIC_FPS = ZEGOAPI_TRAFFIC_CONTROL_ADAPTIVE_FPS,
    ZEGOAPI_TRAFFIC_RESOLUTION = ZEGOAPI_TRAFFIC_CONTROL_ADAPTIVE_RESOLUTION,
    
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

/** 音频设备类型 */
typedef enum : NSUInteger {
    /**< 输入设备 */
    ZEGOAPI_AUDIO_DEVICE_INPUT = 0,
    /**< 输出设备 */
    ZEGOAPI_AUDIO_DEVICE_OUTPUT = 1,
} ZegoAPIAudioDeviceType;

/** 设备状态 */
typedef enum : NSUInteger
{
    /**< 添加设备 */
    ZEGOAPI_DEVICE_ADD = 0,
    /**< 删除设备 */
    ZEGOAPI_DEVICE_DELETE = 1,
} ZegoAPIDeviceState;

/** 音量类型 */
typedef enum : NSUInteger
{
    /**< 设备音量 */
    ZEGOAPI_VOLUME_ENDPOINT = 0,
    /**< App 音量 */
    ZEGOAPI_VOLUME_SIMPLE = 1,
} ZegoAPIVolumeType;

@interface ZegoAPIDeviceInfo : NSObject
/** 设备ID */
@property (copy) NSString* deviceId;
/** 设备名 */
@property (copy) NSString* deviceName;

@end

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

@interface ZegoSoundLevelInMixedStreamInfo : NSObject

/** sound level id */
@property (assign) unsigned int soundLevelID;
/** sound level value */
@property (assign) unsigned char soundLevel;

@end

/**
 转推CDN状态
 */

typedef enum : NSUInteger
{
    /**< 转推停止 */
    ZEGOAPI_RELAY_STOP = 0,
    /**< 正在转推 */
    ZEGOAPI_RELAY_START = 1,
    /**< 正在重试 */
    ZEGOAPI_RELAY_RETRY = 2,
} ZegoAPIStreamRelayCDNState;

typedef enum : NSUInteger
{
    /**< 无 */
    ZEGOAPI_RELAY_NONE = 0,                       
    /**< 服务器错误 */
    ZEGOAPI_RELAY_SERVER_ERROR = 8,
    /**< 握手失败 */
    ZEGOAPI_RELAY_HAND_SHAKE_FAILED = 9,
    /**< 接入点错误 */
    ZEGOAPI_RELAY_ACCESS_POINT_ERROR = 10,
    /**< 创建流失败 */
    ZEGOAPI_RELAY_CREATE_STREAM_FAILED = 11,
    /**< BAD NAME */
    ZEGOAPI_RELAY_BAD_NAME = 12,
    /**< CDN服务器主动断开 */
    ZEGOAPI_RELAY_CDN_SERVER_DISCONNECTED = 13,
    /**< 主动断开 */
    ZEGOAPI_RELAY_DISCONNECTED = 14,
} ZegoAPIStreamRelayCDNDetail;

/**
 转推CDN状态信息
 */
@interface ZegoAPIStreamRelayCDNInfo : NSObject

/** 转推CDN的rtmp地址 */
@property (copy) NSString *rtmpURL;
/** 当前状态 */
@property (assign) ZegoAPIStreamRelayCDNState state;
/** 转推停止或转推重试时的详细原因 */
@property (assign) ZegoAPIStreamRelayCDNDetail detail;
/** 状态改变时的时间 */
@property (assign) unsigned int stateTime;

@end
#endif /* zego_api_defines_oc_h */
