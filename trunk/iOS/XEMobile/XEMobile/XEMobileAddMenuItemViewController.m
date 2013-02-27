//
//  XEMobileAddMenuItemViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileAddMenuItemViewController.h"
#import "XEModule.h"
#import "XEUser.h"

@interface XEMobileAddMenuItemViewController ()

@property (strong, nonatomic) NSArray *arrayWithModules;

@end

@implementation XEMobileAddMenuItemViewController
@synthesize openNewWindowSegment = _openNewWindowSegment;
@synthesize moduleSegment = _moduleSegment;
@synthesize browserTitleField = _browserTitleField;
@synthesize selectModuleIDPicker = _selectModuleIDPicker;
@synthesize createModuleIDField = _createModuleIDField;
@synthesize createOrSelectLabel = _createOrSelectLabel;
@synthesize arrayWithModules = _arrayWithModules;
@synthesize articleWidgetExternalSegment = _articleWidgetExternalSegment;
@synthesize parentModuleSRL = _parentModuleSRL;
@synthesize editedMenu = _editedMenu;
@synthesize labelForErrors = _labelForErrors;
@synthesize indicator = _indicator;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //put a Cancel and a Done button on the navigation bar
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelPressed)];
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed)];
 
    //send request to get all modules
    [self getModules];
       
    [self.indicator startAnimating];
}

//method that sends a request to get all modules
-(void)getModules
{
    //map the response to an object
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEModule class]];
    
    [mapping mapKeyPath:@"module" toAttribute:@"mid"];
    [mapping mapKeyPath:@"module_srl" toAttribute:@"moduleSrl"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.newmodule"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationListModules" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData = @"get_all_modules_request";
     }];

}

//method called when the save button is pressed
-(void)saveButtonPressed
{
    //check what segment is selected for making the correct request
    switch (self.moduleSegment.selectedSegmentIndex) 
    {
        case 0:
            [self createModule];
            break;
        case 1:
            [self selectModule];
            break;
        case 2:
            [self menuURL];
            break;
    } 
    [self.indicator startAnimating];
}

//method called when the cancel button is pressed
-(void)cancelPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}

-(NSInteger)searchForModuleWithName:(NSString*)moduleID
{
    int contor = 0;
    for( XEModule *item in self.arrayWithModules )
    {
        if ( [item.mid isEqualToString:moduleID] ) return contor;
        else contor++;
    }
    return contor;
}

//checks the segmentedcontrol responsable with module type
-(NSString *)isArticleModuleOrExternal
{    switch (self.articleWidgetExternalSegment.selectedSegmentIndex)  
    {
        case 0:
            return @"ARTICLE";
        case 1:
            return @"WIDGET";
        case 2:
            return @"EXTERNAL";
    }
    return nil;
}

//checks the "open new window" segmentedcontroll and returns the correct value
-(NSString *) openInNewWindowOrNot
{
    switch (self.openNewWindowSegment.selectedSegmentIndex)
    {
        case 0:
            return @"Y";
        case 1:
            return @"N";
    }
    return nil;
}

-(void)sendRequestToInsertAMenuItemWithParams:(RKParams *)params
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"value" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/" usingBlock:^(RKObjectLoader *loader)
     {
         loader.params = params;
         loader.method = RKRequestMethodPOST;
         loader.delegate = self;
         loader.userData = @"insert_menu_item_request";
     }];
}

//three possible methods for building the http request
// start
-(void)createModule
{
    RKParams *params = [RKParams params];
    [params setValue:@"insertMenuItem" forParam:@"ruleset"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:@"procmobile_communicationMenuItem" forParam:@"act"];
    if( self.editedMenu )
        [params setValue:self.editedMenu.moduleSrl forParam:@"menu_item_srl"];
    else {
        [params setValue:@"" forParam:@"menu_item_srl"];
    }
    [params setValue:self.parentModuleSRL forParam:@"menu_srl"];
    [params setValue:self.browserTitleField.text forParam:@"menu_name_key"];
    [params setValue:self.browserTitleField.text forParam:@"menu_name"];
    [params setValue:@"CREATE" forParam:@"cType"];
    [params setValue:[self isArticleModuleOrExternal] forParam:@"module_type"];
    [params setValue:[self openInNewWindowOrNot] forParam:@"menu_open_window"];
    [params setValue:self.createModuleIDField.text forParam:@"create_menu_url"];
    
    [self sendRequestToInsertAMenuItemWithParams:params];
}

-(void)selectModule
{
    XEModule *module = [self.arrayWithModules objectAtIndex:[self.selectModuleIDPicker selectedRowInComponent:0]];
    
    RKParams *params = [RKParams params];
    
    [params setValue:@"insertMenuItem" forParam:@"ruleset"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:@"procmobile_communicationMenuItem" forParam:@"act"];
    [params setValue:self.parentModuleSRL forParam:@"menu_srl"];
    if(self.editedMenu != nil)
    [params setValue:self.editedMenu.moduleSrl forParam:@"menu_item_srl"];
    [params setValue:self.browserTitleField.text forParam:@"menu_name_key"];
    [params setValue:self.browserTitleField.text forParam:@"menu_name"];
    [params setValue:@"SELECT" forParam:@"cType"];
    [params setValue:[self isArticleModuleOrExternal] forParam:@"module_type"];
    [params setValue:[self openInNewWindowOrNot] forParam:@"menu_open_window"];
    [params setValue:module.mid forParam:@"select_menu_url"];
    
    [self sendRequestToInsertAMenuItemWithParams:params];
}

