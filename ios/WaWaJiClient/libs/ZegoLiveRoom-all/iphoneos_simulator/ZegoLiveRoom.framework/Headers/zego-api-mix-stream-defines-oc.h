//
//  zego-api-mix-stream-defines-oc.h
//
//  Copyright © 2018年 Zego. All rights reserved.
//

#ifndef zego_api_mix_stream_defines_oc_h
#define zego_api_mix_stream_defines_oc_h

/** 混流图层信息，原点在左上角 */
@interface ZegoMixStreamInput : NSObject

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

/** 混流输出配置 */
@interface ZegoMixStreamOutput : NSObject

/**  isUrl 为 YES，则此值为 Url；否则为流名 */
@property (copy) NSString *target;
/**  输出为流名，或 Url */
@property BOOL isUrl;

@end

/** 混流水印 */
@interface ZegoMixStreamWatermark : NSObject
/** 水印图片 */
@property (nonatomic, copy) NSString *image;
/** 混流画布左上角坐标的第一个值，即左上角坐标为 (left, top) */
@property int left;
/** 混流画布左上角坐标的第二个值 */
@property int top;
/** 混流画布左上角坐标的第一个值，即右下角坐标为 (right, bottom) */
@property int right;
/** 混流画布右下角坐标的第二个值 */
@property int bottom;

@end

/** 混流配置 */
@interface ZegoMixStreamConfig : NSObject

/**  输出帧率 */
@property int outputFps;
/**  输出码率控制模式，0 表示 CBR 恒定码率，1 表示 CRF 恒定质量，默认为 0 */
@property int outputRateControlMode;
/**  输出码率，输出码率控制模式设置为 CBR恒定码率时有效*/
@property int outputBitrate;
/**  输出质量，输出码率控制模式设置为 CRF恒定质量时有效，有效值范围 0-51，默认值是 23 */
@property int outputQuality;
/**  输出音频码率 */
@property int outputAudioBitrate;
/**  输出分辨率 */
@property CGSize outputResolution;
/**  音频编码，默认为 0 */
@property int outputAudioConfig;
/**  输入流列表 */
@property (strong) NSMutableArray<ZegoMixStreamInput*> *inputStreamList;
/**  输出列表 */
@property (strong) NSMutableArray<ZegoMixStreamOutput*> *outputList;
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
/** 混流水印 **/
@property (nonatomic, strong) ZegoMixStreamWatermark *watermark;
@end

@interface ZegoMixStreamOutputResult : NSObject
/** 流ID **/
@property (nonatomic, copy) NSString *streamID;
/** rtmp列表 **/
@property (nonatomic, strong) NSMutableArray<NSString *> *rtmpList;
/** hls列表 **/
@property (nonatomic, strong) NSMutableArray<NSString *> *hlsList;
/** flv列表 **/
@property (nonatomic, strong) NSMutableArray<NSString *> *flvList;

@end

/** 混流结果 **/
@interface ZegoMixStreamResultEx : NSObject

/** 请求序号 **/
@property int seq;
/** 不存在的输入流 **/
@property (nonatomic, strong) NSMutableArray<NSString *> *nonExistInputList;

@property (nonatomic, strong) NSMutableArray<ZegoMixStreamOutputResult *> *outputResultList;

@end



#endif /* zego_api_mix_stream_defines_oc_h */
