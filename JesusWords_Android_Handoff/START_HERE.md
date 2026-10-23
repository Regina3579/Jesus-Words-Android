# How to start the Android version in a new chat

1. Open a new Claude Code chat connected to the GitHub repository **Regina3579/Jesus-Words-Android**. This handoff folder is already inside it.
   (If you're using the zip instead: unzip it and put the `JesusWords_Android_Handoff` folder in your Android project folder.)
2. Paste the message below.

---

**Message to paste:**

> I want to build the Android version of my live iOS app **Jesus Words** (daily Bible verses in English and Tamil). Everything you need is in the folder `JesusWords_Android_Handoff` in this repository:
> - `ANDROID_BUILD_BRIEF.md` — the full specification. Read it completely first and follow it exactly.
> - `data/` — all 365 verses (`jesus_words.json`, English + Tamil), themes, wallpapers and the English↔Tamil UI text.
> - `assets/` — every background image, widget image, tip artwork and the app icon, already converted to Android-friendly WebP names.
> - `ios_reference_source/` — the complete SwiftUI source of the iOS app. Use it as the exact reference for anything the brief doesn't cover.
>
> Build it in **Kotlin + Jetpack Compose** in this repository, following the build order in section 16 of the brief. Use applicationId `com.iregina.jesuswords`. Work milestone by milestone, commit and push after each one, and tell me how to run it on my Android phone so I can check it before you continue.
