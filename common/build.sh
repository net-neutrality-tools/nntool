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
# *      \date Last update: 2019-01-02
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

mkdir build/uglified
mkdir build/uglified/core
mkdir build/uglified/core/modules

mkdir build/uglified/common
mkdir build/uglified/common-mobile


#---------------------------------------------------------

#build core
cp src/*.js build/plain/core/
cp -R src/modules build/plain/core/

cd build/plain/core/
for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/core/$short.js; done
cd modules/
for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../../uglified/core/modules/$short.js; done
cd ../../../../


#---------------------------------------------------------

#build common
cp -R src/common/* build/plain/common/
cp -R src/common/index.html build/uglified/common/
cd build/plain/common/
for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/common/$short.js; done
cd ../../
cat plain/core/modules/browser.report.js		>> plain/common/common.js
cat plain/core/modules/Tool.js 					>> plain/common/common.js
cat plain/core/Control.js						>> plain/common/common.js
cat plain/core/ControlSingleThread.js			>> plain/common/common.js
cat plain/core/Measurement.js 					>> plain/common/common.js
cat plain/core/Worker.js 						>> plain/common/common.js
cat plain/core/WorkerSingleThread.js 			>> plain/common/common.js
cat plain/common/common.footer.js				>> plain/common/common.js

uglifyjs -c drop_console=true -m --comments /^!/ plain/common/common.js > uglified/common/common.js


cp plain/core/Worker.js plain/common/
cp plain/core/modules/Tool.js plain/common/

rm plain/common/common.footer.js


cp uglified/core/Worker.js uglified/common/
cp uglified/core/modules/Tool.js uglified/common/

rm uglified/common/common.footer.js

cd ../


#---------------------------------------------------------

#build common-mobile
cp -R src/common-mobile/* build/plain/common-mobile/
cd build/plain/common-mobile/
for f in *.js; do short=${f%.js}; uglifyjs -c drop_console=true -m --comments /^!/ -- $f > ../../uglified/common-mobile/$short.js; done
cd ../../
cat plain/common-mobile/common-mobile.header.js	>> plain/common-mobile/common-mobile.js
cat plain/core/modules/Tool.js 					>> plain/common-mobile/common-mobile.js
cat plain/core/ControlSingleThread.js			>> plain/common-mobile/common-mobile.js
cat plain/core/Measurement.js 					>> plain/common-mobile/common-mobile.js
cat plain/core/Worker.js 						>> plain/common-mobile/common-mobile.js
cat plain/core/WorkerSingleThread.js 			>> plain/common-mobile/common-mobile.js
cat plain/common-mobile/common-mobile.footer.js	>> plain/common-mobile/common-mobile.js

uglifyjs -c drop_console=true -m --comments /^!/ plain/common-mobile/common-mobile.js > uglified/common-mobile/common-mobile.js


cp plain/core/Worker.js plain/common-mobile/

rm plain/common-mobile/common-mobile.header.js
rm plain/common-mobile/common-mobile.footer.js
rm -R plain/core/


cp uglified/core/Worker.js uglified/common-mobile/

rm uglified/common-mobile/common-mobile.header.js
rm uglified/common-mobile/common-mobile.footer.js
rm -R uglified/core/


#---------------------------------------------------------

exit 0;
