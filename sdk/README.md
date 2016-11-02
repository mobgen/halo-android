# HALO Android SDK
------------------
![Android >= 4.0.3](https://img.shields.io/badge/Android-%3E=%204.0.3-blue.svg)
![Gradle](https://img.shields.io/badge/Gradle-compatible-brightgreen.svg)

This library provides the developer with a huge power to access the HALO cloud platform to consume
generic content and push notifications. Is is built upon a solid architecture
on which the developer can modify almost every behaviour in an easy way, but it also has a predefined
configuration so you don't have to know everything under the hood to use HALO. We built this SDK
with Junior developers in mind, so getting started should not take you more than 5 minutes.

## Repository download ##
-------------------
Define the dependency in your projects build.gradle:

```groovy
dependencies {
    compile 'com.mobgen.halo.android:halo-sdk:{currentVersion}'
}
```

The gradle plugin dependency to be added in the root build.gradle:

```groovy
buildscript {
    dependencies {
          classpath 'com.mobgen.halo.android:halo-plugin:{currentVersion}'
    }
}
```

And apply the plugin in the app after the android application one:
```groovy
apply plugin: 'halo'
```

## SDK Overview ##
-------------------
The HALO platform is a [MOBGEN](http://mobgen.com) cloud platform for developing mobile applications and it was designed
for extensibility and reusability. To access all the services provided by the cloud, we have created
two SDKs for the main mobile platforms, iOS and Android. This SDK is the Android version and you can
check the architecture documentation in this README file or access the [wiki](https://bitbucket.org/mobgen/halo-sdk-android/wiki/Home). Here is also the reference
for the [iOS documentation](http://borjasantos.bitbucket.org/docs/ios/halo-sdk/).

## What is the structure of this project? ##
This project contains 4 different modules:

* __halo-sdk__: contains all the classes and modules to access the HALO cloud platform. Its contents and
how to use it are described in the [wiki](https://bitbucket.org/mobgen/halo-sdk-android/wiki/Home).
* __halo-framework__: contains an extensible framework to use online and local requests.
* __halo-testing__: contains some useful classes that we use to test our sdk. This will not have any
dependency available to be used.
* __halo-plugin__: this project contains a gradle plugin that helps the user to configure HALO
without typing much more code. It also configures the push notifications for you.
* __halo-sample__: this is a sample application that shows how to make an easy request that is
available in local with HALO.

## Module Architecture ##
It has many sdks each of which is meant to handle a proper part of the Api. The available modules are:

* __HaloCore__: the core contains the credentials to be logged in in HALO and the session variables
once the session has been established. It also contains useful information like a debug variable in case
of the version is a debug one, the version of the library and the framework reference, so the developer can do some checks against it.
Check the core [wiki page](https://bitbucket.org/mobgen/halo-sdk-android/wiki/modules/HaloCore) to learn more.
and also to the user preferences, which is a great help in some cases. Check the storage [wiki page](https://bitbucket.org/mobgen/halo-sdk-android/wiki/modules/HaloStorage) to learn more.
* __HaloManagerApi__: manages all the manager actions required by the requests that will be done from the modules and the user requests. If you want to learn more about the manager checkout [the wiki](https://bitbucket.org/mobgen/halo-sdk-android/wiki/modules/HaloAuthentication).
* __HaloContentApi__: this module is intended to access the general content APIs which allow the user
to retrieve general content information. Have a look to the general content [wiki page](https://bitbucket.org/mobgen/halo-sdk-android/wiki/modules/HaloGeneralContent) to learn more.
* __HaloPushApi__: this module manages the user object which contains useful information like the segmentation tags or the
notification GCM token if it is available. Check out the [wiki page](https://bitbucket.org/mobgen/halo-sdk-android/wiki/modules/HaloUser) for the user settings.

Every single module has its own API calls that helps the user to interact with the HALO API. This facade
is not likely to change so it is better to use those than the internal calls. The convention to do so is taking 
the module on which you want to interact and follow this:

```java
HaloModuleApi.with(halo).call(parameters);
```

You can always take data from the core, but it is more meant to be used with some of the apis. Here is an example
of the core calls:
```java
HaloCredentials credentials = halo.core().getCredentials();
```

Remember that before making any call to the HALO SDK it is needed that you install it in your
application. Here it is explained in the [getting started guide](https://bitbucket.org/mobgen/halo-sdk-android/wiki/Home) how
you can do it.

# Proguard #
To use proguard with HALO add the following rules:
```
# HALO
-keepattributes Signature
-keep class com.mobgen.halo.android.sdk.core.internal.storage.HaloManagerContract$* {*;}
-keep class com.mobgen.halo.android.sdk.content.storage.HaloContentContract$* {*;}
-keep class com.mobgen.halo.android.framework.storage.database.dsl.annotations.* {*;}
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
```

# Libraries we use #
With HALO we internally import some libraries detailed here:

* __OkHttp__: To make the online requests.
* __Gson__: The parsing is done using gson.
* __GCM from google play services__: The google play services to enable push notifications. If you don't
use push notifications in your application you can prevent it by adding the following snippet to
your project:

```java
dependencies {
    compile (com.mobgen.halo.android:halo-sdk:{version}) {
        exclude group:"com.google.android.gms"
    }
}
```
If this is the case, add the following to proguard to avoid warnings related:
```
-dontwarn com.mobgen.halo.android.sdk.**
```

# Library development #
-------------------
This project is under development using the latest version of Android Studio, gradle and the Android
build tools. To get started with the development, download the repository from bitbucket:

```bash
git clone https://javier-mobgen@bitbucket.org/mobgen/halo-sdk-android.git
```

If you open the project with AS you may notice an error telling that you don't have the halo-plugin available, so you have to install
it by executing in the command line in the base dir of the project:

```bash
cd halo-sdk-android
./gradlew -PpluginCompile halo-plugin:install
```

This command will install it in your system's gradle repository and you will be able to sync the gradle files successfully.

Now you will be able to open the project with Android Studio clicking on **Open existing Android Project** and let AS
to configure it properly.

## Run the sample application ##
You can run the sample application by clicking the halo-app in the top button in Android Studio and hit
"play". This will install the app which has support for QA and INT environments.

From command line (on halo-sdk-android):

```bash
./gradlew halo-sample:installDebug
or for release
./gradlew halo-sample:installRelease
```

## Create the documentation ##
To generate the documentation you can run the following command:

```bash
./gradlew documentationDebug
or
./gradlew documentationRelease
```

You can find the documentation under ```{module}/build/doc```.

## Run unit tests ##
We are always trying to keep our code coverage to 65%, so this project contains many tests that runs
on the JVM with robolectric and jUnit to keep the code clean and tested. To run the tests you have
the typical tasks from gradle (testDebugUnitTest and testReleaseUnitTest), but we also provide some
to run the Jacoco code coverage report. Here it is the following command:

```bash
./gradlew halo-sdk:createDebugUnitTestCoverageReport
or
./gradlew halo-sdk:createReleaseUnitTestCoverageReport
```

You can find the report under ```{module}/build/reports/jacoco```.

If you just want to run the unit tests you can run the following command:
```bash
./gradlew halo-sdk:testDebugUnitTest
or
./gradlew halo-sdk:testReleaseUnitTest
```

# LICENSE #
---------------
```
Copyright 2016 MOBGEN

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```