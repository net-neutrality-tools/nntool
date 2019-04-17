/*
 *********************************************************************************
 *                                                                               *
 *       ..--== zafaco GmbH ==--..                                               *
 *                                                                               *
 *       Website: http://www.zafaco.de                                           *
 *                                                                               *
 *       Copyright 2019                                                          *
 *                                                                               *
 *********************************************************************************
 */

/*!
 *      \author zafaco GmbH <info@zafaco.de>
 *      \date Last update: 2019-01-02
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#import <Foundation/Foundation.h>
#import "Tool.h"

@import CocoaLumberjack;




@protocol HttpRequestDelegate <NSObject>

/**************************** Required Delegate Functions ****************************/

@required
-(void)httpRequestToUrl:(NSURL *)url response:(NSURLResponse *)response data:(NSData *)data didCompleteWithError:(NSError *)error;
@end




@interface Http : NSObject




/**************************** Public Variables ****************************/

@property (nonatomic) NSTimeInterval httpRequestTimeout;                /**< HttpRequest Timeout */
@property (nonatomic, strong) id httpRequestDelegate;                   /**< HttpRequest Protocol Delegate */




/**************************** Public Delegate Functions HttpRequest ****************************/

-(void)httpRequestToUrl:(NSURL*)url type:(NSString*)type header:(NSDictionary*)header body:(NSString*)body;


@end
