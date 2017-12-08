//
//  ZegoRoomViewController.m
//  WaWaJi
//
//  Created by summery on 16/10/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoRoomViewController.h"
#import "ZegoPlayViewController.h"
#import "ZegoRoomInfo.h"
#import "ZegoSetting.h"

static const NSString *cellIdentifier = @"RoomCellID";

static NSString *machine1 = @"00d11eca1001";
static NSString *machine2 = @"00d200ce17bf";
static NSString *machine3 = @"00d200ce17bf";   // 这里还要修改，临时写成 machine2

@implementation ZegoRoomCell

- (void)awakeFromNib {
    [super awakeFromNib];
    
    self.playStatusLabel.layer.cornerRadius = 2;
    self.playStatusLabel.layer.masksToBounds = YES;
    self.coverView.layer.borderWidth = 1.0;
    self.coverView.layer.borderColor = [UIColor colorWithRed:178/255.0 green:178/255.0 blue:178/255.0 alpha:1].CGColor;
}

@end

@interface ZegoRoomViewController () <UICollectionViewDelegate, UICollectionViewDataSource>

@property (weak, nonatomic) IBOutlet UIView *containerView;
@property (weak, nonatomic) IBOutlet UICollectionView *roomView;

@property (nonatomic, strong) ZegoRoomInfo *selectedRoom;
@property (nonatomic, strong) NSMutableArray<ZegoRoomInfo *> *roomList;
@property (nonatomic, strong) UIRefreshControl *refreshControl;

@end

@implementation ZegoRoomViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    self.roomList = [NSMutableArray array];
    self.refreshControl = [[UIRefreshControl alloc] init];
    
    [self.refreshControl addTarget:self action:@selector(handleRefresh:) forControlEvents:UIControlEventValueChanged];
    [self.roomView insertSubview:self.refreshControl atIndex:0];
    self.roomView.alwaysBounceVertical = YES;

    if ([[[UIDevice currentDevice] systemVersion] floatValue] < 11.0)  {
        self.automaticallyAdjustsScrollViewInsets = NO;
    } else {
        self.roomView.contentInsetAdjustmentBehavior = UIScrollViewContentInsetAdjustmentNever;
    }
    
    [self setBarButtonItemTitle];
    
    [self getPlayRoom];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onApplicationActive:) name:UIApplicationDidBecomeActiveNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onRoomInstanceClear:) name:@"RoomInstanceClear" object:nil];
    
    self.navigationController.navigationBar.barTintColor = [UIColor colorWithRed:13/255.0 green:112/255.0 blue:255/255.0 alpha:1.0];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlack;
    
    self.roomView.layer.cornerRadius = 4;
    
    [self.navigationItem.rightBarButtonItem setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIFont systemFontOfSize:14], NSFontAttributeName, nil]
                                                          forState:UIControlStateNormal];
    [self.navigationItem.rightBarButtonItem setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIFont systemFontOfSize:14], NSFontAttributeName, nil]
                                                          forState:UIControlStateHighlighted];

}

- (void)viewWillDisappear:(BOOL)animated {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    if ([segue.identifier isEqualToString:@"EnterRoomID"]) {
        ZegoPlayViewController *playViewController = (ZegoPlayViewController *)segue.destinationViewController;
    
        playViewController.roomID = self.selectedRoom.roomID;
        playViewController.publishStream = [NSString stringWithFormat:@"zego_wawaji_%@_publish_ios", self.selectedRoom.roomID];
        playViewController.roomTitle = self.selectedRoom.roomName;
        
        NSMutableArray *streamIDs = [NSMutableArray arrayWithCapacity:2];

        if (self.selectedRoom.streamInfo.count) {
            for (NSString *streamID in self.selectedRoom.streamInfo) {
                if ([streamID hasPrefix:@"WWJ"]) {
                    [streamIDs addObject:streamID];
                }
            }
            
            if (streamIDs.count > 1) {
                if ([streamIDs[0] hasSuffix:@"_2"]) {
                    [streamIDs exchangeObjectAtIndex:0 withObjectAtIndex:1];
                }
            }
            
            // 非 ZEGO 娃娃机，没有 WWJ_ZEGO 开头的流就取前面 2 条流
            if (![self.selectedRoom.roomID hasPrefix:@"WWJ_ZEGO_STREAM"] && streamIDs.count == 0) {
                if (self.selectedRoom.streamInfo.count <= 2) {
                    [streamIDs addObjectsFromArray:self.selectedRoom.streamInfo];
                } else {
                    [streamIDs addObject:self.selectedRoom.streamInfo[0]];
                    [streamIDs addObject:self.selectedRoom.streamInfo[1]];
                }
            }
        }

        playViewController.playStreamList = [streamIDs copy];
    }
}

