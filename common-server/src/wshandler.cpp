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
 *      \date Last update: 2019-01-16
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */


#include "wshandler.h"


using namespace std;
using namespace json11;




int                 mSocket;
sockaddr_in6        *mClient;
bool                mTlsSocket;
        
vector<string>      allowedProtocols;
unsigned long long  webSocketTimeout;
string              sClientIp;

string              secret;

string              authToken; 

string              hostname;

bool                rttRunning;
bool                downloadRunning;
bool                uploadRunning;

int                 downloadFrameSize;
long long           downloadRandomDataSize;

int                 rttRequests;
unsigned long long  rttRequestTimeout;
long                rttRequestWait;
unsigned long long  rttTimeout;
int                 rttPayloadSize;
string              rttPayload;
string              rttPayloadTimestamp;
string              rttPayloadDelimiter;
bool                rttStart;
unsigned long long  rttPingSendTime;
unsigned long long  rttPongReceiveTime;
unsigned long long  rttPongAllegedReceiveTime;
vector<long long>   rttVector;

double              rttAvg;
double              rttMed;
double              rttMin;
double              rttMax;
int                 rttRequestsSend;
int                 rttReplies;
int                 rttErrors;
int                 rttMissing;
int                 rttPacketsize;
double              rttStdDevPop;
                            
long long           uploadBytesReceived;
long long           uploadHeaderReceived;

bool                showShutdown;
bool                showStopped;
bool                showRttStart;

string              certDir;




CWsHandler::CWsHandler()
{
}


CWsHandler::~CWsHandler()
{
}


CWsHandler::CWsHandler(sockaddr_in6 *pClient, int nSocket, string nClientIp, bool nTlsSocket)
{
    mClient                     = pClient;
    mSocket                     = nSocket;
    mTlsSocket                  = nTlsSocket;
    
    webSocketTimeout            = 25;
    
    allowedProtocols.push_back("rtt");
    allowedProtocols.push_back("download");
    allowedProtocols.push_back("upload");
    
    sClientIp                   = nClientIp;
    
    secret                      = "";
    
    authToken                   = "";
    
    hostname                    = "peer-ias-de-01";

    rttRunning                  = false;
    downloadRunning             = false;
    uploadRunning               = false;
    
    downloadFrameSize           = 32768;
    downloadRandomDataSize      = 1234571;

    rttRequests                 = 10+1;
    rttRequestTimeout           = 1000;
    rttRequestWait              = 500;
    rttTimeout                  = (rttRequests * (rttRequestTimeout + rttRequestWait)) * 1.3;
    rttPayloadDelimiter         = ";";
    rttPayloadSize              = 64;
    rttStart                    = false;
    rttPingSendTime             = 0;
    rttPongReceiveTime          = 0;
    rttPongAllegedReceiveTime   = 0;
    rttVector.clear();

    uploadBytesReceived         = 0;
    uploadHeaderReceived        = 0;
    
    showShutdown                = false;
    showStopped                 = true;
    showRttStart                = true;
    
    certDir                     = "/var/opt/common-server/certs/";
}


