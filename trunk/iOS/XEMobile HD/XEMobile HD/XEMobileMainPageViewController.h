//
//  XEMobileMainPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 14/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Restkit/Restkit.h"
#import "XEMobileViewController.h"
#import "XEMobileDetailViewController.h"

@interface XEMobileMainPageViewController : XEMobileViewController
<RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UIButton *textyleButton;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *titleLabel;

@property (strong, nonatomic) XEMobileDetailViewController *detailViewController;

-(IBAction)logoutButtonPressed:(id)sender;
-(IBAction)membersButtonPressed:(id)sender;
-(IBAction)globalSettingsButtonPressed:(id)sender;
-(IBAction)pageManagementButtonPressed:(id)sender;
-(IBAction)menuManagementButtonPressed:(id)sender;
-(IBAction)textyleButtonPressed:(id)sender;
-(IBAction)statsButtonPressed:(id)sender;

@end
