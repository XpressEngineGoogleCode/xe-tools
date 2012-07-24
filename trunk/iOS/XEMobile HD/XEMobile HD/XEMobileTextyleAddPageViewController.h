//
//  XEMobileTextyleAddPageViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 06/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XETextyle.h"
#import "XEUser.h"
#import "RestKit/RestKit.h"
#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextyleAddPageViewController : XEMobileTextEditorViewController
<RKObjectLoaderDelegate, UITextViewDelegate>
@property (strong, nonatomic) XETextyle *textyle;

@property (retain, nonatomic) IBOutlet UITextField *urlTextField;
@property (retain, nonatomic) IBOutlet UITextField *nameTextField;
@property (retain, nonatomic) IBOutlet UILabel *labelForURL;

@end
