# Shuttle Tracker for Android Devices

Track RPI shuttles in real-time from your Android device.

Shuttle Tracker is the officially supported application for accessing the [RPI Shuttle Tracking website](http://shuttles.rpi.edu/).

This application is currently still in development, but it will eventually be made available on Google Play and Amazon Appstore.  When that happens, we'll put badges here.

## Contributing Bug Reports

We use GitHub for bug tracking.  Please search our issues for your bug and create a new one if your issue is not yet tracked.

https://github.com/wtg/android_shuttle_tracking/issues

Development chat room is hosted on HipChat.

https://www.hipchat.com/guju09Hfc

## Developer Information

This application uses feature detection to provide the best possible shuttle mapping experience for the following categories of Android devices:

* Android devices with Google Play Services. (Most Android devices in use)
* Android devices with the Amazon Maps API. (Fire device family, except 1st-generation Kindle Fire)
* Other Android devices using a WebView.  (Everything else)

### Build

Until I can figure out how to get Gradle set up, the following should get you with something that works.

* Create the library directory (mkdir libs/)
* Add the Android Support Library (android-support-v4.jar) and Gson 2.2.4 (gson-2.2.4.jar) libraries to the libs folder.