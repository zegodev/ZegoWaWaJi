//
//  ZegoManager.h
//  WaWaJi
//
//  Created by summery on 16/10/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <ZegoLiveRoom/ZegoLiveRoom.h>

@interface ZegoManager : NSObject

// 获取 ZegoLiveRoomAPi 单例对象
+ (ZegoLiveRoomApi *)api;

// 释放 api 对象
+ (void)releaseApi;

@end
