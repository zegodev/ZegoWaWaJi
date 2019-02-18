#ifndef zego_api_audio_encrypt_decrypt_oc
#define zego_api_audio_encrypt_decrypt_oc

#import <Foundation/Foundation.h>

@protocol ZegoAudioEncryptDecryptDelegate <NSObject>
@required
/**
 音频加密、解密回调
 
 @param streamID 标识当前回调的音频数据属于哪条流
 @param inData  SDK回调给用户的音频数据, 推流时用于加密，拉流时用于解密
 @param inDataLen SDK回调给用户的音频数据的长度（单位：btye）
 @param outData 用户将加密（推流）或者解密（拉流）后的音频数据回传给SDK
 @param outDataLen 用户实际回传给SDK的数据大小（单位：btye)
 @param maxOutBufLen 用户回传数据的缓冲区的最大长度（单位：btye）
 **/
- (void)onAudioEncryptDecrypt:(NSString *)streamID inData:(const unsigned char *)pInData inDataLen:(int)inDataLen outData:(unsigned char *)pOutData outDataLen:(int *)pOutDataLen maxOutBufLen:(int)maxOutBufLen;

@end

@interface ZegoAudioEncryptDecrypt : NSObject

/**
 设置回调，接收音频数据进行加密或者解密后，再回传给sdk
 
 @param 音频加密、解密回调
 @param discussion 成功推流、拉流后才会有回调
 **/
- (void)setAudioEncryptDecryptDelegage:(id<ZegoAudioEncryptDecryptDelegate>)delegate;

/**
 音频加密、解密开关
 
 @param enable：开启，false 关闭
 @discussion 必须再init sdk之前调用
 */
+ (void)enableAudioEncryptDecrypt:(BOOL)enable;

@end

#endif
