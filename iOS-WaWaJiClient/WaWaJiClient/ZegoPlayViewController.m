//
//  ZegoPlayViewController.m
//  WaWaJi
//
//  Created by summery on 16/10/2017.
//  Copyright © 2017 zego. All rights reserved.
//

#import "ZegoPlayViewController.h"
#import "ZegoLogTableViewController.h"
#import "ZegoRoomViewController.h"
#import "ZegoManager.h"
#import "ZegoSetting.h"
#import "ZegoCommand.h"
#import "ZegoResultView.h"
#import "ZegoReadyView.h"

#define kHeadSetStateChangeNotification     @"headSetStateChange"

typedef NS_ENUM(NSInteger, ZegoClientState)
{
    ZegoClientStateInitial = 0,         //  初始状态
    ZegoClientStateApplying,            //  预约中
    ZegoClientStateGameWaiting,         //  预约成功，等待上机（可取消预约）
    ZegoClientStateApplyCancelling,     //  取消预约中
    ZegoClientStateGameConfirming,      //  上机选择，等待确认
    ZegoClientStateGamePlaying,         //  游戏中
    ZegoClientStateResultWaiting        //  游戏结束，等待结果
};

typedef NS_ENUM(NSInteger, ZegoStreamStatus)
{
    ZegoStreamStatusStartPlaying = 0,   // 开始播放
    ZegoStreamStatusPlaySucceed,        // 播放成功，有流数据
    ZegoStreamStatusPlaySucceedEmpty,   // 播放成功，没有流数据
    ZegoStreamStatusPlayFail,           // 播放失败
};

static const NSString *applyKey =       @"receivedApplyReply";
static const NSString *cancelApplyKey = @"receivedCacelApplyReply";
static const NSString *confirmKey =     @"receivedConfirmReply";
static const NSString *resultKey =      @"receivedResultReply";

@interface ZegoPlayViewController () <ZegoRoomDelegate, ZegoLivePlayerDelegate, ZegoLivePublisherDelegate, UINavigationControllerDelegate, ZegoResultViewDelegate, ZegoReadyViewDelegate>

@property (weak, nonatomic) IBOutlet UIView *playViewContainer;
@property (weak, nonatomic) IBOutlet UIView *toolView;
@property (weak, nonatomic) IBOutlet UIView *controlView;

@property (weak, nonatomic) IBOutlet UIView *firstPlayView;         // 第一条流的播放 view
@property (weak, nonatomic) IBOutlet UIView *secondPlayView;        // 第二条流的播放 view

@property (weak, nonatomic) IBOutlet UIButton *prepareButton;       // 开始游戏、预约、取消预约
@property (weak, nonatomic) IBOutlet UIButton *switchPlayButton;    // 切换视角

@property (weak, nonatomic) IBOutlet UIButton *forwardButton;       // 向前移动
@property (weak, nonatomic) IBOutlet UIButton *backwardButton;      // 向后
@property (weak, nonatomic) IBOutlet UIButton *leftwardButton;      // 向左移动
@property (weak, nonatomic) IBOutlet UIButton *rightwardButton;     // 向右
@property (weak, nonatomic) IBOutlet UIButton *grabButton;          // 抓取
@property (weak, nonatomic) IBOutlet UILabel *countdownLabel;

@property (unsafe_unretained, nonatomic) IBOutlet UIBarButtonItem *backBarButton;
@property (weak, nonatomic) IBOutlet UIBarButtonItem *logBarButton;

@property (weak, nonatomic) IBOutlet UIView *hintView;
@property (weak, nonatomic) IBOutlet UIImageView *networkView;
@property (weak, nonatomic) IBOutlet UIButton *networkButton;
@property (weak, nonatomic) IBOutlet UILabel *countLabel;           // 房间总人数

@property (weak, nonatomic) IBOutlet UIView *networkQualityView;
@property (weak, nonatomic) IBOutlet UILabel *fpsLabel;
@property (weak, nonatomic) IBOutlet UILabel *videoBitcodeLabel;
@property (weak, nonatomic) IBOutlet UILabel *audioBitcodeLabel;
@property (weak, nonatomic) IBOutlet UILabel *rttLabel;
@property (weak, nonatomic) IBOutlet UILabel *packageLossLabel;

@property (nonatomic, strong) ZegoReadyView *readyView;

@property (nonatomic, strong) NSMutableArray<ZegoStream *> *streamList;
@property (nonatomic, strong) NSMutableArray<ZegoStream *> *originStreamList;   // 直播秒开流列表

@property (nonatomic, copy) NSString *firstStreamID;                // 房间内第一条流 ID，默认显示
@property (nonatomic, copy) NSString *secondStreamID;               // 房间内第二条流 ID，默认不显示，可切换显示
@property (nonatomic, assign) NSInteger currentVisibleStreamIndex;  // 当前可见流，1 表示第一条流可见，2 表示第二条流可见，以此类推

@property (nonatomic, assign) ZegoStreamStatus firstStreamStatus;   // 第一条流状态
@property (nonatomic, assign) ZegoStreamStatus secondStreamStatus;  // 第二条流状态

@property (nonatomic, assign) BOOL loginRoomSucceed;                // 登录成功
@property (nonatomic, assign) BOOL isOperating;                     // 正在上机
@property (nonatomic, copy) NSString *currentPlayer;                // 当前正在玩游戏的用户
@property (nonatomic, strong) ZegoUser *serverUser;                 // 服务器

@property (nonatomic, strong) NSMutableArray *logArray;             // 操作日志
@property (nonatomic, assign) NSInteger queueCount;                 // 前面排队人数
@property (nonatomic, assign) NSInteger totalCount;                 // 房间总人数

@property (nonatomic, strong) UIAlertController *alert;

@property (nonatomic, strong) NSTimer *applyTimer;
@property (nonatomic, strong) NSTimer *cancelApplyTimer;
@property (nonatomic, strong) NSTimer *readyTimer;
@property (nonatomic, strong) NSTimer *playTimer;
@property (nonatomic, strong) NSTimer *confirmTimer;
@property (nonatomic, strong) NSTimer *resultTimer;

@property (nonatomic, assign) NSInteger applyCountdown;
@property (nonatomic, assign) NSInteger cancelApplyCountdown;
@property (nonatomic, assign) NSInteger readyCountdown;
@property (nonatomic, assign) NSInteger playCountdown;
@property (nonatomic, assign) NSInteger confirmCountdown;
@property (nonatomic, assign) NSInteger resultCountdown;

@property (nonatomic, strong) ZegoCommand *command;

@property (nonatomic, assign) ZegoClientState state;                // 控制游戏状态

@property (nonatomic, assign) BOOL isApplyTimeout;

@property (nonatomic, assign) int applySeq;
@property (nonatomic, assign) int cancelApplySeq;
@property (nonatomic, assign) int confirmSeq;
@property (nonatomic, assign) int gameReadySeq;
@property (nonatomic, assign) int gameResultSeq;

@property (nonatomic, strong) NSMutableDictionary *receivedReplyCounts;        // 用于去重同一个 seq reply
@property (nonatomic, strong) NSMutableDictionary *replyTimeout;        // 用于去重同一个 reply

@property (nonatomic, assign) int confirm;  // 1 确认上机，0 确认不上机

@property (nonatomic, strong) UITapGestureRecognizer *tapGesture;

@end

@implementation ZegoPlayViewController

#pragma mark -- Life cycle

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // SDK
    [self setupLiveKit];
    [self loginRoom];
    
    [self setupUI];
    [self setupModel];

    // 秒播
    [self playingStreamOnEnteringRoom];
    
    // 监听电话事件
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(audioSessionWasInterrupted:) name:AVAudioSessionInterruptionNotification object:nil];

    // 监听耳机插拔
    [[ZegoSetting sharedInstance] checkHeadSet];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleAudioRouteChanged:) name:AVAudioSessionRouteChangeNotification object:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self setIdelTimerDisable:NO];
}

