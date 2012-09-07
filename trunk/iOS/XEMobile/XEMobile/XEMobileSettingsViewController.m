//
//  XEMobileSettingsViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

//
//  XEMobileSettingsViewControllerViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 22/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileSettingsViewController.h"
#import "XEMobileOptionsTableViewController.h"

@implementation XEMobileSettingsViewController

@synthesize scrollView = _scrollView;
@synthesize qmailSwitch = _qmailSwitch;
@synthesize sessionDBSwitch = _sessionDBSwitch;
@synthesize useSSLSegmentedControl = _useSSLSegmentedControl;
@synthesize enableSSOSwitch = _enableSSOSwitch;
@synthesize rewriteModeSwitch = _rewriteModeSwitch;
@synthesize adminAccesIPTextView = _adminAccesIPTextView;
@synthesize HtmlDtdSwitch = _HtmlDtdSwitch;
@synthesize defaulURLTextField = _defaulURLTextField;
@synthesize mobileTemplateSwitch = _mobileTemplateSwitch;
@synthesize settings = _settings;
@synthesize localStandardTimeButton = _localStandardTimeButton;
@synthesize defaultLanguageButton = _defaultLanguageButton;


-(void)viewDidLoad
{
    [super viewDidLoad];
    
    //map the response to an object
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEGlobalSettings class]];
    
    [mapping mapKeyPath:@"langs" toAttribute:@"selectedLangString"];
    [mapping mapKeyPath:@"default_lang" toAttribute:@"defaultLanguage"];
    [mapping mapKeyPath:@"timezone" toAttribute:@"timezone"];
    [mapping mapKeyPath:@"mobile" toAttribute:@"mobile"];
    [mapping mapKeyPath:@"ips" toAttribute:@"adminIPs"];
    [mapping mapKeyPath:@"default_url" toAttribute:@"defaultURL"];
    [mapping mapKeyPath:@"use_ssl" toAttribute:@"useSSL"];
    [mapping mapKeyPath:@"rewrite_mode" toAttribute:@"rewriteMode"];
    [mapping mapKeyPath:@"use_sso" toAttribute:@"useSSO"];
    [mapping mapKeyPath:@"db_session" toAttribute:@"dbSession"];
    [mapping mapKeyPath:@"qmail" toAttribute:@"qmail"];
    [mapping mapKeyPath:@"html5" toAttribute:@"html5"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //send the request to load the current settings configuration
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationLoadSettings" usingBlock:^(RKObjectLoader *loader)
     {
         loader.userData = @"load";
         loader.delegate = self;
     }];
    [self.indicator startAnimating];
    
    //put a Done button in navigation bar
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed)];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];    
    
    self.navigationItem.title = @"Settings";
    self.scrollView.contentSize = CGSizeMake(320, 1100);
    
    // load the current settings configuration
    [self loadSettings];
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
    
    if ( [request.userData isEqualToString:@"set_settings"] )
    {
        [self.indicator stopAnimating];
        [self.navigationController popViewControllerAnimated:YES];
    }
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error 
{
    NSLog(@"Error!");
}

//method called when the Language button is pressed
-(IBAction)changeDefaultLanguage
{
    XEMobileOptionsTableViewController *optionsVC = [[ XEMobileOptionsTableViewController alloc] initWithNibName:@"XEMobileOptionsTableViewController" bundle:nil];
    optionsVC.settings = self.settings;
    optionsVC.delegateData = self.settings.selectedLanguages;
    optionsVC.selected = self.settings.defaultLanguage;
    optionsVC.type = langD;
    [self.navigationController pushViewController:optionsVC animated:YES];
}

//method called when the Timezone button is pressed
-(IBAction)changeTimeZone
{
    XEMobileOptionsTableViewController *optionTVC = [[XEMobileOptionsTableViewController alloc] initWithNibName:@"XEMobileOptionsTableViewController" bundle:nil];
    optionTVC.settings = self.settings;
    optionTVC.delegateData = [self.settings.timezones allValues];
    optionTVC.selected = [self.settings.timezones objectForKey:self.settings.timezone];
    optionTVC.type = timeZ;
    [self.navigationController pushViewController:optionTVC animated:YES];
}

//method called when the Selected Languages button is pressed
-(IBAction)changeSelectedLanguages
{
    XEMobileOptionsTableViewController *optionTVC = [[XEMobileOptionsTableViewController alloc] initWithNibName:@"XEMobileOptionsTableViewController" bundle:nil];
    optionTVC.settings = self.settings;
    optionTVC.delegateData = [self.settings.languages allValues];
    optionTVC.selected = self.settings.selectedLanguages;
    optionTVC.type = selectedLangs;
    [self.navigationController pushViewController:optionTVC animated:YES];
}


-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
}

//set up the UI with the current settings configuration
-(void) loadSettings
{
    self.defaulURLTextField.text = self.settings.defaultURL;
    [self.HtmlDtdSwitch setOn:[self.settings useHTML5]];
    [self.mobileTemplateSwitch setOn:[self.settings useMobileView]];
    [self.rewriteModeSwitch setOn:[self.settings useRewrite]];
    [self.enableSSOSwitch setOn:[self.settings usesso]];
    [self.sessionDBSwitch setOn:[self.settings useDBSession]];
    [self.qmailSwitch setOn:[self.settings qmailCompatibility]];
    
    self.defaulURLTextField.text = self.settings.defaultURL;
    
    if( [self.settings.useSSL isEqualToString:@"none"] ) self.useSSLSegmentedControl.selectedSegmentIndex = 0;
    else if( [self.settings.useSSL isEqualToString:@"optional"] ) self.useSSLSegmentedControl.selectedSegmentIndex = 1;
    else if( [self.settings.useSSL isEqualToString:@"always"] ) self.useSSLSegmentedControl.selectedSegmentIndex= 2;
    
    self.adminAccesIPTextView.text = self.settings.adminIPs;
    
    NSLog(@"self.default language but %@ default language = %@",self.defaultLanguageButton,self.settings.defaultLanguage);
    [self.defaultLanguageButton setTitle:self.settings.defaultLanguage forState:UIControlStateNormal];
    [self.localStandardTimeButton setTitle:[NSString stringWithFormat:@"GMT %@",self.settings.timezone] forState:UIControlStateNormal];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];

}

