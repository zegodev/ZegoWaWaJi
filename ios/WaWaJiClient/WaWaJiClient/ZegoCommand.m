//
//  ZegoCommand.m
//  WaWaJiClient
//
//  Created by summery on 02/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoCommand.h"
#import "ZegoManager.h"
#import "ZegoSetting.h"

static NSString *type = @"type";
static int value = 5;

@interface ZegoCommand()

typedef void(^completionBlock)(NSInteger errorCode, NSString *encryptedConfig);

@end

@implementation ZegoCommand

- (NSString *)apply:(int)clientSeq sessionId:(NSString *)sessionId continueChoice:(int)choice {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @513,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey : [NSNumber numberWithInteger:timestamp],
                                       continueKey : [NSNumber numberWithInt:choice]
                                       }
                           };
    
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)gameReadyReply:(int)serverSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInt:serverSeq],
                           cmdKey : @516,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)cancelApply:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @514,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)gameConfirm:(int)confirm clientSeq:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];

    __block NSString *confirmCommand = nil;
    
    dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
    
    // ！！！先从后台获取加密配置。本接口仅提供给 zego demo 使用。请开发者不要调用，自行从各自的业务后台获取。
    [self fetchEncryptedConfig:confirm type:type value:value sessionId:sessionId timestamp:timestamp completion:^(NSInteger errorCode, NSString *encryptedConfig) {
        if (errorCode == 0) {
            NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                                   cmdKey : @515,
                                   sessionIdKey : sessionId ? : @"",
                                   dataKey : @{confirmKey: [NSNumber numberWithInteger:confirm],
                                               configKey: encryptedConfig,
                                               timestampKey: [NSNumber numberWithInteger:timestamp]}
                                   };
            confirmCommand = [self encodeDictionaryToJSON:dict];
        } else {
            NSLog(@"获取后台加密 config 失败，错误码：%ld", (long)errorCode);
        }
        
        dispatch_semaphore_signal(semaphore);
    }];
    
    NSLog(@"等待后台返回加密 config");
    dispatch_time_t time = dispatch_time(DISPATCH_TIME_NOW, (RETRY_DURATION / 2) * NSEC_PER_SEC);
    dispatch_semaphore_wait(semaphore, time);
    
    return confirmCommand;
}

- (NSString *)moveLeft:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @528,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveRight:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @529,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveForward:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @530,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveBackward:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @531,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveDown:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @532,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)moveStop:(int)clientSeq sessionId:(NSString *)sessionId {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @533,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)resultReply:(int)serverSeq sessionId:(NSString *)sessionId choice:(int)choice {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:serverSeq],
                           cmdKey : @517,
                           sessionIdKey : sessionId ? : @"",
                           dataKey : @{continueKey: [NSNumber numberWithInteger:choice],
                                       timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)fetchGameInfo:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @518,
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
                           };
    return [self encodeDictionaryToJSON:dict];
}

- (NSString *)switchCamera:(int)clientSeq {
    NSTimeInterval timestamp = [self timestamp];
    NSDictionary *dict = @{seqKey : [NSNumber numberWithInteger:clientSeq],
                           cmdKey : @999,
                           dataKey : @{timestampKey: [NSNumber numberWithInteger:timestamp]}
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

- (NSDictionary *)parseContent:(NSString *)content {
    NSMutableDictionary *parsedDict = [[NSMutableDictionary alloc]initWithCapacity:1];
    NSDictionary *dict = [self decodeJSONToDictionary:content];
    
    parsedDict[seqKey] = dict[seqKey];                                      // 返回seq，必须
    parsedDict[cmdKey] = dict[cmdKey];                                      // 返回cmd，必须
    parsedDict[sessionIdOuterKey] = dict[sessionIdKey] ? : @"" ;            // data 外的 session_id
    
    NSDictionary *data = dict[dataKey];
    parsedDict[sessionIdInnerKey] = data[sessionIdKey] ? : @"" ;            // data 里的 session_id
    parsedDict[timestampKey] = data[timestampKey];                          // 返回时间戳，必须
    parsedDict[resultKey] = data[resultKey] ? : @-1;                        // 预约回复、游戏结果——>成功/失败
    parsedDict[playerIdKey] = data[playerKey][idKey] ? : @"" ;              // 预约上机回复、准备上机、游戏结果、获取用户信息、用户信息更新——>当前玩家Id
    parsedDict[playerNameKey] = data[playerKey][nameKey] ? : @"" ;          // 预约上机回复、准备上机、游戏结果、获取用户信息、用户信息更新——>当前玩家Id
    parsedDict[leftTimeKey] = data[playerKey][leftTimeKey] ? : @-1 ;        // 获取用户信息——>当前玩家剩余游戏时间
    parsedDict[indexKey] = data[indexKey] ? : @-1 ;                         // 预约上机回复——>当前排在x位
    parsedDict[gameTimeKey] = data[gameTimeKey] ? : @-1 ;                   // 准备上机、用户信息更新——>游戏总时长
    parsedDict[encryptedResultKey] = data[encryptedResultKey] ? : @"" ;     // 游戏结果——>结果校验串
    parsedDict[queueKey] = data[queueKey] ? : @[];                          // 获取用户信息、用户信息更新——>当前排队的玩家数组
    parsedDict[totalKey] = data[totalKey] ? : @-1 ;                         // 获取用户信息、用户信息更新——>房间内总人数
    
    return parsedDict;
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

- (NSDictionary *)decodeJSONToDictionary:(NSString *)json
{
    if (json == nil)
        return nil;
    
    NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
    if (jsonData)
    {
        NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:nil];
        return dictionary;
    }
    
    return nil;
}

- (BOOL)sendCommandToServer:(ZegoUser *)serverUser content:(NSString *)content completion:(ZegoCustomCommandBlock)block {
    if (serverUser) {
        return [[ZegoManager api] sendCustomCommand:@[serverUser] content:content completion:block];
    } else {
        NSLog(@"自定义信令发送对象为空");
    }
    return NO;
}


//WARNING: !!!本接口仅提供给 zego demo 使用。请开发者不要调用，自行从各自的业务后台获取
- (void)fetchEncryptedConfig:(int)confirm type:(NSString *)type value:(int)value sessionId:(NSString *)sessionId timestamp:(NSInteger)timestamp completion:(completionBlock)block {
    NSString *baseUrl = [NSString stringWithFormat:@"http://wsliveroom%u-api.zego.im:8181/pay?", [ZegoSetting sharedInstance].appID];
    NSString *param = [NSString stringWithFormat:@"app_id=%u&id_name=%@&session_id=%@&confirm=%d&time_stamp=%ld&item_type=%@&item_price=%d", [ZegoSetting sharedInstance].appID, [ZegoSetting sharedInstance].userID, sessionId, confirm, timestamp, type, value];
    
    NSString *requestUrl = [NSString stringWithFormat:@"%@%@", baseUrl, param];
    NSLog(@"fetch encrypted config url: %@", requestUrl);
    
    NSURL *URL = [NSURL URLWithString:requestUrl];
    NSURLRequest *request = [NSURLRequest requestWithURL:URL];
    
    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    configuration.timeoutIntervalForRequest = 10;
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
    NSURLSessionTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * data, NSURLResponse * response, NSError * error) {
        if (error) {
            NSLog(@"fetch encrypted config error: %@", error);
        }
        
        NSString *config;
        config = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        NSLog(@"fetch encrypted config: %@", config);
        
        if (block) {
            block(error.code, config);
        }
    }];
    
    [task resume];
}

@end
