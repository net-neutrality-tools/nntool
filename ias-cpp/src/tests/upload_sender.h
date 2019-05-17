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
 *      \date Last update: 2019-04-28
 *      \note Copyright (c) 2019 zafaco GmbH. All rights reserved.
 */

#ifndef UPLOAD_SENDER_H
#define UPLOAD_SENDER_H

#include "../header.h"

/*!
\class CUploadSender
\brief Class with CUploadSender Functions
- Declarations for the Measurementsystem
*/
class CUploadSender : public CBasisThread
{	
	private:
		int mSocket;
		int mResponse;
		
		unsigned long long nPointer;
		vector<char> payload;
		
	public:
		//! Standard Constructor
		CUploadSender();

		//! Standard Destructor
		virtual ~CUploadSender();
		
		CUploadSender(int nSocket);
		
		int run();
};

#endif 