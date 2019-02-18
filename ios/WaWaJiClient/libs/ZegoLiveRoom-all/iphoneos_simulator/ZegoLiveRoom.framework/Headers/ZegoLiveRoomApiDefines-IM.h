//
//  ZegoLiveRoomApiDefines-IM.h
//  ZegoLiveRoom
//
//  Copyright © 2017年 zego. All rights reserved.
//

#ifndef ZegoLiveRoomApiDefines_IM_h
#define ZegoLiveRoomApiDefines_IM_h

/** 用户更新类型 */
typedef enum
{
    /** 全量更新 */
    ZEGO_UPDATE_TOTAL = 1,
    /** 增量更新 */
    ZEGO_UPDATE_INCREASE,
} ZegoUserUpdateType;

/** 用户更新属性 */
typedef enum
{
    /** 新增 */
    ZEGO_USER_ADD = 1,
    /** 删除 */
    ZEGO_USER_DELETE,
} ZegoUserUpdateFlag;

/** 消息类型 */
typedef enum
{
    /** 文字 */
    ZEGO_TEXT = 1,
    /** 图片 */
    ZEGO_PICTURE,
    /** 文件 */
    ZEGO_FILE,
    /** 其他 */
    ZEGO_OTHER_TYPE = 100,
} ZegoMessageType;

/** 消息优先级 */
typedef enum
{
    /** 默认优先级 */
    ZEGO_DEFAULT = 2,
    /** 高优先级 */
    ZEGO_HIGH = 3,
} ZegoMessagePriority;

/** 消息类别 */
typedef enum
{
    /** 聊天 */
    ZEGO_CHAT = 1,
    /** 系统 */
    ZEGO_SYSTEM,
    /** 点赞 */
    ZEGO_LIKE,
    /** 送礼物 */
    ZEGO_GIFT,
    /** 其他 */
    ZEGO_OTHER_CATEGORY = 100,
} ZegoMessageCategory;

/** 用户状态 */
@interface ZegoUserState : NSObject

/** 用户 ID */
@property (nonatomic, copy) NSString *userID;
/** 用户名 */
@property (nonatomic, copy) NSString *userName;
/** 用户更新属性 */
@property (nonatomic, assign) ZegoUserUpdateFlag updateFlag;
/** 角色 */
@property (nonatomic, assign) int role;

@end

/** 房间消息 */
@interface ZegoRoomMessage : NSObject

/** 来源用户 Id */
@property (nonatomic, copy) NSString *fromUserId;
/** 来源用户名 */
@property (nonatomic, copy) NSString *fromUserName;
/** 消息 Id */
@property (nonatomic, assign) unsigned long long messageId;
/** 内容 */
@property (nonatomic, copy) NSString *content;
/** 消息类型 */
@property (nonatomic, assign) ZegoMessageType type;
/** 消息优先级 */
@property (nonatomic, assign) ZegoMessagePriority priority;
/** 消息类别 */
@property (nonatomic, assign) ZegoMessageCategory category;

@end

/** 会话消息 */
@interface ZegoConversationMessage : NSObject

/** 来源用户 Id */
@property (nonatomic, copy) NSString *fromUserId;
/** 来源用户名 */
@property (nonatomic, copy) NSString *fromUserName;
/** 消息 Id */
@property (nonatomic, assign) unsigned long long messageId;
/** 内容 */
@property (nonatomic, copy) NSString *content;
/** 消息类型 */
@property (nonatomic, assign) ZegoMessageType type;
/** 发送时间 */
@property (nonatomic, assign) unsigned int sendTime;

@end


/** 房间不可靠消息 */
@interface ZegoBigRoomMessage : NSObject

/** 来源用户 Id */
@property (nonatomic, copy) NSString *fromUserId;
/** 来源用户名 */
@property (nonatomic, copy) NSString *fromUserName;
/** 消息 Id */
@property (nonatomic, copy) NSString *messageId;
/** 内容 */
@property (nonatomic, copy) NSString *content;
/** 消息类型 */
@property (nonatomic, assign) ZegoMessageType type;
/** 消息优先级 */
@property (nonatomic, assign) ZegoMessagePriority priority;
/** 消息类别 */
@property (nonatomic, assign) ZegoMessageCategory category;

@end


/** 用户 */
@interface ZegoUser : NSObject

/** 用户 Id */
@property (nonatomic, copy) NSString *userId;
/** 用户名 */
@property (nonatomic, copy) NSString *userName;

@end

/** 会话信息 */
@interface ZegoConversationInfo : NSObject

/** 会话名称 */
@property (nonatomic, copy) NSString *conversationName;
/** 会话创建者 Id */
@property (nonatomic, copy) NSString *creatorId;
/** 创建时间 */
@property (nonatomic, assign) unsigned int createTime;
/** 会话成员列表 */
@property (nonatomic, strong) NSArray<ZegoUser*>* members;

@end

#endif /* ZegoLiveRoomApiDefines_h */
