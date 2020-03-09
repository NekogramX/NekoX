#!/usr/bin/env bash


OUTPUT="TMessagesProj/build/outputs"

echo ::set-env name=APK_FILE::$(find $OUTPUT/apk -name "*.apk")
echo ::set-env name=AAB_FILE::$(find $OUTPUT/bundle -name "*.aab")

HEAD=$(git rev-parse HEAD)
COMMIT_MESSAGE=$(git log -1)
COMMIT_MESSAGE=${GIT_MESSAGE/$HEAD/($HEAD)[https://github.com/NekogramX/NekoX/commit/$HEAD]}

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

echo ::set-env name=COMMIT_MSG::$COMMIT_MESSAGE
echo ::set-env name=RELEASE_MESSAGE::$RELEASE_MESSAGE