int CWsHandler::handle_websocket()
{
    TRC_DEBUG("WebSocket handler: started");
        
    noPollCtx *ctx = nopoll_ctx_new();
    if (!ctx)
    {
        TRC_ERR("WebSocket handler: noPoll context initialization failed");
        return 1;
    }
    
    nopoll_log_enable(ctx, nopoll_true);
    nopoll_log_set_handler(ctx, nopoll_logging_handler, NULL);

    noPollConn *conn = nopoll_listener_from_socket(ctx, (NOPOLL_SOCKET)mSocket, mTlsSocket);
    
    if (!nopoll_conn_is_ok(conn))
    {
        TRC_ERR("WebSocket handler: noPoll connection initialization failed");
        return 1;
    }
    
    if (mTlsSocket)
    {
        TRC_INFO("WebSocket handler: TLS secured connection requested");
        
        DIR *dir;
        struct dirent *ent;
        
        if ((dir = opendir(certDir.c_str())) != NULL)
        {
            while ((ent = readdir(dir)) != NULL)
            {
                string dirName = ent->d_name;
                string certFile;
                string keyFile;
                
                if (!dirName.compare(".") == 0 && !dirName.compare("..") == 0)
                {
                    certFile = certDir + dirName + "/" + dirName + ".crt";
                    keyFile = certDir + dirName + "/" + dirName + ".key";
                                     
                    if (nopoll_ctx_set_certificate(ctx, dirName.c_str(), certFile.c_str(), keyFile.c_str(), NULL))
                    {
                        TRC_DEBUG("WebSocket handler: noPoll context TLS certificate set for: " + dirName);
                    }
                    else
                    {
                        TRC_CRIT("WebSocket handler: error: noPoll context TLS certificate set failed for: " + dirName);
                        return 0;
                    }
                }
            }
            closedir (dir);
        } 
        else 
        {
            TRC_CRIT("WebSocket handler: error: failed to open TLS certificate Directory: " + certDir);
            return 0;
        }
        
        if (nopoll_conn_is_tls_on(conn))
        {
            TRC_DEBUG("WebSocket handler: noPoll listener TLS enabled");
        }
        else
        {
            TRC_CRIT("WebSocket handler: error: noPoll listener TLS not enabled");
            return 0;
        }

        nopoll_ctx_set_post_ssl_check(ctx, websocket_post_ssl_handler, NULL);

        nopoll_conn_accept_complete(ctx, conn, conn, (NOPOLL_SOCKET)mSocket, nopoll_true);
    }
    
    //set handlers
    nopoll_ctx_set_on_open(ctx, websocket_open_handler, NULL);
    nopoll_ctx_set_on_ready(ctx, websocket_ready_handler, NULL);
    nopoll_ctx_set_on_msg(ctx, websocket_message_handler, NULL);
  
    //wait loop for event notification 
    nopoll_loop_wait(ctx, 0);

    //cleanup
    nopoll_ctx_unref(ctx);
    nopoll_cleanup_library();
    
    if (showStopped)
    {
        TRC_DEBUG("WebSocket handler: stopped");
    }
    
    return 0;
}


int CWsHandler::websocket_open_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: open");
    
    nopoll_conn_set_on_close(conn, websocket_close_handler, NULL);
    nopoll_conn_set_sock_block(nopoll_conn_socket(conn), nopoll_true);
    
    string sProtocol = string(nopoll_conn_get_requested_protocol(conn));
    CTool::replaceStringInPlace(sProtocol, " ", "");
    
    vector<string> requestestedProtocols;
    string delimiter = ",";
    CTool::tokenize(sProtocol, requestestedProtocols, delimiter);
    
    bool protocolAllowed = false;
    
    string acceptedProtocol;
    
    for (vector<string>::iterator itAllowedProtocols = allowedProtocols.begin(); itAllowedProtocols != allowedProtocols.end(); ++itAllowedProtocols)
    {
        for (vector<string>::iterator itRequestedProtocols = requestestedProtocols.begin(); itRequestedProtocols != requestestedProtocols.end(); ++itRequestedProtocols)
        {
            string requestedProtocol = *itRequestedProtocols;
            if (requestedProtocol.compare(*itAllowedProtocols) == 0)
            {
                if (requestestedProtocols.size() > 2)
                {
                    acceptedProtocol        = requestestedProtocols.at(0);
                    authToken               = requestestedProtocols.at(1);
                    string authTimestamp    = requestestedProtocols.at(2);
                    TRC_DEBUG("WebSocket handler: requested protocol:   			\"" + requestedProtocol + "\"");
                    TRC_DEBUG("WebSocket handler: requested timestamp:  			\"" + authTimestamp + "\"");
                    TRC_DEBUG("WebSocket handler: requested token:      			\"" + authToken + "\"");
					
					if (acceptedProtocol.compare("download") == 0 && requestestedProtocols.size() > 3)
					{
						downloadFrameSize = atoi(requestestedProtocols.at(3).c_str());
						TRC_DEBUG("WebSocket handler: requested download frame size:	\"" + to_string(downloadFrameSize) + "\"");
					}
                
                    protocolAllowed = checkAuth(authToken, authTimestamp);
                    
                    break;
                }
            }
        }
    }
    
    if (protocolAllowed)
    {
        nopoll_conn_set_accepted_protocol(conn, acceptedProtocol.c_str());
        TRC_DEBUG("WebSocket handler: requested protocol: \"" + acceptedProtocol + "\" is allowed");
    }
    else if (!protocolAllowed)
    {
        TRC_ERR("WebSocket handler: requested protocol: \"" + sProtocol + "\" is not allowed");

        char response[] = "HTTP/1.1 400 Bad Request\r\n\r\n";
        int responseSize = strlen(response);

        nopoll_conn_default_send(conn, response, responseSize);
        
        return 0;
    }

    return 1;
}


