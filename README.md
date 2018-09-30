[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

# apps-android-testdevicemanager
Gradle custom plugin to setup test devices.

# What is it?
Testdevicemanager is a custom gradle plugin written in Kotlin. It uses ```adb commands``` to get and set device information. Therefore no additional access rights for connected mobile devices are required.

# What's its purpose?
Testdevicemanager can be used to set up test devices for espresso ui testing.

# Features
The following things can be managed by the plugin:

- Locking / Unlocking the device - The device can be unlocked via pressing the power button, swiping, entering a pin or a password

- Disabling / Enabling animations - To make sure espresso ui tests run smoothly, animations can be deactivated in the developer options of the test device

- Enabling / Disabling the stay awake mode - The device's stay awake mode can be activated in the developer options of the test device, to make sure that the test device does not switch of the screen during testing

- Checking for connection to a specific WLAN - A check for a connection to a specific WLAN can be done

# Integration into the project
//maintain steps for integrating into project

# Setup
All of the following extension values can be maintained but do not need to be used at all.
``` 
testDeviceManager {
    unlockBy = "power button | swipe | pin | password"
    pin = "9999"
    password = "password"
    wifi = "wifi-name"
}
```
# Compatibility
The plugin was tested on several devices of different brands and different Android versions. 

It will run on ```Android 4.1.2 - Android 8```. 

However, there might be device - OS version combinations that where not tested and might result in a not working plugin.

# Preconditions
- Test devices need to be connected to the machine running this plugin.
- Developer options need to be activated on each connected test device.
- ADB connections needs to be established between test device and developer machine / server.

# Usage 
The following tasks will be added to the ```device setup``` section of the gradle tasks
```
connectedAnimationsDisable
connectedAnimationsEnable
connectedCheckWifi
connectedDeviceLock
connectedDeviceUnlock
connectedStayAwakeDisable
connectedStayAwakeEnable
```
Run these tasks before you run your espresso tests and setup your test devices.

# More detailed information
// add Link to article on Medium.ocm
