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
 *      \date Last update: 2019-09-30
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
@property (weak, nonatomic) IBOutlet UIButton *stopButton;
@property (weak, nonatomic) IBOutlet UIButton *clearButton;
@property (weak, nonatomic) IBOutlet UIButton *startButton;
@property (weak, nonatomic) IBOutlet UIButton *startRttButton;
@property (weak, nonatomic) IBOutlet UIButton *startDownloadButton;
@property (weak, nonatomic) IBOutlet UIButton *startUploadButton;

@property (weak, nonatomic) IBOutlet UILabel *statusLabel;
@property (weak, nonatomic) IBOutlet UILabel *rttLabel;
@property (weak, nonatomic) IBOutlet UILabel *downloadLabel;
@property (weak, nonatomic) IBOutlet UILabel *uploadLabel;

@property (weak, nonatomic) IBOutlet UITextView *kpisTextView;

@property (weak, nonatomic) IBOutlet UISegmentedControl *ipVersionParameters;
@property (weak, nonatomic) IBOutlet UISegmentedControl *singleStreamParameters;
@property (weak, nonatomic) IBOutlet UISegmentedControl *tlsParameters;



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

- (IBAction)startButtonTouched:(id)sender
{
    [self measurementStartWithTestCaseRtt:true withTestCaseDownload:true withTestCaseUpload:true];
}

- (IBAction)startRttButtonTouched:(id)sender
{
    [self measurementStartWithTestCaseRtt:true withTestCaseDownload:false withTestCaseUpload:false];
}

- (IBAction)startDownloadButtonTouched:(id)sender
{
    [self measurementStartWithTestCaseRtt:false withTestCaseDownload:true withTestCaseUpload:false];
}

- (IBAction)startUploadButtonTouched:(id)sender
{
    [self measurementStartWithTestCaseRtt:false withTestCaseDownload:false withTestCaseUpload:true];
}

