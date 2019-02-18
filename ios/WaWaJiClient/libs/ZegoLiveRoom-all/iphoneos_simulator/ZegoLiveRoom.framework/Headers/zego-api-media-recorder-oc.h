//
//  zego-api-media-recorder-oc.h
//  ZegoLiveRoom
//
//  Created by MarkWu on 2018/6/15.
//
#ifndef zego_api_media_recorder_oc_h
#define zego_api_media_recorder_oc_h

#import <Foundation/Foundation.h>
#include "zego-api-defines-oc.h"

/** 媒体录制类型 */
typedef enum : NSUInteger
{
    /**< 不录制任何数据 */
    ZEGOAPI_MEDIA_RECORD_NONE = 0,
    /**< 只录制音频 */
    ZEGOAPI_MEDIA_RECORD_AUDIO = 1,
    /**< 只录制视频 */
    ZEGOAPI_MEDIA_RECORD_VIDEO = 2,
    /**< 同时录制音频、视频 */
    ZEGOAPI_MEDIA_RECORD_BOTH = 3
    
} ZegoAPIMediaRecordType;

/** 媒体录制通道 */
typedef enum :  NSUInteger
{
    /** 第一路录制通道 */
    ZEGOAPI_MEDIA_RECORD_CHN_MAIN  = 0,
    /** 第二路录制通道 */
    ZEGOAPI_MEDIA_RECORD_CHN_AUX = 1
    
} ZegoAPIMediaRecordChannelIndex;

/** 媒体录制格式 */
typedef enum : NSUInteger
{
    /** FLV格式 */
    ZEGOAPI_MEDIA_RECORD_FLV  = 1,
    /** MP4格式 */
    ZEGOAPI_MEDIA_RECORD_MP4  = 2
} ZegoAPIMediaRecordFormat;

@protocol ZegoMediaRecordDelegage <NSObject>
@required
/**
 媒体录制回调
 
 @param errCode: 错误码， 0: 成功，1：存储路径太长，2：初始化 avcontext 失败，3：打开文件失败，4：写文件失败
 @param index 录制通道
 @param storagePath 录制文件存储路径
 */
- (void)onMediaRecord:(int)errCode channelIndex:(ZegoAPIMediaRecordChannelIndex)index storagePath:(NSString *)path;

@optional
/**
 录制信息更新回调
 
 @param index 录制通道
 @param path 录制文件存储路径
 @param duration 录制时长，单位毫秒
 @param size 文件大小，单位字节
 */
- (void)onRecordStatusUpdateFromChannel:(ZegoAPIMediaRecordChannelIndex)index storagePath:(NSString *)path duration:(unsigned int)duration fileSize:(unsigned int)size;

@end

@interface ZegoMediaRecorder : NSObject

/**
 开始录制
 
 @param channelIndex 录制通道
 @param recordType 录制类型
 @param storagePath 录制文件存储路径
 @param return true 调用成功，false 调用失败
 @discussion 必须在init sdk之后调用
 @deprecated 请使用 {@link ZegoMediaRecorder#startRecord:recordType:storagePath:enableStatusUpdate:interval:}
 */
- (BOOL)startRecord:(ZegoAPIMediaRecordChannelIndex)channelIndex recordType:(ZegoAPIMediaRecordType)recordType storagePath:(NSString *)storagePath;

/**
 开始录制
 
 @param channelIndex 录制通道
 @param recordType 录制类型
 @param storagePath 录制文件存储路径
 @param enable  是否开启录制信息更新回调。YES: 开启，NO: 关闭。
 @param interval 录制文件时状态回调的间隔，单位毫秒，有效范围：1000-10000
 @param return true 调用成功，false 调用失败
 @discussion 必须在init sdk之后调用, 默认录制格式为FLV
 @discussion 如果开启了录制信息更新回调，请实现{@link ZegoMediaRecordDelegage#onRecordStatusUpdateFromChannel:storagePath:duration:fileSize:}。
 */
- (BOOL)startRecord:(ZegoAPIMediaRecordChannelIndex)channelIndex recordType:(ZegoAPIMediaRecordType)recordType storagePath:(NSString *)storagePath enableStatusUpdate:(BOOL)enable interval:(int)interval;

/**
 开始录制
 
 @param channelIndex 录制通道
 @param recordType 录制类型
 @param storagePath 录制文件存储路径
 @param enable  是否开启录制信息更新回调。YES: 开启，NO: 关闭。
 @param interval 录制文件时状态回调的间隔，单位毫秒，有效范围：1000-10000
 @param recordFromat 录制文件的文件格式
 @param return true 调用成功，false 调用失败
 @discussion 必须在init sdk之后调用
 @discussion 如果开启了录制信息更新回调，请实现{@link ZegoMediaRecordDelegage#onRecordStatusUpdateFromChannel:storagePath:duration:fileSize:}。
 */
- (BOOL)startRecord:(ZegoAPIMediaRecordChannelIndex)channelIndex recordType:(ZegoAPIMediaRecordType)recordType storagePath:(NSString *)storagePath enableStatusUpdate:(BOOL)enable interval:(int)interval recordFormat:(ZegoAPIMediaRecordFormat)recordFormat;

/**
 开始录制
 
 @param channelIndex 录制通道
 @param recordType 录制类型
 @param storagePath 录制文件存储路径
 @param enable  是否开启录制信息更新回调。YES: 开启，NO: 关闭。
 @param interval 录制文件时状态回调的间隔，单位毫秒，有效范围：1000-10000
 @param recordFromat 录制文件的文件格式
 @param isFragment 录制文件是否分片，MP4格式才有效
 @param return true 调用成功，false 调用失败
 @discussion 必须在init sdk之后调用
 @discussion 如果开启了录制信息更新回调，请实现{@link ZegoMediaRecordDelegage#onRecordStatusUpdateFromChannel:storagePath:duration:fileSize:}。
 */
- (BOOL)startRecord:(ZegoAPIMediaRecordChannelIndex)channelIndex recordType:(ZegoAPIMediaRecordType)recordType storagePath:(NSString *)storagePath enableStatusUpdate:(BOOL)enable interval:(int)interval recordFormat:(ZegoAPIMediaRecordFormat)recordFormat isFragment:(BOOL)isFragment;

/**
 停止录制
 
 @param channelIndex 录制通道
 */
- (void)stopRecord:(ZegoAPIMediaRecordChannelIndex)channelIndex;

/**
 设置录制回调
 
 @param delegate 媒体录制回调
 @discussion startRecord后才有回调
 */
- (void)setMediaRecordDelegage:(id<ZegoMediaRecordDelegage>)delegate;

@end

#endif
