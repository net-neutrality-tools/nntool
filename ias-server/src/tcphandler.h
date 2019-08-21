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
 *      \date Last update: 2019-08-20
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef TCPHANDLER_H
#define TCPHANDLER_H


#include "header.h"


using namespace json11;


class CTcpHandler
{
    public:
        CTcpHandler();

        virtual ~CTcpHandler();

        CTcpHandler(int nSocket, string nClientIp, bool nTlsSocket, sockaddr_in6 *pClient);
        
        int handle_tcp();

        static int websocket_open_handler               (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static int websocket_ready_handler              (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static int websocket_reject_handler             (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static int handle_http                          (Json::object http_header_values, noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static void websocket_message_handler           (noPollCtx *ctx, noPollConn *conn, noPollMsg *msg, noPollPtr user_data);
        static void websocket_close_handler             (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static nopoll_bool websocket_post_ssl_handler   (noPollCtx *ctx, noPollConn *conn, noPollPtr SSL_CTX, noPollPtr SSL, noPollPtr user_data);
        static void tcp_timeout_handler                 (noPollCtx *ctx, noPollConn *conn);
        static void nopoll_logging_handler              (noPollCtx * ctx, noPollDebugLevel level, const char * log_msg, noPollPtr user_data);
    
        static int roundTripTime                        (noPollCtx *ctx, noPollConn *conn);
        static void setRoundTripTimeKPIs                ();
        static void sendRoundTripTimeResponse           (noPollCtx *ctx, noPollConn *conn);
        static int download                             (noPollCtx *ctx, noPollConn *conn);
        static int upload                               (noPollCtx *ctx, noPollConn *conn);
        static unsigned long long formatCurrentTime     (unsigned long long endTime, unsigned long long currentTime);
        static bool checkAuth                           (string authToken, string authTimestamp, string handler);
        static void printTcpMetrics                     ();
};

#endif