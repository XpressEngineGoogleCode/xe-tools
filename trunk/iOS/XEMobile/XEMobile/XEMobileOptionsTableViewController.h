//
//  XEMobileOptionsTableViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XESettings.h"


typedef enum
{
    langD,
    timeZ,
    selectedLangs,
}typeOfOptionsTVC;

@interface XEMobileOptionsTableViewController : UITableViewController

@property (strong, nonatomic) XESettings *settings;
@property (strong, nonatomic) NSArray *delegateData;
@property (strong, nonatomic) id selected;
@property typeOfOptionsTVC type;

@end

