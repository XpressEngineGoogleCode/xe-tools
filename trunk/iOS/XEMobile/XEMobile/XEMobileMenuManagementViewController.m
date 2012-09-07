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

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.tableView.rowHeight = 100;
    
    self.navigationItem.title = @"Menu List";
    
    //add menu button declaration
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMenuBarButtonPressed)];
    
    //send request to get the menus
    [self getMenusRequest];
}

//method called when a response is received
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

//method called when an error occured
- (void)request:(RKRequest*)request didFailLoadWithError:(NSError *)error 
{
    [self showErrorWithMessage:self.errorMessage];
}


//TABLE VIEW with menus
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
    
    //get the menu that will be displayed in cell
    XEMenu *menu = [self.arrayWithMenus objectAtIndex:indexPath.row];
    
    cell.textLabel.text = menu.name;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}


//method called when a cell in TableView is pressed
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEMobileMenuItemViewController *menuItemVC = [[XEMobileMenuItemViewController alloc] initWithNibName:@"XEMobileMenuItemViewController" bundle:nil];
    
    XEMenu *menu = [self.arrayWithMenus objectAtIndex:indexPath.row];
    
    menuItemVC.parentMenu = menu;
    
    [self.navigationController pushViewController:menuItemVC animated:YES];
}

//method called when the delete button get's pressed
-(void)deletePressed:(UIButton *)sender
{
    //push a confirmation window
    UIActionSheet *action = [[ UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles: nil];
    [action showInView:self.view];
    
    self.menuForDelete = [self.arrayWithMenus objectAtIndex:sender.tag];
    
}

//method called when a button in the confirmation window is clicked
-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    // buttonIndex == 0 => Delete button was clicked
    if( buttonIndex == 0)
    {
        [self deleteSelectedItem];
    }
}

//method that deletes the menu selected for removing
-(void)deleteSelectedItem
{
    RKParams *params = [RKParams params];
    [params setValue:self.menuForDelete.moduleSrl forParam:@"menu_srl"];
    [params setValue:self.menuForDelete.name forParam:@"title"];
    
    RKRequest *request= [[RKClient sharedClient] post:@"/index.php?module=mobile_communication&&act=procmobile_communicationMenuDelete" params:params delegate:self];
    //for identify in "request:didLoadResponse:"
    request.userData = @"delete_request";
    [self.indicator startAnimating];
}


-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
}

//method called when an array with objects was mapped from response
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

//method that makes a request to load the menus
-(void)getMenusRequest
{
    [self.indicator startAnimating];
    
    //map the response to an object
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
    
    //sends the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:resourcePath delegate:self];
    
}

//
//WINDOW FOR ADDING A MENU
//

//Method that pops up the window
-(void)addMenuBarButtonPressed
{
    UIView *transparentBackground = [[UIView alloc] initWithFrame:self.view.frame];
    transparentBackground.backgroundColor = [UIColor colorWithWhite:0.7 alpha:0.4];
    UIView *view = [[UIView alloc] initWithFrame:CGRectMake(80, 40, 160, 90)];
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

//method called when the back button in Add Menu Window
-(void)backButton:(UIButton *)sender
{
    UIView *view = [sender superview];
    UIView *background = [view superview];
    
    [view removeFromSuperview];
    [background removeFromSuperview];
}

//method called when the OK button is pressed
//sends a request to add the menu
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