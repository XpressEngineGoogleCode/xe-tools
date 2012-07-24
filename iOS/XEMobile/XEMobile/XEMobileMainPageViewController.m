//
//  XEMobileMainPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 14/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileMainPageViewController.h"
#import "XEMobileLoginViewController.h"
#import "XEUser.h"
#import "XEMobileMembersManagementViewController.h"
#import "XEMobileSettingsViewController.h"
#import "XEMobilePageManagementViewController.h"
#import "XEMobileMenuManagementViewController.h"
#import "XEMobileTextyleSelectViewController.h"
#import "XEMobileStatisticsViewController.h"

@interface XEMobileMainPageViewController ()

@end

@implementation XEMobileMainPageViewController
@synthesize textyleButton = _textyleButton;
@synthesize titleLabel = _titleLabel;

- (void)viewDidLoad
{
    [super viewDidLoad];
    self.navigationItem.title = @"Home";
}

-(void)loadTitle
{
    NSString *link = [RKObjectManager sharedManager].baseURL.absoluteString;
    
    if( [link isEqualToString:@"http://(null)"] ) self.titleLabel.text = @"Welcome";
    else
    if( [[link substringToIndex:7] isEqualToString:@"http://"] )
    {
        self.titleLabel.text = [link substringWithRange:NSMakeRange(7, link.length-7)];
    }
}

-(void)viewWillAppear:(BOOL)animated
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    [mapping mapKeyPath:@"forum" toAttribute:@"forum"];
    [mapping mapKeyPath:@"textyle" toAttribute:@"textyle"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    
    NSDictionary *parametr = [[NSDictionary alloc] 
                              initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationCheckTextyleAndForum", nil] 
                              forKeys:[NSArray arrayWithObjects:@"module",@"act", nil]];
    
    NSString *path = [@"/index.php" stringByAppendingQueryParameters:parametr];
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData = @"modules";
     }
     ];
    
    [self loadTitle];
    [self.indicator startAnimating];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}


- (void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    
    if ( [objectLoader.userData isEqualToString:@"logout"] )
    {
        XEUser *user = (XEUser *)object;
        if( [user.loggedOut isEqualToString:@"true"] )
        {
            [self pushLoginViewController];
        }
    }
    else if ( [objectLoader.userData isEqualToString:@"modules"] )
    {
        XEUser *user = (XEUser *)object;

        if ( [user containsTextyleModule] ) self.textyleButton.hidden = NO;
        else {
            UILabel *label = [[UILabel alloc] initWithFrame:self.textyleButton.frame];
            label.text = @"No Textyle!";
            [self.view addSubview:label];
        }
        [self.indicator stopAnimating];
        [self.indicator removeFromSuperview];
    }
}

-(IBAction)statsButtonPressed:(id)sender
{
    XEMobileStatisticsViewController *statsVC = [[XEMobileStatisticsViewController alloc] initWithNibName:@"XEMobileStatisticsViewController" bundle:nil];
    [self.navigationController pushViewController:statsVC animated:YES];
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    
    self.titleLabel = nil;
    self.textyleButton = nil;
    self.indicator = nil;
}

-(IBAction)logoutButtonPressed:(id)sender
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    [mapping mapKeyPath:@"value" toAttribute:@"loggedOut"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    NSDictionary *parametr = [[NSDictionary alloc] 
                              initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationLogout", nil] 
                              forKeys:[NSArray arrayWithObjects:@"module",@"act", nil]];
    NSString *request = [@"/index.php" stringByAppendingQueryParameters:parametr];
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:request usingBlock:^(RKObjectLoader*loader)
     {
         loader.delegate = self;
         loader.userData = @"logout";
     }];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}


-(IBAction)membersButtonPressed:(id)sender
{
    XEMobileMembersManagementViewController *memberVC = [[XEMobileMembersManagementViewController alloc] initWithNibName:@"XEMobileMembersManagementViewController" bundle:nil];
    [self.navigationController pushViewController:memberVC animated:YES];
}

-(IBAction)globalSettingsButtonPressed:(id)sender
{
    XEMobileSettingsViewController *settingsVC = [[XEMobileSettingsViewController alloc] initWithNibName:@"XEMobileSettingsViewController" bundle:nil];
    [self.navigationController pushViewController:settingsVC animated:YES];
}
-(IBAction)pageManagementButtonPressed:(id)sender
{
    XEMobilePageManagementViewController *managePagesVC = [[XEMobilePageManagementViewController alloc] initWithNibName:@"XEMobilePageManagementViewController" bundle:nil];
    [self.navigationController pushViewController:managePagesVC animated:YES];
}
-(IBAction)menuManagementButtonPressed:(id)sender
{
    XEMobileMenuManagementViewController *menuManagementVC = [[XEMobileMenuManagementViewController alloc] initWithNibName:@"XEMobileMenuManagementViewController" bundle:nil];
    [self.navigationController pushViewController:menuManagementVC animated:YES];
}
-(IBAction)textyleButtonPressed:(id)sender
{
    XEMobileTextyleSelectViewController *textyleVC = [[XEMobileTextyleSelectViewController alloc] initWithNibName:@"XEMobileTextyleSelectViewController" bundle:nil];
    [self.navigationController pushViewController:textyleVC animated:YES];
}

@end
