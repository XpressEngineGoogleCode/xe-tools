//
//  XEMobileTextyleWritingSettingsViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 09/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleWritingSettingsViewController.h"
#import "RestKit/RKRequestSerialization.h"

@interface XEMobileTextyleWritingSettingsViewController ()

@end

@implementation XEMobileTextyleWritingSettingsViewController
@synthesize textyle = _textyle;
@synthesize suffixExistsSwitch = _suffixExistsSwitch;
@synthesize prefixExistsSwitch = _prefixExistsSwitch;
@synthesize suffixTextView = _suffixTextView;
@synthesize prefixTextView = _prefixTextView;
@synthesize fontSizeTextField = _fontSizeTextField;
@synthesize fontFamilyTextField = _fontFamilyTextField;
@synthesize editorSegmentedControl = _editorSegmentedControl;
@synthesize textyleSettings = _textyleSettings;
@synthesize scrollView = _scrollView;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //add a cancel and a done button to the navigation bar and add an action to them
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    //load the current setting configuration
    [self loadSettings];
    
    self.scrollView.contentSize = CGSizeMake(320, 1400);
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

//method called when an object is loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XETextyleSettings class]] )
    {
        self.textyleSettings = object;
        [self adjustViewElements];
        [self.indicator stopAnimating];
    }
}

//method called when the response came
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]]) [ self pushLoginViewController];
    
    [self.indicator stopAnimating];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

//method that sends a request to get the current setting configuration for the current textyle
-(void)loadSettings
{
    //map the response to an object
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XETextyleSettings class]];
    [mapping mapKeyPath:@"editor" toAttribute:@"editor"];
    [mapping mapKeyPath:@"fontFamily" toAttribute:@"fontFamily"];
    [mapping mapKeyPath:@"fontSize" toAttribute:@"fontSize"];
    [mapping mapKeyPath:@"usePrefix" toAttribute:@"usePrefix"];
    [mapping mapKeyPath:@"prefix" toAttribute:@"prefix"];
    [mapping mapKeyPath:@"useSuffix" toAttribute:@"useSuffix"];
    [mapping mapKeyPath:@"suffix" toAttribute:@"suffix"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //sending the response
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationToolConfig&module_srl=%@",self.textyle.moduleSrl] delegate:self];
    
    [self.indicator startAnimating];
}


-(void)adjustViewElements
{
    if( [self.textyleSettings.editor isEqualToString:@"xpresseditor"] )
        {
            self.editorSegmentedControl.selectedSegmentIndex = 1;
        }
    else self.editorSegmentedControl.selectedSegmentIndex = 0;
    
    if( [self.textyleSettings.usePrefix isEqualToString:@"Y"] )
        self.prefixExistsSwitch.on = TRUE;
    else self.prefixExistsSwitch.on = FALSE;
    
    if( [self.textyleSettings.useSuffix isEqualToString:@"Y"] )
        self.suffixExistsSwitch.on = TRUE;
    else self.suffixExistsSwitch.on = FALSE;
    
    self.fontFamilyTextField.text = self.textyleSettings.fontFamily;
    self.fontSizeTextField.text= self.textyleSettings.fontSize;
    
    self.prefixTextView.text = self.textyleSettings.prefix;
    self.suffixTextView.text = self.textyleSettings.suffix;
}

-(void)cancelButtonPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}


//method called when the done button is pressed
// it saves the current setting configuration
-(void)doneButtonPressed
{
    //building the request
    NSString *updateSettingsXML;
    
    if( [[self typeOfEditor] isEqualToString:@"xpresseditor"])
    updateSettingsXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_config_postwrite]]></_filter>\n<act><![CDATA[procTextyleConfigPostwriteInsert]]></act>\n<vid><![CDATA[%@]]></vid>\n<mid><![CDATA[textyle]]></mid>\n<etc_post_editor_skin><![CDATA[xpresseditor]]></etc_post_editor_skin>\n<font_family><![CDATA[%@]]></font_family>\n<font_size><![CDATA[%@]]></font_size>\n<post_prefix><![CDATA[%@]]></post_prefix>\n<post_use_suffix><![CDATA[%@]]></post_use_suffix>\n<post_use_prefix><![CDATA[%@]]></post_use_prefix>\n<post_suffix><![CDATA[%@]]></post_suffix>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.textyle.domain,self.fontFamilyTextField.text,self.fontSizeTextField.text,self.prefixTextView.text,[self hasSuffix],[self hasPrefix],self.suffixTextView.text];
    else {
        updateSettingsXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[insert_config_postwrite]]></_filter>\n<act><![CDATA[procTextyleConfigPostwriteInsert]]></act>\n<vid><![CDATA[%@]]></vid>\n<mid><![CDATA[textyle]]></mid>\n<post_editor_skin><![CDATA[dreditor]]></post_editor_skin>\n<etc_post_editor_skin><![CDATA[xpresseditor]]></etc_post_editor_skin>\n<font_family><![CDATA[%@]]></font_family>\n<font_size><![CDATA[%@]]></font_size>\n<post_prefix><![CDATA[%@]]></post_prefix>\n<post_use_suffix><![CDATA[%@]]></post_use_suffix>\n<post_use_prefix><![CDATA[%@]]></post_use_prefix>\n<post_suffix><![CDATA[%@]]></post_suffix>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.textyle.domain,self.fontFamilyTextField.text,self.fontSizeTextField.text,self.prefixTextView.text,[self hasSuffix],[self hasPrefix],self.suffixTextView.text];
    }
    
    //sending the request
    [[RKClient sharedClient] post:@"/index.php" params:[RKRequestSerialization serializationWithData:[updateSettingsXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML] delegate:self];
    
    [self.indicator startAnimating];
}


- (void)scrollViewDidScroll:(UIScrollView *)scrollView
{   
    //hide keyboard when the scrollview scrolls
    [self.prefixTextView resignFirstResponder];
    [self.suffixTextView resignFirstResponder];
    [self.fontSizeTextField resignFirstResponder];
    [self.fontFamilyTextField resignFirstResponder];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

-(NSString *)hasSuffix
{
    if( self.suffixExistsSwitch.on ) return @"Y";
    else return @"N";
}

-(NSString *)hasPrefix
{
    if( self.prefixExistsSwitch.on ) return @"Y";
    else return @"N";
}

-(NSString *)typeOfEditor
{
    if( self.editorSegmentedControl.selectedSegmentIndex == 0 ) return @"dreditor";
    else return @"xpresseditor";
}

-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    
    return NO;
}

- (void)viewDidUnload
{
    [super viewDidUnload];

    self.editorSegmentedControl = nil;
    self.fontFamilyTextField = nil;
    self.fontSizeTextField = nil;
    self.prefixExistsSwitch = nil;
    self.prefixTextView = nil;
    self.suffixExistsSwitch = nil;
    self.suffixTextView = nil;
    self.scrollView = nil;
}



@end
