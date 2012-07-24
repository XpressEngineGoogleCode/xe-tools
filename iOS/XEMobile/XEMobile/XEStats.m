//
//  XEStats.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 13/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEStats.h"

@implementation XEStats
@synthesize uniqueVisitor = _uniqueVisitor;
@synthesize pageViews = _pageViews;
@synthesize date = _date;

-(void)setDate:(NSString *)date
{
    if( date.length == 8 )
    _date = [NSString stringWithFormat:@"%@/%@/%@", [date substringToIndex:4],[date substringWithRange:NSMakeRange(4, 2)],[date substringWithRange:NSMakeRange(6, 2)]];
    else _date = date;
        
}

@end
