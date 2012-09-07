//
//  XEMobileTextyleCommentsListViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextyleCommentsListViewController.h"
#import "XEComment.h"
#import "XEMobileTextyleReplyCommentViewController.h"
#import "RestKit/RestKit.h"
#import "RestKit/RKRequestSerialization.h"
#import "XEUser.h"

@interface XEMobileTextyleCommentsListViewController ()

@property (strong, nonatomic) NSArray *arrayWithComments;
@property (strong, nonatomic) XEComment *commentForRemoving;

@end

@implementation XEMobileTextyleCommentsListViewController

@synthesize textyle = _textyle;
@synthesize arrayWithComments = _arrayWithComments;
@synthesize tableView = _tableView;
@synthesize commentForRemoving = _commentForRemoving;

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.navigationItem.title = @"Comments";
    self.tableView.rowHeight = 124;
}

-(void)viewWillAppear:(BOOL)animated
{
    [super viewWillAppear:animated];
    
    //send request to get all the comments
    [self loadComments];
}

-(void)viewWillDisappear:(BOOL)animated
{
    [super viewWillDisappear:animated];
    [[RKObjectManager sharedManager].requestQueue cancelRequestsWithDelegate:self];
    [[RKClient sharedClient].requestQueue cancelRequestsWithDelegate:self];
}

//method called when a response came
-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    if( [response.bodyAsString isEqualToString:[self isLogged]] ) [ self pushLoginViewController ]; 
}

//method called when an error occured
-(void)request:(RKRequest *)request didFailLoadWithError:(NSError *)error
{
    [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

-(void)objectLoader:(RKObjectLoader *)objectLoader didFailWithError:(NSError *)error
{
    NSLog(@"Error!");
   // [self showErrorWithMessage:@"There is a problem with your internet connection!"];
}

//method called when the array with objects was loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObjects:(NSArray *)objects
{
    if( [objectLoader.userData isEqualToString:@"load_comments"] )
    {
        self.arrayWithComments = objects;
        
        //sort the array with comments
        
       self.arrayWithComments = [self.arrayWithComments sortedArrayUsingComparator:^NSComparisonResult(id a, id b)
         {
             XEComment *comment1 = a;
             XEComment *comment2 = b;
             
             NSDate *date1 = comment1.data;
             NSDate *date2 = comment2.data;
             
             return [date1 compare:date2];
         }];
        
        //prepare the array for reply comments indentation
        self.arrayWithComments = [self prepareArrayForTableView:self.arrayWithComments];
        
        [self.tableView reloadData];
        self.commentForRemoving = nil;
    }
}

-(NSMutableArray *)prepareArrayForTableView:(NSArray *)array
{
    NSMutableArray *anotherArray = [[NSMutableArray alloc] init];
    NSMutableArray *arrayWithReplies = [[NSMutableArray alloc ] init ];
    
    //put reply comments and normal comments in two different arrays
    for(XEComment *comment in array)
    {
        // normal comment
        if( [comment.parentSRL isEqualToString:@"0"] )
        {
            [anotherArray addObject:comment];
        }
        else 
        //reply to a comment
        {
            [arrayWithReplies addObject:comment];
        }
    }
    
    //insert the reply comments after their parent comment
    for(XEComment *reply in arrayWithReplies)
    {
        int index = [self indexInArrayForCommentWithModuleSRL:reply.parentSRL inArray:anotherArray];
        [anotherArray insertObject:reply atIndex:index];
    }
    
    return anotherArray;
}

-(NSInteger) indexInArrayForCommentWithModuleSRL:(NSString *)comment_srl inArray:(NSMutableArray *)array
{   
    int index = 0;
    
    for(XEComment *comment in array)
    {
        index++;
        if( [comment.commentSRL isEqualToString:comment_srl] )
        {
            return index;
        }
    }

    return index;
}


//method called when an object was loaded
-(void)objectLoader:(RKObjectLoader *)objectLoader didLoadObject:(id)object
{
    if( [objectLoader.userData isEqualToString:@"delete_comment"])
    {
        XEUser *objectCast = object;
        if( [objectCast.auxVariable isEqualToString:@"Deleted successfully."] )
        {
            [self loadComments];
        }
    }
}

// method that send the request for comments
-(void)loadComments
{
    //map the response to an Object
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEComment class]];
    [mapping mapKeyPath:@"comment_srl" toAttribute:@"commentSRL"];
    [mapping mapKeyPath:@"module_srl" toAttribute:@"moduleSrl"];
    [mapping mapKeyPath:@"document_srl" toAttribute:@"documentSRL"];
    [mapping mapKeyPath:@"is_secret" toAttribute:@"isSecret"];
    [mapping mapKeyPath:@"content" toAttribute:@"content"];
    [mapping mapKeyPath:@"nickname" toAttribute:@"nickname"];
    [mapping mapKeyPath:@"email" toAttribute:@"emailAddress"];
    [mapping mapKeyPath:@"homepage" toAttribute:@"homePage"];
    [mapping mapKeyPath:@"ipaddress" toAttribute:@"ipAddress"];
    [mapping mapKeyPath:@"parent_srl" toAttribute:@"parentSRL"];
    [mapping mapKeyPath:@"regdate" toAttribute:@"regdate"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response.comment"];
    
    
    NSString *path = [NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationShowComments&module_srl=%@",self.textyle.moduleSrl];
    
    //sends the request
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:path usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.userData =@"load_comments";
     }];
    
}

