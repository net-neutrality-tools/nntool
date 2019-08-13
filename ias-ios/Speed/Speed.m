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
 *      \date Last update: 2019-07-03
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#import "Speed.h"
#import <NativeScript/NativeScript.h>
#import <JavaScriptCore/JavaScript.h>
#import <JavaScriptCore/JSStringRefCF.h>




/**************************** Loging Level ****************************/

#ifdef DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelDebug;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelWarning;
#endif


/**************************** Default Parameters ****************************/

static NSString *const defaultPlatform                      = @"mobile";

static NSString *const defaultTargetsTld                    = @"net-neutrality.tools";
static NSString *const defaultTargetsPort                   = @"80";
static NSInteger const defaultWss                           = 0;

static const bool defaultPerformRttMeasurement              = true;
static const bool defaultPerformDownloadMeasuement          = true;
static const bool defaultPerformUploadMeasurement           = true;
static const bool defaultPerformRouteToClientLookup         = false;
static const bool defaultPerformGeolocationLookup           = true;

static const NSInteger defaultRouteToClientTargetPort       = 8080;

//static NSString * const defaultIndexUrl                     = @"";

static const float tnsContextUnrefTimeout                   = 3.0f;




@interface Speed () <LocationDelegate, SystemUsageDelegate, HttpRequestDelegate>


/**************************** Global Variables ****************************/

@property (nonatomic, strong) Tool *tool;
@property (nonatomic, strong) NSString *errorDomain;

@property (nonatomic, strong) NSMutableDictionary *deviceKPIs;
@property (nonatomic, strong) NSMutableDictionary *locationKPIs;
@property (nonatomic, strong) NSMutableDictionary *systemUsageKPIs;
@property (nonatomic, strong) NSMutableDictionary *reportKPIs;
@property (nonatomic, strong) NSMutableDictionary *libraryKPIs;
@property (nonatomic, strong) NSMutableDictionary *radioKPIs;

@property (nonatomic, strong) NSMutableDictionary *additionalKPIs;

@property (nonatomic) bool downloadRunning;
@property (nonatomic) bool uploadRunning;
@property (nonatomic) bool measurementSuccessful;

@property (nonatomic, strong) TNSRuntime *tnsRuntime;
@property (nonatomic, strong) Http *httpRequest;

@property (nonatomic) bool performedRouteToClientLookup;
@property (nonatomic, strong) NSURL *routeToClientLookupUrl;

@property (nonatomic, strong) NSMutableDictionary *rttParameters;
@property (nonatomic, strong) NSMutableDictionary *downloadParameters;
@property (nonatomic, strong) NSMutableDictionary *uploadParameters;

@end




@implementation Speed




/**************************** Static Functions ****************************/

+(NSString*)version
{
    return [NSBundle bundleWithIdentifier:@"com.zafaco.Speed"].infoDictionary[@"CFBundleShortVersionString"];
}




/**************************** Public Functions ****************************/

