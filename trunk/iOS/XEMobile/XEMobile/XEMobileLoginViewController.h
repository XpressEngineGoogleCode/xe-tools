//
//  XEMobileViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// Login View Controller:
// - pushed when the application starts
// - pushed when the user is logged out

@interface XEMobileLoginViewController : XEMobileViewController
<UITextFieldDelegate,RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITextField *addressField;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *usernameField;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *passwordField;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *errorLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *rememberSwitch;
@property (strong, nonatomic) NSData *deviceToken;

-(IBAction)loginButtonPressed;

@end