//
// UITableView to display XEComments 
//

-(NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    return self.arrayWithComments.count;
}


-(UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    XEMobileCommentsViewCell *cell = [tableView dequeueReusableCellWithIdentifier:@"comment_cell"];
    
    if( cell == nil )
    {
        NSArray *topLevelObjects = [[NSBundle mainBundle] loadNibNamed:@"XEMobileCommentsViewCell" owner:nil options:nil];
        
        for(id currentObject in topLevelObjects)
        {
            if([currentObject isKindOfClass:[XEMobileCommentsViewCell class]])
            {
                cell = (XEMobileCommentsViewCell *)currentObject;
                break;
            }
        }
    }
    
    cell.delegate = self;
    
    XEComment *comment = [self.arrayWithComments objectAtIndex:indexPath.row];
    if( ![comment.parentSRL isEqualToString:@"0"] ) 
        [cell setReplyComment];
                   else [cell setNormalComment];
    
    if(![comment isPrivate]) [cell.visibilityButton setTitle:@"Public" forState:UIControlStateNormal];
    else [cell.visibilityButton setTitle:@"Private" forState:UIControlStateNormal];
    cell.nicknameLabel.text = comment.nickname;
    [cell.contentWebView loadHTMLString:comment.content baseURL:nil];
    
    return cell;
}

// XEMobileCommentsViewCellProtocol PROTOCOL METHODS IMPLEMENTED 

//
// method called when the reply button in UITableViewCell is pressed
//

-(void)replyButtonPressedInCell:(XEMobileCommentsViewCell *)cell
{
    XEMobileTextyleReplyCommentViewController *replyVC = [[XEMobileTextyleReplyCommentViewController alloc] initWithNibName:@"XEMobileTextyleReplyCommentViewController" bundle:nil];
    UINavigationController *replyNavCon =[[UINavigationController alloc] initWithRootViewController:replyVC];
        NSIndexPath *indexPath = [self.tableView indexPathForCell:cell];
        replyVC.comment = [self.arrayWithComments objectAtIndex:indexPath.row];
        replyVC.textyle = self.textyle;
    [self.navigationController presentModalViewController:replyNavCon animated:YES];
    
}

//
// method called when the delete button in UITableViewCell is pressed
//

