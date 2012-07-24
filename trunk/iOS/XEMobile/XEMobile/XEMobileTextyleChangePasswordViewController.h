//
//  XEMobileTextyleChangePasswordViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "XEMobileViewController.h"
#import "RestKit/RestKit.h"

@interface XEMobileTextyleChangePasswordViewController : XEMobileViewController
<RKObjectLoaderDelegate>

@property (retain, nonatomic) IBOutlet UITextField *changedPasswordTextField;
@property (retain, nonatomic) IBOutlet UITextField *currentPasswordTextField;
@property (retain, nonatomic) IBOutlet UITextField *retypePasswordTextField;

@property (strong, nonatomic) IBOutlet XETextyle *textyle;

@end
