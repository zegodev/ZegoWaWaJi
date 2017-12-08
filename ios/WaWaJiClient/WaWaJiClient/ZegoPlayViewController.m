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

BOOL g_isGrabed = NO;

static const NSString *applyReceivedKey =       @"receivedApplyReply";
static const NSString *cancelApplyReceivedKey = @"receivedCacelApplyReply";
static const NSString *confirmReceivedKey =     @"receivedConfirmReply";
static const NSString *resultReceivedKey =      @"receivedResultReply";

@interface ZegoPlayViewController () <ZegoRoomDelegate, ZegoLivePlayerDelegate, UINavigationControllerDelegate, ZegoResultViewDelegate, ZegoReadyViewDelegate>

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
@property (nonatomic, strong) ZegoResultView *resultView;

@property (nonatomic, copy) NSString *firstStreamID;                // 房间内第一条流 ID，默认显示
@property (nonatomic, copy) NSString *secondStreamID;               // 房间内第二条流 ID，默认不显示，可切换显示
@property (nonatomic, assign) NSInteger currentVisibleStreamIndex;  // 当前可见流，1 表示第一条流可见，2 表示第二条流可见，以此类推

@property (nonatomic, assign) ZegoStreamStatus firstStreamStatus;   // 第一条流状态
@property (nonatomic, assign) ZegoStreamStatus secondStreamStatus;  // 第二条流状态

@property (nonatomic, strong) NSMutableArray *logArray;             // 操作日志

@property (nonatomic, assign) BOOL loginRoomSucceed;                // 登录成功
@property (nonatomic, assign) BOOL isOperating;                     // 上机

@property (nonatomic, copy) NSString *currentPlayer;                // 正在游戏的用户Id
@property (nonatomic, strong) ZegoUser *serverUser;                 // 服务器用户
@property (nonatomic, assign) NSInteger queueCount;                 // 前面排队人数
@property (nonatomic, assign) NSInteger totalCount;                 // 房间总人数
@property (nonatomic, assign) NSInteger leftGameTime;               // 剩余的游戏时间
@property (nonatomic, copy) NSString *sessionId;

@property (nonatomic, strong) UIAlertController *alert;

@property (nonatomic, strong) NSTimer *operationTimer;
@property (nonatomic, strong) NSTimer *applyTimer;
@property (nonatomic, strong) NSTimer *cancelApplyTimer;
@property (nonatomic, strong) NSTimer *readyTimer;
@property (nonatomic, strong) NSTimer *playTimer;
@property (nonatomic, strong) NSTimer *confirmTimer;
@property (nonatomic, strong) NSTimer *resultTimer;
@property (nonatomic, strong) NSTimer *gameContinueTimer;

@property (nonatomic, assign) NSInteger operationTimestamp;
@property (nonatomic, assign) NSInteger applyTimestamp;
@property (nonatomic, assign) NSInteger cancelApplyTimestamp;
@property (nonatomic, assign) NSInteger readyTimestamp;
@property (nonatomic, assign) NSInteger playTimestamp;
@property (nonatomic, assign) NSInteger confirmTimestamp;
@property (nonatomic, assign) NSInteger resultTimestamp;
@property (nonatomic, assign) NSInteger gameContinueTimestamp;

@property (nonatomic, strong) ZegoCommand *command;

@property (nonatomic, assign) ZegoClientState state;                // 控制游戏状态

@property (nonatomic, assign) int gameInfoSeq;
@property (nonatomic, assign) int applySeq;
@property (nonatomic, assign) int cancelApplySeq;
@property (nonatomic, assign) int confirmSeq;
@property (nonatomic, assign) int gameReadySeq;
@property (nonatomic, assign) int gameResultSeq;

@property (nonatomic, strong) NSMutableDictionary *receivedReplyCounts; // 用于去重同一个 seq reply
@property (nonatomic, strong) NSMutableDictionary *replyTimeout;        // 等待reply 是否异常时

@property (nonatomic, assign) int confirm;           // 1 确认上机，0 确认不上机
@property (nonatomic, assign) int continueChoice;    // 1 继续玩，0 不继续玩
@property (nonatomic, assign) BOOL isGrabed;
@property (nonatomic, assign) BOOL isStartGameDirectly;   // 非再来一次玩游戏
@property (nonatomic, assign) int confirmCheckResult;     // CONFIRM 时，后台返回的校验结果

@property (nonatomic, strong) UITapGestureRecognizer *tapGesture;

@end

@implementation ZegoPlayViewController

#pragma mark - Life cycle

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

    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(willEnterForeground:) name:UIApplicationWillEnterForegroundNotification object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(didEnterBackgournd:) name:UIApplicationDidEnterBackgroundNotification object:nil];
}

- (void)viewWillAppear:(BOOL)animated {
    [super viewWillAppear:animated];
    
    [self setIdelTimerDisable:NO];
}

