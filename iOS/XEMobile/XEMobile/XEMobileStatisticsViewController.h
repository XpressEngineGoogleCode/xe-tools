//
//  XEMobileStatisticsViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 13/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileViewController.h"
#import "XEStats.h"
#import "RestKit/RestKit.h"

@interface XEMobileStatisticsViewController : XEMobileViewController
<UITableViewDelegate, UITableViewDataSource, RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end
