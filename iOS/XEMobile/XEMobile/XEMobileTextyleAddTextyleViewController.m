//
//  XEMobileTextyleAddTextyleViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleAddTextyleViewController.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEUser.h"

@interface XEMobileTextyleAddTextyleViewController ()

@end

@implementation XEMobileTextyleAddTextyleViewController
@synthesize adminTextField = _adminTextField;
@synthesize idLabel = _idLabel;
@synthesize idTextField = _idTextField;
@synthesize textyleTypeSegmentControl = _textyleTypeSegmentControl;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //add a done button to the navigation bar and set an action to it
    self.navigationItem.rightBarButtonItem = [[ UIBarButtonItem alloc ] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    //add a cancel button to the navigation bar and set an action to it
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
 
    //modifies the idLabel value
    [self setTypeOfTextyle];
}

//method called when the textyleTypeSegmentControl selection changed
-(IBAction)segmentControlChanged:(id)sender
{
    [self setTypeOfTextyle];
}


-(void)setTypeOfTextyle
{
    if( self.textyleTypeSegmentControl.selectedSegmentIndex == 1 ) 
        self.idLabel.text = [NSString stringWithFormat:@"%@/",[RKClient sharedClient].baseURL.absoluteString];
    else self.idLabel.text = @"http://";
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    //an error occured
}

//an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"Error!"];
}

//method called when the object was loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    //verify what kind of object it is
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *objectAux = object;
        //check the confirmation that we received
        if( [ objectAux.auxVariable isEqualToString:@"Textyle is created"] )
        {
            [ self.navigationController popViewControllerAnimated:YES ];
        }
        else if ( [objectAux.auxVariable isEqualToString:@"You do not have permission to execute requested action."] )
        {
            [self pushLoginViewController];
        }
        else if( [objectAux.auxVariable isEqualToString:@"No one matches."] )
        {
            [self showErrorWithMessage:@"No administrator with that email!"];
        }
    }
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    
}

//method called when the done button is pressed
-(void)doneButtonPressed
{
    // build the request
    NSString *addTextyleXML;
    if( self.textyleTypeSegmentControl.selectedSegmentIndex = 1)
    addTextyleXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_textyle]]></_filter>\n<access_type><![CDATA[vid]]></access_type>\n<site_id><![CDATA[%@]]></site_id>\n<user_id><![CDATA[%@]]></user_id>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleAdminCreate]]></act>\n</params>\n</methodCall>",self.idTextField.text,self.adminTextField.text];
    else 
        addTextyleXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_textyle]]></_filter>\n<access_type><![CDATA[domain]]></access_type>\n<domain><![CDATA[%@]]></domain>\n<user_id><![CDATA[vlad.bogdan@me.com]]></user_id>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleAdminCreate]]></act>\n</params>\n</methodCall>",self.idTextField.text,self.adminTextField.text];
    
    //map the response to an object
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[addTextyleXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
     }];
}

-(void)cancelButtonPressed
{
    [self.navigationController popViewControllerAnimated:YES];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.textyleTypeSegmentControl = nil;
    self.idLabel = nil;
    self.idTextField = nil;
    self.adminTextField = nil;
}

@end