-(void)showKpisFromResponse:(NSDictionary*)response
{
    NSString *kpis = response.description;
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
            self.rttLabel.text = [NSString stringWithFormat:@"%@ ns", [[response objectForKey:@"rtt_info"] objectForKey:@"average_ns"]];
        }
        if ([[response objectForKey:@"test_case"] isEqualToString:@"download"])
        {
            self.downloadLabel.text = [NSString stringWithFormat:@"%@ Mbit/s", [self.tool formatNumberToCommaSeperatedString:[NSNumber numberWithDouble:([[[[response objectForKey:@"download_info"] lastObject] objectForKey:@"throughput_avg_bps"] doubleValue] /1000.0 / 1000.0)] withMinDecimalPlaces:2 withMaxDecimalPlace:2]];
        }
        if ([[response objectForKey:@"test_case"] isEqualToString:@"upload"])
        {
            self.uploadLabel.text = [NSString stringWithFormat:@"%@ Mbit/s", [self.tool formatNumberToCommaSeperatedString:[NSNumber numberWithDouble:([[[[response objectForKey:@"upload_info"] lastObject] objectForKey:@"throughput_avg_bps"] doubleValue] /1000.0 / 1000.0)] withMinDecimalPlaces:2 withMaxDecimalPlace:2]];
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

-(void)measurementStartWithTestCaseRtt:(bool)rtt withTestCaseDownload:(bool)download withTestCaseUpload:(bool)upload
{
    [self toggleButtons:false];
    self.stopButton.enabled = true;
    
    [self clearUI];
    
    DDLogInfo(@"Measurement started");
    self.statusLabel.text = @"Measurement started";
    
    /*-------------------------set parameters for demo implementation start------------------------*/

    self.speed.platform                           = @"mobile";
    
    self.speed.targets                            = [NSArray arrayWithObjects:@"peer-ias-de-01", nil];
    
    if ([self.ipVersionParameters selectedSegmentIndex] == 1 || [self.ipVersionParameters selectedSegmentIndex] == 2)
    {
        NSString *targetIpVersion = @"";
        if ([self.ipVersionParameters selectedSegmentIndex] == 1)
        {
            targetIpVersion = @"-ipv4";
        }
        else if ([self.ipVersionParameters selectedSegmentIndex] == 2)
        {
            targetIpVersion = @"-ipv6";
        }

        NSMutableArray *targets = [NSMutableArray new];
        for (int i=0; i<[self.speed.targets count]; i++)
        {
            [targets addObject:[self.speed.targets[i] stringByAppendingString:targetIpVersion]];
        }
        self.speed.targets = targets;
        targets = [NSMutableArray new];
    }
    
    self.speed.performRttMeasurement              = rtt;
    self.speed.performDownloadMeasurement         = download;
    self.speed.performUploadMeasurement           = upload;
    self.speed.performGeolocationLookup           = true;
    
    
    //dl/ul parameters if no speed classes are used or singleStream is active
    self.speed.parallelStreamsDownload  = [NSNumber numberWithInt:4];
    self.speed.frameSizeDownload        = [NSNumber numberWithInt:32768];
    if ([self.singleStreamParameters selectedSegmentIndex] == 1)
    {
        self.speed.frameSizeDownload        = [NSNumber numberWithInt: [self.speed.frameSizeDownload intValue] * [self.speed.parallelStreamsDownload intValue]];
        self.speed.parallelStreamsDownload  = [NSNumber numberWithInt:1];
    }
    
    self.speed.parallelStreamsUpload    = [NSNumber numberWithInt:4];
    self.speed.frameSizeUpload          = [NSNumber numberWithInt:32768];
    if ([self.singleStreamParameters selectedSegmentIndex] == 1)
    {
        self.speed.frameSizeUpload          = [NSNumber numberWithInt: [self.speed.frameSizeUpload intValue] * [self.speed.parallelStreamsUpload intValue]];
        self.speed.parallelStreamsUpload    = [NSNumber numberWithInt:1];
    
        if ([self.speed.frameSizeUpload intValue] > 65535)
        {
            self.speed.frameSizeUpload      = [NSNumber numberWithInt:65535];
        }
    }
    
    if ([self.tlsParameters selectedSegmentIndex] == 1)
    {
        self.speed.targetsPort = @"443";
        self.speed.wss  = 1;
    }
    
    if ([self.singleStreamParameters selectedSegmentIndex] != 1)
    {
        //dl/ul speed classes
        NSMutableDictionary *class = [NSMutableDictionary new];
        NSMutableDictionary *classBounds = [NSMutableDictionary new];
        
        
        //dl low
        [class setObject:[NSNumber numberWithBool:false] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:2048] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:0.00] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:1.05] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [self.speed.downloadClasses addObject:class];
        
        //dl default
        class = [NSMutableDictionary new];
        classBounds = [NSMutableDictionary new];
        [class setObject:[NSNumber numberWithBool:true] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:32768] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:0.95] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:525] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [self.speed.downloadClasses addObject:class];
        
        //dl high
        class = [NSMutableDictionary new];
        classBounds = [NSMutableDictionary new];
        [class setObject:[NSNumber numberWithBool:false] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:524288] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:475] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:1050] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [self.speed.downloadClasses addObject:class];
        
        //dl very high
        class = [NSMutableDictionary new];
        classBounds = [NSMutableDictionary new];
        [class setObject:[NSNumber numberWithBool:false] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:8] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:524288] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:950] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:9000] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [self.speed.downloadClasses addObject:class];
        
        
        //ul low
        [class setObject:[NSNumber numberWithBool:false] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:2048] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:0.00] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:1.05] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [class setObject:[NSNumber numberWithInt:1] forKey:@"framesPerCall"];
        [self.speed.uploadClasses addObject:class];
        
        //ul default
        class = [NSMutableDictionary new];
        classBounds = [NSMutableDictionary new];
        [class setObject:[NSNumber numberWithBool:true] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:65535] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:0.95] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:525] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [class setObject:[NSNumber numberWithInt:2] forKey:@"framesPerCall"];
        [self.speed.uploadClasses addObject:class];
        
        //ul high
        class = [NSMutableDictionary new];
        classBounds = [NSMutableDictionary new];
        [class setObject:[NSNumber numberWithBool:false] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:65535] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:475] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:1050] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [class setObject:[NSNumber numberWithInt:4] forKey:@"framesPerCall"];
        [self.speed.uploadClasses addObject:class];
        
        //ul very high
        class = [NSMutableDictionary new];
        classBounds = [NSMutableDictionary new];
        [class setObject:[NSNumber numberWithBool:false] forKey:@"default"];
        [class setObject:[NSNumber numberWithInt:20] forKey:@"streams"];
        [class setObject:[NSNumber numberWithInt:65535] forKey:@"frameSize"];
        [classBounds setObject:[NSNumber numberWithDouble:950] forKey:@"lower"];
        [classBounds setObject:[NSNumber numberWithDouble:9000] forKey:@"upper"];
        [class setObject:classBounds forKey:@"bounds"];
        [class setObject:[NSNumber numberWithInt:20] forKey:@"framesPerCall"];
        [self.speed.uploadClasses addObject:class];
    }

    /*-------------------------set parameters for demo implementation end------------------------*/
    
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
    
    self.clearButton.enabled            = true;
    self.startButton.enabled            = true;
    self.startRttButton.enabled         = true;
    self.startDownloadButton.enabled    = true;
    self.startUploadButton.enabled      = true;
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
    }
    else
    {
        DDLogInfo(@"Measurement successful");
        self.statusLabel.text = @"Measurement successful";
    }
    
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
        self.stopButton.enabled             = false;
        self.clearButton.enabled            = false;
        self.startButton.enabled            = false;
        self.startRttButton.enabled         = false;
        self.startDownloadButton.enabled    = false;
        self.startUploadButton.enabled      = false;
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
    self.stopButton.enabled                     = enable;
    self.clearButton.enabled                    = enable;
    self.startButton.enabled                    = enable;
    self.startRttButton.enabled                 = enable;
    self.startDownloadButton.enabled            = enable;
    self.startUploadButton.enabled              = enable;
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
