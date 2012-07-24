//
//  XEMobileTextyleEditPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 06/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleEditPageViewController.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEUser.h"
#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextyleEditPageViewController ()

@property (strong, nonatomic) NSString *stringToEdit;

@end

@implementation XEMobileTextyleEditPageViewController

@synthesize textyle = _textyle;
@synthesize page = _page;
@synthesize nameTextField = _nameTextField;
@synthesize contentTextView = _contentTextView;
@synthesize labelForURL = _labelForURL;
@synthesize stringToEdit = _stringToEdit;
@synthesize scrollView = _scrollView;
@synthesize keyboardToolbar = _keyboardToolbar;

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    [self.indicator stopAnimating];
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController];
    
    if( [request.userData isEqualToString:@"load"] )
            
    {
     self.stringToEdit = response.bodyAsString;
        if( self.stringToEdit.length > 6)
        {
        self.stringToEdit = [self.stringToEdit substringWithRange:NSMakeRange(3, self.stringToEdit.length-7)];
        self.contentTextView.text = [self.stringToEdit stringByReplacingOccurrencesOfString:@"<br/>" withString:@"\n"];
        }
    }
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = self.page.name;
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    self.labelForURL.text = [NSString stringWithFormat:@"%@index.php?vid=%@&mid=%@",[RKClient sharedClient].baseURL.absoluteString,self.textyle.domain,self.page.mid];
    self.nameTextField.text = self.page.name;
    
    RKRequest *request = [[RKClient sharedClient] get:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationContentPage&menu_mid=%@&site_srl=%@",self.page.mid,self.textyle.siteSRL] delegate:self];
    request.userData = @"load";
    
    [self.indicator startAnimating];
    
    self.scrollView.contentSize = CGSizeMake(320, 700);
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.nameTextField = nil;
    self.contentTextView = nil;
    self.labelForURL = nil;
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    [self.indicator stopAnimating];
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *aux = object;
        if ( [aux.auxVariable isEqualToString:@"success"] )
            {
                [self.navigationController popViewControllerAnimated:YES];
            }
    }
}

-(void)doneButtonPressed
{
    NSString *saveXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<error_return_url><![CDATA[/xe2/index.php?act=dispTextyleToolExtraMenuInsert&menu_mid=qwqwe&vid=blog]]></error_return_url>\n<act><![CDATA[procTextyleToolExtraMenuUpdate]]></act>\n<menu_mid><![CDATA[%@]]></menu_mid>\n<publish><![CDATA[N]]></publish>\n<_filter><![CDATA[modify_extra_menu]]></_filter>\n<mid><![CDATA[textyle]]></mid>\n<vid><![CDATA[%@]]></vid>\n<menu_name><![CDATA[%@]]></menu_name>\n<msg_close_before_write><![CDATA[Changed contents are not saved.]]></msg_close_before_write>\n<content><![CDATA[<p>%@</p>]]></content>\n<_saved_doc_message><![CDATA[There is a draft automatically saved. Do you want to restore it? The auto-saved draft will be discarded when you write and save it.]]></_saved_doc_message>\n<hx><![CDATA[h3]]></hx>\n<hr><![CDATA[hline]]></hr>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.page.mid,self.textyle.domain,self.nameTextField.text,[self.contentTextView.text stringByReplacingOccurrencesOfString:@"\n" withString:@"<br/>"]];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[saveXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
     }];
    
    [self.indicator startAnimating];
}

-(BOOL)textViewShouldBeginEditing:(UITextView *)textView
{
    XEMobileTextEditorViewController *editor = [[XEMobileTextEditorViewController alloc] initWithNibName:@"XEMobileTextEditorViewController" bundle:nil];
    editor.field = self.contentTextView;
    [self.navigationController pushViewController:editor animated:YES];
    return NO;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

@end
