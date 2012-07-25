//
//  XEMobileSettingsViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XEGlobalSettings.h"
#import "XEMobileViewController.h"

// ViewController used to modify the settings of XE

@interface XEMobileSettingsViewController: XEMobileViewController
<RKRequestDelegate, UITextFieldDelegate, UITextViewDelegate, RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UIScrollView *scrollView;
@property (unsafe_unretained, nonatomic) IBOutlet UITextView *adminAccesIPTextView;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *mobileTemplateSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *defaulURLTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *useSSLSegmentedControl;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *rewriteModeSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *enableSSOSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *sessionDBSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *qmailSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *HtmlDtdSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *defaultLanguageButton;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *localStandardTimeButton;

@property (strong, nonatomic) XEGlobalSettings *settings;

-(IBAction)changeDefaultLanguage;
-(IBAction)changeTimeZone;
-(IBAction)changeSelectedLanguages;
-(IBAction)rewriteModeSwitchChanged:(UISwitch *)sender;
-(IBAction)enableSSOSwitchChanged:(UISwitch *)sender;
-(IBAction)sessionDBSwitchChanged:(UISwitch *)sender;
-(IBAction)qmailSwitchChanged:(UISwitch *)sender;
-(IBAction)htmlDtdSwitch:(UISwitch *)sender;
-(IBAction)mobileTemplateSwitchChanged:(UISwitch *)sender;
-(IBAction)useSSLSegmentedControlChange:(UISegmentedControl *)sender;

@end