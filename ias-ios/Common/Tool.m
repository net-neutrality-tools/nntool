/*!
    \file Tool.m
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

#import "Tool.h"
#include <CommonCrypto/CommonDigest.h>




/**************************** Loging Level ****************************/

#ifdef DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelDebug;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelWarning;
#endif




@interface Tool () <CLLocationManagerDelegate, CXCallObserverDelegate>


/**************************** Global Variables ****************************/

@property (nonatomic, strong) CLLocationManager *locationManager;
@property (nonatomic, strong) NSMutableArray<CLLocation *> *locations;
@property (nonatomic, retain) UIView *activityIndicatorView;
@property (nonatomic, retain) UIActivityIndicatorView *activityIndicator;
@property (nonatomic, strong) CXCallObserver *callObserver;
//@property (nonatomic, strong) NSMutableDictionary *callState;

//System usage
@property (nonatomic) processor_info_array_t cpuInfo;
@property (nonatomic) processor_info_array_t prevCpuInfo;
@property (nonatomic) mach_msg_type_number_t numCpuInfo;
@property (nonatomic) mach_msg_type_number_t numPrevCpuInfo;
@property (nonatomic, strong) NSTimer *cpuUsageCheckTimer;
@property (nonatomic) unsigned numCPUs;
@property (nonatomic, strong) NSLock *CPUUsageLock;
@property (nonatomic) float cpuLoadSum;
@property (nonatomic) int cpuLoadCount;
@property (nonatomic) float cpuLoadAvg;
@property (nonatomic, strong) NSTimer *memoryUsageCheckTimer;
@property (nonatomic) float memoryLoadSum;
@property (nonatomic) int memoryLoadCount;
@property (nonatomic) float memoryLoadAvg;
@property (nonatomic) float systemUsageCheckInterval;
@property (nonatomic, strong) NSTimer *systemUsageUpdateTimer;
@property (nonatomic) float systemUsageUpdateInterval;


@end




@implementation Tool


/**************************** Public Functions ****************************/

-(Tool*)init
{
    [DDLog removeAllLoggers];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    [DDTTYLogger sharedInstance].logFormatter = [LogFormatter new];
    
    return self;
}

-(bool)networkReachable
{
    if ([[self networkStatus] isEqualToString:@"-"])
    {
        return false;
    }
    
    return true;
}

-(NSString*)networkStatus
{
    NetworkStatus networkStatus = [[Reachability reachabilityForInternetConnection] currentReachabilityStatus];
    
    if (networkStatus == ReachableViaWiFi)
    {
        return @"WIFI";
    }
    else if (networkStatus == ReachableViaWWAN)
    {
        return @"WWAN";
    }
    
    //networkStatus = NotReachable
    return @"-";
}

-(NSError*)getError:(long)errorCode description:(NSString*)errorDescription domain:(NSString*)errorDomain
{
    NSDictionary *errorDict = @{NSLocalizedDescriptionKey: errorDescription};
    
    return [[NSError alloc] initWithDomain:errorDomain code:errorCode userInfo:errorDict];
}

-(NSError*)getNetworkReachableErrorWithDomain:(NSString*)domain
{
    return [self getError:1 description:@"Network unreachable" domain:domain];
}

-(NSError*)getHttpErrorWithStatusCode:(long)statusCode domain:(NSString*)domain
{
    return [self getError:statusCode description:[NSString stringWithFormat:@"HTTP Response %li", statusCode] domain:domain];
}

-(NSError*)getHttpErrorWithMalformedUrl:(NSURL*)url domain:(NSString*)domain
{
    return [self getError:2 description:[NSString stringWithFormat:@"Malformed URL: %@", [url absoluteString]] domain:domain];
}

-(NSError *)getScriptingErrorWithDescription:(NSString*)errorDescription domain:(NSString*)errorDomain
{
    return [self getError:30 description:[NSString stringWithFormat:@"Scripting: %@", errorDescription] domain:errorDomain];
}

-(NSString*)formatNumberToCommaSeperatedString:(NSNumber*)number withMinDecimalPlaces:(int)min withMaxDecimalPlace:(int)max
{
    NSNumberFormatter *formatter = [[NSNumberFormatter alloc] init];
    formatter.numberStyle= NSNumberFormatterDecimalStyle;
    formatter.locale = [NSLocale currentLocale];
    [formatter setMinimumFractionDigits:min];
    [formatter setMaximumFractionDigits:max];
    
    return [NSString stringWithFormat:@"%@", [formatter stringForObjectValue:number]];
}

-(NSDictionary*)getDeviceData
{
    NSMutableDictionary *deviceData = [NSMutableDictionary new];
    
    UIDevice *currentDevice = [UIDevice currentDevice];
    
    [deviceData setObject:@"Apple" forKey:@"app_manufacturer"];
    [deviceData setObject:[[currentDevice identifierForVendor] UUIDString] forKey:@"app_manufacturer_id"];
    [deviceData setObject:[self getCurrentDeviceModelName] forKey:@"app_manufacturer_version"];
    [deviceData setObject:[[NSLocale currentLocale] localeIdentifier] forKey:@"app_country"];
    
    return (NSDictionary*)deviceData;
}

