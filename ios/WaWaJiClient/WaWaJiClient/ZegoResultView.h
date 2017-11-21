//
//  ZegoResultView.h
//  WaWaJiClient
//
//  Created by summery on 08/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ZegoResultViewDelegate <NSObject>

- (void)resultButtonClicked:(UIButton *)button;

@end

@interface ZegoResultView : UIView

@property (weak, nonatomic) IBOutlet UIImageView *resultImageView;
@property (weak, nonatomic) IBOutlet UIButton *resultConfirmButton;

@property (nonatomic, copy) NSString *imageName;

@property (nonatomic, weak) id<ZegoResultViewDelegate> delegate;

@end
