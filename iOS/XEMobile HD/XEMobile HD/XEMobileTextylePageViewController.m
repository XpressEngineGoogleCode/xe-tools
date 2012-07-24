//
//  XEMobileTextylePageViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 06/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextylePageViewController.h"
#import "XEMobileTextyleAddPageViewController.h"
#import "XEMobileTextyleEditPageViewController.h"
#import "XEUser.h"
#import "RestKit/RKRequestSerialization.h"

@interface XEMobileTextylePageViewController ()

@property (strong, nonatomic) NSArray *arrayWithPages;
@property (strong, nonatomic) XETextylePage *pageForDelete;

@end

@implementation XEMobileTextylePageViewController

@synthesize textyle = _textyle;
@synthesize arrayWithPages = _arrayWithPages;
@synthesize tableView = _tableView;
@synthesize pageForDelete = _pageForDelete; 

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    self.arrayWithPages = [[NSArray alloc] init];
    [self.indicator stopAnimating];
    if( objects.count != 0 && [[objects objectAtIndex:0] isKindOfClass:[XETextylePage class]] )
    {
    self.arrayWithPages = objects;
    [self.tableView reloadData];
    
    }
    
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *auxObject = object;
        if( [auxObject.auxVariable isEqualToString:@"success"] )
        {
            [self loadPages];
            [self.indicator stopAnimating];
        }
    }
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    [self.tableView reloadData];
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    [self loadPages];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}
             
-(void)loadPages
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XETextylePage class]];
    
    [mapping mapKeyPath:@"module" toAttribute:@"module"];
    [mapping mapKeyPath:@"name" toAttribute:@"name"];
    [mapping mapKeyPath:@"type" toAttribute:@"type"];
    [mapping mapKeyPath:@"module_srl" toAttribute:@"moduleSrl"];
    [mapping mapKeyPath:@"mid" toAttribute:@"mid"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.page"];
    
    NSString *path = [NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationExtraMenuList&site_srl=%@",self.textyle.siteSRL];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path delegate:self];     
    
    [self.indicator startAnimating];
}

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Pages";
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPageButtonPressed)];
    
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithPages.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if( cell == nil )
    {
        cell = [[ UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        
        UIButton *button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        button.frame = CGRectMake(150, 10, 60, 30);
        button.tag = indexPath.row;
        [button setTitle:@"Delete" forState:UIControlStateNormal];
        [button addTarget:self action:@selector(deleteButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:button];
    }
    else {
        UIButton *button = (UIButton *)[cell.contentView.subviews objectAtIndex:1];
        button.tag = indexPath.row;
         }
    
    
    
    XETextylePage *page = [self.arrayWithPages objectAtIndex:indexPath.row];
    
    cell.textLabel.text = page.name;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

-(void)deleteButtonPressed:(UIButton *)button
{
    self.pageForDelete = [self.arrayWithPages objectAtIndex:button.tag];
    
    UIActionSheet *action = [[UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles:nil];
    [action showInView:self.view];
    
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if( buttonIndex == 0 )
    {
        [self deleteSelectedItem];
    }
}

-(void)deleteSelectedItem
{
    NSString *deleteXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<menu_mid><![CDATA[%@]]></menu_mid>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleToolExtraMenuDelete]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",self.pageForDelete.mid,self.textyle.domain];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[deleteXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
         loader.userData = @"delete_page";
     } ];
    
    [self.indicator startAnimating];
}

-(void)addPageButtonPressed
{
    XEMobileTextyleAddPageViewController *addPageVC = [[XEMobileTextyleAddPageViewController alloc] initWithNibName:@"XEMobileTextyleAddPageViewController" bundle:nil];
    addPageVC.textyle = self.textyle;
    
    UINavigationController *navcon = [[UINavigationController alloc] initWithRootViewController:addPageVC];
    
    [self.navigationController presentModalViewController:navcon animated:YES];
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XETextylePage *page = [self.arrayWithPages objectAtIndex:indexPath.row];
    XEMobileTextyleEditPageViewController *editPostVC = [[XEMobileTextyleEditPageViewController alloc] initWithNibName:@"XEMobileTextyleEditPageViewController" bundle:nil];
    editPostVC.textyle = self.textyle;
    editPostVC.page = page;
    
    UINavigationController *navcon = [[UINavigationController alloc] initWithRootViewController:editPostVC];
    
    [self.navigationController presentModalViewController:navcon animated:YES];
}

@end
