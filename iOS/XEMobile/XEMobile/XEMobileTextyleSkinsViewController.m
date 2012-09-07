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
    
    //add Cancel button to navigation bar and sets an action to it
    self.navigationItem.leftBarButtonItem = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemCancel target:self action:@selector(cancelButtonPressed)];
    
    //add Save button to navigation bar and sets an action to it
    self.navigationItem.rightBarButtonItem = [[ UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemDone target:self action:@selector(saveButtonPressed) ];
    
    self.tableView.rowHeight = 130;
    
    //send requests for loading the skins
    [self loadSkins];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when the save button is pressed
-(void)saveButtonPressed
{
    //prepare request
    NSString *changeSkinXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<skin><![CDATA[%@]]></skin>\n<mid><![CDATA[textyle]]></mid>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleToolLayoutConfigSkin]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",self.textyle.skin,self.textyle.domain];
    
    //send request for save
    RKRequest *request = [[RKClient sharedClient] post:@"/index.php" params:[RKRequestSerialization serializationWithData:[changeSkinXML dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML ] delegate:self];
    request.userData = @"change_skin";
    [self.indicator startAnimating];
}

//method called when the cancel is pressed
-(void)cancelButtonPressed
{
    [self.navigationController dismissModalViewControllerAnimated:YES];
}


//method called when a response came
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{    
    //check if the user is logged
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController ];
    
    if ( [request.userData isEqualToString:@"change_skin"] )
        {
            [self.indicator stopAnimating];
            [self.navigationController dismissModalViewControllerAnimated:YES];
        }
}

//method called when the array with mapped objects is loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    
    if( objects.count != 0 )
        if( [[objects objectAtIndex:0] isKindOfClass:[XESkin class]] )
        {
            [self.indicator stopAnimating];
            self.arrayWithSkins = objects;
            [self.tableView reloadData];
        }
        else self.arrayWithSkins = [[NSArray alloc] init ];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
}

-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:self.errorMessage];
}

//send request for loading the skins
-(void)loadSkins
{
    RKObjectMapping *mapping = [ RKObjectMapping mappingForClass:[XESkin class]];
    [mapping mapKeyPath:@"id" toAttribute:@"id"];
    [mapping mapKeyPath:@"name" toAttribute:@"name"];
    [mapping mapKeyPath:@"description" toAttribute:@"description"];
    [mapping mapKeyPath:@"large_ss" toAttribute:@"largeSS"];
    [mapping mapKeyPath:@"small_ss" toAttribute:@"smallSS"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.skin"];
    
    //send the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php?module=mobile_communication&act=procmobile_communicationGetSkins" delegate:self];
    [self.indicator startAnimating];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    self.tableView = nil;
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}

//UITableView with XESkins

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
        //if cell can't be dequeued we create a new one
        
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

    //the skin that will be displayed in cell
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

//method called when a row in UITableView is pressed
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