-(NSDictionary*)getAirplaneModeAndGsmOnlyStatusWithNetworkData:(NSDictionary*)networkData andCallState:(NSDictionary*)callState andSCNetworkReachabilityFlags:(NSDictionary*)sCNetworkReachabilityFlags
{
    //ref:
    /*
     kSCNetworkReachabilityFlagsTransientConnection     = 1<<0,     1
            The specified node name or address can be reached via a transient connection, such as PPP.
     kSCNetworkReachabilityFlagsReachable               = 1<<1,     2
            The specified node name or address can be reached using the current network configuration.
     kSCNetworkReachabilityFlagsConnectionRequired      = 1<<2,     4
            The specified node name or address can be reached using the current network configuration, but a connection must first be established. If this flag is set, the kSCNetworkReachabilityFlagsConnectionOnTraffic flag, kSCNetworkReachabilityFlagsConnectionOnDemand flag, or kSCNetworkReachabilityFlagsIsWWAN flag is also typically set to indicate the type of connection required. If the user must manually make the connection, the kSCNetworkReachabilityFlagsInterventionRequired flag is also set.
     kSCNetworkReachabilityFlagsConnectionOnTraffic     = 1<<3,     8
            The specified node name or address can be reached using the current network configuration, but a connection must first be established. Any traffic directed to the specified name or address will initiate the connection.
     kSCNetworkReachabilityFlagsInterventionRequired    = 1<<4,     16
            The specified node name or address can be reached using the current network configuration, but a connection must first be established.
     kSCNetworkReachabilityFlagsConnectionOnDemand
     API_AVAILABLE(macos(6.0),ios(3.0))                 = 1<<5,     32
            The specified node name or address can be reached using the current network configuration, but a connection must first be established.
     kSCNetworkReachabilityFlagsIsLocalAddress          = 1<<16,    65536
            The specified node name or address is one that is associated with a network interface on the current system.
     kSCNetworkReachabilityFlagsIsDirect                = 1<<17     131072
            Network traffic to the specified node name or address will not go through a gateway, but is routed directly to one of the interfaces in the system.
     kSCNetworkReachabilityFlagsIsWWAN
     API_UNAVAILABLE(macos) API_AVAILABLE(ios(2.0))     = 1<<18,    262144
            The specified node name or address can be reached via a cellular connection, such as EDGE or GPRS.
     kSCNetworkReachabilityFlagsConnectionAutomatic = kSCNetworkReachabilityFlagsConnectionOnTraffic
     */
    
    //DDLogDebug(@"SCNetworkReachabilityFlagsStatus: %i, flags: %i, networkStatus: %@", SCNetworkReachabilityFlagsStatus, flags, [self networkStatus]);
    
    /*
    check if:
    #1 flags are zero
    OR
    #2 app_call_state is 1 AND app_access_id is 0, e.g. CALLING and UNKNOWN
     */
    
    NSMutableDictionary *airplaneModeAndGsmOnlyStatus = [NSMutableDictionary new];
    [airplaneModeAndGsmOnlyStatus setObject:[NSNumber numberWithBool:false] forKey:@"airplaneModeOrGsmOnlyActive"];
    [airplaneModeAndGsmOnlyStatus setObject:[NSNumber numberWithBool:false] forKey:@"gsmCallActive"];
    
    if ([[sCNetworkReachabilityFlags objectForKey:@"SCNetworkReachabilityFlagsStatus"] boolValue] && [[sCNetworkReachabilityFlags objectForKey:@"SCNetworkReachabilityFlags"] longLongValue] == 0)
    {
        [airplaneModeAndGsmOnlyStatus setObject:[NSNumber numberWithBool:true] forKey:@"airplaneModeOrGsmOnlyActive"];
        DDLogDebug(@"getAirplaneModeAndGsmOnlyStatus: !reachability, Airplane Mode active or GSM-only connection");
    }
    
    if ([[callState objectForKey:@"app_call_state"] intValue] == 1 && [[networkData objectForKey:@"app_access_id"] intValue] == 0)
    {
        [airplaneModeAndGsmOnlyStatus setObject:[NSNumber numberWithBool:true] forKey:@"gsmCallActive"];
        DDLogDebug(@"getAirplaneModeAndGsmOnlyStatus: ActiveCall on GSM-only connection");
    }
    
    return airplaneModeAndGsmOnlyStatus;
}

-(NSDictionary*)getSCNetworkReachabilityFlags
{
    SCNetworkReachabilityRef reachabilityRef = SCNetworkReachabilityCreateWithName(NULL, [@"zafaco.de" UTF8String]);
    SCNetworkReachabilityFlags flags;
    bool SCNetworkReachabilityFlagsStatus = false;
    SCNetworkReachabilityFlagsStatus = SCNetworkReachabilityGetFlags(reachabilityRef, &flags);
    CFRelease(reachabilityRef);
    
    NSMutableDictionary* sCNetworkReachabilityFlags = [NSMutableDictionary new];
    [sCNetworkReachabilityFlags setObject:[NSNumber numberWithBool:SCNetworkReachabilityFlagsStatus] forKey:@"SCNetworkReachabilityFlagsStatus"];
    [sCNetworkReachabilityFlags setObject:[NSNumber numberWithLongLong:flags] forKey:@"SCNetworkReachabilityFlags"];
    NSLog(@"%i", flags);
    return sCNetworkReachabilityFlags;
}

