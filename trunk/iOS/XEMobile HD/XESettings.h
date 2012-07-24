//
//  XESettings.h
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 22/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import <Foundation/Foundation.h>


@interface XESettings : NSObject

@property (strong, nonatomic) NSMutableArray *selectedLanguages;
@property (strong, nonatomic) NSString *selectedLangString;
@property (strong, nonatomic) NSString *defaultLanguage;
@property (strong, nonatomic) NSString *timezone;
@property (strong, nonatomic) NSDictionary *languages;
@property (strong, nonatomic) NSDictionary *timezones;

-(NSString *)getKeyForLanguage:(NSString *)lang;



@end