int CWsHandler::websocket_ready_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: ready for IP: " + sClientIp + " on Port: " + string(nopoll_conn_port(conn)));
    
    string sProtocol = string(nopoll_conn_get_accepted_protocol(conn));
	
    if (sProtocol.compare("rtt") == 0)
    {
        int on = 1;
        setsockopt(mSocket, IPPROTO_TCP, TCP_NODELAY, (void *) &on, sizeof (on));
        thread rttThread(roundTripTime, ctx, conn);
        rttThread.detach();
    }

    if (sProtocol.compare("download") == 0)
    {
        thread downloadThread(download, ctx, conn);
        downloadThread.detach();
    }
    
    if (sProtocol.compare("upload") == 0)
    {
        thread uploadThread(upload, ctx, conn);
        uploadThread.detach();
    }
    
    return 1;
}

void CWsHandler::websocket_message_handler(noPollCtx *ctx, noPollConn *conn, noPollMsg *msg, noPollPtr user_data)
{   
    ///check for PONG Message 
    if (nopoll_msg_opcode(msg) == NOPOLL_PONG_FRAME && rttRunning)
    {    
        rttPongAllegedReceiveTime = CTool::get_timestamp();
        
        string rttPayloadReceivedString = (const char*)nopoll_msg_get_payload(msg);
 
        size_t rttPayloadReceivedPos;
        if ((rttPayloadReceivedPos = rttPayloadReceivedString.find(rttPayloadDelimiter)) != string::npos)
        {
            TRC_DEBUG("WebSocket handler: rtt PONG received");
            
            string rttPayloadReceivedTimestamp = rttPayloadReceivedString.substr(0, rttPayloadReceivedPos);
            
            if (rttPayloadReceivedTimestamp.compare(rttPayloadTimestamp) == 0)
            {
                rttPongReceiveTime = rttPongAllegedReceiveTime;
                return;
            }
            else
            {
                TRC_DEBUG("WebSocket handler: rtt PING/PONG mismatch, discarding PONG");
            }
        }
 
        return;
    }
    
    if (rttRunning && !rttStart)
    {
        string error;
        Json rttParameters = Json::parse((const char*)nopoll_msg_get_payload(msg), error);
        
        if (rttParameters["cmd"].string_value().compare("rttStart") == 0)
        {
            if (rttParameters["rttRequests"].int_value())        rttRequests         = rttParameters["rttRequests"].int_value()+1;
            if (rttParameters["rttRequestTimeout"].int_value())  rttRequestTimeout   = rttParameters["rttRequestTimeout"].int_value();
            if (rttParameters["rttRequestWait"].int_value())     rttRequestWait      = rttParameters["rttRequestWait"].int_value();
            if (rttParameters["rttTimeout"].int_value())         rttTimeout          = rttParameters["rttTimeout"].int_value();
            if (rttParameters["rttPayloadSize"].int_value())     
            {
                if (rttParameters["rttPayloadSize"].int_value() > 6)
                {
                    rttPayloadSize = rttParameters["rttPayloadSize"].int_value();
                }
            }
        
            rttStart = true;
        }
    }
    
    if (uploadRunning)
    {
        uploadBytesReceived         += nopoll_msg_get_payload_size(msg);

        if (nopoll_msg_opcode(msg) != 0)
        {
            uploadHeaderReceived++;
        }
        
        return;
    }
    
    if (!uploadRunning) 
    {
        TRC_DEBUG("WebSocket handler: Message received: ");
        TRC_DEBUG((const char*)nopoll_msg_get_payload(msg));
        
        return;
    }
}


