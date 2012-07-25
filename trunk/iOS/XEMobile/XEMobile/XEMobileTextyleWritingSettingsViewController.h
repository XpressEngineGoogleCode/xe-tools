//
//  XEMobileTextyleWritingSettingsViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 09/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XETextyleSettings.h"
#import "XETextyle.h"
#import "XEMobileViewController.h"

// SettingsViewController contains the following settings:
// - change editor
// - font family
// - font size
// - prefix 
// - suffix

@interface XEMobileTextyleWritingSettingsViewController : XEMobileViewController
<RKObjectLoaderDelegate, UIScrollViewDelegate, UITextFieldDelegate>

@property (assign, nonatomic) IBOutlet UISegmentedControl *editorSegmentedControl;
@property (assign, nonatomic) IBOutlet UITextField *fontFamilyTextField;
@property (assign, nonatomic) IBOutlet UITextField *fontSizeTextField;
@property (assign, nonatomic) IBOutlet UISwitch *prefixExistsSwitch;
@property (assign, nonatomic) IBOutlet UITextView *prefixTextView;
@property (assign, nonatomic) IBOutlet UISwitch *suffixExistsSwitch;
@property (assign, nonatomic) IBOutlet UITextView  *suffixTextView;
@property (assign, nonatomic) IBOutlet UIScrollView *scrollView;

@property (strong, nonatomic) XETextyle *textyle;
@property (strong, nonatomic) XETextyleSettings *textyleSettings;

@end
