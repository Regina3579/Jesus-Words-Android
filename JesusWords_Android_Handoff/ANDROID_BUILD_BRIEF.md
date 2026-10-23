# Jesus Words — Android Build Brief

**Purpose:** build the Android version of the **Jesus Words** app, which is live on the Apple App Store (iOS version 1.1). It should behave and look like the iOS app.
**Everything needed is in this package:** this brief, the full verse data, all images, and the complete iOS source code as an exact reference.

> **To the AI assistant reading this:** treat this document as the specification. When a detail is not covered here, read the matching file in `ios_reference_source/` (SwiftUI) and reproduce its behaviour. Every number in this brief (sizes, opacities, colours, rules) was taken from that source. SwiftUI `pt` ≈ Android `dp`, and font sizes in `pt` ≈ `sp`.

---

## 0. Package contents

| Path | What it is |
|---|---|
| `ANDROID_BUILD_BRIEF.md` | This specification |
| `data/jesus_words.json` | **All 365 daily verses**, English + Tamil (fields below) |
| `data/themes.json` | 30 themes: colours, fonts, overlays, background image resource |
| `data/wallpapers.json` | 23 share wallpapers with English + Tamil names and subtitles |
| `data/ui_strings_en_ta.json` | English ↔ Tamil UI text pairs extracted from the app |
| `assets/backgrounds/` | 24 background images as `bg_<name>_phone.webp` (portrait) + `bg_<name>_tablet.webp` (≈3:4, for tablets and square crops) |
| `assets/widget/` | 23 small (800 px) widget backgrounds `widget_<designKey>.webp` |
| `assets/tips/` | Transparent artwork for the two first-launch tips |
| `assets/icon/` | `app_icon_1024.png` (launcher/Play icon source), `share_app_icon.png` (used inside shared images) |
| `ios_reference_source/` | Complete SwiftUI source of the iOS app + widget |

The iOS project also lives at GitHub `istalin2020/Claude_Jesus-Words`, branch `claude/jesus-words-app-EQTQi`.

---

## 1. Product summary

A daily devotional app. Every day it shows one comforting **word of Jesus / Bible verse** in **English or Tamil**, on beautiful heavenly backgrounds. Users can:
- switch language;
- choose themes and wallpapers;
- share the verse as an image;
- save favourites;
- look back at history;
- get an optional daily reminder notification;
- add a Home Screen widget.

- **App name (launcher):** Jesus Words
- **Store name:** Jesus Words - New Testament · **Subtitle:** Daily Bible Verses & Blessings · **Category:** Lifestyle
- **iOS bundle id:** `com.iregina.JesusWords` · **Suggested Android applicationId:** `com.iregina.jesuswords`
- **Developer contact / support:** support page `https://istalin2020.github.io/Claude_Jesus-Words/`, email `reginasabina3579@gmail.com`
- **Privacy:** no account, no ads, no analytics, **no data collected**. Everything is stored on the device.
- **Tone:** gentle, reverent, warm. Every verse must deliver comfort, courage, hope or faith. Avoid verses that are only a story setup or a bare command.

---

## 2. Recommended Android stack

- **Kotlin + Jetpack Compose (Material 3)**, single-activity app.
- **minSdk 26, targetSdk 35** (latest stable).
- **DataStore (Preferences)** for all settings, history and favourites (keys in §13).
- **kotlinx.serialization** to read `jesus_words.json` from `res/raw` or `assets/`.
- **AlarmManager** (inexact daily window is fine) or **WorkManager** for the reminder notifications; reschedule on `BOOT_COMPLETED`.
- **Jetpack Glance** for the App Widget.
- **HorizontalPager** for the swipeable main tabs.
- Sharing: render a 1080×1080 bitmap, save it to the cache dir, and share it through `FileProvider` + `Intent.ACTION_SEND` (`image/png`).
- Fonts:
  - English: **Noto Serif**, closest to iOS "New York" (`.serif` design); "rounded" themes use **Nunito** or Noto Sans Rounded.
  - Tamil: **Noto Serif Tamil**, falling back to Noto Sans Tamil.
  - Bundle these as downloadable or embedded fonts so Tamil always renders correctly.