/*
-(bool)getSimCardStatus
{
    CTTelephonyNetworkInfo *telephonyNetworkInfo = [CTTelephonyNetworkInfo new];
    NSString *mobileCountryCode = @"-";
    
    @try
    {
        CTCarrier *carrierInfo = [telephonyNetworkInfo subscriberCellularProvider];
        
        if ([carrierInfo mobileCountryCode])
        {
            mobileCountryCode = [carrierInfo mobileCountryCode];
        }
    }
    @catch (NSException *exception)
    {
        mobileCountryCode = @"-";
    }
    
    if (![mobileCountryCode isEqualToString:@"-"])
    {
        return true;
    }
    
    DDLogWarn(@"getSimCardStatus: No SIM Card installed or no SIM Card Slot present");
    return false;
}
*/
 
-(NSDictionary*)getCarrierData
{
    NSMutableDictionary *carrierData = [NSMutableDictionary new];
    int simsActive = 0;

    NSString *pSimCarrierName       = [NSString new];
    NSString *pSimMobileCountryCode = [NSString new];
    NSString *pSimMobileNetworkCode = [NSString new];
    
    CTTelephonyNetworkInfo *telephonyNetworkInfo = [CTTelephonyNetworkInfo new];

    
    //physical sim
    @try
    {
        CTCarrier *carrierInfo = telephonyNetworkInfo.subscriberCellularProvider;
        
        if ([carrierInfo carrierName])
        {
            pSimCarrierName = [carrierInfo carrierName];
        }
        if ([carrierInfo mobileCountryCode])
        {
            pSimMobileCountryCode = [carrierInfo mobileCountryCode];
        }
        if ([carrierInfo mobileNetworkCode])
        {
            pSimMobileNetworkCode = [carrierInfo mobileNetworkCode];
        }
    }
    @catch (NSException *exception)
    {
    }
    
    NSMutableDictionary *pSim = [NSMutableDictionary new];
    
    if (![pSimCarrierName isEqualToString:@""] || ![pSimMobileCountryCode isEqualToString:@""] || ![pSimMobileNetworkCode isEqualToString:@""])
    {
        [pSim setObject:[NSNumber numberWithBool:false] forKey:@"active"];
    }
    
    if (![pSimCarrierName isEqualToString:@""])
    {
        [pSim setObject:pSimCarrierName forKey:@"carrier"];
    }
    if (![pSimMobileCountryCode isEqualToString:@""])
    {
        [pSim setObject:pSimMobileCountryCode forKey:@"mcc"];
        [pSim setObject:[NSNumber numberWithBool:true] forKey:@"active"];
        simsActive++;
    }
    if (![pSimMobileNetworkCode isEqualToString:@""])
    {
        [pSim setObject:pSimMobileNetworkCode forKey:@"mnc"];
    }
    
    [carrierData setObject:pSim forKey:@"sim_physical"];
    
    
    //service subscriber cellular providers
    NSMutableArray *aSimArray = [NSMutableArray new];
    if (@available(iOS 12.0, *))
    {
        if ([telephonyNetworkInfo.serviceSubscriberCellularProviders count] > 0)
        {
            simsActive = 0;
        }
        else
        {
            [aSimArray addObject:pSim];
        }
        
        for (NSString *key in telephonyNetworkInfo.serviceSubscriberCellularProviders)
        {
            CTCarrier *carrierInfo = [telephonyNetworkInfo.serviceSubscriberCellularProviders objectForKey:key];
            
            NSString *aSimCarrierName       = [NSString new];
            NSString *aSimMobileCountryCode = [NSString new];
            NSString *aSimMobileNetworkCode = [NSString new];
            
            @try
            {
                if ([carrierInfo carrierName])
                {
                    aSimCarrierName = [carrierInfo carrierName];
                }
                if ([carrierInfo mobileCountryCode])
                {
                    aSimMobileCountryCode = [carrierInfo mobileCountryCode];
                }
                if ([carrierInfo mobileNetworkCode])
                {
                    aSimMobileNetworkCode = [carrierInfo mobileNetworkCode];
                }
            }
            @catch (NSException *exception)
            {
            }
            
            NSMutableDictionary *aSim = [NSMutableDictionary new];
            [aSim setObject:key forKey:@"serviceSubscriberCellularProvidersKey"];
            
            if (![aSimCarrierName isEqualToString:@""] || ![aSimMobileCountryCode isEqualToString:@""] || ![aSimMobileNetworkCode isEqualToString:@""])
            {
                [aSim setObject:[NSNumber numberWithBool:false] forKey:@"active"];
            }
            
            if (![aSimCarrierName isEqualToString:@""])
            {
                [aSim setObject:aSimCarrierName forKey:@"carrier"];
            }
            if (![aSimMobileCountryCode isEqualToString:@""])
            {
                [aSim setObject:aSimMobileCountryCode forKey:@"mcc"];
                [aSim setObject:[NSNumber numberWithBool:true] forKey:@"active"];
                simsActive++;
            }
            if (![aSimMobileNetworkCode isEqualToString:@""])
            {
                [aSim setObject:aSimMobileNetworkCode forKey:@"mnc"];
            }
            
            [aSimArray addObject:aSim];
        }
    }
    else
    {
        [aSimArray addObject:pSim];
    }
    
    if (simsActive == 0)
    {
        DDLogWarn(@"getCarrier: No (e)SIM Card active or no (e)SIM Card Slot present");
    }
    else if (simsActive > 1)
    {
        DDLogWarn(@"getCarrier: Multiple (e)SIM Cards active");
    }
    
    [carrierData setObject:[NSNumber numberWithInt:simsActive] forKey:@"sims_active"];
    [carrierData setObject:aSimArray forKey:@"sims_available"];
    
    return (NSDictionary*)carrierData;
}