-(void)deleteButtonPressedInCell:(XEMobileCommentsViewCell *)cell
{
    if( self.commentForRemoving == nil )
    {
    int index = [self.tableView indexPathForCell:cell].row;
    
    self.commentForRemoving = [self.arrayWithComments objectAtIndex:index];
    
    //confirmation
    UIActionSheet *action = [[UIActionSheet alloc] initWithTitle:@"Are you sure?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:@"Delete" otherButtonTitles:nil];
    [action showInView:self.view];
    }
}

//method called when a button in confirmation is pressed
-(void)actionSheet:(UIActionSheet *)actionSheet clickedButtonAtIndex:(NSInteger)buttonIndex
{
    if( buttonIndex == 0)
    {
        [self removeSelectedComment];
    }
    else self.commentForRemoving = nil;
}

-(void)removeSelectedComment
{
    NSLog(@"%@",self.commentForRemoving.commentSRL);
    NSString *requestDeleteXML = [ NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<comment_srl><![CDATA[%@]]></comment_srl>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleCommentItemDelete]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",self.commentForRemoving.commentSRL,self.textyle.domain];
    
    [self sendStringRequestToServer:requestDeleteXML withUserData:@"delete_comment"];
}

//
// method that sends a request
//

-(void)sendStringRequestToServer:(NSString*)request withUserData:(NSString *)userData
{
    RKObjectMapping *mapping = [RKObjectMapping mappingForClass:[XEUser class]];
    
    [mapping mapKeyPath:@"message" toAttribute:@"auxVariable"];
    
    [[RKObjectManager sharedManager].mappingProvider setMapping:mapping forKeyPath:@"response"];
    
    [[RKObjectManager sharedManager] loadObjectsAtResourcePath:@"/index.php" usingBlock:^(RKObjectLoader *loader)
     {
         loader.delegate = self;
         loader.params = [RKRequestSerialization serializationWithData:[request dataUsingEncoding:NSUTF8StringEncoding] MIMEType:RKMIMETypeXML];
         loader.userData = userData;
         loader.method = RKRequestMethodPOST;
     }];
}

//
// method called when the visibility button in UITableViewCell is pressed
//

-(void)visibilityButtonPressedInCell:(XEMobileCommentsViewCell *)cell
{
    int index = [self.tableView indexPathForCell:cell].row;
    XEComment *comment = [ self.arrayWithComments objectAtIndex:index];
    
    NSString *requestXML;
    if( [cell.visibilityButton.titleLabel.text isEqualToString:@"Private"] )
    {
        requestXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<comment_srl><![CDATA[%@]]></comment_srl>\n<page><![CDATA[1]]></page>\n<is_secret><![CDATA[N]]></is_secret>\n<module_srl><![CDATA[%@]]></module_srl>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleCommentItemSetSecret]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",comment.commentSRL,comment.moduleSrl,self.textyle.domain];
    }
    else if ( [cell.visibilityButton.titleLabel.text isEqualToString:@"Public"] )
    {
        requestXML = [NSString stringWithFormat:@"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<methodCall>\n<params>\n<comment_srl><![CDATA[%@]]></comment_srl>\n<page><![CDATA[1]]></page>\n<is_secret><![CDATA[Y]]></is_secret>\n<module_srl><![CDATA[%@]]></module_srl>\n<module><![CDATA[textyle]]></module>\n<act><![CDATA[procTextyleCommentItemSetSecret]]></act>\n<vid><![CDATA[%@]]></vid>\n</params>\n</methodCall>",comment.commentSRL,comment.moduleSrl,self.textyle.domain];
    }
    
    [self sendStringRequestToServer:requestXML withUserData:@"change_visibility"];
    
    if ( [cell.visibilityButton.titleLabel.text isEqualToString:@"Public"] )
        [cell.visibilityButton setTitle:@"Private" forState:UIControlStateNormal];
    else if ( [cell.visibilityButton.titleLabel.text isEqualToString:@"Private"] )
        [cell.visibilityButton setTitle:@"Public" forState:UIControlStateNormal];
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    [self setTableView:nil];
}

@end