// FIXME: 用 loginroomsuccess 不合理，暂时没有更好的办法，待改
- (void)viewWillLayoutSubviews {
    if (!self.loginRoomSucceed) {
        if (self.currentVisibleStreamIndex == 1 && self.firstStreamStatus != ZegoStreamStatusPlaySucceed) {
            if (self.firstStreamID.length) {
                [self addPlayStatusImage:[UIImage imageNamed:@"Loading-1"] inView:self.firstPlayView];
            } else {
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-1"] inView:self.firstPlayView];
            }
        } else if (self.currentVisibleStreamIndex == 2 && self.secondStreamStatus != ZegoStreamStatusPlaySucceed) {
            if (self.secondStreamID.length) {
                [self addPlayStatusImage:[UIImage imageNamed:@"Loading-2"] inView:self.secondPlayView];
            } else {
                [self addPlayStatusImage:[UIImage imageNamed:@"DeviceOff-2"] inView:self.firstPlayView];
            }
        }
    }
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

#pragma mark - Private

#pragma mark -- Setup

- (void)setupLiveKit {
    [[ZegoManager api] setRoomDelegate:self];
    [[ZegoManager api] setPlayerDelegate:self];
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
    
    [self.forwardButton addTarget:self action:@selector(startOperationTimer:) forControlEvents:UIControlEventTouchDown];
    [self.forwardButton addTarget:self action:@selector(stopOperation:) forControlEvents:UIControlEventTouchUpInside];
    [self.backwardButton addTarget:self action:@selector(startOperationTimer:) forControlEvents:UIControlEventTouchDown];
    [self.backwardButton addTarget:self action:@selector(stopOperation:) forControlEvents:UIControlEventTouchUpInside];
    [self.leftwardButton addTarget:self action:@selector(startOperationTimer:) forControlEvents:UIControlEventTouchDown];
    [self.leftwardButton addTarget:self action:@selector(stopOperation:) forControlEvents:UIControlEventTouchUpInside];
    [self.rightwardButton addTarget:self action:@selector(startOperationTimer:) forControlEvents:UIControlEventTouchDown];
    [self.rightwardButton addTarget:self action:@selector(stopOperation:) forControlEvents:UIControlEventTouchUpInside];
    
    UITapGestureRecognizer *gesture = [[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(dismissNetworkView)];
    gesture.numberOfTapsRequired = 1;
    [self.toolView addGestureRecognizer:gesture];
}

- (void)setupModel {
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
    
    self.leftGameTime = PLAY_DURATION;
    
    self.isGrabed = NO;
    
    // 进入页面后，默认从服务器拉流
    [ZegoLiveRoomApi setConfig:@"prefer_play_ultra_source=1"];
    
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
            }
            
            self.state = ZegoClientStateInitial;
            
            if (!self.serverUser) {
                [self addLog:[NSString stringWithFormat:NSLocalizedString(@"登录房间成功，但获取服务器 user 失败", nil), self.roomID]];
            }
            
            // 登录成功后，主动去获取当前房间情况
            clientSeq ++;
            self.gameInfoSeq = clientSeq;
            NSString *gameInfoCommand = [self.command fetchGameInfo:clientSeq];
            BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:gameInfoCommand completion:^(int errorCode, NSString *roomID) {
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_GET_GAME_INFO 发送结果：%d(0成功)", errorCode]);
            }];
            NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_GET_GAME_INFO 调用结果：%d(1成功)", invokeSuccess]);
            
            // FIXME: 暂时在此时高亮开始游戏 button
            self.prepareButton.enabled = YES;
            self.switchPlayButton.enabled = YES;
        } else {
            self.loginRoomSucceed = NO;
            
            [self showAlert:NSLocalizedString(@"进入房间失败，请重新进入", nil) title:NSLocalizedString(@"提示", nil)];
            [self addLog: [NSString stringWithFormat:NSLocalizedString(@"登录房间失败，房间 ID：%@，错误码：%d", nil), self.roomID, errorCode]];
        }
    }];
}

#pragma mark -- Stream

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
        
        // 如果娃娃机控制端推流不推声音，则不需要设置音量
//        [[ZegoManager api] setPlayVolume:100 ofStream:streamID];
    }
}

- (void)playInvisibleStream:(NSString *)streamID inView:(UIView *)view {
    if (streamID.length) {
        [[ZegoManager api] startPlayingStream:streamID inView:view];
        [[ZegoManager api] setViewMode:ZegoVideoViewModeScaleAspectFit ofStream:streamID];
        
        // 如果娃娃机控制端推流不推声音，则不需要设置音量
//        [[ZegoManager api] setPlayVolume:0 ofStream:streamID];
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

#pragma mark -- Other

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

// 保持屏幕常亮
- (void)setIdelTimerDisable:(BOOL)disable
{
    [[UIApplication sharedApplication] setIdleTimerDisabled:disable];
}

// 音频打断处理
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

- (void)willEnterForeground:(NSNotification *)notification {
    if (self.firstStreamID.length) {
        [[ZegoManager api] startPlayingStream:self.firstStreamID inView:self.firstPlayView];
    }
    
    if (self.secondStreamID.length) {
        [[ZegoManager api] startPlayingStream:self.secondStreamID inView:self.secondPlayView];
    }
}

- (void)didEnterBackgournd:(NSNotification *)notification {
    NSLog(@"App did enter backgournd，停止拉流");
    [[ZegoManager api] stopPlayingStream:self.firstStreamID];
    [[ZegoManager api] stopPlayingStream:self.secondStreamID];
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

- (void)startOperationTimer:(UIButton *)button {
    [self.operationTimer invalidate];
    
    clientSeq++;
    self.operationTimestamp = [self timestamp];
    if (!self.operationTimer) {
        self.operationTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(startOperation:) userInfo:button repeats:YES];
    }

    [self.operationTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 operationCommand 定时器", nil)];
}

- (void)startApplyTimer {
    [self.applyTimer invalidate];
    
    self.applyTimestamp = [self timestamp];
    if (!self.applyTimer) {
        self.applyTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onApplyTimerAction:) userInfo:nil repeats:YES];
    }
    
    clientSeq ++;
    [self.applyTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 apply 定时器", nil)];
}

- (void)startCancelApplyTimer {
    [self.cancelApplyTimer invalidate];
    
    self.cancelApplyTimestamp = [self timestamp];
    if (!self.cancelApplyTimer) {
        self.cancelApplyTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onCancelApplyTimerAction:) userInfo:nil repeats:YES];
    }
    
    [self.cancelApplyTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 cancel apply 定时器", nil)];
}

- (void)startReadyTimer {
    [self.readyTimer invalidate];
    
    self.readyTimestamp = [self timestamp];
    
    if (!self.readyTimer) {
        self.readyTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(onReadyTimerAction:) userInfo:nil repeats:YES];
    }

    [self.readyTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 ready 定时器", nil)];
}

- (void)startPlayTimer {
    [self.playTimer invalidate];
    
    self.playTimestamp = [self timestamp];
    if (!self.playTimer) {
        self.playTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(onPlayTimerAction:) userInfo:nil repeats:YES];
    }
    [self.playTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 play 定时器，开始游戏", nil)];
}

- (void)startConfirmTimer {
    [self.confirmTimer invalidate];
    
    self.confirmTimestamp = [self timestamp];
    if (!self.confirmTimer) {
        self.confirmTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onConfirmTimerAction:) userInfo:nil repeats:YES];
    }
    [self.confirmTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 confirm 定时器，发送用户确认游戏与否信息", nil)];
}