// FIXME: 用 loginroomsuccess 不合理，暂时没有更好的办法，待改
- (void)viewWillLayoutSubviews {
    if (!self.loginRoomSucceed) {
        if (self.currentVisibleStreamIndex == 1) {
            if (self.firstStreamID.length) {
                [self addPlayStatusImage:[UIImage imageNamed:@"Loading-1"] inView:self.firstPlayView];
            } else {
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-1"] inView:self.firstPlayView];
            }
        } else if (self.currentVisibleStreamIndex == 2) {
            if (self.secondStreamID.length) {
                [self addPlayStatusImage:[UIImage imageNamed:@"Loading-2"] inView:self.secondPlayView];
            } else {
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-2"] inView:self.firstPlayView];
            }
        }
    }
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Private

- (void)setupLiveKit {
    [[ZegoManager api] setRoomDelegate:self];
    [[ZegoManager api] setPlayerDelegate:self];
    [[ZegoManager api] setPublisherDelegate:self];
}

- (void)setupUI {
    self.controlView.hidden = YES;
    self.toolView.layer.cornerRadius = 6.0;
    self.playViewContainer.layer.cornerRadius = 6.0;
    self.firstPlayView.layer.cornerRadius = 6.0;
    self.firstPlayView.clipsToBounds = YES;
    self.secondPlayView.layer.cornerRadius = 6.0;
    self.secondPlayView.clipsToBounds = YES;
    self.hintView.layer.cornerRadius = 6.0;
    
    self.navigationController.navigationBar.barTintColor = [UIColor colorWithRed:13/255.0 green:112/255.0 blue:255/255.0 alpha:1.0];
    self.navigationController.navigationBar.barStyle = UIBarStyleBlack;
    [self.logBarButton setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14]} forState:UIControlStateNormal];
    [self.logBarButton setTitleTextAttributes:@{NSFontAttributeName:[UIFont systemFontOfSize:14]} forState:UIControlStateHighlighted];
    self.navigationItem.title = self.roomTitle;
    self.navigationController.delegate = self;
    
    self.prepareButton.enabled = NO;
    self.prepareButton.titleLabel.numberOfLines = 2;
    self.prepareButton.titleLabel.textAlignment = NSTextAlignmentCenter;
    
    // 切换视角 button，登录成功后才能 enable
    self.switchPlayButton.layer.cornerRadius = 6;
    self.switchPlayButton.enabled = NO;
    
    self.networkQualityView.hidden = YES;
    self.networkButton.adjustsImageWhenHighlighted = NO;
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissNetworkView)];
    gesture.numberOfTapsRequired = 1;
    [self.toolView addGestureRecognizer:gesture];
}

- (void)setupModel {
//    self.originStreamList = [[NSMutableArray alloc] initWithCapacity:0];
//    self.streamList = [[NSMutableArray alloc] initWithCapacity:0];
    
    if (self.playStreamList.count == 0) {
        [self addLog:NSLocalizedString(@"获取到的第一路流，第二路流 ID 均为空", nil)];
    } else if (self.playStreamList.count == 1) {
        if ([self.playStreamList.firstObject hasSuffix:@"_2"]) {
            self.firstStreamID = nil;
            self.secondStreamID = self.playStreamList.firstObject;
            [self addLog:NSLocalizedString(@"获取到的第一路流 ID 为空", nil)];
        } else {
            self.firstStreamID = self.playStreamList.firstObject;
            self.secondStreamID = nil;
            [self addLog:NSLocalizedString(@"获取到的第二路流 ID 为空", nil)];
        }
    } else if (self.playStreamList.count == 2) {
        self.firstStreamID = self.playStreamList[0];
        self.secondStreamID = self.playStreamList[1];
    }
    
    self.currentVisibleStreamIndex = 1;
    
    self.isOperating = NO;
    self.queueCount = 0;
    self.logArray = [NSMutableArray array];
    
    self.command = [[ZegoCommand alloc] init];
    
    self.receivedReplyCounts = [[NSMutableDictionary alloc] initWithCapacity:0];
    self.replyTimeout = [[NSMutableDictionary alloc] initWithCapacity:0];
    
    [self addLog:[NSString stringWithFormat:NSLocalizedString(@"用户ID: %@，用户名: %@", nil), [ZegoSetting sharedInstance].userID, [ZegoSetting sharedInstance].userName]];
}

- (void)loginRoom {
    [self addLog:[NSString stringWithFormat:NSLocalizedString(@"开始登录房间，房间 ID：%@", nil), self.roomID]];
    
    [[ZegoManager api] setRoomConfig:NO userStateUpdate:NO];
    
    [[ZegoManager api] loginRoom:self.roomID role:ZEGO_AUDIENCE withCompletionBlock:^(int errorCode, NSArray<ZegoStream *> *streamList) {
        if (errorCode == 0) {
            self.loginRoomSucceed = YES;
            [self addLog:[NSString stringWithFormat:NSLocalizedString(@"登录房间成功，房间 ID：%@", nil), self.roomID]];
            
            if (streamList.count == 0) {
                [self addLog:NSLocalizedString(@"登录成功，房间流列表为空", nil)];
                return;
            }
            
            for (ZegoStream *stream in streamList) {
                // 找到服务器推流的 user
                if ([stream.userName hasPrefix:@"WWJS_"]) {
                    ZegoUser *user = [[ZegoUser alloc] init];
                    user.userId = stream.userID;
                    user.userName = stream.userName;
                    self.serverUser = user;
                }
                
                if (stream.extraInfo.length) {
                    NSDictionary *dict = [self decodeJSONToDictionary:stream.extraInfo];
                    if (dict) {
                        // 房间排队人数
                        NSInteger queueCount = [dict[@"queue_number"] integerValue];
                        self.queueCount = queueCount;
                        
                        // 房间总人数
                        self.totalCount = [dict[@"total"] integerValue];
                        self.countLabel.text = [NSString stringWithFormat: NSLocalizedString(@"%d人在房间", nil), self.totalCount];
                        
                        // 正在游戏的人
                        NSDictionary *player = dict[@"player"];
                        NSString *playerId = player[@"id"];
                        self.currentPlayer = playerId;
                        
                        [self addLog:[NSString stringWithFormat:NSLocalizedString(@"房间 %@ 内排队人数：%d，总人数：%d", nil), self.roomID, self.queueCount, self.totalCount]];
                    }
                }
            }
            
            self.state = ZegoClientStateInitial;
            
            if (!self.serverUser) {
                [self addLog:[NSString stringWithFormat:NSLocalizedString(@"登录房间成功，但获取服务器 user 失败", nil), self.roomID]];
            }
            
            // FIXME: 暂时在此时高亮开始游戏 button，最好改成流播放成功以后
            self.prepareButton.enabled = YES;
            self.switchPlayButton.enabled = YES;
        } else {
            self.loginRoomSucceed = NO;
            
            [self showAlert:NSLocalizedString(@"登录房间失败，请退出房间稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
            [self addLog: [NSString stringWithFormat:NSLocalizedString(@"登录房间失败，房间 ID：%@，错误码：%d", nil), self.roomID, errorCode]];
        }
    }];
}

- (void)playingStreamOnEnteringRoom {
    NSString *logString = [NSString stringWithFormat:NSLocalizedString(@"秒播。房间 ID：%@，\n第一路流 ID：%@，\n第二路流 ID：%@", nil), self.roomID, self.firstStreamID, self.secondStreamID];
    [self addLogString:logString];
    
    if (self.firstStreamID.length) {
        self.currentVisibleStreamIndex = 1;
        [self playVisibleStream:self.firstStreamID inView:self.firstPlayView];
    }
    
    if (self.secondStreamID.length) {
         [self playInvisibleStream:self.secondStreamID inView:self.secondPlayView];
    }
}

- (void)playVisibleStream:(NSString *)streamID inView:(UIView *)view {
    if (streamID.length) {
        [[ZegoManager api] startPlayingStream:streamID inView:view];
        [[ZegoManager api] setViewMode:ZegoVideoViewModeScaleAspectFit ofStream:streamID];
        [[ZegoManager api] setPlayVolume:100 ofStream:streamID];
    }
}

- (void)playInvisibleStream:(NSString *)streamID inView:(UIView *)view {
    if (streamID.length) {
        [[ZegoManager api] startPlayingStream:streamID inView:view];
        [[ZegoManager api] setViewMode:ZegoVideoViewModeScaleAspectFit ofStream:streamID];
        [[ZegoManager api] setPlayVolume:0 ofStream:streamID];
    }
}

- (void)updateStreamToVisible:(NSString *)streamID inView:(UIView *)view {
    if (streamID.length) {
        [[ZegoManager api] setPlayVolume:100 ofStream:streamID];
    }
}

- (void)updateStreamToInvisible:(NSString *)streamID inView:(UIView *)view {
    if (streamID.length) {
        [[ZegoManager api] setPlayVolume:0 ofStream:streamID];
    }
    [self.playViewContainer sendSubviewToBack:view];
}

- (void)removePlayStatusImage:(UIView *)view {
    for (UIView *sub in view.subviews) {
        if ([sub isKindOfClass:[UIImageView class]]) {
            [sub removeFromSuperview];
        }
    }
}

- (void)addPlayStatusImage:(UIImage *)image inView:(UIView *)view {
    [self removePlayStatusImage:view];
    
    UIImageView *imageView = [[UIImageView alloc] init];
    imageView.image = image;
    [view addSubview:imageView];
    [imageView setTranslatesAutoresizingMaskIntoConstraints: NO];
    
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:view attribute:NSLayoutAttributeTop multiplier:1.0 constant:145];
    NSLayoutConstraint *centerX = [NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeCenterX relatedBy:NSLayoutRelationEqual toItem:view attribute:NSLayoutAttributeCenterX multiplier:1.0 constant:0];
    
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:184];
    NSLayoutConstraint *height = [NSLayoutConstraint constraintWithItem:imageView attribute:NSLayoutAttributeHeight relatedBy:NSLayoutRelationGreaterThanOrEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1.0 constant:209];
    
    NSArray *array = [NSArray arrayWithObjects:top, centerX, width, height, nil];
    [view addConstraints: array];
    
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

- (NSDictionary *)decodeJSONToDictionary:(NSString *)json
{
    if (json == nil)
        return nil;
    
    NSData *jsonData = [json dataUsingEncoding:NSUTF8StringEncoding];
    if (jsonData)
    {
        NSDictionary *dictionary = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:nil];
        return dictionary;
    }
    
    return nil;
}

