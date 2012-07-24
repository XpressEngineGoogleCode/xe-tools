//
//  XEMobileWidgetViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 16/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XEPage.h"
#import "RestKit/RestKit.h"

@interface XEMobileWidgetViewController : UIViewController
<UIWebViewDelegate>

@property (unsafe_unretained,nonatomic) IBOutlet UIWebView *webView;
@property (strong, nonatomic) XEPage *page;
@property (unsafe_unretained, nonatomic) IBOutlet UIButton *button;

@end
