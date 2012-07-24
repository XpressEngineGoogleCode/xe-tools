//
//  XETextyleSettings.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 04/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XETextyleSettings.h"

@implementation XETextyleSettings

@synthesize blogTitle = _blogTitle;
@synthesize editor = _editor;
@synthesize prefix = _prefix;
@synthesize suffix = _suffix;
@synthesize usePrefix = _usePrefix;
@synthesize useSuffix = _useSuffix;
@synthesize fontSize = _fontSize;
@synthesize fontFamily = _fontFamily;

-(id)init
{
    self = [super init];
    if( self )
    {
        self.blogTitle = @" ";
        self.editor = @" ";
        self.prefix = @" ";
        self.suffix = @" ";
        self.useSuffix = @" ";
        self.usePrefix = @" ";
        self.fontSize = @" ";
        self.fontFamily = @" ";
    }

    return self;
}

-(void)setFontSize:(NSString *)fontSize
{
    if( fontSize == nil ) _fontSize = @" ";
    else _fontSize = fontSize;
}

@end
