//
//  XEMobileTextyleSkinsViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 09/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// ViewController that contains a UITableView with Textyle Skins

@interface XEMobileTextyleSkinsViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITableViewDelegate, UITableViewDataSource>

@property (strong, nonatomic) XETextyle *textyle;
@property (unsafe_unretained, nonatomic) IBOutlet UITableView *tableView;

@end
