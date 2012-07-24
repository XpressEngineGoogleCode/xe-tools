//
//  XEMobileViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileViewController.h"
#import "XEMobileLoginViewController.h"
#import "RestKit/RestKit.h"

@interface XEMobileViewController ()

@end

@implementation XEMobileViewController
@synthesize indicator = _indicator;
@synthesize errorMessage = _errorMessage;

-(NSString *)errorMessage
{
    return @"Error!";
}

-(NSString *)isLogged
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

-(void)showErrorWithMessage:(NSString *)message
{
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Error!"
                                                    message:message
                                                 delegate:nil
                                        cancelButtonTitle:@"OK"
                                        otherButtonTitles:nil];
    [self.indicator stopAnimating];
    [alert show];
}

@end
