//
//  XEMobileMembersManagementViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 15/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileMembersManagementViewController.h"
#import "XEMobileLoginViewController.h"
#import "XEMobileMemberEditViewController.h"
#import "XEMember.h"

@interface XEMobileMembersManagementViewController ()

@property (strong, nonatomic) NSArray *arrayWithMembers;

@end

@implementation XEMobileMembersManagementViewController
@synthesize arrayWithMembers = _arrayWithMembers;
@synthesize tableView = _tableView;



-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //send request to load the members
    [self loadMembers];
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    //set the title in navigation bar
    self.navigationItem.title = @"Members List";
    
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

//method called when a response was received
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

//method called when an array with objects was mapped from the response
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(id)object
{
    self.arrayWithMembers = object;
    [self.tableView reloadData];
    
    [self.indicator stopAnimating];
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error 
{
    NSLog(@"Error!");
}

//TABLE VIEW with members

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithMembers.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"Identifier";
    
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    
    if(cell == nil)
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:identifier];
    }
    
    XEMember *member = [self.arrayWithMembers objectAtIndex:indexPath.row];
    cell.textLabel.text = member.email;
    cell.accessoryType = UITableViewCellAccessoryDisclosureIndicator;
    return cell;
}

-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEMember *member = [self.arrayWithMembers objectAtIndex:indexPath.row];
    XEMobileMemberEditViewController *memberEditVC = [[ XEMobileMemberEditViewController alloc] initWithNibName:@"XEMobileMemberEditViewController" bundle:nil];
    memberEditVC.member = member;
    [self.navigationController pushViewController:memberEditVC animated:YES];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];    
    
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method that sends the request that returns all the members
-(void)loadMembers
{
    NSDictionary *parametr = [[NSDictionary alloc] 
                              initWithObjects:[NSArray arrayWithObjects:@"mobile_communication",@"procmobile_communicationDisplayMembers", nil] 
                              forKeys:[NSArray arrayWithObjects:@"module",@"act", nil]];
    
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEMember class]];
    [mapping mapKeyPath:@"nickname" toAttribute:@"nickname"];
    [mapping mapKeyPath:@"member_srl" toAttribute:@"member_srl"];
    [mapping mapKeyPath:@"user_id" toAttribute:@"user_id"];
    [mapping mapKeyPath:@"email" toAttribute:@"email"];
    [mapping mapKeyPath:@"password" toAttribute:@"password"];
    [mapping mapKeyPath:@"denied" toAttribute:@"denied"];
    [mapping mapKeyPath:@"allow_mailing" toAttribute:@"allow_mailing"];
    [mapping mapKeyPath:@"allow_message" toAttribute:@"allow_message"];
    [mapping mapKeyPath:@"description" toAttribute:@"description"];
    [mapping mapKeyPath:@"find_account_question" toAttribute:@"question"];
    [mapping mapKeyPath:@"secret_answer" toAttribute:@"secretAnswer"];
    [mapping mapKeyPath:@"is_admin" toAttribute:@"isAdmin"];
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.user"];
    
    NSString *path = [@"/index.php" stringByAppendingQueryParameters:parametr];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path delegate:self];
    [self.indicator startAnimating];
}

-(void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}

@end
