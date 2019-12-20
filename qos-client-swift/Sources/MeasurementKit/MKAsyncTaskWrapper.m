/***************************************************************************
 * Copyright 2019 alladin-IT GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

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
