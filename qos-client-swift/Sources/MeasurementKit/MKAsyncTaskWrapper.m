#import "MKAsyncTaskWrapper.h"

#import <mkall/MKAsyncTask.h>

@interface MKAsyncTaskWrapper () {
    
    @protected
    MKAsyncTask *_wrappedAsyncTask;
}

@end

@implementation MKAsyncTaskWrapper

-(instancetype)initWithAsyncTask:(MKAsyncTask *)task
{
    self = [super init];
    if (self) {
        _wrappedAsyncTask = task;
    }
    return self;
}

+ (MKAsyncTaskWrapper *)start:(NSDictionary *)settings {
    MKAsyncTask *task = [MKAsyncTask start:settings];
    
    if (task == nil) {
        return nil;
    }
    
    return [[MKAsyncTaskWrapper alloc] initWithAsyncTask: task];
}

- (BOOL)done {
    return [_wrappedAsyncTask done];
}

- (NSDictionary *)waitForNextEvent {
    return [_wrappedAsyncTask waitForNextEvent];
}

- (void)interrupt {
    [_wrappedAsyncTask interrupt];
}

@end
