/*!
    \file tcphandler.cpp
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


#include "tcphandler.h"


using namespace std;




int                 mSocket;
sockaddr_in6        *mClient;
bool                mTlsSocket;
        
vector<string>      allowedProtocols;
unsigned long long  tcpTimeout;
string              sClientIp;

string              authentication_secret;

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
long long           uploadBytesReceivedLast;
long long           uploadHeaderReceived;

bool                showShutdown;
bool                showStopped;
bool                showRttStart;

string              certDir;

bool                connectionIsValidWebSocket;
bool                connectionIsValidHttp;

bool                overloadSignaled;




CTcpHandler::CTcpHandler()
{
}


CTcpHandler::~CTcpHandler()
{
}


CTcpHandler::CTcpHandler(int nSocket, string nClientIp, bool nTlsSocket, sockaddr_in6 *pClient)
{
    mClient                     = pClient;
    mSocket                     = nSocket;
    mTlsSocket                  = nTlsSocket;
    
    if (::CONFIG["tcp_timeout"].int_value() != 0)
    {
        tcpTimeout = ::CONFIG["tcp_timeout"].int_value();
    }
    else
    {
        tcpTimeout              = 25;
    }

    allowedProtocols.push_back("rtt");
    allowedProtocols.push_back("download");
    allowedProtocols.push_back("upload");
    
    sClientIp                   = nClientIp;
    
    if (::CONFIG["authentication"]["secret"].string_value().compare("") != 0)
    {
        authentication_secret   = ::CONFIG["authentication"]["secret"].string_value();
    }
    else
    {
        authentication_secret   = "default_authentication_secret";
    }

    if (::CONFIG["hostname"].string_value().compare("") != 0)
    {
        hostname = ::CONFIG["hostname"].string_value();
    }
    else
    {
        hostname                = "default_hostname";
    }

    rttRunning                  = false;
    downloadRunning             = false;
    uploadRunning               = false;
    
    downloadFrameSize           = 32768;
    
    if (::CONFIG["random_data_size"].int_value() != 0)
    {
        downloadRandomDataSize = ::CONFIG["random_data_size"].int_value();
    }
    else
    {
        downloadRandomDataSize  = 1123457;
    }

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
    uploadBytesReceivedLast     = 0;
    uploadHeaderReceived        = 0;
    
    showShutdown                = false;
    showStopped                 = true;
    showRttStart                = true;
    
    certDir                     = "/var/opt/ias-server/certs/";

    connectionIsValidWebSocket  = false;
    connectionIsValidHttp       = false;

    overloadSignaled            = false;
}


int CTcpHandler::handle_tcp()
{
    TRC_DEBUG("TCP handler: started");
        
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
    nopoll_ctx_set_on_reject(ctx, websocket_reject_handler, NULL);
    nopoll_ctx_set_on_msg(ctx, websocket_message_handler, NULL);
  
    //wait loop for event notification 
    nopoll_loop_wait(ctx, 0);

    //cleanup
    nopoll_ctx_unref(ctx);
    nopoll_cleanup_library();
    
    if (showStopped)
    {
        TRC_DEBUG("TCP handler: stopped");
    }
    
    return 0;
}


int CTcpHandler::websocket_open_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
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
    bool overloadCheck = false;

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
                    int i = 0;
                    acceptedProtocol = requestestedProtocols.at(i);
                    if (requestestedProtocols.at(i+1).compare("overload") == 0)
                    {
                        i++;
                        overloadCheck = true;
                    }
                    string authToken        = requestestedProtocols.at(i+1);
                    string authTimestamp    = requestestedProtocols.at(i+2);
                    TRC_DEBUG("WebSocket handler: requested protocol:               \"" + requestedProtocol + "\"");
                    TRC_DEBUG("WebSocket handler: requested token:                  \"" + authToken + "\"");
                    TRC_DEBUG("WebSocket handler: requested timestamp:              \"" + authTimestamp + "\"");
					
					if (acceptedProtocol.compare("download") == 0 && ( (i == 0 && requestestedProtocols.size() > 3) || (i == 1 && requestestedProtocols.size() > 4) ))
					{
						downloadFrameSize = atoi(requestestedProtocols.at(i+3).c_str());
						TRC_DEBUG("WebSocket handler: requested download frame size:      \"" + to_string(downloadFrameSize) + "\"");
					}
                
                    protocolAllowed = checkAuth(authToken, authTimestamp, "WebSocket");
                    
                    break;
                }
            }
        }
    }
    
    if (protocolAllowed)
    {
        connectionIsValidWebSocket = true;
        if (overloadCheck && OVERLOADED)
        {
            nopoll_conn_set_accepted_protocol(conn, "overload");
            TRC_WARN("WebSocket handler: peer overloaded");

            overloadSignaled = true;
        }
        else
        {
            nopoll_conn_set_accepted_protocol(conn, acceptedProtocol.c_str());
            TRC_DEBUG("WebSocket handler: requested protocol: \"" + acceptedProtocol + "\" is allowed");
        }
    }
    else if (!protocolAllowed)
    {
        TRC_ERR("WebSocket handler: requested protocol: \"" + sProtocol + "\" is not allowed or authorization failed");

        char response[] = HTTP_FORBIDDEN;
        int responseSize = strlen(response);

        nopoll_conn_default_send(conn, response, responseSize);
        
        return 0;
    }

    return 1;
}


int CTcpHandler::websocket_ready_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: ready for IP: " + sClientIp + " on Port: " + string(nopoll_conn_port(conn)));
    
    string sProtocol = string(nopoll_conn_get_accepted_protocol(conn));

    if (overloadSignaled)
    {
        return 0;
    }
	
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


int CTcpHandler::websocket_reject_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    //check if valid http connection was received

    //get http header information
    struct http_header *http_header_values_struct;
    http_header_values_struct = nopoll_conn_get_http_header(conn);

    Json::object http_header_values;
    while (http_header_values_struct)
    {
        struct http_header *node = http_header_values_struct;
        http_header_values[node->key] = node->value;
        http_header_values_struct = http_header_values_struct->next;
    }

    if ((http_header_values["request_url"].string_value().compare(HTTP_DATA) == 0 
        && http_header_values[HTTP_COOKIE].string_value().compare("") != 0)
     || ( http_header_values["http_method"].string_value().compare("GET") == 0
        && http_header_values["http_method"].string_value().compare("POST") == 0)
     )
    {
        TRC_INFO("WebSocket handler: valid HTTP " + http_header_values["http_method"].string_value() + " request received, falling back to HTTP");

        return handle_http(http_header_values, ctx, conn, user_data);
    }
    else
    {
        TRC_ERR("WebSocket handler: invalid HTTP " + http_header_values["http_method"].string_value() + " request received, closing connection");

        char response[] = HTTP_BAD_REQUEST;
        int responseSize = strlen(response);

        nopoll_conn_default_send(conn, response, responseSize);
        nopoll_loop_stop(ctx);
    }

    return 0;
}

int CTcpHandler::handle_http(Json::object http_header_values, noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    string cookie = http_header_values[HTTP_COOKIE].string_value();

    CTool::replaceStringInPlace(cookie, " ", "");
    
    vector<string> cookies;
    string delimiter = ";";
    CTool::tokenize(cookie, cookies, delimiter);

    bool auth = false;
    bool overloadCheck = false;

    if (cookies.size() > 1)
    {
        delimiter               = "=";
        string authToken        = "";
        string authTimestamp    = "";

        for (vector<string>::iterator itCookies = cookies.begin(); itCookies != cookies.end(); ++itCookies)
        {
            cookie = *itCookies;
            vector<string> cookieVector;
            CTool::tokenize(cookie, cookieVector, delimiter);
            
            if (cookieVector.at(0).compare("overload") == 0)
            {
                overloadCheck = true;
            }
            if (cookieVector.at(0).compare("tk") == 0)
            {
                authToken = cookieVector.at(1);
            }
            if (cookieVector.at(0).compare("ts") == 0)
            {
                authTimestamp = cookieVector.at(1);
            }
        }

        TRC_DEBUG("HTTP handler: requested protocol:               \"HTTP " + http_header_values["http_method"].string_value() + "\"");
        TRC_DEBUG("HTTP handler: requested token:                  \"" + authToken + "\"");
        TRC_DEBUG("HTTP handler: requested timestamp:              \"" + authTimestamp + "\"");

        auth = checkAuth(authToken, authTimestamp, "HTTP");
        
        if (auth)
        {
            connectionIsValidHttp = true;
            nopoll_conn_set_http_on(conn, true);

            if (overloadCheck && OVERLOADED)
            {
                TRC_WARN("HTTP handler: peer overloaded");

                char response[] = HTTP_BANDWIDTH_LIMIT_EXCEEDED;
                int responseSize = strlen(response);

                nopoll_conn_default_send(conn, response, responseSize);

                overloadSignaled = true;

                nopoll_conn_close(conn);
                
                TRC_DEBUG("Socket: Connection Shutdown for Client IP: " + sClientIp);

                return 0;
            }

            TRC_DEBUG("HTTP handler: requested protocol: \"HTTP " + http_header_values["http_method"].string_value() + "\" is allowed");

            TRC_DEBUG("HTTP handler: ready for IP: " + sClientIp + " on Port: " + string(nopoll_conn_port(conn)));

            string response = "";

            if (http_header_values["http_method"].string_value().compare("GET") == 0)
            {
                response += "HTTP/1.1 200 OK\r\n";
                response += "Accept-Ranges: bytes\r\n";
                if (http_header_values["Origin"].string_value().compare("") != 0)
                {
                    response += "Access-Control-Allow-Origin: " + http_header_values["Origin"].string_value() + "\r\n";
                }
                response += "Access-Control-Allow-Credentials: true\r\n";
                response += "Content-Language: en\r\n";
                response += "Content-Type: application/octet-stream\r\n";
                response += "Cache-Control: max-age=0, no-cache, no-store\r\n";
                response += "Pragma: no-cache\r\n";
                response += "X-Rack-Cache: miss\r\n";
                response += "Connection: keep-alive\r\n\r\n";

                nopoll_conn_default_send(conn, const_cast<char*>(response.c_str()), response.size());

                thread downloadThread(download, ctx, conn);
                downloadThread.detach();
            }
            else if (http_header_values["http_method"].string_value().compare("POST") == 0)
            {
                response += "HTTP/1.1 100 Continue\r\n";
                response += "Content-Length: 1024000000\r\n";
                response += "Content-Type: application/octet-stream\r\n";
                response += "Connection: keep-alive\r\n\r\n";

                nopoll_conn_default_send(conn, const_cast<char*>(response.c_str()), response.size());

                thread uploadThread(upload, ctx, conn);
                uploadThread.detach();
            }
        }
        else if (!auth)
        {
            TRC_ERR("HTTP handler: requested protocol: \"HTTP " + http_header_values["http_method"].string_value() + "\" is not allowed or authorization failed");

            char response[] = HTTP_FORBIDDEN;
            int responseSize = strlen(response);

            nopoll_conn_default_send(conn, response, responseSize);
            
            return 0;
        }

    }
    else
    {
        return 0;
    }

    return 1;
}

void CTcpHandler::websocket_message_handler(noPollCtx *ctx, noPollConn *conn, noPollMsg *msg, noPollPtr user_data)
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

    //TRC_DEBUG("SIZE RECEIVED: " + to_string(nopoll_msg_get_payload_size(msg)) + " OPCODE: " + to_string(nopoll_msg_opcode(msg)));
    
    if (uploadRunning)
    {
        uploadBytesReceived += nopoll_msg_get_payload_size(msg);

        if (nopoll_msg_opcode(msg) != 0)
        {
            uploadHeaderReceived++;
        }
        
        return;
    }
    
    if (!uploadRunning) 
    {
        TRC_DEBUG("WebSocket handler: Message received");
        //TRC_DEBUG((const char*)nopoll_msg_get_payload(msg));
        
        return;
    }
}


void CTcpHandler::websocket_close_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: close");
    
    rttRunning      = false;
    downloadRunning = false;
    uploadRunning   = false;
    
    nopoll_conn_flush_writes(conn, 2000, 0);
    
    nopoll_loop_stop(ctx);
}


nopoll_bool CTcpHandler::websocket_post_ssl_handler(noPollCtx *ctx, noPollConn *conn, noPollPtr SSL_CTX, noPollPtr SSL, noPollPtr user_data)
{
    TRC_DEBUG("WebSocket handler: TLS handshake completed");
    
    return nopoll_true;
}


void CTcpHandler::tcp_timeout_handler(noPollCtx *ctx, noPollConn *conn)
{
    TRC_ERR("TCP handler: Timeout reached");
    
    TRC_DEBUG("TCP handler: stopped");
    
    //nopoll_conn_close(conn);
    nopoll_loop_stop(ctx);

    if (showShutdown) TRC_DEBUG("Socket: Connection Shutdown for Client IP: " + sClientIp);
}


void CTcpHandler::nopoll_logging_handler(noPollCtx * ctx, noPollDebugLevel level, const char * log_msg, noPollPtr user_data)
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

int CTcpHandler::roundTripTime(noPollCtx *ctx, noPollConn *conn)
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
        tcp_timeout_handler(ctx, conn);
    }
    
    return 1;
}


void CTcpHandler::setRoundTripTimeKPIs()
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
        double variancePopulation = (double)rttSumSq / (double)rttReplies - rttAvg * rttAvg;
        rttStdDevPop = sqrt(variancePopulation);
    }
         
    rttMissing      = rttRequestsSend - 1 - rttReplies - rttErrors;
    rttPacketsize   = rttPayloadSize;
}


void CTcpHandler::sendRoundTripTimeResponse(noPollCtx *ctx, noPollConn *conn)
{
    Json::array jRtts;

    for (double rtt : rttVector)
    {
        Json jRtt = Json::object{
            {"rtt_ns", rtt},
        };
        jRtts.push_back(jRtt);
    }

    //only send the first, then every second and the last RTT Report
    if (((rttRequestsSend-1)%2 == 1) || (rttRequestsSend == rttRequests))
    {
        Json rttReport = Json::object{
            {"cmd",         "rttReport"},
            {"avg",         CTool::to_string_precision(rttAvg, 3)},
            {"med",         rttMed},  
            {"min",         rttMin},
            {"max",         rttMax},
            {"req",         rttRequestsSend - 1},
            {"rep",         rttReplies},
            {"err",         rttErrors},
            {"mis",         rttMissing},
            {"pSz",         rttPacketsize},
            {"std_dev_pop", CTool::to_string_precision(rttStdDevPop, 3)},
            {"rtts",        jRtts},
        };

        nopoll_conn_send_text(conn, rttReport.dump().c_str(), rttReport.dump().length());

        TRC_DEBUG("WebSocket handler: rtt report send");
    }
}


int CTcpHandler::download(noPollCtx *ctx, noPollConn *conn)
{
    if (connectionIsValidWebSocket)
    {
        TRC_DEBUG("TCP handler: download using WebSocket, frame size: " + to_string(downloadFrameSize));
    }
    else if (connectionIsValidHttp)
    {
        TRC_DEBUG("TCP handler: download using HTTP");
        downloadFrameSize = MAX_PACKET_SIZE;
    }
    
    downloadRunning = true;
    
    vector<char>randomDataValues;
    randomDataValues.clear();
    randomDataValues.reserve(downloadRandomDataSize);
    CTool::randomData(randomDataValues, downloadRandomDataSize);

    unsigned long long index = 0;

    unsigned long long startTime    = 0;
    unsigned long long runningTime  = 0;

    int nResponse = 0;

    usleep(500000);

    startTime = CTool::get_timestamp_sec();

    char *firstChar = randomDataValues.data();

    do
    {
        if (index + downloadFrameSize > randomDataValues.size()) {
            index += downloadFrameSize - randomDataValues.size();
        }

        if (connectionIsValidWebSocket)
        {
            nResponse = nopoll_conn_send_binary(conn, firstChar + index, downloadFrameSize);
        }
        else if (connectionIsValidHttp)
        {
            nResponse = nopoll_conn_default_send(conn, firstChar + index, downloadFrameSize);
        }

        index += downloadFrameSize;
                
        if (nResponse <= 0)
        {
            TRC_ERR("TCP handler: download send: " + to_string(nResponse));
            break;
        }

        runningTime = CTool::get_timestamp_sec() - startTime;
             
    } while (nResponse > 0 && runningTime < tcpTimeout && downloadRunning);
    
    if (connectionIsValidWebSocket)
    {
        nopoll_conn_flush_writes(conn, 2000, 0);
    }
    
    if (downloadRunning && runningTime >= tcpTimeout) 
    {
        showShutdown = true;
        showStopped  = false;
        tcp_timeout_handler(ctx, conn);
    }
    
    return 1;
}


int CTcpHandler::upload(noPollCtx *ctx, noPollConn *conn)
{
    TRC_DEBUG("TCP handler: upload");

    string sResponse;
    
    uploadRunning = true;
    
    unsigned long long startTime    = 0;
    unsigned long long endTime      = CTool::get_timestamp();
    unsigned long long runningTime  = 0;
    unsigned long long currentTime  = endTime / 100000;
    
    currentTime = formatCurrentTime(endTime, currentTime);

    startTime   = CTool::get_timestamp_sec();
     
    do
    {
        //TRC_DEBUG("upload running: " + to_string(uploadRunning));

        if ((endTime - (currentTime * 100000)) > 500000)
        {
            sResponse.clear();

            // store the amount of bytes received in the actual and the previous timeframe
            sResponse = ""+CTool::toString( uploadBytesReceived )+","+CTool::toString( uploadBytesReceivedLast )+","+CTool::toString( CTool::get_timestamp_sec() )+","+CTool::toString( currentTime )+","+CTool::toString( uploadHeaderReceived )+";";

            uploadBytesReceivedLast = uploadBytesReceived;
            uploadBytesReceived = 0;
            uploadHeaderReceived = 0;

            string currentTimeString = to_string(currentTime);
            currentTime = formatCurrentTime(endTime, currentTime);

            //TRC_DEBUG("Upload Response: " + sResponse);

            if (connectionIsValidWebSocket)
            {
                nopoll_conn_send_text(conn, sResponse.c_str(), sResponse.size());
            }
            else if (connectionIsValidHttp)
            {
                nopoll_conn_default_send(conn, const_cast<char*>(sResponse.c_str()), sResponse.size());
            }  
            
            //printTcpMetrics();
        }
        
        usleep(1000);
        
        endTime = CTool::get_timestamp();
        runningTime = CTool::get_timestamp_sec() - startTime;
        
    } while (runningTime < tcpTimeout && uploadRunning);
    
    if (connectionIsValidWebSocket)
    {
        nopoll_conn_flush_writes(conn, 2000, 0);
    }
    
    if (uploadRunning && runningTime >= tcpTimeout) 
    {
        showShutdown = false;
        showStopped  = false;
        tcp_timeout_handler(ctx, conn);
    }
    
    return 1;
}


unsigned long long CTcpHandler::formatCurrentTime(unsigned long long endTime, unsigned long long currentTime)
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


bool CTcpHandler::checkAuth(string authToken, string authTimestamp, string handler)
{
    if (!::CONFIG["authentication"]["enabled"].bool_value())
    {
        TRC_WARN(handler + " handler: authentication deactivated");
        return true;
    }

    long long currentTimestamp = CTool::get_timestamp();
    long long requestedTimestamp = CTool::toLL(authTimestamp);
    
    //check if authentication is older dan allow maximum
    if ( ((currentTimestamp - requestedTimestamp) > (AUTHENTICATION_MAX_AGE * 1e6)) || ((currentTimestamp - requestedTimestamp) < 0) )
    {
        TRC_WARN("WebSocket handler: authentication failed: token expired: " + to_string((currentTimestamp - requestedTimestamp)));
        return false;
    }

    string authTokenComputed = sha1(authTimestamp + authentication_secret);
    
    TRC_DEBUG("WebSocket handler: computed token:       \"" + authTokenComputed + "\"");
    
    if (authToken.compare(authTokenComputed) != 0)
    {
        TRC_WARN("WebSocket handler: authentication failed: token mismatch");
        return false;
    }
    
    TRC_DEBUG(handler + " handler: authentication successful");
    return true;
}

void CTcpHandler::printTcpMetrics()
{
    struct tcp_info tcp_info;
    socklen_t tcp_info_length = sizeof(tcp_info);

    if (getsockopt(mSocket, SOL_TCP, TCP_INFO, &tcp_info, &tcp_info_length) != 0)
    {
        TRC_ERR("TCP handler: printTcpMetrics: getsockopt failed");
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