void CWsHandler::websocket_close_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: close");
    
    rttRunning      = false;
    downloadRunning = false;
    uploadRunning   = false;
    
    nopoll_conn_flush_writes(conn, 2000, 0);
    
    nopoll_loop_stop(ctx);
}


nopoll_bool CWsHandler::websocket_post_ssl_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr SSL_CTX, noPollPtr SSL, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: TLS handshake completed");
    
    return nopoll_true;
}


void CWsHandler::websocket_timeout_handler(noPollCtx *ctx, noPollConn *conn)
{
    TRC_ERR("WebSocket handler: Timeout reached");
    
    TRC_DEBUG("WebSocket handler: stopped");
    
    nopoll_conn_close(conn);
    
    if (showShutdown) TRC_DEBUG("Socket: Connection Shutdown for Client IP: " + sClientIp);
}


void CWsHandler::nopoll_logging_handler(noPollCtx * ctx, noPollDebugLevel level, const char * log_msg, noPollPtr user_data)
{
    if (level == NOPOLL_LEVEL_DEBUG && ::DEBUG_NOPOLL)
    {
        TRC_DEBUG("NoPoll: " + string(log_msg));
    }
    
    if (level == NOPOLL_LEVEL_WARNING && DEBUG_NOPOLL_ERROR)
    {
        TRC_ERR("NoPoll: " + string(log_msg));
    }
        
    if (level == NOPOLL_LEVEL_CRITICAL && DEBUG_NOPOLL_CRITICAL)
    {
        TRC_CRIT("NoPoll: " + string(log_msg));
        
        nopoll_loop_stop(ctx);
    }
    
    return;
}

int CWsHandler::roundTripTime(noPollCtx *ctx, noPollConn *conn)
{
    TRC_DEBUG("WebSocket handler: Round Trip Time");
    
    rttRunning = true;
    
    unsigned long long startTime;
    unsigned long long runningTime;
    
    startTime = CTool::get_timestamp();
    
    do
    {
        runningTime = CTool::get_timestamp() - startTime;
        
        if (!rttStart)
        {
            usleep(100);
            if (((runningTime / 1000) > rttTimeout) || !rttRunning) break;
            else continue;
        }
        
        if (showRttStart)
        {
            showRttStart = false;
            TRC_DEBUG("WebSocket handler: Round Trip Time start");
        }
        
        char randomData[rttPayloadSize-6];
        CTool::randomData(randomData, rttPayloadSize-6);
        
        rttPayloadTimestamp = to_string(CTool::get_timestamp_usec());
        
        rttPayload = rttPayloadTimestamp + ";" + randomData;

        if (nopoll_conn_send_frame (conn, nopoll_true, nopoll_false, NOPOLL_PING_FRAME, rttPayloadSize, (noPollPtr)rttPayload.c_str(), 0) == -1)
        {
            //PING send failed
            rttErrors++;
            setRoundTripTimeKPIs();
            sendRoundTripTimeResponse(ctx, conn);
            rttPingSendTime     = 0;
            rttPongReceiveTime  = 0;
            rttPayloadTimestamp = "0";
            usleep(rttRequestWait * 1000); 
            continue;
        }
        
        rttPingSendTime = CTool::get_timestamp();
        
        rttRequestsSend++;
        
        TRC_DEBUG("WebSocket handler: rtt PING send");
        
        while (rttPongReceiveTime == 0)
        {
            if (((CTool::get_timestamp() - rttPingSendTime) / 1000) > rttRequestTimeout)
            {
                TRC_DEBUG("WebSocket handler: Round Trip Time Request Timeout");
                if (rttRequestsSend != 1)
                {
                    rttMissing++;
                    setRoundTripTimeKPIs();
                    sendRoundTripTimeResponse(ctx, conn);
                }

                break;
            }
            usleep(300);
        }

        if (((CTool::get_timestamp() - rttPingSendTime) / 1000) > rttRequestTimeout)
        {
            rttPingSendTime         = 0;
            rttPongReceiveTime      = 0;
            rttPayloadTimestamp     = "0";
            usleep(rttRequestWait * 2000);  //1000
            continue;
        }
        
        long long rtt = rttPongReceiveTime - rttPingSendTime;

        if (rttRequestsSend != 1)
        {
            rttVector.push_back(rtt);

            rttReplies++;
            setRoundTripTimeKPIs();
            sendRoundTripTimeResponse(ctx, conn);
        }
        
        rttPingSendTime        = 0;
        rttPongReceiveTime     = 0;
        
        usleep(rttRequestWait * 1000);
        
    } while (((runningTime  / 1000) < rttTimeout) && rttRunning && rttRequestsSend < rttRequests);

    nopoll_conn_flush_writes(conn, 2000, 0);
    
    if (rttRequestsSend == rttRequests)
    {
        usleep(6000 * 1000);
        nopoll_conn_close(conn);
        return 1;
    }
    
    if (rttRunning && ((runningTime / 1000) >= rttTimeout)) 
    {
        showShutdown = true;
        showStopped  = false;
        websocket_timeout_handler(ctx, conn);
    }
    
    return 1;
}


