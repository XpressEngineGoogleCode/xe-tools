//
//  XEMobileCommentsViewCell.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//


#import <UIKit/UIKit.h>

//Custom UITableViewCell for Comment section in Textyle 
//each cell has 3 button: reply, delete and visibility

@class XEMobileCommentsViewCell;
@protocol XEMobileCommentsViewCellProtocol

-(void)replyButtonPressedInCell:(XEMobileCommentsViewCell *)cell;
-(void)deleteButtonPressedInCell:(XEMobileCommentsViewCell *)cell;
-(void)visibilityButtonPressedInCell:(XEMobileCommentsViewCell *)cell;

@end

@interface XEMobileCommentsViewCell : UITableViewCell

@property (retain, nonatomic) IBOutlet UILabel *nicknameLabel;
@property (retain, nonatomic) IBOutlet UIWebView *contentWebView;
@property (retain, nonatomic) IBOutlet UIButton *visibilityButton;
@property (retain, nonatomic) IBOutlet UIButton *deleteButton;
@property (retain, nonatomic) IBOutlet UIButton *replyButton;
@property (strong, nonatomic) id<XEMobileCommentsViewCellProtocol> delegate;

@property (unsafe_unretained, nonatomic) IBOutlet UIView *elementsSuperView;

-(void)setReplyComment;
-(void)setNormalComment;

@end
