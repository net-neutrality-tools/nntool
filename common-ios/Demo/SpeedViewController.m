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

#import "SpeedViewController.h"




/**************************** Loging Level ****************************/


#ifdef DEBUG
    static const DDLogLevel ddLogLevel = DDLogLevelDebug;
#else
    static const DDLogLevel ddLogLevel = DDLogLevelWarning;
#endif




@interface SpeedViewController () <SpeedDelegate>


/**************************** UI Elements ****************************/

@property (weak, nonatomic) IBOutlet UIButton *loadButton;
@property (weak, nonatomic) IBOutlet UIButton *startButton;
@property (weak, nonatomic) IBOutlet UIButton *stopButton;
@property (weak, nonatomic) IBOutlet UIButton *clearButton;

@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *rttLabel;
@property (weak, nonatomic) IBOutlet UILabel *downloadLabel;
@property (weak, nonatomic) IBOutlet UILabel *uploadLabel;

@property (weak, nonatomic) IBOutlet UITextView *kpisTextView;




/**************************** Global Variables ****************************/

@property (nonatomic, strong) Speed *speed;
@property (nonatomic, strong) Tool *tool;
@property (nonatomic, strong) CLLocationManager *locationManager;

@end




@implementation SpeedViewController




/**************************** UI Handling ****************************/

- (IBAction)loadButtonTouched:(id)sender
{
    [self toggleButtons:false];
    
    [self clearUI];
    
    [self measurementLoad];
}

- (IBAction)startButtonTouched:(id)sender
{
    [self toggleButtons:false];
    self.stopButton.enabled                         = true;
    
    [self clearUI];
    
    [self measurementStart];
}

- (IBAction)stopButtonTouched:(id)sender
{
    [self toggleButtons:false];

    [self measurementStop];
}

- (IBAction)clearButtonTouched:(id)sender
{
    [self toggleButtons:false];
    
    [self clearUI];
    
    [self measurementClearCache];
}

-(void)showKpisFromResponse:(NSDictionary*)response
{
    NSString *kpis = response.description;
    kpis = [kpis stringByReplacingOccurrencesOfString:@"{\n" withString:@""];
    kpis = [kpis stringByReplacingOccurrencesOfString:@"\n}" withString:@""];
    kpis = [kpis stringByReplacingOccurrencesOfString:@";" withString:@""];
    self.kpisTextView.text = kpis;
    
    NSString *cmd = [response objectForKey:@"cmd"];
    
    if ([cmd isEqualToString:@"info"] || [cmd isEqualToString:@"finish"])
    {
        self.statusLabel.text = [NSString stringWithFormat:@"%@: %@", [response objectForKey:@"test_case"], [response objectForKey:@"msg"]];
    }
    
    if ([cmd isEqualToString:@"report"] || [cmd isEqualToString:@"finish"])
    {
        if ([[response objectForKey:@"test_case"] isEqualToString:@"rtt"])
        {
            self.rttLabel.text = [NSString stringWithFormat:@"%@ ms", [self.tool formatNumberToCommaSeperatedString:[response objectForKey:@"rtt_avg"] withMinDecimalPlaces:2 withMaxDecimalPlace:2]];
        }
        if ([[response objectForKey:@"test_case"] isEqualToString:@"download"])
        {
            self.downloadLabel.text = [NSString stringWithFormat:@"%@ Mbit/s", [self.tool formatNumberToCommaSeperatedString:[response objectForKey:@"download_rate_avg_mbits"] withMinDecimalPlaces:2 withMaxDecimalPlace:2]];
        }
        if ([[response objectForKey:@"test_case"] isEqualToString:@"upload"])
        {
            self.uploadLabel.text = [NSString stringWithFormat:@"%@ Mbit/s", [self.tool formatNumberToCommaSeperatedString:[response objectForKey:@"upload_rate_avg_mbits"] withMinDecimalPlaces:2 withMaxDecimalPlace:2]];
        }
    }
    
}




/**************************** Measurement Handling ****************************/