- Icons: iOS uses SF Symbols. Map them to Material Symbols:
  - `house.fill` → Home
  - `clock.fill` → History/Schedule
  - `paintpalette.fill` → Palette
  - `photo.stack.fill` → Photo Library
  - `gearshape.fill` → Settings
  - `heart` / `heart.fill` → Favorite / FavoriteBorder
  - `photo.on.rectangle.angled` → Image
  - `square.and.arrow.up` → Share
  - `bell.fill` → Notifications
  - `lightbulb.fill` → Lightbulb
  - `sparkle(s)` → Auto Awesome
  - `checkmark.circle.fill` → Check Circle
  - for others, pick the nearest match.
- **The Christian cross is NOT a plus sign.** Draw the custom **Latin cross** shape (§6.1) everywhere a cross appears.

---

## 3. Verse data and the daily rule

`data/jesus_words.json` holds an array of 365 objects:

```json
{ "id": 1, "day": 1, "category": "DontBeAfraid",
  "quote": "Peace I leave with you; ...", "reference": "John 14:27", "theme": "Peace Over Fear",
  "tamilQuote": "அமைதியை உங்களுக்கு ...", "tamilTheme": "பயத்தின் மேல் சமாதானம்", "tamilReference": "யோவான் 14:27" }
```

- The Tamil text uses the **திருவிவிலியம்** (Thiruviviliam, the Catholic Tamil Bible) wording. Keep it exactly as given; do not "correct" it.
- **Today's verse:**
  - `index = (dayOfYear − 1) % 365`, where `dayOfYear` is 1-based in the device's local calendar. In a leap year, day 366 wraps to index 0.
  - The same rule is used by the app, the notifications and the widget, so all three must agree.
- **Display:** in Tamil mode show `tamilQuote / tamilTheme / tamilReference`, falling back to English if one is missing.
- **Categories** (8 topics). Use the display names and emoji from `ios_reference_source/JesusWords/Models/JesusWord.swift`:

| key | English | Tamil | emoji |
|---|---|---|---|
| DontBeAfraid | Don't Be Afraid | பயப்படாதே | 🛡️ |
| DontBeSad | Don't Be Sad | கவலைப்படாதே | ☀️ |
| YouCanDoIt | You Can Do It | உன்னால் முடியும் | 💪 |
| IAmThereForYou | I Am There For You | நான் உனக்காக இருக்கிறேன் | 🤲 |
| DontWorry | Don't Worry | கவலைப்படாதே | 🕊️ |
| YouAreBlessed | You Are Blessed | நீ ஆசீர்வதிக்கப்பட்டவன் | ✨ |
| PrayYouWillGet | Pray & Receive | ஜெபி, பெறுவாய் | 🙏 |
| ParableMoral | Parable Wisdom | உவமை ஞானம் | 📖 |

---

## 4. Language

- Two languages: **English** and **Tamil (தமிழ்)**. The choice is persisted and switches **all** UI text, the verse, the notification text and the share image instantly.
- **UI text:** every Tamil string is in `data/ui_strings_en_ta.json` (and in the iOS views). Two sets of strings are not in that file; take them from the source:
  - Tab labels — English `Today, History, Themes, Wallpapers, Settings` / Tamil `இன்று, வரலாறு, தீம்கள், வால்பேப்பர், அமைப்புகள்`.
  - Category names — the table in §3.
- **Exceptions:**
  - The share image's bottom branding is **always English**: "Jesus Words" / "Daily Blessings".
  - The two tip artworks are images with English text baked in.
- Changing language **reschedules the reminder notifications** in the new language.

---

## 5. App flow and navigation

1. **First run → onboarding** (`ReminderSetupView.swift`), shown until `hasCompletedSetup = true`. It has two swipeable pages on a golden-sunrise gradient.
   - **Welcome page:**
     - white Latin cross (70)
     - "Jesus Words" (40 bold serif)
     - "Daily Blessings from the\nNew Testament" (18 serif, 85% white)
     - four feature rows: sun "365 Jesus-spoken words", bell "Optional daily reminders", clock "History of all blessings", share "Share with loved ones"
     - a capsule **Get Started** button (white 25% fill, 50% stroke)
   - **Reminder page:**
     - bell icon
     - "Set Your Reminder"
     - "Choose when you'd like to receive\nyour daily blessing (optional)"
     - time wheel, default 7:00
     - **Enable Reminders** button → asks for notification permission; if granted, saves the time and schedules notifications
     - a plain-text **Skip for Now** button
   - Either button finishes onboarding. **Notifications must stay optional.** Apple rejected the first iOS build because notifications were mandatory; Play has the same expectation.
   - The onboarding gradient is `#FAD95E → #F28C40 → #8C4073`, top-leading to bottom-trailing (day-1 colours of `BackgroundView`).
