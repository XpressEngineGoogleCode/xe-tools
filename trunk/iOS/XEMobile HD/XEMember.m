//
//  XEMember.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 15/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMember.h"

@implementation XEMember
@synthesize user_id = _user_id;
@synthesize nickname = _nickname;
@synthesize email = _email;
@synthesize allow_mailing = _allow_mailing;
@synthesize allow_message =_allow_message;
@synthesize member_srl = _member_srl;
@synthesize denied = _denied;
@synthesize password = _password;
@synthesize description = _description;
@synthesize question = _question;
@synthesize secretAnswer = _secretAnswer;
@synthesize isAdmin = _isAdmin;

-(void)setUser_id:(NSString *)user_id
{
    if ( ![user_id isEqualToString:@""]) _user_id = user_id;
}

-(void)setNickname:(NSString *)nickname
{
    if ( ![nickname isEqualToString:@""] ) _nickname = nickname;
}

-(void)setEmail:(NSString *)email
{
    if( ![email isEqualToString:@""] ) _email = email;
}

-(id)init
{
    self = [ super init];
    if(self)
    {
        self.user_id = @"empty";
        self.nickname = @"empty";
        self.email = @"empty";
        self.allow_mailing = NO;
        self.allow_message = NO;
    }
    return self;
}

-(BOOL)allowMessage
{
    if( [self.allow_message isEqualToString:@"Y"]) return YES;
    else return NO;
}
-(BOOL)allowMailing
{
    if( [self.allow_mailing isEqualToString:@"Y"] ) return YES;
    else return NO;
}

-(BOOL)isApproved
{
    if( [self.denied isEqualToString:@"N"] ) return YES;
    else return NO;
}

-(BOOL)admin
{
    if( [self.isAdmin isEqualToString:@"Y"] ) return YES;
    else return NO;
}

@end
