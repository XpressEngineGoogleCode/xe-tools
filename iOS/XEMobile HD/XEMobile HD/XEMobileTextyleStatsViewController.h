//
//  XEMobileTextyleStatsViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileViewController.h"
#import "XETextyleStats.h"
#import "RestKit/RestKit.h"
#import "XETextyle.h"

@interface XEMobileTextyleStatsViewController : XEMobileViewController
<UITableViewDataSource, UITableViewDelegate, RKObjectLoaderDelegate>
@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;
@property (strong, nonatomic) XETextyle *textyle;

@end
