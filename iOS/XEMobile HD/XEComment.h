//
//  XEComment.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 03/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEModule.h"

@interface XEComment : XEModule

@property (strong, nonatomic) NSString *commentSRL;
@property (strong, nonatomic) NSString *documentSRL;
@property (strong, nonatomic) NSString *isSecret;
@property (strong, nonatomic) NSString *content;
@property (strong, nonatomic) NSString *nickname;
@property (strong, nonatomic) NSString *emailAddress;
@property (strong, nonatomic) NSString *homePage;
@property (strong, nonatomic) NSString *ipAddress;
@property (strong, nonatomic) NSString *regdate;
@property (strong, nonatomic) NSString *parentSRL;
@property (strong, nonatomic) NSDate *data;

-(BOOL)isPrivate;

@end