2. **Main screen:** five pages in a **HorizontalPager** (swipe between them) with a **custom floating tab bar** at the bottom:
   - **Today**
   - **History** (with Favourites)
   - **Themes**
   - **Wallpapers**
   - **Settings**
   - **Tab bar:**
     - rounded rect, radius 25, translucent blur/material with a soft shadow
     - inner padding h10 v12; outer padding h16, bottom 20
     - each item: icon 18 + label 10 medium, single line, may shrink to 70%
     - selected item white; others white 50%
   - Every page draws the **current theme's background** full-screen behind its content (§7).

---

## 6. Screens (exact styling)

### 6.1 Shared visual building blocks
- **Latin cross shape** (`LatinCrossShape.swift`): width:height = 0.55:1.
  - Bar thickness = 35% of width.
  - Crossbar top edge at 22% of height, so the bottom arm is longer.
  - Draw it as a filled polygon.
- **Content card** (used for every card):
  - fill **black 22%** (Today card: **black 18%**, with no blur so the wallpaper shows through)
  - **1 dp white 28% stroke**, corner radius 20–24
  - shadow black 25%, blur 20, y +10
  - Other cards may add a light blur (Android 12+ `RenderEffect`). Without it the dark fill alone is fine.
- **Readability on any background:** white text with shadow black 60%, radius 3 (titles use a double shadow: 60% r2 plus 40% r10).
- **Margins:** cards use 20 dp side margins (16 on Themes/Wallpapers/Settings). The background must always show on the left and right of a card.

### 6.2 Today
Top to bottom, in a vertical scroll:

1. **Language toggle**, top-right; trailing padding 24, top padding 16 so it stays clear of the screen corner.
   - Capsule with two segments, **"En"** and **"த"** (14 bold, padding h14 v7).
   - Selected segment white 28%.
   - Toggle background: blur + **black 45%**, stroke white 55% 1.2 dp, shadow black 35% r6; labels have a text shadow.
2. **Latin cross** (36 high) and the title **"Jesus Words" / "இயேசுவின் வார்த்தைகள்"**, 32 bold in the theme font, double shadow.
3. **Verse card** (padding 28, spacing 16, centred):
   - category badge capsule (emoji + name, 14 semibold, white 25% fill)
   - theme title 18 bold
   - divider 60×2 white 30%
   - quote in curly quotes “…” — 20 medium, line spacing 6, centred
   - reference "— John 14:27", 16 semibold italic, white 85%
   - **Heart button** in the card's top-right corner (24): outline white ↔ filled pink `#FF5C7A`, bounce animation and light haptic; toggles favourite.
   - Card appears with a fade and scale from 0.95 over 0.8 s.
4. **"Share This Blessing" / "இந்த ஆசீர்வாதத்தைப் பகிரு"** capsule button.
   - Image icon + 16 semibold.
   - Blur + **black 38%**, stroke white 55%, shadow.
   - Opens the Share flow (§6.6).

### 6.3 History (+ Favourites)
- Header:
  - "History" / "வரலாறு" (32 bold) with "Your Daily Blessings"; in Favourites mode it reads **"My Favourites" / "பிடித்தவை"** with "Verses you saved, by topic" / "நீங்கள் சேமித்த வசனங்கள்"
  - below it a **switch** capsule: [🕒 History | ♥ Favourites] — selected segment white 90% with dark text; container black 35% with a white 50% stroke.
- **History list:**
  - Every day the app is opened, today's verse is added to the top of the history; at most one entry per day (tracked by `lastShownDay`).
  - **Row card:** padding 16, radius 16; top line: emoji, category (12 semibold), date (12, medium date style), heart (17).
  - Below that: theme 16 bold, quote 14 (3 lines max), reference 13 italic.
  - **Empty state:** book icon, "No history yet" / "Your daily words will appear here".
- **Tapping a row** opens a **detail sheet** on the theme background:
  - close X
  - centred card: emoji 44, category 16, theme 24 bold, divider, quote 22 (line spacing 8), reference 18 italic, "Shown on <long date>" / "<date> அன்று காட்டப்பட்டது"
  - heart in the card corner
  - **"Share as Image" / "படமாகப் பகிரு"** button → Share flow
- **Favourites:** see §10.

