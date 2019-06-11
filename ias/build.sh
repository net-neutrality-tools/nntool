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
# *      \date Last update: 2019-04-08
# *      \note Copyright (c) 2018 - 2019 zafaco GmbH. All rights reserved.
# */



#---------------------------------------------------------

#create dir structure

if [ -d "build" ]; then
    rm -R build
fi

mkdir build
mkdir build/plain
mkdir build/plain/core

mkdir build/plain/web
mkdir build/plain/mobile
mkdir build/plain/desktop

if [ -z $1 ]; then
    mkdir build/uglified
    mkdir build/uglified/core
    mkdir build/uglified/core/modules

    mkdir build/uglified/web
    mkdir build/uglified/mobile
    mkdir build/uglified/desktop
fi


#---------------------------------------------------------

#build core
cp src/*.js build/plain/core/
cp -R src/modules build/plain/core/
find build/plain/core -type f -name "*.spec.js" -exec rm -f {} \;

if [ -z $1 ]; then
    cd build/plain/core/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/core/$short.js; done
    cd modules/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../../uglified/core/modules/$short.js; done
    cd ../../../../
fi

#---------------------------------------------------------

#build web
cp -R src/web_desktop/* build/plain/web/

if [ -z $1 ]; then
    cp -R src/web_desktop/index.html build/uglified/web/
fi

cd build/

cat plain/core/modules/browser.report.js    >> plain/web/ias.web.js
cat plain/core/modules/Tool.js              >> plain/web/ias.web.js
cat plain/core/Control.js                   >> plain/web/ias.web.js
cat plain/core/ControlSingleThread.js       >> plain/web/ias.web.js
cat plain/core/Ias.js                       >> plain/web/ias.web.js
cat plain/core/Worker.js                    >> plain/web/ias.web.js
cat plain/core/WorkerSingleThread.js        >> plain/web/ias.web.js
cat plain/core/PortBlocking.js              >> plain/web/ias.web.js

if [ -z $1 ]; then
    uglifyjs -c drop_console=true -m --comments /^!/ plain/web/ias.web.js > uglified/web/ias.web.js
fi

cp plain/core/Worker.js plain/web/
cp plain/core/modules/Tool.js plain/web/

if [ -z $1 ]; then
    cp uglified/core/Worker.js uglified/web/
    cp uglified/core/modules/Tool.js uglified/web/
fi

cd ../


#---------------------------------------------------------

#build mobile
cp -R src/mobile/* build/plain/mobile/

if [ -z $1 ]; then
    cd build/plain/mobile/
    for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/mobile/$short.js; done
    cd ../../
else
    cd build/
fi

cat plain/mobile/header.js              >> plain/mobile/ias.mobile.js
cat plain/core/modules/Tool.js          >> plain/mobile/ias.mobile.js
cat plain/core/ControlSingleThread.js   >> plain/mobile/ias.mobile.js
cat plain/core/Ias.js                   >> plain/mobile/ias.mobile.js
cat plain/core/Worker.js                >> plain/mobile/ias.mobile.js
cat plain/core/WorkerSingleThread.js    >> plain/mobile/ias.mobile.js
cat plain/mobile/footer.js              >> plain/mobile/ias.mobile.js

if [ -z $1 ]; then
    uglifyjs -c drop_console=true -m --comments /^!/ plain/mobile/ias.mobile.js > uglified/mobile/ias.mobile.js
fi

rm plain/mobile/header.js
rm plain/mobile/footer.js

if [ -z $1 ]; then
    rm uglified/mobile/header.js
    rm uglified/mobile/footer.js
fi

cd ../


#---------------------------------------------------------

#build desktop
cp -R src/web_desktop/* build/plain/desktop/

if [ -z $1 ]; then
    cp -R src/web_desktop/index.html build/uglified/desktop/
fi

cd build/

cat plain/core/modules/browser.report.js    >> plain/desktop/ias.desktop.js
cat plain/core/modules/Tool.js              >> plain/desktop/ias.desktop.js
cat plain/core/Control.js                   >> plain/desktop/ias.desktop.js
cat plain/core/Ias.js                       >> plain/desktop/ias.desktop.js
cat plain/core/Worker.js                    >> plain/desktop/ias.desktop.js

if [ -z $1 ]; then
    uglifyjs -c drop_console=true -m --comments /^!/ plain/desktop/ias.desktop.js > uglified/desktop/ias.desktop.js
fi

cp plain/core/Worker.js plain/desktop/
cp plain/core/modules/Tool.js plain/desktop/JSTool.js

if [ -z $1 ]; then
    cp uglified/core/Worker.js uglified/desktop/
    cp uglified/core/modules/Tool.js uglified/desktop/JSTool.js
fi


#---------------------------------------------------------

#cleanup core
rm -R plain/core/

if [ -z $1 ]; then
    rm -R uglified/core/
fi


#---------------------------------------------------------

exit 0;
