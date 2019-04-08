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

#ifndef WSHANDLER_H
#define WSHANDLER_H


#include "header.h"


class CWsHandler
{
    public:
        CWsHandler();

        virtual ~CWsHandler();

        CWsHandler(sockaddr_in6 *pClient, int nSocket, string nClientIp, bool nTlsSocket);
        
        int handle_websocket();

        static int websocket_open_handler               (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static int websocket_ready_handler              (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static void websocket_message_handler           (noPollCtx *ctx, noPollConn *conn, noPollMsg *msg, noPollPtr user_data);
        static void websocket_close_handler             (noPollCtx *ctx, noPollConn *conn, noPollPtr user_data);
        static nopoll_bool websocket_post_ssl_handler   (noPollCtx *ctx, noPollConn *conn, noPollPtr SSL_CTX, noPollPtr SSL, noPollPtr user_data);
        static void websocket_timeout_handler           (noPollCtx *ctx, noPollConn *conn);
        static void nopoll_logging_handler              (noPollCtx * ctx, noPollDebugLevel level, const char * log_msg, noPollPtr user_data);
    
        static int roundTripTime                        (noPollCtx *ctx, noPollConn *conn);
        static void setRoundTripTimeKPIs                ();
        static void sendRoundTripTimeResponse           (noPollCtx *ctx, noPollConn *conn);
        static int download                             (noPollCtx *ctx, noPollConn *conn);
        static int upload                               (noPollCtx *ctx, noPollConn *conn);
        static unsigned long long formatCurrentTime     (unsigned long long endTime, unsigned long long currentTime);
        static bool checkAuth                           (string authToken, string authTimestamp);
        static void setMySQLParameters                  ();
        static void mysqlClientMonitoring               (string protocol);
        static void printTcpMetrics                     ();
        static string to_string_precision(double value, const int precision);
};

#endif