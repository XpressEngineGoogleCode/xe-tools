//
//  XEMobileCommentsViewCell.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileCommentsViewCell.h"

@interface XEMobileCommentsViewCell()
{
    BOOL cellIsModified;
}

@end

@implementation XEMobileCommentsViewCell
@synthesize nicknameLabel = _nicknameLabel;
@synthesize contentWebView = _contentWebView;
@synthesize visibilityButton = _visibilityButton;
@synthesize replyButton = _replyButton;
@synthesize deleteButton = _deleteButton;
@synthesize delegate = _delegate;
@synthesize elementsSuperView = _elementsSuperView;

-(void)awakeFromNib
{
    [self.replyButton addTarget:self action:@selector(replyButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    [self.deleteButton addTarget:self action:@selector(deleteButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    [self.visibilityButton addTarget:self action:@selector(visibilityButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    
}

-(void)setReplyComment
{
    if( cellIsModified == false )
    {
    CGRect frame = self.elementsSuperView.frame;
    self.elementsSuperView.frame = CGRectMake(frame.origin.x + 30, frame.origin.y, frame.size.width, frame.size.height);
        cellIsModified = true;
    }
}

-(void)setNormalComment
{
    self.elementsSuperView.frame = CGRectMake(11, 8, 269, 108);
    cellIsModified = false;
}

-(void)replyButtonPressed
{
    [self.delegate replyButtonPressedInCell:self];
}

-(void)deleteButtonPressed
    {
        [self.delegate deleteButtonPressedInCell:self];   
    }
     
-(void)visibilityButtonPressed
{
    [self.delegate visibilityButtonPressedInCell:self];
}

@end
