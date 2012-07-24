//
//  XEMobileViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEMobileDetailViewController.h"

//FOUNDATION_EXPORT NSString *const isLogged;

@interface XEMobileViewController : UIViewController

@property (retain, nonatomic) IBOutlet UIActivityIndicatorView *indicator;
@property (strong, nonatomic, readonly) NSString *errorMessage;

@property (strong, nonatomic) XEMobileDetailViewController *detailViewController;

-(void)pushLoginViewController;
-(void)showErrorWithMessage:(NSString *)message;
-(NSString *)isLogged;
@end
