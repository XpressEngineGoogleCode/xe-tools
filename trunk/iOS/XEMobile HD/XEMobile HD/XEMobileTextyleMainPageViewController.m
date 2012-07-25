//
//  XEMobileTextyleMainPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleMainPageViewController.h"
#import "XEMobileTextylePostsTableViewController.h"
#import "XEMobileTextyleCommentsListViewController.h"
#import "XEMobileTextyleSettingsViewController.h"
#import "XEMobileTextylePageViewController.h"
#import "XEMobileTextyleWritingSettingsViewController.h"
#import "XEMobileTextyleSkinsViewController.h"

@interface XEMobileTextyleMainPageViewController ()

@end

@implementation XEMobileTextyleMainPageViewController
@synthesize textyleItem = _textyleItem;
@synthesize tableView = _tableView;
@synthesize stats;
@synthesize titleLabel = _titleLabel;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = self.textyleItem.domain;
	
    self.titleLabel.text = self.textyleItem.browserTitle;
    
    [self makeRequestForStatistics];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XETextyleStats class]] ) 
    {
        self.stats = object;
        self.tableView.hidden = NO;
        [self.indicator stopAnimating];
        [self.tableView reloadData];
    }
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"Error!"];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

-(void)makeRequestForStatistics
{
    self.tableView.hidden = YES;
    [self.indicator startAnimating];
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XETextyleStats class]];
    
    [mapping mapKeyPath:@"monday" toAttribute:@"monday"];
    [mapping mapKeyPath:@"tuesday" toAttribute:@"tuesday"];
    [mapping mapKeyPath:@"wednesday" toAttribute:@"wednesday"];
    [mapping mapKeyPath:@"thursday" toAttribute:@"thursday"];
    [mapping mapKeyPath:@"friday" toAttribute:@"friday"];
    [mapping mapKeyPath:@"saturday" toAttribute:@"saturday"];
    [mapping mapKeyPath:@"sunday" toAttribute:@"sunday"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationTextyleStats&site_srl=%@",self.textyleItem.siteSRL] delegate:self];

}

- (void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}



- (IBAction)postsButtonPressed:(id)sender
{
    XEMobileTextylePostsTableViewController *postsVC = [[XEMobileTextylePostsTableViewController alloc] initWithNibName:@"XEMobileTextylePostsTableViewController" bundle:nil];
    postsVC.textyleItem = self.textyleItem;
    [self.detailViewController.navigationController pushViewController:postsVC animated:YES];
}

- (IBAction)commentsButtonPressed:(id)sender
{
    XEMobileTextyleCommentsListViewController *commentsVC = [ [XEMobileTextyleCommentsListViewController alloc] initWithNibName:@"XEMobileTextyleCommentsListViewController" bundle:nil];
    commentsVC.textyle = self.textyleItem;
    [self.detailViewController.navigationController pushViewController:commentsVC animated:YES];
}

- (IBAction)settingsButtonPressed:(id)sender
{
    XEMobileTextyleSettingsViewController *settings = [[XEMobileTextyleSettingsViewController alloc] initWithNibName:@"XEMobileTextyleSettingsViewController" bundle:nil];    
    XETextyleSettings *settingsObject = [[XETextyleSettings alloc] init];
    settingsObject.defaultLanguage = self.textyleItem.defaultLanguage;
    settingsObject.timezone = self.textyleItem.timezone;
    settingsObject.blogTitle = self.textyleItem.textyleTitle;
    settings.textyle = self.textyleItem;
    settings.detailViewController = self.detailViewController;
    settings.settings = settingsObject;
    UINavigationController *generalSettingNavCon = [[ UINavigationController alloc] initWithRootViewController:settings];
    
    
    XEMobileTextyleWritingSettingsViewController *writing = [[XEMobileTextyleWritingSettingsViewController alloc] initWithNibName:@"XEMobileTextyleWritingSettingsViewController" bundle:nil];
    writing.title = @"Writing";
    writing.textyle = self.textyleItem;
    writing.detailViewController = self.detailViewController;
    UINavigationController *writingSettingsNavCon = [[ UINavigationController alloc] initWithRootViewController:writing];
    
    XEMobileTextyleSkinsViewController *skinsVC = [[ XEMobileTextyleSkinsViewController alloc] initWithNibName:@"XEMobileTextyleSkinsViewController" bundle:nil];
    skinsVC.title = @"Skins";
    skinsVC.textyle = self.textyleItem;
    skinsVC.detailViewController = self.detailViewController;
    UINavigationController *skinsNavCon = [[UINavigationController alloc] initWithRootViewController:skinsVC];
    
    UITabBarController *tabBarController = [[UITabBarController alloc] init ];
    [tabBarController setViewControllers:[NSArray arrayWithObjects:generalSettingNavCon, writingSettingsNavCon, skinsNavCon, nil]];
    
    
    [self.detailViewController.navigationController pushViewController:tabBarController animated:YES];
}

-(IBAction)pagesButtonPressed:(id)sender
{
    XEMobileTextylePageViewController *pagesVC = [[XEMobileTextylePageViewController alloc] initWithNibName:@"XEMobileTextylePageViewController" bundle:nil];
    pagesVC.textyle = self.textyleItem;
    [self.detailViewController.navigationController pushViewController:pagesVC animated:YES];
}



-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 7;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ident = @"CellIdentifier";
    UILabel *label;
    UITableViewCell *cell = [ tableView dequeueReusableCellWithIdentifier:ident];
    if (  cell == nil )
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ident];
        label = [[ UILabel alloc] initWithFrame:CGRectMake(200, 10, 60, 30)];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:label];
        NSLog(@"index = %d",indexPath.row);
    }
    else 
    {
        label = [[cell.contentView subviews] objectAtIndex:1];
    }
    
    switch (indexPath.row) {
        case 0:
            cell.textLabel.text = @"Monday";
            label.text = self.stats.monday;
            break;
            
        case 1:
            cell.textLabel.text = @"Tuesday";
            label.text = self.stats.tuesday;
            break;
            
        case 2:
            cell.textLabel.text = @"Wednesday";
            label.text = self.stats.wednesday;
            break;
        case 3:
            cell.textLabel.text = @"Thursday";
            label.text = self.stats.thursday;
            break;
        case 4:
            cell.textLabel.text = @"Friday";
            label.text = self.stats.friday;
            break;
        case 5:
            cell.textLabel.text = @"Saturday";
            label.text = self.stats.saturday;
            break;
        case 6:
            cell.textLabel.text = @"Sunday";
            label.text = self.stats.sunday;
            break;
    }
    
    cell.userInteractionEnabled = NO;
    return cell;
}

@end