- (NSString *)encodeDictionaryToJSON:(NSDictionary *)dict {
    if (dict == nil) {
        return nil;
    }
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:dict options:0 error:&error];
    
    if (jsonData) {
        NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        return jsonString;
    }
    
    return nil;
}

// 保持屏幕常亮
- (void)setIdelTimerDisable:(BOOL)disable
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:disable];
}

- (void)audioSessionWasInterrupted:(NSNotification *)notification
{
    NSLog(@"%s: %@", __func__, notification);
    if (AVAudioSessionInterruptionTypeBegan == [notification.userInfo[AVAudioSessionInterruptionTypeKey] intValue])
    {
        // 暂停音频设备
        [[ZegoManager api] pauseModule:ZEGOAPI_MODULE_AUDIO];
    }
    else if(AVAudioSessionInterruptionTypeEnded == [notification.userInfo[AVAudioSessionInterruptionTypeKey] intValue])
    {
        // 恢复音频设备
        [[ZegoManager api] resumeModule:ZEGOAPI_MODULE_AUDIO];
    }
}

// 响应系统音频路径变更通知
- (void)handleAudioRouteChanged:(NSNotification *)notification
{
    NSInteger reason = [[notification.userInfo objectForKey:AVAudioSessionRouteChangeReasonKey] integerValue];
    
    if (reason == AVAudioSessionRouteChangeReasonNewDeviceAvailable ||
        reason == AVAudioSessionRouteChangeReasonOldDeviceUnavailable ||
        reason == AVAudioSessionRouteChangeReasonOverride)
    {
        dispatch_async(dispatch_get_main_queue(), ^{
            [[ZegoSetting sharedInstance] checkHeadSet];
            
            [[NSNotificationCenter defaultCenter] postNotificationName:kHeadSetStateChangeNotification object:self];
        });
    }
}

- (void)updateQuality:(ZegoApiPlayQuality)quality {
    if (quality.quality  == 0) {
        [self.networkView setImage:[UIImage imageNamed:@"excellent"]];
        [self.networkButton setTitle:NSLocalizedString(@"网络优秀", nil) forState:UIControlStateNormal];
    } else if (quality.quality == 1) {
        [self.networkView setImage:[UIImage imageNamed:@"good"]];
        [self.networkButton setTitle:NSLocalizedString(@"网络流畅", nil) forState:UIControlStateNormal];
    } else if (quality.quality == 2) {
        [self.networkView setImage:[UIImage imageNamed:@"average"]];
        [self.networkButton setTitle:NSLocalizedString(@"网络缓慢", nil) forState:UIControlStateNormal];
    } else {
        [self.networkView setImage:[UIImage imageNamed:@"bad"]];
        [self.networkButton setTitle:NSLocalizedString(@"网络拥堵", nil) forState:UIControlStateNormal];
    }
    
    self.fpsLabel.text = [NSString stringWithFormat:@"%.2f", quality.fps];
    self.videoBitcodeLabel.text = [NSString stringWithFormat:@"%.2f kb/s", quality.kbps];
    self.audioBitcodeLabel.text = [NSString stringWithFormat:@"%.2f kb/s", quality.akbps];
    self.rttLabel.text = [NSString stringWithFormat:@"%d ms", quality.rtt];
    self.packageLossLabel.text = [NSString stringWithFormat:@"%.2f%%", quality.pktLostRate/256.0 * 100];
}

#pragma mark -- Log

- (void)addLog:(NSString *)logString {
    [self addLogString:logString];
    
#ifdef DEBUG
    NSLog(@"%@", logString);
#endif
}

- (void)addLogString:(NSString *)logString {
    if (logString.length != 0)
    {
        NSString *totalString = [NSString stringWithFormat:@"%@: %@", [self getCurrentTime], logString];
        [self.logArray insertObject:totalString atIndex:0];
        
        [[NSNotificationCenter defaultCenter] postNotificationName:@"logUpdateNotification" object:self userInfo:nil];
    }
}

- (NSString *)getCurrentTime {
    NSDateFormatter *formatter = [[NSDateFormatter alloc] init];
    formatter.dateFormat = @"[HH:mm:ss:SSS]";
    return [formatter stringFromDate:[NSDate date]];
}

- (void)onShowLogAlert {
    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:NSLocalizedString(@"选择操作类型", nil)
                                                                             message:nil
                                                                      preferredStyle:UIAlertControllerStyleActionSheet];
    
    // 临时写在这里，用于调试
    UIAlertAction *userID = [UIAlertAction actionWithTitle:[NSString stringWithFormat: NSLocalizedString(@"用户ID：%@", nil), [ZegoSetting sharedInstance].userID] style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
    }];
    userID.enabled = NO;
    [alertController addAction:userID];
    
    UIAlertAction *showLog = [UIAlertAction actionWithTitle:NSLocalizedString(@"查看日志", nil) style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [self onShowLogViewController];
    }];
    [alertController addAction:showLog];
    
    UIAlertAction *uploadLog = [UIAlertAction actionWithTitle:NSLocalizedString(@"上传日志", nil) style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [ZegoLiveRoomApi uploadLog];
        [self showAlert:nil title:NSLocalizedString(@"日志上传成功", nil)];
    }];
    [alertController addAction:uploadLog];
    
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    [alertController addAction:cancelAction];
    
    // 防止 ipad 上 crash
    alertController.popoverPresentationController.sourceView = self.view;
    alertController.popoverPresentationController.sourceRect = CGRectMake(CGRectGetMaxX(self.view.frame) - 40 , 64, 1.0, 1.0);

    [self presentViewController:alertController animated:YES completion:nil];
}

- (void)onShowLogViewController {
    ZegoLogTableViewController *logViewController = [[ZegoLogTableViewController alloc] init];
    logViewController.logArray = self.logArray;
    
    ZegoLogNavigationController *navigationController = [[ZegoLogNavigationController alloc] initWithRootViewController:logViewController];
    [self presentViewController:navigationController animated:YES completion:nil];
}

#pragma mark -- Timer