-(NSDictionary*)getNetworkData
{
    NSMutableDictionary *networkData = [NSMutableDictionary new];

    [networkData setObject:[self networkStatus] forKey:@"app_mode"];
    [networkData setObject:[self getCarrierData] forKey:@"carrier"];
    
    NSString *serviceSubscriberCellularProvidersKey = nil;
    
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
    
        if ([activeSim objectForKey:@"serviceSubscriberCellularProvidersKey"])
        {
            serviceSubscriberCellularProvidersKey = [activeSim objectForKey:@"serviceSubscriberCellularProvidersKey"];
        }
    }

    NSDictionary *currentRadioAccessTechnology = [self getCurrentRadioAccessTechnologyWithServiceSubscriberCellularProvidersKey:serviceSubscriberCellularProvidersKey];
    [networkData setObject:[currentRadioAccessTechnology objectForKey:@"app_access"] forKey:@"app_access"];
    [networkData setObject:[currentRadioAccessTechnology objectForKey:@"app_access_id"] forKey:@"app_access_id"];
    [networkData setObject:[currentRadioAccessTechnology objectForKey:@"app_access_category"] forKey:@"app_access_category"];
    
    /*
    if ([carrier objectForKey:@"carrier"])
    {
        [networkData setObject:[carrier objectForKey:@"carrier"] forKey:@"app_operator_sim"];
    }
    if ([carrier objectForKey:@"mcc"])
    {
        [networkData setObject:[carrier objectForKey:@"mcc"] forKey:@"app_operator_sim_mcc"];
    }
    if ([carrier objectForKey:@"mcc"])
    {
        [networkData setObject:[carrier objectForKey:@"mnc"] forKey:@"app_operator_sim_mnc"];
    }
     */
    
    return networkData;
}

-(NSArray*)getWifiData
{
    NSMutableArray *wifiData = [NSMutableArray new];
    
    NSArray *interFaceNames = (__bridge_transfer id)CNCopySupportedInterfaces();
    
    for (NSString *name in interFaceNames)
    {
        NSDictionary *interface = (__bridge_transfer id)CNCopyCurrentNetworkInfo((__bridge CFStringRef)name);
        
        if (interface)
        {
            NSMutableDictionary *info = [NSMutableDictionary new];
            [info setObject:[interface objectForKey:@"SSID"] forKey:@"ssid"];
            [info setObject:[interface objectForKey:@"BSSID"] forKey:@"bssid"];
            [wifiData addObject:info];
        }
    }
    
    return wifiData;
}

-(NSDictionary*)getCurrentRadioAccessTechnologyWithServiceSubscriberCellularProvidersKey:(NSString*)serviceSubscriberCellularProvidersKey;
{
    CTTelephonyNetworkInfo *telephonyNetworkInfo = [CTTelephonyNetworkInfo new];
    NSString *currentRadioAccessTechnologyString = telephonyNetworkInfo.currentRadioAccessTechnology;
    NSMutableDictionary *currentRadioAccessTechnology = [NSMutableDictionary new];
    [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:0] forKey:@"app_access_id"];
    [currentRadioAccessTechnology setObject:@"unknown" forKey:@"app_access"];
    [currentRadioAccessTechnology setObject:@"unknown" forKey:@"app_access_category"];
    
    if (@available(iOS 12.0, *))
    {
        if (serviceSubscriberCellularProvidersKey)
        {
            currentRadioAccessTechnologyString = [telephonyNetworkInfo.serviceCurrentRadioAccessTechnology objectForKey:serviceSubscriberCellularProvidersKey];
        }
    }
    
    //NSDictionary *test = telephonyNetworkInfo.serviceCurrentRadioAccessTechnology;
    //DDLogDebug(@"%@", [test description]);
    
    //2G Networks
    if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyGPRS"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:1] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"GPRS" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"2G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyEdge"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:2] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"EDGE" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"2G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyCDMA1x"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:4] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"CDMA" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"2G" forKey:@"app_access_category"];
    }
    
    //3G Networks
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyWCDMA"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:5] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"EVDO revision 0" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyCDMAEVDORev0"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:5] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"EVDO revision 0" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyCDMAEVDORevA"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:6] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"EVDO revision A" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyCDMAEVDORevB"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:12] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"EVDO revision B" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyHSUPA"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:9] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"HSUPA" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyHSDPA"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:10] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"HSDPA" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyeHRPD"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:14] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"eHRPD" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"3G" forKey:@"app_access_category"];
    }
    
    //4G Networks
    else if ([currentRadioAccessTechnologyString isEqualToString:@"CTRadioAccessTechnologyLTE"])
    {
        [currentRadioAccessTechnology setObject:[NSNumber numberWithInteger:13] forKey:@"app_access_id"];
        [currentRadioAccessTechnology setObject:@"LTE" forKey:@"app_access"];
        [currentRadioAccessTechnology setObject:@"4G" forKey:@"app_access_category"];
    }
    
    return (NSDictionary*)currentRadioAccessTechnology;
}

