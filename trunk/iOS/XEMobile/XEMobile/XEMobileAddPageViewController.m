//
//  XEMobileAddPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileAddPageViewController.h"
#import "XEUser.h"
#import "XELayout.h"

@interface XEMobileAddPageViewController ()

@property (strong, nonatomic) NSArray *arrayWithLayouts;

@end

@implementation XEMobileAddPageViewController

@synthesize pageType = _pageType;
@synthesize pageName = _pageName;
@synthesize browserTitle = _browserTitle;
@synthesize pageCache = _pageCache;
@synthesize arrayWithLayouts = _arrayWithLayouts;
@synthesize layoutPickerView = _layoutPickerView;

- (BOOL)textFieldShouldReturn:(UITextField *)textField 
{
    [textField resignFirstResponder];
    return NO;
}

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    [self getLayout];
    
    //put a Done and a Cancel button on navigation bar
    self.navigationItem.rightBarButtonItem = [[ UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(postItButton:)];
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButton:)];
}

//method that sends a requests to load all the layouts
-(void)getLayout
{
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XELayout class]];
    
    [mapping mapKeyPath:@"layout_srl" toAttribute:@"layoutSRL"];
    [mapping mapKeyPath:@"layout_name" toAttribute:@"layoutName"];
    [mapping mapKeyPath:@"title" toAttribute:@"title"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.layout"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationGetLayout" usingBlock:^(RKObjectLoader *loader)
     {
         loader.userData = @"layout_request";
         loader.delegate = self;
     }];
}

//method called when an object is loaded from response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]])
    {
        XEUser *user = object;
        if( [user.auxVariable isEqualToString:@"true"] )
        {
        [self dismissModalViewControllerAnimated:YES];
        [self.indicator stopAnimating];
        }
        else 
        {
        [self.indicator stopAnimating];
        [self showErrorWithMessage:@"Probably there is another page with that page name."];
        }
    }
}

//method called when an array with objects is loaded from response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    if( objects.count != 0 && [[objects objectAtIndex:0] isKindOfClass:[XELayout class]])
    {
        self.arrayWithLayouts = objects;
        [self.layoutPickerView reloadAllComponents];
    }
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{

}

//method called when the Cancel button is pressed
-(void)cancelButton:(id)sender
{
    [self dismissModalViewControllerAnimated:YES];
}

//method called when the Post button is pressed
-(void)postItButton:(id)sender
{
    RKParams* params = [RKParams params];
    
    //map the response to an object
    [params setValue:@"/xe_dev/index.php?module=admin&act=dispPageAdminInsert" forParam:@"error_return_url"];
    [params setValue:@"insertPage" forParam:@"ruleset"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:@"procmobile_communicationPageInsert" forParam:@"act"];
    
    switch (self.pageType.selectedSegmentIndex) 
    {
        case 0:
            [params setValue:@"WIDGET" forParam:@"page_type"];
            break;
        case 1:
            [params setValue:@"ARTICLE" forParam:@"page_type"];
            break;
        case 2:
            [params setValue:@"EXTERNAL" forParam:@"page_type"];
            break;
    }
    
    
    [params setValue:self.pageName.text forParam:@"page_name"];
    [params setValue:self.browserTitle.text forParam:@"browser_title"];
    [params setValue:[NSNumber numberWithInt:[self.pageCache.text intValue]] forParam:@"page_caching_interval"];
    [params setValue:@"default" forParam:@"skin"];
    [params setValue:@"default" forParam:@"mskin"];
    
    XELayout *layoutSelected = [self.arrayWithLayouts objectAtIndex:[self.layoutPickerView selectedRowInComponent:0]];
    [params setValue:layoutSelected.layoutSRL forParam:@"layout_srl"];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    [mapping mapKeyPath:@"value" toAttribute:@"auxVariable"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/" usingBlock:^(RKObjectLoader * loader)
     {
         loader.params = params;
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
     }];
    
    [self.indicator startAnimating];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController ]; 
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self.indicator stopAnimating];
    [self showErrorWithMessage:self.errorMessage];
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    
    self.pageType = nil;
    self.pageName = nil;
    self.browserTitle = nil;
    self.pageCache = nil;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//picker view delegate and datasource methods

- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return self.arrayWithLayouts.count;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    XELayout *layout = [self.arrayWithLayouts objectAtIndex:row];
    return layout.title;
}

@end
