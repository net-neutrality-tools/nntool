/*!
    \file tcpserver.h
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-03-16

    Copyright (C) 2016 - 2020 zafaco GmbH

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


#ifndef TCPSERVER_H
#define TCPSERVER_H


#include "header.h"


using namespace std;


/*!
\class CTcpServer
\brief Thread CTcpServer
*/
class CTcpServer : public CBasisThread
{
	private:
        int mTargetPort;
        int mTargetPortTraceroute;
        bool mTlsSocket;
        int mIpType;
        string mIp;

        int mSock;
        
        std::unique_ptr<CConnection> mConnection;
	
	public:
		CTcpServer();
		
		virtual ~CTcpServer();

        CTcpServer(int nTargetPort, int nTargetPortTraceroute, bool nTlsSocket) : CTcpServer(nTargetPort, nTargetPortTraceroute, 0 , "::", nTlsSocket) {};

        CTcpServer(int nTargetPort, int nTargetPortTracerouteint, int nIpType, string sIp, bool nTlsSocket);

        int run() override;
};

#endif