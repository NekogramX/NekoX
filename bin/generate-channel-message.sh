#!/usr/bin/env bash

LANG-"en_US.UTF8"

OUTPUT="TMessagesProj/build/outputs"
echo ::set-env name=APK_FILE::$(find $OUTPUT/apk -name "*.apk")
echo ::set-env name=AAB_FILE::$(find $OUTPUT/bundle -name "*.aab")

GIT_HEAD=$(git rev-parse HEAD)
GIT_MESSAGE=$(git log -1)
GIT_MESSAGE=${GIT_MESSAGE/$GIT_HEAD/($GIT_HEAD)[https://github.com/NekogramX/NekoX/commit/$GIT_HEAD]}

echo $GIT_MESSAGE

VERSION_NAME=$(grep -oP 'versionName "(.*)"' TMessagesProj/build.gradle)
VERSION_NAME=${VERSION_NAME:13:-1}

RELEASE_MESSAGE="
#测试版 $VERSION_NAME

$(cat whatsnew/whatsnew-zh-CN)
"

if [ -f whatsnew/whatsnew-en-US ]; then

  RELEASE_MESSAGE = "$RELEASE_MESSAGE

-----------------

$(cat whatsnew/whatsnew-en-US)
"

fi

echo ::set-env name=COMMIT_MSG::$GIT_MESSAGE
echo ::set-env name=RELEASE_MESSAGE::$GIT_MESSAGE