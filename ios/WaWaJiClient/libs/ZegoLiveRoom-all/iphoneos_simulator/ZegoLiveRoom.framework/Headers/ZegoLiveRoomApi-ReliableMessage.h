//
//  ZegoLiveRoomApi-ReliableMessage.h
//  ZegoLiveRoom
//
//  Copyright © 2018年 zego. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZegoLiveRoomApi.h"
#import "ZegoLiveRoomApiDefines-ReliableMessage.h"

@protocol ZegoReliableMessageDelegate;

typedef void(^ZegoSendReliableMessageCompletionBlock)(int errorCode, NSString *roomId, NSString *msgType, uint32_t msgSeq);


@interface ZegoLiveRoomApi (ReliableMessage)


- (bool)setReliableMessageDelegate:(id<ZegoReliableMessageDelegate>)delegate;

/**
 业务广播
 
 @param msg 业务广播数据，不能超过 10240 字节
 @param type 业务广播类型，不能超过 128 字节
 @param completionBlock 业务广播结果，回调 server 下发的转发结果
 @return true 成功，false 失败
 @discussion
 */
- (bool)sendReliableMessage:(NSString *)msg type:(NSString *)type completion:(ZegoSendReliableMessageCompletionBlock)completionBlock;

@end

@protocol ZegoReliableMessageDelegate <NSObject>

- (void)onRecvReliableMessage:(ZegoReliableMessage *)msg;

@end