-(Speed*)init
{
    [DDLog removeAllLoggers];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    [DDTTYLogger sharedInstance].logFormatter = [LogFormatter new];
    
    self.tool                                       = [Tool new];
    self.errorDomain                                = @"Speed";

    self.httpRequest                                = [Http new];
    self.httpRequest.httpRequestDelegate            = self;

    //set default parameters
    self.platform                                   = defaultPlatform;
    
    self.targetsTld                                 = defaultTargetsTld;
    self.targetsPort                                = defaultTargetsPort;
    self.wss                                        = defaultWss;
    
    self.performRttMeasurement                      = defaultPerformRttMeasurement;
    self.performDownloadMeasurement                 = defaultPerformDownloadMeasuement;
    self.performUploadMeasurement                   = defaultPerformUploadMeasurement;
    self.performRouteToClientLookup                 = defaultPerformRouteToClientLookup;
    self.performGeolocationLookup                   = defaultPerformGeolocationLookup;
    self.routeToClientTargetPort                    = defaultRouteToClientTargetPort;
    
    //self.indexUrl                                   = [NSURL URLWithString:defaultIndexUrl];
    
    self.deviceKPIs                                 = [NSMutableDictionary new];
    self.locationKPIs                               = [NSMutableDictionary new];
    self.systemUsageKPIs                            = [NSMutableDictionary new];
    self.reportKPIs                                 = [NSMutableDictionary new];
    self.libraryKPIs                                = [NSMutableDictionary new];
    self.radioKPIs                                  = [NSMutableDictionary new];
    
    self.additionalKPIs                             = [NSMutableDictionary new];
 
    self.downloadRunning                            = false;
    self.uploadRunning                              = false;
    self.measurementSuccessful                      = false;
    
    self.rttParameters                              = [NSMutableDictionary new];
    self.downloadParameters                         = [NSMutableDictionary new];
    self.uploadParameters                           = [NSMutableDictionary new];
    
    self.downloadClasses                            = [NSMutableArray new];
    self.uploadClasses                              = [NSMutableArray new];
    
    NSMutableDictionary *versions = [NSMutableDictionary new];
    [versions setObject:[Speed version] forKey:@"speed"];
    [versions setObject:[Common version] forKey:@"common"];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:versions options:0 error:nil];
    
    [self.libraryKPIs setObject:[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding] forKey:@"app_library_version"];
    
    [self removeObserver];
    
    DDLogInfo(@"Speed: Versions: %@", [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding]);
    
    return self;
}




/**************************** Public Delegate Functions Speed ****************************/

-(void)measurementLoad
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = true;
        [UIApplication sharedApplication].idleTimerDisabled = true;
    });
    
    if (!self.tool.networkReachable)
    {
        NSError *error = [self.tool getNetworkReachableErrorWithDomain:self.errorDomain];
        DDLogError(@"MeasurementLoad failed with Error: %@", [error.userInfo objectForKey:@"NSLocalizedDescription"]);
        
        [self measurementDidLoadWithResponse:nil withError:error];
        
        return;
    }
    
    [self tnsLoad];
}