- (NSInteger)timestamp {
    NSDate *date = [NSDate date];
    NSTimeInterval interval = floor([date timeIntervalSince1970]);
    if (!interval) {
        return 0;
    }
    return interval;
}

- (void)startApplyTimer {
    [self.applyTimer invalidate];
    
    self.applyCountdown = [self timestamp];
    if (!self.applyTimer) {
        self.applyTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onApplyTimerAction:) userInfo:nil repeats:YES];
    }
    
    clientSeq ++;
    [self.applyTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 apply 定时器", nil)];
}

- (void)startCancelApplyTimer {
    [self.cancelApplyTimer invalidate];
    
    self.cancelApplyCountdown = [self timestamp];
    if (!self.cancelApplyTimer) {
        self.cancelApplyTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onCancelApplyTimerAction:) userInfo:nil repeats:YES];
    }
    
    [self.cancelApplyTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 cancel apply 定时器", nil)];
}

- (void)startReadyTimer {
    [self.readyTimer invalidate];
    
    self.readyCountdown = [self timestamp];
    
    if (!self.readyTimer) {
        self.readyTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(onReadyTimerAction:) userInfo:nil repeats:YES];
    }

    [self.readyTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 ready 定时器", nil)];
}

- (void)startPlayTimer {
    [self.playTimer invalidate];
    
    self.playCountdown = [self timestamp];
    if (!self.playTimer) {
        self.playTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(onPlayTimerAction:) userInfo:nil repeats:YES];
    }
    [self.playTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 play 定时器，开始游戏", nil)];
}

- (void)startConfirmTimer {
    [self.confirmTimer invalidate];
    
    self.confirmCountdown = [self timestamp];
    if (!self.confirmTimer) {
        self.confirmTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onConfirmTimerAction:) userInfo:nil repeats:YES];
    }
    [self.confirmTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 confirm 定时器，发送用户确认游戏与否信息", nil)];
}

- (void)startResultTimer {
    [self.resultTimer invalidate];
    
    self.resultCountdown = [self timestamp];
    if (!self.resultTimer) {
        self.resultTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onResultTimerAction:) userInfo:nil repeats:YES];
    }

    [self.resultTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 result 定时器，等待游戏结果", nil)];
}

//- (void)stopTimer:(NSTimer *)timer {
//    [timer invalidate];
//    timer = nil;
//}

- (void)onApplyTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    NSString *applyCommand = [self.command apply:clientSeq];
    self.applySeq = clientSeq;
    
    if (![[self.receivedReplyCounts objectForKey:applyKey] integerValue]) {
        if (current - self.applyCountdown >= RETRY_DURATION) {
            [self addLog:@"停止 apply 定时器"];
            [self.applyTimer invalidate];
            self.applyTimer = nil;
            
            self.replyTimeout[applyKey] = @1;
            self.state = ZegoClientStateInitial;
            
            NSLog(@"current state-apply：%ld", (long)self.state);
            
            // 发送预约指令超过重试次数，弹框提示，恢复预约按钮可操作状态
            [self showAlert:NSLocalizedString(@"预约超时，请稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
        } else {
            NSLog(@"serverUser: %@", self.serverUser.userId);
            BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:applyCommand completion:^(int errorCode, NSString *roomID) {
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_APPLY 发送结果：%d(0成功，1失败)，第 %ld 次发送", errorCode, (current - self.applyCountdown) / 2 + 1]);
            }];
            
            NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_APPLY 调用结果：%d(1成功，0失败)，第 %ld 次发送", invokeSuccess, (current - self.applyCountdown) / 2 + 1]);
        }
    } else {
        [self addLog: NSLocalizedString(@"预约后收到预约确认 reply，停止发送预约命令，等待上机", nil)];
        [self addLog:@"停止 apply 定时器"];
        [self.applyTimer invalidate];
        self.applyTimer = nil;
    }
}

- (void)onCancelApplyTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    NSString *cancelApplyCommand = [self.command cancelApply:clientSeq];
    self.cancelApplySeq = clientSeq;
    
    if (![self.receivedReplyCounts[cancelApplyKey] integerValue]) {
        if (current - self.cancelApplyCountdown >= RETRY_DURATION) {
            [self.cancelApplyTimer invalidate];
            self.cancelApplyTimer = nil;
            
            self.replyTimeout[cancelApplyKey] = @1;
            
            NSLog(@"current state-cancel apply: %ld", (long)self.state);
            if (self.state == ZegoClientStateApplyCancelling) {
                [self showAlert:NSLocalizedString(@"取消预约超时，请稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
            }
            
            self.prepareButton.enabled = YES;

            [self addLog: NSLocalizedString(@"取消预约后等待确认超时", nil)];
        } else {
            BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:cancelApplyCommand completion:^(int errorCode, NSString *roomID) {
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_CANCEL_APPLY 发送结果：%d(0成功，1失败)，第 %ld 次发送", errorCode, (current - self.cancelApplyCountdown) / 2 + 1]);
            }];
            
            NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_CANCEL_APPLY 调用结果：%d(1成功，0失败)，第 %ld 次发送", invokeSuccess, (current - self.cancelApplyCountdown) / 2 + 1]);
        }
    } else {
        [self addLog: NSLocalizedString(@"取消预约后收到确认 reply，停止发送取消预约命令，恢复初始状态", nil)];
        
        self.state = ZegoClientStateInitial;
        [self.cancelApplyTimer invalidate];
        self.cancelApplyTimer = nil;
    }
}

- (void)onReadyTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    if (current - self.readyCountdown >= RETRY_DURATION) {
        // 停止并释放定时器
        [self.readyTimer invalidate];
        self.readyTimer = nil;
        
        // 倒计时结束，没有点击任何按钮，默认为不上机
        self.confirm = 0;
        self.receivedReplyCounts[resultKey] = 0;
        
        [self.readyView removeFromSuperview];
         
        NSLog(@"current timer: %@", self.readyTimer);
        NSLog(@"current state-ready: %ld", (long)self.state);
        [self addLog: NSLocalizedString(@"等待用户确认上机计时结束，用户未做任何操作", nil)];
        
        self.state = ZegoClientStateInitial;
    } else {
        self.readyView.startButtonTitle = [NSString stringWithFormat:NSLocalizedString(@"开始游戏(%ds)", nil), RETRY_DURATION - (current - self.readyCountdown)];
    }
}

- (void)onConfirmTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    NSString *confirm = [self.command gameConfirm:self.confirm clientSeq:clientSeq];
    self.confirmSeq = clientSeq;
    
    if ([self.receivedReplyCounts[confirmKey] integerValue]) {
        if (self.confirm) {
            // 收到回复且上机成功，才能进行操作
            if (self.isOperating) {
                [self.confirmTimer invalidate];
                self.confirmTimer = nil;
            
                // 开始上机，更新界面
                [self addLog: NSLocalizedString(@"用户开始上机，更新界面为可操作", nil)];
                
                if (self.loginRoomSucceed) {
                    self.state = ZegoClientStateGamePlaying;
                    
                    [self setControlViewVisible:YES];
                    
                    // 确认上机，启动游戏计时器
                    [self startPlayTimer];
                }
            }
        } else {
            [self.confirmTimer invalidate];
            self.confirmTimer = nil;
            
            self.state = ZegoClientStateInitial;
            [self addLog: NSLocalizedString(@"用户取消上机，恢复预约状态", nil)];
        }
    }
  
    if (current - self.confirmCountdown >= RETRY_DURATION) {
        // 超时，停止并释放定时器
        [self.confirmTimer invalidate];
        self.confirmTimer = nil;
        
        self.state = ZegoClientStateInitial;
        self.replyTimeout[confirmKey] = @1;
        
        // 倒计时结束，弹框提示
        NSLog(@"current state-confirm: %ld", (long)self.state);
        [self showAlert:NSLocalizedString(@"用户上机超时，或取消上机超时，请稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
    } else {
        if (![self.receivedReplyCounts[confirmKey] integerValue]) {
            BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:confirm completion:^(int errorCode, NSString *roomID) {
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_GAME_CONFIRM 发送结果：%d(0成功，1失败)，第 %ld 次发送", errorCode, (current - self.confirmCountdown) / 2 + 1]);
            }];
            
            NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_GAME_CONFIRM 调用结果：%d(1成功，0失败)，第 %ld 次发送", invokeSuccess, (current - self.confirmCountdown) / 2 + 1]);
        } else {
            [self addLog: NSLocalizedString(@"客户端收到用户上机与否 reply，停止发送用户上机与否信息", nil)];
        }
    }
}

