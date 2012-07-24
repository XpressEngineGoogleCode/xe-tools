//
//  XEGlobalSettings.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 04/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEGlobalSettings.h"

@implementation XEGlobalSettings

@synthesize useSSL = _useSSL;
@synthesize useSSO = _useSSO;
@synthesize dbSession = _dbSession;
@synthesize adminIPs = _adminIPs;
@synthesize qmail = _qmail;
@synthesize mobile = _mobile;
@synthesize defaultURL = _defaultURL;
@synthesize html5 = _html5;
@synthesize rewriteMode = _rewriteMode;

-(BOOL)useMobileView
{
    if ([self.mobile isEqualToString:@"Y"]) return YES;
    else return NO;
}
-(BOOL)useRewrite
{
    if ([self.rewriteMode isEqualToString:@"Y"]) return YES;
    else return NO;
}
-(BOOL)usesso
{
    if ([self.useSSO isEqualToString:@"Y"]) return YES;
    else return NO;
}
-(BOOL)useDBSession
{
    if ([self.dbSession isEqualToString:@"Y"]) return YES;
    else return NO;
}
-(BOOL)qmailCompatibility
{
    if ([self.qmail isEqualToString:@"Y"]) return YES;
    else return NO;
}
-(BOOL)useHTML5
{
    if ([self.html5 isEqualToString:@"Y"]) return YES;
    else return NO;
}

@end
