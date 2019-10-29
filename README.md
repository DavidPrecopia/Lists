# Lists

Keeps lists synchronized across different devices.

**Technical Features**

- Firebase Cloud Firestore to synchronize between Android devices.
- Firebase Authentication to authenticate users.
- MVP-like architecture with dependency injection via Dagger2.
- Multithreading with RxJava 2.
- Unit testing with JUnit 5, AssertJ, and MockK.
- Originally written in Java, completely converted to Kotlin.

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
