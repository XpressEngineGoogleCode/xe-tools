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

// View Controller that adds a Textyle

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
    
    //put a Done and a Cancel button on the navigation bar
    
    self.navigationItem.rightBarButtonItem = [[ UIBarButtonItem alloc ] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    
    // check if the Textyle is external (domanin name) or internal (site ID) 
    [self setTypeOfTextyle];
}

//method called when the textyleTypeSegmentControll changes his selected segment
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

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"Error!"];
}

//method called when an array with objects was mapped from response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *objectAux = object;
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

//method called when the Done button is pressed
-(void)doneButtonPressed
{
    NSString *addTextyleXML;
    if( self.textyleTypeSegmentControl.selectedSegmentIndex = 1)
    addTextyleXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_textyle]]></_filter>\n<access_type><![CDATA[vid]]></access_type>\n<site_id><![CDATA[%@]]></site_id>\n<user_id><![CDATA[%@]]></user_id>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleAdminCreate]]></act>\n</params>\n</methodCall>",self.idTextField.text,self.adminTextField.text];
    else 
        addTextyleXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_textyle]]></_filter>\n<access_type><![CDATA[domain]]></access_type>\n<domain><![CDATA[%@]]></domain>\n<user_id><![CDATA[vlad.bogdan@me.com]]></user_id>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleAdminCreate]]></act>\n</params>\n</methodCall>",self.idTextField.text,self.adminTextField.text];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[addTextyleXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
     }];
}

//method called when the Cancel button is pressed
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