-(void)measurementStart
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = true;
        [UIApplication sharedApplication].idleTimerDisabled = true;
    });
    
    if (!self.tool.networkReachable)
    {
        NSError *error = [self.tool getNetworkReachableErrorWithDomain:self.errorDomain];
        DDLogError(@"MeasurementStart failed with Error: %@", [error.userInfo objectForKey:@"NSLocalizedDescription"]);
        
        [self measurementDidCompleteWithResponse:nil withError:error];
        
        return;
    }
    
    DDLogInfo(@"Measurement started");
    
    if (self.performGeolocationLookup)
    {
        self.tool.locationDelegate = self;
        [self.tool startUpdatingLocationWithAccuracy:kCLLocationAccuracyBest distanceFilter:kCLDistanceFilterNone allowsBackgroundLocationUpdates:false];
    }
    
    self.tool.systemUsageDelegate = self;
    [self.tool startUpdatingSystemUsage];
    
    [self.deviceKPIs addEntriesFromDictionary:[self.tool getDeviceData]];
    [self.radioKPIs addEntriesFromDictionary:[self.tool getNetworkData]];
    [self.radioKPIs removeObjectForKey:@"carrier"];
    
    NSMutableDictionary *measurementParametersDict  = [NSMutableDictionary new];
    
    [measurementParametersDict setObject:@"start"                                                       forKey:@"cmd"];
    [measurementParametersDict setObject:self.platform                                                  forKey:@"platform"];
    
    [measurementParametersDict setObject:self.targets                                                   forKey:@"wsTargets"];
    [measurementParametersDict setObject:self.targetsRtt                                                forKey:@"wsTargetsRtt"];
    
    [measurementParametersDict setObject:self.targetsTld                                                forKey:@"wsTLD"];
    [measurementParametersDict setObject:self.targetsPort                                               forKey:@"wsTargetPort"];
    [measurementParametersDict setObject:[NSNumber numberWithInteger:self.wss]                          forKey:@"wsWss"];
    
    [measurementParametersDict setObject:@"placeholderToken"                                            forKey:@"wsAuthToken"];
    [measurementParametersDict setObject:@"placeholderTimestamp"                                        forKey:@"wsAuthTimestamp"];
    
    [measurementParametersDict setObject:[NSNumber numberWithBool:false]                                forKey:@"cookieId"];
    
    if (self.startupTime) [measurementParametersDict setObject:self.startupTime                         forKey:@"wsStartupTime"];
    if (self.measureTime) [measurementParametersDict setObject:self.measureTime                         forKey:@"wsMeasureTime"];
    
    [self.rttParameters setObject:[NSNumber numberWithBool:self.performRttMeasurement] forKey:@"performMeasurement"];
    [self.downloadParameters setObject:[NSNumber numberWithBool:self.performDownloadMeasurement] forKey:@"performMeasurement"];
    [self.uploadParameters setObject:[NSNumber numberWithBool:self.performUploadMeasurement] forKey:@"performMeasurement"];

    if (self.parallelStreamsDownload)
    {
        [self.downloadParameters setObject:self.parallelStreamsDownload forKey:@"streams"];
    }
    if (self.parallelStreamsUpload)
    {
        [self.uploadParameters setObject:self.parallelStreamsUpload forKey:@"streams"];
    }
    if (self.frameSizeDownload)
    {
        [self.downloadParameters setObject:self.frameSizeDownload forKey:@"frameSize"];
    }
    if (self.frameSizeUpload)
    {
        [self.uploadParameters setObject:self.frameSizeUpload forKey:@"frameSize"];
    }

    [self.downloadParameters setObject:self.downloadClasses forKey:@"classes"];
    [self.uploadParameters setObject:self.uploadClasses forKey:@"classes"];
    
    
    [measurementParametersDict setObject:self.rttParameters forKey:@"rtt"];
    [measurementParametersDict setObject:self.downloadParameters forKey:@"download"];
    [measurementParametersDict setObject:self.uploadParameters forKey:@"upload"];

    NSData *measurementParametersJson = [NSJSONSerialization dataWithJSONObject:measurementParametersDict options:0 error:nil];
    NSString *measurementParameters = [[NSString alloc] initWithData:measurementParametersJson encoding:NSUTF8StringEncoding];
    
    NSString *filePath = [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true) objectAtIndex:0] stringByAppendingPathComponent:@"ias.mobile.js"];
    DDLogDebug(filePath);
    
    [self tnsRunScript:[NSString stringWithFormat:@"global.require('%@'); global.measurementStart(%@);", filePath, measurementParameters] inContext:@"tnsSpeedtest"];
}

-(void)measurementStop
{
    if (self.tool.systemUsageDelegate)
    {
        [self tnsRunScript:@"global.measurementStop();" inContext:@"tnsSpeedtest"];
        [self performSelector:@selector(tnsContextUnref) withObject:nil afterDelay:tnsContextUnrefTimeout];
    }
    
    DDLogInfo(@"Measurement stopped");
    [self measurementDidStop];
}

-(void)measurementClearCache
{
    [self removeObserver];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = true;
        [UIApplication sharedApplication].idleTimerDisabled = true;
    });
    
    NSFileManager *fileManager = [NSFileManager defaultManager];
    NSString *filePath = [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true) objectAtIndex:0] stringByAppendingPathComponent:@"ias.mobile.js"];
    
    [fileManager removeItemAtPath:filePath error:nil];
    
    DDLogDebug(@"Cache cleared");
    [self measurementDidClearCache];
}




/**************************** Internal Functions ****************************/