- (void)startResultTimer {
    [self.resultTimer invalidate];
    
    self.resultTimestamp = [self timestamp];
    if (!self.resultTimer) {
        self.resultTimer = [NSTimer scheduledTimerWithTimeInterval:2.0 target:self selector:@selector(onResultTimerAction:) userInfo:nil repeats:YES];
    }

    [self.resultTimer fire];
    
    [self addLog: NSLocalizedString(@"启动 result 定时器，等待游戏结果", nil)];
}

- (void)startGameContinueTimer {
    [self.gameContinueTimer invalidate];
    
    self.gameContinueTimestamp = [self timestamp];
    if (!self.gameContinueTimer) {
        self.gameContinueTimer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(onGameContinueTimerAction:) userInfo:nil repeats:YES];
    }
    [self.gameContinueTimer fire];
}

- (void)onApplyTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    NSString *applyCommand = [self.command apply:clientSeq sessionId:self.sessionId continueChoice:self.continueChoice];
    self.applySeq = clientSeq;
    
    if (![self.receivedReplyCounts[applyReceivedKey] integerValue]) {
        if (current - self.applyTimestamp >= RETRY_DURATION) {
            [self addLog:@"停止 apply 定时器"];
            [self.applyTimer invalidate];
            self.applyTimer = nil;
            
            self.replyTimeout[applyReceivedKey] = @1;
            self.state = ZegoClientStateInitial;
            
            NSLog(@"current state-apply：%ld", (long)self.state);
            
            // 发送预约指令超过重试次数，弹框提示，恢复预约按钮可操作状态
            [self showAlert:NSLocalizedString(@"预约超时，请稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
        } else {
            BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:applyCommand completion:^(int errorCode, NSString *roomID) {
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_APPLY 发送结果：%d(0成功)，第 %ld 次发送", errorCode, (current - self.applyTimestamp) / 2 + 1]);
            }];
            
            NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_APPLY 调用结果：%d(1成功)，第 %ld 次发送", invokeSuccess, (current - self.applyTimestamp) / 2 + 1]);
        }
    } else {
        [self addLog: NSLocalizedString(@"预约后收到预约回复，停止发送预约命令，等待上机", nil)];
        [self addLog:@"停止 apply 定时器"];
        [self.applyTimer invalidate];
        self.applyTimer = nil;
    }
}

- (void)onCancelApplyTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    NSString *cancelApplyCommand = [self.command cancelApply:clientSeq sessionId:self.sessionId];
    self.cancelApplySeq = clientSeq;
    
    if (![self.receivedReplyCounts[cancelApplyReceivedKey] integerValue]) {
        if (current - self.cancelApplyTimestamp >= RETRY_DURATION) {
            [self.cancelApplyTimer invalidate];
            self.cancelApplyTimer = nil;
            
            self.replyTimeout[cancelApplyReceivedKey] = @1;
            
            NSLog(@"current state-cancel apply: %ld", (long)self.state);
            if (self.state == ZegoClientStateApplyCancelling) {
                [self showAlert:NSLocalizedString(@"取消预约超时，请稍后重试", nil) title:NSLocalizedString(@"提示", nil)];
            }
            
            self.prepareButton.enabled = YES;

            [self addLog: NSLocalizedString(@"取消预约后等待确认超时", nil)];
        } else {
            BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:cancelApplyCommand completion:^(int errorCode, NSString *roomID) {
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_CANCEL_APPLY 发送结果：%d(0成功)，第 %ld 次发送", errorCode, (current - self.cancelApplyTimestamp) / 2 + 1]);
            }];
            
            NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_CANCEL_APPLY 调用结果：%d(1成功)，第 %ld 次发送", invokeSuccess, (current - self.cancelApplyTimestamp) / 2 + 1]);
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
    if (current - self.readyTimestamp >= RETRY_DURATION) {
        [self.readyTimer invalidate];
        self.readyTimer = nil;
        
        // 倒计时结束，没有点击任何按钮，默认为不上机
        self.confirm = 0;
        [self.readyView removeFromSuperview];
         
        NSLog(@"current timer: %@", self.readyTimer);
        NSLog(@"current state-ready: %ld", (long)self.state);
        [self addLog: NSLocalizedString(@"等待用户确认上机计时结束，用户未做任何操作", nil)];
        
        self.state = ZegoClientStateInitial;
    } else {
        self.readyView.startButtonTitle = [NSString stringWithFormat:NSLocalizedString(@"开始游戏(%ds)", nil), RETRY_DURATION - (current - self.readyTimestamp)];
    }
}

- (void)onConfirmTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    NSString *confirm = [self.command gameConfirm:self.confirm clientSeq:clientSeq sessionId:self.sessionId];
    self.confirmSeq = clientSeq;
    
    if ([self.receivedReplyCounts[confirmKey] integerValue]) {
        if (self.confirm) {
            if (self.confirmCheckResult == 0) {
                [self.confirmTimer invalidate];
                self.confirmTimer = nil;
                
                // 收到回复且上机校验通过，才能进行操作
                if (self.isOperating) {
                    // 准备操作，更新界面
                    [self addLog: NSLocalizedString(@"用户开始游戏，更新界面为可操作", nil)];
                    
                    if (self.loginRoomSucceed) {
                        self.state = ZegoClientStateGamePlaying;
                        
                        [self setControlViewVisible:YES];
                        
                        // 确认上机，启动游戏计时器
                        if (self.serverUser) {
                            [self startPlayTimer];
                        }
                    }
                }
            } else {
                [self.confirmTimer invalidate];
                self.confirmTimer = nil;
                
                [self showAlert:[NSString stringWithFormat: NSLocalizedString(@"后台校验上机不通过（错误码：%d）\n请检查加密是否正确", nil), self.confirmCheckResult] title:NSLocalizedString(@"提示", nil)];
            }
        } else {
            [self.confirmTimer invalidate];
            self.confirmTimer = nil;
            
            self.state = ZegoClientStateInitial;
            [self addLog: NSLocalizedString(@"用户取消上机，恢复预约状态", nil)];
        }
    }
  
    if (current - self.confirmTimestamp >= RETRY_DURATION) {
        // 超时，停止并释放定时器
        [self.confirmTimer invalidate];
        self.confirmTimer = nil;
        
        self.state = ZegoClientStateInitial;
        self.replyTimeout[confirmKey] = @1;

        NSLog(@"current state-confirm: %ld", (long)self.state);
        if (self.confirm) {
            [self showAlert:NSLocalizedString(@"上机超时，请重新开始游戏", nil) title:NSLocalizedString(@"提示", nil)];
        } else {
            [self showAlert:NSLocalizedString(@"放弃上机超时，请重新开始游戏", nil) title:NSLocalizedString(@"提示", nil)];
        }
    } else {
        if (![self.receivedReplyCounts[confirmKey] integerValue]) {
            if (confirm) {
                BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:confirm completion:^(int errorCode, NSString *roomID) {
                    NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_GAME_CONFIRM 发送结果：%d(0成功)，第 %ld 次发送", errorCode, (current - self.confirmTimestamp) / 2 + 1]);
                }];
                
                NSLog(@"%@", [NSString stringWithFormat:@"[COMMAND] CMD_GAME_CONFIRM 调用结果：%d(1成功)，第 %ld 次发送", invokeSuccess, (current - self.confirmTimestamp) / 2 + 1]);
            } else {
                [self addLog: NSLocalizedString(@"客户端获取加密 config 失败，上机失败", nil)];
            }
        } else {
            [self addLog: NSLocalizedString(@"客户端收到用户上机与否 reply，停止发送用户上机与否信息", nil)];
        }
    }
}

