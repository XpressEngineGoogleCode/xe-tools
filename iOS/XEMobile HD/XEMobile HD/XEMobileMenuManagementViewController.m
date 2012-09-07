//
//  XEMobileMenuManagementViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileMenuManagementViewController.h"
#import "XEMenu.h"
#import "XEMobileMenuItemViewController.h"
#import "XEMenuItem.h"
#import "XEMobileLoginViewController.h"

@interface XEMobileMenuManagementViewController ()

@property (strong, nonatomic) NSMutableArray *arrayWithMenus;
@property (strong, nonatomic) XEMenu *menuForDelete;

@end

@implementation XEMobileMenuManagementViewController
@synthesize arrayWithMenus = _arrayWithMenus;
@synthesize indicator = _indicator;
@synthesize tableView = _tableView;
@synthesize menuForDelete = _menuForDelete;

- (void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response 
{
    if( [response.bodyAsString isEqualToString:[self isLogged]]) [self pushLoginViewController];
    else
    if ( [request.userData isEqualToString:@"delete_request"] )
    {
        [self getMenusRequest];
        [self.indicator stopAnimating];
    }
    else if ( [request.userData isEqualToString:@"add_request"] )
    {
        [self getMenusRequest];
        [self.indicator stopAnimating];
    }
}

- (void)request:(RKRequest*)request didFailLoadWithError:(NSError *)error 
{
    [self showErrorWithMessage:self.errorMessage];
    NSLog(@"error!");
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithMenus.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"Identifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if(cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        
        //create delete uibutton in each cell
        UIButton *button = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        button.frame = CGRectMake(170, 65, 70, 30);
        [button setTitle:@"Delete" forState:UIControlStateNormal];
        [button addTarget:self action:@selector(deletePressed:) forControlEvents:UIControlEventTouchUpInside];
        button.tag = indexPath.row;
        cell.textLabel.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:button];
    }
    else{
        //set the correct indexpath if the cell already exists
        UIButton *button = [[cell.contentView subviews] objectAtIndex:1];
        button.tag = indexPath.row;
    }
    
    XEMenu *menu = [self.arrayWithMenus objectAtIndex:indexPath.row];
    cell.textLabel.text = menu.name;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

//method called when the delete button get's pressed
-(void)deletePressed:(UIButton *)sender
{
    UIActionSheet *action = [[ UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles: nil];
    [action showInView:self.view];
    
    //select the menu for removing
    self.menuForDelete = [self.arrayWithMenus objectAtIndex:sender.tag];
    
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if( buttonIndex == 0)
    {
        [self deleteSelectedItem];
    }
}

//delete the selected menu
-(void)deleteSelectedItem
{
    RKParams *params = [RKParams params];
    [params setValue:self.menuForDelete.moduleSrl forParam:@"menu_srl"];
    [params setValue:self.menuForDelete.name forParam:@"title"];
    
    RKRequest *request= [[RKClient sharedClient] post:@"/index.php?module=mobile_communication&act=procmobile_communicationMenuDelete" params:params delegate:self];
    //for identify in "request:didLoadResponse:"
    request.userData = @"delete_request";
    
    [self.indicator startAnimating];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.tableView.rowHeight = 100;
    
    self.navigationItem.title = @"Menu List";
    
    //add menu button declaration
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMenuBarButtonPressed)];
    
    [self getMenusRequest];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
}

- (void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    self.arrayWithMenus = [objects mutableCopy];
    [self.indicator stopAnimating];
    [self.tableView reloadData];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    
}


-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

//method that sends a request to obtain all menus
-(void)getMenusRequest
{
    [self.indicator startAnimating];
    
    
    RKObjectMapping *menuItems = [RKObjectMapping mappingForClass:[XEMenuItem class]];
    [menuItems mapKeyPath:@"menuItemName" toAttribute:@"name"];
    [menuItems mapKeyPath:@"srl" toAttribute:@"moduleSrl"];
    [menuItems mapKeyPath:@"open_window" toAttribute:@"openWindow"];
    [menuItems mapKeyPath:@"url" toAttribute:@"URL"];
    
    RKObjectMapping *menu = [RKObjectMapping mappingForClass:[XEMenu class]];
    [menu mapKeyPath:@"menuName" toAttribute:@"name"];
    [menu mapKeyPath:@"menuSrl" toAttribute:@"moduleSrl"];
    [menu mapKeyPath:@"menuItem" toRelationship:@"menuItems" withMapping:menuItems];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:menu forKeyPath:@"response.menu"];
    
    NSDictionary *parametr = [[NSDictionary alloc] 
                              initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationDisplayMenu", nil] 
                              forKeys:[NSArray arrayWithObjects:@"module",@"act", nil]];
    
    NSString *resourcePath = [@"/index.php" stringByAppendingQueryParameters:parametr];
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:resourcePath delegate:self];
    
}

//method that pop up the window for adding a Menu
-(void)addMenuBarButtonPressed
{
    UIView *transparentBackground = [[UIView alloc] initWithFrame:self.view.frame];
    transparentBackground.backgroundColor = [UIColor colorWithWhite:0.7 alpha:0.4];
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(148, 200, 160, 90)];
    view.backgroundColor = [UIColor grayColor];
    UITextField *textField = [[UITextField alloc] initWithFrame:CGRectMake(10, 10, 140, 31)];
    textField.returnKeyType = UIReturnKeyDone;
    textField.borderStyle = UITextBorderStyleRoundedRect;
    textField.placeholder = @"Insert title";
    textField.autocapitalizationType = UITextAutocapitalizationTypeNone;
    [textField addTarget:self action:@selector(addMenu:) forControlEvents:UIControlEventEditingDidEndOnExit];
    
    UIButton *backButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    backButton.frame = CGRectMake(20, 50, 120, 30);
    [backButton setTitle:@"back" forState:UIControlStateNormal];
    [backButton addTarget:self action:@selector(backButton:) forControlEvents:UIControlEventTouchUpInside];
    
    [view addSubview:backButton];
    [view addSubview:textField];
    [transparentBackground addSubview:view];
    [self.view addSubview:transparentBackground];
}

//method called when the Back button from the Adding Menu Window is pressed
-(void)backButton:(UIButton *)sender
{
    UIView *view = [sender superview];
    UIView *background = [view superview];
    
    [view removeFromSuperview];
    [background removeFromSuperview];
}

//method called when the Ok button from the Adding Menu Window is pressed
-(void)addMenu:(UITextField *)sender
{
    [sender resignFirstResponder];
    UIView *view = [sender superview];
    UIView *background = [view superview];
    [view removeFromSuperview];
    [background removeFromSuperview];
    
    NSString *title = sender.text;
    
    RKParams *params = [RKParams params];
    
    [params setValue:title forParam:@"title"];
    [params setValue:@"mobile_communication" forParam:@"module"];
    [params setValue:@"procmobile_communicationMenuInsert" forParam:@"act"];
    
    RKRequest *request = [[RKClient sharedClient] post:@"/" params:params delegate:self];
    request.userData = @"add_request";
    
    [self.indicator startAnimating];
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEMobileMenuItemViewController *menuItemVC = [[XEMobileMenuItemViewController alloc] initWithNibName:@"XEMobileMenuItemViewController" bundle:nil];
    
    XEMenu *menu = [self.arrayWithMenus objectAtIndex:indexPath.row];
    
    menuItemVC.parentMenu = menu;
    
    [self.navigationController pushViewController:menuItemVC animated:YES];
}


- (void)viewDidUnload
{
    [super viewDidUnload];
    
    self.tableView = nil;
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}


@end