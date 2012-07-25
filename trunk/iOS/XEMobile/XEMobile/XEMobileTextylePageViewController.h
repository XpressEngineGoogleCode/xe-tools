//
//  XEMobileTextylePageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 06/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XETextyle.h"
#import "XETextylePage.h"
#import "XEMobileViewController.h"

// ViewController that contains a UITableView with all the XETextylePage

@interface XEMobileTextylePageViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>

@property (strong, nonatomic) XETextyle *textyle;
@property (assign, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) NSArray *arrayWithPages;

@end
