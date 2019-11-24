# Lists

Keeps lists synchronized across different devices.

**Technical Features**

- Firebase's [Cloud Firestore](https://firebase.google.com/docs/firestore) to synchronize between Android devices.
  - Offline on-device storage.
  - [Cloud Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started) written from-strach to provide user and data validation.
  - [Cloud Functions](https://firebase.google.com/docs/functions), written in JavaScript, to automatically and remotely delete the contents of a list when a list is deleted by the user.
- Firebase Authentication to authenticate users.
  - User can delete their account from within the app. A user's data will be automatically deleted when they do so.
  - Users can authenticate via their Google account, a phone number (verification via SMS), or email and password (with email verification).
- MVP-like architecture with dependency injection via Dagger2.
  - View: Fragments set-up as Passive Views. I use a single Activity architecture with Jetpack's [Navigation library](https://developer.android.com/guide/navigation).
  - Logic: Similar to a presenter.
  - ViewModel: Stores and retrives user-facing messages for the the Logic class. That is kept seperate so I can keep the Logic class free of refeneces to the Android framework, thus I can test it with JUnit on the JVM.
  - Repository: It validates data before forwarding it to the RemoteRepository classes that directly communicate with Cloud Firestore.
- Multithreading with RxJava 2.
- [Firebase Crashlytics](https://firebase.google.com/docs/crashlytics/) for crash reporting.
- Unit testing with JUnit 5, AssertJ, and MockK.
- Originally written in Java, completely converted to Kotlin.
  - When the app was written in Java, I used JUnit 4 and Mockito for unit testing.

**Features**

- Automatic backs-up all of your lists to "the cloud".
- Synchronized all of your lists between different devices.
  - Requires the creation of an account (supported methods below).
- All lists are stored on your device for offline access.
- Night mode
- Drag to rearrange
- Supports keyboard navigation.
- Available authentication methods:
  - Google account
  - Email
  - Phone number

# Screenshots

Sign-in

<img src="screenshots/authenticate.jpg" width=30% />

Main screen

<img src="screenshots/user_lists.jpg" width=30% />  <img src="screenshots/user_lists_2.jpg" width=30% />  

Inside of a list

<img src="screenshots/items.jpg" width=30% />

Preferences

<img src="screenshots/preferences.jpg" width=30% />

Night mode

<img src="screenshots/night_mode.jpg" width=30% />

# Resources

**Icons**

- https://material.io/tools/icons/?style=round

## License

    Copyright (c) 2018-present, David M Precopia.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
