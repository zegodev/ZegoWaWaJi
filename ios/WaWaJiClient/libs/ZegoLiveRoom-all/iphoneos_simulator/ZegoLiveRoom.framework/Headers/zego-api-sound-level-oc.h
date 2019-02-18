#ifndef zego_api_sound_level_oc_h
#define zego_api_sound_level_oc_h

#import <Foundation/Foundation.h>
#include "zego-api-defines-oc.h"

/** soundLevel 信息 */
@interface ZegoSoundLevelInfo: NSObject

/** 流 ID */
@property(nonatomic, copy) NSString *streamID;
/** soundLevel 数值 */
@property(nonatomic, assign) float soundLevel;

@end


@protocol ZegoSoundLevelDelegate <NSObject>

@optional

/**
 soundLevel 更新回调

 @param soundLevels 回调信息列表，列表项结构参考 ZegoSoundLevelInfo 定义
 */
- (void)onSoundLevelUpdate:(NSArray<ZegoSoundLevelInfo *> *)soundLevels;


/**
 captureSoundLevel 更新回调

 @param captureSoundLevel 采集音量回调，结构参考 ZegoSoundLevelInfo 定义
 */
- (void)onCaptureSoundLevelUpdate:(ZegoSoundLevelInfo *)captureSoundLevel;

@end


@interface ZegoSoundLevel : NSObject

/**
 获取 ZegoSoundLevel 的单例对象

 @return ZegoSoundLevel 的单例对象
 */
+ (instancetype)sharedInstance;

/**
 设置代理对象

 @param delegate 实现 ZegoSoundLevelDelegate 的代理对象
 @discussion 未设置代理，或者设置代理失败，会造成无法正常获取 soundLevel 回调
 */
- (void)setSoundLevelDelegate:(id<ZegoSoundLevelDelegate>)delegate;

/**
 设置 soundLevel 的监控周期

 @param timeInMS 时间周期，单位为毫秒，取值范围 [100, 3000]。默认 200 ms。
 @return true 成功；false 失败
 @discussion 该设置会影响 [ZegoSoundLevelDelegate -onSoundLevelUpdate:] [ZegoSoundLevelDelegate -onCaptureSoundLevelUpdate:] 的回调频率
 */
- (bool)setSoundLevelMonitorCycle:(unsigned int)timeInMS;

/**
 启动 soundLevel 监听

 @return true 成功，等待回调；false 失败
 @discussion startSoundLevelMonitor 后一定要 stopSoundLevelMonitor
 */
- (bool)startSoundLevelMonitor;

/**
 停止 soundLevel 监听

 @return true 成功；false 失败
 */
- (bool)stopSoundLevelMonitor;

@end

#endif /* zego_api_sound_level_oc_h */
