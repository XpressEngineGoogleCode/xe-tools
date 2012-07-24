//
//  XEMobileTextyleAddPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 06/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleAddPageViewController.h"
#import "XEUser.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextyleAddPageViewController ()

@end

@implementation XEMobileTextyleAddPageViewController
@synthesize textyle = _textyle;
@synthesize urlTextField = _urlTextField;
@synthesize nameTextField = _nameTextField;
@synthesize contentTextView = _contentTextView;
@synthesize labelForURL = _labelForURL;
@synthesize scrollView = _scrollView;
@synthesize keyboardToolbar = _keyboardToolbar;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    self.labelForURL.text = [NSString stringWithFormat:@"%@/index.php?vid=%@&mid=",[RKClient sharedClient].baseURL.absoluteString,self.textyle.domain];

    self.scrollView.contentSize = CGSizeMake(320, 700);
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    NSLog(@"%@",response.bodyAsString);
    if( [response.bodyAsString isEqualToString:[self isLogged]]) [self pushLoginViewController];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
        {
            XEUser *objectCast = object;
            if( [objectCast.auxVariable isEqualToString:@"success"] )
            {
                [self.indicator stopAnimating];
                [self.navigationController popViewControllerAnimated:YES];
            }
            
            if( [objectCast.auxVariable isEqualToString:@"Only alphanumeric can be used in the module name."])
                [self showErrorWithMessage:@"Only alphanumeric can be used in the module name."];
        }
}

-(BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    XEMobileTextEditorViewController *editorVC = [[ XEMobileTextEditorViewController alloc] initWithNibName:@"XEMobileTextEditorViewController" bundle:nil];
    editorVC.field = self.contentTextView;
    [self.navigationController pushViewController:editorVC animated:YES];
    return NO;
}

-(void)doneButtonPressed
{
    NSString *addPostXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<publish><![CDATA[N]]></publish>\n<_filter><![CDATA[insert_extra_menu]]></_filter>\n<mid><![CDATA[textyle]]></mid>\n<vid><![CDATA[%@]]></vid>\n<menu_mid><![CDATA[%@]]></menu_mid>\n<menu_name><![CDATA[%@]]></menu_name>\n<content><![CDATA[<p>%@</p>]]></content>\n<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it? The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n<hx><![CDATA[h3]]></hx>\n<hr><![CDATA[hline]]></hr>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleToolExtraMenuInsert]]></act>\n</params>\n</methodCall>",self.textyle.domain,self.urlTextField.text,self.nameTextField.text,[self.contentTextView.text stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"]];

    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
        
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
    {
        loader.delegate = self;
        loader.params = [RKRequestSerialization serializationWithData:[addPostXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
        loader.method = RKRequestMethodPOST;
    }];
    
    [self.indicator startAnimating];
}


@end
