//
//  ZegoSetting.h
//
//
//  Created by summery on 13/09/2017.
//  Copyright © 2017 ZEGO. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZegoManager.h"

#ifdef VIDEOLIVE

#import "video_capture_external_demo.h"
#import "ZegoVideoCaptureFromImage.h"

#endif

//#define I18N  // 切换到国际版宏

// 房间模式
typedef NS_ENUM(NSUInteger, ZegoRoomType)
{
    SinglePublisherRoom = 0,    // 单主播模式
    MultiPublisherRoom,         // 多主播（连麦）模式
    MixStreamRoom,              // 混流模式
};

// App 版本
typedef NS_ENUM(NSUInteger, ZegoAppType)
{
    ZegoAppTypeUDP  = 0,        // 国内版，App ID 为 1739272706
    ZegoAppTypeI18N,            // 国际版，App ID 为 3322882036
    ZegoAppTypeCustom,          // 自定义，App ID 由用户自定义
};

static int clientSeq = 0;

@interface ZegoSetting : NSObject

// 环境设置
@property (nonatomic, assign) ZegoAppType appType;                  // App 版本
@property (nonatomic, assign) uint32_t appID;                       // App ID
@property (nonatomic, copy) NSData *appSign;                        // App 签名，与 App ID 配合校验
@property (nonatomic, copy) NSString *customAppSign;                // 自定义模式下，用户输入的 App 签名
@property (nonatomic, readonly) NSArray *appTypeList;               // 默认支持的 App 类型列表

@property (nonatomic, assign) BOOL useTestEnv;                      // 是否使用测试环境
@property (nonatomic, assign) BOOL useAlphaEnv;                     // 是否使用 alpha 环境

// 用户设置
@property (nonatomic, copy) NSString *userID;                       // 用户 ID
@property (nonatomic, copy) NSString *userName;                     // 用户名

// 视频质量设置
@property (nonatomic, strong) ZegoAVConfig *avConfig;               // 视频推流质量配置
@property (nonatomic, readonly) NSInteger presetAVConfigIndex;      // 视频推流质量预配置
@property (nonatomic, readonly) NSArray *presetVideoQualityList;    // 默认支持的视频参数配置列表

// 高级设置
@property (nonatomic, assign) BOOL useExternalCapture;              // 外部采集
@property (nonatomic, assign) BOOL useExternalRender;               // 外部渲染
@property (nonatomic, assign) BOOL useExternalFilter;               // 外部滤镜
@property (nonatomic, assign) BOOL useHardwareEncode;               // 硬件编码
@property (nonatomic, assign) BOOL useHardwareDecode;               // 硬件解码
@property (nonatomic, assign) BOOL enableRateControl;               // 自适应码率
@property (nonatomic, assign) BOOL enableAudioPrep;                 // 音频前处理
@property (nonatomic, assign) BOOL recordTime;                      // 计时

// 其他
@property (nonatomic, assign) BOOL useHeadSet;                      // 是否使用耳机(有线或蓝牙）
@property (nonatomic, assign) NSInteger beautifyFeature;            // 当前的美颜特性
@property (nonatomic, assign) NSInteger filterFeature;              // 当前的滤镜特性
@property (nonatomic, readonly) NSArray *beautifyList;              // 美颜选项列表
@property (nonatomic, readonly) NSArray *filterList;                // 滤镜选项列表

// 获取 ZegoSetting 单例对象
+ (instancetype)sharedInstance;

+ (NSString *)getMyRoomID:(ZegoRoomType)roomType;
+ (NSString *)getPublishStreamID;

#ifdef VIDEOLIVE

#if TARGET_OS_SIMULATOR
- (ZegoVideoCaptureFactory *)getVideoCaptureFactory;
#else
- (VideoCaptureFactoryDemo *)getVideoCaptureFactory;
#endif

#endif

- (BOOL)selectPresetQuality:(NSInteger)presetIndex;
- (void)setCustomAppID:(uint32_t)appid sign:(NSString *)sign;

// 获取直播中的背景 Zego 图
- (UIImage *)getBackgroundImage:(CGSize)viewSize withText:(NSString *)text;

// 检查用户是否使用耳机（有线或蓝牙）
- (void)checkHeadSet;

- (void)setupVideoCaptureDevice;
- (void)setupVideoFilter;

@end

