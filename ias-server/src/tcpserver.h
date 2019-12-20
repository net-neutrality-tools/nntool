/*!
    \file tcpserver.h
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
        
        int mSock;
        
        std::unique_ptr<CConnection> mConnection;
	
	public:
		CTcpServer();
		
		virtual ~CTcpServer();

        CTcpServer(int nTargetPort, int nTargetPortTraceroute, bool nTlsSocket);

        int run() override;
};

#endif