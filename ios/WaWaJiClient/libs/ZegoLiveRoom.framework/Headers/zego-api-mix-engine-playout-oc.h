#ifndef zego_api_mix_engine_playout_oc_h
#define zego_api_mix_engine_playout_oc_h

#import <Foundation/Foundation.h>
#include "zego-api-defines-oc.h"

@interface ZegoMixEngine : NSObject


/**
 把引擎播放的声音混到推流中

 @param enable 是否打开
 @return 0 成功
 */
+ (int)MixEnginePlayout:(BOOL)enable;

@end

#endif /* zego_api_mix_engine_playout_oc_h */
