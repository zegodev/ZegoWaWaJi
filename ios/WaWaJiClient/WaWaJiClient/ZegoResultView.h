//
//  ZegoResultView.h
//  WaWaJiClient
//
//  Created by summery on 08/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ZegoResultViewDelegate <NSObject>

- (void)onClickBackButton:(UIButton *)button;
- (void)onClickContinueButton:(UIButton *)button;

@end

@interface ZegoResultView : UIView

@property (weak, nonatomic) IBOutlet UIImageView *resultImageView;
@property (weak, nonatomic) IBOutlet UIButton *backButton;
@property (weak, nonatomic) IBOutlet UIButton *continueButton;

@property (nonatomic, copy) NSString *imageName;

@property (nonatomic, weak) id<ZegoResultViewDelegate> delegate;

@end
