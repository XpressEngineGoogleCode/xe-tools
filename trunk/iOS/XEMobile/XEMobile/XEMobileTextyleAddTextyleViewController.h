//
//  XEMobileTextyleAddTextyleViewController.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileViewController.h"
#import "RestKit/RestKit.h"

//ViewController used to create a new Textyle blog

@interface XEMobileTextyleAddTextyleViewController : XEMobileViewController
<RKObjectLoaderDelegate>

@property (unsafe_unretained, nonatomic) IBOutlet UISegmentedControl *textyleTypeSegmentControl;
@property (unsafe_unretained, nonatomic) IBOutlet UILabel *idLabel;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *idTextField;
@property (unsafe_unretained, nonatomic) IBOutlet UITextField *adminTextField;

-(IBAction)segmentControlChanged:(id)sender;

@end