-(void)menuURL
{
    RKParams *params = [RKParams params];
    
    [params setValue:@"insertMenuItem" forParam:@"ruleset"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:@"procmobile_communicationMenuItem" forParam:@"act"];
    [params setValue:self.parentModuleSRL forParam:@"menu_srl"];
    [params setValue:self.browserTitleField.text forParam:@"menu_name_key"];
    [params setValue:self.browserTitleField.text forParam:@"menu_name"];
    [params setValue:@"URL" forParam:@"cType"];
    [params setValue:@"ARTICLE" forParam:@"module_type"];
    [params setValue:self.createModuleIDField.text forParam:@"menu_url"];
    [params setValue:[self openInNewWindowOrNot] forParam:@"menu_open_window"];
    [params setValue:self.editedMenu.moduleSrl forParam:@"menu_item_srl"];
    
    [self sendRequestToInsertAMenuItemWithParams:params];
}
//stop

-(NSString *)willThePageOpenInNewWindow
{
    NSString *open;
    
    switch (self.openNewWindowSegment.selectedSegmentIndex) {
        case 0:
            open = @"";
            break;
            
        case 1:
            open = @"";
            break;
    }
    return open;
}

//picker view delegate and datasource methods
- (NSInteger)numberOfComponentsInPickerView:(UIPickerView *)pickerView
{
    return 1;
}
- (NSInteger)pickerView:(UIPickerView *)pickerView numberOfRowsInComponent:(NSInteger)component
{
    return self.arrayWithModules.count;
}

- (NSString *)pickerView:(UIPickerView *)pickerView titleForRow:(NSInteger)row forComponent:(NSInteger)component
{
    XEModule *module = [self.arrayWithModules objectAtIndex:row];
    return module.mid;
}

//method called when the moduleSegment changes selected segment
-(IBAction)moduleSegmentChanged:(UISegmentedControl *)sender
{
    if ( sender.selectedSegmentIndex == 0)
    {
        self.createModuleIDField.hidden = NO;
        self.selectModuleIDPicker.hidden = YES;
        self.createOrSelectLabel.text = @"Create module ID";
        self.articleWidgetExternalSegment.hidden = NO;
    }
    else
        if( sender.selectedSegmentIndex == 1 )
        {
            self.createModuleIDField.hidden = YES;
            self.selectModuleIDPicker.hidden = NO;
            self.createOrSelectLabel.text = @"Select module ID";
            self.articleWidgetExternalSegment.hidden = YES;
        }
        else 
            if ( sender.selectedSegmentIndex == 2 )
            {
                self.createModuleIDField.hidden = NO;
                self.selectModuleIDPicker.hidden = YES;
                self.createOrSelectLabel.text = @"Menu URL";
                self.articleWidgetExternalSegment.hidden = YES;
            }
}

//hide the keyboard when Return is pressed
-(BOOL)textFieldShouldReturn:(UITextField *)textField
{
    [textField resignFirstResponder];
    return NO;
}

//load the current configuration settings if a Menu Item is edited
-(void)loadEditedObject
{
    self.browserTitleField.text = self.editedMenu.name;
    if([self.editedMenu.type isEqualToString:@"url"] )
            {
                self.moduleSegment.selectedSegmentIndex = 2;
//                self.navigationItem.rightBarButtonItem = nil;
                [self moduleSegmentChanged:self.moduleSegment];
                self.articleWidgetExternalSegment.hidden = YES;
            }
    else 
    {
        self.moduleSegment.selectedSegmentIndex = 1;
        [self moduleSegmentChanged:self.moduleSegment];
        [self.selectModuleIDPicker selectRow:[self searchForModuleWithName:self.editedMenu.URL] inComponent:0 animated:NO];
    }
    
    if([self.editedMenu.openWindow isEqualToString:@"Y"]) self.openNewWindowSegment.selectedSegmentIndex = 0;
    else self.openNewWindowSegment.selectedSegmentIndex = 1;
    
    self.createModuleIDField.text = self.editedMenu.URL;
}

// method called when an array with objects was mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    if([objectLoader.userData isEqualToString:@"get_all_modules_request"])
    {
        self.arrayWithModules = objects;
        [self.selectModuleIDPicker reloadAllComponents];
        
        // if editedMenu is nil => the View Controller adds a menu item
        // editedMenu is not nil => the View Controller edits a menu item
        if(self.editedMenu != nil)
        {
            [self loadEditedObject];
        }
        [self.indicator stopAnimating];
    }
}

// method called when an object was mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *user = object;
        
        if ( ![user.auxVariable isEqualToString:@"true"] )
        {
            [self showErrorWithMessage:@"Probably there is another page with that page name."];
        }
        else 
        {
            [self.navigationController dismissModalViewControllerAnimated:YES];
        }
        [self.indicator stopAnimating];
    }
}

//methods called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
}

//method called when a response was received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    //check if the user is logged in
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController];
}

-(void)requestWillPrepareForSend:(RKRequest *)request
{
    self.labelForErrors.text = @"";
}



-(void)viewDidUnload
{
    [super viewDidUnload];
    
    self.browserTitleField = nil;
    self.moduleSegment = nil;
    self.openNewWindowSegment = nil;
    self.createModuleIDField = nil;
    self.selectModuleIDPicker = nil;
    self.articleWidgetExternalSegment = nil;
    self.labelForErrors = nil;
    self.createOrSelectLabel = nil;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

@end