//
//  XEMobilePageManagementViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobilePageManagementViewController.h"
#import "XEMobileAddPageViewController.h"
#import "XEPage.h"
#import "XEMobileEditContentPageViewController.h"
#import "XEMobileWidgetViewController.h"
#import "XEMobileEditPageViewController.h"

@interface XEMobilePageManagementViewController ()

@property (strong,nonatomic) NSArray *arrayWithPages;
@property (strong,nonatomic) XEPage *pageForDelete;
@end

@implementation XEMobilePageManagementViewController

@synthesize arrayWithPages = _arrayWithPages;
@synthesize tableView = _tableView;
@synthesize pageForDelete = _pageForDelete;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Page List";
    
    self.tableView.rowHeight = 100;
    
    //put an Add button on the navigation bar
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPost)];
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //send a request to get all Pages
    [self getPages];
}

-(void)getPages
{
    [self.indicator startAnimating];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEPage class]];
    
    [mapping mapKeyPath:@"module_srl" toAttribute:@"moduleSrl"];
    [mapping mapKeyPath:@"module" toAttribute:@"module"];
    [mapping mapKeyPath:@"page_type" toAttribute:@"pageType"];
    [mapping mapKeyPath:@"mid" toAttribute:@"mid"];
    [mapping mapKeyPath:@"content" toAttribute:@"content"];
    [mapping mapKeyPath:@"document_srl" toAttribute:@"document_srl"];
    [mapping mapKeyPath:@"browser_title" toAttribute:@"browserTitle"];
    [mapping mapKeyPath:@"layout_srl" toAttribute:@"layoutSRL"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.page"];
    
    NSDictionary *parametr = [[NSDictionary alloc] 
                              initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationDisplayPages", nil] 
                              forKeys:[NSArray arrayWithObjects:@"module",@"act", nil]];
    
    NSString *path = [@"/index.php" stringByAppendingQueryParameters:parametr];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
     }];
}


-(void)viewDidUnload
{
    [super viewDidUnload];
    
    self.tableView = nil;
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    //check if the user is logged out
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
    
    if( [request.userData isEqualToString:@"delete_page"] ) 
    {
        [self getPages];
        [self.indicator stopAnimating];
    }
}

//method called when an array with objects mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    self.arrayWithPages = objects;
    [self.indicator stopAnimating];
    [self.tableView reloadData];
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{

}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}


-(void)addPost
{
    XEMobileAddPageViewController *addPageVC = [[XEMobileAddPageViewController alloc] initWithNibName:@"XEMobileAddPageViewController" bundle:nil];

    [self.navigationController pushViewController:addPageVC animated:YES];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithPages.count;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath   
{
    static NSString *identifier =@"Identifier";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if(cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
        
        UIButton *buttonDelete = [ UIButton buttonWithType:UIButtonTypeRoundedRect];
        buttonDelete.frame = CGRectMake(290, 65, 70, 30);
        [buttonDelete setTitle:@"Delete" forState:UIControlStateNormal];
        [buttonDelete addTarget:self action:@selector(deletePageButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        buttonDelete.tag = indexPath.row;
        
        UIButton *buttonEdit = [ UIButton buttonWithType:UIButtonTypeRoundedRect];
        buttonEdit.frame = CGRectMake(210, 65, 70, 30);
        [buttonEdit setTitle:@"Edit" forState:UIControlStateNormal];
        [buttonEdit addTarget:self action:@selector(editPageButtonPressed:) forControlEvents:UIControlEventTouchUpInside];
        buttonEdit.tag = indexPath.row;
        
        cell.textLabel.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:buttonDelete]; 
        [cell.contentView addSubview:buttonEdit];
    }
    else {
        UIButton *buttonDelete = [[cell.contentView subviews] objectAtIndex:1];
        buttonDelete.tag = indexPath.row;
        
        UIButton *buttonEdit = [[cell.contentView subviews] objectAtIndex:2];
        buttonEdit.tag = indexPath.row;
         }
    
    XEPage *page =  [self.arrayWithPages objectAtIndex:indexPath.row];
    cell.textLabel.text = page.mid;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if( buttonIndex == 0 )
        {
            [self deleteTheSelectedPage];
        }
}

-(void)deleteTheSelectedPage
{
    NSDictionary *params = [[NSDictionary alloc] initWithObjects:[NSArray arrayWithObjects:@"deletePage",@"page",@"procPageAdminDelete",self.pageForDelete.moduleSrl, nil] forKeys:[NSArray arrayWithObjects:@"ruleset",@"module",@"act",@"module_srl", nil]];
    
    RKRequest *request = [[RKClient sharedClient] post:@"/" params:params delegate:self];
    request.userData = @"delete_page";
    
    [self.indicator startAnimating];

}

-(void)editPageButtonPressed:(UIButton *)button
{
    XEMobileEditPageViewController *editPageVC = [[XEMobileEditPageViewController alloc] initWithNibName:@"XEMobileEditPageViewController" bundle:nil];
    editPageVC.page = [self.arrayWithPages objectAtIndex:button.tag];
    [self.navigationController pushViewController:editPageVC animated:YES];
}

-(void)deletePageButtonPressed:(UIButton *)button
{
    UIActionSheet *action = [[UIActionSheet alloc ] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles: nil];
    [action showInView:self.view];
    
    self.pageForDelete = [self.arrayWithPages objectAtIndex:button.tag];
    
   }

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEPage *page = [self.arrayWithPages objectAtIndex:indexPath.row];
    if( [page.pageType isEqualToString:@"ARTICLE"] )
    {
    XEMobileEditContentPageViewController *editContentVC = [[XEMobileEditContentPageViewController alloc] initWithNibName:@"XEMobileEditContentPageViewController" bundle:nil];
        editContentVC.page = page;
        
    //we present a modal view controller over the split view controller
    UIWindow *window = [[UIApplication sharedApplication].windows objectAtIndex:0];
    //get a pointer to the split view controller
    UISplitViewController *splitVC = (UISplitViewController *)window.rootViewController;
        
    UINavigationController *navcon = [[UINavigationController alloc] initWithRootViewController:editContentVC];
    [splitVC presentModalViewController:navcon animated:YES];
    }
    else if( [page.pageType isEqualToString:@"WIDGET"] )
    {
        XEMobileWidgetViewController *webViewController = [[XEMobileWidgetViewController alloc] initWithNibName:@"XEMobileWidgetViewController" bundle:nil];
        webViewController.page = page;
        [self.navigationController pushViewController:webViewController animated:YES];

    }
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

@end
