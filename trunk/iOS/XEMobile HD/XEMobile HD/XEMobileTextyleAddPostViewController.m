//
//  XEMobileTextyleAddPostViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleAddPostViewController.h"
#import "XEUser.h"
#import "XETextylePost.h"
#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextyleAddPostViewController ()

@property (strong, nonatomic) NSString *documentSRL;
//variabile that tells us if the post must be saved or publish
@property BOOL publish;

@end

@implementation XEMobileTextyleAddPostViewController
@synthesize textyle = _textyle;
@synthesize titleTextField = _titleTextField;
@synthesize documentSRL = _documentSRL;
@synthesize publish = _publish;

//method called when the Publish button is pressed
-(IBAction)publishButtonPressed:(id)sender
{
    [self actionForSave];
    self.publish = YES;
}

//method called when the Save button is pressed
-(IBAction)saveButtonPressed:(id)sender
{
    self.publish = NO;
    [self actionForSave];
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    //put a Cancel button on the navigation bar
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

//method that sends a request to save the page
-(void)actionForSave
{
    
    //prepare the request
    NSString *content = @"<p>";
    content = [content stringByAppendingFormat:@"%@</p>",self.textView.text];
    content = [content stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"];
    
    
    NSString *saveXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n<vid><![CDATA[%@]]></vid>\n<publish><![CDATA[N]]></publish>\n<_filter><![CDATA[save_post]]></_filter>\n<mid><![CDATA[textyle]]></mid>\n<title><![CDATA[%@]]></title>\n<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n<content><![CDATA[%@]]></content>\n<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it? The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.textyle.domain,self.titleTextField.text,content ];
    
    // send the request
    [self sendStringRequestToServer:saveXML withUserData:@"save"];
}

//method called when an object was mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(XETextylePost *)object
{
    self.documentSRL = object.documentSRL;
    [self.indicator stopAnimating];
    
    //to publish a post, we must first save it
    // then we obtain the document srl of the saved post and we publish it
    
    if( [objectLoader.userData isEqualToString:@"published"] )
        {
            [self.navigationController dismissModalViewControllerAnimated:YES];
        }
    //check if the post must be published or just saved
    if( self.publish ) 
    {
        NSString *publishXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[publish_post]]></_filter>\n<act><![CDATA[procTextylePostPublish]]></act>\n<document_srl><![CDATA[%@]]></document_srl>\n<mid><![CDATA[textyle]]></mid>\n<vid><![CDATA[%@]]></vid>\n<trackback_charset><![CDATA[UTF-8]]></trackback_charset>\n<use_alias><![CDATA[N]]></use_alias>\n<allow_comment><![CDATA[Y]]></allow_comment>\n<allow_trackback><![CDATA[Y]]></allow_trackback>\n<subscription><![CDATA[N]]></subscription>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.documentSRL,self.textyle.domain];
        
        [self sendStringRequestToServer:publishXML withUserData:@"published"];
        self.publish = NO;
    }
    else 
        {
        [self.navigationController dismissModalViewControllerAnimated:YES];
        }
}

//method that sends a request
-(void)sendStringRequestToServer:(NSString *)request withUserData:(NSString *)userData
{
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XETextylePost class]];
    
    [mapping mapKeyPath:@"document_srl" toAttribute:@"documentSRL"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[request 
                                                                        dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
         loader.delegate = self;
         loader.userData = userData;
     }];
    [self.indicator startAnimating];
}

//method called when the Cancel button is pressed
-(void)cancelButtonPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.titleTextField = nil;
    self.textView = nil;
}



-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}


@end
