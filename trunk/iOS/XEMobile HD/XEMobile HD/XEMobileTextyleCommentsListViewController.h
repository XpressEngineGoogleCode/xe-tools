//
//  XEMobileTextyleCommentsListViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XETextyle.h"
#import "XEMobileCommentsViewCell.h"
#import "XEMobileViewController.h"

// ViewController that contains a UITableView with comments
// The UITableViewCells in UITableView are XEMobileCommentsViewCell

@interface XEMobileTextyleCommentsListViewController : XEMobileViewController
<RKObjectLoaderDelegate, XEMobileCommentsViewCellProtocol, UITableViewDelegate, UITableViewDataSource, UIActionSheetDelegate>

@property (strong, nonatomic) XETextyle *textyle;
@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end