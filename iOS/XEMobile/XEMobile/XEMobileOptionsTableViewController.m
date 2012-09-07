//
//  XEMobileOptionsTableViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileOptionsTableViewController.h"

//
// ViewController used to select only one option or more options from a set of options
//

@interface XEMobileOptionsTableViewController ()

{
    int indexSelected;
}

@end

@implementation XEMobileOptionsTableViewController
@synthesize settings = _settings;
@synthesize delegateData = _delegateData;
@synthesize selected = _selected;
@synthesize type = _type;


-(void)setDelegateData:(NSArray *)delegateData
{
    _delegateData = [delegateData sortedArrayUsingSelector:@selector(caseInsensitiveCompare:)];
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.delegateData.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    UITableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    
    UILabel *label = nil;
    
    if( cell == nil )
    {
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:CellIdentifier];
        
        label = [[UILabel alloc] initWithFrame:CGRectMake(190, 10, 30, 30)];
        label.tag = 1111;
        [cell.contentView addSubview:label];
        cell.textLabel.backgroundColor = [UIColor clearColor];
    }
    else
    {
        label = (UILabel *)[cell viewWithTag:1111];
    }
    
    NSString *string = [self.delegateData objectAtIndex:indexPath.row];
    cell.textLabel.text = string;
    
    if(self.type == selectedLangs)
    {
        if( [self.selected containsObject:string] ) 
        {
            label.backgroundColor = [UIColor greenColor];
        }
        else 
        {
            label.backgroundColor = [UIColor redColor];
        }
    }
    else 
        if( [string isEqual:self.selected] ) 
        {
            label.backgroundColor = [UIColor greenColor];
            indexSelected = indexPath.row;
        }
        else 
        {
            label.backgroundColor = [UIColor redColor];
        }
    
    return cell;
}


-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [tableView cellForRowAtIndexPath:[NSIndexPath indexPathForRow:indexSelected inSection:0]];
    UILabel *selected = (UILabel *)[cell viewWithTag:1111];
    selected.backgroundColor = [UIColor redColor];
    
    UITableViewCell *newSelectedCell = [tableView cellForRowAtIndexPath:indexPath];
    UILabel *newSelected = (UILabel *)[newSelectedCell viewWithTag:1111];
    newSelected.backgroundColor = [UIColor greenColor];
    indexSelected = indexPath.row;
    
    if(self.type == langD)
    {
        NSString *string = [self.delegateData objectAtIndex:indexPath.row];
        NSString *final = [[self.settings.languages allKeysForObject:string] objectAtIndex:0];
        self.settings.defaultLanguage = final;
        self.selected = final;
    }
    else if (self.type == timeZ)
    {
        NSString *timezone = [self.delegateData objectAtIndex:indexPath.row];
        NSString *final = [[self.settings.timezones allKeysForObject:timezone] objectAtIndex:0];
        self.settings.timezone = final;
        self.selected = timezone;
    }
    else if (self.type == selectedLangs)
    {
        NSString *lang = [self.delegateData objectAtIndex:indexPath.row];
        
        if( [self.settings.selectedLanguages containsObject:lang] )
            [self.settings.selectedLanguages removeObject:lang];
        else [self.settings.selectedLanguages addObject:lang];
        
        [self.tableView reloadData];
    }
}

@end
