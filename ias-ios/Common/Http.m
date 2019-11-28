/*!
    \file Http.m
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2019-11-13

    Copyright (C) 2016 - 2019 zafaco GmbH

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3 
    as published by the Free Software Foundation.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

#import "Http.h"




/**************************** Loging Level ****************************/

#ifdef DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelDebug;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelWarning;
#endif




@interface Http ()

/**************************** Global Variables ****************************/

@property (nonatomic, strong) Tool *tool;
@property (nonatomic, strong) NSString *errorDomainHttpRequest;
@property (nonatomic, strong) NSString *errorDomainUrlResponse;


@end




@implementation Http


/**************************** Public Functions ****************************/

-(Http*)init
{
    [DDLog removeAllLoggers];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    [DDTTYLogger sharedInstance].logFormatter = [LogFormatter new];

    self.tool                           = [Tool new];
    self.httpRequestTimeout             = 3.0;
    self.errorDomainHttpRequest         = @"HttpRequest";
    self.errorDomainUrlResponse         = @"NSHttpUrlResponse";
 
    return self;
}




/**************************** Public Delegate Functions HttpRequest ****************************/

-(void)httpRequestToUrl:(NSURL*)url type:(NSString *)type header:(NSDictionary *)header body:(NSString *)body
{
    DDLogDebug(@"HTTP %@ request to url: %@", type, [url absoluteString]);
    
    NSURLSessionConfiguration *sessionConfiguration = [NSURLSessionConfiguration defaultSessionConfiguration];
    [sessionConfiguration setRequestCachePolicy:NSURLRequestReloadIgnoringCacheData];
    [sessionConfiguration setURLCache:nil];
    [sessionConfiguration setTimeoutIntervalForRequest:self.httpRequestTimeout];
    [sessionConfiguration setHTTPAdditionalHeaders:@{ @"User-Agent": @"Mozilla/5.0" }];
    
    if (header)
    {
        [sessionConfiguration setHTTPAdditionalHeaders:header];
    }
    
    NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfiguration delegate:(id)self delegateQueue:[NSOperationQueue mainQueue]];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    if ([type isEqualToString:@"GET"])
    {
        request.HTTPMethod  = type;
    }
    else if ([type isEqualToString:@"POST"])
    {
        request.HTTPBody    = [body dataUsingEncoding:NSUTF8StringEncoding];
        request.HTTPMethod  = type;
    }
    else
    {
        DDLogError(@"Only GET or POST allowed");
        
        NSError *error = [self.tool getError:1 description:@"http request type != GET or POST" domain:self.errorDomainHttpRequest];
        
        [self httpRequestToUrl:url response:nil data:nil didCompleteWithError:error];
        return;
    }
    
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error)
                                      {
                                          if (error)
                                          {
                                              DDLogError(@"NSURLSession: error received");
                                              
                                              NSURL *errorUrl             = [NSURL URLWithString:@"-"];
                                              if ([error.userInfo objectForKey:@"NSErrorFailingURLKey"])
                                              {
                                                  errorUrl = [error.userInfo objectForKey:@"NSErrorFailingURLKey"];
                                              }
                                              
                                              [self httpRequestToUrl:errorUrl response:response data:data didCompleteWithError:error];
                                          }
                                          else
                                          {
                                              long httpStatusCode = ((NSHTTPURLResponse *)response).statusCode;
                                              
                                              DDLogDebug(@"NSURLSession: response received with status code: %li", httpStatusCode);
                                              
                                              if (httpStatusCode != 200)
                                              {
                                                  error = [self.tool getError:httpStatusCode description:@"http response != 200" domain:self.errorDomainUrlResponse];
                                              }
                                              
                                              [self httpRequestToUrl:response.URL response:response data:data didCompleteWithError:error];
                                          }
                                      }];
    
    [dataTask resume];
}




/**************************** HttpRequestDelegate Callback Initiator ****************************/

-(void)httpRequestToUrl:(NSURL *)url response:(NSURLResponse *)response data:(NSData *)data didCompleteWithError:(NSError *)error
{
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.httpRequestDelegate && [self.httpRequestDelegate respondsToSelector:@selector(httpRequestToUrl:response:data:didCompleteWithError:)])
                       {
                           [self.httpRequestDelegate httpRequestToUrl:url response:response data:data didCompleteWithError:error];
                       }
                   });
}


@end