- (void)onPlayTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    if (current - self.playCountdown > PLAY_DURATION) {
        // 停止并释放定时器
        [self.playTimer invalidate];
        self.playTimer = nil;
        //        [self stopTimer:self.playTimer];
        
        NSLog(@"current state-play: %ld", (long)self.state);
        [self addLog: NSLocalizedString(@"游戏倒计时结束，自动抓娃娃", nil)];
        
        // 倒计时结束，自动发送抓娃娃指令
        [self startGrab:nil];
    } else {
        self.countdownLabel.text = [NSString stringWithFormat:@"%lds", PLAY_DURATION - (current - self.playCountdown)];
    }
}

- (void)onResultTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    if (![self.receivedReplyCounts[resultKey] integerValue]) {
        if (current - self.resultCountdown > RESULT_DURATION) {
            // 停止并释放定时器
            [self.resultTimer invalidate];
            self.resultTimer = nil;
            
            if (self.isOperating) {
                [self offBoard];
                self.isOperating = NO;
            }
            
            [self addLog: NSLocalizedString(@"用户获取结果超时，自动下机", nil)];
            
            // 倒计时结束，没有收到结果
            NSLog(@"current state-result: %ld", (long)self.state);
            [self showAlert:NSLocalizedString(@"获取游戏结果超时", nil) title:NSLocalizedString(@"提示", nil)];

            
            [self setPrepareButtonVisible:YES];
            self.state = ZegoClientStateInitial;
        }
    }
}

#pragma mark -- View Change

- (void)setPrepareButtonVisible:(BOOL)visible {
    if (visible) {
        self.prepareButton.hidden = NO;
        self.prepareButton.enabled = YES;
        
        self.controlView.hidden = YES;
//        [self.view bringSubviewToFront:self.prepareButton];
    } else {
        self.prepareButton.hidden = YES;
    }
}

- (void)setControlViewVisible:(BOOL)visible {
    if (visible) {
        self.prepareButton.hidden = YES;
        
        self.controlView.hidden = NO;
        
        self.forwardButton.enabled = YES;
        self.backwardButton.enabled = YES;
        self.leftwardButton.enabled = YES;
        self.rightwardButton.enabled = YES;
        self.grabButton.enabled = YES;
        
//        [self.view bringSubviewToFront:self.controlView];
    } else {
        self.controlView.hidden = YES;
    }
}

#pragma mark -- Prepare button

- (void)updatePrepareButtonToApplyStatus:(NSString *)text isCancel:(BOOL)cancel {
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:text];
    [self.prepareButton setAttributedTitle:attributedString forState:UIControlStateNormal];
    [self.prepareButton setTitle:nil forState:UIControlStateNormal];
    
    if (cancel) {
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:17.0] range:NSMakeRange(0, 5)];
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:11.0] range:NSMakeRange(5, 6)];    // FIXME: 动态获取长度

        [self.prepareButton setBackgroundImage:[UIImage imageNamed:@"cancel"] forState:UIControlStateNormal];
    } else {
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:17.0] range:NSMakeRange(0, 5)];
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:11.0] range:NSMakeRange(5, 9)];    // FIXME: 动态获取长度
  
        [self.prepareButton setBackgroundImage:[UIImage imageNamed:@"book"] forState:UIControlStateNormal];
    }
}

- (void)updatePrepareButtonToStartStatus:(NSString *)text {
    [self.prepareButton setAttributedTitle:nil forState:UIControlStateNormal];
    [self.prepareButton setTitle:text forState:UIControlStateNormal];
    [self.prepareButton setBackgroundImage:[UIImage imageNamed:@"start"] forState:UIControlStateNormal];
}

- (void)onBoard {
    [ZegoLiveRoomApi setConfig:@"prefer_play_ultra_source=1"];
    [self switchStream];
}

- (void)offBoard {
    [ZegoLiveRoomApi setConfig:@"prefer_play_ultra_source=0"];
    [self switchStream];
}

- (void)switchStream {
    // 停止播放所有的流
    if (self.firstStreamID.length) {
        [[ZegoManager api] stopPlayingStream:self.firstStreamID];
        [self.firstPlayView setBackgroundColor:[UIColor whiteColor]];
    }
    
    if (self.secondStreamID.length) {
        [[ZegoManager api] stopPlayingStream:self.secondStreamID];
        [self.secondPlayView setBackgroundColor:[UIColor whiteColor]];
    }
    
    if (self.currentVisibleStreamIndex == 1) {
        [self playVisibleStream:self.firstStreamID inView:self.firstPlayView];
        [self playInvisibleStream:self.secondStreamID inView:self.secondPlayView];
    } else {
        [self playVisibleStream:self.secondStreamID inView:self.secondPlayView];
        [self playInvisibleStream:self.firstStreamID inView:self.firstPlayView];
    }
}

#pragma mark - Event response

- (IBAction)onNetworkButton:(id)sender {
    self.networkQualityView.hidden = NO;
}

- (void)dismissNetworkView  {
    self.networkQualityView.hidden = YES;
}

- (IBAction)onLog:(id)sender {
    [self onShowLogAlert];
}

- (IBAction)onSwitchPlayView:(id)sender {
    if (self.loginRoomSucceed) {
        if (self.currentVisibleStreamIndex == 1) {    // 切换到第二条流画面
            [self addLog: NSLocalizedString(@"用户切换到第二条流画面", nil)];
            
            self.currentVisibleStreamIndex = 2;
            
            if (!self.secondStreamID.length) {
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-2"] inView:self.secondPlayView];
            } else {
                [self updateStreamToInvisible:self.firstStreamID inView:self.firstPlayView];
                
                switch (self.secondStreamStatus) {
                    case ZegoStreamStatusStartPlaying:
                    {
                        [self addPlayStatusImage:[UIImage imageNamed:@"Loading-2"] inView:self.secondPlayView];
                        break;
                    }
                    case ZegoStreamStatusPlaySucceed:
                    {
                        [self removePlayStatusImage:self.secondPlayView];
                        [self updateStreamToVisible:self.secondStreamID inView:self.secondPlayView];
                        break;
                    }
                    case ZegoStreamStatusPlaySucceedEmpty:
                    {
                        [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-2"] inView:self.secondPlayView];
                        break;
                    }
                    case ZegoStreamStatusPlayFail:
                    {
                        [self addPlayStatusImage:[UIImage imageNamed:@"PlayFailed-2"] inView:self.secondPlayView];
                        break;
                    }
                    default:
                        break;
                }
            }
        } else if (self.currentVisibleStreamIndex == 2) {    // 切换到第一条流画面
            [self addLog: NSLocalizedString(@"用户切换到第一条流画面", nil)];
            
            self.currentVisibleStreamIndex = 1;
            
            if (!self.firstStreamID.length) {
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-1"] inView:self.firstPlayView];
            } else {
                [self updateStreamToInvisible:self.secondStreamID inView:self.secondPlayView];
                
                switch (self.firstStreamStatus) {
                    case ZegoStreamStatusStartPlaying:
                    {
                        [self addPlayStatusImage:[UIImage imageNamed:@"Loading-1"] inView:self.firstPlayView];
                        break;
                    }
                    case ZegoStreamStatusPlaySucceed:
                    {
                        [self removePlayStatusImage:self.firstPlayView];
                        [self updateStreamToVisible:self.firstStreamID inView:self.firstPlayView];
                        break;
                    }
                    case ZegoStreamStatusPlaySucceedEmpty:
                    {
                        [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-1"] inView:self.firstPlayView];
                        break;
                    }
                    case ZegoStreamStatusPlayFail:
                    {
                        [self addPlayStatusImage:[UIImage imageNamed:@"PlayFailed-1"] inView:self.firstPlayView];
                        break;
                    }
                    default:
                        break;
                }
            }
        }
    }
}

- (IBAction)onClose:(id)sender {
    [[ZegoManager api] stopPlayingStream:self.firstStreamID];
    [[ZegoManager api] stopPlayingStream:self.secondStreamID];

    if (self.loginRoomSucceed) {
        [[ZegoManager api] logoutRoom];
    }
    
    [self setIdelTimerDisable:YES];
    
    [self addLog:@"停止 apply 定时器"];
    [self.applyTimer invalidate];
    self.applyTimer = nil;
    
    [self.cancelApplyTimer invalidate];
    self.cancelApplyTimer = nil;
    
    [self.readyTimer invalidate];
    self.readyTimer = nil;
    
    [self.playTimer invalidate];
    self.playTimer = nil;
    
    [self.resultTimer invalidate];
    self.resultTimer = nil;
    
    [self.navigationController popViewControllerAnimated:YES];
}

// 预约
- (IBAction)onApply:(UIButton *)sender {
    if ([self.prepareButton.currentAttributedTitle.string hasPrefix:NSLocalizedString(@"取消预约", nil)]) {
        self.prepareButton.enabled = NO;
        
        self.state = ZegoClientStateApplyCancelling;
        self.receivedReplyCounts[cancelApplyKey] = @0;
        self.replyTimeout[cancelApplyKey] = @0;
        
        clientSeq ++;
        [self startCancelApplyTimer];
        return;
    }
    
    self.replyTimeout[applyKey] = @0;
    self.receivedReplyCounts[applyKey] = @0;
    
    self.prepareButton.enabled = NO;
    
    if (self.serverUser) {
        [self startApplyTimer];
        self.state = ZegoClientStateApplying;
    }
}

- (IBAction)goForward:(id)sender {
    if (self.isOperating && self.serverUser) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向前", nil)];
        
        NSString *goForwardCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            clientSeq ++;
            goForwardCommand = [self.command moveLeft:clientSeq];
        } else {
            clientSeq ++;
            goForwardCommand = [self.command moveBackward:clientSeq];
        }
        
        BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:goForwardCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_FORWARD 发送结果：%d（0成功，1失败）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_FORWARD 调用结果：%d（1成功，0失败）", nil), invokeSuccess]];
    }
}

