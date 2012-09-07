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
@property BOOL publish;

@end

@implementation XEMobileTextyleAddPostViewController
@synthesize scrollView = _scrollView;
@synthesize textyle = _textyle;
@synthesize titleTextField = _titleTextField;
@synthesize contentTextView = _contentTextView;
@synthesize documentSRL = _documentSRL;
@synthesize publish = _publish;


-(IBAction)publishButtonPressed:(id)sender
{
    [self actionForSave];
    self.publish = YES;
    
}
-(IBAction)saveButtonPressed:(id)sender
{
    self.publish = NO;
    [self actionForSave];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}


-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}


-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}


-(void)actionForSave
{
    NSString *content = @"<p>";
    content = [content stringByAppendingFormat:@"%@</p>",self.contentTextView.text];
    content = [content stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"];
    
    
    NSString *saveXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<act><![CDATA[procTextylePostsave]]></act>\n<vid><![CDATA[%@]]></vid>\n<publish><![CDATA[N]]></publish>\n<_filter><![CDATA[save_post]]></_filter>\n<mid><![CDATA[textyle]]></mid>\n<title><![CDATA[%@]]></title>\n<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n<content><![CDATA[%@]]></content>\n<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it? The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.textyle.domain,self.titleTextField.text,content ];
    
    [self sendStringRequestToServer:saveXML withUserData:@"save"];
}


//to publish a post, firstly the post is saved, the server returns the document SRL of the new post created,
//and then a publish request is send
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(XETextylePost *)object
{
    self.documentSRL = object.documentSRL;
    [self.indicator stopAnimating];
    
    if( [objectLoader.userData isEqualToString:@"published"] )
        {
            [self.navigationController popViewControllerAnimated:YES];
        }
    
    if( self.publish ) 
    {
        NSString *publishXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[publish_post]]></_filter>\n<act><![CDATA[procTextylePostPublish]]></act>\n<document_srl><![CDATA[%@]]></document_srl>\n<mid><![CDATA[textyle]]></mid>\n<vid><![CDATA[%@]]></vid>\n<trackback_charset><![CDATA[UTF-8]]></trackback_charset>\n<use_alias><![CDATA[N]]></use_alias>\n<allow_comment><![CDATA[Y]]></allow_comment>\n<allow_trackback><![CDATA[Y]]></allow_trackback>\n<subscription><![CDATA[N]]></subscription>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.documentSRL,self.textyle.domain];
        
        [self sendStringRequestToServer:publishXML withUserData:@"published"];
        self.publish = NO;
    }
    else 
        {
        [self.navigationController popViewControllerAnimated:YES];
        }
}

//method to send a request
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

- (void)viewDidLoad
{
    [super viewDidLoad];
	
    self.scrollView.contentSize = CGSizeMake(320, 800);
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.scrollView = nil;
    self.titleTextField = nil;
    self.contentTextView = nil;
}

//when contentTextView is pressed TextEditorViewController is pushed
-(BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    XEMobileTextEditorViewController *editorVC = [[ XEMobileTextEditorViewController alloc] initWithNibName:@"XEMobileTextEditorViewController" bundle:nil];
    editorVC.field = self.contentTextView;
    
    [self.navigationController pushViewController:editorVC animated:YES];
    return NO;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}


@end
