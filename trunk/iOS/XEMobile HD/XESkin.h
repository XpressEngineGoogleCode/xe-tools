//
//  XESkin.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 09/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "RestKit/RestKit.h"
@interface XESkin : NSObject

@property (strong, nonatomic) NSString *id;
@property (strong, nonatomic) NSString *name;
@property (strong, nonatomic) NSString *description;
@property (strong, nonatomic) NSString *largeSS;
@property (strong, nonatomic) NSString *smallSS;
@property (strong, nonatomic) UIImage *image;

@end
