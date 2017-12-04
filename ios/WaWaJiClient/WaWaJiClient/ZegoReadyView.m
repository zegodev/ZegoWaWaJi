//
//  ZegoReadyView.m
//  WaWaJiClient
//
//  Created by summery on 10/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoReadyView.h"

@implementation ZegoReadyView

- (void)awakeFromNib {
    [super awakeFromNib];
    self.startButton.adjustsImageWhenHighlighted = NO;
}

- (IBAction)onClickCancelButton:(id)sender {
    [self removeFromSuperview];
    
    if ([self.delegate respondsToSelector:@selector(onClickCancelButton:)]) {
        [self.delegate onClickCancelButton:sender];
    }
}

- (IBAction)onClickStartButton:(id)sender {
    [self removeFromSuperview];
    
    if ([self.delegate respondsToSelector:@selector(onClickStartButton:)]) {
        [self.delegate onClickStartButton:sender];
    }
}

- (void)setStartButtonTitle:(NSString *)title {
    if (_startButtonTitle != title) {
        _startButtonTitle = title;
        [self.startButton setTitle:_startButtonTitle forState:UIControlStateNormal];
    }
}

@end
