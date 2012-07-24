//
//  XEUser.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 28/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEUser.h"

@implementation XEUser

@synthesize username = _username;
@synthesize loggedIn = _loggedIn;
@synthesize loggedOut = _loggedOut;
@synthesize forum = _forum;
@synthesize textyle = textyle;
@synthesize auxVariable = _auxVariable;

-(BOOL)containsForumModule
{
    if( [self.forum isEqualToString:@"yes"] ) return YES;
    else return NO;
}
-(BOOL)containsTextyleModule
{
    if( [self.textyle isEqualToString:@"yes"] ) return YES;
    else return NO;
}

@end
