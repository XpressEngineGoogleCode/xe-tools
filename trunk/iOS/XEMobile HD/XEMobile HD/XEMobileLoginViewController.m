//
//  XEMobileViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileLoginViewController.h"
#import "XEUser.h"
#import "XEMobileMainPageViewController.h"

@interface XEMobileLoginViewController ()

@property (strong, nonatomic) RKObjectManager *client;
@property (strong, nonatomic) NSString *link;
@end

@implementation XEMobileLoginViewController
@synthesize client = _client;
@synthesize link = _link;
@synthesize addressField = _addressField;
@synthesize usernameField = _usernameField;
@synthesize passwordField = _passwordField;
@synthesize indicator = _indicator;
@synthesize errorLabel = _errorLabel;
@synthesize rememberSwitch = _rememberSwitch;
@synthesize loginElements = _loginElements;

-(IBAction)loginButtonPressed
{
    NSString *userName = self.usernameField.text;
    NSString *password = self.passwordField.text;
    NSString *address = self.addressField.text;
    if(userName != nil && password != nil && address != nil)
    {
        //the server address is saved
        [[NSUserDefaults standardUserDefaults] setValue:address forKey:@"last_address"];
        
        self.client = [RKObjectManager objectManagerWithBaseURLString:[NSString stringWithFormat:@"http://%@",address]];
        [RKObjectManager setSharedManager:self.client];
        
        //request parameters
        NSDictionary *parametr;
        //if remember option is selected
        if( self.rememberSwitch.on )
            parametr = [[NSDictionary alloc] 
                                  initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationLogin",userName,password,@"Y",nil] 
                                  forKeys:[NSArray arrayWithObjects:@"module",@"act",@"user_id",@"password",@"remember", nil]];
        // if remember option isn't selected
        else 
            parametr = [[NSDictionary alloc] 
                        initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationLogin",userName,password,@"N",nil] 
                        forKeys:[NSArray arrayWithObjects:@"module",@"act",@"user_id",@"password",@"remember", nil]];
        
        //point the received xml to a class
        RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
        [mapping mapKeyPath:@"value" toAttribute:@"loggedIn"];
        [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"]; 
        
        [self checkRememberOptionWithUsername:userName andWithPassword:password];
        
        //build the request url
        NSString *resourcePath = [@"/index.php" stringByAppendingQueryParameters:parametr];
        
        //send the request to server
        [[RKObjectManager sharedManager] loadObjectsAtResourcePath:resourcePath delegate:self];
        
        [self.indicator startAnimating];
    }
    else
    {
        self.errorLabel.text = @"Incorrect password!";
        [self.indicator stopAnimating];
    }
}

//remember username and password if remember switch is on

-(void)checkRememberOptionWithUsername:(NSString *)username andWithPassword:(NSString *)password
{
    if( self.rememberSwitch.on )
            {
                [[NSUserDefaults standardUserDefaults] setValue:username forKey:@"username"];
                [[NSUserDefaults standardUserDefaults] setValue:password forKey:@"password"];
            }
    else {
        [[NSUserDefaults standardUserDefaults] setValue:nil forKey:@"username"];
        [[NSUserDefaults standardUserDefaults] setValue:nil forKey:@"password"];
        }
}

//called when the response is received and the response object is created
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    XEUser *user = object;
    
    if( [user.loggedIn isEqualToString:@"true"] )
    {
        XEMobileMainPageViewController *mainPage = [[XEMobileMainPageViewController alloc] initWithNibName:@"XEMobileMainPageViewController" bundle:nil];
        mainPage.navigationItem.hidesBackButton = YES;
        
        [[UIApplication sharedApplication] registerForRemoteNotificationTypes:
         (UIRemoteNotificationTypeBadge | UIRemoteNotificationTypeSound | UIRemoteNotificationTypeAlert)];
        
        mainPage.detailViewController = self.detailViewController;
        [self.navigationController pushViewController:mainPage animated:YES];
    }
    else self.errorLabel.text = @"Incorrect password!";
}


- (void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{

}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"Error!"];
}


-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return NO;
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    NSString *rememberedUsername = [[NSUserDefaults standardUserDefaults] objectForKey:@"username"];
    NSString *rememberedPassword = [[NSUserDefaults standardUserDefaults] objectForKey:@"password"];
    NSString *rememberedAddress = [[NSUserDefaults standardUserDefaults] objectForKey:@"last_address"];
    
    [self setPortaitLayout];
    
    if( rememberedPassword && rememberedUsername )
    {
        self.addressField.text = rememberedAddress;
        self.passwordField.text = rememberedPassword;
        self.usernameField.text = rememberedUsername;
    }
    
    self.passwordField.secureTextEntry = YES;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    [RKObjectManager sharedManager].serializationMIMEType = RKMIMETypeXML;

    
    self.navigationItem.title = @"XpressEngine";
    
    self.navigationItem.hidesBackButton = YES;
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.indicator = nil;
    self.addressField = nil;
    self.usernameField = nil;
    self.passwordField = nil;
    self.errorLabel = nil;
    self.rememberSwitch = nil;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return YES;
}

-(void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    if( toInterfaceOrientation == UIDeviceOrientationLandscapeLeft
       || toInterfaceOrientation == UIDeviceOrientationLandscapeRight)
    {
        [self setLandscapeLayout];
    }
    else if ( toInterfaceOrientation == UIDeviceOrientationPortrait )
    {
        [self setPortaitLayout];
    }
}

-(void)setLandscapeLayout
{
    self.loginElements.frame = CGRectMake(0, 10, 320 , 217);
}

-(void)setPortaitLayout
{
    self.loginElements.frame = CGRectMake(0, 254, 320, 217);
}

@end

