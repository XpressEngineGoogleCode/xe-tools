//
//  XEMobileTextyleEditPostViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//


#import "XEMobileTextyleEditPostViewController.h"
#import <RestKit/RKRequestSerialization.h>
#import "XEUser.h"

@interface XEMobileTextyleEditPostViewController ()

@property BOOL publish;

@end

@implementation XEMobileTextyleEditPostViewController
@synthesize post = _post;
@synthesize textyle = _textyle;
@synthesize titleTextField = _titleTextField;

@synthesize publish = _publish;
@synthesize deleteButton = _deleteButton;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    //adjust view
    self.titleTextField.text = self.post.title;
    
    self.navigationItem.title = self.post.title;
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];

    [self loadPostContentAndTitle];
}

-(void)cancelButtonPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

-(void)loadPostContentAndTitle
{
    RKRequest *load = [[RKClient sharedClient] get:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationContentForPost&module_srl=%@&document_srl=%@",self.textyle.moduleSrl,self.post.documentSRL] 
                                          delegate:self];
    load.userData = @"load";
    [self.indicator startAnimating];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}


-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(XEUser *)object
{
    [self.indicator stopAnimating];
    if( [objectLoader.userData isEqualToString:@"delete"] )
    {
        if( [object.auxVariable isEqualToString:@"Moved to Recycle Bin."] )
        {
            [self.navigationController dismissModalViewControllerAnimated:YES];
        }
    }
    else 
        if( [object.auxVariable isEqualToString:@"Saved successfully."] && self.publish != YES)
        {
            [self.navigationController dismissModalViewControllerAnimated:YES];
        }
        else
        {
            NSLog(@"saved response!");
        }
    
    if ( [objectLoader.userData isEqualToString:@"publish"] )
    {
        if ( [object.auxVariable isEqualToString:@"success"] )
        {
            NSLog(@"publish response!");
            [self.navigationController dismissModalViewControllerAnimated:YES];
        }
    }
}

-(IBAction)saveButtonPressed:(id)sender
{
    self.publish = NO;
    [self actionForSave];
}

-(void)actionForSave
{
    NSString *contentForSave = @"<p>";
    contentForSave = [contentForSave stringByAppendingFormat:@"%@</p>",self.textView.text];
    contentForSave = [contentForSave stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"];
    
    
    NSString *saveXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall><params>\n<act><![CDATA[procTextylePostsave]]></act>\n<vid><![CDATA[%@]]></vid>\n<publish><![CDATA[%@]]></publish>\n<_filter><![CDATA[save_post]]></_filter>\n<mid><![CDATA[textyle]]></mid>\n<title><![CDATA[%@]]></title>\n<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n<content><![CDATA[%@]]></content>\n<document_srl><![CDATA[%@]]></document_srl>\n<editor_sequence><![CDATA[%@]]></editor_sequence>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>\n",self.textyle.domain,@"N",self.titleTextField.text,contentForSave,self.post.documentSRL,self.post.documentSRL];
    
    [self sendStringRequestToServer:saveXML withUserData:@"save"];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    [self.indicator stopAnimating];
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
    if( [request.userData isEqualToString:@"load"] )
    {
        if( response.bodyAsString.length >= 7 )
            //the response comes with "<p> ... </p>" tags. 
            //the tags are removed
            self.textView.text = [response.bodyAsString substringWithRange:NSMakeRange(3, response.bodyAsString.length-7)];
            self.textView.text = [self prepareStringForDisplay:self.textView.text]; 
    }
}

-(IBAction)deleteButtonPressed:(id)sender
{
    UIActionSheet *action = [[ UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles:nil];
    [action showInView:self.view];
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if( buttonIndex == 0 )
    {
        NSString *deleteXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<document_srl><![CDATA[%@]]></document_srl>\n<page><![CDATA[1]]></page>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextylePostTrash]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",self.post.documentSRL,self.textyle.domain];
        
        [self sendStringRequestToServer:deleteXML withUserData:@"delete"];
    }
}

-(IBAction)publishButtonPressed:(id)sender
{
    self.publish = YES;
    
    [self actionForSave];
    
    NSString *publishXML;
    
    publishXML= [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall><params><_filter><![CDATA[publish_post]]></_filter><act><![CDATA[procTextylePostPublish]]></act><document_srl><![CDATA[%@]]></document_srl><mid><![CDATA[textyle]]></mid><vid><![CDATA[%@]]></vid><category_srl><![CDATA[%@]]></category_srl><trackback_charset><![CDATA[UTF-8]]></trackback_charset><use_alias><![CDATA[N]]></use_alias><allow_comment><![CDATA[Y]]></allow_comment><allow_trackback><![CDATA[Y]]></allow_trackback><subscription><![CDATA[N]]></subscription><module><![CDATA[textyle]]></module></params></methodCall>",self.post.documentSRL,self.textyle.domain,self.post.categorySRL];
    
    [self sendStringRequestToServer:publishXML withUserData:@"publish"];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    
    self.titleTextField = nil;
    self.textView = nil;
    self.deleteButton = nil;
}



-(void)sendStringRequestToServer:(NSString *)request withUserData:(NSString *)userData
{
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
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


@end
