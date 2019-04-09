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

#import "LogFormatter.h"

@implementation LogFormatter

-(NSString *)formatLogMessage:(DDLogMessage *)logMessage
{
    NSString *logLevel      = @"";
    
    switch (logMessage->_flag)
    {
        case DDLogFlagError    : logLevel = @"E"; break;
        case DDLogFlagWarning  : logLevel = @"W"; break;
        case DDLogFlagInfo     : logLevel = @"I"; break;
        case DDLogFlagDebug    : logLevel = @"D"; break;
        default                : logLevel = @"V"; break;
    }

    NSDateFormatter *dateFormatter = [NSDateFormatter new];
    [dateFormatter setDateFormat:@"HH:mm:ss:SSS"];
    
    return [NSString stringWithFormat:@"%@ %@ \t%@: %@", [dateFormatter stringFromDate:(logMessage->_timestamp)], logMessage->_fileName, logLevel, logMessage->_message];
}


@end
