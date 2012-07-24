//
//  XEMobileAddMenuItemViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XEMenuItem.h"
#import "XEMobileViewController.h"

@interface XEMobileAddMenuItemViewController : XEMobileViewController
<UIPickerViewDelegate, UIPickerViewDataSource, RKRequestDelegate, UITextFieldDelegate, RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITextField *browserTitleField;
@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *moduleSegment;
@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *openNewWindowSegment;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *createModuleIDField;
@property (unsafe_unretained, nonatomic) IBOutlet UIPickerView *selectModuleIDPicker;
@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *articleWidgetExternalSegment;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *labelForErrors;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *createOrSelectLabel;

@property (strong, nonatomic) NSString *parentModuleSRL;

@property (strong, nonatomic) XEMenuItem *editedMenu;

-(IBAction)moduleSegmentChanged:(UISegmentedControl *)sender;

@end