-(void)startActivityIndicatorOnView:(id)view withFrame:(CGRect)frame withBackgroundColor:(UIColor*)color withActivityIndicatorStyle:(UIActivityIndicatorViewStyle)style
{
    self.activityIndicatorView = [[UIView alloc] initWithFrame:frame];
    self.activityIndicatorView.backgroundColor = color;
    //self.activityIndicatorView.backgroundColor = [UIColor colorWithRed:0 green:0 blue:0 alpha:0.3];
    //self.activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
    self.activityIndicator = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:style];
    self.activityIndicator.center = self.activityIndicatorView.center;
    [self.activityIndicatorView addSubview:self.activityIndicator];
    [self.activityIndicator startAnimating];
    [view addSubview:self.activityIndicatorView];
}

-(void)stopActivityIndicatorOnView:(id)view
{
    [self.activityIndicator stopAnimating];
    [self.activityIndicatorView removeFromSuperview];
    
    self.activityIndicator      = nil;
    self.activityIndicatorView  = nil;
}

-(id)getCurrentTimestampAsString:(bool)string inMs:(bool)ms
{
    double timestamp = [[NSDate date] timeIntervalSince1970];
    
    if (ms)
    {
        timestamp *= 1000;
    }
    long long timestampLongLong = timestamp;
    
    if (string)
    {
        return [NSString stringWithFormat:@"%lli", timestampLongLong];
    }
    
    if (!string)
    {
        return [NSNumber numberWithLongLong:timestampLongLong];
    }
    
    return @"-";
}

-(id)getCurrentTimezoneAsString:(bool)string
{
    NSDate *currentDate = [NSDate date];
    NSTimeZone *systemTimeZone = [NSTimeZone systemTimeZone];
    NSInteger currentTimezone = [systemTimeZone secondsFromGMTForDate:currentDate];
    
    if (string)
    {
        return [NSString stringWithFormat:@"%i", currentTimezone];
    }
    
    if (!string)
    {
        return [NSNumber numberWithInteger:currentTimezone];
    }
    
    return @"-";
}

-(NSDictionary*)getClientOS
{
    UIDevice *currentDevice = [UIDevice currentDevice];
    
    return @{
             @"client_os": [currentDevice systemName],
             @"client_os_version": [currentDevice systemVersion]
             };
}

-(NSNumber*)formatNumberToKb:(NSNumber*)number unit:(NSString *)unit
{
    long factor = 1;
    
    //bandwidth
    if ([unit caseInsensitiveCompare:@"mbit/s"] == NSOrderedSame)
    {
        factor = 1000;
    }
    
    //sizes
    else
        if ([unit caseInsensitiveCompare:@"kb"] == NSOrderedSame)
        {
            factor = 1;
        }
        else
            if ([unit caseInsensitiveCompare:@"mb"] == NSOrderedSame)
            {
                factor = 1000;
            }
            else
                if ([unit caseInsensitiveCompare:@"gb"] == NSOrderedSame)
                {
                    factor = 1000000;
                }
    
    return [NSNumber numberWithDouble:([number doubleValue] * factor)];
}

-(NSString*)generateRandomStringWithSize:(NSUInteger)size
{
    NSString *letters = @"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    NSMutableString *randomString = [NSMutableString stringWithCapacity:size];
    
    for (int i = 0; i < size; i++) {
        [randomString appendFormat: @"%C", [letters characterAtIndex: (long)arc4random_uniform((uint32_t)[letters length])]];
    }
    
    return randomString;
}

-(NSString*)getSHA256HashFromString:(NSString*)string
{
    const char* str = [string UTF8String];
    unsigned char result[CC_SHA256_DIGEST_LENGTH];
    CC_SHA256(str, (CC_LONG)strlen(str), result);
    
    NSMutableString *ret = [NSMutableString stringWithCapacity:CC_SHA256_DIGEST_LENGTH*2];
    
    for(int i = 0; i<CC_SHA256_DIGEST_LENGTH; i++)
    {
        [ret appendFormat:@"%02x",result[i]];
    }
    
    return ret;
}

