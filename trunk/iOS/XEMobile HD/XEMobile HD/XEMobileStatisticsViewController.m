//
//  XEMobileStatisticsViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 13/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileStatisticsViewController.h"
#import "XEMobileStatsCell.h"


@interface XEMobileStatisticsViewController ()

@property (strong, nonatomic) NSArray *arrayWithStats;

@end

@implementation XEMobileStatisticsViewController
@synthesize tableView = _tableView;
@synthesize arrayWithStats = _arrayWithStats;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Statistics";
    
    self.tableView.rowHeight = 85;
    [self loadStats];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    [self.indicator stopAnimating];
     if( [objects count] != 0 && [[ objects objectAtIndex:0] isKindOfClass:[XEStats class]] )
     {
         self.arrayWithStats =objects;
         [self.tableView reloadData];
     }
}

-(void)loadStats
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEStats class]];
    [mapping mapKeyPath:@"date" toAttribute:@"date"];
    [mapping mapKeyPath:@"unique_visitor" toAttribute:@"uniqueVisitor"];
    [mapping mapKeyPath:@"pageview" toAttribute:@"pageViews"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.day"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationViewerData" delegate:self];
    [self.indicator startAnimating];
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    NSLog(@"Error! %@",error.localizedDescription);
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [self pushLoginViewController];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"%@",error.localizedDescription);
}

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithStats.count;
}

-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *identifier = @"Identifier";
    XEMobileStatsCell *cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    if( cell == nil )
        {
            NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"XEMobileStatsCell" owner:nil options:nil];
            
            for(id currentObject in topLevelObjects)
            {
                if([currentObject isKindOfClass:[XEMobileStatsCell class]])
                {
                    cell = (XEMobileStatsCell *)currentObject;
                    break;
                }
            }

        }
    XEStats *stats;
    switch (indexPath.row) {
        case 0:
            stats = [self.arrayWithStats objectAtIndex:0];
            break;
        case 1:
            stats = [self.arrayWithStats objectAtIndex:1];
            break;
        case 2:
            stats = [self.arrayWithStats objectAtIndex:2];
            break;
        case 3:
            stats = [self.arrayWithStats objectAtIndex:3];
            break;
        case 4:
            stats = [self.arrayWithStats objectAtIndex:4];
            break;
        case 5:
            stats = [self.arrayWithStats objectAtIndex:5];
            break;
        case 6:
            stats = [self.arrayWithStats objectAtIndex:6];
            break;
    }
    
    cell.dayLabel.text = stats.date;
    cell.uniqueVisitorsLabel.text = stats.uniqueVisitor;
    cell.pageViewsLabel.text = stats.pageViews;
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}


- (void)viewDidUnload
{
    [self setTableView:nil];
    [super viewDidUnload];
}

@end
