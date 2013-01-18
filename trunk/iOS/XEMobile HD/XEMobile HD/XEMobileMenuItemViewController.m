//
//  XEMobileMenuItemViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileMenuItemViewController.h"
#import "XEMobileAddMenuItemViewController.h"
#import "XEMenuItem.h"
#import "XEMenu.h"
#import <RestKit/RKRequestSerialization.h>
#import "XEUser.h"

@interface XEMobileMenuItemViewController()

@property (strong, nonatomic) XEMenuItem *menuItemForDelete;

@end

@implementation XEMobileMenuItemViewController
@synthesize arrayWithMenuItems = _arrayWithMenuItems;
@synthesize parentMenu = _parentMenu;
@synthesize indicator = _indicator;
@synthesize tableView = _tableView;
@synthesize menuItemForDelete = _menuItemForDelete;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = self.parentMenu.name;
    
    self.tableView.rowHeight = 100;
    
    // put an Add button on the navigation bar
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addMenuItem)];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //send a request to load all the menu items
    
    [self makeRequestForMenuItems];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when an object was mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [objectLoader.userData isEqualToString:@"edit_menu"] )
    {
        XEMenuItem *menuItem = object;
        
        XEMobileAddMenuItemViewController *editMenuVC=[[XEMobileAddMenuItemViewController alloc] initWithNibName:@"XEMobileAddMenuItemViewController" bundle:nil];
        editMenuVC.editedMenu = menuItem;
        editMenuVC.parentModuleSRL = self.parentMenu.moduleSrl;
        
        [self.navigationController pushViewController:editMenuVC animated:YES];
        
    }
    else if ( [objectLoader.userData isEqualToString:@"delete_menu_item"] )
    {
        XEUser *auxObiect = object;
        if( [auxObiect.auxVariable isEqualToString:@"true"] )
            {
                [self makeRequestForMenuItems];
            }
    }
}

//method called when an array with objects was mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects 
{
    if( objects.count != 0 && [[objects objectAtIndex:0] isKindOfClass:[XEMenu class]] )
    for( XEMenu *menu in objects)
    {
        if ([menu.moduleSrl isEqualToString:self.parentMenu.moduleSrl]) 
        {
            self.arrayWithMenuItems = menu.menuItems;
            [self.tableView reloadData];
            [self.indicator stopAnimating];
        }
    }
    
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"load object %@",error);
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    //check if the user is logged out
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

-(void)makeRequestForMenuItems
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
    
    NSString *path = [@"/index.php" stringByAppendingQueryParameters:parametr];
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData = @"getMenuItems";
     } ];
    
}

//method called when the Add button is pressed
-(void)addMenuItem
{
    XEMobileAddMenuItemViewController *addMenuItemVC = [[XEMobileAddMenuItemViewController alloc] initWithNibName:@"XEMobileAddMenuItemViewController" bundle:nil];
        addMenuItemVC.parentModuleSRL = self.parentMenu.moduleSrl;
    [self.navigationController pushViewController:addMenuItemVC animated:YES];
}

// TABLE VIEW with menu items

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithMenuItems.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"Identifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(!cell)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        
        UIButton *buttonEdit = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        buttonEdit.frame = CGRectMake(150, 60, 70, 30);
        [buttonEdit setTitle:@"Edit" forState:UIControlStateNormal];
        [buttonEdit addTarget:self action:@selector(editMenuItem:) forControlEvents:UIControlEventTouchUpInside];
        [cell addSubview:buttonEdit];
        buttonEdit.tag = indexPath.row;
        
        UIButton *buttonDelete = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        buttonDelete.frame = CGRectMake(230, 60, 70, 30);
        [buttonDelete setTitle:@"Delete" forState:UIControlStateNormal];
        [buttonDelete addTarget:self action:@selector(deleteMenuItem:) forControlEvents:UIControlEventTouchUpInside];
        [cell addSubview:buttonDelete];
        buttonDelete.tag = indexPath.row;
    }
    else 
    {
        UIButton *buttonEdit = [[cell subviews] objectAtIndex:1];
        buttonEdit.tag = indexPath.row;
        
        UIButton *buttonDelete = [[cell subviews] objectAtIndex:2];
        buttonDelete.tag = indexPath.row;
    }
    XEMenuItem *menuItem = [self.arrayWithMenuItems objectAtIndex:indexPath.row];
    cell.textLabel.text = menuItem.name;
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}

//method called when the Edit button is pressed
//it sends a request to obtain aditional details about the menu item
-(void)editMenuItem:(UIButton *)button
{
    XEMenuItem *menuItem = [self.arrayWithMenuItems objectAtIndex:button.tag];
    
    NSString *string= [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?><methodCall><params><menu_item_srl><![CDATA[%d]]></menu_item_srl><module><![CDATA[menu]]></module><act><![CDATA[getMenuAdminItemInfo]]></act></params></methodCall>", menuItem.moduleSrl.intValue ];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEMenuItem class]];
    [mapping mapKeyPath:@"name" toAttribute:@"name"];
    [mapping mapKeyPath:@"menu_item_srl" toAttribute:@"moduleSrl"];
    [mapping mapKeyPath:@"open_window" toAttribute:@"openWindow"];
    [mapping mapKeyPath:@"url" toAttribute:@"URL"];
    [mapping mapKeyPath:@"moduleType" toAttribute:@"type"];
    [[RKClient sharedClient] setValue:@"application/xml" forHTTPHeaderField:@"Accept"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.menu_item"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.method = RKRequestMethodPOST;
         loader.params = [RKRequestSerialization serializationWithData:[string 
                                                                        dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
         loader.delegate = self;
         loader.userData = @"edit_menu";
         
     }];
}

//method called when the Delete button is pressed
-(void)deleteMenuItem:(UIButton *)button
{
    // push a Confirmation Window
    UIActionSheet *action = [[UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles:nil];
    [action showInView:self.view];
    
    //set the menu item that will be removed
    self.menuItemForDelete = [self.arrayWithMenuItems objectAtIndex:button.tag];
}
//method called when a button in the Confirmation window was pressed
-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if( buttonIndex == 0 )
    {
        [self deleteSelecteditem];
    }
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}

//method that removes the selected menu item 
-(void)deleteSelecteditem
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    [mapping mapKeyPath:@"value" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    RKParams *params = [RKParams params];
    [params setValue:@"mobile_communication"  forParam:@"module"];
    [params setValue:@"procmobile_communicationDeleteMenuItem" forParam:@"act"];
    [params setValue:self.parentMenu.moduleSrl forParam:@"menu_srl"];
    [params setValue:self.menuItemForDelete.moduleSrl forParam:@"menu_item_srl"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.method = RKRequestMethodPOST;
         loader.params = params;
         loader.userData = @"delete_menu_item";
     }];
}


@end