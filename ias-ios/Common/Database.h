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
#import "LogFormatter.h"

@import FMDB;
@import CocoaLumberjack;

@interface Database : NSObject




/**************************** Public Variables ****************************/

@property (nonatomic, strong) NSString *name;
@property (nonatomic, strong) NSString *table;


/**************************** Public Functions ****************************/

-(bool)open;
-(void)close;

-(bool)executeUpdateStatement:(NSString*)statement;
-(NSArray*)executeSelectStatement:(NSString*)statement limit:(NSInteger)limit;

-(bool)createTableWithKeys:(NSDictionary*)keys;
-(void)checkColumnsForKeys:(NSDictionary*)keys;
-(bool)insert:(NSArray*)rows;
-(NSArray*)selectWithLimit:(NSInteger)limit;
-(NSArray*)selectWithLimit:(NSInteger)limit where:(NSDictionary*)where;
-(bool)updateWithWhere:(NSDictionary*)where row:(NSDictionary*)row;
-(bool)deleteWithWhere:(NSDictionary*)where;


@end