- (void)onPlayTimerAction:(NSTimer *)timer {
    if (self.isStartGameDirectly) {
        self.leftGameTime = PLAY_DURATION;
    }
    NSInteger current = [self timestamp];
    if (current - self.playTimestamp > self.leftGameTime) {
        [self.playTimer invalidate];
        self.playTimer = nil;
        
        NSLog(@"current state-play: %ld", (long)self.state);
        [self addLog: NSLocalizedString(@"游戏倒计时结束，自动抓娃娃", nil)];
        
        // 倒计时结束，自动发送抓娃娃指令
        [self startGrab:nil];
    } else {
        self.countdownLabel.text = [NSString stringWithFormat:@"%lds", self.leftGameTime - (current - self.playTimestamp)];
    }
}

- (void)onResultTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    if (![self.receivedReplyCounts[resultReceivedKey] integerValue]) {
        if (current - self.resultTimestamp > RESULT_DURATION) {
            // 停止并释放定时器
            [self.resultTimer invalidate];
            self.resultTimer = nil;

            [self addLog: NSLocalizedString(@"用户获取结果超时，自动下机", nil)];
            
            NSLog(@"current state-result: %ld", (long)self.state);
            [self showAlert:NSLocalizedString(@"获取游戏结果超时", nil) title:NSLocalizedString(@"提示", nil)];
            
            [self setPrepareButtonVisible:YES];
            self.state = ZegoClientStateInitial;
        }
    }
}

- (void)onGameContinueTimerAction:(NSTimer *)timer {
    NSInteger current = [self timestamp];
    if (current - self.gameContinueTimestamp > RETRY_DURATION) {
        [self.gameContinueTimer invalidate];
        self.gameContinueTimer = nil;
        
        [self.resultView.continueButton setTitle:NSLocalizedString(@"再来一局", nil) forState:UIControlStateNormal];
        
        self.resultView.continueButton.enabled = NO;
        [self addLog:NSLocalizedString(@"等待用户确认再来一局上机超时，取消上机", nil)];
        
        //FIXME: 待确认
        self.state = ZegoClientStateInitial;
    } else {
        [self.resultView.continueButton setTitle:[NSString stringWithFormat:NSLocalizedString(@"再来一局(%ds)", nil), RETRY_DURATION - (current - self.gameContinueTimestamp)] forState:UIControlStateNormal];
    }
}

#pragma mark -- View Change

- (void)setPrepareButtonVisible:(BOOL)visible {
    if (visible) {
        self.prepareButton.hidden = NO;
        self.prepareButton.enabled = YES;
        
        self.controlView.hidden = YES;
    } else {
        self.prepareButton.hidden = YES;
    }
}

- (void)setControlViewVisible:(BOOL)visible {
    if (visible) {
        self.prepareButton.hidden = YES;
        self.controlView.hidden = NO;
        [self disableOperationButton:NO];
    } else {
        self.controlView.hidden = YES;
    }
}

- (void)disableOperationButton:(BOOL)disable {
    if (!disable) {
        self.forwardButton.enabled = YES;
        self.backwardButton.enabled = YES;
        self.leftwardButton.enabled = YES;
        self.rightwardButton.enabled = YES;
        self.grabButton.enabled = YES;
    } else {
        self.forwardButton.enabled = NO;
        self.backwardButton.enabled = NO;
        self.leftwardButton.enabled = NO;
        self.rightwardButton.enabled = NO;
        self.grabButton.enabled = NO;
    }
}

#pragma mark -- Prepare button

- (void)updatePrepareButtonToApplyStatus:(NSString *)text isCancel:(BOOL)cancel {
    NSMutableAttributedString *attributedString = [[NSMutableAttributedString alloc] initWithString:text];
    [self.prepareButton setAttributedTitle:attributedString forState:UIControlStateNormal];
    [self.prepareButton setTitle:nil forState:UIControlStateNormal];
    
    if (cancel) {
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:17.0] range:NSMakeRange(0, 4)];              // 取消预约
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:11.0] range:NSMakeRange(5, text.length - 5)];    // 前面 x 人

        [self.prepareButton setBackgroundImage:[UIImage imageNamed:@"cancel"] forState:UIControlStateNormal];
    } else {
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:17.0] range:NSMakeRange(0, 5)];              // 预约抓娃娃
        [attributedString addAttribute:NSFontAttributeName value:[UIFont systemFontOfSize:11.0] range:NSMakeRange(6, text.length - 6)];    // 当前排队 x 人
  
        [self.prepareButton setBackgroundImage:[UIImage imageNamed:@"book"] forState:UIControlStateNormal];
    }
}

