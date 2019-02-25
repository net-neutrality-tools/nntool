#!/bin/bash

#/*
# *********************************************************************************
# *                                                                               *
# *       ..--== zafaco GmbH ==--..                                               *
# *                                                                               *
# *       Website: http://www.zafaco.de                                           *
# *                                                                               *
# *       Copyright 2019                                                          *
# *                                                                               *
# *********************************************************************************
# */

#/*!
# *      \author zafaco GmbH <info@zafaco.de>
# *      \date Last update: 2019-02-13
# *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
# */



#---------------------------------------------------------

#create dir structure
rm -R build

mkdir build
mkdir build/plain
mkdir build/plain/core

mkdir build/plain/common
mkdir build/plain/common-mobile

if [ -z $1 ]; then
    mkdir build/uglified
    mkdir build/uglified/core
    mkdir build/uglified/core/modules

    mkdir build/uglified/common
    mkdir build/uglified/common-mobile
fi


#---------------------------------------------------------

#build core
cp src/*.js build/plain/core/
cp -R src/modules build/plain/core/

if [ -z $1 ]; then
    cd build/plain/core/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/core/$short.js; done
    cd modules/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../../uglified/core/modules/$short.js; done
    cd ../../../../
fi

#---------------------------------------------------------

#build common
cp -R src/common/* build/plain/common/

if [ -z $1 ]; then
    cp -R src/common/index.html build/uglified/common/
    cd build/plain/common/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/common/$short.js; done
    cd ../../
else
    cd build/
fi

cat plain/core/modules/browser.report.js		>> plain/common/common.js
cat plain/core/modules/Tool.js 					>> plain/common/common.js
cat plain/core/Control.js						>> plain/common/common.js
cat plain/core/ControlSingleThread.js			>> plain/common/common.js
cat plain/core/Measurement.js 					>> plain/common/common.js
cat plain/core/Worker.js 						>> plain/common/common.js
cat plain/core/WorkerSingleThread.js 			>> plain/common/common.js
cat plain/core/PortBlocking.js 					>> plain/common/common.js
cat plain/common/common.footer.js				>> plain/common/common.js

if [ -z $1 ]; then
    uglifyjs -c drop_console=true -m --comments /^!/ plain/common/common.js > uglified/common/common.js
fi

cp plain/core/Worker.js plain/common/
cp plain/core/modules/Tool.js plain/common/
rm plain/common/common.footer.js

if [ -z $1 ]; then
    cp uglified/core/Worker.js uglified/common/
    cp uglified/core/modules/Tool.js uglified/common/
    rm uglified/common/common.footer.js
fi

cd ../


#---------------------------------------------------------

#build common-mobile
cp -R src/common-mobile/* build/plain/common-mobile/

if [ -z $1 ]; then
    cd build/plain/common-mobile/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/common-mobile/$short.js; done
    cd ../../
else
    cd build/
fi

cat plain/common-mobile/common-mobile.header.js	>> plain/common-mobile/common-mobile.js
cat plain/core/modules/Tool.js 					>> plain/common-mobile/common-mobile.js
cat plain/core/ControlSingleThread.js			>> plain/common-mobile/common-mobile.js
cat plain/core/Measurement.js 					>> plain/common-mobile/common-mobile.js
cat plain/core/Worker.js 						>> plain/common-mobile/common-mobile.js
cat plain/core/WorkerSingleThread.js 			>> plain/common-mobile/common-mobile.js
cat plain/common-mobile/common-mobile.footer.js	>> plain/common-mobile/common-mobile.js

if [ -z $1 ]; then
    uglifyjs -c drop_console=true -m --comments /^!/ plain/common-mobile/common-mobile.js > uglified/common-mobile/common-mobile.js
fi

rm plain/common-mobile/common-mobile.header.js
rm plain/common-mobile/common-mobile.footer.js
rm -R plain/core/

if [ -z $1 ]; then
    rm uglified/common-mobile/common-mobile.header.js
    rm uglified/common-mobile/common-mobile.footer.js
    rm -R uglified/core/
fi


#---------------------------------------------------------

exit 0;
