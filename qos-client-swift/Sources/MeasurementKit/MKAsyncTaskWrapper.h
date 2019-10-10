#ifndef MKAsyncTaskWrapper_h
#define MKAsyncTaskWrapper_h

#import <Foundation/Foundation.h>

@interface MKAsyncTaskWrapper : NSObject

+ (MKAsyncTaskWrapper *)start:(NSDictionary *)data;

- (BOOL)done;

- (NSDictionary *)waitForNextEvent;

- (void)interrupt;

@end

#endif /* MKAsyncTaskWrapper_h */
