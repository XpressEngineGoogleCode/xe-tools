//
//  XEMobileOptionsTableViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "XESettings.h"

// Universal UITableViewController for option selected.
// three types:
// - langD = default language : single selection option
// - timeZ = timezone : single selection option
// - selectedLangs - selected languages : multiple selection option

// used when the user has to choose between multiple choices

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

