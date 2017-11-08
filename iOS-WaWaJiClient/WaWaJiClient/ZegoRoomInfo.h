//
//  ZegoRoomInfo.h
//  WaWaJiClient
//
//  Created by summery on 06/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface ZegoRoomInfo : NSObject

@property (nonatomic, copy) NSString *roomID;
@property (nonatomic, copy) NSString *anchorID;
@property (nonatomic, copy) NSString *anchorName;
@property (nonatomic, copy) NSString *roomName;
@property (nonatomic, strong) NSMutableArray *streamInfo;   // stream_id 列表

@end
