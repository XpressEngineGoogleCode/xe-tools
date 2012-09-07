//
//  XEMobileAppDelegate.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEMobileLoginViewController.h"
#import "RestKit/RestKit.h"

@class XEMobileViewController;

@interface XEMobileAppDelegate : UIResponder <UIApplicationDelegate,RKRequestDelegate>

@property (strong, nonatomic) UIWindow *window;

@property (strong, nonatomic) XEMobileLoginViewController *viewController;

@end