-(NSDictionary*)getCallState
{
    NSMutableDictionary *callState = [NSMutableDictionary new];
    [callState setObject:[NSNumber numberWithBool:false] forKey:@"call"];
    [callState setObject:[NSNumber numberWithBool:false] forKey:@"hasEnded"];
    [callState setObject:[NSNumber numberWithBool:false] forKey:@"hasConnected"];
    [callState setObject:[NSNumber numberWithBool:false] forKey:@"isOutgoing"];
    [callState setObject:@"-" forKey:@"state"];
    [callState setObject:[NSNumber numberWithInt:0] forKey:@"app_call_state"];
    
    CXCallObserver *callObserver = [CXCallObserver new];
    
    if ([[callObserver calls] count] > 0)
    {
        CXCall *call = [[callObserver calls] objectAtIndex:0];
        
        if (call)
        {
            [callState setObject:[NSNumber numberWithBool:true] forKey:@"call"];
        }
        if (call.hasEnded)
        {
            [callState setObject:[NSNumber numberWithBool:true] forKey:@"hasEnded"];
        }
        if (call.hasConnected)
        {
            [callState setObject:[NSNumber numberWithBool:true] forKey:@"hasConnected"];
        }
        if (call.isOutgoing)
        {
            [callState setObject:[NSNumber numberWithBool:true] forKey:@"isOutgoing"];
        }
        
        if (call == nil || call.hasEnded == YES)
        {
            [callState setObject:@"disconnected" forKey:@"state"];
            [callState setObject:[NSNumber numberWithInt:0] forKey:@"app_call_state"];
            DDLogDebug(@"CXCallState : Disconnected");
        }
        if (call.isOutgoing == YES && call.hasConnected == NO)
        {
            [callState setObject:@"dialing" forKey:@"state"];
            [callState setObject:[NSNumber numberWithInt:1] forKey:@"app_call_state"];
            DDLogDebug(@"CXCallState : Dialing");
        }
        if (call.isOutgoing == NO  && call.hasConnected == NO && call.hasEnded == NO && call != nil)
        {
            [callState setObject:@"incoming" forKey:@"state"];
            [callState setObject:[NSNumber numberWithInt:1] forKey:@"app_call_state"];
            DDLogDebug(@"CXCallState : Incoming");
        }
        if (call.hasConnected == YES && call.hasEnded == NO)
        {
            [callState setObject:@"connected" forKey:@"state"];
            [callState setObject:[NSNumber numberWithInt:1] forKey:@"app_call_state"];
            DDLogDebug(@"CXCallState : Connected");
        }
    }

    return callState;
}

-(NSString*)getClientInstallationId
{
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    if (![userDefaults objectForKey:@"client_installation_id"])
    {
        NSString *randomString = [self generateRandomStringWithSize:512];
        NSString *client_cookie = [self getSHA256HashFromString:randomString];
        
        [userDefaults setObject:client_cookie forKey:@"client_installation_id"];
        [userDefaults synchronize];
    }
    
    return [userDefaults objectForKey:@"client_installation_id"];
}




/**************************** Public Debug Functions ****************************/

-(NSString*)debugAirplaneModeGsmOnlySimCardCarrierNetworkDataCurrentRadioAccessTechnology
{
    NSString *debugDescription;
    NSDictionary *networkData                   = [self getNetworkData];
    NSDictionary *callState                     = [self getCallState];
    NSDictionary *sCNetworkReachabilityFlags    = [self getSCNetworkReachabilityFlags];
    
    //airplane mode
    debugDescription = [NSString stringWithFormat:@"################\n\nairplane:\n%@", [NSString stringWithFormat:@"SCNetworkReachabilityFlagsStatus: %i, flags: %lli, networkStatus: %@", [[sCNetworkReachabilityFlags objectForKey:@"SCNetworkReachabilityFlagsStatus"] boolValue], [[sCNetworkReachabilityFlags objectForKey:@"SCNetworkReachabilityFlags"] longLongValue], [self networkStatus]]];
    
    debugDescription = [NSString stringWithFormat:@"%@\n\nairplane:\n%@", debugDescription, [[self getAirplaneModeAndGsmOnlyStatusWithNetworkData:networkData andCallState:callState andSCNetworkReachabilityFlags:sCNetworkReachabilityFlags] debugDescription]];
    
    //simcard
    CTTelephonyNetworkInfo *telephonyNetworkInfo = [CTTelephonyNetworkInfo new];
    CTCarrier *carrierInfo = [telephonyNetworkInfo subscriberCellularProvider];
    
    debugDescription = [NSString stringWithFormat:@"%@\n\nsim:\n%@", debugDescription, [carrierInfo debugDescription]];
    
    //carrier
    debugDescription = [NSString stringWithFormat:@"%@\ncarrier:\n%@", debugDescription, [[self getCarrierData] description]];
    
    //networkdata
    debugDescription = [NSString stringWithFormat:@"%@\n\nnetwork:\n%@", debugDescription, [networkData description]];
    
    return debugDescription;
}




/**************************** Public Delegate Functions Location ****************************/

-(void)startUpdatingLocationWithAccuracy:(CLLocationAccuracy)accuracy distanceFilter:(CLLocationDistance)distanceFilter allowsBackgroundLocationUpdates:(bool)allowsBackgroundLocationUpdates
{
    self.locationManager    = [CLLocationManager new];
    self.locations          = [NSMutableArray new];
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined)
    {
        [self.locationManager requestWhenInUseAuthorization];
        [self.locationManager requestAlwaysAuthorization];
    }
    self.locationManager.delegate           = self;
    self.locationManager.distanceFilter     = distanceFilter;
    self.locationManager.desiredAccuracy    = accuracy;
    if (allowsBackgroundLocationUpdates)
    {
        self.locationManager.allowsBackgroundLocationUpdates = true;
        self.locationManager.pausesLocationUpdatesAutomatically = false;
    }
    
    [self.locationManager startUpdatingLocation];
    //[self.locationManager startUpdatingHeading];
}

