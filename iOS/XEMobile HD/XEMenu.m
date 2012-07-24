//
//  XEMenu.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 18/06/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMenu.h"

@implementation XEMenu
@synthesize menuItems = _menuItems;
@synthesize name = _name;

-(NSMutableArray *)menuItems
{
    if(!_menuItems) _menuItems = [[NSMutableArray alloc] init ];
    return _menuItems;
    
}


@end
