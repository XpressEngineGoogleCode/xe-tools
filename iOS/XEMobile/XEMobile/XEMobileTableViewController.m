//
//  XEMobileTableViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTableViewController.h"
#import "XEMobileLoginViewController.h"

@interface XEMobileTableViewController ()

@end

@implementation XEMobileTableViewController
@synthesize isLoggedOut = _isLoggedOut;

-(NSString *)isLoggedOut
{
    return @"logout_error!";
}


-(void)pushLoginViewController
{
    [self performSelector:@selector(delayedPush) withObject:self afterDelay:1];
}

-(void)delayedPush
{
    XEMobileLoginViewController *loginVC = [[XEMobileLoginViewController alloc] initWithNibName:@"XEMobileLoginViewController" bundle:nil];
    [self.navigationController pushViewController:loginVC animated:YES];

}

@end
