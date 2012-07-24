//
//  XEMobileTableViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

//FOUNDATION_EXPORT NSString *const isLogged;

@interface XEMobileTableViewController : UITableViewController

@property (strong, nonatomic) NSString *isLoggedOut;

-(void)pushLoginViewController;

@end