-(void)tnsLoad
{
    [self removeObserver];
    
    /*
    if ([[self.indexUrl absoluteString] rangeOfString:@"http"].location == NSNotFound)
    {
        DDLogError(@"TNS: Malformed URL");
        
        NSMutableDictionary *response = [NSMutableDictionary new];
        [response setObject:self.indexUrl forKey:@"url"];
        
        [self measurementDidLoadWithResponse:response withError:[self.tool getHttpErrorWithMalformedUrl:self.indexUrl domain:self.errorDomain]];
        
        return;
    }
    */
    
    [self tnsRuntimeInit];
    
    NSURL *filePathBundled = [[NSBundle mainBundle] URLForResource:@"ias.mobile" withExtension:@"js"];
    NSData *indexDataBundled = [NSData dataWithContentsOfURL:filePathBundled];
    [indexDataBundled writeToFile:[[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true) objectAtIndex:0] stringByAppendingPathComponent:@"ias.mobile.js"] atomically:true];
    
    NSString *filePath = [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true) objectAtIndex:0] stringByAppendingPathComponent:@"ias.mobile.js"];
    NSData *indexData = [[NSData alloc] initWithContentsOfFile:filePath];

    if (!indexData)
    {
        /*
        DDLogDebug(@"TNS: Loading URL: %@", self.indexUrl);
        
        self.httpRequest = [Http new];
        self.httpRequest.httpRequestDelegate = self;
        self.httpRequest.httpRequestTimeout = 10.0f;
        
        [self.httpRequest httpRequestToUrl:self.indexUrl type:@"GET" header:nil body:nil];
         */
    }
    else
    {
        NSMutableDictionary *responseDict = [NSMutableDictionary new];
        self.indexUrl = [NSURL URLWithString:filePath];
        [responseDict setObject:self.indexUrl forKey:@"url"];
        
        [self measurementDidLoadWithResponse:responseDict withError:nil];
    }
}

-(void)tnsRuntimeInit
{
    extern char startOfMetadataSection __asm("section$start$__DATA$__TNSMetadata");
    
    [TNSRuntime initializeMetadata:&startOfMetadataSection];
    
    TNSSetUncaughtErrorHandler(handler);
    
    self.tnsRuntime = [[TNSRuntime alloc] initWithApplicationPath:[[NSBundle mainBundle] bundlePath]];
    
#ifdef DEBUG
    [TNSRuntimeInspector setLogsToSystemConsole:true];
#endif
    
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(tnsConsoleLogCallback:) name: @"tnsConsoleLogCallback" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(tnsReportCallback:) name: @"tnsReportCallback" object:nil];
    [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(applicationDidEnterBackground) name:UIApplicationDidEnterBackgroundNotification object:nil];
}

void handler(JSContextRef ctx, JSValueRef error)
{
    DDLogError(@"TNS: uncaught error");
}

-(void)tnsContextUnref
{
    [self removeObserver];
    
    JSGlobalContextRelease(self.tnsRuntime.globalContext);
}

- (void)tnsRunScript:(NSString *)source inContext:(NSString*)context
{
    JSValueRef error = nil;
    JSStringRef script = JSStringCreateWithCFString((__bridge CFStringRef)(source));
    JSEvaluateScript(self.tnsRuntime.globalContext, script, nil, nil, 0, &error);
    JSStringRelease(script);
    
    if (error)
    {
        JSStringRef stringifiedError = JSValueToStringCopy(self.tnsRuntime.globalContext, error, NULL);
        NSString *errorString = [NSString stringWithFormat:@"%@", JSStringCopyCFString(kCFAllocatorDefault, stringifiedError)];
        JSStringRelease(stringifiedError);
        DDLogError(@"TNS: Scripting failed: %@", errorString);
    }
}

-(void)stopUpdatingKpis
{
    [self.tool stopUpdatingLocation];
    self.tool.locationDelegate = nil;
    
    [self.tool stopUpdatingSystemUsage];
    self.tool.systemUsageDelegate = nil;
}




/**************************** TNS Callbacks ****************************/

- (void)tnsConsoleLogCallback:(NSNotification *)notification
{
    DDLogDebug(@"TNS: JS Console: %@", [notification.userInfo objectForKey:@"message"]);
}

