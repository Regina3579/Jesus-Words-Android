# How to start the Android version in a new chat

1. Unzip this package. The folder is `JesusWords_Android_Handoff`.
2. Open a new Claude Code chat (or any coding assistant) in an empty folder where the Android project should be created.
3. Attach or copy the whole `JesusWords_Android_Handoff` folder into that folder.
4. Paste the message below.

---

**Message to paste:**

> I want to build the Android version of my live iOS app **Jesus Words** (daily Bible verses in English and Tamil). Everything you need is in the folder `JesusWords_Android_Handoff`:
> - `ANDROID_BUILD_BRIEF.md` — the full specification. Read it completely first and follow it exactly.
> - `data/` — all 365 verses (`jesus_words.json`, English + Tamil), themes, wallpapers and the English↔Tamil UI text.
> - `assets/` — every background image, widget image, tip artwork and the app icon, already converted to Android-friendly WebP names.
> - `ios_reference_source/` — the complete SwiftUI source of the iOS app. Use it as the exact reference for anything the brief doesn't cover.
>
> Build it in **Kotlin + Jetpack Compose**, following the build order in section 16 of the brief. Use applicationId `com.iregina.jesuswords`. Work milestone by milestone. After each milestone, tell me how to run it on my Android phone so I can check it before you continue.
