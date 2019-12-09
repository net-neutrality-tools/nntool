/*!
    \file Tool.h
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

#import "Reachability.h"
#import "LogFormatter.h"
#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import <sys/utsname.h>
#import <CoreTelephony/CTTelephonyNetworkInfo.h>
#import <CoreTelephony/CTCarrier.h>
#import <MapKit/MapKit.h>
#import <sys/sysctl.h>
#import <sys/types.h>
#import <mach/mach.h>
#import <mach/processor_info.h>
#import <mach/mach_host.h>
#import <CallKit/CXCallObserver.h>
#import <CallKit/CXCall.h>
#import <SystemConfiguration/CaptiveNetwork.h>

@import CocoaLumberjack;




@protocol LocationDelegate <NSObject>

/**************************** Required Delegate Functions Location ****************************/

@required
-(void)locationDelegateDidUpdate:(NSArray<CLLocation *> *)locations;
@end




@protocol SystemUsageDelegate <NSObject>

/**************************** Required Delegate Functions MTSystemUsage ****************************/

@required
-(void)systemUsageDelegateDidUpdate:(NSDictionary*)systemUsage;
@end




@interface Tool : NSObject




/**************************** Public Variables ****************************/

@property (nonatomic, strong) id locationDelegate;              /**< Location Protocol Delegate */
@property (nonatomic, strong) id systemUsageDelegate;           /**< SystemUsage Protocol Delegate */


/**************************** Public Functions ****************************/

@property (nonatomic) bool networkReachable;
@property (nonatomic, strong) NSString *networkStatus;

-(NSError *)getError:(long)errorCode description:(NSString*)errorDescription domain:(NSString*)errorDomain;
-(NSError*)getNetworkReachableErrorWithDomain:(NSString*)domain;
-(NSError*)getHttpErrorWithStatusCode:(long)statusCode domain:(NSString*)domain;
-(NSError*)getHttpErrorWithMalformedUrl:(NSURL*)url domain:(NSString*)domain;
-(NSError *)getScriptingErrorWithDescription:(NSString*)errorDescription domain:(NSString*)errorDomain;
-(NSString*)formatNumberToCommaSeperatedString:(NSNumber*)number withMinDecimalPlaces:(int)min withMaxDecimalPlace:(int)max;
-(NSDictionary*)getDeviceData;
-(NSDictionary*)getAirplaneModeAndGsmOnlyStatusWithNetworkData:(NSDictionary*)networkData andCallState:(NSDictionary*)callState andSCNetworkReachabilityFlags:(NSDictionary*)sCNetworkReachabilityFlags;
-(NSDictionary*)getSCNetworkReachabilityFlags;
//-(bool)getSimCardStatus;
-(NSDictionary*)getCarrierData;
-(NSDictionary*)getNetworkData;
-(NSArray*)getWifiData;
-(NSDictionary*)getCurrentRadioAccessTechnologyWithServiceSubscriberCellularProvidersKey:(NSString*)serviceSubscriberCellularProvidersKey;
-(void)startActivityIndicatorOnView:(id)view withFrame:(CGRect)frame withBackgroundColor:(UIColor*)color withActivityIndicatorStyle:(UIActivityIndicatorViewStyle)style;
-(void)stopActivityIndicatorOnView:(id)view;
-(id)getCurrentTimestampAsString:(bool)string inMs:(bool)ms;
-(id)getCurrentTimezoneAsString:(bool)string;
-(NSDictionary*)getClientOS;
-(NSNumber*)formatNumberToKb:(NSNumber*)number unit:(NSString *)unit;
-(NSString*)generateRandomStringWithSize:(NSUInteger)size;
-(NSString*)getSHA256HashFromString:(NSString*)string;
-(NSDictionary*)getCallState;
-(NSString*)getClientInstallationId;


/**************************** Public Debug Functions ****************************/

-(NSString*)debugAirplaneModeGsmOnlySimCardCarrierNetworkDataCurrentRadioAccessTechnology;


/**************************** Public Delegate Functions Location ****************************/

-(void)startUpdatingLocationWithAccuracy:(CLLocationAccuracy)accuracy distanceFilter:(CLLocationDistance)distanceFilter allowsBackgroundLocationUpdates:(bool)allowsBackgroundLocationUpdates;
-(void)setDistanceFilter:(CLLocationDistance)distanceFilter;
-(void)stopUpdatingLocation;


/**************************** Public Delegate Functions SystemUsage ****************************/

-(void)startUpdatingSystemUsage;
-(void)stopUpdatingSystemUsage;


@end