- (void)updatePrepareButtonToStartStatus:(NSString *)text {
    [self.prepareButton setAttributedTitle:nil forState:UIControlStateNormal];
    [self.prepareButton setTitle:text forState:UIControlStateNormal];
    [self.prepareButton setBackgroundImage:[UIImage imageNamed:@"start"] forState:UIControlStateNormal];
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
    if (self.state == ZegoClientStateGamePlaying) {
        UIAlertController *alertController = [UIAlertController alertControllerWithTitle:NSLocalizedString(@"提示", nil)
                                                                                 message:NSLocalizedString(@"正在游戏中，确定退出房间？", nil)
                                                                          preferredStyle:UIAlertControllerStyleAlert];
        UIAlertAction *cancel = [UIAlertAction actionWithTitle:NSLocalizedString(@"取消", nil)
                                                         style:UIAlertActionStyleCancel
                                                       handler:^(UIAlertAction * _Nonnull action) {
                                                           
                                                       }];
        UIAlertAction *confirm = [UIAlertAction actionWithTitle:NSLocalizedString(@"确定", nil)
                                                          style:UIAlertActionStyleDefault
                                                        handler:^(UIAlertAction * _Nonnull action) {
                                                            [self stopPlayGame];
                                                        }];
        
        [alertController addAction:cancel];
        [alertController addAction:confirm];
        
        [self presentViewController:alertController animated:YES completion:nil];
    } else {
        [self stopPlayGame];
    }
}

- (void)stopPlayGame {
    g_isGrabed = self.isGrabed;
    
    [[ZegoManager api] stopPlayingStream:self.firstStreamID];
    [[ZegoManager api] stopPlayingStream:self.secondStreamID];
    
    if (self.loginRoomSucceed) {
        [[ZegoManager api] logoutRoom];
    }
    
    [self setIdelTimerDisable:YES];

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
    
    [self.gameContinueTimer invalidate];
    self.gameContinueTimer = nil;
    
    [self.operationTimer invalidate];
    self.operationTimer = nil;
    
    [self.confirmTimer invalidate];
    self.confirmTimer = nil;
    
    [self.navigationController popViewControllerAnimated:YES];

}

// 预约
- (IBAction)onApply:(UIButton *)sender {
    if ([self.prepareButton.currentAttributedTitle.string hasPrefix:NSLocalizedString(@"取消预约", nil)]) {
        self.prepareButton.enabled = NO;
        
        self.state = ZegoClientStateApplyCancelling;
        self.receivedReplyCounts[cancelApplyReceivedKey] = @0;
        self.replyTimeout[cancelApplyReceivedKey] = @0;
        
        clientSeq ++;
        
        if (self.serverUser) {
            [self startCancelApplyTimer];
        }
        
        return;
    }
    
    self.isStartGameDirectly = YES;

    self.replyTimeout[applyReceivedKey] = @0;
    self.receivedReplyCounts[applyReceivedKey] = @0;
    self.continueChoice = 1;
    
    self.prepareButton.enabled = NO;
    
    if (self.serverUser) {
        [self startApplyTimer];
        self.state = ZegoClientStateApplying;
    }
}

- (void)startOperation:(NSTimer *)timer {
    UIButton *button = timer.userInfo;
    if (button == self.forwardButton) {
        [self goForward:button];
    } else if (button == self.backwardButton) {
        [self goBackward:button];
    } else if (button == self.leftwardButton) {
        [self goLeftward:button];
    } else if (button == self.rightwardButton) {
        [self goRightward:button];
    }
}

- (void)stopOperation:(id)sender {
    [self.operationTimer invalidate];
    self.operationTimer = nil;
    
    if (self.isOperating) {
        [self addLog:NSLocalizedString(@"用户发送移动指令：停止", nil)];

        clientSeq++;
        NSString *stopCommand = nil;
        stopCommand = [self.command moveStop:clientSeq sessionId:self.sessionId];

        BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:stopCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_STOP 发送结果：%d（0成功）", nil), errorCode]];
        }];

        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_STOP 调用结果：%d（1成功）", nil), invokeSuccess]];
    }
}

- (void)goForward:(id)sender {
    if (self.isOperating) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向前", nil)];
        
        NSString *goForwardCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            goForwardCommand = [self.command moveLeft:clientSeq sessionId:self.sessionId];
        } else {
            goForwardCommand = [self.command moveBackward:clientSeq sessionId:self.sessionId];
        }
        
        BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:goForwardCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_FORWARD 发送结果：%d（0成功）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_FORWARD 调用结果：%d（1成功）", nil), invokeSuccess]];
    }
}

- (void)goBackward:(id)sender {
    if (self.isOperating) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向后", nil)];
        
        NSString *goBackwardCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            goBackwardCommand = [self.command moveRight:clientSeq sessionId:self.sessionId];
        } else {
            goBackwardCommand = [self.command moveForward:clientSeq sessionId:self.sessionId];
        }
        BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:goBackwardCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_BACKWARD 发送结果：%d（0成功）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_BACKWARD 调用结果：%d（1成功）", nil), invokeSuccess]];
    }
}

- (void)goLeftward:(id)sender {
    if (self.isOperating) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向左", nil)];
        
        NSString *goLeftCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            goLeftCommand = [self.command moveForward:clientSeq sessionId:self.sessionId];
        } else {
            goLeftCommand = [self.command moveLeft:clientSeq sessionId:self.sessionId];
        }
        BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:goLeftCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_LEFT 发送结果：%d（0成功）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_LEFT 调用结果：%d（1成功）", nil), invokeSuccess]];
    }
}

- (void)goRightward:(id)sender {
    if (self.isOperating) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：向右", nil)];
        
        NSString *goRightCommand  = nil;
        if (self.currentVisibleStreamIndex == 1) {
            goRightCommand = [self.command moveBackward:clientSeq sessionId:self.sessionId];
        } else {
            goRightCommand = [self.command moveRight:clientSeq sessionId:self.sessionId];
        }
        BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:goRightCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_RIGHT 发送结果：%d（0成功）", nil), errorCode]];
        }];
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_RIGHT 调用结果：%d（1成功）", nil), invokeSuccess]];
    }
}