- (IBAction)goBackward:(id)sender {
    if (self.isOperating && self.serverUser) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向后", nil)];
        
        NSString *goBackwardCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            clientSeq ++;
            goBackwardCommand = [self.command moveRight:clientSeq];
        } else {
            clientSeq ++;
            goBackwardCommand = [self.command moveForward:clientSeq];
        }
        BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:goBackwardCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_BACKWARD 发送结果：%d（0成功，1失败）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_BACKWARD 调用结果：%d（1成功，0失败）", nil), invokeSuccess]];
    }
}

- (IBAction)goLeftward:(id)sender {
    if (self.isOperating && self.serverUser) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向左", nil)];
        
        NSString *goLeftCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            clientSeq ++;
            goLeftCommand = [self.command moveForward:clientSeq];
        } else {
            clientSeq ++;
            goLeftCommand = [self.command moveLeft:clientSeq];
        }
        BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:goLeftCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_LEFT 发送结果：%d（0成功，1失败）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_LEFT 调用结果：%d（1成功，0失败）", nil), invokeSuccess]];
    }
}

- (IBAction)goRightward:(id)sender {
    if (self.isOperating && self.serverUser) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向右", nil)];
        
        NSString *goRightCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            clientSeq ++;
            goRightCommand = [self.command moveBackward:clientSeq];
        } else {
            clientSeq ++;
            goRightCommand = [self.command moveRight:clientSeq];
        }
        BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:goRightCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_RIGHT 发送结果：%d（0成功，1失败）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_RIGHT 调用结果：%d（1成功，0失败）", nil), invokeSuccess]];
    }
}

- (IBAction)startGrab:(id)sender {
    self.receivedReplyCounts[resultKey] = @0;
    
    clientSeq ++;
    NSString *startGrabCommand = [self.command moveDown:clientSeq];

    if (self.isOperating && self.serverUser) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：抓娃娃", nil)];
        [self.playTimer invalidate];
        self.playTimer = nil;

        BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:startGrabCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_DOWN 发送结果：%d（0成功，1失败）", nil), errorCode]];
            if (errorCode == 0) {
                if (![self.receivedReplyCounts[resultKey] integerValue]) {
                    [self startResultTimer];
                }
                
                self.state = ZegoClientStateResultWaiting;
                
//              [self setButtonStatus:self.grabButton enable:NO];
                self.forwardButton.enabled = NO;
                self.backwardButton.enabled = NO;
                self.leftwardButton.enabled = NO;
                self.rightwardButton.enabled = NO;
                
                self.countdownLabel.text = @"";
            }
        }];
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_DOWN 调用结果：%d（1成功，0失败）", nil), invokeSuccess]];
    }
}

#pragma mark - ZegoLivePlayerDelegate

- (void)onPlayStateUpdate:(int)stateCode streamID:(NSString *)streamID {
    if (stateCode == 0) {
        [self addLog: [NSString stringWithFormat:NSLocalizedString(@"拉流成功，流 ID：%@", nil), streamID]];
        
        if ([streamID isEqualToString:self.firstStreamID]) {
            [self removePlayStatusImage:self.firstPlayView];
            self.firstStreamStatus = ZegoStreamStatusPlaySucceed;
        } else {
            [self removePlayStatusImage:self.secondPlayView];
            self.secondStreamStatus = ZegoStreamStatusPlaySucceed;
        }
    } else {
        [self addLog: [NSString stringWithFormat:NSLocalizedString(@"拉流失败，流 ID：%@，错误码：%d", nil), streamID, stateCode]];
        
        if ([streamID isEqualToString:self.firstStreamID]) {
            self.firstStreamStatus = ZegoStreamStatusPlayFail;
            [self addPlayStatusImage:[UIImage imageNamed:@"PlayFailed-1"] inView:self.firstPlayView];
        } else {
            self.secondStreamStatus = ZegoStreamStatusPlayFail;
            [self addPlayStatusImage:[UIImage imageNamed:@"PlayFailed-2"] inView:self.firstPlayView];
        }
    }
}

- (void)onPlayQualityUpate:(NSString *)streamID quality:(ZegoApiPlayQuality)quality {
//    NSLog(@"current video kbps: %f, streamID: %@, quality: %d", quality.kbps, streamID, quality.quality);
    
    if (self.currentVisibleStreamIndex == 1 && [streamID isEqualToString:self.firstStreamID]) {
        [self updateQuality:quality];
    }
    
    if (self.currentVisibleStreamIndex == 2 && [streamID isEqualToString:self.secondStreamID]) {
        [self updateQuality:quality];
    }
    
    if (quality.kbps < 0.00001) {
        // 播放成功，但流数据为空（CDN 支持拉空流）
        [self addLog: [NSString stringWithFormat:NSLocalizedString(@"没有流数据，请确认已成功推流，流 ID：%@", nil), streamID]];

        if ([streamID isEqualToString:self.firstStreamID]) {
            self.firstStreamStatus = ZegoStreamStatusPlaySucceedEmpty;
            
            if (self.currentVisibleStreamIndex == 1) {
                [[ZegoManager api] updatePlayView:nil ofStream:self.firstStreamID];
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-1"] inView:self.firstPlayView];
            }
        } else {
            self.secondStreamStatus = ZegoStreamStatusPlaySucceedEmpty;
            
            if (self.currentVisibleStreamIndex == 2) {
                [[ZegoManager api] updatePlayView:nil ofStream:self.secondStreamID];
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-2"] inView:self.secondPlayView];
            }
        }
    } else {
        // 播放成功，流数据更新为非空
//        [self addLog: [NSString stringWithFormat:NSLocalizedString(@"获取到流数据，流 ID：%@", nil), streamID]];
        
        if ([streamID isEqualToString:self.firstStreamID]) {
            if (self.firstStreamStatus == ZegoStreamStatusPlaySucceedEmpty) {   // 状态由 succeedEmpty 转换到 succeed
                [[ZegoManager api] updatePlayView:self.firstPlayView ofStream:self.firstStreamID];
                
                if (self.currentVisibleStreamIndex == 1) {
                    [self removePlayStatusImage:self.firstPlayView];
                    [self updateStreamToVisible:self.firstStreamID inView:self.firstPlayView];
                }
                
                self.firstStreamStatus = ZegoStreamStatusPlaySucceed;
            }
        } else {
            if (self.secondStreamStatus == ZegoStreamStatusPlaySucceedEmpty) {
                [[ZegoManager api] updatePlayView:self.secondPlayView ofStream:self.secondStreamID];
                
                if (self.currentVisibleStreamIndex == 2) {
                    [self removePlayStatusImage:self.secondPlayView];
                    [self updateStreamToVisible:self.secondStreamID inView:self.secondPlayView];
                }
                
                self.secondStreamStatus = ZegoStreamStatusPlaySucceed;
            }
        }
    }
}

