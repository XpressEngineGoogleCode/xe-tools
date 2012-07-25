//
//  XEMobileMenuItemViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"
#import "XEMenu.h"

// ViewController that contains a UITableView with all the MenuItems in the "parentMenu"


@interface XEMobileMenuItemViewController : XEMobileViewController
<RKRequestDelegate,RKObjectLoaderDelegate, UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>

@property (strong, nonatomic) NSArray *arrayWithMenuItems;
@property (strong, nonatomic) XEMenu *parentMenu;
@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end