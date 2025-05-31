# Kontrol
App for blocking other apps for Android written in Kotlin. <br><br>How it works - you create profile, put there apps you want to block and choose restriction (just to make it harder to edit active profile in future). 
Enable profile, give the app required perfissions - and now it blocks chosen apps!

## Features
- Fetch apps on the device
- Profiles CRUD
- Add and remove apps from the profile
- Choose edit restriction for profile (or requirement to edit profile, ex. enter password)
- Enable/Disable profiles
- Require user to fullfill a specific requirement when they try to edit restricted profile
- Autoclose blocked apps
- Show overlay when user tries to open blocked app

## Screenshots
![Group 1](https://github.com/user-attachments/assets/06944eca-ae03-4d26-bf74-449b883759b4)



## TODO
- Feature: Clear chosen apps in profile
- Modify overlay design
- Add edit restriction types (device reboot, timed pause)
- Pause profiles for specific duration
- App usage stats
- Profile's app limits (ex. allow app usage for 1 hour/day)

## Used technologies & libraries
- Coroutines & Flows
- Jetpack Compose
- Navigation Compose (Type-Safe)
- Room DB
- Dagger & Hilt
- Coil
