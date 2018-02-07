//
//  ZegoAESEncrypt.h
//  WaWaJiClient
//
//  Created by summery on 06/12/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <Foundation/Foundation.h>

/**
 开发者业务后台调用 ZEGO 后台接口的密钥，由即构提供 32 个字符的字符串。
 娃娃机业务中用于娃娃机端程序和业务后台敏感数据通讯的密钥。
 目前创建 App 时，由技术支持人员生成。
 
 ！！！请注意：
 1. 此处只是为了演示 demo 方便，临时 hardcode，请开发者不要复制粘贴该串。
 2. 正式代码中，客户端不应该获取该 serverSecret。
 **/
static const NSString *serverSecret = @"abcdefghabcdefghabcdefghabcdefgh";


@interface ZegoAESEncrypt : NSObject

+ (NSString *)encryptAES:(NSString *)content key:(NSString *)key;

@end
