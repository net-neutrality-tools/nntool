/*!
    \file load_balancing.cpp
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

#include "load_balancing.h"




CLoadBalancing::CLoadBalancing(Json::object *load)
{
    jLoad = load;
}

CLoadBalancing::~CLoadBalancing()
{
}

int CLoadBalancing::run()
{
    TRC_INFO("Start Thread: Load Balancing with PID " + std::to_string(syscall(SYS_gettid)));

    int port = 44301;
    
    if (::CONFIG["load"]["balancer"]["port"].number_value())
    {
        port = ::CONFIG["load"]["balancer"]["port"].number_value();
    }
    

    mConnection = std::make_unique<CConnection>();

    int on = 1;

    struct sockaddr_in6 client;
    unsigned int clientlen = sizeof(client);

    if (mConnection->tcp6SocketServer(port) == 1)
    {
        TRC_CRIT("Socket creation failed - Could not establish connection on target Port " + to_string(port));
        return -1;
    }


    TRC_INFO("Start Thread: TCP TLS socket on target Port " + to_string(port) + " with PID " + std::to_string(syscall(SYS_gettid)));

    timeval tv;
    timeval tv_l;
    tv.tv_sec = 60;
    tv.tv_usec = 0;
    tv_l.tv_sec = 1;
    tv_l.tv_usec = 0;
    setsockopt(mConnection->mSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv_l, sizeof(timeval));
    setsockopt(mConnection->mSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv_l, sizeof(timeval));
    while (::RUNNING)
    {
        int nSocket = accept(mConnection->mSocket, (struct sockaddr *)&client, &clientlen);
        if (nSocket == -1)
        {
            continue;
        }

        setsockopt(nSocket, SOL_SOCKET, SO_RCVTIMEO, (timeval *)&tv, sizeof(timeval));
        setsockopt(nSocket, SOL_SOCKET, SO_SNDTIMEO, (timeval *)&tv, sizeof(timeval));
        
        setsockopt(nSocket, IPPROTO_TCP, TCP_QUICKACK,  (void *)&on, sizeof(on));

        if (fork() == 0)
        {
            string ip = CTool::get_ip_str((struct sockaddr *)&client);  
                
            if (ip.find("::ffff:") != string::npos)
            {
                ip = ip.substr(7,string::npos);
            }

            std::unique_ptr<CConnection> mAcceptedConnection    = std::make_unique<CConnection>();
            mAcceptedConnection->mSocket                        = nSocket;

            if (mAcceptedConnection->tlsServe() < 0)
            {
                TRC_ERR("TCP load balancing handler: TLS negotiation failed");
                mAcceptedConnection->close();
                exit(0);
            }

            TRC_INFO("Socket: TCP TLS connection received from Client IP " + ip + " on target Port " + to_string(port));

            std::unique_ptr<char[]> rbufferOwner = std::make_unique<char[]>(5000);
            char *rbuffer = rbufferOwner.get();

            std::unique_ptr<char[]> rchunkOwner = std::make_unique<char[]>(50);
            char *rchunk = rchunkOwner.get();

            string request;

            bzero(rbuffer, 5000);
            
            unsigned long long timeout = 2*1e6;
            unsigned long long currentTime;
            unsigned long long startTime = CTool::get_timestamp();
           
            int bytes_received = 0;
            mAcceptedConnection->setNonBlocking();

            //read socket non-blocking chunked for at most timeout seconds
            while(1)
            {
                currentTime = CTool::get_timestamp();
                bzero(rchunk, 20);

                bytes_received = mAcceptedConnection->receive(rchunk, 20, 0);

                strcat(rbuffer, rchunk);

                //break if timeout is reached or no bytes are outstanding
                if ((currentTime - startTime) >= timeout || (bytes_received < 0 && strlen(rbuffer) != 0) )  
                {
                    break;
                }

                usleep(10);
            }

            request = string(rbuffer);

            Json jRequest = Json::object{};

            string error = "";

            try 
            {
                std::size_t pos = request.find("\r\n\r\n");
                request = request.substr(pos);

                jRequest = Json::parse(request, error);
            }
            catch(exception e)
            {
                error = "1";
            }

            //parse parameters
            if (error.compare("") != 0)
            {
                TRC_ERR("TCP load balancing handler: JSON parameter parse failed");

                char response[] = HTTP_BAD_REQUEST;
                mAcceptedConnection->send(response, strlen(response), 0);

                mAcceptedConnection->close();
                exit(0);
            }

            //check secret and cmd
            if (::CONFIG["load"]["balancer"]["secret"].string_value().compare(jRequest["secret"].string_value()) != 0 || jRequest["cmd"].string_value().compare("load") != 0)
            {
                TRC_ERR("TCP load balancing handler: missing secret, wrong secret or wrong cmd");

                char response[] = HTTP_FORBIDDEN;
                mAcceptedConnection->send(response, strlen(response), 0);

                mAcceptedConnection->close();
                exit(0);
            }

            pthread_mutex_lock(&mutexLoad);
            string responseBody = Json(*jLoad).dump();
            pthread_mutex_unlock(&mutexLoad);


            //return load and close connection
            string response  = "";
            response += "HTTP/1.1 200 OK\r\n";  
            response += "Content-Length: " + to_string(responseBody.size()) + "\r\n";
            response += "Content-Type: application/json\r\n";
            response += "Date: " + CTool::get_timestamp_string() + "\r\n\r\n";
            response += responseBody;
            
            mAcceptedConnection->send(response.c_str(), response.size(), 0);
            mAcceptedConnection->close();

            TRC_DEBUG("TCP load balancing handler: sent 200 OK with body: " + responseBody);

            Json::object jConfig = ::CONFIG.object_items();

            //check for collector configuration
            if (jRequest.object_items().count("collector") > 0)
            {
                jConfig["collector"] = jRequest["collector"];
                ::CONFIG = jConfig;

                TRC_DEBUG("TCP load balancing handler: Updated collector config");
            }

            exit(0);
        }

       close(nSocket);
    }

    TRC_DEBUG("End Thread: Load Balancing with PID " + std::to_string(syscall(SYS_gettid)));

    return 0;
}

