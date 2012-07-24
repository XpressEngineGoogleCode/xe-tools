//
//  XEMobileTextylePostsTableViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "RestKit/RestKit.h"
#import "XETextylePost.h"
#import "XEMobileViewController.h"

@interface XEMobileTextylePostsTableViewController : XEMobileViewController
<UITableViewDelegate,UITableViewDelegate, RKObjectLoaderDelegate>

@property (strong, nonatomic) XETextyle *textyleItem;
@property (retain, nonatomic) IBOutlet UISegmentedControl *selectPublishOrSave;
@property (retain, nonatomic) IBOutlet UITableView *tableView;

-(IBAction)segmentedControlChanged:(UISegmentedControl *)sender;

@end