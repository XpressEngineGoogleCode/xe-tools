//
//  XEMobileTextyleAddPostViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "RestKit/RestKit.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEMobileViewController.h"

// ViewController used to add a new post to Textyle blog

@interface XEMobileTextyleAddPostViewController : XEMobileViewController
<RKObjectLoaderDelegate, UITextFieldDelegate,RKRequestDelegate, UITextViewDelegate>

@property (strong, nonatomic) XETextyle *textyle;
@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) IBOutlet UITextField *titleTextField;
@property (retain, nonatomic) IBOutlet UITextView *contentTextView;

-(IBAction)publishButtonPressed:(id)sender;
-(IBAction)saveButtonPressed:(id)sender;

@end