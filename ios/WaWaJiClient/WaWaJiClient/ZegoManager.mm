//
//  ZegoManager.m
//  WaWaJi
//
//  Created by summery on 16/10/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoManager.h"
#import "ZegoSetting.h"

@implementation ZegoManager

static ZegoLiveRoomApi *_apiInstance = nil;

+ (ZegoLiveRoomApi *)api {
    if (_apiInstance == nil) {
        
        [ZegoLiveRoomApi setUseTestEnv:NO];
        
#ifdef DEBUG
        [ZegoLiveRoomApi setVerbose:NO];
#endif
        
        // 初始化用户信息
        [ZegoLiveRoomApi setUserID:[ZegoSetting sharedInstance].userID userName:[ZegoSetting sharedInstance].userName];
        
        // 暂时 hardcode 鉴权信息
#ifdef OLD
#endif
        
#ifdef SHOW
#endif
        
#ifdef DEBUG_OUTER
        NSString *sign = @"0x30,0x3a,0x83,0x1b,0xae,0x23,0xc6,0x6e,0x73,0xba,0x23,0xfc,0x69,0xa2,0x7f,0xe4,0x9f,0xca,0x1c,0x03,0x9f,0x93,0x5b,0x47,0xf0,0x6b,0xa6,0xf2,0x81,0x21,0x5b,0xa5"; // 外部调试
#endif
        
#ifdef DEBUG_INNER
#endif
        
        NSData *signData = ConvertStringToSign(sign);

#ifdef OLD
#endif
#ifdef SHOW
#endif
#ifdef DEBUG_OUTER
        _apiInstance = [[ZegoLiveRoomApi alloc] initWithAppID:3671502238 appSignature:signData]; // 外部调试
#endif
#ifdef DEBUG_INNER
#endif
        
    }
    return _apiInstance;
}

+ (void)releaseApi {
    _apiInstance = nil;
}

NSData* ConvertStringToSign(NSString* strSign)
{
    if(strSign == nil || strSign.length == 0)
        return nil;
    strSign = [strSign lowercaseString];
    strSign = [strSign stringByReplacingOccurrencesOfString:@" " withString:@""];
    strSign = [strSign stringByReplacingOccurrencesOfString:@"0x" withString:@""];
    NSArray* szStr = [strSign componentsSeparatedByString:@","];
    int nLen = (int)[szStr count];
    Byte szSign[32];
    for(int i = 0; i < nLen; i++)
    {
        NSString *strTmp = [szStr objectAtIndex:i];
        if(strTmp.length == 1) {
            szSign[i] = toByte(strTmp);
        } else {
            szSign[i] = toByte([strTmp substringWithRange:NSMakeRange(0, 1)]) << 4 | toByte([strTmp substringWithRange:NSMakeRange(1, 1)]);
        }
//        NSLog(@"%x,", szSign[i]);
    }
    
    NSData *sign = [NSData dataWithBytes:szSign length:32];
    return sign;
}

Byte toByte(NSString* c)
{
    NSString *str = @"0123456789abcdef";
    Byte b = [str rangeOfString:c].location;
    return b;
}


@end

