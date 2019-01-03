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

#import "Database.h"




/**************************** Loging Level ****************************/

#ifdef DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelDebug;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelWarning;
#endif




@interface Database ()


/**************************** Global Variables ****************************/

@property (nonatomic, strong) FMDatabase *database;

@end



@implementation Database

-(Database*)init
{
    [DDLog removeAllLoggers];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    [DDTTYLogger sharedInstance].logFormatter = [LogFormatter new];
    
    return self;
}

-(bool)open
{
    NSArray *searchPaths = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
    NSString *documentFolderPath = [searchPaths objectAtIndex: 0];
    
    self.database = [FMDatabase databaseWithPath:[documentFolderPath stringByAppendingPathComponent:[NSString stringWithFormat:@"%@.db", self.name]]];
    
    if (![self.database open])
    {
        DDLogError(@"Opening Database '%@' failed with error: %@", self.name, [self.database lastError]);
        return false;
    }
    
    DDLogDebug(@"Database '%@' opened", self.name);
    return true;
}

-(void)close
{
    [self.database close];
    
    DDLogDebug(@"Database '%@' closed", self.name);
}

-(bool)executeUpdateStatement:(NSString*)statement
{
    DDLogDebug(@"Executing UPDATE statement: %@", statement);
    
    bool result = [self.database executeUpdate:statement];
    
    if(!result)
    {
        DDLogError(@"UPDATE failed with error: %@", [self.database lastError]);
    }
    
    return result;
}

-(NSArray*)executeSelectStatement:(NSString*)statement limit:(NSInteger)limit
{
    if (limit > 0)
    {
        statement = [NSString stringWithFormat:@"%@ LIMIT %li", statement, (long)limit];
    }
    
    DDLogDebug(@"Executing SELECT statement: %@", statement);
    
    FMResultSet *rs = [self.database executeQuery:statement];
    
    if (!rs)
    {
        DDLogError(@"SELECT failed with error: %@", [self.database lastError]);
        
        return nil;
    }
    
    NSMutableDictionary *columns = [rs columnNameToIndexMap];
    
    NSMutableArray *resultArray = [NSMutableArray new];
    
    while ([rs next])
    {
        NSMutableDictionary *resultDict = [NSMutableDictionary new];
        
        for(id key in columns)
        {
            if (![rs stringForColumn:key])
            {
                //[resultDict setObject:@"-" forKey:key];
            }
            else
            {
                [resultDict setObject:[rs stringForColumn:key] forKey:key];
            }
        }
        
        [resultArray addObject:resultDict];
    }
    
    return resultArray;
}

-(bool)createTableWithKeys:(NSDictionary*)keys
{
    NSString *statement = [NSString stringWithFormat:@"CREATE TABLE IF NOT EXISTS %@ (id INTEGER PRIMARY KEY AUTOINCREMENT", self.table];
    
    for (NSString* key in keys)
    {
        statement = [NSString stringWithFormat:@"%@, '%@' TEXT", statement, key];
    }
    
    statement = [NSString stringWithFormat:@"%@)", statement];
    
    return [self executeUpdateStatement:statement];
}

-(void)checkColumnsForKeys:(NSDictionary*)keys
{
    NSString *statement = [NSString stringWithFormat:@"SELECT * FROM %@ LIMIT 1", self.table];
    
    FMResultSet *rs = [self.database executeQuery:statement];
    
    if (!rs)
    {
        DDLogError(@"checkColumns failed with error: %@", [self.database lastError]);
        return;
    }
    
    NSMutableDictionary *columns = [rs columnNameToIndexMap];

    for (id key in keys)
    {
        if (![columns objectForKey:key])
        {
            NSString *statement = [NSString stringWithFormat:@"ALTER TABLE %@ ADD COLUMN %@ TEXT", self.table, key];
            
            bool result = [self executeUpdateStatement:statement];
            
            if (!result)
            {
                DDLogError(@"column '%@' not found, adding failed with error: %@", key, [self.database lastError]);
            }
            else
            {
                DDLogInfo(@"column '%@' not found, added", key);
            }
        }
    }
}

-(bool)insert:(NSArray*)rows
{
    [self checkColumnsForKeys:[rows objectAtIndex:0]];
    
    NSArray *keys = [[rows objectAtIndex:0] allKeys];
    NSString *statement;
    
    for (id row in rows)
    {
        if (!statement)
        {
            statement = @"";
        }
        else
        {
            statement = [NSString stringWithFormat:@"%@,", statement];
        }

        NSString *values;
        for (id key in keys)
        {
            if (!values)
            {
                values = @"";
            }
            else
            {
                values = [NSString stringWithFormat:@"%@, ", values];
            }
            values = [NSString stringWithFormat:@"%@'%@'", values, [row objectForKey:key]];
        }
        
        statement = [NSString stringWithFormat:@"%@ (%@)", statement, values];
    }
    
    statement = [NSString stringWithFormat:@"INSERT INTO %@ (%@) VALUES %@", self.table, [keys componentsJoinedByString:@", "], statement];
    
    bool result = [self executeUpdateStatement:statement];
    
    if (!result)
    {
        DDLogError(@"INSERT failed with error: %@", [self.database lastError]);
    }
    else
    {
        DDLogDebug(@"INSERT successful");
    }

    return result;
}

-(NSArray*)selectWithLimit:(NSInteger)limit
{
    NSString *statement = [NSString stringWithFormat:@"SELECT * FROM %@", self.table];
    
    return [self executeSelectStatement:statement limit:limit];
}

-(NSArray*)selectWithLimit:(NSInteger)limit where:(NSDictionary*)where
{
    if (!where)
    {
        return [self selectWithLimit:limit];
    }
    
    NSMutableArray *conditions = [NSMutableArray new];
    
    for (id key in where)
    {
        [conditions addObject:[NSString stringWithFormat:@"%@ = %@", key, [where objectForKey:key]]];
    }
    
    return [self executeSelectStatement:[NSString stringWithFormat:@"SELECT * FROM %@ WHERE %@", self.table, [conditions componentsJoinedByString:@" AND "]] limit:limit];
}

-(bool)updateWithWhere:(NSDictionary*)where row:(NSDictionary*)row
{
    NSMutableArray *conditions = [NSMutableArray new];
    
    if (where)
    {
        for (id key in where)
        {
            [conditions addObject:[NSString stringWithFormat:@"%@ = %@", key, [where objectForKey:key]]];
        }
    }
    
    NSMutableArray *rowArray = [NSMutableArray new];
    
    for (id key in row)
    {
        [rowArray addObject:[NSString stringWithFormat:@"%@ = '%@'", key, [row objectForKey:key]]];
    }

    return [self executeUpdateStatement:[NSString stringWithFormat:@"UPDATE %@ SET %@ %@", self.table, [rowArray componentsJoinedByString:@", "], [NSString stringWithFormat:@"WHERE %@", [conditions componentsJoinedByString:@" AND "]]]];
}

-(bool)deleteWithWhere:(NSDictionary*)where
{
    if (!where)
    {
        return false;
    }
    
    NSMutableArray *conditions = [NSMutableArray new];
    
    for (id key in where)
    {
        [conditions addObject:[NSString stringWithFormat:@"%@ = %@", key, [where objectForKey:key]]];
    }
    
    return [self executeUpdateStatement:[NSString stringWithFormat:@"DELETE FROM %@ WHERE %@", self.table, [conditions componentsJoinedByString:@" AND "]]];
}




@end
