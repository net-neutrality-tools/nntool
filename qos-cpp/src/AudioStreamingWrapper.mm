/***************************************************************************
* Copyright 2020 alladin-IT GmbH
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

#include "AudioStreamingWrapper.h"

#include "AudioStreamingTest.h"

#include "gst_ios_init.h"

@interface AudioStreamingWrapper () {
    
    @protected
    AudioStreamingTest *_audioStreamingTest;
}

@end

@implementation AudioStreamingWrapper

-(instancetype)initWithUrl:(NSString *)url bufferDurationNs:(uint64_t)bufferDurationNs playbackDurationNs:(uint64_t)playbackDurationNs
{
    self = [super init];
    if (self) {
        gst_ios_init();
        
        _audioStreamingTest = new AudioStreamingTest();
        _audioStreamingTest->initialize(
            std::string([url UTF8String]),
            bufferDurationNs,
            playbackDurationNs
        );
        _audioStreamingTest->setLogFunction(
            [] (std::string str, std::string str2, std::string str3) {
                NSString *nsstr = [NSString stringWithUTF8String:str.c_str()];
                NSString *nsstr2 = [NSString stringWithUTF8String:str2.c_str()];
                NSString *nsstr3 = [NSString stringWithUTF8String:str3.c_str()];
                NSLog(@"%@, %@: %@", nsstr, nsstr2, nsstr3);
            }
        );
    }
    return self;
}

- (NSString *)start {
    return [NSString stringWithCString:_audioStreamingTest->start().c_str() encoding:[NSString defaultCStringEncoding]];
}

- (void)stop {
    _audioStreamingTest->stop();
}

-(void)dealloc {
    delete _audioStreamingTest;
}

@end
