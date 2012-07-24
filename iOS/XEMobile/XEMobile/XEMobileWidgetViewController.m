//
//  XEMobileWidgetViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 16/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileWidgetViewController.h"
#import "RestKit/RKRequestSerialization.h"

@interface XEMobileWidgetViewController ()

@end

@implementation XEMobileWidgetViewController
@synthesize webView = _webView;
@synthesize page = _page;
@synthesize button = _button;

-(void)viewDidLoad
{
    [super viewDidLoad];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:[NSString stringWithFormat:@"%@/index.php?mid=%@",[RKClient sharedClient].baseURL.absoluteString,self.page.mid]]];
 
    request.HTTPShouldHandleCookies = NO;
    [self.webView loadRequest:request];
}



@end
