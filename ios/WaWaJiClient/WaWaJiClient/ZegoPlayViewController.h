//
//  ZegoPlayViewController.h
//  WaWaJi
//
//  Created by summery on 16/10/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <UIKit/UIKit.h>

extern BOOL g_isGrabed;

@interface ZegoPlayViewController : UIViewController

@property (nonatomic, copy) NSString *roomID;
@property (nonatomic, copy) NSArray *playStreamList;
@property (nonatomic, copy) NSString *publishStream;
@property (nonatomic, copy) NSString *roomTitle;

@end
