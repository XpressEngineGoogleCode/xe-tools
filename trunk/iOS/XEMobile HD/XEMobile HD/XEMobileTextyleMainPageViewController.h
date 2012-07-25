//
//  XEMobileTextyleMainPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"
#import "XETextyleStats.h"

// ViewController that represents the Dashboard of a Textyle blog
// It contains the 4 buttons and the statistics

@interface XEMobileTextyleMainPageViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITableViewDelegate, UITableViewDataSource>

- (IBAction)postsButtonPressed:(id)sender;

- (IBAction)commentsButtonPressed:(id)sender;

- (IBAction)settingsButtonPressed:(id)sender;

- (IBAction)pagesButtonPressed:(id)sender;



@property (strong, nonatomic) XETextyleStats *stats;
@property (strong, nonatomic) XETextyle *textyleItem;
@property (assign, nonatomic) IBOutlet UITableView *tableView;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *titleLabel;

@end