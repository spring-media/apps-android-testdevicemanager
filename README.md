[![Generic badge](https://img.shields.io/badge/Version-1.3-green)](https://shields.io/)
[![Build Status](https://travis-ci.com/spring-media/apps-android-testdevicemanager.svg?token=xAVzxLGs5Eppk88QPiED&branch=master)](https://travis-ci.com/spring-media/apps-android-testdevicemanager)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](https://opensource.org/licenses/MIT)

# apps-android-testdevicemanager
Gradle custom plugin to setup android test devices.

# Problem
When running Espresso tests for your Android projects on real devices you need to setup these before the tests run. Doing this manually takes time and is cumbersome.

# Solution
Testdevicemanager - It is a custom gradle plugin written in Kotlin. It uses ```adb commands``` to get and set device information. This way, no additional access rights for connected mobile devices are required.

# Detailed description
Find a more detailed decription here: https://medium.com/axel-springer-tech/preparing-android-devices-for-espresso-tests-with-testdevicemanager-5d8e63e43269

# Features
The following things are handled by the plugin:

- Locking / Unlocking the device - The device will be unlocked by pressing the power button, swiping, entering a pin or a password

- Enabling / Disabling animations - To make sure espresso ui tests run smoothly, animations will be deactivated in the developer options of the test device

- Enabling / Disabling the stay awake mode - The device's stay awake mode will be activated in the developer options of the test device, to make sure that the test device does not switch of the screen during testing

- Checking for connection to a specific WLAN - A check for a connection to a specific WLAN will be performed

- Checking language set on the device

- Setting language on the device - Possible only when additional app is installed: AdbChangeLanguage available in Google Play: https://play.google.com/store/apps/details?id=net.sanapeli.adbchangelanguage

# Integration into the project
## Groovy
### Plugins DSL
```
plugins {
  id "de.welt.apps.testdevicemanager" version "1.3"
}
```
### Legacy plugin application
```
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "de.welt.apps:testdevicemanager:1.3"
  }
}

apply plugin: "de.welt.apps.testdevicemanager"
```
## Kotlin
### Plugins DSL
```
plugins {
  id("de.welt.apps.testdevicemanager") version "1.3"
}
```
### Legacy plugin application
```
buildscript {
  repositories {
    maven {
      url = uri("https://plugins.gradle.org/m2/")
    }
  }
  dependencies {
    classpath("de.welt.apps:testdevicemanager:1.3")
  }
}

apply(plugin = "de.welt.apps.testdevicemanager")
```
# Setup
All of the following extension values can be used but do not need to be used at all.
``` 
testDeviceManager {
    unlockBy = "power button | swipe | pin | password"
    pin = "9999"
    password = "password"
    wifi = "wifi-name"
    language = "en-US"
}
```
# Compatibility
The plugin was tested on several devices of different brands and different Android versions. 

It will run on ```Android 4.1.2 - Android 10```. 

However, there might be device - OS version combinations that where not tested and might result in a not working plugin.

# Preconditions
- Test devices need to be connected to the machine running this plugin.
- Developer options need to be activated on each connected test device.
- ADB connections needs to be established between test device and developer machine / server.

# Usage 
The following tasks will be added to the ```device setup``` section of the gradle tasks
```
-connectedAnimationsDisable
-connectedAnimationsEnable
-connectedCheckLanguage
-connectedCheckWifi
-connectedDeviceLock
-connectedDeviceUnlock
-connectedSetLanguage
-connectedStayAwakeDisable
-connectedStayAwakeEnable
```
Run these tasks before you run your espresso tests and setup your test devices.

# License
Testdevicemanager is available under the MIT license. See the LICENSE file for more info.
