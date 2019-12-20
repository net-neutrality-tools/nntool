#!/bin/bash
#
#############################################################################
#
# Copyright 2013 appscape gmbh
# Copyright 2017-2019 alladin-IT GmbH
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
##############################################################################
#

set -o errexit
set -o nounset

GIT_DIR="${PROJECT_DIR}/../.git"
INFO_PLIST="${TARGET_BUILD_DIR}"/"${INFOPLIST_PATH}"

# GIT_TAG=$(git --git-dir=$GIT_DIR --work-tree="${PROJECT_DIR}" describe --dirty | sed -e 's/^v//' -e 's/g//')
GIT_COMMIT=$(git --git-dir=$GIT_DIR --work-tree="${PROJECT_DIR}" rev-parse --short HEAD)
GIT_BRANCH=$(git --git-dir=$GIT_DIR --work-tree="${PROJECT_DIR}" rev-parse --abbrev-ref HEAD)
GIT_COMMIT_COUNT=$(git --git-dir=$GIT_DIR --work-tree="${PROJECT_DIR}" rev-list ${GIT_BRANCH} | wc -l)

BUILD_DATE=$(date "+%Y-%m-%d %H:%M:%S")

defaults write $INFO_PLIST BuildDate "${BUILD_DATE}"
defaults write $INFO_PLIST GitCommit $GIT_COMMIT
defaults write $INFO_PLIST GitBranch $GIT_BRANCH
defaults write $INFO_PLIST GitCommitCount $GIT_COMMIT_COUNT
