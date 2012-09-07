//
//  XEMobileTextyleChangePasswordViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleChangePasswordViewController.h"
#import "RestKit/RKRequestSerialization.h"

@interface XEMobileTextyleChangePasswordViewController ()

@end

@implementation XEMobileTextyleChangePasswordViewController
@synthesize retypePasswordTextField = _retypePasswordTextField;
@synthesize currentPasswordTextField = _currentPasswordTextField;
@synthesize changedPasswordTextField = _changedPasswordTextField;
@synthesize textyle = _textyle;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    // add a cancel and a done button to the navigation bar and sets actions to them
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
}

//method called when the cancel button is pressed
-(void)cancelButtonPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

//method called when a response came
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    NSLog(@"%@",response.bodyAsString);
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

//method called
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{

}

//method called when an object is loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{

}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when the done button is pressed
-(void)doneButtonPressed
{
    //preparing the request
        NSString *changePasswordXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[modify_password]]></_filter>\n<act><![CDATA[procMemberModifyPassword]]></act>\n<vid><![CDATA[%@]]></vid>\n<mid><![CDATA[textyle]]></mid>\n<current_password><![CDATA[%@]]></current_password>\n<password><![CDATA[%@]]></password>\n<password2><![CDATA[%@]]></password2>\n<module><![CDATA[member]]></module>\n</params>\n</methodCall>",self.textyle.domain,self.currentPasswordTextField.text,self.changedPasswordTextField.text,self.retypePasswordTextField.text];
    
    //send request
    [[RKClient sharedClient] post:@"/index.php" params:[RKRequestSerialization serializationWithData:[changePasswordXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML] delegate:self];
}

@end