- (void)setBarButtonItemTitle
{
    UIBarButtonItem *rightBarButton = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"刷新", nil) style:UIBarButtonItemStylePlain target:self action:@selector(onRightBarButton:)];
    [rightBarButton setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIFont systemFontOfSize:14], NSFontAttributeName, nil]
                                                          forState:UIControlStateNormal];
    [rightBarButton setTitleTextAttributes:[NSDictionary dictionaryWithObjectsAndKeys:[UIFont systemFontOfSize:14], NSFontAttributeName, nil]
                                                          forState:UIControlStateHighlighted];
    rightBarButton.tintColor = [UIColor whiteColor];
    self.navigationItem.rightBarButtonItem = rightBarButton;
}

- (void)setBarButtonItemCustomView
{
    UIActivityIndicatorView *activityView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleGray];
    activityView.hidesWhenStopped = YES;
    [activityView startAnimating];
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithCustomView:activityView];
}

- (void)onRightBarButton:(id)sender
{
    [self setBarButtonItemCustomView];
    
    [self refreshRoomList];
}

- (void)onRefreshRoomListFinished
{
    if (self.navigationItem.rightBarButtonItem != nil)
    {
        [self setBarButtonItemTitle];
    }
}

- (void)refreshRoomList
{
    if ([self.refreshControl isRefreshing])
        return;
    
    [self.roomList removeAllObjects];
    [self getPlayRoom];
}

- (void)handleRefresh:(UIRefreshControl *)refreshControl
{
    [self.roomList removeAllObjects];
    [self getPlayRoom];
}

- (void)onApplicationActive:(NSNotification *)notification
{
    [self handleRefresh:self.refreshControl];
}

- (void)onRoomInstanceClear:(NSNotification *)notification
{
    [self getPlayRoom];
}

- (void)getPlayRoom
{
    [self.refreshControl beginRefreshing];

    NSString *mainDomain = @"zego.im";
    NSString *baseUrl = nil;
//    if ([ZegoDemoHelper usingAlphaEnv])
//        baseUrl = @"https://alpha-liveroom-api.zego.im";
//    else if([ZegoDemoHelper usingTestEnv])
//        baseUrl =@"https://test2-liveroom-api.zego.im";
//    else
    baseUrl = [NSString stringWithFormat:@"https://liveroom%u-api.%@", [ZegoSetting sharedInstance].appID, mainDomain];

    NSURL *URL = [NSURL URLWithString:[NSString stringWithFormat:@"%@/demo/roomlist?appid=%u", baseUrl, [ZegoSetting sharedInstance].appID]];
    NSURLRequest *request = [NSURLRequest requestWithURL:URL];

    NSLog(@"URL %@", URL.absoluteString);

    NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration defaultSessionConfiguration];
    configuration.timeoutIntervalForRequest = 10;

    NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {

        dispatch_async(dispatch_get_main_queue(), ^{

            if ([self.refreshControl isRefreshing])
                [self.refreshControl endRefreshing];

            [self onRefreshRoomListFinished];

            [self.roomList removeAllObjects];

            if (error)
            {
                NSLog(@"get play room error: %@", error);
                return;
            }

            if ([response isKindOfClass:[NSHTTPURLResponse class]])
            {
                NSError *jsonError;
                NSDictionary *jsonResponse = [NSJSONSerialization JSONObjectWithData:data options:0 error:&jsonError];
                if (jsonError)
                {
                    NSLog(@"parsing json error");
                    return;
                }
                else
                {
                    NSLog(@"%@", jsonResponse);
                    NSUInteger code = [jsonResponse[@"code"] integerValue];
                    if (code != 0)
                        return;

                    NSArray *roomList = jsonResponse[@"data"][@"room_list"];
                    for (int idx = 0; idx < roomList.count; idx++)
                    {
                        ZegoRoomInfo *info = [ZegoRoomInfo new];
                        NSDictionary *infoDict = roomList[idx];
                        
                        // 过滤掉没有流的房间
                        NSArray *streams = infoDict[@"stream_info"];
                        if (streams.count == 0) {
                            continue;
                        }
                        
                        info.roomID = infoDict[@"room_id"];
                        if (info.roomID.length == 0)
                            continue;

                        info.anchorID = infoDict[@"anchor_id_name"];
                        info.anchorName = infoDict[@"anchor_nick_name"];
                        info.roomName = infoDict[@"room_name"];

                        info.streamInfo = [[NSMutableArray alloc] initWithCapacity:1];
                        for (NSDictionary *dict in infoDict[@"stream_info"]) {
                            [info.streamInfo addObject:dict[@"stream_id"]];
                        }

                        [self.roomList addObject:info];
                    }
                    
                    NSMutableArray *zegoMachine = [NSMutableArray arrayWithCapacity:1];
                    NSMutableArray *tmpRoom = [NSMutableArray arrayWithArray:self.roomList];
                    
                    for (ZegoRoomInfo *info in self.roomList) {
                        if ([info.roomID hasSuffix:machine1] || [info.roomID hasSuffix:machine2] || [info.roomID hasSuffix:machine3]) {
                            [tmpRoom removeObject:info];
                            [zegoMachine addObject:info];
                        }
                    }
                    
                    [zegoMachine addObjectsFromArray:tmpRoom];
                    self.roomList = zegoMachine;

                    [self.roomView reloadData];
                }
            }
        });
    }];

    [task resume];
}

