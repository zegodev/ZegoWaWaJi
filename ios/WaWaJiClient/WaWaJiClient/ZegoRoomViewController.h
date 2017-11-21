//
//  ZegoRoomViewController.h
//  WaWaJi
//
//  Created by summery on 16/10/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface ZegoRoomCell: UICollectionViewCell

@property (weak, nonatomic) IBOutlet UIView *coverView;

@property (unsafe_unretained, nonatomic) IBOutlet UIImageView *roomImageView;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *playStatusLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *queueCountLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *roomTitleLabel;

@end

@protocol ZegoRoomViewControllerDelegate <NSObject>

- (void)onRefreshRoomListFinished;

@end

@interface ZegoRoomViewController : UIViewController

- (void)refreshRoomList;

@property (nonatomic, weak) id<ZegoRoomViewControllerDelegate> delegate;

@end