- (IBAction)startGrab:(id)sender {
    self.receivedReplyCounts[resultReceivedKey] = @0;
    
    clientSeq ++;
    NSString *startGrabCommand = [self.command moveDown:clientSeq sessionId:self.sessionId];

    if (self.isOperating) {
        [self addLog: NSLocalizedString(@"用户发送移动命令：抓娃娃", nil)];
        [self.playTimer invalidate];
        self.playTimer = nil;

        BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:startGrabCommand completion:^(int errorCode, NSString *roomID) {
            [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_DOWN 发送结果：%d（0成功）", nil), errorCode]];
        }];
        
        self.isGrabed = YES;
        
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] MOVE_DOWN 调用结果：%d（1成功）", nil), invokeSuccess]];
        
        [self disableOperationButton:YES];
        self.grabButton.enabled = YES;
        self.countdownLabel.text = @"";
        
        if (![self.receivedReplyCounts[resultReceivedKey] integerValue]) {
            [self startResultTimer];
        }
        
        self.state = ZegoClientStateResultWaiting;
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
            [self addPlayStatusImage:[UIImage imageNamed:@"PlayFailed-2"] inView:self.secondPlayView];
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
//         [self addLog: [NSString stringWithFormat:NSLocalizedString(@"没有流数据，请检查推流或网络是否异常，流 ID：%@", nil), streamID]];
         
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

- (void)onDisconnect:(int)errorCode roomID:(NSString *)roomID {
    if ([roomID isEqualToString:self.roomID]) {
        [self disableOperationButton:YES];
        self.prepareButton.enabled = NO;
        [self showAlert:[NSString stringWithFormat:NSLocalizedString(@"与服务器断开连接（错误码：%d）\n请检查网络是否正常", nil), errorCode] title:NSLocalizedString(@"提示", nil)];
    }
}

- (void)onTempBroken:(int)errorCode roomID:(NSString *)roomID {
    if ([roomID isEqualToString:self.roomID]) {
        [self disableOperationButton:YES];
        self.prepareButton.enabled = NO;
        [self showAlert:NSLocalizedString(@"与服务器连接异常，正在尝试恢复...", nil) title:NSLocalizedString(@"提示", nil)];
    }
}

- (void)onReconnect:(int)errorCode roomID:(NSString *)roomID {
    if ([roomID isEqualToString:self.roomID] && errorCode == 0) {
        [self disableOperationButton:NO];
        self.prepareButton.enabled = YES;
        [self showAlert:NSLocalizedString(@"与服务器连接恢复，请继续游戏", nil) title:NSLocalizedString(@"提示", nil)];
    }
}

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

#pragma mark -- Handle custom command

- (void)onReceiveCustomCommand:(NSString *)fromUserID userName:(NSString *)fromUserName content:(NSString *)content roomID:(NSString *)roomID {
    // 校验 serverUser
    if (self.serverUser && [fromUserID isEqualToString:self.serverUser.userId]) {
        // 校验房间
        if (roomID && self.roomID && [roomID isEqualToString:self.roomID]) {
            NSDictionary *response = [self.command parseContent:content];
            NSInteger command = [response[cmdKey] integerValue];
            
            // server 回复收到预约申请，并告知预约结果（Server-->Client）
            if (command == CMD_APPLY_REPLY) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_APPLY_REPLY, %@", content]];
                [self handleApplyReply:response];
            }
            
            // 收到取消预约 reply
            if (command == CMD_CANCEL_APPLY_REPLY) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_CANCEL_APPLY_REPLY, %@", content]];
                [self handleCancelApplyReply:response];
            }
            
            // 通知某人准备上机, 此时用户可使用 CMD_ABANDON_PLAY 放弃游戏（Server-->Client）
            if (command == CMD_GAME_READY) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_GAME_READY, %@", content]];
                [self handleGameReady:response];
            }
            
            // 回复收到确认上机或者放弃玩游戏指令（Server-->Client）
            if (command == CMD_CONFIRM_REPLY) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_CONFIRM_REPLY, %@", content]];
                [self handleConfirmReply:response];
            }
            
            // 全员广播房间信息（总人数，排队列表、当前游戏者）更新（Server-->Client）
            if (command == CMD_USER_UPDATE) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_USER_UPDATE, %@", content]];
                [self handleUserUpdate:response];
            }
            
            // 收到游戏结果
            if (command == CMD_GAME_RESULT) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_GAME_RESULT, %@", content]];
                [self handleGameResult:response];
            }
            
            // 收到获取游戏结果 reply
            if (command == CMD_GET_GAME_INFO_REPLY) {
                [self addLog: [NSString stringWithFormat:@"[RECEIVED] CMD_GET_GAME_INFO_REPLY, %@", content]];
                [self handleGameInfoReply:response];
            }
        }
    }
}

- (void)handleApplyReply:(NSDictionary *)response {
    if ((self.state == ZegoClientStateApplying) && ![self.replyTimeout[applyReceivedKey] intValue]) {
        NSInteger result = [response[resultKey] integerValue];
        NSString *userId = response[playerIdKey];
        NSInteger seq = [response[seqKey] integerValue];
        NSInteger index = [response[indexKey] integerValue];
        self.sessionId = response[sessionIdInnerKey];
        NSLog(@">>>>>applyReply 更新 sessionId：%@", self.sessionId);
        
        // 去重后台回复的多条同一 seq 的 apply reply
        NSInteger repeat = [self.receivedReplyCounts[applyReceivedKey] integerValue];
        
        if (seq == self.applySeq && !repeat) {
            if ([userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                self.receivedReplyCounts[applyReceivedKey] = @1;
                
                if (result == 0) {
                    self.state = ZegoClientStateGameWaiting;
                    
                    // 当前正在游戏的人，也在排队之列
                    [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"取消预约\n前面 %d 人", nil), index] isCancel:YES];
                    self.prepareButton.enabled = YES;
                } else {
                    if (self.state == ZegoClientStateApplying) {
                        [self showAlert:[NSString stringWithFormat: NSLocalizedString(@"预约失败（错误码：%d）\n请稍后重试", nil), result] title:NSLocalizedString(@"提示", nil)];
                    }
                    
                    self.state = ZegoClientStateInitial;
                }
            }
        }
    }
}

- (void)handleCancelApplyReply:(NSDictionary *)response {
    if (self.state == ZegoClientStateApplyCancelling && ![self.replyTimeout[cancelApplyReceivedKey] intValue]) {
        NSInteger seq = [response[seqKey] integerValue];;
        
        // 去重后台回复的多条同一 seq 的 apply reply
        NSInteger repeat = [self.receivedReplyCounts[cancelApplyReceivedKey] integerValue];
        
        if (seq == self.cancelApplySeq && !repeat) {
            self.receivedReplyCounts[cancelApplyReceivedKey] = @1;
        }
    }
}

