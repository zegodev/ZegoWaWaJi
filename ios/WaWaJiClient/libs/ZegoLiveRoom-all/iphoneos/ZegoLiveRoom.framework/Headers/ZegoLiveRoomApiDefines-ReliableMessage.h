//
//  ZegoLiveRoomApiDefines-ReliableMessage.h
//  ZegoLiveRoom
//
//  Copyright © 2018年 zego. All rights reserved.
//

#ifndef ZegoLiveRoomApiDefines_ReliableMessage_h
#define ZegoLiveRoomApiDefines_ReliableMessage_h

/** 可靠消息 */
@interface ZegoReliableMessage : NSObject

/** 消息类型 */
@property (nonatomic, copy) NSString    *type;
/** 消息内容 */
@property (nonatomic, copy) NSString    *msg;
/** 房间号 */
@property (nonatomic, copy) NSString    *roomId;
/** 消息序号 */
@property (nonatomic, assign) uint32_t  seq;
/** 发消息用户的ID */
@property (nonatomic, copy) NSString    *fromUserId;
/** 发消息用户的名字 */
@property (nonatomic, copy) NSString    *fromUsername;
/** 发送时间 */
@property (nonatomic, assign) uint64_t  sendTime;

@end

#endif /* ZegoLiveRoomApiDefines_ReliableMessage_h */
