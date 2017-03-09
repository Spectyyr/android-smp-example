SessionM Platform SDK Example Apps
===================

Included in this repo are example modules that show how to use the SessionM Platform SDK for Android. Follow the steps below to setup and build a project:

- Clone this repo.
- Open settings.gradle in Android Studio.

You should be able to run different modules with default demo server and app.

If you want to change the APP key and server as your custom ones, make the following changes:

- Add your SessionM App API key in res/values/Strings.xml in "app_key" field.
- set your server url in SEApplication.java: sessionM.setServerType(SessionM.SERVER_TYPE_CUSTOM, "PLEASE_SET_YOUR_CUSTOM_SERVER_HERE");

For more SessionM Platform features, please email us directly.

LICENSE: MIT

For more help see https://mmc.sessionm.com/docs/mmc-sdk/