- (void)handleGameReady:(NSDictionary *)response {
    // ready 先于 apply reply 收到
    
    if (self.state == ZegoClientStateApplying) {
        [self addLog: NSLocalizedString(@"客户端先收到 gameready，仍然更新 sessionId，抛弃 apply reply", nil)];
        
        self.sessionId = response[sessionIdOuterKey];
        NSLog(@">>>>>gameReady，更新 sessionId：%@", self.sessionId);
        
        self.state = ZegoClientStateGameWaiting;
        
        // 不再发送 apply
        [self.applyTimer invalidate];
        self.applyTimer = nil;
    }
    
    if (![self.replyTimeout[applyReceivedKey] intValue] && (self.state == ZegoClientStateGameWaiting || self.state == ZegoClientStateApplyCancelling)) {
        [self addLog: NSLocalizedString(@"客户端收到允许上机，状态吻合", nil)];
    
        NSInteger seq = [response[seqKey] intValue];
        NSString *userId = response[playerIdKey];
        
        // 过滤同一个 seq 的通知
        if (self.gameReadySeq != seq) {
            self.gameReadySeq = (int)seq;
            
            if ([userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                // 向服务回复收到 CMD_GAME_READY_REPLY 命令，否则服务器会一直重试发送
                NSString *gameReadyReplyCommand = [self.command gameReadyReply:self.gameReadySeq sessionId:self.sessionId];
                BOOL invokeSuccess = [self.command sendCommandToServer:self.serverUser content:gameReadyReplyCommand completion:^(int errorCode, NSString *roomID) {
                    [self addLog: [NSString stringWithFormat:NSLocalizedString(@"[COMMAND] CMD_READY_REPLY 发送结果: %d（0成功）", nil), errorCode]];
                }];
                
                [self addLog: [NSString stringWithFormat:NSLocalizedString(@"[COMMAND] CMD_READY_REPLY 调用结果: %d（1成功）", nil), invokeSuccess]];
                
                if (self.isStartGameDirectly) {
                    self.readyView = [[[NSBundle mainBundle] loadNibNamed:@"ZegoReadyView" owner:nil options:nil] firstObject];
                    self.readyView.delegate = self;
                    self.readyView.frame = self.view.frame;
                    [self.view addSubview:self.readyView];
                    
                    if (self.serverUser) {
                        [self startReadyTimer];
                    }
                } else {
                    // 默认发送再来一局后，收到上机提醒
                    self.state = ZegoClientStateGameConfirming;
                    
                    if (self.continueChoice == 0) {
                        [self addLog: NSLocalizedString(@"客户端收到服务器的再来一局确认，用户已放弃上机", nil)];
                        self.receivedReplyCounts[confirmKey] = @0;
                        
                        self.confirm = 0;
                        clientSeq ++;
                        [self startConfirmTimer];
                    } else {
                        [self addLog: NSLocalizedString(@"客户端收到服务器的再来一局确认，等待用户上机", nil)];
                        self.resultView.continueButton.enabled = YES;
                        [self startGameContinueTimer];
                    }
                }
            }
        }
    }
}

- (void)handleConfirmReply:(NSDictionary *)response {
    NSInteger seq = [response[seqKey] intValue];
    self.confirmCheckResult = [response[resultKey] intValue];
    
    if (self.state == ZegoClientStateGameConfirming && ![self.replyTimeout[confirmKey] intValue]) {
        // 去重后台回复的多条同一 seq 的 apply reply
        NSInteger repeat = [self.receivedReplyCounts[confirmKey] integerValue];
        
        if (seq == self.confirmSeq && !repeat) {
            [self addLog: NSLocalizedString(@"客户端收到确认上机与否 reply，状态吻合", nil)];
            self.receivedReplyCounts[confirmKey] = @1;
        }
    }
}

- (void)handleUserUpdate:(NSDictionary *)response {
    // 当前正在玩游戏的人
    self.currentPlayer = response[playerIdKey];
    
    // 房间排队列表
    NSArray *queueList = response[queueKey];
    self.queueCount = queueList.count;
    
    self.totalCount = [response[totalKey] integerValue];
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
    
    // 取消预约超时
    if (self.state == ZegoClientStateApplyCancelling && [self.replyTimeout[cancelApplyReceivedKey] intValue]) {
        if (exist) {
            [self addLog: NSLocalizedString(@"取消预约超时后，排队列表中仍有当前用户，取消预约失败", nil)];
            self.state = ZegoClientStateGameWaiting;
        } else {
            [self addLog: NSLocalizedString(@"取消预约超时后，排队列表中没有当前用户，取消预约成功", nil)];
            self.state = ZegoClientStateInitial;
        }
    }
    
    if (self.state == ZegoClientStateGameWaiting) {
        // 当前正在游戏的人，也算在排队人数之列
        [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"取消预约\n前面 %d 人", nil), currentIndex + 1] isCancel:YES];
    }
    
    if (self.state == ZegoClientStateInitial) {
        if (self.queueCount || (self.queueCount == 0 && self.currentPlayer.length && ![self.currentPlayer isEqualToString:[ZegoSetting sharedInstance].userID])) {
            // 当前正在游戏的人，也算在排队人数之列
            [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"预约抓娃娃\n当前排队 %d 人", nil), self.queueCount + 1] isCancel:NO];
        } else {
            [self updatePrepareButtonToStartStatus:NSLocalizedString(@"开始游戏", nil)];
        }
        self.prepareButton.enabled = YES;
    }
}

