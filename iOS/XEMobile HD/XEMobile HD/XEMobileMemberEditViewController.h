//
//  XEMobileMemberEditViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 12/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEMobileViewController.h"
#import "RestKit/RestKit.h"
#import "XEMember.h"

// ViewController used to modify the settings of a member

@interface XEMobileMemberEditViewController : XEMobileViewController
<RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITextField *nicknameTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *emailLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *allowMailingSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *allowMessageSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *statusSegmentedBar;
@property (unsafe_unretained, nonatomic) IBOutlet UISwitch *isAdminSwitch;
@property (unsafe_unretained, nonatomic) IBOutlet UITextView *descriptionTextView;
@property (unsafe_unretained, nonatomic) IBOutlet UIScrollView *scrollView;

@property (strong, nonatomic) XEMember *member;

@end
