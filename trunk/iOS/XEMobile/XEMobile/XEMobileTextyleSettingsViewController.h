//
//  XEMobileTextyleSettingsViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyleSettings.h"
#import "XETextyle.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

@interface XEMobileTextyleSettingsViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITextFieldDelegate>

@property (strong, nonatomic) XETextyleSettings *settings;
@property (strong, nonatomic) XETextyle *textyle;

@property (retain, nonatomic) IBOutlet UITextField *blogTitleTextField;
@property (retain, nonatomic) IBOutlet UIButton *languageButton;
@property (retain, nonatomic) IBOutlet UIButton *timezoneButton;

-(IBAction)saveButtonPressed:(id)sender;
-(IBAction)changeLanguageButtonPressed;
-(IBAction)changeTimezoneButtonPressed;
-(IBAction)changePasswordButtonPressed:(id)sender;
-(IBAction)resetAllDataButtonPressed:(id)sender;

@end