### 6.4 Themes
- Header: palette icon, "Themes" / "தீம்கள்", "Choose your visual experience".
- **Current Theme card:** 56 dp circle preview (gradient + icon, or the theme picture) with name 18 bold and subtitle 14.
- **Grid:** 2 columns, spacing 14, side padding 16.
  - **Picture themes first, then the older gradient themes.**
  - Tile: preview area 120 high, corner 16, showing the theme picture (15% dark overlay) or gradient + overlays + light rays, with a large decorative icon and a small cross in the centre.
  - Label strip below: name 13 bold, subtitle 10 at 60%, white 8% fill.
  - Selected tile: white 2.5 dp border + green check badge top-right.
  - Tap: spring "press" scale to 0.92, then select.
- Selection persists and changes every screen's background.

### 6.5 Wallpapers
- Header: "Share Wallpapers" / "பகிர் வால்பேப்பர்கள்", "Choose the background for shared verses".
- "Current Wallpaper" card with a 56 dp thumbnail.
- **2-column grid**, image 160 high (crop to fill), label strip black 50% with name and subtitle (English or Tamil); selected tile has the same white border + green check.
- The selected wallpaper is the **default** for the Share flow.

### 6.6 Share flow (full-screen, black background)
- **Top bar:**
  - X button (36 circle, white 15%)
  - title "Share Blessing" / "ஆசீர்வாதத்தைப் பகிரு" (18 semibold serif)
- **Live preview:**
  - a square, `min(screenWidth − 40, 480)`, corner 24, shadow
  - it is the **real 1080×1080 share layout drawn live and scaled down**, never a cached bitmap
  - **It must update instantly when a wallpaper is tapped.** (The iOS app had a bug where the preview lagged one tap behind.)
- **"Choose Wallpaper" / "வால்பேப்பர் தேர்வு"** horizontal strip:
  - 72×96 thumbnails (corner 10), each with its name (11) below
  - selected thumbnail: white 2.5 border + green check
  - **Each thumbnail must respond only to taps inside its own bounds.** (On iOS, oversized cropped images stole taps from neighbours.)
  - Choosing a wallpaper also saves it as the default.
- **Share / பகிரு** button:
  - full-width capsule, white 25%→15% gradient, stroke white 40%
  - shows a spinner while rendering
  - renders the 1080×1080 image with the **currently selected** wallpaper and opens the system share sheet

### 6.7 Share image (1080×1080 PNG) — `ShareImageView.swift`
- **Background:**
  - With a wallpaper: the picture crop-filled, plus a vertical black gradient 15% → 35% → 15%.
  - Otherwise: the theme gradient + overlays (at 2× size) + light rays (§7).
- **Layout:**
  - side padding 52; vertical padding 5% of the height; gaps 2%
  - **Top band (18%)**, fixed height: cross 72 + "Jesus Words" (or the Tamil title) 56 bold serif, single line, may shrink to 70%.
  - **Card**, fills the middle: black 22%, corner 28, white 8% stroke, padding h40 v16. It contains:
    - badge (emoji 30 + category 28 semibold)
    - theme 48 bold (max 2 lines, may shrink to 50%)
    - divider (70-wide lines with a sparkle)
    - **quote 52 medium italic serif, line spacing 10, shrinks to as little as 30% to fit** (Tamil verses are long)
    - reference 36 semibold italic
  - **Bottom band (13%)**, fixed height: app icon 100×100 (corner 24) + "Jesus Words" 34 bold + "Daily Blessings" 22 at 55% — **always in English**.
- The header and the branding must never be pushed out of the image. **Only the quote shrinks.**

### 6.8 Settings
- Header: "Settings" / "அமைப்புகள்", "Customize your experience".
- **Reminder Time card:**
  - bell + "Reminder Time"
  - time wheel (default 7:00)
  - **"Update Reminder" / "நினைவூட்டலைப் புதுப்பி"** capsule → saves and reschedules; shows "Saved!" / "சேமிக்கப்பட்டது!" in a green tint for 2 s
- **"💡 Show Tips Again" / "குறிப்புகளை மீண்டும் காட்டு"** row (card style with a chevron): resets both tip flags, jumps to Today and replays the tips (§11).
- **About card:**
  - text: "Jesus Words brings you 365 authentic spoken words of Jesus Christ from the New Testament. Start each day with His wisdom, comfort, and encouragement."
  - rows: Total Words 365 · Categories 8 · Source "New Testament (NIV)" · Version (use the Android versionName)

