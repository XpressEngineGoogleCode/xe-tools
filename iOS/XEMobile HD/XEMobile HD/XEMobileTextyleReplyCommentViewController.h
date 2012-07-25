//
//  XEMobileTextyleReplyCommentViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEComment.h"
#import "XETextyle.h"
#import "RestKit/RestKit.h"
#import "XEMobileViewController.h"

//ViewController that has a UITextView which is used to reply to a comment

@interface XEMobileTextyleReplyCommentViewController : XEMobileViewController
<RKRequestDelegate, RKObjectLoaderDelegate>

@property (strong, nonatomic) IBOutlet UITextView *contentTextView;
@property (strong, nonatomic) XEComment *comment;
@property (strong, nonatomic) XETextyle *textyle;

-(IBAction)doneButtonPressed;
-(IBAction)cancelButtonPressed;

@end