-(void)setDistanceFilter:(CLLocationDistance)distanceFilter
{
    self.locationManager.distanceFilter = distanceFilter;
}

-(void)stopUpdatingLocation
{
    [self.locationManager stopUpdatingLocation];
    //[self.locationManager stopUpdatingHeading];
    
    self.locationManager = nil;
}




/**************************** Public Delegate Functions MTSystemUsage ****************************/

-(void)startUpdatingSystemUsage
{
    self.cpuUsageCheckTimer         = [NSTimer new];
    self.cpuLoadSum                 = 0.0;
    self.cpuLoadCount               = 0;
    self.cpuLoadAvg                 = 0.0;
    
    self.memoryUsageCheckTimer      = [NSTimer new];
    self.memoryLoadSum              = 0.0;
    self.memoryLoadCount            = 0;
    self.memoryLoadAvg              = 0.0;
    
    self.systemUsageCheckInterval   = 0.8;
    
    self.systemUsageUpdateTimer     = [NSTimer new];
    self.systemUsageUpdateInterval  = 1.0;
    
    [self startTrackingCPUUsage];
    [self startTrackingMemoryUsage];
    
    self.systemUsageUpdateTimer = [NSTimer scheduledTimerWithTimeInterval:self.systemUsageUpdateInterval
                                                                   target:self
                                                                 selector:@selector(systemUsageUpdate)
                                                                 userInfo:nil
                                                                  repeats:true];
}

-(void)stopUpdatingSystemUsage
{
    [self.cpuUsageCheckTimer invalidate];
    [self.memoryUsageCheckTimer invalidate];
    
    [self.systemUsageUpdateTimer invalidate];
}




/**************************** LocationDelegate Callback Initiator ****************************/

-(void)locationDelegateDidUpdate:(NSArray<CLLocation *> *)locations
{
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.locationDelegate)
                       {
                           [self.locationDelegate locationDelegateDidUpdate:locations];
                       }
                   });
}




/**************************** SystemUsageDelegate Callback Initiator ****************************/

-(void)systemUsageDelegateDidUpdate:(NSDictionary*)systemUsage;
{
    dispatch_async(dispatch_get_main_queue(), ^
                   {
                       if (self.systemUsageDelegate)
                       {
                           [self.systemUsageDelegate systemUsageDelegateDidUpdate:systemUsage];
                       }
                   });
}




/**************************** LocationManagerDelegate Callbacks ****************************/

-(void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray<CLLocation *> *)locations
{
    [self.locations addObject:locations.lastObject];
    
    [self locationDelegateDidUpdate:self.locations];
}




/**************************** Internal Functions ****************************/

-(NSString*)getCurrentDeviceModelName
{
    struct utsname systemInfo;
    uname(&systemInfo);
    
    return [NSString stringWithCString:systemInfo.machine encoding:NSUTF8StringEncoding];
}

- (void)currentCPUUsage:(NSTimer *)timer
{
    natural_t numCPUsU = 0U;
    kern_return_t err = host_processor_info(mach_host_self(), PROCESSOR_CPU_LOAD_INFO, &numCPUsU, &_cpuInfo, &_numCpuInfo);
    float cpuLoadCurrentAvg = 0.0;
    if(err == KERN_SUCCESS) {
        [self.CPUUsageLock lock];
        
        for(unsigned i = 0U; i < _numCPUs; ++i) {
            float inUse, total;
            if(_prevCpuInfo) {
                inUse = (
                         (_cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_USER]   - _prevCpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_USER])
                         + (_cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_SYSTEM] - _prevCpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_SYSTEM])
                         + (_cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_NICE]   - _prevCpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_NICE])
                         );
                total = inUse + (_cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_IDLE] - _prevCpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_IDLE]);
            } else {
                inUse = _cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_USER] + _cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_SYSTEM] + _cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_NICE];
                total = inUse + _cpuInfo[(CPU_STATE_MAX * i) + CPU_STATE_IDLE];
            }
            
            cpuLoadCurrentAvg += inUse / total;
            //DDLogCVerbose(@"Core: %u Usage: %f", i, inUse / total);
        }
        [self.CPUUsageLock unlock];
        
        cpuLoadCurrentAvg = cpuLoadCurrentAvg / _numCPUs;
        
        _cpuLoadCount++;
        if (isnan(_cpuLoadSum)) _cpuLoadSum = 0.0;
        _cpuLoadSum += cpuLoadCurrentAvg;
        
        if(_prevCpuInfo) {
            size_t prevCpuInfoSize = sizeof(integer_t) * _numPrevCpuInfo;
            vm_deallocate(mach_task_self(), (vm_address_t)_prevCpuInfo, prevCpuInfoSize);
        }
        
        _prevCpuInfo = _cpuInfo;
        _numPrevCpuInfo = _numCpuInfo;
        
        _cpuInfo = NULL;
        _numCpuInfo = 0U;
    }
    else
    {
        DDLogError(@"currentCPUUsage error");
    }
}