---

## 7. Themes (`data/themes.json`, 30 entries)

- **Fields:**
  - `id`, `name`, `subtitle`
  - `gradientColors` (4 hex colours)
  - `accentColor`
  - `fontDesign` (`serif` | `rounded`)
  - `decorativeIcon_SFSymbol`
  - `overlayElements` (icon, size, opacity, x, y as fractions of the screen, rotationDeg)
  - `backgroundImage` and `androidResource` (non-null for picture themes)
- **Gradient themes** (12, ids 1–12; default **`sunrise_chapel`**):
  - linear gradient top-left → bottom-right
  - each overlay icon placed at (x·width, y·height), white at the given opacity, rotated
  - a big Latin-cross watermark (200 high, white 4%, rotated −15°) in the centre
  - "light rays": 12 lines from (0.75·w, −50) every 30°, white 3%, stroke width 40
- **Picture themes** (18): the image crop-filled full-screen with a black 15% overlay.
  - **Bug to avoid:** on iOS, a scaled-to-fill image widened the whole layout. On Android, draw the background inside a fixed full-size `Box` with `ContentScale.Crop` and clip it so it can never change the size of the content.
- Use the `_tablet` image on tablets and `_phone` on phones.
- **Display order in the grid:** all picture themes (in JSON order), then the gradient themes.

## 8. Wallpapers (`data/wallpapers.json`, 23 entries)

- Fields: `id`, `name`, `tamilName`, `subtitle`, `tamilSubtitle`, `androidResource`.
- Default **`wallpaper_gates_of_heaven`**.
- The first 18 are heavenly or biblical scenes. The last 5 (Little Blessings, Lavender Grace, Friends on High, Smiling Skies, Stacked Joy) are cute pastel designs and are **wallpapers only, not themes**.

---

## 9. Daily reminder notifications (`NotificationManager.swift`)

- **Optional.** Ask for `POST_NOTIFICATIONS` (Android 13+) only when the user taps Enable/Update Reminder.
- Saved time: `reminderHour` / `reminderMinute`.
- **Schedule the next 7 days**, one notification per day, each with **that day's verse** (§3 rule):
  - **title** "✝️ Jesus Words" / "✝️ இயேசுவின் வார்த்தைகள்"
  - **subtitle / summary** the reference
  - **body** the quote (use `BigTextStyle` so long verses show in full), in the current language
- Reschedule (cancel all, then schedule 7 again) when:
  - the app opens;
  - the language changes;
  - the reminder time changes;
  - the phone reboots.
- Tapping a notification opens the app on Today. Clear delivered notifications when the app comes to the foreground (iOS also clears the badge).
- **Known iOS quirk, fix it on Android:** iOS treats hour 0 + minute 0 as "no reminder set". Use an explicit `reminderEnabled` flag instead.

---

## 10. My Favourite Verses