#pragma mark - ZegoRoomDelegate

- (void)onStreamUpdated:(int)type streams:(NSArray<ZegoStream *> *)streamList roomID:(NSString *)roomID {
    for (ZegoStream *stream in streamList) {
        if ([stream.streamID hasPrefix:@"WWJ"]) {
            if ([stream.streamID hasSuffix:@"_2"]) {
                self.secondStreamID = stream.streamID;
                if (self.currentVisibleStreamIndex == 2) {
                    [self playVisibleStream:self.secondStreamID inView:self.secondPlayView];
                } else {
                    [self playInvisibleStream:self.secondStreamID inView:self.secondPlayView];
                }
            } else {
                self.firstStreamID = stream.streamID;
                if (self.currentVisibleStreamIndex == 1) {
                    [self playVisibleStream:self.firstStreamID inView:self.secondPlayView];
                } else {
                    [self playInvisibleStream:self.firstStreamID inView:self.secondPlayView];
                }
            }
        }
    }
}

- (void)onStreamExtraInfoUpdated:(NSArray<ZegoStream *> *)streamList roomID:(NSString *)roomID {
//    if ([roomID isEqualToString:self.roomID]) {
//        for (ZegoStream *stream in streamList) {
//            if (stream.extraInfo.length) {
//                NSDictionary *dict = [self decodeJSONToDictionary:stream.extraInfo];
//                if (dict) {
//                    // 获取当前房间排队人数
//                    NSInteger queueCount = [dict[@"queue_number"] integerValue];
//                    self.queueCount = queueCount;
//
//                    // 获取当前房间总人数
//                    self.totalCount =  [dict[@"total"] integerValue];
//                    self.countLabel.text = [NSString stringWithFormat: NSLocalizedString(@"%d人在房间", nil), self.totalCount];
//
//                    // 获取当前正在游戏的人
//                    NSDictionary *player = dict[@"player"];
//                    NSString *playerId = player[@"id"];
//
//                    [self addLog:[NSString stringWithFormat:NSLocalizedString(@"房间 %@ 内排队人数为：%d，总人数为：%d", nil), self.roomID, self.queueCount, self.totalCount]];
//
//                    if (self.state != ZegoClientStateApplyCancelling) {
//                        if (!queueCount && !playerId.length) {
//                            [self updatePrepareButtonToStartStatus:NSLocalizedString(@"开始游戏", nil)];
//                        } else {
//                            [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"预约抓娃娃\n当前排队 %d 人", nil), queueCount] isCancel:NO];
//                        }
//                    }
//
//                }
//            }
//        }
//    }
}

