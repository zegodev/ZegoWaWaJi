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
//        NSString *sign = @"0x91,0x93,0xcc,0x66,0x2a,0x1c,0x0e,0xc1,0x35,0xec,0x71,0xfb,0x07,0x19,0x4b,0x38,0x41,0xd4,0xad,0x83,0x78,0xf2,0x59,0x90,0xe0,0xa4,0x0c,0x7f,0xf4,0x28,0x41,0xf7";
        NSString *sign = @"0x16,0x6c,0x57,0x8b,0xb0,0xb5,0x51,0xfd,0xc4,0xd9,0xb7,0xaf,0x96,0x1f,0x13,0x82,0xc9,0xb6,0x2b,0x0f,0x99,0x75,0x3a,0xb3,0xc1,0x7e,0xc4,0x54,0x30,0x93,0x28,0xfa";
        NSData *signData = ConvertStringToSign(sign);
        _apiInstance = [[ZegoLiveRoomApi alloc] initWithAppID:3177435262 appSignature:signData];
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
