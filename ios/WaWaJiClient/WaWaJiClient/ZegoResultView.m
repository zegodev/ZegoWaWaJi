//
//  ZegoResultView.m
//  WaWaJiClient
//
//  Created by summery on 08/11/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoResultView.h"

@implementation ZegoResultView

- (instancetype)initWithCoder:(NSCoder *)aDecoder {
    self = [super initWithCoder:aDecoder];
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
}

- (IBAction)onBackButton:(id)sender {
    [self removeFromSuperview];
    
    if ([self.delegate respondsToSelector:@selector(onClickBackButton:)]) {
        [self.delegate onClickBackButton:sender];
    }
}

- (IBAction)onContinueButton:(id)sender {
    [self removeFromSuperview];
    
    if ([self.delegate respondsToSelector:@selector(onClickContinueButton:)]) {
        [self.delegate onClickContinueButton:sender];
    }
}

- (void)setImageName:(NSString *)imageName {
    if (_imageName != imageName) {
        _imageName = imageName;
        [self.resultImageView setImage:[UIImage imageNamed:imageName]];
    }
}

@end