-(void)measurementLoad
{
    //initialize MTWSMeasurement object and set mandatory options
    self.speed                              = nil;
    self.speed                              = [Speed new];
    //self.speed.indexUrl                     = @"";
    self.speed.speedDelegate                = self;
    
    self.tool                               = nil;
    self.tool                               = [Tool new];
    
    //load measurement api
    [self.speed measurementLoad];
}

-(void)measurementStart
{
    DDLogInfo(@"Measurement started");
    self.statusLabel.text = @"Measurement started";
    
    //set measurement parameters
    self.speed.platform                           = @"mobile";
    
    self.speed.performRttMeasurement              = true;
    self.speed.performDownloadMeasurement         = true;
    self.speed.performUploadMeasurement           = true;
    
    self.speed.performRouteToClientLookup         = true;
    self.speed.performGeolocationLookup           = true;
    
    [self.speed measurementStart];
}

-(void)measurementStop
{
    [self.speed measurementStop];
}

-(void)measurementClearCache
{
    [self.speed measurementClearCache];
}




/**************************** Speed Callbacks ****************************/

-(void)measurementDidLoadWithResponse:(NSDictionary *)response withError:(NSError *)error
{
    if (error)
    {
        DDLogError(@"MeasurementLoad failed with Error: %@", [error.userInfo objectForKey:@"NSLocalizedDescription"]);
        self.statusLabel.text = [error.userInfo objectForKey:@"NSLocalizedDescription"];
        self.loadButton.enabled = true;

        return;
    }
    
    DDLogInfo(@"MeasurementLoad successful");
    self.statusLabel.text = @"MeasurementLoad successful";
    
    self.startButton.enabled        = true;
    self.clearButton.enabled        = true;
}

-(void)measurementCallbackWithResponse:(NSDictionary *)response
{
    [self showKpisFromResponse:response];
}

-(void)measurementDidCompleteWithResponse:(NSDictionary *)response withError:(NSError *)error
{
    [self showKpisFromResponse:response];
    
    if (error)
    {
        DDLogError(@"Measurement failed with Error: %@", [error.userInfo objectForKey:@"NSLocalizedDescription"]);
        self.statusLabel.text = [error.userInfo objectForKey:@"NSLocalizedDescription"];
        return;
    }
    
    DDLogInfo(@"Measurement successful");
    self.statusLabel.text = @"Measurement successful";
    
    self.loadButton.enabled         = true;
    self.stopButton.enabled         = false;
    self.clearButton.enabled        = true;
}

-(void)measurementDidStop
{
    DDLogInfo(@"Measurement stopped");
    self.statusLabel.text = @"Measurement stopped";
    
    self.loadButton.enabled         = true;
    self.stopButton.enabled         = false;
    self.clearButton.enabled        = true;
}

-(void)measurementDidClearCache
{
    DDLogInfo(@"Cache cleared");
    self.statusLabel.text = @"Cache cleared";
    
    self.loadButton.enabled         = true;
}




/**************************** Others ****************************/

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    [DDLog removeAllLoggers];
    [DDLog addLogger:[DDTTYLogger sharedInstance]];
    [DDTTYLogger sharedInstance].logFormatter = [LogFormatter new];
    
    if ([CLLocationManager authorizationStatus] == kCLAuthorizationStatusNotDetermined || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusDenied || [CLLocationManager authorizationStatus] == kCLAuthorizationStatusRestricted)
    {
        self.locationManager = [CLLocationManager new];
        [self.locationManager requestWhenInUseAuthorization];
    }
    
    DDLogInfo(@"Demo: Version %@", [NSString stringWithFormat:@"%@", [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleShortVersionString"]]);
    
    if (!self.speed)
    {
        self.startButton.enabled        = false;
        self.stopButton.enabled         = false;
        self.clearButton.enabled        = false;
    }
    
    [self loadButtonTouched:nil];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

-(void)toggleButtons:(bool)enable
{
    self.loadButton.enabled                     = enable;
    self.startButton.enabled                    = enable;
    self.stopButton.enabled                     = enable;
    self.clearButton.enabled                    = enable;
}

-(void)clearUI
{
    self.statusLabel.text       = @"-";
    self.rttLabel.text          = @"-";
    self.downloadLabel.text     = @"-";
    self.uploadLabel.text       = @"-";
    
    self.kpisTextView.text      = @"";
}


@end
