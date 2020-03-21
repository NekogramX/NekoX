# NekoX
![Logo](https://raw.githubusercontent.com/NekogramX/NekoX/master/TMessagesProj/src/main/res/mipmap-xxxhdpi/ic_launcher.png)  

NekogramX is an UNOFFICIAL app that uses Telegram's API.

- Google play store: (unavailable now)
- Update news : https://t.me/NekogramX
- Feedback: https://t.me/NekoXChat
- Feedback: https://github.com/NekogramX/NekoX/issues

## API, Protocol documentation

Telegram API manuals: https://core.telegram.org/api

MTproto protocol manuals: https://core.telegram.org/mtproto

## Compilation Guide

### Specify APP_ID and APP_HASH

Just fill out TELEGRAM_APP_ID and TELEGRAM_APP_HASH in local.properties

### Build Types

#### Debug

`./gradlew assemble<Variant>Debug`

The default debug key is used, and placing yours is not needed.

#### Release

`./gradlew assemble<Variant>Release`

The difference between release and other build types is that it adds fcm and firebase crash analysis, if you don't like them, use releaseNoGcm.

To compile the release version, please place your keysotre at TMessageProj/release.jks, and fill in KEYSTORE_PASS, ALIAS_NAME, ALIAS_PASS in local.properties, environment variables are also recommended

If you don't use NekoX's APP_ID and APP_HASH, you need to register a physical firebase app and replace google-services.json to ensure fcm works

#### ReleaseNoGcm

`./gradlew assemble<Variant>ReleaseNoGcm`

OK, a version without firebase cloud messaging, maybe this makes you feel more free, or your phone does not have Google services.

#### Foss

`./gradlew assemble<Variant>Foss`

It's not any different from releaseNoGcm, if you trust us that we haven't fiddled with precompiled native libraries.

To compile the foss version, you need to run `build_ffmpeg_clang.sh` `patch_ffmpeg.sh` `patch_boringssl.sh` `build_boringssl.sh` under **TMessageProj/jni** in turn, and then follow the [instructions](https://github.com/2dust/AndroidLibV2rayLite/blob/master/.travis.yml) to compile https://github.com/2dust/AndroidLibV2rayLite to **TMessageProj/libs/libv2ary.aar** first.

### Build Variants

Available variant list:

`Afat`, `Armv7`, `Armv7_SDK21`, `Arm64`, `Arm64_SDK21`, `X86`, `X86_SDK21`, `x64`, `x64_SDK21`.

If you don't know their difference, `Afat` is recommended.


## Localization

NekoX is forked from Nekogram-FOSS, thus most locales follows the translations of Telegram for Android, checkout https://translations.telegram.org/en/android/.

As for the specialized strings, we use Crowdin to translate Nekogram. Join project at https://neko.crowdin.com/nekogram and https://nekox.crowdin.com/nekox. Help us bring Nekogram to the world!

## Contributors

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
| [<img src="https://avatars2.githubusercontent.com/u/42698724?s=460&v=4" width="80px;"/><br /><sub>猫耳逆变器</sub>](https://github.com/NekoInverter)<br />[💻](https://github.com/Nekogram/Nekogram/commits?author=NekoInverter "Code") | [<img src="https://avatars1.githubusercontent.com/u/18373361?s=460&v=4" width="80px;"/><br /><sub>梨子</sub>](https://github.com/rikakomoe)<br />[💻](https://github.com/Nekogram/Nekogram/commits?author=rikakomoe "Code") | [<img src="https://i.loli.net/2020/01/17/e9Z5zkG7lNwUBPE.jpg" width="80px;"/><br /><sub>呆瓜</sub>](https://t.me/Duang)<br /> [🎨](#design-duang "Design") |
| :---: | :---: | :---: |
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/kentcdodds/all-contributors) specification. Contributions of any kind welcome!
