//
//  XEMobileStatsCell.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 13/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileStatsCell.h"

@implementation XEMobileStatsCell
@synthesize dayLabel;
@synthesize uniqueVisitorsLabel;
@synthesize pageViewsLabel;


- (id)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier
{
    self = [super initWithStyle:style reuseIdentifier:reuseIdentifier];
    if (self) 
    {
        // Initialization code
    }
    return self;
}


- (void)setSelected:(BOOL)selected animated:(BOOL)animated
{
    [super setSelected:selected animated:animated];

    // Configure the view for the selected state
}

@end
