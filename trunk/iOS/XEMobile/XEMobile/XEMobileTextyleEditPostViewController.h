//
//  XEMobileTextyleEditPostViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextylePost.h"
#import "RestKit/RestKit.h"
#import "XETextyle.h"
#import "XEMobileViewController.h"
#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextyleEditPostViewController : XEMobileViewController
<RKObjectLoaderDelegate, UIActionSheetDelegate, UITextViewDelegate>

@property (strong, nonatomic) XETextylePost *post;
@property (strong, nonatomic) XETextyle *textyle;

@property (retain, nonatomic) IBOutlet UIScrollView *scrollView;
@property (retain, nonatomic) IBOutlet UITextField *titleTextField;
@property (retain, nonatomic) IBOutlet UITextView *contentTextView;
@property (retain, nonatomic) IBOutlet UIBarButtonItem *deleteButton;

-(IBAction)deleteButtonPressed:(id)sender;
-(IBAction)publishButtonPressed:(id)sender;
-(IBAction)saveButtonPressed:(id)sender;


@end