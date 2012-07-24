//
//  XEMobileTextEditorViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 12/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface XEMobileTextEditorViewController : UIViewController

@property (unsafe_unretained, nonatomic) IBOutlet UITextView *textView;
@property (unsafe_unretained, nonatomic) IBOutlet UIToolbar *keyboardToolbar;

@property (unsafe_unretained, nonatomic) UITextView *field;

-(IBAction)bold:(id)sender;

-(IBAction)italic:(id)sender;

-(IBAction)underline:(id)sender;

-(IBAction)done:(id)sender;

-(IBAction)striked:(id)sender;

-(IBAction)li:(id)sender;

-(IBAction)ul:(id)sender;

-(NSString *)prepareStringForDisplay:(NSString *)string;

@end
