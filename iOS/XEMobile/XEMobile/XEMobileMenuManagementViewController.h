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

@interface XEMobileMenuManagementViewController : XEMobileViewController
<UITableViewDataSource, UITableViewDelegate,RKObjectLoaderDelegate, UIActionSheetDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end
