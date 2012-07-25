//
//  XEMobileEditContentPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEPage.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

// ViewController used to edit the content of a XE's Page

@interface XEMobileEditContentPageViewController : XEMobileViewController
<RKRequestDelegate, UITextViewDelegate,RKObjectLoaderDelegate, UITextFieldDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UITextField *titleTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UITextView *contentTextView;
@property (unsafe_unretained, nonatomic) IBOutlet UIScrollView *scrollView;

@property (strong, nonatomic) XEPage *page;

-(IBAction)saveButtonPressed:(id)sender;

@end