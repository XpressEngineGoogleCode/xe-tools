//
//  XESkin.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 09/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XESkin.h"

@implementation XESkin

@synthesize smallSS = _smallSS;
@synthesize largeSS = _largeSS;
@synthesize name = _name;
@synthesize description = _description;
@synthesize id = _id;
@synthesize image = _image;

-(void)setSmallSS:(NSString *)smallSS
{
    NSMutableArray *pathParts = [[smallSS pathComponents] mutableCopy];
    
    NSMutableArray *auxParts = [[NSMutableArray alloc] init ];
    
    for(NSString *pathPart in pathParts)
    {
        if( [pathPart isEqualToString:@"modules"] ) break;
            else [auxParts addObject:pathPart];
    }
    
    for( NSString *string in auxParts )
    {
        [pathParts removeObject:string];
    }
    NSString *relativePath = [NSString pathWithComponents:pathParts];
    NSString *url = [RKClient sharedClient].baseURL.absoluteString;
    
    NSString *finalPath = [NSString stringWithFormat:@"%@/%@",url,relativePath];
    
    self.image = [UIImage imageWithData:[NSData dataWithContentsOfURL:[NSURL URLWithString:finalPath]]];
    
    _smallSS = finalPath;
}

@end
