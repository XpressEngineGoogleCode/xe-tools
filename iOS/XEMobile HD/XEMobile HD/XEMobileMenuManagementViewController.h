//
//  XEMobileMenuManagementViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// ViewController that contains a UITableView with all the XE's Menu

@interface XEMobileMenuManagementViewController : XEMobileViewController
<UITableViewDataSource, UITableViewDelegate,RKObjectLoaderDelegate, UIActionSheetDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end
