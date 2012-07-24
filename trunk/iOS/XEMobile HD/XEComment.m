//
//  XEComment.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 03/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEComment.h"

@implementation XEComment

@synthesize ipAddress = _ipAddress;
@synthesize content = _content;
@synthesize documentSRL = _documentSRL;
@synthesize emailAddress = _emailAddress;
@synthesize homePage = _homePage;
@synthesize commentSRL = _commentSRL;
@synthesize isSecret = _isSecret;
@synthesize nickname = _nickname;
@synthesize regdate = _regdate;
@synthesize parentSRL = _parentSRL;
@synthesize data = _data;

-(void)setRegdate:(NSString *)regdate
{
    NSDateFormatter *dateFormat = [[NSDateFormatter alloc] init];
    [dateFormat setDateFormat:@"yyyyMMddHHmmss"];
    NSDate *date = [dateFormat dateFromString:regdate];
    dateFormat.locale = [NSLocale currentLocale];
    dateFormat.timeZone = [NSTimeZone localTimeZone];
    dateFormat.timeStyle = NSDateFormatterShortStyle;
    dateFormat.dateStyle = NSDateFormatterShortStyle;    
    dateFormat.locale = [NSLocale currentLocale];
    self.data  = date;
    _regdate = regdate;
}

-(BOOL)isPrivate
{
    if( [self.isSecret isEqualToString:@"Y"] ) return YES;
        else return NO;
}

@end
