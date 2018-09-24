# apps-android-testdevicemanager
Gradle custom plugin to setup test devices.

This custom gradle plugin can be used to set up test devices in a CI build. 
The main purpose is to setup Android test devices for espresso ui testing.

# Features
The following things can be managed by the plugin:

- Locking / Unlocking the device - The device can be unlocked via pressing the power button, swiping, entering a pin or a password

- Disabling / Enabling animations - To make sure espresso ui tests run smoothly, animations can be deactivated in the developer options 

- Enabling / Disabling the stay awake mode - The device's stay awake mode can be activated, to make sure that the test device does not switch of the screen during testing

- Checking for connection to a specific WLAN - A check for a specific WLAN can be done


# Usage

``` 
testDeviceManager {
    unlockBy = "power button | swipe | pin | password"
    pin = "9999"
    password = "password"
    wifi = "wifi-name"
}
```
