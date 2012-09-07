//
//  XEMobileTextyleReplyCommentViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleReplyCommentViewController.h"
#import "XEUser.h"
#import "RestKit/RKRequestSerialization.h"

@interface XEMobileTextyleReplyCommentViewController ()

@end

@implementation XEMobileTextyleReplyCommentViewController

@synthesize contentTextView = _contentTextView;
@synthesize comment = _comment;
@synthesize textyle = _textyle;


- (void)viewDidLoad
{
    [super viewDidLoad];
    
    // add a Cancel button to the navigation bar and sets an action to it
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    
    // add a Done button to the navigation bar and sets an action to it
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

//method called when the a respons came
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController]; 
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    
}

//method called when an object was loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(XEUser *)object
{
    if( [object.auxVariable isEqualToString:@"Registered successfully."] )
    {
        [self.navigationController dismissModalViewControllerAnimated:YES];
    }
}

// method called when the Cancel button in navigation bar is pressed
-(IBAction)cancelButtonPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

// method called when the Done button in navigation bar is pressed
-(IBAction)doneButtonPressed
{
    //prepare the request
    NSString *postCommentXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_comment]]></_filter>\n<act><![CDATA[procTextyleInsertComment]]></act>\n<vid><![CDATA[%@]]></vid>\n<mid><![CDATA[textyle]]></mid>\n<document_srl><![CDATA[%@]]></document_srl>\n<content><![CDATA[<p>%@</p>]]></content>\n<parent_srl><![CDATA[%@]]></parent_srl>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.textyle.domain,self.comment.documentSRL,self.contentTextView.text,self.comment.commentSRL];
    
    //map the response to an object
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    // send request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php"usingBlock:^(RKObjectLoader *loader)
     {
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[postCommentXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML ];
         loader.delegate = self;
         
     } ];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

@end
