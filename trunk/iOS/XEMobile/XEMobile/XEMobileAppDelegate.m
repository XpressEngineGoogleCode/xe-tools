//
//  XEMobileAppDelegate.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 05/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileAppDelegate.h"


#import <RestKit/RestKit.h>

@implementation XEMobileAppDelegate

@synthesize window = _window;
@synthesize viewController = _viewController;

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    self.window = [[UIWindow alloc] initWithFrame:[[UIScreen mainScreen] bounds]];
    // Override point for customization after application launch.
    
    self.viewController = [[XEMobileLoginViewController alloc] initWithNibName:@"XEMobileLoginViewController" bundle:nil];
    UINavigationController *navCon = [[UINavigationController alloc] initWithRootViewController:self.viewController];
    
    self.window.rootViewController = navCon;
    [self.window makeKeyAndVisible];
    return YES;
}

- (void)application:(UIApplication*)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData*)deviceToken
{
	NSLog(@"My token is: %@", deviceToken);
    NSLog(@"HOST: %@/index.php&module=mobile_communication&act=procmobile_communicationRegistreForPopUp",[RKObjectManager sharedManager].baseURL.absoluteString);
    
    NSString *deviceTokenString = [[[deviceToken description]
                                    stringByTrimmingCharactersInSet:[NSCharacterSet characterSetWithCharactersInString:@"<>"]] 
                                   stringByReplacingOccurrencesOfString:@" " 
                                   withString:@""];
    
    NSLog(@"My token string before: %@",deviceTokenString);
    
    deviceTokenString = [deviceTokenString stringByReplacingOccurrencesOfString:@" " withString:@""];
    deviceTokenString = [deviceTokenString stringByReplacingOccurrencesOfString:@"<" withString:@""];
    deviceTokenString = [deviceTokenString stringByReplacingOccurrencesOfString:@">" withString:@">"];
    
    NSLog(@"My token string is %@",deviceTokenString);
    
    [[RKClient sharedClient] get:[NSString stringWithFormat:@"/index.php?module=mobile_communication&act=procmobile_communicationRegistreForPopUp&id=%@",deviceTokenString] delegate:self];
    
    
}

-(void)request:(RKRequest *)request didLoadResponse:(RKResponse *)response
{
    NSLog(@"%@",response.bodyAsString);
}

- (void)application:(UIApplication*)application didFailToRegisterForRemoteNotificationsWithError:(NSError*)error
{
	NSLog(@"Failed to get token, error: %@", error);
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo 
{
    NSLog(@"%@",userInfo);
    
    NSDictionary *messageDictionary = [userInfo objectForKey:@"aps"];
    
    NSString *message = [messageDictionary objectForKey:@"alert"];
    
    UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"New comment!"
                                                    message:message
                                                   delegate:nil
                                          cancelButtonTitle:@"OK"
                                          otherButtonTitles:nil];
    [alert show];
}

- (void)applicationWillResignActive:(UIApplication *)application
{
    // Sent when the application is about to move from active to inactive state. This can occur for certain types of temporary interruptions (such as an incoming phone call or SMS message) or when the user quits the application and it begins the transition to the background state.
    // Use this method to pause ongoing tasks, disable timers, and throttle down OpenGL ES frame rates. Games should use this method to pause the game.
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    // Use this method to release shared resources, save user data, invalidate timers, and store enough application state information to restore your application to its current state in case it is terminated later. 
    // If your application supports background execution, this method is called instead of applicationWillTerminate: when the user quits.
}

- (void)applicationWillEnterForeground:(UIApplication *)application
{
    // Called as part of the transition from the background to the inactive state; here you can undo many of the changes made on entering the background.
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    NSString *address = [[ NSUserDefaults standardUserDefaults] objectForKey:@"last_address"];
    NSLog(@"scoate din user defaults %@",address);
    [RKObjectManager setSharedManager:[ RKObjectManager managerWithBaseURLString:[NSString stringWithFormat:@"http://%@",address]]];
}

- (void)applicationWillTerminate:(UIApplication *)application
{
    // Called when the application is about to terminate. Save data if appropriate. See also applicationDidEnterBackground:.
}

@end
