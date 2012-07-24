//
//  XEMobileTextEditorViewController.m
//  XEMobile
//
//  Created by Iulian-Bogdan Vlad on 12/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "XEMobileTextEditorViewController.h"

@interface XEMobileTextEditorViewController ()

@end

@implementation XEMobileTextEditorViewController
@synthesize textView;
@synthesize keyboardToolbar;
@synthesize frameButton = _frameButton;

-(void)viewDidLoad
{
    [super viewDidLoad];
    
    [textView becomeFirstResponder];
    
    if( UIDeviceOrientationIsLandscape(self.interfaceOrientation) )
    {
        [self setLandscapeLayout];
    }
    else if( UIDeviceOrientationIsPortrait(self.interfaceOrientation) )
    {
        [self setPortaitLayout];
    }
    
    [self keyboardWillShow:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillShow:) name:UIKeyboardWillShowNotification object:nil];
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(keyboardWillHide:) name:UIKeyboardWillHideNotification object:nil];

}

-(IBAction)bold:(id)sender
{
    [self insertHtmlTag:@"<strong>" withCloseTag:@"</strong>"];
}

-(IBAction)italic:(id)sender 
{
    [self insertHtmlTag:@"<i>" withCloseTag:@"</i>"];
}

-(IBAction)underline:(id)sender
{
    [self insertHtmlTag:@"<u>" withCloseTag:@"</u>"];
}

-(IBAction)striked:(id)sender
{
    [self insertHtmlTag:@"<strike>" withCloseTag:@"</strike>"];
}

-(IBAction)li:(id)sender
{
    [self insertHtmlTag:@"<li>" withCloseTag:@"</li>"];
}

-(IBAction)ul:(id)sender
{
    [self insertHtmlTag:@"<ul>" withCloseTag:@"</ul>"];
}

-(void)keyboardWillHide:(NSNotification *)note
{
    
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.2];
    
    CGRect frame = self.keyboardToolbar.frame;
    frame.origin.y = self.view.frame.size.height;
    self.keyboardToolbar.frame = frame;
    
    [UIView commitAnimations];
}

-(void)keyboardWillShow:(NSNotification *)note
{
    [UIView beginAnimations:nil context:NULL];
    [UIView setAnimationDuration:0.3];
    
    CGRect frame = self.keyboardToolbar.frame;

    if( UIDeviceOrientationIsPortrait(self.interfaceOrientation))
        frame.origin.y = self.view.frame.size.height - 308.0;
    else if( UIDeviceOrientationIsLandscape(self.interfaceOrientation) ) frame.origin.y = self.view.frame.size.height - 396;
    self.keyboardToolbar.frame = frame;
    
    [UIView commitAnimations];
}

-(void)insertHtmlTag:(NSString *)tag withCloseTag:(NSString *)closeTag
{
    NSRange range = self.textView.selectedRange;
    NSString *contentText = self.textView.text;
    
    if( range.length == 0 )
    {
        self.textView.text = [self insertSubstring:[NSString stringWithFormat:@"%@%@",tag,closeTag] inString:self.textView.text atIndex:range.location];
        self.textView.selectedRange = NSMakeRange(range.location + tag.length, 0);
    }
    else 
    {
        NSString *firstPartOfString = [contentText substringToIndex:range.location];
        NSString *boldString = [contentText substringWithRange:range];
        
        firstPartOfString = [ firstPartOfString stringByAppendingString:tag];
        firstPartOfString = [ firstPartOfString stringByAppendingString:boldString];
        firstPartOfString = [ firstPartOfString stringByAppendingString:closeTag];
        
        if( contentText.length > range.location + range.length )
        {
            firstPartOfString = [firstPartOfString stringByAppendingString:[contentText substringFromIndex:range.length + range.location]];
        }
        
        self.textView.text = firstPartOfString;
    }
}

-(NSString *)prepareStringForDisplay:(NSString *)string
{   
    return [string stringByReplacingOccurrencesOfString:@"<br/>" withString:@"\n"];
}

-(NSString *)insertSubstring:(NSString *)substring inString:(NSString *)string atIndex:(NSUInteger)loc
{
    return [NSString stringWithFormat:@"%@%@%@",[string substringToIndex:loc],substring,[string substringFromIndex:loc]];
}

-(void)willRotateToInterfaceOrientation:(UIInterfaceOrientation)toInterfaceOrientation duration:(NSTimeInterval)duration
{
    if( UIDeviceOrientationIsLandscape(toInterfaceOrientation))
    {
        [self setLandscapeLayout];
    }
    else if( UIDeviceOrientationIsPortrait(toInterfaceOrientation))
    {
        [self setPortaitLayout];
    }
}

-(void)setLandscapeLayout
{
    self.frameButton.frame = CGRectMake(15, 125, 745, 200);
    self.textView.frame = CGRectMake(25, 155, 725, 150);
}

-(void)setPortaitLayout
{
    self.frameButton.frame = CGRectMake(25, 135, 988, 489);
    self.textView.frame = CGRectMake(39, 150, 960, 431);
}

- (void)viewDidUnload 
{
    self.keyboardToolbar = nil;
    self.textView = nil;
    self.frameButton = nil;
    [super viewDidUnload];
}

@end
