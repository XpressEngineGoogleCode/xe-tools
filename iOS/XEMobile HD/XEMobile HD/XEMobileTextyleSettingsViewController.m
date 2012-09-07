//
//  XEMobileTextyleSettingsViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleSettingsViewController.h"
#import "XEMobileOptionsTableViewController.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEUser.h"
#import "XEMobileTextyleChangePasswordViewController.h"

@interface XEMobileTextyleSettingsViewController ()

@end

@implementation XEMobileTextyleSettingsViewController

@synthesize settings = _settings;
@synthesize blogTitleTextField = _blogTitleTextField;
@synthesize languageButton = _languageButton;
@synthesize timezoneButton = _timezoneButton;
@synthesize textyle = _textyle;


- (void)viewDidLoad
{
    [super viewDidLoad];

    self.blogTitleTextField.text = self.settings.blogTitle;
    
    //add actions on Language and Timezone buttons
    
    [self.languageButton addTarget:self action:@selector(changeLanguageButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    [self.timezoneButton addTarget:self action:@selector(changeTimezoneButtonPressed) forControlEvents:UIControlEventTouchUpInside];
    
    //put a Done button on the navigation bar
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed:)];
    
    
    self.title = @"General Settings";
    
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //set the selected Language and Timezone on the button when the view controller appearrs
    [self.languageButton setTitle:self.settings.defaultLanguage forState:UIControlStateNormal];
    [self.timezoneButton setTitle:self.settings.timezone forState:UIControlStateNormal];
    
}

//method called when the Language button is pressed
-(IBAction)changeLanguageButtonPressed
{
    XEMobileOptionsTableViewController *optionsVC = [[XEMobileOptionsTableViewController alloc ]initWithNibName:@"XEMobileOptionsTableViewController" bundle:nil];
    optionsVC.type = langD;
    optionsVC.settings = self.settings;
    optionsVC.delegateData = [self.settings.languages allValues];
    optionsVC.selected = self.settings.defaultLanguage;
    
    [self.navigationController pushViewController:optionsVC animated:YES];
}

//method called when the Timezone button is pressed
-(IBAction)changeTimezoneButtonPressed
{
    XEMobileOptionsTableViewController *optionsVC = [[XEMobileOptionsTableViewController alloc] initWithNibName:@"XEMobileOptionsTableViewController" bundle:nil];
    optionsVC.type = timeZ;
    optionsVC.settings = self.settings;
    optionsVC.delegateData = [self.settings.timezones allValues];
    optionsVC.selected = [self.settings.timezones objectForKey:self.settings.timezone];

    
    [self.navigationController pushViewController:optionsVC animated:YES];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    self.textyle.defaultLanguage =[self.settings getKeyForLanguage:self.settings.defaultLanguage];
    self.textyle.timezone = self.settings.timezone;
    self.textyle.textyleTitle = self.blogTitleTextField.text;
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    NSLog(@"Error!");
    [self showErrorWithMessage:self.errorMessage];

}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{

}

//method called when the Done button is pressed
-(void)saveButtonPressed:(id)sender
{
    //prepare the request
    RKParams *params = [RKParams params];
    [params setValue:@"procTextyleInfoUpdate" forParam:@"act"];
    [params setValue:@"textyle" forParam:@"module"];
    [params setValue:@"textyle" forParam:@"mid"];
    [params setValue:self.textyle.domain forParam:@"vid"];
    [params setValue:self.blogTitleTextField.text forParam:@"textyle_title"];
    [params setValue:[self.settings getKeyForLanguage:self.settings.defaultLanguage] forParam:@"language"];
    [params setValue:self.settings.timezone forParam:@"timezone"];
    [params setValue:@"" forParam:@"delete_icon"];
    [params setValue:@"" forParam:@"textyle_content"];
    [params setValue:@"/xe2/index.php?mid=textyle&act=dispTextyleToolConfigInfo&vid=blog" forParam:@"error_return_url"];
    
    //send the response
    RKRequest *request = [[RKClient sharedClient] post:@"/" params:params delegate:self];
    request.userData = @"save_settings";
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    [self.indicator stopAnimating];
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController];
    
    if( [request.userData isEqualToString:@"save_settings"] )
        {
            [self.detailViewController.navigationController popViewControllerAnimated:YES];
        }
}

//method called when an object is mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
     [self.indicator stopAnimating];
    if( [objectLoader.userData isEqualToString:@"resetAllData"])
    {
        XEUser *aux = object;
        if( [aux.auxVariable isEqualToString:@"Resetting textyle is completed"] )
        {//ceva de completat
        }
    }
}

//method called when the Change Password button is pressed
-(IBAction)changePasswordButtonPressed:(id)sender
{
    XEMobileTextyleChangePasswordViewController *changePasswordVC = [[ XEMobileTextyleChangePasswordViewController alloc] initWithNibName:@"XEMobileTextyleChangePasswordViewController" bundle:nil];
    changePasswordVC.textyle = self.textyle;
    
    [self.navigationController pushViewController:changePasswordVC animated:YES];
}

//method called when the Reset All Data button is pressed
-(IBAction)resetAllDataButtonPressed:(id)sender
{
    //prepare the request to reset all data
    NSString *resetingXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<mid><![CDATA[textyle]]></mid>\n<vid><![CDATA[%@]]></vid>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleToolInit]]></act>\n</params>\n</methodCall>",self.textyle.domain];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData = @"resetAllData";
         loader.params = [RKRequestSerialization serializationWithData:[resetingXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
         loader.method = RKRequestMethodPOST;
     }];
    [self.indicator startAnimating];
}

//hide the keyboard when the Return button is pressed
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return NO;
}

@end