- (void)tnsReportCallback:(NSNotification *)notification
{
    NSData *reportData = [[notification.userInfo objectForKey:@"message"] dataUsingEncoding:NSUTF8StringEncoding];
    NSMutableDictionary *report = [[NSJSONSerialization JSONObjectWithData:reportData options:0 error:nil] mutableCopy];
    
    NSString *cmd = [report objectForKey:@"cmd"];
    
    NSDictionary *networkData = [self.tool getNetworkData];
    
    //download/upload running
    if (self.performDownloadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"download"] && [[report objectForKey:@"msg"] isEqualToString:@"starting measurement"])
    {
        self.downloadRunning = true;
    }
    if (self.performUploadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"upload"] && [[report objectForKey:@"msg"] isEqualToString:@"starting measurement"])
    {
        self.uploadRunning = true;
    }
    
    //DDLogDebug(@"JS callback: cmd:          %@", [report objectForKey:@"cmd"]);
    //DDLogDebug(@"JS callback: test_case:    %@", [report objectForKey:@"test_case"]);
    //DDLogDebug(@"JS callback: msg:          %@", [report objectForKey:@"msg"]);
    
    //app_access counter download/upload_start/end
    //download start
    if (self.performDownloadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"download"] && [[report objectForKey:@"msg"] isEqualToString:@"starting measurement"])
    {
        [self.radioKPIs setObject:[networkData objectForKey:@"app_access_id"] forKey:@"app_access_id_download_start"];
        [self.radioKPIs setObject:[self.tool networkStatus] forKey:@"app_mode_download_start"];
        
        if (self.performRouteToClientLookup && !self.performedRouteToClientLookup)
        {
            [self routeToClientLookupWithReport:(NSDictionary*)report];
        }
    }
    //upload start
    if (self.performDownloadMeasurement && self.performUploadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"upload"] && [[report objectForKey:@"msg"] isEqualToString:@"starting measurement"])
    {
        [self.radioKPIs setObject:[networkData objectForKey:@"app_access_id"] forKey:@"app_access_id_upload_start"];
        [self.radioKPIs setObject:[self.tool networkStatus] forKey:@"app_mode_upload_start"];

        if (self.performRouteToClientLookup && !self.performedRouteToClientLookup)
        {
            [self routeToClientLookupWithReport:(NSDictionary*)report];
        }
    }
    else if (!self.performDownloadMeasurement && self.performUploadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"upload"] && [[report objectForKey:@"msg"] isEqualToString:@"starting measurement"])
    {
        [self.radioKPIs setObject:[networkData objectForKey:@"app_access_id"] forKey:@"app_access_id_upload_start"];
        [self.radioKPIs setObject:[self.tool networkStatus] forKey:@"app_mode_upload_start"];
    }
    
    
    //app_access_id_*/app_mode_* changed during test?
    //download: access_id
    if (![self.radioKPIs objectForKey:@"app_access_id_download_changed"] && self.performDownloadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"download"] && ([cmd isEqualToString:@"report"] || [cmd isEqualToString:@"finish"]))
    {
        if ((long)[self.radioKPIs objectForKey:@"app_access_id_download_start"] != (long)[networkData objectForKey:@"app_access_id"])
        {
            [self.radioKPIs setObject:[NSNumber numberWithInt:1] forKey:@"app_access_id_download_changed"];
        }
    }
    //download: mode
    if (![self.radioKPIs objectForKey:@"app_mode_download_changed"] && self.performDownloadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"download"] && ([cmd isEqualToString:@"report"] || [cmd isEqualToString:@"finish"]))
    {
        if (![[self.radioKPIs objectForKey:@"app_mode_download_start"] isEqualToString:[self.tool networkStatus]])
        {
            [self.radioKPIs setObject:[NSNumber numberWithInt:1] forKey:@"app_mode_download_changed"];
        }
    }
    
    //upload: access_id
    if (![self.radioKPIs objectForKey:@"app_access_id_upload_changed"] && self.performDownloadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"upload"] && ([cmd isEqualToString:@"report"] || [cmd isEqualToString:@"finish"]))
    {
        if ((long)[self.radioKPIs objectForKey:@"app_access_id_upload_start"] != (long)[networkData objectForKey:@"app_access_id"])
        {
            [self.radioKPIs setObject:[NSNumber numberWithInt:1] forKey:@"app_access_id_upload_changed"];
        }
    }
    //upload: mode
    if (![self.radioKPIs objectForKey:@"app_mode_upload_changed"] && self.performUploadMeasurement && [[report objectForKey:@"test_case"] isEqualToString:@"upload"] && ([cmd isEqualToString:@"report"] || [cmd isEqualToString:@"finish"]))
    {
        if (![[self.radioKPIs objectForKey:@"app_mode_upload_start"] isEqualToString:[self.tool networkStatus]])
        {
            [self.radioKPIs setObject:[NSNumber numberWithInt:1] forKey:@"app_mode_upload_changed"];
        }
    }
    
    
    [self.radioKPIs setObject:[networkData objectForKey:@"app_access"] forKey:@"app_access"];
    [self.radioKPIs setObject:[networkData objectForKey:@"app_access_id"] forKey:@"app_access_id"];
    [self.radioKPIs setObject:[networkData objectForKey:@"app_access_category"] forKey:@"app_access_category"];
    
    if ([networkData objectForKey:@"app_mode"])
    {
        [self.radioKPIs setObject:[networkData objectForKey:@"app_mode"] forKey:@"app_mode"];
    }
    
    
    //first active sim, starting with physical
    if ([networkData objectForKey:@"carrier"] && [[networkData objectForKey:@"carrier"] objectForKey:@"sim_physical"] && [[networkData objectForKey:@"carrier"] objectForKey:@"sims_available"])
    {
        NSDictionary *pSim = [[networkData objectForKey:@"carrier"] objectForKey:@"sim_physical"];
        NSArray *aSimArray = [[networkData objectForKey:@"carrier"] objectForKey:@"sims_available"];
        
        NSDictionary *activeSim;
        if ([[pSim objectForKey:@"active"] boolValue])
        {
            activeSim = pSim;
        }
        else
        {
            for (NSDictionary *aSim in aSimArray)
            {
                if ([[aSim objectForKey:@"active"] boolValue])
                {
                    activeSim = aSim;
                    break;
                }
            }
        }
        
        if ([activeSim objectForKey:@"carrier"])
        {
            [self.radioKPIs setObject:[activeSim objectForKey:@"carrier"] forKey:@"app_operator_sim"];
        }
        if ([activeSim objectForKey:@"mcc"])
        {
            [self.radioKPIs setObject:[activeSim objectForKey:@"mcc"] forKey:@"app_operator_sim_mcc"];
        }
        if ([activeSim objectForKey:@"mnc"])
        {
            [self.radioKPIs setObject:[activeSim objectForKey:@"mnc"] forKey:@"app_operator_sim_mnc"];
        }
    }
    
    if ([[networkData objectForKey:@"carrier"] objectForKey:@"sims_active"])
    {
        [self.radioKPIs setObject:[NSNumber numberWithInt:[[[networkData objectForKey:@"carrier"] objectForKey:@"sims_active"] intValue]] forKey:@"app_sims_active"];
    }

    //set app_call_state initially
    if (![self.radioKPIs objectForKey:@"app_call_state"])
    {
        [self.radioKPIs setObject:[[self.tool getCallState] objectForKey:@"app_call_state"] forKey:@"app_call_state"];
    }
    //only update if 1
    if ([[[self.tool getCallState] objectForKey:@"app_call_state"] intValue] == 1)
    {
        [self.radioKPIs setObject:[[self.tool getCallState] objectForKey:@"app_call_state"] forKey:@"app_call_state"];
    }
    
    
    [report addEntriesFromDictionary:self.deviceKPIs];
    [report addEntriesFromDictionary:self.locationKPIs];
    [report addEntriesFromDictionary:self.systemUsageKPIs];
    [report addEntriesFromDictionary:self.libraryKPIs];
    [report addEntriesFromDictionary:self.additionalKPIs];
    [report addEntriesFromDictionary:self.radioKPIs];

    self.reportKPIs = [report mutableCopy];
    
    if ([cmd isEqualToString:@"completed"] || [cmd isEqualToString:@"error"])
    {
        NSError *error = nil;
        
        if ([cmd isEqualToString:@"error"])
        {
            error = [self.tool getError:[[report objectForKey:@"error_code"] longValue] description:[NSString stringWithFormat:@"%@: %@", [report objectForKey:@"test_case"], [report objectForKey:@"msg"]] domain:self.errorDomain];
            
            DDLogError(@"Measurement failed with Error: %@", [error.userInfo objectForKey:@"NSLocalizedDescription"]);
        }
        else
        {
            self.measurementSuccessful = true;
            DDLogInfo(@"Measurement successful");
        }
        
        [self measurementDidCompleteWithResponse:(NSDictionary*)report withError:error];
    }
    else
    {
        //DDLogDebug(@"Measurement report");
        [self measurementCallbackWithResponse:(NSDictionary*)report];
    }
}

