//
//  XEMobileAddPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// ViewController used to add a new Page to XE

@interface XEMobileAddPageViewController : XEMobileViewController
<RKRequestDelegate, UITextFieldDelegate, RKObjectLoaderDelegate, UIPickerViewDelegate, UIPickerViewDataSource>

@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *pageType;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *pageName;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *browserTitle;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *pageCache;
@property (unsafe_unretained, nonatomic) IBOutlet UIPickerView *layoutPickerView;


@end