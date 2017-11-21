//
//  ZegoCommand.m
//  WaWaJiClient
//
//  Created by summery on 02/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoCommand.h"
#import "ZegoManager.h"

@implementation ZegoCommand

- (NSString *)apply:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @513,
                           @"data" : @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)gameReadyReply:(int)clientSeq serverSeq:(int)serverSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @516,
                           @"data" : @{@"time_stamp": [NSNumber numberWithInteger:timestamp],
                                       @"seq": [NSNumber numberWithInteger:serverSeq]
                                        }
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)cancelApply:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @514,
                           @"data" : @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)gameConfirm:(NSInteger)confirm clientSeq:(NSInteger)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @515,
                           @"data" :  @{@"confirm": [NSNumber numberWithInteger:confirm],
                                        @"time_stamp": [NSNumber numberWithInteger:timestamp]
                                        }
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveLeft:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @528,
                           @"data" :  @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveRight:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @529,
                           @"data" :  @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveForward:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @530,
                           @"data" :  @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveBackward:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @531,
                           @"data" :  @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveDown:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @532,
                           @"data" :  @{@"time_stamp": [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)resultReply:(int)clientSeq serverSeq:(int)serverSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{@"seq" : [NSNumber numberWithInteger:clientSeq],
                           @"cmd" : @517,
                           @"data" :  @{@"time_stamp": [NSNumber numberWithInteger:timestamp],
                                        @"seq": [NSNumber numberWithInteger:serverSeq]
                                        }
                           };
    return [self encodeDictionaryToJSON:dict];
}


- (NSTimeInterval)timestamp {
    NSDate *date = [NSDate date];
    NSTimeInterval interval = [date timeIntervalSince1970] * 1000;
    if (!interval) {
        NSLog(@"发送命令时间戳生成失败");
        return 0;
    }
    return interval;
}

- (NSString *)encodeDictionaryToJSON:(NSDictionary *)dict {
    if (dict == nil) {
        return nil;
    }
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:&error];
    
    if (jsonData) {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        
        NSLog(@"%@", [NSString stringWithFormat:@"指令字符串：%@", jsonString]);
        return jsonString;
    }
    
    return nil;
}

@end
