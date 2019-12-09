/*!
    \file basisthread.h
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

#ifndef BASISTHREAD_H
#define BASISTHREAD_H

#include "header.h"
#include "tool.h"
#include "typedef.h"

using namespace std;

void* threadStart(void* pParam);

class CBasisThread
{
	public:
		CBasisThread();
		virtual ~CBasisThread();

		virtual int		initInstance();
		virtual int		exitInstance();
		virtual int		run();
		
		int			waitForEnd();
		int			detachThread();
		unsigned long		getExitCode();
		int			stopThread();
		int 			createThread( );
		bool			isRunning();

		pthread_t		m_hThread;
		void *params;
		
	protected:

		friend void*		threadStart(void* pParam);

		bool			m_fStop;
		unsigned long		m_dwExitCode;

};




#endif // CBASISTHREAD_H
