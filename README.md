# HelaClock-Pro

[![license](https://img.shields.io/github/license/DAVFoundation/captain-n3m0.svg?style=flat-square)](https://github.com/00sanoj00/HelaClock-Pro/blob/master/LICENSE)

###### @SaNoJ :+1: Simple Sinhala TextClock Widget  - it's ready :shipit:

#### [Download](https://github.com/bhashanaliyanage/HelaClock-Pro/releases/)

## Build

Install JDK 17 and Android SDK Platform 37, and configure the Android SDK path in
`local.properties` or `ANDROID_HOME`. Then build the debug APK with:

```sh
./gradlew assembleDebug
```

The APK is written to `app/build/outputs/apk/debug/app-debug.apk`. The app supports
Android 7.0 (API 24) and newer. On Android 12 and later, Widget Settings offers
access to precise alarms when the widget needs it; without access, Android may
delay updates.

## Signed releases

Pushing a version tag such as `v3.1.4` builds a signed release APK and publishes
it as a GitHub Release. Before creating the first tag:

1. Create a release keystore and keep a secure backup. You must use this same
   keystore for every future update so Android accepts the APK as an upgrade.
   For example, run `keytool -genkeypair -v -keystore helaclock-release.jks
   -keyalg RSA -keysize 2048 -validity 10000 -alias helaclock` and retain the
   passwords and alias you choose.
2. In the repository's **Settings → Secrets and variables → Actions**, add these
   repository secrets: `ANDROID_KEYSTORE_BASE64` (the keystore file encoded as
   one Base64 string), `ANDROID_KEYSTORE_PASSWORD`, `ANDROID_KEY_ALIAS`, and
   `ANDROID_KEY_PASSWORD`.
3. On Windows PowerShell, encode the keystore for the first secret with:

   ```powershell
   [Convert]::ToBase64String([IO.File]::ReadAllBytes(".\helaclock-release.jks"))
   ```

4. Update `versionCode` and `versionName` in `app/build.gradle`, commit, and
   push the matching tag (for example, `git tag v3.1.4; git push origin v3.1.4`).

The workflow at `.github/workflows/release.yml` builds `app-release.apk` and
attaches it to the generated GitHub Release. Never commit the keystore or its
passwords. Local `assembleRelease` builds also require the four signing
environment variables used by the workflow.

## Recent Updates by Bhashana (August 2024)
- Main screen now have a preview of the widget
- App now have a new UI
- Colors you change in the widget are now synced with the hint image also
- You can now go directly to your alarm app by clicking the widget
- Fixed minor Color Picker issues and added the functionality to change colors by entering the HEX value
- Various performance optimizations

![alt text](https://github.com/bhashanaliyanage/HelaClock-Pro/blob/master/Image/updates/application_screenshots.png?raw=true)

![alt text](https://github.com/bhashanaliyanage/HelaClock-Pro/blob/master/Image/updates/application_description.png?raw=true)






