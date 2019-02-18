//
//  ZegoLiveRoomApi.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "ZegoLiveRoomApi.h"
#import "ZegoLiveRoomApiDefines-IM.h"


@protocol ZegoIMDelegate;

typedef void(^ZegoRoomMessageCompletion)(int errorCode, NSString *roomId, unsigned long long messageId);
typedef void(^ZegoCreateConversationCompletion)(int errorCode, NSString *roomId, NSString *conversationId);
typedef void(^ZegoConversationMessageCompletion)(int errorCode, NSString *roomId, NSString *conversationId, unsigned long long messageId);
typedef void(^ZegoConversationInfoBlock)(int errorCode, NSString *roomId, NSString *conversationId, ZegoConversationInfo *info);
typedef void(^ZegoBigRoomMessageCompletion)(int errorCode, NSString *roomId, NSString *messageId);

@interface ZegoLiveRoomApi (IM)

/**
 设置 IM 代理对象
 
 @param imDelegate 遵循 ZegoIMDelegate 协议的代理对象
 @return true 成功，false 失败
 @discussion 使用 IM 功能，初始化相关视图控制器时需要设置代理对象。未设置代理对象，或对象设置错误，可能导致无法正常收到相关回调
 */
- (bool)setIMDelegate:(id<ZegoIMDelegate>)imDelegate;

/**
 房间发送广播消息
 
 @param content 消息内容
 @param type 消息类型，可以自定义
 @param category 消息分类，可以自定义
 @param priority 消息优先级, deprecated, 由 SDK 内部确定优先级
 @param completionBlock 消息发送结果，回调 server 下发的 messageId
 @return true 成功，false 失败
 @discussion 实现点赞主播、评论、送礼物等 IM 功能时，需要调用本 API
 @deprecated 请使用 sendRoomMessage:type:category:completion:
 */
- (bool)sendRoomMessage:(NSString *)content type:(ZegoMessageType)type category:(ZegoMessageCategory)category priority:(ZegoMessagePriority)priority completion:(ZegoRoomMessageCompletion)completionBlock;

/**
 房间发送广播消息
 
 @param content 消息内容
 @param type 消息类型，可以自定义
 @param category 消息分类，可以自定义
 @param completionBlock 消息发送结果，回调 server 下发的 messageId
 @return true 成功，false 失败
 @discussion 实现点赞主播、评论、送礼物等 IM 功能时，需要调用本 API
 */
- (bool)sendRoomMessage:(NSString *)content type:(ZegoMessageType)type category:(ZegoMessageCategory)category completion:(ZegoRoomMessageCompletion)completionBlock;

/**
 在房间中创建一个会话
 
 @param conversationName 会话名称
 @param memberList 会话成员列表
 @param completionBlock 创建结果，回调 server 下发的会话 Id
 @return true 成功，false 失败
 */
- (bool)createConversation:(NSString *)conversationName memberList:(NSArray<ZegoUser *> *)memberList completion:(ZegoCreateConversationCompletion)completionBlock;

/**
 获取会话相关信息
 
 @param conversationId 会话 Id
 @param completionBlock 获取结果，包括会话名称，会话成员，创建者等信息
 @return true 成功，false 失败
 */
- (bool)getConversationInfo:(NSString *)conversationId completion:(ZegoConversationInfoBlock)completionBlock;

/**
 在会话中发送一条消息
 
 @param content 消息内容
 @param type 消息类型，可以自定义
 @param conversationId 会话 Id
 @param completionBlock 发送消息结果，回调 server 下发的 messageId
 @return true 成功，false 失败
 */
- (bool)sendConversationMessage:(NSString *)content type:(ZegoMessageType)type conversationId:(NSString *)conversationId completion:(ZegoConversationMessageCompletion)completionBlock;

/**
 房间发送不可靠信道的消息
 
 @param content 消息内容
 @param type 消息类型，可以自定义
 @param category 消息分类，可以自定义
 @param completionBlock 消息发送结果，回调 server 下发的 messageId
 @return true 成功，false 失败
 @discussion 用于高并发的场景，消息可能被丢弃，当高并发达到极限时会根据策略丢弃部分消息
 */
- (bool)sendBigRoomMessage:(NSString *)content type:(ZegoMessageType)type category:(ZegoMessageCategory)category completion:(ZegoBigRoomMessageCompletion)completionBlock;

@end


@protocol ZegoIMDelegate <NSObject>

@optional

/**
 房间成员更新回调
 
 @param userList 成员更新列表
 @param type  更新类型(增量，全量)
 @discussion 用户调用 [ZegoLiveRoomApi setRoomConfig:userStateUpdate:] 开启用户状态（用户进入、退出房间）广播，当房间成员变化（例如用户进入、退出房间）时，会触发此通知
 */
- (void)onUserUpdate:(NSArray<ZegoUserState *> *)userList updateType:(ZegoUserUpdateType)type;

/**
 收到房间的广播消息
 
 @param roomId 房间 Id
 @param messageList 消息列表，包括消息内容，消息分类，消息类型，发送者等信息
 @discussion 调用 [ZegoLiveRoomApi (IM) -sendRoomMessage:type:category:priority:completion:] 发送消息，会触发此通知
 */
- (void)onRecvRoomMessage:(NSString *)roomId messageList:(NSArray<ZegoRoomMessage*> *)messageList;

/**
 收到会话消息
 
 @param roomId 房间 Id
 @param conversationId 会话 Id
 @param message 会话消息，包括消息内容，消息类型，发送者，发送时间等信息
 @discussion 调用 [ZegoLiveRoomApi (IM) -sendConversationMessage:type:conversationId:completion] 发送消息，会触发此通知
 */
- (void)onRecvConversationMessage:(NSString *)roomId conversationId:(NSString *)conversationId message:(ZegoConversationMessage *)message;

/**
 收到在线人数更新
 
 @param onlineCount 在线人数
 @param roomId 房间 Id
 */
- (void)onUpdateOnlineCount:(int)onlineCount room:(NSString *)roomId;

/**
 收到房间的不可靠消息广播
 
 @param roomId 房间 Id
 @param messageList 消息列表，包括消息内容，消息分类，消息类型，发送者等信息
 @discussion 调用 [ZegoLiveRoomApi (IM) -sendBigRoomMessage:type:category:completion:] 发送消息，会触发此通知
 */
- (void)onRecvBigRoomMessage:(NSString *)roomId messageList:(NSArray<ZegoBigRoomMessage*> *)messageList;

@end
