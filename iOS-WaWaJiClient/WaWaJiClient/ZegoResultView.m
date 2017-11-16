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
    
    if (self) {
        NSLog(@"resultview-initwithcoder");
    }
    return self;
}

- (void)awakeFromNib
{
    [super awakeFromNib];
    
    NSLog(@"resultview-awakefromnib");
}

- (IBAction)onConfirmButton:(id)sender {
    [self removeFromSuperview];
    
    if ([self.delegate respondsToSelector:@selector(resultButtonClicked:)]) {
        [self.delegate resultButtonClicked:sender];
    }
}

- (void)setImageName:(NSString *)imageName {
    if (_imageName != imageName) {
        _imageName = imageName;
        [self.resultImageView setImage:[UIImage imageNamed:imageName]];
    }
}

/*
// Only override drawRect: if you perform custom drawing.
// An empty implementation adversely affects performance during animation.
- (void)drawRect:(CGRect)rect {
    // Drawing code
}
*/

@end
