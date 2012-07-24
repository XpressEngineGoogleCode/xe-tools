//
//  XEMobileEditContentPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileEditContentPageViewController.h"
#import <RestKit/RKRequestSerialization.h>
#import "XEUser.h"
#import "XEMobileTextEditorViewController.h"
@interface XEMobileEditContentPageViewController ()

@property (strong, nonatomic) NSString *htmlString;

@property (strong, nonatomic) NSString *titleString;
@property (strong, nonatomic) NSString *contentString;

@end

@implementation XEMobileEditContentPageViewController
@synthesize titleTextField = _titleTextField;
@synthesize page = _page;
@synthesize htmlString = _htmlString;
@synthesize titleString = _titleString;
@synthesize contentString = _contentString;
@synthesize scrollView = _scrollView;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = self.page.mid;
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed:)];
    
    RKRequest *requestForContent = [[RKClient sharedClient] get:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationArticleContent&srl=%@",self.page.document_srl] delegate:self];
    requestForContent.userData = @"contentRequest";
    
    RKRequest *requestForTitle = [[RKClient sharedClient] get:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationArticleTitle&srl=%@",self.page.document_srl] delegate:self];
    requestForTitle.userData = @"titleRequest";
    
    [self.indicator startAnimating];
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed:)];
    
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed:)];
    
    self.scrollView.contentSize = CGSizeMake(320, 700);
    self.scrollView.scrollEnabled = NO;
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    XEUser *obj = object;
    
    if( [obj.auxVariable isEqualToString:@"Updated successfully."] || [obj.auxVariable isEqualToString:@"Registered successfully."] )
    {
        [self.indicator stopAnimating];
        [self.navigationController dismissModalViewControllerAnimated:YES];
    }
    
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return NO;
}


-(void)saveButtonPressed:(id)sender
{
    NSLog(@"saving this %@",[self.textView.text stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"]);
    NSString *requestString = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_article]]></_filter>\n<error_return_url><![CDATA[/xe_dev2/index.php?mid=%@&act=dispPageAdminContentModify]]></error_return_url>\n<act><![CDATA[procPageAdminArticleDocumentInsert]]></act>\n<mid><![CDATA[%@]]></mid>\n<content><![CDATA[%@]]></content>\n<document_srl><![CDATA[%@]]></document_srl>\n<title><![CDATA[%@]]></title><module><![CDATA[page]]></module></params></methodCall>",self.page.mid,self.page.mid,[self.textView.text stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"],self.page.moduleSrl,self.titleTextField.text];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php"usingBlock:^(RKObjectLoader *loader)
     {
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[requestString dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML ];
         loader.delegate = self;
         
     }];
    
    [self.indicator startAnimating];
}

-(void)cancelButtonPressed:(id)sender
{
    [self dismissModalViewControllerAnimated:YES];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) 
    {
        [ self pushLoginViewController ];
    }
    else 
    if( [request.userData isEqualToString:@"contentRequest"] )
    {
        
        self.contentString = [self prepareStringForDisplay:response.bodyAsString];
        [self.indicator stopAnimating];
        [self loadContent];
    }
    else if ( [request.userData isEqualToString:@"titleRequest"] )
    {
        self.titleString = response.bodyAsString;
        [self.indicator stopAnimating];
        [self loadContent];
    }
}

-(void)loadContent
{
    self.textView.text = self.contentString;
    
    self.titleTextField.text = self.titleString;
    
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    
    self.titleTextField = nil;
    self.textView = nil;
    self.scrollView = nil;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

@end
