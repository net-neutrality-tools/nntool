/*!
    \file timer.cpp
    \author zafaco GmbH <info@zafaco.de>
    \date Last update: 2020-07-01

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

#include "timer.h"

#ifdef __ANDROID__
    #include "android_connector.h"
#endif

//! \brief
//!	Standard Destructor
CTimer::CTimer()
{
}

//! \brief
//!	Virtual Destructor
CTimer::~CTimer()
{
}

//! \brief
//!	Standard Destructor
CTimer::CTimer( int nInstances, CCallback *pCallback, unsigned long long nInitialCallbackDelay)
{
	mInstances 				= nInstances;
	mCallback				= pCallback;
	unreachableSignaled		= false;
	forbiddenSignaled 		= false;
	overloadSignaled 		= false;

	mInitialCallbackDelay	= nInitialCallbackDelay;

	::TIMER_ACTIVE 			= true;
	::TIMER_RUNNING 		= false;
	::TIMER_STOPPED 		= false;

	::TIMER_INDEX 			= 0;
	::TIMER_DURATION 		= 0;
	::MEASUREMENT_DURATION 	= 0;
}

//! \brief
//!    Run-Function
//! \return 0
int CTimer::run()
{
	//Log Message
	TRC_INFO( ("Starting Timer Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );
	
	//++++++INIT++++++
	unsigned long long time1 = 0;
	unsigned long long time2 = 0;
	unsigned long long time_diff = 0;
	
	map<int, int>::iterator AI;
	
	int sync_counter;
	
	//++++++MAIN++++++
	while( RUNNING && !TIMER_STOPPED && !m_fStop)
	{
		if (::UNREACHABLE && !unreachableSignaled)
		{
			unreachableSignaled = true;
			mCallback->callback("error", "no connection to measurement peer", 7, "no connection to measurement peer");

	        ::hasError 		= true;
			::TIMER_STOPPED = true;

        	break;
		}
		if (::FORBIDDEN && !forbiddenSignaled)
		{
			forbiddenSignaled = true;
			mCallback->callback("error", "authorization unsuccessful", 4, "authorization unsuccessful");

	        ::hasError 		= true;
			::TIMER_STOPPED = true;

        	break;
		}
		if (::OVERLOADED && !overloadSignaled)
		{
			overloadSignaled = true;
			mCallback->callback("error", "measurement peer overloaded", 6, "measurement peer overloaded");

	        ::hasError 		= true;
			::TIMER_STOPPED = true;

        	break;
		}
		
		//If Timer is not running
		if( !TIMER_RUNNING )
		{
			sync_counter = 0;
			
			//Check if all Threads available
			for (AI = syncing_threads.begin(); AI!= syncing_threads.end(); ++AI)
			{
				//Check if we are synced
				if( (*AI).second == 1 )
					sync_counter++;
			}

			//If equal, we are synced
			if( sync_counter == mInstances )
			{
				mCallback->callback("info", "starting measurement", 0, "");
				//Wait for inial callback delay
				usleep( mInitialCallbackDelay );
				mCallback->callback("info", "measurement started", 0, "");

				//Start timer
				TIMER_RUNNING = true;
				
				//Get init timestamp
				time1 = CTool::get_timestamp();
				
				#ifndef NNTOOL
				MYSQL_LOG("LOG_TIMER_START", CTool::toString( CTool::get_timestamp() ) );
				#endif
			}
		}
		else
		{
			int current_index = 0;
			do
			{
				time2 = CTool::get_timestamp();

				//Get over all duration of measurement while synced
				time_diff = time2 - time1;
				TIMER_DURATION = time_diff;

				//if we reached the time, stop immediately
				if( MEASUREMENT_DURATION <= (unsigned long long)(time_diff/1000000) )
				{
					//Stop timer
					TIMER_STOPPED = true;

					#ifndef NNTOOL
					MYSQL_LOG("LOG_TIMER_END", CTool::toString( CTool::get_timestamp() ) );
					#endif
				}
				else
					TIMER_INDEX = (int)(time_diff/500000);

				if (TIMER_INDEX != current_index)
				{
					current_index = TIMER_INDEX;

					mCallback->callback("report", "measurement report", 0, "");
				}

				//Sleep 10ms
				usleep(10000);

			}while( RUNNING && !TIMER_STOPPED && !m_fStop );
		}

		//Sleep 10ms
		usleep(1000);

		//Stop Timer if needed
		if( m_fStop )
			break;
	}

    if (::RUNNING && !::hasError) {
	    mCallback->callback("finish", "measurement completed", 0, "");
	}
	
	TIMER_ACTIVE = false;
	
	//++++++END+++++++

	//Log Message
	TRC_INFO( ("Ending Timer Thread with PID: " + CTool::toString(syscall(SYS_gettid))).c_str() );

	#ifdef __ANDROID__
		AndroidConnector::detachCurrentThreadFromJavaVM();
	#endif
	
	return 0;
}