- (void)handleGameInfoReply:(NSDictionary *)response {
    if (self.state == ZegoClientStateInitial) {
        NSInteger seq = [response[seqKey] integerValue];
        // 校验 seq
        if (self.gameInfoSeq == seq) {
            self.totalCount = [response[totalKey] integerValue];
            self.countLabel.text = [NSString stringWithFormat: NSLocalizedString(@"%d人在房间",
                                                                                 nil), self.totalCount];
            NSArray *queue = response[queueKey];
            self.queueCount = queue.count;
            
            NSString *playerId = response[playerIdKey];
            self.currentPlayer = playerId;
            
            if (self.queueCount || (self.queueCount == 0 && self.currentPlayer.length && ![self.currentPlayer isEqualToString:[ZegoSetting sharedInstance].userID])) {
                // 当前正在游戏的人，也算在排队人数之列
                [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"预约抓娃娃\n当前排队 %d 人", nil), self.queueCount + 1] isCancel:NO];
            }
        
            // 如果当前正在玩的用户是自己，则提示是否继续游戏
            if ([self.currentPlayer isEqualToString:[ZegoSetting sharedInstance].userID] && !g_isGrabed) {
                self.leftGameTime = [response[leftTimeKey] integerValue];
                if (self.leftGameTime > 0) {
                    UIAlertController *alertController = [UIAlertController alertControllerWithTitle:NSLocalizedString(@"提示", nil)
                                                                                             message:[NSString stringWithFormat:NSLocalizedString(@"上次游戏未完成，还剩 %d s，是否继续？", nil), self.leftGameTime]
                                                                                      preferredStyle:UIAlertControllerStyleAlert];
                    UIAlertAction *cancel = [UIAlertAction actionWithTitle:NSLocalizedString(@"取消", nil)
                                                                     style:UIAlertActionStyleCancel
                                                                   handler:^(UIAlertAction * _Nonnull action) {
                                                                   }];
                    UIAlertAction *confirm = [UIAlertAction actionWithTitle:NSLocalizedString(@"确定", nil)
                                                                      style:UIAlertActionStyleDefault
                                                                    handler:^(UIAlertAction * _Nonnull action) {
                                                                        if (self.loginRoomSucceed) {
                                                                            if (!self.isOperating) {
                                                                                self.isOperating = YES;
                                                                            }
                                                                        }
                                                                        
                                                                        self.isStartGameDirectly = NO;
                                                                        
                                                                        self.continueChoice = 1;
                                                                        self.state = ZegoClientStateGamePlaying;
                                                                        [self setControlViewVisible:YES];
                                                                        
                                                                        [self startPlayTimer];
                                                                    }];
                    
                    [alertController addAction:cancel];
                    [alertController addAction:confirm];
                    
                    [self presentViewController:alertController animated:YES completion:nil];
                }
            }
        }
    }
}

- (void)handleGameResult:(NSDictionary *)response {
    if (self.state == ZegoClientStateResultWaiting) {
        [self addLog: NSLocalizedString(@"客户端收到游戏结果，状态吻合", nil)];
        
        int seq = [response[seqKey] intValue];
        NSString *userId = response[playerIdKey];
        NSInteger result = [response[resultKey] integerValue];
        
        if (self.gameResultSeq != seq) {
            self.gameResultSeq = seq;
            
            [self addLog:[NSString stringWithFormat:@"gameResult-player-id: %@", userId]];
            
            if ([userId isEqualToString:[ZegoSetting sharedInstance].userID]) {
                self.receivedReplyCounts[resultReceivedKey] = @1;
                
                [self.resultTimer invalidate];
                self.resultTimer = nil;
                
                self.resultView = [[[NSBundle mainBundle] loadNibNamed:@"ZegoResultView" owner:nil options:nil] firstObject];
                self.resultView.delegate = self;
                self.resultView.frame = self.view.frame;
                self.resultView.continueButton.enabled = NO;
                
                if (result == 1) {
                    self.resultView.imageName = @"success";
                } else {
                    self.resultView.imageName = @"failure";
                }
                
                [self.view addSubview:self.resultView];
            }
        }
        
        // 默认再来一局
        [self sendResultReply:self.continueChoice];
        self.isStartGameDirectly = NO;
        
        self.receivedReplyCounts[applyReceivedKey] = @0;
        self.replyTimeout[applyReceivedKey] = @0;
        [self startApplyTimer];
        
        self.state = ZegoClientStateApplying;
    }
    
    // 用户放弃再来一局，收到 gameResult 时，仍然回复
    if (self.state == ZegoClientStateInitial && self.continueChoice == 0) {
        [self sendResultReply:0];
    }
}

- (void)sendResultReply:(NSInteger)continueChoice {
    NSString *gameResultReplyCommand = [self.command resultReply:self.gameResultSeq sessionId:self.sessionId choice:continueChoice];
    BOOL invokeSuccess =  [self.command sendCommandToServer:self.serverUser content:gameResultReplyCommand completion:^(int errorCode, NSString *roomID) {
        [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] GAME_RESULT_REPLY 发送结果：%d（0成功），再来一局：%d", nil), errorCode, self.continueChoice]];
    }];
    
    [self addLog: [NSString stringWithFormat: NSLocalizedString(@"[COMMAND] GAME_RESULT_REPLY 调用结果：%d（1成功），再来一局：%d", nil), invokeSuccess, self.continueChoice]];
    
    self.state = ZegoClientStateInitial;
}

#pragma mark - ZegoResultViewDelegate

- (void)onClickBackButton:(UIButton *)button {
    if (self.gameContinueTimer.isValid) {
        [self.gameContinueTimer invalidate];
        self.gameContinueTimer = nil;
    }
    
    if (self.state == ZegoClientStateGameConfirming) {
        self.receivedReplyCounts[confirmKey] = @0;
        
        self.prepareButton.enabled = NO;
        self.confirm = 0;
        clientSeq ++;
        [self startConfirmTimer];
    } else {
        self.continueChoice = 0;
        
        [self setPrepareButtonVisible:YES];
        self.state = ZegoClientStateInitial;
    }
}

- (void)onClickContinueButton:(UIButton *)button {
    [self.gameContinueTimer invalidate];
    self.gameContinueTimer = nil;
    
    self.continueChoice = 1;
    self.receivedReplyCounts[confirmKey] = @0;
    self.confirm = 1;
    self.state = ZegoClientStateGameConfirming;
    
    [self setControlViewVisible:YES];
    [self disableOperationButton:YES];
    
    clientSeq ++;
    [self startConfirmTimer];
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
        
        if (self.loginRoomSucceed) {
            if (!self.isOperating) {
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
            [self updatePrepareButtonToApplyStatus:[NSString stringWithFormat:NSLocalizedString(@"预约抓娃娃\n当前排队 %d 人", nil), self.queueCount + 1] isCancel:NO];
        } else {
            [self updatePrepareButtonToStartStatus:NSLocalizedString(@"开始游戏", nil)];
        }
        [self setPrepareButtonVisible:YES];
    }
}

@end