- (void)showAlert:(NSString *)message title:(NSString *)title {
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:title
                                                                             message:message
                                                                      preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *confirm = [UIAlertAction actionWithTitle:NSLocalizedString(@"OK", nil)
                                                      style:UIAlertActionStyleDefault
                                                    handler:^(UIAlertAction * _Nonnull action) {
                                                        
                                                    }];
    
    [alertController addAction:confirm];
    
    [self presentViewController:alertController animated:YES completion:nil];
}

#pragma mark - UICollectionViewDataSource

- (NSInteger)collectionView:(UICollectionView *)collectionView numberOfItemsInSection:(NSInteger)section {
    return self.roomList.count;
}

- (NSInteger)numberOfSectionsInCollectionView:(UICollectionView *)collectionView {
    return 1;
}

- (UICollectionViewCell *)collectionView:(UICollectionView *)collectionView cellForItemAtIndexPath:(NSIndexPath *)indexPath {
    ZegoRoomCell *cell = (ZegoRoomCell*)[collectionView dequeueReusableCellWithReuseIdentifier:cellIdentifier forIndexPath:indexPath];
    
    if (indexPath.item > self.roomList.count) {
        return cell;
    }
    
    ZegoRoomInfo *roomInfo = self.roomList[indexPath.item];
    
#ifdef SHOW
    // 演示环境里都是 zego 的机器
    [cell.roomImageView setImage:[UIImage imageNamed:[NSString stringWithFormat:@"0%ld", indexPath.item % 6 + 1]]];
#else
    if ([roomInfo.roomID hasSuffix:machine1] || [roomInfo.roomID hasSuffix:machine2] || [roomInfo.roomID hasSuffix:machine3]) {
        [cell.roomImageView setImage:[UIImage imageNamed:[NSString stringWithFormat:@"0%ld", indexPath.item % 6 + 1]]];
    } else {
        [cell.roomImageView setImage:[UIImage imageNamed:@"customer"]];
    }
#endif
    
    
    cell.roomTitleLabel.lineBreakMode = NSLineBreakByTruncatingMiddle;
    if (roomInfo.roomName.length > 0) {
        cell.roomTitleLabel.text = roomInfo.roomName;
    } else {
        cell.roomTitleLabel.text = [NSString stringWithFormat:@"娃娃机 %ld", indexPath.item + 1];
    }
    
    return cell;
}

#pragma mark - UICollectionViewDelegate

- (void)collectionView:(UICollectionView *)collectionView didSelectItemAtIndexPath:(NSIndexPath *)indexPath {
    if (!self.roomList.count) {
        [self showAlert:NSLocalizedString(@"进入房间失败，请刷新后重试", nil) title:NSLocalizedString(@"提示", nil)];
        return;
    }
    
    if (indexPath.item > self.roomList.count) {
        return;
    }

    self.selectedRoom = self.roomList[indexPath.item];
    
    [self performSegueWithIdentifier:@"EnterRoomID" sender:nil];
}

@end
