//
//  XESettings.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 22/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XESettings.h"

@implementation XESettings

@synthesize selectedLanguages = _selectedLanguages;

@synthesize timezone = _timezone;

@synthesize defaultLanguage = _defaultLanguage;
@synthesize languages = _languages;
@synthesize timezones = _timezones;
@synthesize selectedLangString = _selectedLangString;

-(id)init
{
    self = [super init];
    if(self)
    {
        NSLog(@"se apeleaza init");
        self.languages = [[NSDictionary alloc] initWithObjects:
                          [NSArray arrayWithObjects:@"English",@"한국어",@"日本語",@"中文(中国)",@"中文(臺灣)",@"Français",@"Deutsch",@"Русский",@"Español",@"Türkçe",@"Tiếng Việt",@"Mongolian",nil] forKeys:[NSArray arrayWithObjects:@"en",@"ko",@"jp",@"zh-CN",@"zh-TW",@"fr",@"de",@"ru",@"es",@"tr",@"vi",@"mn", nil]];
        
      
        
        self.timezones = [[NSDictionary alloc] initWithObjects:[NSArray arrayWithObjects:@"GMT -12:00",@"GMT -11:00",@"GMT -10:00",@"GMT -09:00",@"GMT -08:00",@"GMT -07:00",@"GMT -06:00",@"GMT -05:00",@"GMT -04:00",@"GMT -03:00",@"GMT -02:00",@"GMT -01:00",@"GMT 00:00",@"GMT +01:00",@"GMT +02:00",@"GMT +03:00",@"GMT +04:00",@"GMT +05:00",@"GMT +06:00",@"GMT +07:00",@"GMT +08:00",@"GMT +09:00",@"GMT +10:00",@"GMT +11:00",@"GMT +12:00",@"GMT +13:00",@"GMT +14:00", nil] forKeys:[NSArray arrayWithObjects:@"-1200",@"-1100",@"-1000",@"-0900",@"-0800",@"-0700",@"-0600",@"-500",@"-0400",@"-0300",@"-0200",@"-0100",@"0000",@"+0100",@"+0200",@"+0300",@"+0400",@"+0500",@"+0600",@"+0700",@"+0800",@"+0900",@"+1000",@"+1100",@"+1200",@"+1300",@"+1400", nil]];
        
        //self.timezones =  [[NSArray alloc] initWithObjects:@"-1200",@"-1100",@"-1000",@"-0900",@"-0800",@"-0700",@"-0600",@"-500",@"-0400",@"-0300",@"-0200",@"-0100",@"0000",@"+0100",@"+0200",@"+0300",@"+0400",@"+0500",@"+0600",@"+0700",@"+0800",@"+0900",@"+1000",@"+1100",@"+1200",@"+1300",@"+1400", nil];
    } 
    return self;
}

-(void)setSelectedLangString:(NSString *)selectedLangString
{
    NSArray *arrayWithKeyLangs = [selectedLangString componentsSeparatedByString:@":"];
    
    for(NSString *obj in arrayWithKeyLangs)
    {
        if( ![obj isEqualToString:@""] )  
        [self addSelectedLanguage:obj];
    }
    
    _selectedLangString = selectedLangString;
}

-(NSMutableArray *)selectedLanguages 
{
    if ( _selectedLanguages == nil ) _selectedLanguages = [[NSMutableArray alloc] init ];
    return _selectedLanguages;
}

-(void)addSelectedLanguage:(NSString*)lang
{
    NSLog(@"%@",lang);
    [self.selectedLanguages addObject:[self.languages objectForKey:lang]];
}

-(NSString *)defaultLanguage
{
    NSString *string = _defaultLanguage;
    
    return [self.languages objectForKey:string];
}

-(NSString *)getKeyForLanguage:(NSString *)lang
{
    return [[self.languages allKeysForObject:lang] objectAtIndex:0];
}



@end