-(void)removeObserver
{
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"tnsConsoleLogCallback" object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:@"tnsReportCallback" object:nil];
    [[NSNotificationCenter defaultCenter] removeObserver:self name:UIApplicationDidEnterBackgroundNotification object:nil];
}

-(void)routeToClientLookupWithReport:(NSDictionary*)report
{
    self.performedRouteToClientLookup = true;
    
    NSString *routeToClientLookupUrl = [NSString new];
    
    if (![report objectForKey:@"server_v6"])
    {
        routeToClientLookupUrl = [report objectForKey:@"server"];
    }
    else
    {
        routeToClientLookupUrl = [report objectForKey:@"server_v6"];
    }
    
    routeToClientLookupUrl = [NSString stringWithFormat:@"http://%@:%li/", routeToClientLookupUrl, self.routeToClientTargetPort];
    self.routeToClientLookupUrl = [NSURL URLWithString:routeToClientLookupUrl];
    
    NSDictionary *routeToClientRequestData = [NSDictionary dictionaryWithObject:@"traceroute" forKey:@"cmd"];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:routeToClientRequestData options:kNilOptions error:nil];
    
    NSMutableDictionary *header = [NSMutableDictionary new];
    [header setObject:@"application/json" forKey:@"Content-Type"];
    [header setObject:@"" forKey:@"Origin"];
    
    self.httpRequest = [Http new];
    self.httpRequest.httpRequestDelegate = self;
    self.httpRequest.httpRequestTimeout = 15.0f;
    
    [self.httpRequest httpRequestToUrl:self.routeToClientLookupUrl type:@"POST" header:header body:[[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding]];
}

