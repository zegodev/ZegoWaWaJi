//
//  ZegoCommand.h
//  WaWaJiClient
//
//  Created by summery on 02/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <Foundation/Foundation.h>

static const int CMD_USER_UPDATE =        0x101;   // 全员广播房间信息（总人数，排队列表、当前游戏者）更新（Server-->Client）

static const int CMD_APPLY =              0x201;   // 预约上机申请（Client-->Server）
static const int CMD_APPLY_REPLY =        0x110;   // 回复收到预约申请，并告知预约结果（Server-->Client）

static const int CMD_CANCEL_APPLY =       0x202;   // 取消预约 (Client-->Server)
static const int CMD_CANCEL_APPLY_REPLY = 0x112;   // 取消预约回复 （Server-->Client）

static const int CMD_GAME_READY =         0x102;   // 通知某人准备上机, 此时用户可使用 CMD_ABANDON_PLAY 放弃游戏（Server-->Client）
static const int CMD_GAME_READY_REPLY =   0x204;   // 回复收到上机指令（Client-->Server）

static const int CMD_GAME_CONFIRM =       0x203;   // 确认上机或者放弃玩游戏，仅在正式开始玩之前发送此指令有效，即在收到服务端的 CMD_GAME_READY 指令时，通过该指令告诉服务端开始玩还是放弃（Client-->Server）
static const int CMD_CONFIRM_REPLY =      0x111;   // 回复收到确认上机或者放弃玩游戏指令（Server-->Client）

static const int CMD_GAME_RESULT =        0x104;   // 通知游戏结果（Server-->Client）
static const int CMD_RESULT_REPLY =       0x205;   // 回复收到游戏结果（Client-->Server）

static const int CMD_MOVE_LEFT =          0x210;
static const int CMD_MOVE_RIGHT =         0x211;
static const int CMD_MOVE_FORWARD =       0x212;
static const int CMD_MOVE_BACKWARD =      0x213;
static const int CMD_MOVE_DOWN =          0x214;

static const int RETRY_DURATION =         10;
static const int PLAY_DURATION =          30;
static const int RESULT_DURATION =        15;  // FIXME: 启动抓娃娃后，爪子回位需要花时间，先算进去

@interface ZegoCommand : NSObject


- (NSString *)apply:(int)clientSeq;
- (NSString *)gameReadyReply:(int)clientSeq serverSeq:(int)serverSeq;
- (NSString *)cancelApply:(int)clientSeq;
- (NSString *)gameConfirm:(NSInteger)confirm clientSeq:(NSInteger)clientSeq;
- (NSString *)moveLeft:(int)clientSeq;
- (NSString *)moveRight:(int)clientSeq;
- (NSString *)moveForward:(int)clientSeq;
- (NSString *)moveBackward:(int)clientSeq;
- (NSString *)moveDown:(int)clientSeq;
- (NSString *)resultReply:(int)clientSeq serverSeq:(int)serverSeq;

@end
