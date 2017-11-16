//
//  ZegoReadyView.h
//  WaWaJiClient
//
//  Created by summery on 10/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <UIKit/UIKit.h>

@protocol ZegoReadyViewDelegate <NSObject>

- (IBAction)onClickCancelButton:(id)sender;
- (IBAction)onClickStartButton:(id)sender;

@end


@interface ZegoReadyView : UIView

@property (weak, nonatomic) IBOutlet UIButton *cancelButton;
@property (weak, nonatomic) IBOutlet UIButton *startButton;

@property (nonatomic, copy) NSString *startButtonTitle;

@property (nonatomic, weak) id<ZegoReadyViewDelegate> delegate;

@end
