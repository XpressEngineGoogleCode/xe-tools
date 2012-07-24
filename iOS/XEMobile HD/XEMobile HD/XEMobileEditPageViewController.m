//
//  XEMobileEditPageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 19/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileEditPageViewController.h"
#import "XELayout.h"
#import "XEUser.h"

@interface XEMobileEditPageViewController ()

@property (strong, nonatomic) NSMutableArray *arrayWithLayouts;

@end

@implementation XEMobileEditPageViewController

@synthesize pageTypeLabel = _pageTypeLabel;
@synthesize browserTitleTextField = _browserTitleTextField;
@synthesize moduleNameTextField = _moduleNameTextField;
@synthesize layoutPickerView = _layoutPickerView;
@synthesize page = _page;
@synthesize arrayWithLayouts = _arrayWithLayouts;

-(NSMutableArray *)arrayWithLayouts
{
    if( _arrayWithLayouts == nil ) 
    {
     _arrayWithLayouts = [[NSMutableArray alloc] init ];
        XELayout *layout = [[XELayout alloc] init];
        layout.title = @"None";
        layout.layoutSRL = 0;
        [_arrayWithLayouts addObject:layout];
    }
    
    return _arrayWithLayouts;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.pageTypeLabel.text = self.page.pageType;
    self.moduleNameTextField.text = self.page.mid;
    self.browserTitleTextField.text = self.page.browserTitle;
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(doneButtonPressed)];
    
    [self getLayout];
}

-(void)doneButtonPressed
{
    RKParams* params = [RKParams params];
    
    [params setValue:@"/xe_dev/index.php?module=admin&act=dispPageAdminInsert" forParam:@"error_return_url"];
    [params setValue:@"updatePage" forParam:@"ruleset"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:self.page.moduleSrl forParam:@"module_srl"];
    [params setValue:@"procmobile_communicationPageInsert" forParam:@"act"];
    
    [params setValue:self.moduleNameTextField.text forParam:@"page_name"];
    [params setValue:self.browserTitleTextField.text forParam:@"browser_title"];
    [params setValue:@"default" forParam:@"skin"];
    [params setValue:@"default" forParam:@"mskin"];
    
    XELayout *layoutSelected = [self.arrayWithLayouts objectAtIndex:[self.layoutPickerView selectedRowInComponent:0]];
    [params setValue:layoutSelected.layoutSRL forParam:@"layout_srl"];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    [mapping mapKeyPath:@"value" toAttribute:@"auxVariable"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/" usingBlock:^(RKObjectLoader * loader)
     {
         loader.params = params;
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.userData = @"save_request";
     }];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.pageTypeLabel = nil;
    self.browserTitleTextField = nil;
    self.moduleNameTextField = nil;
    self.layoutPickerView = nil;
}

-(void)getLayout
{
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XELayout class]];
    
    [mapping mapKeyPath:@"layout_srl" toAttribute:@"layoutSRL"];
    [mapping mapKeyPath:@"layout_name" toAttribute:@"layoutName"];
    [mapping mapKeyPath:@"title" toAttribute:@"title"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.layout"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationGetLayout" usingBlock:^(RKObjectLoader *loader)
     {
         loader.userData = @"layout_request";
         loader.delegate = self;
     }];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"Error!"];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    NSLog(@"%@",response.bodyAsString);
    if( [response.bodyAsString isEqualToString:self.isLogged] ) [self pushLoginViewController];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *confirmation = object;
        if( [confirmation.auxVariable isEqualToString:@"true"] )
            {
                [self.navigationController popViewControllerAnimated:YES];
            }
    }
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    if( objects.count != 0 && [[objects objectAtIndex:0] isKindOfClass:[XELayout class]])
    {
        [self.arrayWithLayouts addObjectsFromArray: objects];
        [self.layoutPickerView reloadAllComponents];
        [self loadSelectedLayout];
    }
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
}

-(void)loadSelectedLayout
{
    [self.layoutPickerView selectRow:[self indexForLayoutSrl:self.page.layoutSRL] inComponent:0 animated:NO];
}


-(NSInteger)indexForLayoutSrl:(NSString *)layoutSRL
{
    int index = 0;
    for(XELayout *layout in self.arrayWithLayouts)
    {
        if ( [layout.layoutSRL isEqualToString:layoutSRL] ) return index;
        index++;
    }
    return 0;
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
