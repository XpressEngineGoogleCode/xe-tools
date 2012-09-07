//
//  XEMobileTextylePostsTableViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextylePostsTableViewController.h"
#import "XEMobileTextyleEditPostViewController.h"
#import "XEMobileTextyleAddPostViewController.h"

@interface XEMobileTextylePostsTableViewController()

@property (strong, nonatomic) NSArray *arrayWithPublishedTextylePosts;
@property (strong, nonatomic) NSArray *arrayWithSavedTextylePosts;

@property (strong, nonatomic) NSArray *arrayForTableView;

@end

@implementation XEMobileTextylePostsTableViewController

@synthesize textyleItem = _textyleItem;
@synthesize arrayWithSavedTextylePosts = _arrayWithSavedTextylePosts;
@synthesize arrayWithPublishedTextylePosts = _arrayWithPublishedTextylePosts;
@synthesize arrayForTableView = _arrayForTableView;
@synthesize selectPublishOrSave = _selectPublishOrSave;
@synthesize tableView = _tableView;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Posts";
    
    //method called when the Add button is pressed
    
    self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemAdd target:self action:@selector(addPostButtonPressed)];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when a response is received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"error!");
}

//method called when an array with objects is mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    [self.indicator stopAnimating];
    if([objectLoader.userData isEqualToString:@"published"])
    {
        self.arrayWithPublishedTextylePosts = objects;
    }
    else if( [objectLoader.userData isEqualToString:@"saved"] )
    {
        self.arrayWithSavedTextylePosts = objects;
    }
    if( self.selectPublishOrSave.selectedSegmentIndex == 0 )
        self.arrayForTableView = self.arrayWithPublishedTextylePosts;
    else self.arrayForTableView = self.arrayWithSavedTextylePosts;
    [self.tableView reloadData];
}

//when the view appears, two requests are made: one for publish posts and one for saved posts

- (void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XETextylePost class]];
    
    [mapping mapKeyPath:@"document_srl" toAttribute:@"documentSRL"];
    [mapping mapKeyPath:@"module_srl" toAttribute:@"moduleSrl"];
    [mapping mapKeyPath:@"category_srl" toAttribute:@"categorySRL"];
    [mapping mapKeyPath:@"title" toAttribute:@"title"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.post"];
    
    NSDictionary *paramsForPublised = [NSDictionary dictionaryWithKeysAndObjects:@"module",@"mobile_communication",@"act",@"procmobile_communicationTextylePostList",@"module_srl",self.textyleItem.moduleSrl,@"published",@"1", nil];
    
    NSDictionary *paramsForSaved = [NSDictionary dictionaryWithKeysAndObjects:@"module",@"mobile_communication",@"act",@"procmobile_communicationTextylePostList",@"module_srl",self.textyleItem.moduleSrl,@"published",@"-1", nil];
    
    NSString *pathForPublished = [@"/index.php" stringByAppendingQueryParameters:paramsForPublised];
    
    //send the request for the publish posts
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:pathForPublished usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData = @"published";
     }];
    
    NSString *pathForSaved = [@"/index.php" stringByAppendingQueryParameters:paramsForSaved];
    
    //send the request for the saved posts
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:pathForSaved usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData = @"saved";
     }];
    
    [self.indicator startAnimating];
}

//method called when the selectPublishOrSaved SegmentedControl changed his selection segment
-(IBAction)segmentedControlChanged:(UISegmentedControl *)sender
{
    switch (sender.selectedSegmentIndex) 
    {
        case 0:
            self.arrayForTableView = self.arrayWithPublishedTextylePosts;
            break;
            
        case 1:
            self.arrayForTableView = self.arrayWithSavedTextylePosts;
            break;
    }
    [self.tableView reloadData];
}

//TABLE VIEW with posts

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return [self.arrayForTableView count];
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    if( cell == nil )
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
    }
    
    //the post object that will be displayed in cell
    XETextylePost *post = [self.arrayForTableView objectAtIndex:indexPath.row];
    
    cell.textLabel.text = post.title;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEMobileTextyleEditPostViewController *editPostVC = [[XEMobileTextyleEditPostViewController alloc] initWithNibName:@"XEMobileTextyleEditPostViewController" bundle:nil];
    
    editPostVC.post = [self.arrayForTableView objectAtIndex:indexPath.row];
    editPostVC.textyle = self.textyleItem;
    
    UINavigationController *navCon = [[UINavigationController alloc] initWithRootViewController:editPostVC];
    
    [self.navigationController presentModalViewController:navCon animated:YES];
}

//method called when the Add button is pressed
-(void)addPostButtonPressed
{
    XEMobileTextyleAddPostViewController *addPostVC = [[ XEMobileTextyleAddPostViewController alloc] initWithNibName:@"XEMobileTextyleAddPostViewController" bundle:nil];
    addPostVC.textyle = self.textyleItem;
    
    UINavigationController *navCon = [[UINavigationController alloc] initWithRootViewController:addPostVC];
    
    [self.navigationController presentModalViewController:navCon animated:YES];
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    
    self.selectPublishOrSave = nil;
    self.tableView = nil;
}


@end
