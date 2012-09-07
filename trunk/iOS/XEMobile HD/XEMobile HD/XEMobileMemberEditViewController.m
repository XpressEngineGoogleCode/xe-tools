//
//  XEMobileMemberEditViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 12/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileMemberEditViewController.h"
#import "XEUser.h"

@interface XEMobileMemberEditViewController ()

@end

@implementation XEMobileMemberEditViewController
@synthesize nicknameTextField;
@synthesize emailLabel;
@synthesize allowMailingSwitch;
@synthesize allowMessageSwitch;
@synthesize statusSegmentedBar;
@synthesize isAdminSwitch;
@synthesize descriptionTextView;
@synthesize scrollView;
@synthesize member = _member;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = self.member.nickname;
    
    //put a Done and a Cancel button on the navigation bar
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    
    self.scrollView.contentSize = CGSizeMake(320, 850);
}

-(void)viewWillAppear:(BOOL)animated 
{
    [super viewWillAppear:animated];
    
    [self loadInterface];
}

//load the UI elements with the current configuration settings
-(void)loadInterface
{
    self.emailLabel.text = self.member.email;
    self.nicknameTextField.text = self.member.nickname;
    self.descriptionTextView.text= self.member.description;
    
    self.allowMessageSwitch.on = [self.member allowMessage];
    self.allowMailingSwitch.on = [self.member allowMailing];
    self.isAdminSwitch.on = [self.member admin];
    
    if( [self.member isApproved] ) self.statusSegmentedBar.selectedSegmentIndex = 0;
    else self.statusSegmentedBar.selectedSegmentIndex = 1;
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{

}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *user = object;
        if( [user.auxVariable isEqualToString:@"true"] )
        {
            [self.navigationController popViewControllerAnimated:YES];
        }
        else [self showErrorWithMessage:@"An error occurred"];
    }
}

//method called when the Done button is pressed
-(void)doneButtonPressed
{
    RKParams *params = [RKParams params];
    
    //prepare and send request to save the edited member
    
    [params setValue:@"insertAdminMember" forParam:@"ruleset"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:@"procmobile_communicationEditMember" forParam:@"act"];
    [params setValue:self.member.member_srl forParam:@"member_srl"];
    [params setValue:self.member.email forParam:@"email_address"];
    [params setValue:self.member.password forParam:@"password"];
    [params setValue:self.nicknameTextField.text forParam:@"nick_name"];
    [params setValue:self.descriptionTextView.text forParam:@"description"];
    [params setValue:self.member.secretAnswer forParam:@"find_account_answer"];
    [params setValue:self.member.question forParam:@"find_account_question"];
    
    if( self.isAdminSwitch.on ) [params setValue:@"Y" forParam:@"is_admin"];
                    else [params setValue:@"N" forParam:@"is_admin"];

    [params setValue:@"" forParam:@"mid"];
    [params setValue:@"" forParam:@"vid"];
    if( self.allowMailingSwitch.on ) [params setValue:@"Y" forParam:@"allow_mailing"];
                    else [ params setValue:@"N" forParam:@"allow_mailing" ];
    
    if( self.allowMessageSwitch.on ) [params setValue:@"Y" forParam:@"allow_message"];
                    else [params setValue:@"N" forParam:@"allow_message"];
    if( self.statusSegmentedBar.selectedSegmentIndex == 0 )[params setValue:@"N" forParam:@"denied"];
                    else [params setValue:@"Y" forParam:@"denied"];
    
    
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"value" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //send request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/" usingBlock:^(RKObjectLoader *loader)
    {
        loader.method = RKRequestMethodPOST;
        loader.params = params;
        loader.delegate = self;
        loader.userData = @"save_member";
    }];
    
    [self.indicator startAnimating];
}

//method called when the Cancel button is pressed
-(void)cancelButtonPressed
{
    [self.navigationController popViewControllerAnimated:YES];
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    [self.indicator stopAnimating];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

- (void)viewDidUnload
{
    [self setNicknameTextField:nil];
    [self setEmailLabel:nil];
    [self setAllowMailingSwitch:nil];
    [self setAllowMessageSwitch:nil];
    [self setStatusSegmentedBar:nil];
    [self setIsAdminSwitch:nil];
    [self setDescriptionTextView:nil];
    [self setScrollView:nil];
    
    [super viewDidUnload];
}

@end
