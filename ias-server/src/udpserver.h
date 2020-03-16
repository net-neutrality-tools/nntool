/*!
    \file udpserver.h
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

#ifndef UDPSERVER_H
#define UDPSERVER_H


#include "header.h"


class CUdpListener : public CBasisThread
{
	private:
		int mPort;
		int mIpType;
		string mBindIp;
		
		std::unique_ptr<CConnection> mConnection;

	public:
		CUdpListener();
		
		virtual ~CUdpListener();

		CUdpListener(int nPort, int nIpType, string sBindIp);
		
		int run();
};

#endif
