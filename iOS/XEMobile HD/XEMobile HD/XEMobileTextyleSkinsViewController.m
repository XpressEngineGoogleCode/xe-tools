//
//  XEMobileTextyleSkinsViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 09/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleSkinsViewController.h"
#import "XESkin.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEMobileSkinCell.h"

@interface XEMobileTextyleSkinsViewController ()

{
    NSIndexPath *selected;
}

@property (strong, nonatomic) NSArray *arrayWithSkins;

@end

@implementation XEMobileTextyleSkinsViewController
@synthesize textyle = _textyle;
@synthesize arrayWithSkins = _arrayWithSkins;
@synthesize tableView = _tableView;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Skins";
    
    //put a Done button in the navigation bar
    self.navigationItem.rightBarButtonItem = [[ UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed) ];
    
    self.tableView.rowHeight = 130;
    
    //send a requests to get an array with all the Skins
    [self loadSkins];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when the Done button is pressed 
-(void)saveButtonPressed
{
    //prepare the request 
    
    NSString *changeSkinXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<skin><![CDATA[%@]]></skin>\n<mid><![CDATA[textyle]]></mid>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleToolLayoutConfigSkin]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",self.textyle.skin,self.textyle.domain];
    
    //sends the request
    RKRequest *request = [[RKClient sharedClient] post:@"/index.php" params:[RKRequestSerialization serializationWithData:[changeSkinXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML ] delegate:self];
    request.userData = @"change_skin";
    [self.indicator startAnimating];
}

//method called when the response was loaded
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController ];
    
    if ( [request.userData isEqualToString:@"change_skin"] )
        {
            [self.indicator stopAnimating];
            [self.detailViewController.navigationController popViewControllerAnimated:YES];
        }
}

//method called when the array with objects was loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    //check if the array is empty and if it contains XESkins
    if( objects.count != 0 )
        if( [[objects objectAtIndex:0] isKindOfClass:[XESkin class]] )
        {
            [self.indicator stopAnimating];
            self.arrayWithSkins = objects;
            [self.tableView reloadData];
        }
        else self.arrayWithSkins = [[NSArray alloc] init ];
}

//method called when an error occured
-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

//method that sends the request to obtain the skins
-(void)loadSkins
{
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XESkin class]];
    [mapping mapKeyPath:@"id" toAttribute:@"id"];
    [mapping mapKeyPath:@"name" toAttribute:@"name"];
    [mapping mapKeyPath:@"description" toAttribute:@"description"];
    [mapping mapKeyPath:@"large_ss" toAttribute:@"largeSS"];
    [mapping mapKeyPath:@"small_ss" toAttribute:@"smallSS"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.skin"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationGetSkins" delegate:self];
    [self.indicator startAnimating];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}

//TABLE VIEW with skins

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    // Return the number of rows in the section.
    return self.arrayWithSkins.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *CellIdentifier = @"Cell";
    XEMobileSkinCell *cell = [tableView dequeueReusableCellWithIdentifier:CellIdentifier];
    if( cell == nil )
    {
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"XEMobileSkinCell" owner:nil options:nil];
        
        for(id currentObject in topLevelObjects)
        {
            if([currentObject isKindOfClass:[XEMobileSkinCell class]])
            {
                cell = (XEMobileSkinCell *)currentObject;
                break;
            }
        }
    }

    //the skin that will be displayed in the cell
    XESkin *skin = [self.arrayWithSkins objectAtIndex:indexPath.row];
    
    [cell.skinView setImage:skin.image];
    
    if( [skin.id isEqualToString:self.textyle.skin]) 
    {
        [cell checkBox];
        selected = indexPath;
    }
    
    cell.label.text = skin.name;
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    return cell;
}


//method called when a cell in table view is pressed
-(void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    if( selected.row != indexPath.row )
    {
    XEMobileSkinCell *cell = (XEMobileSkinCell *)[tableView cellForRowAtIndexPath:indexPath];
    XESkin *skin = [self.arrayWithSkins objectAtIndex:indexPath.row];
    
    self.textyle.skin = skin.id;
    [cell checkBox];
    
    XEMobileSkinCell *deselectCell = (XEMobileSkinCell *)[tableView cellForRowAtIndexPath:selected];
    [deselectCell uncheckBox];
    selected = indexPath;
    }
}

@end
