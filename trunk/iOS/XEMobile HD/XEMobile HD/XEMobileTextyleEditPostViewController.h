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

@interface XEMobileTextyleEditPostViewController : XEMobileTextEditorViewController
<RKObjectLoaderDelegate, UIActionSheetDelegate, UITextViewDelegate>

@property (strong, nonatomic) XETextylePost *post;
@property (strong, nonatomic) XETextyle *textyle;

@property (unsafe_unretained, nonatomic) IBOutlet UITextField *titleTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UIBarButtonItem *deleteButton;

-(IBAction)deleteButtonPressed:(id)sender;
-(IBAction)publishButtonPressed:(id)sender;
-(IBAction)saveButtonPressed:(id)sender;


@end