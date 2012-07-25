//
//  XEMobileTextyleSelectViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// ViewController that contains:
// - a TableView with a list of Textyle blogs
// - "add button" to create a new Textyle blog

//when a UITableViewCell is pressed XEMobileTextyleMainPageViewController is pushed

@interface XEMobileTextyleSelectViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end

