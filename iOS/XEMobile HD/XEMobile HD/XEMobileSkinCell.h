//
//  XEMobileSkinCell.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 11/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface XEMobileSkinCell : UITableViewCell

@property (assign, nonatomic) IBOutlet UIImageView *skinView;
@property (assign, nonatomic) IBOutlet UILabel *label;
@property (assign, nonatomic) IBOutlet UIButton *check;

-(void)checkBox;
-(void)uncheckBox;

@end
