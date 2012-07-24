//
//  XEMobileTextyleEditPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 06/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "XETextylePage.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

@interface XEMobileTextyleEditPageViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITextViewDelegate>

@property (strong, nonatomic) XETextyle *textyle;
@property (strong, nonatomic) XETextylePage *page;
@property (retain, nonatomic) IBOutlet UITextField *nameTextField;
@property (retain, nonatomic) IBOutlet UITextView *contentTextView;
@property (retain, nonatomic) IBOutlet UILabel *labelForURL;
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) IBOutlet UIView *keyboardToolbar;

@end