- (void)applicationDidEnterBackground
{
    [self.libraryKPIs setObject:[NSNumber numberWithInt:1] forKey:@"app_suspended"];
}




/**************************** HttpRequestDelegate Callbacks ****************************/

-(void)httpRequestToUrl:(NSURL *)url response:(NSURLResponse *)response data:(NSData *)data didCompleteWithError:(NSError *)error
{
    if (url == self.indexUrl)
    {
        NSMutableDictionary *responseDict = [NSMutableDictionary new];
        [responseDict setObject:self.indexUrl forKey:@"url"];
        
        if (error)
        {
            [self measurementDidLoadWithResponse:responseDict withError:error];
        }
        else
        {
            NSString *filePath = [[NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true) objectAtIndex:0] stringByAppendingPathComponent:@"ias.mobile.js"];
            
            [data writeToFile:filePath atomically:false];
            
            DDLogDebug(@"200 OK received for: %@", [url absoluteString]);
            
            [self measurementDidLoadWithResponse:responseDict withError:nil];
        }
    }
    
    if (url == self.routeToClientLookupUrl)
    {
        if (!error)
        {
            @try
            {
                NSDictionary *routeToClient = [NSJSONSerialization JSONObjectWithData:data options:0 error:nil];
                
                if ([routeToClient objectForKey:@"hops"])
                {
                    NSMutableArray *hops = [[routeToClient objectForKey:@"hops"] mutableCopy];
                    
                    [hops removeLastObject];
                    [hops removeLastObject];
                    
                    [self.libraryKPIs setObject:[[hops lastObject] objectForKey:@"id"] forKey:@"server_client_route_hops"];
                    
                    [self.libraryKPIs setObject:[[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:hops options:0 error:nil] encoding:NSUTF8StringEncoding] forKey:@"server_client_route"];
                }
            }
            @catch(NSException *exception)
            {
                
            }
        }
    }
}




/**************************** LocationDelegate Callbacks ****************************/