void CWsHandler::setRoundTripTimeKPIs()
{
    long long rttSum    = 0;
    long long rttSumSq  = 0;

    for (long long rtt : rttVector)
    {
        //TRC_DEBUG("rtt: " + to_string(rtt));
        rttSum      += rtt;
        rttSumSq    += rtt * rtt;
    }
    
    if (rttSum != 0 && rttReplies > 0)
    {
        rttAvg = (double)rttSum / (double)rttReplies;
        
        if ((rttVector.back() < rttMin) || rttMin == 0)
        {
            rttMin = rttVector.back();
        }
        if (rttVector.back() > rttMax)
        {
            rttMax = rttVector.back();
        }

        //calc median
        vector<long long> rttMedianVector = rttVector;
        sort(rttMedianVector.begin(), rttMedianVector.end());
        size_t length = rttMedianVector.size();
        
        if (length%2 == 0)
        {
            rttMed  = (rttMedianVector[length/2-1] + rttMedianVector[length/2])/2;
        }
        else
        {
            rttMed = rttMedianVector[length/2];
        }
        
        //calc population standard deviation (analog to freeBSD 4.3)
        /*
         * note: currently, the linux iputils package suffers from a precision loss 
         * which leads to wrong population standard deviation results. A github issue 
         * and pull request were issued
        */
        double variancePopulation = (double)rttSumSq / (double)rttReplies - rttAvg * rttAvg;
        rttStdDevPop = sqrt(variancePopulation);
    }
         
    rttMissing      = rttRequestsSend - 1 - rttReplies - rttErrors;
    rttPacketsize   = rttPayloadSize;
}


void CWsHandler::sendRoundTripTimeResponse(noPollCtx *ctx, noPollConn *conn)
{
    //only send the first, then every second and the last RTT Report
    if (((rttRequestsSend-1)%2 == 1) || (rttRequestsSend == rttRequests))
    {
        Json rttReport = Json::object{
            {"cmd",         "rttReport"},
            {"avg",         to_string_precision(rttAvg / 1000, 3)},
            {"med",         rttMed / 1000},  
            {"min",         rttMin / 1000},
            {"max",         rttMax / 1000},
            {"req",         rttRequestsSend - 1},
            {"rep",         rttReplies},
            {"err",         rttErrors},
            {"mis",         rttMissing},
            {"pSz",         rttPacketsize},
            {"std_dev_pop", to_string_precision(rttStdDevPop / 1000, 3)},
            {"srv",         hostname},
        };

        nopoll_conn_send_text(conn, rttReport.dump().c_str(), rttReport.dump().length());

        TRC_DEBUG("WebSocket handler: rtt report send");
    }
}


