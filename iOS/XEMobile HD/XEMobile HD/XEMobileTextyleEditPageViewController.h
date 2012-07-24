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
#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextyleEditPageViewController : XEMobileTextEditorViewController
<RKObjectLoaderDelegate, UITextViewDelegate>

@property (strong, nonatomic) XETextyle *textyle;
@property (strong, nonatomic) XETextylePage *page;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *nameTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *labelForURL;
@property (unsafe_unretained, nonatomic) IBOutlet UIScrollView *scrollView;

@end
