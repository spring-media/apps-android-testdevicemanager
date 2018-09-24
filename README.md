# apps-android-testdevicemanager
Gradle custom plugin to setup test devices.

# What is it?
Testdevicemanager is a custom gradle plugin written in Kotlin.

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
``` 
testDeviceManager {
    unlockBy = "power button | swipe | pin | password"
    pin = "9999"
    password = "password"
    wifi = "wifi-name"
}
```

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