int CWsHandler::download(noPollCtx *ctx, noPollConn *conn)
{
    TRC_DEBUG("WebSocket handler: download, frame size: " + to_string(downloadFrameSize));

    downloadRunning = true;
    
    vector<char>randomDataValues;
    randomDataValues.clear();
    CTool::randomData(randomDataValues, downloadRandomDataSize);

    unsigned long long index = 0;
    
    unsigned long long startTime    = 0;
    unsigned long long endTime      = CTool::get_timestamp();
    unsigned long long runningTime  = 0;
    unsigned long long currentTime  = endTime / 100000;
    
    int nResponse = 0;
    
    usleep(500000);
    
    startTime = CTool::get_timestamp_sec();
    
    do
    {
        vector<char> payload(&randomDataValues[index], &randomDataValues[index+downloadFrameSize]);
        index += downloadFrameSize;
        
        if (index > randomDataValues.size())
        {
            index = index - randomDataValues.size();
            
            vector<char> payload(&randomDataValues[index], &randomDataValues[index+downloadFrameSize]);
            index += downloadFrameSize;
        }
        
        nResponse = nopoll_conn_send_binary(conn, payload.data(), downloadFrameSize);
                
        if (nResponse <= 0)
        {
            TRC_ERR("WebSocket handler: download send: " + to_string(nResponse));
            break;
        }

        if ((endTime - (currentTime * 100000)) > 500000)
        {
            currentTime = formatCurrentTime(endTime, currentTime);
            
            //printTcpMetrics();
        }
        
        endTime = CTool::get_timestamp();
        runningTime = CTool::get_timestamp_sec() - startTime;
             
    } while (nResponse > 0 && runningTime < webSocketTimeout && downloadRunning);
    
    nopoll_conn_flush_writes(conn, 2000, 0);
    
    if (downloadRunning && runningTime >= webSocketTimeout) 
    {
        showShutdown = true;
        showStopped  = false;
        websocket_timeout_handler(ctx, conn);
    }
    
    return 1;
}


int CWsHandler::upload(noPollCtx *ctx, noPollConn *conn)
{
    TRC_DEBUG("WebSocket handler: upload");
    
    uploadRunning = true;
    
    unsigned long long startTime    = 0;
    unsigned long long endTime      = CTool::get_timestamp();
    unsigned long long runningTime  = 0;
    unsigned long long currentTime  = endTime / 100000;
    
    currentTime = formatCurrentTime(endTime, currentTime);

    startTime   = CTool::get_timestamp_sec();
     
    do
    {
        if ((endTime - (currentTime * 100000)) > 500000)
        {
            string uploadBytesReceivedString = to_string(uploadBytesReceived);
            uploadBytesReceived = 0;
            string uploadHeaderReceivedString = to_string(uploadHeaderReceived);
            uploadHeaderReceived = 0;
            string currentTimeString = to_string(currentTime);
            currentTime = formatCurrentTime(endTime, currentTime);
            
            Json rtReport = Json::object{
                {"cmd", "ulReport"},
                {"time", currentTimeString},
                {"bRcv", uploadBytesReceivedString},
                {"hRcv", uploadHeaderReceivedString},
            };
            
            nopoll_conn_send_text(conn, rtReport.dump().c_str(), rtReport.dump().length());
            
            //printTcpMetrics();
        }
        
        usleep(1000);
        
        endTime = CTool::get_timestamp();
        runningTime = CTool::get_timestamp_sec() - startTime;
        
    } while (runningTime < webSocketTimeout && uploadRunning);
    
    nopoll_conn_flush_writes(conn, 2000, 0);
    
    if (uploadRunning && runningTime >= webSocketTimeout) 
    {
        showShutdown = false;
        showStopped  = false;
        websocket_timeout_handler(ctx, conn);
    }
    
    return 1;
}


