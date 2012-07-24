//
//  XEGlobalSettings.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 04/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XESettings.h"

@interface XEGlobalSettings : XESettings

@property (strong, nonatomic) NSString *mobile;
@property (strong, nonatomic) NSString *adminIPs;
@property (strong, nonatomic) NSString *defaultURL;
@property (strong, nonatomic) NSString *useSSL;
@property (strong, nonatomic) NSString *rewriteMode;
@property (strong, nonatomic) NSString *useSSO;
@property (strong, nonatomic) NSString *dbSession;
@property (strong, nonatomic) NSString *qmail;
@property (strong, nonatomic) NSString *html5;

-(BOOL)useMobileView;
-(BOOL)useRewrite;
-(BOOL)usesso;
-(BOOL)useDBSession;
-(BOOL)qmailCompatibility;
-(BOOL)useHTML5;

@end
