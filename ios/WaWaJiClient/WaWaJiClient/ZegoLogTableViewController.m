//
//  ZegoLogTableViewController.m
//  
//
//  Created by summery on 13/09/2017.
//  Copyright © 2017 ZEGO. All rights reserved.
//

#import "ZegoLogTableViewController.h"


@interface ZegoLogTableViewController ()

@end

@implementation ZegoLogTableViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Uncomment the following line to preserve selection between presentations.
    // self.clearsSelectionOnViewWillAppear = NO;
    
    // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
    // self.navigationItem.rightBarButtonItem = self.editButtonItem;
    
    self.tableView.tableFooterView = [[UIView alloc] init];
    self.tableView.estimatedRowHeight = 44.0;
    self.tableView.rowHeight = UITableViewAutomaticDimension;
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(onLogUpdate:) name:@"logUpdateNotification" object:nil];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

- (void)onLogUpdate:(NSNotification *)notification
{
    CGPoint contentOffset = self.tableView.contentOffset;
    contentOffset.y += [self tableView:self.tableView heightForRowAtIndexPath:[NSIndexPath indexPathForRow:0 inSection:0]];
    [self.tableView reloadData];
    [self.tableView setContentOffset:contentOffset];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return self.logArray.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath {
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"logCell"];
    if (!cell)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:@"logCell"];
    }
    if (indexPath.row >= self.logArray.count)
        return cell;
    
    NSString *logString = [self.logArray objectAtIndex:indexPath.row];
    cell.textLabel.text = logString;
    cell.textLabel.numberOfLines = 0;
    cell.textLabel.font = [UIFont systemFontOfSize:16.0];
    cell.textLabel.textColor = [UIColor grayColor];
    
    return cell;
}

- (IBAction)onClose:(id)sender
{
    [self dismissViewControllerAnimated:YES completion:nil];
}

- (BOOL)tableView:(UITableView *)tableView shouldHighlightRowAtIndexPath:(NSIndexPath *)indexPath {
    return NO;
}

- (BOOL)tableView:(UITableView *)tableView shouldShowMenuForRowAtIndexPath:(NSIndexPath *)indexPath
{
    return YES;
}

- (BOOL)tableView:(UITableView *)tableView canPerformAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender
{
    return (action == @selector(copy:));
}

- (void)tableView:(UITableView *)tableView performAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender
{
    if (action == @selector(copy:))
    {
        if (indexPath.row >= self.logArray.count)
            return;
        
        NSString *logString = [self.logArray objectAtIndex:indexPath.row];
        if (logString)
        {
            [[UIPasteboard generalPasteboard] setString:logString];
        }
    }
}

@end

@implementation ZegoLogNavigationController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    CGRect screenRect = [[UIScreen mainScreen] bounds];
    UINavigationBar *navigationBar = [[UINavigationBar alloc] initWithFrame:CGRectMake(0, 20, screenRect.size.width, 44)];
    
    UINavigationItem *item = [[UINavigationItem alloc] initWithTitle:NSLocalizedString(@"日志", nil)];
    [navigationBar pushNavigationItem:item animated:YES];
    [self.view addSubview:navigationBar];
    
    UIBarButtonItem *closeItem = [[UIBarButtonItem alloc] initWithTitle:NSLocalizedString(@"完成", nil) style:UIBarButtonItemStylePlain target:self action:@selector(closeLog)];
    
    [item setRightBarButtonItem:closeItem];
}

- (void)closeLog {
    [self dismissViewControllerAnimated:YES completion:nil];
}



@end
