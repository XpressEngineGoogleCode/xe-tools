//
//  XEMobileEditPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 19/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEPage.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// ViewController used to modify the settings of a XE's Page

@interface XEMobileEditPageViewController : XEMobileViewController
<RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UILabel *pageTypeLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *moduleNameTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *browserTitleTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UIPickerView *layoutPickerView;

@property (strong, nonatomic) XEPage *page;

@end