unsigned long long CWsHandler::formatCurrentTime(unsigned long long endTime, unsigned long long currentTime)
{
    currentTime = endTime / 100000;

    if ((currentTime % 10) < 5)
    {
        currentTime -= currentTime % 10;
    }
    else
    {
        currentTime = (currentTime - (currentTime % 10)) + 5;
    }
    
    return currentTime;
}


bool CWsHandler::checkAuth(string authToken, string authTimestamp)
{
	/*
    long long currentTimestamp = CTool::get_timestamp();
    long long requestedTimestamp = CTool::toLL(authTimestamp);
    
    if ((currentTimestamp - requestedTimestamp) > 120000000)
    {
        TRC_WARN("WebSocket handler: authentication failed: token expired: " + to_string((currentTimestamp - requestedTimestamp)));
        return false;
    }
    
    if ((currentTimestamp - requestedTimestamp) < 0)
    {
        TRC_WARN("WebSocket handler: authentication failed: token expired: " + to_string((currentTimestamp - requestedTimestamp)));
        return false;
    }
    
    string authTokenComputed = sha1(authTimestamp + secret);
    
    TRC_DEBUG("WebSocket handler: computed token:       \"" + authTokenComputed + "\"");
    
    if (authToken.compare(authTokenComputed) != 0)
    {
        TRC_WARN("WebSocket handler: authentication failed: token mismatch");
        return false;
    }
	*/
    
    TRC_DEBUG("WebSocket handler: authentication successful");
    return true;
}

void CWsHandler::printTcpMetrics()
{
    struct tcp_info tcp_info;
    socklen_t tcp_info_length = sizeof(tcp_info);

    if (getsockopt(mSocket, SOL_TCP, TCP_INFO, &tcp_info, &tcp_info_length) != 0)
    {
        TRC_ERR("WebSocket handler: printTcpMetrics: getsockopt failed");
    }
    else
    {
        std::stringstream ss;
        ss << std::this_thread::get_id();
        
        TRC_DEBUG("############ TCP Metrics:\r\n"
                "   tcpi_pmtu:              " + to_string(tcp_info.tcpi_pmtu) + "\r\n"
                "   tcpi_rcv_ssthresh:      " + to_string(tcp_info.tcpi_rcv_ssthresh) + "\r\n"
                "   tcpi_rtt:               " + to_string(tcp_info.tcpi_rtt) + "\r\n"
                "   tcpi_rttvar:            " + to_string(tcp_info.tcpi_rttvar) + "\r\n"
                "   tcpi_snd_ssthresh:      " + to_string(tcp_info.tcpi_snd_ssthresh) + "\r\n"
                "   tcpi_snd_cwnd:          " + to_string(tcp_info.tcpi_snd_cwnd) + "\r\n"
                "   tcpi_rcv_rtt:           " + to_string(tcp_info.tcpi_rcv_rtt) + "\r\n"
                "   tcpi_rcv_space:         " + to_string(tcp_info.tcpi_rcv_space) + "\r\n"
                "   tcpi_total_retrans:     " + to_string(tcp_info.tcpi_total_retrans) + "\r\n"
                "   tcpi_options:           " + to_string(tcp_info.tcpi_options) + "\r\n"
                "   tcpi_snd_mss:           " + to_string(tcp_info.tcpi_snd_mss) + "\r\n"
                "   tcpi_advmss:            " + to_string(tcp_info.tcpi_advmss) + "\r\n"
                "   tcpi_rcv_mss:           " + to_string(tcp_info.tcpi_rcv_mss) + ""
                );
    }
}

string CWsHandler::to_string_precision(double value, const int precision)
{
    std::ostringstream out;
    out << std::fixed << std::setprecision(precision) << value;
    return out.str();
}