- The heart toggles a verse id in `favoriteVerseIDs`: an ordered list, **newest first**, persisted.
- The Favourites view inside History, **organised by topic** (the verse's category):
  - **Filter chips:** "All · N", then one chip per topic present ("🛡️ Don't Be Afraid · 3"). Selected chip: white 90% with dark text; others black 30% with a white 50% stroke.
  - **Sections in fixed topic order:** DontBeAfraid, DontWorry, DontBeSad, IAmThereForYou, YouCanDoIt, YouAreBlessed, PrayYouWillGet, ParableMoral.
  - Each section header: emoji + topic name (17 bold) + a count badge.
  - The rows are the same row cards as History, without the date.
  - Tapping a row opens the detail sheet without a date line.
  - Un-hearting animates the row out.
  - **Empty state:** heart icon, "No favourites yet" / "இதுவரை பிடித்த வசனங்கள் இல்லை", plus "Tap the ♡ on any verse to save it here, ready whenever you need encouragement." / "எந்த வசனத்திலும் ♡ ஐத் தட்டி இங்கே சேமியுங்கள்"

---

## 11. First-launch tips (spotlight coach marks)

Both tips show **once per install**, in this order: the language tip first, then the Themes tip right after it is dismissed. Flags: `hasSeenLanguageTip`, `hasSeenThemesTip`. "Show Tips Again" in Settings replays both.

**Both tips share this mechanism:**
- full-screen dim of **black 60%** with a **see-through hole** (even-odd path) over the real target;
- a glowing golden stroke around the hole (`#FFD166`-ish, two blurred shadows);
- **taps inside the hole reach the real control underneath**; taps on the art or on the dim dismiss the tip;
- fade in and out over 0.3 s;
- measure the target's on-screen bounds (`onGloballyPositioned`) and position the art from them, so it lines up on every screen size.

**Language tip** — art `assets/tips/tip_language_art.webp`:
- Source art is **1086×1448 px**. A girl angel points with an arrow at an *empty glowing capsule*; the drawn En/த pill was removed so the **real** toggle shows through. The capsule gap's centre is at **(933, 320) px** and its width is **256 px**.
- Hole = the toggle's bounds inflated by 6 dp, capsule-shaped.
- `scale = min(holeWidth / 256, (screenWidth − 12) / 1086)`
- `artLeft = clamp(toggleCenterX − 933·scale, 6, screenWidth − 1086·scale − 6)`
- `artTop = toggleCenterY − 320·scale`
- Delay **0.9 s** after Today appears, and only once the toggle has been measured.

**Themes tip** — art `assets/tips/tip_themes_art.webp`:
- Source art is **881×555 px**: a sleeping cherub on an ornate cream card reading "Explore the **Themes** tab below to discover beautiful new themes for your daily Bible verses", with a gold **Got it** button baked in.
- Width = `min(screenWidth − 16, 440 dp)`, centred horizontally; bottom edge 56 dp above the spotlight.
- The spotlight is a **circle** around the Themes tab button: side = max(width, height) + 16.
- Draw a curved arrow (cream stroke 3.5, rounded caps, golden glow, arrowhead) from art point **(322, 540) px** to the top-centre of the spotlight. Use a quadratic curve with control point `(start.x − 10, end.y − 10)`.
- Tapping the Themes tab through the hole also dismisses the tip.
- Delay 0.6 s after the language tip closes.

---

## 12. Home Screen widget (iOS: WidgetKit → Android: Glance)

- Shows **today's verse** (same §3 rule) and **refreshes at local midnight**: schedule an update for the next midnight, plus `updatePeriodMillis` as a fallback.
- **Sizes:** small (≈2×2), medium (≈4×2), large (≈4×4). iOS also has Lock Screen widgets; Android has none, so skip them.
- **Configuration activity** (opens when the widget is added, and from Reconfigure):
  - **Language:** English / தமிழ் (Tamil)
  - **Design:** **"Daily Surprise (new every day)"** — the default — plus the 23 named designs.
- **Daily Surprise:**
  - Design list order, excluding Daily Surprise (exactly 23): gatesOfHeaven, riverOfLife, goodShepherd, holySpirit, resurrection, treeOfLife, newJerusalem, stillWaters, starOfBethlehem, rainbowPromise, easterDawn, crystalParadise, pentecostLight, sacredHeart, crownOfGlory, nativityStar, roseOfSharon, gardenOfGrace, littleBlessings, lavenderGrace, friendsOnHigh, smilingSkies, stackedJoy.
  - `index = (dayOfYear * 7 + year) % 23`. Consecutive days never repeat, and all 23 appear once per 23 days.
  - Images: `assets/widget/widget_<design>.webp` (800 px, small enough for widget memory limits).
- **Layout:** crop-filled background + vertical black scrim 15% → 35% → 20%; all text white with a shadow.
  - **Small:** cross (10×16), "Jesus Words" (12 bold serif), quote in curly quotes (15 serif, shrinks to fit), reference pill (black 28% capsule).
  - **Medium / large:** header row (cross + title 14 bold + "✨ Today's Blessing" / "இன்றைய ஆசீர்வாதம்" pill at white 22%), quote 17 (large: 22) centred and shrink-to-fit, reference pill.
- Tapping the widget opens the app.

---

## 13. Persisted settings (DataStore keys)

| key | type / default | meaning |
|---|---|---|
| `hasCompletedSetup` | Bool / false | onboarding finished |
| `selectedLanguage` | "english" / "tamil", default english | language (iOS stored the raw strings "English" / "தமிழ்") |
| `selectedThemeId` | String / `sunrise_chapel` | theme |
| `selectedWallpaperId` | String / `wallpaper_gates_of_heaven` | default share wallpaper |
| `reminderEnabled`, `reminderHour`, `reminderMinute` | Bool / Int / Int | reminder (default time 7:00) |
| `history` | list of {verseId, dateShown}, newest first | history |
| `lastShownDay` | date | prevents duplicate history entries per day |
| `favoriteVerseIDs` | List<Int>, newest first | favourites |
| `hasSeenLanguageTip`, `hasSeenThemesTip` | Bool / false | first-launch tips |

---

## 14. Lessons from the iOS build (avoid these bugs)

1. **Picture backgrounds must not affect layout.** Draw them crop-filled in a clipped, fixed-size box (see §7).
2. **Grid tiles only respond inside their own shape.** Cropped image overflow must not steal taps from neighbouring tiles.
3. **The share preview is live**: it is recomposed from the selected wallpaper, never a stale bitmap.
4. **Tamil text is long.** Use shrink-to-fit (auto-size) wherever a verse sits in a fixed box: share image, widget, small cards.
5. **Every control must stay readable on bright and dark backgrounds.** Use dark translucent backplates + white text + text shadow.
6. Keep the language toggle well clear of the screen corners and cut-outs (top 16 dp, end 24 dp, plus window insets).
7. **Notifications are optional.** Never block the app if permission is denied.

---

## 15. Google Play listing (adapted from the App Store copy)

- **Title:** Jesus Words - Daily Bible Verse
- **Short description (≤ 80 chars):** Daily words of Jesus in English & Tamil — widgets, favourites & beautiful themes.
- **Full description:** reuse this text.

```
Begin every day with the words of Jesus.

Jesus Words brings you 365 daily Bible verses — the comforting, encouraging words of Jesus — in English and Tamil. Each morning a new verse reminds you that you are loved, you are not alone, and you need not be afraid.

DAILY BIBLE VERSE
• A new verse every day of the year, chosen to bring peace, courage and hope
• Topics such as Don't Be Afraid, Don't Worry, I Am There For You, You Are Blessed and Pray & Receive
• Switch between English and Tamil (திருவிவிலியம்) with one tap

HOME SCREEN WIDGETS
• See today's verse without opening the app
• A beautiful new background every day, or choose your favourite design

MY FAVOURITE VERSES
• Tap the ♡ to save any verse — organised by topic, so encouragement is always close at hand

BEAUTIFUL THEMES & WALLPAPERS
• 30 themes, including Gates of Heaven, River of Life, Holy Spirit Glow and Nativity Star
• 23 wallpapers for sharing, from peaceful landscapes to gentle pastel designs

SHARE THE BLESSING
• Turn today's verse into a beautiful image and share it on WhatsApp, Instagram and more

GENTLE DAILY REMINDERS
• Optional morning notification with your daily verse, in your chosen language

No account, no ads, no data collection. Your favourites and settings stay private on your device.
```

- **Data safety form:** no data collected or shared.
- **Content rating:** Everyone.

---

## 16. Suggested build order

1. Project setup (Compose, fonts, resources from `assets/` → `res/drawable-nodpi`, data JSON → `res/raw`), the Latin cross, the theme background composable.
2. Data layer: verse repository, the daily rule, DataStore settings.
3. Today screen + language toggle + custom tab bar + HorizontalPager.
4. Themes + Wallpapers screens.
5. History + detail sheet + Favourites.
6. Share image renderer + Share flow + FileProvider sharing.
7. Onboarding + reminder notifications (7-day scheduling, reboot receiver).
8. Glance widget + configuration activity + Daily Surprise.
9. First-launch spotlight tips + "Show Tips Again".
10. Polish: tablets, dark/light backgrounds, Tamil text fitting, Play Store assets and listing.

## 17. Acceptance checklist

- [ ] Today's verse matches `(dayOfYear − 1) % 365` in the app, the notification and the widget.
- [ ] Every screen switches fully between English and Tamil, except the share-image branding and the tip art.
- [ ] All 30 themes and 23 wallpapers display correctly; layout and margins are identical on every theme.
- [ ] Share preview updates instantly; the shared image uses the selected wallpaper; long Tamil verses fit.
- [ ] Favourites persist, group by topic, chips filter, and the empty state shows.
- [ ] Notifications are optional, arrive in the chosen language, and survive a reboot.
- [ ] The widget shows today's verse, changes at midnight, Daily Surprise rotates the background, and the configuration works.
- [ ] Both tips show once in order, point exactly at the real controls, and "Show Tips Again" replays them.
- [ ] No crashes on phones and tablets (portrait); no data leaves the device.