-(void)locationDelegateDidUpdate:(NSArray<CLLocation *> *)locations
{
    //app_velocity_*_max
    //download
    if (self.downloadRunning && ![self.locationKPIs valueForKey:@"app_velocity_download_max"])
    {
        [self.locationKPIs setObject:[NSNumber numberWithDouble:0] forKey:@"app_velocity_download_max"];
    }
    if (self.downloadRunning && [[self.locationKPIs valueForKey:@"app_velocity_download_max"] doubleValue] <= (double)locations.lastObject.speed)
    {
        [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.speed]  forKey:@"app_velocity_download_max"];
    }
    //upload
    if (self.uploadRunning && ![self.locationKPIs valueForKey:@"app_velocity_upload_max"])
    {
        [self.locationKPIs setObject:[NSNumber numberWithDouble:0] forKey:@"app_velocity_upload_max"];
    }
    if (self.uploadRunning && [[self.locationKPIs valueForKey:@"app_velocity_upload_max"] doubleValue] <= (double)locations.lastObject.speed)
    {
        [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.speed]  forKey:@"app_velocity_upload_max"];
    }
        
    //only update locationKPIs if the accuracy improved
    if ((locations.lastObject.horizontalAccuracy <= [[self.locationKPIs objectForKey:@"app_accuracy"] doubleValue]) || [[self.locationKPIs objectForKey:@"app_accuracy"] doubleValue] == 0.0)
    {
        [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.coordinate.latitude]   forKey:@"app_latitude"];
        [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.coordinate.longitude]  forKey:@"app_longitude"];
        [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.horizontalAccuracy]    forKey:@"app_accuracy"];
        [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.altitude]              forKey:@"app_altitude"];
        
        if (locations.lastObject.speed < 0)
        {
            [self.locationKPIs setObject:[NSNumber numberWithDouble:0] forKey:@"app_velocity"];
        }
        else
        {
            [self.locationKPIs setObject:[NSNumber numberWithDouble:locations.lastObject.speed] forKey:@"app_velocity"];
        }
    }
}




/**************************** SystemUsageDelegate Callbacks ****************************/

-(void)systemUsageDelegateDidUpdate:(NSDictionary *)systemUsage
{
    [self.systemUsageKPIs addEntriesFromDictionary:systemUsage];
}




/**************************** Speed Callback Initiator ****************************/

-(void)measurementDidLoadWithResponse:(NSDictionary*)response withError:(NSError*)error
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = false;
        [UIApplication sharedApplication].idleTimerDisabled = false;
                       
        if (self.speedDelegate && [self.speedDelegate respondsToSelector:@selector(measurementDidLoadWithResponse:withError:)]) {
            [self.speedDelegate measurementDidLoadWithResponse:response withError:error];
        }
    });
}

-(void)measurementCallbackWithResponse:(NSDictionary*)response
{
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.speedDelegate && [self.speedDelegate respondsToSelector:@selector(measurementCallbackWithResponse:)])
                       {
                           [self.speedDelegate measurementCallbackWithResponse:response];
                       }
                   });
}

-(void)measurementDidCompleteWithResponse:(NSDictionary*)response withError:(NSError*)error
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = false;
        [UIApplication sharedApplication].idleTimerDisabled = false;
    });
    
    [self stopUpdatingKpis];
    [self performSelector:@selector(tnsContextUnref) withObject:nil afterDelay:tnsContextUnrefTimeout];
    
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.speedDelegate && [self.speedDelegate respondsToSelector:@selector(measurementDidCompleteWithResponse:withError:)])
                       {
                           [self.speedDelegate measurementDidCompleteWithResponse:response withError:error];
                       }
                   });
}

-(void)measurementDidStop
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = false;
        [UIApplication sharedApplication].idleTimerDisabled = false;
    });
    
    [self stopUpdatingKpis];

    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.speedDelegate && [self.speedDelegate respondsToSelector:@selector(measurementDidStop)])
                       {
                           [self.speedDelegate measurementDidStop];
                       }
                   });
}

-(void)measurementDidClearCache
{
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIApplication sharedApplication].networkActivityIndicatorVisible = false;
        [UIApplication sharedApplication].idleTimerDisabled = false;
    });
    
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.speedDelegate && [self.speedDelegate respondsToSelector:@selector(measurementDidClearCache)])
                       {
                           [self.speedDelegate measurementDidClearCache];
                       }
                   });
}

@end