-(void)startTrackingCPUUsage
{
    int mib[2U] = { CTL_HW, HW_NCPU };
    size_t sizeOfNumCPUs = sizeof(_numCPUs);
    int status = sysctl(mib, 2U, &_numCPUs, &sizeOfNumCPUs, NULL, 0U);
    if(status) _numCPUs = 1;
    
    self.CPUUsageLock = [NSLock new];
    
    self.cpuUsageCheckTimer = [NSTimer scheduledTimerWithTimeInterval:self.systemUsageCheckInterval
                                                               target:self
                                                             selector:@selector(currentCPUUsage:)
                                                             userInfo:nil
                                                              repeats:true];
}

-(double)getTotalMemory
{
    @try
    {
        double TotalMemory = 0.00;
        double AllMemory = [[NSProcessInfo processInfo] physicalMemory];
        
        TotalMemory = (AllMemory / 1024.0) / 1024.0;
        
        int toNearest = 256;
        int remainder = (int)TotalMemory % toNearest;
        
        if (remainder >= toNearest / 2)
        {
            TotalMemory = ((int)TotalMemory - remainder) + 256;
        } else
        {
            TotalMemory = (int)TotalMemory - remainder;
        }
        
        if (TotalMemory <= 0)
        {
            return -1;
        }
        
        return TotalMemory;
    }
    @catch (NSException *exception)
    {
        DDLogError(@"getTotalMemory Error");
    }
}

-(void)currentMemoryUsage{
    
    @try
    {
        double TotalUsedMemory = 0.00;
        mach_port_t host_port;
        mach_msg_type_number_t host_size;
        vm_size_t pagesize;
        
        host_port = mach_host_self();
        host_size = sizeof(vm_statistics_data_t) / sizeof(integer_t);
        host_page_size(host_port, &pagesize);
        
        vm_statistics_data_t vm_stat;
        
        if (host_statistics(host_port, HOST_VM_INFO, (host_info_t)&vm_stat, &host_size) != KERN_SUCCESS)
        {
            DDLogError(@"Memory Usage Track Error");
        }
        
        // Memory statistics in bytes
        natural_t UsedMemory = (natural_t)((vm_stat.active_count +
                                            vm_stat.inactive_count +
                                            vm_stat.wire_count) * pagesize);
        natural_t AllMemory = self.getTotalMemory;
        
        double UM = (UsedMemory /1024) / 1024;
        double AM = AllMemory;
        
        TotalUsedMemory = UM / AM;
        
        self.memoryLoadCount++;
        self.memoryLoadSum += TotalUsedMemory;
    }
    @catch (NSException *exception)
    {
        DDLogError(@"currentMemoryUsage error");
    }
}

-(void)startTrackingMemoryUsage
{
    self.memoryUsageCheckTimer = [NSTimer scheduledTimerWithTimeInterval:self.systemUsageCheckInterval
                                                                  target:self
                                                                selector:@selector(currentMemoryUsage)
                                                                userInfo:nil
                                                                 repeats:true];
}

-(long long)getDiskSpace
{
    @try
    {
        long long diskSpace = 0L;
        NSError *error = nil;
        NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfFileSystemForPath:NSHomeDirectory() error:&error];
        
        if (error == nil)
        {
            diskSpace = [[fileAttributes objectForKey:NSFileSystemSize] longLongValue];
        }
        else
        {
            return -1;
        }
        
        if (diskSpace <= 0)
        {
            return -1;
        }
        
        return diskSpace;
    }
    @catch (NSException *exception)
    {
        DDLogError(@"getDiskSpace error");
        return -1;
    }
}

-(long long)getFreeDiskSpace
{
    @try
    {
        long long freeDiskSpace = 0L;
        NSError *error = nil;
        NSDictionary *fileAttributes = [[NSFileManager defaultManager] attributesOfFileSystemForPath:NSHomeDirectory() error:&error];
        
        if (error == nil)
        {
            freeDiskSpace = [[fileAttributes objectForKey:NSFileSystemFreeSize] longLongValue];
        }
        else
        {
            return -1;
        }
        
        if (freeDiskSpace <= 0)
        {
            return -1;
        }
        
        return freeDiskSpace;
    }
    @catch (NSException *exception)
    {
        DDLogError(@"getFreeDiskSpace error");
        return -1;
    }
}

-(void)systemUsageUpdate
{
    NSMutableDictionary *systemUsage = [NSMutableDictionary new];
    
    [systemUsage setObject:[NSNumber numberWithDouble:(self.cpuLoadSum / (double)self.cpuLoadCount)] forKey:@"app_cpu"];
    [systemUsage setObject:[NSNumber numberWithDouble:(self.memoryLoadSum / (double)self.memoryLoadCount)] forKey:@"app_memory"];
    [systemUsage setObject:[NSNumber numberWithDouble:(1.0 - ((double)[self getFreeDiskSpace] / (double)[self getDiskSpace]))] forKey:@"app_storage"];
    
    [self systemUsageDelegateDidUpdate:systemUsage];
}


@end
