//
//  XEMember.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 15/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface XEMember : NSObject

@property (strong, nonatomic) NSString *nickname;
@property (strong, nonatomic) NSString *member_srl;
@property (strong, nonatomic) NSString *denied;
@property (strong, nonatomic) NSString *user_id;
@property (strong, nonatomic) NSString *email;
@property (nonatomic, strong) NSString *allow_mailing;
@property (nonatomic, strong) NSString *allow_message;
@property (strong, nonatomic) NSString *password;
@property (strong, nonatomic) NSString *isAdmin;
@property (strong, nonatomic) NSString *description;
@property (strong, nonatomic) NSString *question;
@property (strong, nonatomic) NSString *secretAnswer;

-(BOOL)allowMessage;
-(BOOL)allowMailing;
-(BOOL)isApproved;
-(BOOL)admin;

@end
