//
//  ZegoLiveRoomApi.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZegoLiveRoomApi.h"
#import "ZegoLiveRoomApiDefines-Relay.h"


typedef void(^ZegoRelayCompletionBlock)(int errorCode, NSString *roomId, NSString *relayResult);

@interface ZegoLiveRoomApi (Relay)

/**
 转发接口
 
 @param data 需要转发的数据
 @param type 转发类型
 @param completionBlock 转发发送结果，回调 server 下发的转发结果
 @return true 成功，false 失败
 @discussion 这个接口用来实现大并发的调用，实现观众答题的功能就需要调用这个接口
 */
- (bool)relayData:(NSString *)data type:(ZegoRelayType)type completion:(ZegoRelayCompletionBlock)completionBlock;

@end
