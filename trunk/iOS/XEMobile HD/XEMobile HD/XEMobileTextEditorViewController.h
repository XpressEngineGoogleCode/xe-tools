//
//  XEMobileTextEditorViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 12/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEMobileViewController.h"

// TextEditorViewController that gets pushed when the user wants 
// to edit the content of a page or post

@interface XEMobileTextEditorViewController : XEMobileViewController

@property (unsafe_unretained, nonatomic) IBOutlet UITextView *textView;
@property (unsafe_unretained, nonatomic) IBOutlet UIToolbar *keyboardToolbar;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *frameButton;

-(IBAction)bold:(id)sender;

-(IBAction)italic:(id)sender;

-(IBAction)underline:(id)sender;

-(IBAction)striked:(id)sender;

-(IBAction)li:(id)sender;

-(IBAction)ul:(id)sender;

-(NSString *)prepareStringForDisplay:(NSString *)string;

@end