//
// methods called when the switches or segmentcontrollers change their selected option
//

-(IBAction)useSSLSegmentedControlChange:(UISegmentedControl *)sender
{
    switch (sender.selectedSegmentIndex) 
    {
        case 0:
            self.settings.useSSL = @"none";
            break;
        case 1:
            self.settings.useSSL = @"optional";
            
        case 2:
            self.settings.useSSL = @"always";
            break;
    }
}

-(IBAction)rewriteModeSwitchChanged:(UISwitch *)sender
{
    if(sender.on) self.settings.rewriteMode = @"Y";
    else self.settings.rewriteMode = @"N";
}
-(IBAction)enableSSOSwitchChanged:(UISwitch *)sender
{
    if(sender.on) self.settings.useSSO = @"Y";
    else self.settings.useSSO = @"N";
}
-(IBAction)sessionDBSwitchChanged:(UISwitch *)sender
{
    if(sender.on) self.settings.dbSession = @"Y";
    else self.settings.dbSession = @"N";
}
-(IBAction)qmailSwitchChanged:(UISwitch *)sender
{
    if(sender.on) self.settings.qmail = @"Y";
    else self.settings.qmail = @"N";
}
-(IBAction)htmlDtdSwitch:(UISwitch *)sender
{
    if(sender.on) self.settings.html5 = @"Y";
    else self.settings.html5 = @"N";
}

-(IBAction)mobileTemplateSwitchChanged:(UISwitch *)sender
{
    if(sender.on) self.settings.mobile = @"Y";
    else self.settings.mobile = @"N";
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [objectLoader.userData isEqualToString:@"load"] )
    {
    self.settings = object;
    [self.indicator stopAnimating];
    [self loadSettings];
    }
}

// keyboard hides when the Return button is pressed
-(BOOL)textFieldShouldReturn:(UITextField *)textField 
{
    [textField resignFirstResponder];
    return NO;
}

//method called when the Done button is pressed
-(void)saveButtonPressed
{
    [self.indicator startAnimating];
    
    //prepare the request
    
    RKParams *params = [RKParams params];
    [params setValue:@"install" forParam:@"module"];
    [params setValue:@"procInstallAdminConfig" forParam:@"act"];
    
    NSString *ips = self.adminAccesIPTextView.text;
    ips = [ips stringByReplacingOccurrencesOfString:@" " withString:@""];
    ips = [ips stringByReplacingOccurrencesOfString:@"," withString:@"\n"];
    
    [params setValue:ips forParam:@"admin_ip_list"];
    
    for(NSString *string in self.settings.selectedLanguages )
    {
        NSString *langKey = [self.settings getKeyForLanguage:string];
        [params setValue:langKey forParam:@"selected_lang[]"];
    }
    
    [params setValue:[self.settings getKeyForLanguage:self.settings.defaultLanguage] forParam:@"change_lang_type"];
    [params setValue:self.settings.timezone forParam:@"time_zone"];
    [params setValue:self.settings.mobile forParam:@"use_mobile_view"];
    
    // check if defaultURL is null or empty string and report error
    if( self.defaulURLTextField.text == nil || [self.defaulURLTextField.text isEqualToString:@""] )
    {
        [self showErrorWithMessage:@"Error!"];
        return;
    }
    
    [params setValue:self.defaulURLTextField.text forParam:@"default_url"];
    
    //check value for use_ssl
    if( self.useSSLSegmentedControl.selectedSegmentIndex == 0 )
                                [params setValue:@"none" forParam:@"use_ssl"];
    else if( self.useSSLSegmentedControl.selectedSegmentIndex == 1 )
                                [params setValue:@"optional" forParam:@"use_ssl"];
    else if( self.useSSLSegmentedControl.selectedSegmentIndex == 2)
                                [params setValue:@"always" forParam:@"use_ssl"];
    
    
    [params setValue:self.settings.rewriteMode forParam:@"use_rewrite"];
    [params setValue:self.settings.useSSO forParam:@"use_sso"];
    [params setValue:self.settings.dbSession forParam:@"use_db_session"];
    [params setValue:self.settings.qmail forParam:@"qmail_compatibility"];
    [params setValue:self.settings.html5 forParam:@"use_html5"];
    
    //send the request
    RKRequest * request = [[RKClient sharedClient] post:@"/index.php?module=admin&act=dispAdminConfigGeneral" params:params delegate:self];
    request.userData = @"set_settings";
    
}

- (BOOL)textView:(UITextView *)textView shouldChangeTextInRange:(NSRange)range replacementText:(NSString *)text 
{
    if([text isEqualToString:@"\n"]) 
    {
        [textView resignFirstResponder];
        return NO;
    }
    return YES;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.scrollView = nil;
    self.adminAccesIPTextView = nil;
    self.mobileTemplateSwitch = nil;
    self.defaulURLTextField = nil;
    self.useSSLSegmentedControl = nil;
    self.rewriteModeSwitch = nil;
    self.enableSSOSwitch = nil;
    self.sessionDBSwitch = nil;
    self.qmailSwitch = nil;
    self.HtmlDtdSwitch = nil;
    self.defaultLanguageButton = nil;
    self.localStandardTimeButton = nil;
}

@end

