/*!
    \file Database.h
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
