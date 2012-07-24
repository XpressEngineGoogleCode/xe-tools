//
//  XEMobileTextyleStatsViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 10/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleStatsViewController.h"

@interface XEMobileTextyleStatsViewController ()

@property (strong, nonatomic) XETextyleStats *dayStats;

@end

@implementation XEMobileTextyleStatsViewController
@synthesize tableView = _tableView;
@synthesize textyle = _textyle;
@synthesize dayStats = _dayStats;

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [object isKindOfClass:[XETextyleStats class]] ) 
    {
        self.dayStats = object;
        [self.tableView reloadData];
    }
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"Error!"];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Statistics";
    
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XETextyleStats class]];
    
    [mapping mapKeyPath:@"monday" toAttribute:@"monday"];
    [mapping mapKeyPath:@"tuesday" toAttribute:@"tuesday"];
    [mapping mapKeyPath:@"wednesday" toAttribute:@"wednesday"];
    [mapping mapKeyPath:@"thursday" toAttribute:@"thursday"];
    [mapping mapKeyPath:@"friday" toAttribute:@"friday"];
    [mapping mapKeyPath:@"saturday" toAttribute:@"saturday"];
    [mapping mapKeyPath:@"sunday" toAttribute:@"sunday"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationTextyleStats&site_srl=%@",self.textyle.siteSRL] delegate:self];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return 7;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *ident = @"CellIdentifier";
    UILabel *label;
    UITableViewCell *cell = [ tableView dequeueReusableCellWithIdentifier:ident];
    if (  cell == nil )
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ident];
        label = [[ UILabel alloc] initWithFrame:CGRectMake(200, 10, 60, 30)];
        label.backgroundColor = [UIColor clearColor];
        cell.textLabel.backgroundColor = [UIColor clearColor];
        [cell.contentView addSubview:label];
    }
    else 
    {
        label = [[cell.contentView subviews] objectAtIndex:1];
    }
    switch (indexPath.row) {
        case 0:
            cell.textLabel.text = @"Monday";
            label.text = self.dayStats.monday;
            break;
          
        case 1:
            cell.textLabel.text = @"Tuesday";
            label.text = self.dayStats.tuesday;
            break;
            
        case 2:
            cell.textLabel.text = @"Wednesday";
            label.text = self.dayStats.wednesday;
            break;
        case 3:
            cell.textLabel.text = @"Thursday";
            label.text = self.dayStats.thursday;
            break;
        case 4:
            cell.textLabel.text = @"Friday";
            label.text = self.dayStats.friday;
            break;
        case 5:
            cell.textLabel.text = @"Saturday";
            label.text = self.dayStats.saturday;
            break;
        case 6:
            cell.textLabel.text = @"Sunday";
            label.text = self.dayStats.sunday;
            break;
    }
    

    return cell;
}


-(void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}


@end
