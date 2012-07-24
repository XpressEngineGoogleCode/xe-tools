//
//  XEMobileStatsCell.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 13/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface XEMobileStatsCell : UITableViewCell
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *dayLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *uniqueVisitorsLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *pageViewsLabel;

@end
