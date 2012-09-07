//
//  XEMobileSkinCell.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 11/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileSkinCell.h"

//Custom UITableViewCell for XESkins

@implementation XEMobileSkinCell
@synthesize skinView = _skinView;
@synthesize label = _label;
@synthesize check = _check;

-(void)checkBox
{
    [self.check setTitle:@"X" forState:UIControlStateNormal];
}

-(void)uncheckBox
{
    [self.check setTitle:@"" forState:UIControlStateNormal];
}

@end
