//
//  XEMobileTextyleSelectViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleSelectViewController.h"
#import "XETextyle.h"
#import "XEMobileTextyleMainPageViewController.h"
#import "XEMobileTextyleAddTextyleViewController.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEUser.h"

@interface XEMobileTextyleSelectViewController ()

@property (strong, nonatomic) NSArray *arrayWithTextyles;
@property (strong, nonatomic) XETextyle *textyleToDelete;

@end



@implementation XEMobileTextyleSelectViewController
@synthesize arrayWithTextyles = _arrayWithTextyles;
@synthesize tableView = _tableView;
@synthesize textyleToDelete = _textyleToDelete;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Textyle List";
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    // set an add button in navigation bar
    // set an action to it
    self.navigationItem.rightBarButtonItem = [ [UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addTextyleButtonPressed) ];
    
    // make requests to load all the textyles
    [self loadTextyles];
}

-(void)loadTextyles
{
    //building the request
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XETextyle class]];
    
    [mapping mapKeyPath:@"domain" toAttribute:@"domain"];
    [mapping mapKeyPath:@"textyle_srl" toAttribute:@"textyleSRL"];
    [mapping mapKeyPath:@"module_srl" toAttribute:@"moduleSrl"];
    [mapping mapKeyPath:@"timezone" toAttribute:@"timezone"];
    [mapping mapKeyPath:@"default_lang" toAttribute:@"defaultLanguage"];
    [mapping mapKeyPath:@"user_id" toAttribute:@"userId"];
    [mapping mapKeyPath:@"site_srl" toAttribute:@"siteSRL"];
    [mapping mapKeyPath:@"email_address" toAttribute:@"emailAddress"];
    [mapping mapKeyPath:@"use_mobile" toAttribute:@"useMobile"];
    [mapping mapKeyPath:@"mid" toAttribute:@"mid"];
    [mapping mapKeyPath:@"skin" toAttribute:@"skin"];
    [mapping mapKeyPath:@"browser_title" toAttribute:@"browserTitle"];
    [mapping mapKeyPath:@"textyle_title" toAttribute:@"textyleTitle"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"textyle-list.textyle"];
    
    NSDictionary *parametr = [[NSDictionary alloc] 
                              initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationTextyleList", nil] 
                              forKeys:[NSArray arrayWithObjects:@"module",@"act", nil]];
    
    NSString *path = [@"/index.php" stringByAppendingQueryParameters:parametr];
    
    //sending the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path delegate:self];
    
    [self.indicator startAnimating];
}

//called when a message is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    //check if user is logged ouy
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    // an error occurred
}

//called when the response is mapped to an array of objects
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    // check if the received answer is the expected one
    if( objects.count != 0 )
        if( [[objects objectAtIndex:0] isKindOfClass:[XETextyle class]] )
        {
            //load the array with XETextyle
                self.arrayWithTextyles = objects;
                [self.indicator stopAnimating];
                [self.tableView reloadData];
        }
}

//called when the response is mapped to an object
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XEUser class]] )
    {
        XEUser *auxObject = object;
        
        //check if response is ok at delete request
        if( [auxObject.auxVariable isEqualToString:@"Deleted successfully."] )
        {
            [self.indicator stopAnimating];
            [self loadTextyles];
            [self.tableView reloadData];
        }
    }
}


-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    
    //when the viewcontroller dissapears we cancel the queued requests
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}


// UITableView protocol methods
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEMobileTextyleMainPageViewController *textyleMainPageVC = [[XEMobileTextyleMainPageViewController alloc] initWithNibName:@"XEMobileTextyleMainPageViewController" bundle:nil];
    XETextyle *textyle =  [self.arrayWithTextyles objectAtIndex:indexPath.row];
    textyleMainPageVC.textyleItem =textyle;
    if( [textyle.domain rangeOfString:@"."].location == NSNotFound )
        [self.navigationController pushViewController:textyleMainPageVC animated:YES];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithTextyles.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if( cell == nil )
    {
        
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        UIButton *button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        button.frame = CGRectMake(200, 10, 50, 30);
        [button setTitle:@"Delete" forState:UIControlStateNormal];
        button.tag = indexPath.row;
        [button addTarget:self action:@selector(deleteTextyleButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:button];
    }
    else {
        UIButton *button = [[cell.contentView subviews] objectAtIndex:1];
        button.tag = indexPath.row;
         }
    
    XETextyle *textyle = [self.arrayWithTextyles objectAtIndex:indexPath.row];
    cell.textLabel.text = textyle.domain;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

//when the delete button is pressed this method is called
//a confirmation from user is required
-(void)deleteTextyleButtonPressed:(UIButton *)button
{
    self.textyleToDelete = [self.arrayWithTextyles objectAtIndex:button.tag];
    
    UIActionSheet *action = [[UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles:nil];
    [action showInView:self.view];
}

//if user confirms the delete, the objects gets deleted
-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
        if( buttonIndex == 0)
        {
            [self deleteSelectedItem];
        }
}

//prepare and send request to delete the textyle
-(void)deleteSelectedItem
{
    NSString *deleteXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<_filter><![CDATA[delete_textyle]]></_filter>\n<error_return_url><![CDATA[/xe3/index.php?module=admin&act=dispTextyleAdminDelete&module_srl=%@]]></error_return_url>\n<act><![CDATA[procTextyleAdminDelete]]></act>\n<site_srl><![CDATA[%@]]></site_srl>\n<module><![CDATA[textyle]]></module>\n</params>\n</methodCall>",self.textyleToDelete.siteSRL,self.textyleToDelete.siteSRL];
    
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    //sending the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[deleteXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
         loader.userData = @"delete_textyle";
     }];
    [self.indicator startAnimating];
}

//method called when the Add button is pressed
-(void)addTextyleButtonPressed
{
    //it pushes 
    XEMobileTextyleAddTextyleViewController *addTextyle = [[XEMobileTextyleAddTextyleViewController alloc] initWithNibName:@"XEMobileTextyleAddTextyleViewController" bundle:nil];
    [self.navigationController pushViewController:addTextyle animated:YES];
}


-(void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}

@end
