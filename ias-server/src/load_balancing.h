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

#ifndef LOAD_BALANCING_H
#define LOAD_BALANCING_H


#include "header.h"


using namespace json11;

class CLoadBalancing : public CBasisThread
{     
    public:
        CLoadBalancing(Json::object *load);

        virtual ~CLoadBalancing();

        int run() override;
        
    private:
        std::unique_ptr<CConnection> mConnection;

        Json::object *jLoad;
};

#endif
