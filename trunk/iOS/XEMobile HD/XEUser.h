//
//  XEUser.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 28/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface XEUser : NSObject

@property (strong, nonatomic) NSString *loggedIn;
@property (strong, nonatomic) NSString *loggedOut;
@property (strong, nonatomic) NSString *username;
@property (strong, nonatomic) NSString *textyle;
@property (strong, nonatomic) NSString *forum;
@property NSString *auxVariable;

-(BOOL)containsForumModule;
-(BOOL)containsTextyleModule;

@end
