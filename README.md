# Kontrol

[English](README.md) | [Русский](README.ru.md)

___

Kontrol is an app, that lets you set different restrictions on other apps and view your app usage in your Android device. Kontrol monitors active apps and restricts them by showing an overlay window and (or not) closing the app. For this key function, Accessibility Service and Overlay Window permissions should be granted.

Kontrol operates by using profiles - structures, that contain information about apps it restricts, the restriction itself and profile lock if you want to create some resistance to edit profile. The profile can be in active, inactive or paused state.

App restrictions available:
- Full block
- Password prompt
- Random text prompt

Profile locks available:
- None
- Password prompt
- Random text prompt
- Until certain date and time
- Until device reboot

## App Usage
App usage screen shows a weekly diagram with usage statistics that's always up to date. Choose desired week and day and see how much time you spent in each app!

## Other mentions
- Light & Dark theme
- If the profile is locked and you don't want to fulfill its requirement, you can view profile without being able to edit it
- If the profile is paused, you can change its activation time directly
- App lists in profiles can overlap, but you'll get respective warning about that
- Kontrol doesn't let you bypass restrctions by reinstalling blocked app. Kontrol remembers deleted apps if they were used in any profile
- Blocking overlay will automatically close if you use system navigation (back, home, recents) or open other unrestricted app (ex. Dialer)
- Time/timezone change dynamically changes time on UI and recalculates statistics

## Screenshots
![Group 1](screenshots/all%20screenshots.png)

## Used technologies & libraries
- Kotlin as a programming language
- Kotlin Coroutines
- Jetpack Compose
- Navigation Compose (Type-Safe)
- Room DB
- Dagger & Hilt
- Coil

## Minimum device requirements
- Android version: 5
- RAM: 1 GB
- CPU: 2-core 1 Ghz processor

*Even budget Android 5 phone will run this app*
