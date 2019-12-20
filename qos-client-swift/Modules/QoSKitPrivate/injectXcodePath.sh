#!/bin/sh

# FROM https://github.com/svdo/swift-netutils/blob/master/ifaddrs/injectXcodePath.sh
# 
# The MIT License (MIT) Copyright (c) 2015 Stefan van den Oord
# 
# Permission is hereby granted, free of charge, to any person obtaining a
# copy of this software and associated documentation files (the
# "Software"), to deal in the Software without restriction, including
# without limitation the rights to use, copy, modify, merge, publish,
# distribute, sublicense, and/or sell copies of the Software, and to
# permit persons to whom the Software is furnished to do so, subject to
# the following conditions:
# 
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
# OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
# MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
# IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
# CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
# TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
# SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
#

set -x

defaultXcodePath="/Applications/Xcode.app/Contents/Developer"
realXcodePath="`xcode-select -p`"

fatal() {
    echo "[fatal] $1" 1>&2
    exit 1
}

absPath() {
    case "$1" in
        /*)
            printf "%s\n" "$1"
            ;;
        *)
            printf "%s\n" "$PWD/$1"
            ;;
    esac;
}

scriptDir="`dirname $0`"
scriptName="`basename $0`"
absScriptDir="`cd $scriptDir; pwd`"

main() {
    for f in `find ${absScriptDir} -name module.modulemap`; do
        cat ${f} | sed "s,${defaultXcodePath},${realXcodePath},g" > ${f}.new || fatal "Failed to update modulemap ${f}"
        mv ${f}.new ${f} || fatal "Failed to replace modulemap ${f}"
    done
}

main $*