- (void)onReceiveCustomCommand:(NSString *)fromUserID userName:(NSString *)fromUserName content:(NSString *)content roomID:(NSString *)roomID {
    // 先校验房间
    if (roomID && self.roomID && [roomID isEqualToString:self.roomID]) {
        NSDictionary *dict = [self decodeJSONToDictionary:content];
        NSInteger command = [dict[@"cmd"] integerValue];
        NSDictionary *data = dict[@"data"];
        
        // server 回复收到预约申请，并告知预约结果（Server-->Client）
        if (command == CMD_APPLY_REPLY) {
            [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_APPLY_REPLY, %@", content]];
            
            if (self.state == ZegoClientStateApplying && ![self.replyTimeout[applyKey] intValue]) {
                NSInteger result = [data[@"result"] integerValue];
                NSDictionary *player = data[@"player"];
                NSString *userId = player[@"id"];
                NSInteger seq = [data[@"seq"] integerValue];
                NSInteger index = [data[@"index"] integerValue];
            
                // 去重后台回复的多条同一 seq 的 apply reply
                NSInteger repeat = [[self.receivedReplyCounts objectForKey:applyKey] integerValue];
                
                if (seq == self.applySeq && !repeat) {
                    if ([userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                        [self.receivedReplyCounts setObject:[NSNumber numberWithInteger:1] forKey:applyKey];
                        
                        if (result == 0) {
                            [self addLog: @"客户端收到预约申请 reply，预约成功"];
                            
                            self.state = ZegoClientStateGameWaiting;
                            
                            [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"取消预约\n前面 %d 人", nil), index - 1] isCancel:YES];
                            self.prepareButton.enabled = YES;
                        } else {
                            [self addLog: @"客户端收到预约申请 reply，预约失败"];
                            
                            if (self.state == ZegoClientStateApplying) {
                                [self showAlert:NSLocalizedString(@"预约失败，请稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
                            }
                            
                            self.state = ZegoClientStateInitial;
                        }
                    }
                }
            }
        }
        
        if (command == CMD_CANCEL_APPLY_REPLY) {
            [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_CANCEL_APPLY_REPLY, %@", content]];
            
            if (self.state == ZegoClientStateGameWaiting && ![self.replyTimeout[cancelApplyKey] intValue]) {
                NSInteger seq = [data[@"data"] integerValue];;
                
                // 去重后台回复的多条同一 seq 的 apply reply
                NSInteger repeat = [[self.receivedReplyCounts objectForKey:cancelApplyKey] integerValue];
                
                if (seq == self.cancelApplySeq && !repeat) {
                    self.receivedReplyCounts[cancelApplyKey] = @1;
                }
            }
        }
        
        // 通知某人准备上机, 此时用户可使用 CMD_ABANDON_PLAY 放弃游戏（Server-->Client）
        if (command == CMD_GAME_READY) {
            [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_GAME_READY, %@", content]];
            
            // ready 先于 apply reply 收到
            if (self.state == ZegoClientStateApplying) {
                self.state = ZegoClientStateGameWaiting;
                
                // 不再发送 apply
                [self addLog:@"停止 apply 定时器"];
                [self.applyTimer invalidate];
                self.applyTimer = nil;
            }
            
            if (![self.replyTimeout[applyKey] intValue] && (self.state == ZegoClientStateGameWaiting || self.state == ZegoClientStateApplyCancelling)) {
                [self addLog: @"客户端收到允许上机，状态吻合"];
                
                NSInteger seq = [dict[@"seq"] intValue];
                NSDictionary *player = data[@"player"];
                NSString *userId = player[@"id"];
                
                // 过滤同一个 seq 的通知
                if (self.gameReadySeq != seq) {
                    self.gameReadySeq = (int)seq;
                    
                    if ([userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                        
                        // 向服务回复收到 CMD_GAME_READY_REPLY 命令，否则服务器会一直重试发送
                        clientSeq ++;
                        NSString *gameReadyReplyCommand = [self.command gameReadyReply:clientSeq serverSeq:self.gameReadySeq];
                        if (self.serverUser) {
                            BOOL invokeSuccess = [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:gameReadyReplyCommand completion:^(int errorCode, NSString *roomID) {
                                [self addLog: [NSString stringWithFormat:NSLocalizedString(@"[COMMAND] CMD_APPLY_REPLY 发送结果: %d（0成功）", nil), errorCode]];
                            }];
                            
                            [self addLog: [NSString stringWithFormat:NSLocalizedString(@"[COMMAND] CMD_APPLY_REPLY 调用结果: %d（1成功）", nil), invokeSuccess]];
                        }
                        
                        // 弹出是否继续游戏 view
                        self.readyView = [[[NSBundle mainBundle] loadNibNamed:@"ZegoReadyView" owner:nil options:nil] firstObject];
                        self.readyView.delegate = self;
                        self.readyView.frame = self.view.frame;
                        [self.view addSubview:self.readyView];

                        // 上机弹框出现，启动上机确认计时器
                        [self startReadyTimer];
                    }
                }
            }
        }
        
        // 回复收到确认上机或者放弃玩游戏指令（Server-->Client）
        if (command == CMD_CONFIRM_REPLY) {
            [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_CONFIRM_REPLY, %@", content]];
            
            NSInteger seq = [data[@"seq"] intValue];

            if (self.state == ZegoClientStateGameConfirming && ![self.replyTimeout[confirmKey] intValue]) {
                // 去重后台回复的多条同一 seq 的 apply reply
                NSInteger repeat = [[self.receivedReplyCounts objectForKey:confirmKey] integerValue];
                
                if (seq == self.confirmSeq && !repeat) {
                    [self addLog: NSLocalizedString(@"客户端收到确认上机与否 reply，状态吻合", nil)];
                    self.receivedReplyCounts[confirmKey] = @1;
                }
            }
        }
    
        // 全员广播房间信息（总人数，排队列表、当前游戏者）更新（Server-->Client）
        if (command == CMD_USER_UPDATE) {
            [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_USER_UPDATE, %@", content]];
            
            // 当前正在玩游戏的人
            NSDictionary *player = data[@"player"];
            if (player.count) {
                NSString *playerId = player[@"id"];
                self.currentPlayer = playerId;
            } else {
                self.currentPlayer = nil;
            }
            
            // 房间排队列表
            NSArray *queueList = data[@"queue"];
            self.queueCount = queueList.count;
            
            self.totalCount = [data[@"total"] integerValue];
            self.countLabel.text = [NSString stringWithFormat: NSLocalizedString(@"%d人在房间", nil), self.totalCount];

            // 如果用户在取消预约状态，收到用户更新中，含有本人，说明取消预约失败
            int exist = 0;
            int currentIndex = 0;
            
            if (queueList.count) {
                for (int i = 0; i < queueList.count; i++) {
                    NSString *userId = [queueList[i] objectForKey:@"id"];
                    if (userId && [userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                        currentIndex = i;
                        exist ++;
                    }
                }
            }
            
            // 取消预约已超时
            if (self.state == ZegoClientStateApplyCancelling && [self.replyTimeout[cancelApplyKey] intValue]) {
                if (exist) {
                    [self addLog: NSLocalizedString(@"取消预约超时后，排队列表中仍有当前用户，取消预约失败", nil)];
                    self.state = ZegoClientStateGameWaiting;
                } else {
                    [self addLog: NSLocalizedString(@"取消预约超时后，排队列表中没有当前用户，取消预约成功", nil)];
                    self.state = ZegoClientStateInitial;
                }
            }
            
            if (self.state == ZegoClientStateGameWaiting) {
                [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"取消预约\n前面 %d 人", nil), currentIndex] isCancel:YES];
            }
            
            if (self.state == ZegoClientStateInitial) {
                if (self.queueCount || (self.queueCount == 0 && self.currentPlayer.length && ![self.currentPlayer isEqualToString:[ZegoSetting sharedInstance].userID])) {
                    [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"预约抓娃娃\n当前排队 %d 人", nil), self.queueCount] isCancel:NO];
                } else {
                    [self updatePrepareButtonToStartStatus:NSLocalizedString(@"开始游戏", nil)];
                }
                self.prepareButton.enabled = YES;
                
            }
        }
        
        // 收到游戏结果
        if (command == CMD_GAME_RESULT) {
            [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_GAME_RESULT, %@", content]];
            
            if (self.state == ZegoClientStateResultWaiting) {
                [self addLog: NSLocalizedString(@"客户端收到游戏结果，状态吻合", nil)];
                
                int seq = [dict[@"seq"] intValue];;
                NSDictionary *player = data[@"player"];
                NSString *userId = player[@"id"];
                NSInteger result = [data[@"result"] integerValue];
                
                if (self.gameResultSeq!= seq) {
                    self.gameResultSeq = seq;
                    
                    if ([userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                        self.receivedReplyCounts[resultKey] = @1;
                        
                        [self.resultTimer invalidate];
                        self.resultTimer = nil;
                        
                        // 向服务器回复收到 CMD_GAME_RESULT 命令
                        clientSeq ++;
                        NSString *gameResultReplyCommand = [self.command resultReply:clientSeq serverSeq:self.gameResultSeq];
                        if (self.serverUser) {
                            BOOL invokeSuccess =  [[ZegoManager api] sendCustomCommand:@[self.serverUser] content:gameResultReplyCommand completion:^(int errorCode, NSString *roomID) {
                                [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] GAME_RESULT_REPLY 发送结果：%d（0成功，1失败）", nil), errorCode]];
                            }];
                            
                            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] GAME_RESULT_REPLY 调用结果：%d（1成功，0失败）", nil), invokeSuccess]];
                        }
                        
                        ZegoResultView *resultView = [[[NSBundle mainBundle] loadNibNamed:@"ZegoResultView" owner:nil options:nil] firstObject];
                        resultView.delegate = self;
                        resultView.frame = self.view.frame;
//                        resultView.frame = CGRectMake(0, -self.view.frame.size.height, self.view.frame.size.width, self.view.frame.size.height);

                        if (result == 1) {
                            resultView.imageName = @"success";
                        } else {
                            resultView.imageName = @"failure";
                        }
                        
                        [self.view addSubview:resultView];
//                        [UIView animateWithDuration:1.0 delay:0 usingSpringWithDamping:0.7 initialSpringVelocity:25 options:UIViewAnimationOptionCurveEaseInOut animations:^{
//                            resultView.frame = CGRectMake(0, 0, self.view.frame.size.width, self.view.frame.size.height);
//                        } completion:nil];
                        
                        if (self.isOperating) {
                            [self offBoard];
                            self.isOperating = NO;
                        }
                        
                        [self addLog: NSLocalizedString(@"用户获取到结果，自动下机", nil)];
                    }
                }
            }
        }
    }
}

#pragma mark - ZegoResultViewDelegate

- (void)resultButtonClicked:(UIButton *)button {
    [self setPrepareButtonVisible:YES];
    self.state = ZegoClientStateInitial;
}

#pragma mark - ZegoReadyViewDelegate

- (void)onClickCancelButton:(id)sender {
    self.confirm = 0;
    
    self.prepareButton.enabled = NO;
    
    self.receivedReplyCounts[confirmKey] = @0;
    self.replyTimeout[confirmKey] = @0;
    [self addLog: NSLocalizedString(@"用户选择了取消上机", nil)];
    self.state = ZegoClientStateGameConfirming;
    
    [self.readyTimer invalidate];
    self.readyTimer = nil;
    
    clientSeq ++;
    [self startConfirmTimer];
}

- (void)onClickStartButton:(id)sender {
    if (self.loginRoomSucceed) {
        self.confirm = 1;
        
        self.prepareButton.enabled = NO;
        
        self.receivedReplyCounts[confirmKey] = @0;
        self.replyTimeout[confirmKey] = @0;
        [self addLog: NSLocalizedString(@"用户选择了上机", nil)];
        
        self.state = ZegoClientStateGameConfirming;
        
        // 点击确认就开始切换拉流，而不是等到收到 confirm reply，否则太慢
        if (self.loginRoomSucceed) {
            
            if (!self.isOperating) {
                [self onBoard];
                self.isOperating = YES;
            }
        }
        
        [self.readyTimer invalidate];
        self.readyTimer = nil;
        
        clientSeq ++;
        [self startConfirmTimer];
    }
}

#pragma mark - Access method

- (void)setState:(ZegoClientState)state {
    NSLog(@"状态扭转为：%ld", (long)state);
    
    if (_state != state) {
        _state = state;
    }
    
    if (state == ZegoClientStateInitial) {
        if (self.queueCount || (self.queueCount == 0 && self.currentPlayer.length && ![self.currentPlayer isEqualToString:[ZegoSetting sharedInstance].userID])) {
            [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"预约抓娃娃\n当前排队 %d 人", nil), self.queueCount] isCancel:NO];
        } else {
            [self updatePrepareButtonToStartStatus:NSLocalizedString(@"开始游戏", nil)];
        }
        self.prepareButton.enabled = YES;
        
    }
}

@end
