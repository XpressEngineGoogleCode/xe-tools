//
//  XEPage.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 19/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEModule.h"

@interface XEPage : XEModule

@property (strong, nonatomic) NSString *module;
@property (strong, nonatomic) NSString *pageType;
@property (strong, nonatomic) NSString *document_srl;
@property (strong, nonatomic) NSString *content;
@property (strong, nonatomic) NSString *browserTitle;
@property (strong, nonatomic) NSString *layoutSRL;

@end
