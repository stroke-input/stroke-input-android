# Changelog


## [Unreleased]


## [v1.4.3] (69) Cursed be Android 16 (2025-06-29)

### User

- Fixed keyboard failing to spawn in Android 16

### Developer

- Upgraded Android Gradle Plugin to 8.10.1
- Fixed various Gradle-related deprecations
- Replaced `printStackTrace()` with logging
- Replaced `size()` and `length()` comparisons with `isEmpty()` check


## [v1.4.2] (68) 煮者 (2025-01-12)

- Fixed candidate duplication when exact match is also a prefix match (者, 煮)
- Rewrote `addCodePointsToSet` as `toCodePointSet`
- Eliminated suffix Hungarian notation outside method names
- Improved Help > Miscellaneous table
  - Put Function column before Action column in table
  - Moved 'Retract keyboard' explanation into table
- Made minor edits to Privacy Policy
  - English: added parenthetical "(English version)"
  - Chinese: added parenthetical "（中文版）", reduced "不會" to "不", expanded "英文" to "英文版"
- Updated privacy policy button URL anchor for said minor edits


## [v1.4.1] (67) 不鏽鋼 (2024-12-29)

- Updated stroke input data to [Conway Stroke Data v1.34.0]
  - Adds phrases 喼汁, 矮瓜, 蜜棗
  - Adds phrases 縮骨, 縮骨遮
  - Adds phrases 生鏽, 不鏽鋼


## [v1.4.0] (66) Cursed be edge-to-edge (2024-11-04)

- Upgraded Android SDK to Level 35
  - Implemented keyboard edge-to-edge padding (bottom spacer)
  - Opted out of Main Activity edge-to-edge (screw you Google)
- Fixed buggy key preview initialisation in landscape mode
- Changed Help/About link colour to blue


## [v1.3.1] (65) CMD v5.0.0 (2024-10-02)

- Updated links for Conway-Markdown slug rename


## [v1.3.0] (64) 攀燓學覺里黑必 (2024-09-29)

- Updated stroke input data to [Conway Stroke Data v1.33.1]
  - Allows left-first order in 攀燓..., 學覺..., 興釁..., 與舉..., etc.
  - Allows ㇑㇐㇐ instead of ㇐㇑㇐ in 里, 黑, etc.
  - Allows ㇒ second-last instead of last in 必 etc.
  - Adds phrases 審計署 and 統計署 (to complement -處 equivalents)


## [v1.2.10] (63) Cursed be BroadcastReceiver (2024-05-17)

- Updated stroke input data to [Conway Stroke Data v1.32.0]
  - Adds phrases 吖嘛, 拗撬, 詏撬, 揞口費, 幫襯, 搽𢰸, 鏨刺
  - Adds phrases 鐺底, 𥋇眼, 插贓嫁禍, 尋日, 黐鐺, 黐脷根
  - Adds phrases 長氣, 打冷震, 花紙, 家姐, 咁嚌, 腳趾尾, 乞人憎
  - Adds phrases 後尾, 後尾枕, 揦鮓, 垃雜, 老抽, 門棖, 生抽, 話齋
  - Adds phrases 矺住, 矺扁, 矺死, 㜺鬼, 爭在, 扎扎跳, 砧板, 枕住
  - Adds phrases 𠹻味, 𠹻𡃴, 陣間, 執笠, 執碼, 執生, 周時
  - Adds phrases 手㬹, 腳㬹, 鞋㬹
  - Adds phrase 輪候
- Removed DYNAMIC_RECEIVER_NOT_EXPORTED_PERMISSION (screw you Google)
- Upgraded Material Components For Android to 1.12.0


## [v1.2.9] (62) Enter key fix (2024-02-27)

- Fixed enter key to search not working in Google Play Store / crashing Aurora Store


## [v1.2.8] (61) 過海 (2023-12-03)

- Updated stroke input data to [Conway Stroke Data v1.31.0]
  - Fixes incorrect stroke sequence for U+6D77 海
  - Adds phrases 牛肉丸, 牛肉球, 寄艙, 過海
- Upgraded Android SDK to Level 34
- Upgraded Android Gradle Plugin to 8.1.4
- Upgraded Material Components For Android to 1.10.0
- Fixed `DisplayMetrics.scaledDensity` deprecated in API level 34
- Fixed Java "nullability problems" (using `@NonNull`)
- Fixed Java "nullability and data flow problems" (using `Objects.requireNonNull`)


## [v1.2.7] (60) 㨆莊 (2023-11-05)

- Updated stroke input data to [Conway Stroke Data v1.30.0]
  (adds phrases 㨆莊, 過莊, 紅頭十, 伶冧六, 大頭六, 六公, 覺意, 唔覺意, 冧咗, 揩花)
- Improved About Chinese lead sentence wording


## [v1.2.6] (59) 食夾棍 (2023-10-28)

- Updated stroke input data to [Conway Stroke Data v1.29.0]
  (adds phrases 食夾棍, 爆坼)


## [v1.2.5] (58) 肚餓 (2023-10-05)

- Updated stroke input data to [Conway Stroke Data v1.28.0]
  (adds phrase 肚餓)


## [v1.2.4] (57) 詐諦上堂 (2023-09-17)

- Updated stroke input data to [Conway Stroke Data v1.27.0]
  (adds phrases 上堂, 落堂, 詐諦)


## [v1.2.3] (56) 清補涼 (2023-06-27)

- Updated stroke input data to [Conway Stroke Data v1.26.0]
  (adds phrases 清補涼, 芡實, 茨實, 南杏, 北杏, 黨參)


## [v1.2.2] (55) Candidate ease (2023-05-27)

### User

- Stopped ordinary keys (digits and punctuation) committing first candidate.
  Now only space bar, enter, and the candidate buttons themselves
  will commit a candidate
- Increased candidates bar height by 5%
- Increased gutter height under candidates bar from 1px to 4dp
- Made candidate button text/background colours swap on press

### Developer

- Fixed gradle vs wrapper version mismatch
- Fixed missing gradle `distributionSha256Sum`
- Upgraded Material Components For Android to v1.9.0


## [v1.2.1-whitespace] (54) Whitespace changes (2023-05-06)

- Removed trailing whitespace in source Java files


## [v1.2.1] (54) Monochrome icon (2023-04-23)

- Fixed missing monochrome icon (for Android 13 themed icons)
- Clarified Back Gesture vs 3-Button Navigation and Back Button in Help
- Changed to non-breaking space in height multiplier display
- Moved package from Android manifest to build files
- Upgraded GitHub Actions to Node.js 16


## [v1.2.0] (53) Keyboard height adjustment (2023-04-15)

### User

- Added keyboard height adjustment option
- Updated stroke input data to [Conway Stroke Data v1.25.0]
  (adds phrases 即管, 細個, 嗰陣, 嗰陣時, 舊陣時, 侷限)
- Improved Welcome screen layout
  - Applied max width 488dp so that input box isn't overwide in landscape mode
  - Increased spacing between numbered items
- Improved wording of Step 1 (replace 'first time' with 'newly')
- Moved 'Source Code' button to second last
- Added external link icon to 'Source Code' and 'Privacy Policy' buttons
  (to make it obvious that they will spawn a browser)

### Developer

- Upgraded targetSdkVersion to 33
- Upgraded Android Gradle Plugin to 7.4.2
- Upgraded Material Components For Android to v1.8.0
- Split launcher icon paths to fix 'long vector path' warning


## [v1.1.0] (51, 52) 夠鐘 (2023-03-06)

- Updated stroke input data to [Conway Stroke Data v1.24.0]
  (adds phrases 圍裙, 攞位, 揀位, 揀選, 夠鐘, 夠瞓, 唔夠)
- Suppressed `dataExtractionRules` API 31+ warning


## [v1.0.0] (50) First stable (2023-01-29)

- Fixed missing heading rows in Help tables
- Updated stroke input data to [Conway Stroke Data v1.22.0]
  (adds phrases 等埋, 休憩)


## [v0.9.27] (49) Sorted cyanide (2023-01-22)

- Sorted unranked candidates by code point plus CJK Extension Block penalty
  (fixes [Candidate order changes between Android 7 and 8])
- Updated stroke input data to [Conway Stroke Data v1.21.0]
  (adds phrase 山埃)

[Candidate order changes between Android 7 and 8]:
  https://github.com/stroke-input/stroke-input-android/issues/12


## [v0.9.26] (48) Mouth besides (2023-01-15)

- Updated stroke input data to [Conway Stroke Data v1.20.0]
  - Adds characters 𡀔𡅅 (Jyutping-recommended particles)
  - Adds characters 𠮿𠯆𠯢𠯫𠯻𠯿𠰍𠰠𠰴𠰺𠰻𠱼𠲍𠲖𠲵𠲸𠳓𠳔𠳝𠳭𠳿𠴨𠴱𠴲𠵅𠵆𠵌𠵎𠵘𠵱𠵴𠶖𠷈𠸄𠸍𠸖𠸝𠹤𠹵𠹶𠹹𠺕𠺖𠼝𠼦𠼻𠽟𠾐𠾶𡂴𡂿𡃏
  - Adds phrase 指擬
- Updated androidx.test.ext:junit to v1.1.5
- Updated androidx.test.espresso:espresso-core to v3.5.1


## [v0.9.25] (47) 落街插蘇 (2022-12-23)

- Updated stroke input data to [Conway Stroke Data v1.19.0]
  (adds phrases 落街, 插蘇)


## [v0.9.24] (46) 軟熟 (2022-11-20)

- Updated stroke input data to [Conway Stroke Data v1.18.0]
  - Adds phrase 器重
  - Adds phrase 鬥木
  - Adds phrase 軟熟
- Updated androidx.test.ext:junit to v1.1.4
- Updated androidx.test.espresso:espresso-core to v3.5.0


## [v0.9.23] (45) 乾透 (2022-10-30)

- Updated stroke input data to [Conway Stroke Data v1.17.0]
  - Adds phrases 大佬, 大堂, 大會, 大禍, 大蒜, 大難
  - Adds phrases 乾透, 俐落
  - Adds phrases 凡塵, 凡間
  - Adds phrase 幹道
- Upgraded Material Components For Android to v1.7.0


## [v0.9.22] (44) 打喊露 (2022-09-23)

- Updated stroke input data to [Conway Stroke Data v1.16.0]
  - Adds phrases 喊露, 打喊露
  - Adds phrases 七七八八, 七彩
  - Adds phrases 九十後, 九重
  - Adds phrase 了結
  - Adds phrases 二進制, 十進制, 進制
  - Adds phrases 人事部, 人名, 人均
  - Adds phrase 兒科
  - Adds phrases 入伙, 入住, 入味, etc.
  - Adds phrases 刁蠻, 力度, 叉腰
  - Adds phrase 幾點


## [v0.9.21] (43) God save the King (2022-09-11)

- Updated stroke input data to [Conway Stroke Data v1.15.0]
  (adds phrases 保重, 英皇壽辰)
- Updated Android Gradle Plugin to 7.2.2


## [v0.9.20] (42) 早前騙案 (2022-09-04)

- Updated stroke input data to [Conway Stroke Data v1.14.0]
  - Raises ranking of 機
  - Adds phrases 早前, 騙案, 對路
  - Adds phrases 一旦, 一早, 一時, 一條, 一條龍, 一概, 一概而論, 一模一樣, 一貫, 一連, 一點
  - Adds phrases 一年 etc., 一日 etc., 一月 etc., 一號 etc.


## [v0.9.19] (41) Stroke fix for 莉 (2022-08-02)

- Updated stroke input data to [Conway Stroke Data v1.13.1]
  (fixes stroke sequence for U+8389 莉)


## [v0.9.18] (40) 秋葵起樓 (2022-07-31)

- Updated stroke input data to [Conway Stroke Data v1.13.0]
  - Fixes inconsistent leniency for 3rd stroke of 亦-above
  - Fixes missing leniency for last stroke of U+4F5F 佟
  - Fixes missing 衮袞-leniency in U+78D9 磙, U+3665 㙥
  - Fixes missing 青靑-leniency in U+775B 睛
  - Fixes missing 黄黃-leniency in U+9ECB 黋
  - Adds phrases 起屋, 起樓, 秋葵


## [v0.9.17] (39) 拜扵悉數 (2022-07-16)

- Updated stroke input data to [Conway Stroke Data v1.12.0]
  - Allows 4th stroke ㇑ for U+62DC 拜
  - Allows left 才 for U+6275 扵
  - Adds phrases 悉數, 掌上壓
- Upgraded Material Components for Android to 1.6.1


## [v0.9.16] (38) 債主清盤 (2022-07-02)

- Updated stroke input data to [Conway Stroke Data v1.11.0]
  (adds phrases 債主, 清盤)


## [v0.9.15] (37) 屋企直程 (2022-06-10)

- Updated stroke input data to [Conway Stroke Data v1.10.0]
  (adds phrases 屋企, 屋企人, 直程, 直頭, 流感針, 豬流感, 折腰, 緊絀)


## [v0.9.14] (36) Off you go Scott Morrison (2022-05-21)

- Updated stroke input data to [Conway Stroke Data v1.9.0]
  (adds phrases 好在, 好彩)


## [v0.9.13] (35) 暢順彈牙 (2022-05-16)

**NOTE:** v0.9.12 (34) was cancelled
because Conway forgot to add fastlane changelog `34.txt`.
Stupid Conway.


## <s>v0.9.12 (34) 暢順彈牙 (2022-05-16)</s>

### User

- Updated stroke input data to [Conway Stroke Data v1.8.0]
  (adds phrases 暢順, 彈牙, 避彈衣)

### Developer

- Upgraded Material Components for Android to 1.6.0
- Updated CMD to v4 syntax


## [v0.9.11] (33) 較為爆煲 (2022-04-23)

- Updated stroke input data to [Conway Stroke Data v1.7.0]
  (adds phrases 較為, 爆煲)
- Upgraded Android Gradle Plugin to 7.1.3
- Fixed `allowBackup` deprecated in Android 12+ using `dataExtractionRules`


## [v0.9.10] (32) 奇難雜症 (2022-03-26)

**NOTE:** v0.9.9 (31) was cancelled
because Conway forgot to add fastlane changelog `31.txt`.


## <s>v0.9.9 (31) 奇難雜症 (2022-03-26)</s>

- Updated stroke input data to [Conway Stroke Data v1.6.0]
  (adds phrases 奇難雜症, 床下底, 擺低, 放低, 放工, 漏低)
- Upgraded Android Gradle Plugin to 7.1.2


## [v0.9.8] (30) Space bar fix (2022-03-07)

- Fixed space bar for first candidate emitting an extra space


## [v0.9.7] (29) 樹熊侍應 (2022-02-26)

- Updated stroke input data to [Conway Stroke Data v1.5.0]
  (adds phrases 侍應, 樹熊)


## [v0.9.6] (28) Fix for blocked touches (2022-02-13)

### User

- Fixed blocking of touches outside keyboard in API 31 (Android 12) or later
- Added Help section explaining retraction of keyboard with Back Button/Gesture

### Developer

- Upgraded Android Gradle Plugin to 7.1.1
- Upgraded targetSdkVersion to 32


## [v0.9.5] (27) Stroke fixes for 膝 'knee' etc. (2022-02-06)

- Updated stroke input data to [Conway Stroke Data v1.4.1]
  - Fixes incorrect stroke sequences for meat-beside
    in 膒膓膔膕膖膗膘膙膛膜膝膞膟


## [v0.9.4] (26) 安裝功架 (2022-01-31)

- Updated stroke input data to [Conway Stroke Data v1.4.0]
  (adds phrases 功架, 安裝)
- Simplified privacy policy
- Added fastlane (with some translation)

**NOTE:**
F-Droid fastlane changelog `26.txt`
has changes [c23d584a57...v0.9.4] rather than [v0.9.3...v0.9.4]
because there was an issue with fastlane not detecting en-GB,
see <<https://gitlab.com/yawnoc/f-droid-data/-/commit/2fbdc28f6f>>
and <<https://gitlab.com/fdroid/fdroidserver/-/issues/813#note_822427606>>.

[c23d584a57...v0.9.4]:
  https://github.com/stroke-input/stroke-input-android/compare/c23d584a57...v0.9.4
[v0.9.3...v0.9.4]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.3...v0.9.4


## [v0.9.3] (25) Initial store release (2022-01-22)

No code changes. Just some housekeeping for release on Play Store.


## [v0.9.2] (24) Fixes and unit testing (2022-01-21)

### Dependencies

- Updated stroke input data to [Conway Stroke Data v1.3.0]
  (some Extension B dialectal terms)
- Updated Material Components to v1.5.0

### Behaviour

- Moved loading of Stroke Data from `onCreateInputView` to `onCreate`
  (fixes unnecessary reloading when screen is rotated during input)
- Fixed API level 31+ fullscreen not working
  after rotate whilst keyboard showing
- Fixed enter key display text not updating properly
- Wrapped duration debug logs with `if (BuildConfig.DEBUG)`

### Code improvement

- Added unit testing for Mappy, Stringy, and Valuey


## [v0.9.1] (23) Bigger keys and behavioural tweaks (2022-01-17)

### Dependencies

- Updated stroke input data to [Conway Stroke Data v1.2.1]
  (成語動畫廊, HK place names, non-BMP characters in 通用规范汉字表, etc.)

### Behaviour

- Fixed swipe space bar for SYMBOLS_3 keyboard
- Fixed unnecessary overdraw for candidate buttons
- Increased (stroke sequence) prefix match candidate count
- Removed restriction on phrase completion candidate count
- Made backspace clear candidates in Termux and similar apps
- Added fallback alert when openInBrowser fails
- Added fallback alert when WebView fails

### Appearance

- Made candidate order dialog style consistent with overall style
- Fixed initial focus on EditText for API 28+
- Increased candidate button height and font size
- Increased keyboard key heights
- Increased keyboard height max fraction to 0.5
- Increased key preview vertical margin
- Improved About listings order/layout

### Code improvement

- Changed regex prefix removal to literal prefix removal where possible


## [v0.9.0] (22) Phrases complete (2022-01-11)

### Dependencies

- Updated stroke input data to [Conway Stroke Data v1.0.1]
  (officially completes the stroke/phrase data set)
- Updated keyboard font to [Stroke Input Font v2.0.1]
  (name change; adds glyphs for tone markers etc.)

### Layout

- Rearranged placement of symbol keys
- Improved Main Activity layout order
- Added tone marker symbols keyboard
- Added tortoise shell bracket keys (`U+3014` and `U+3015`)
- Added Mainlandia quotation mark keys (`U+2018`, `U+2019`, `U+201C`, `U+201D`)
- Added (non-fullwidth) middle dot key (`U+00B7`)
- Added tone letter keys (`U+02E5` to `U+02E9`)
- Removed useless key `U+`

### Main Activity

- Added stroke sequence examples to Help
- Made minor improvements to About
- Added numbering to Main Activity instructions for clarity
- Added Privacy Policy button to Main Activity

### Gradle

- Migrated from aaptOptions to androidResources
- Bumped Android Gradle Plugin to 7.0.4
- Bumped targetSdkVersion to 31


## [v0.8.0] (21) Beta-ready (2021-11-29)

- Actually implemented stroke input and candidates
  (only the phrase set is incomplete)
- Refactored `InputContainer` god-class into four separate classes
  (`InputContainer`, `StrokeSequenceBar`, `CandidatesView`, `KeyboardView`)
- Updated keyboard font to [Stroke Input Keyboard v1.8.1]
  (adds right-handed magnifying glass)
- Changed search magnifying glass to right-handed


## [v0.7.5] (20) Android 11 fix (2021-08-13)

- Fixed crash on Android 11 (API level 30)


## [v0.7.4] (19) Enter key display (2021-08-11)

- Upgraded Android Gradle Plugin to 7.0.0
- Implemented action-dependent enter-key display
- Updated keyboard font to [Stroke Input Keyboard v1.7.0]
  (adds enter-key action glyphs)


## [v0.7.3] (18) Symbols reshuffle (2021-08-09)

- Reshuffled and changed existing symbols keys
- Added second symbols keyboard for ideographic description characters
- Changed debug mode toggle to long press enter key
- Updated keyboard font to [Stroke Input Keyboard v1.6.1]
  (adds ideographic description characters, horizontal ellipsis,
  and white lenticular brackets, and changes fullwidth space to
  a wide version of the open box glyph)


## [v0.7.2] (17) Clipped previews fix (2021-08-08)

- Limited keyboard height to 50% of screen
- Allocated space above keyboard for candidates bar
- Fixed key previews clipped in Android 9 (API level 28)
- Fixed key previews disappearing on change app
- Fixed key preview vertical position on devices with soft buttons


## [v0.7.1] (16) Key previews shift fix (2021-07-30)

- Fixed key previews not updating on certain shift mode changes


## [v0.7.0] (15) Key previews II (2021-07-30)

- Re-implemented key preview popups
  - Fixed popup lag (noticeable delay before appearing)
  - Fixed popups not appearing when rapidly alternating between keys
  - Eliminated transition animation when moving between different-sized keys
  - Implemented time delay for dismissal of key previews
- Fixed missing logic for touch event cancel action


## [v0.6.4] (14) Key previews (2021-07-24)

- Implemented key preview popups
- Improved organisation of utility classes


## [v0.6.3] (13) App icon (2021-07-23)

- Made Help and About HTML web views zoomable
- Updated keyboard font to [Stroke Input Keyboard v1.5.1]
  (adds private use glyph for 5-strokes logo)
- Changed launcher icon to 5-strokes logo


## [v0.6.2] (12) Code cleanup (2021-07-21)

- Simplified structure of Main Activity layout XML
- Removed unused dependencies (AppCompat, ConstraintLayout)
- Fixed shift pointer not unset on 3 or more pointers
- Moved Keyboard.Row and Keyboard.Key to their own classes


## [v0.6.1] (11) User interface improvements (2021-07-20)

- Updated keyboard font to [Stroke Input Keyboard v1.4.0]
  (adds glyph `U+82F1 英` for localised space bar)
- Added proper localisation (instead of parallel text)
- Added Help to main activity
- Fixed flash of white background when loading About
- Fixed keyboard not persistent on screen rotate or change app
- Made change keyboard go to main rather than symbols
- Made About key (circled information) launch Main Activity
- Implemented extended keys to left/right edge (`a`, `l`, `¢`)


## [v0.6.0] (10) Touch logic rewrite (2021-07-17)

- Rewrote touch logic:
  - Fixed broken logic for second moving pointer
  - Reimplemented shift key logic to allow for:
    - Held mode
    - Move to shift key
    - Move from shift key
- Reset shift mode on change keyboard (unless persistent)
- Added toggleable debug mode showing currently pressed key and active pointer


## [v0.5.2] (9) Dark theme (2021-07-14)

- Implemented dark theme for main activity and About
- Made About show full screen on mobile
- Made About have `max-width` on desktop
- Made stroke keys text colour match theme (umbrella yellow)


## [v0.5.1] (8) Visual improvements (2021-07-13)

- Updated keyboard font to [Stroke Input Keyboard v1.3.1]
  (adds glyphs for qwerty symbols etc.)
- Reduced key label font sizes
- Fixed InputContainer background not transparent
- Fixed keyboard background bounds (fill only the keyboard, not the whole view)


## [v0.5.0] (7) Implemented qwerty (2021-07-12)

- Implemented qwerty keyboard (swipe space bar to switch)
- Implemented horizontal swiping of a key
- Fixed backspace not working in Termux
- Increased backspace repeat speed for ASCII
- Prevented keyboard from showing on main activity startup
- Added Source code (link) and About to main activity
- Improved main activity information text
- Reduced main activity padding sizes


## [v0.4.0] (6) Symbols keyboard and fixes (2021-07-09)

- Added symbols keyboard and implemented switching to it
- Fixed NullPointerException for `onSinglePointerTouchEvent` of null key
- Fixed key press colour change dependent on activity background
- Implemented abort on move outside keyboard (by adding 1 px gutter)
- Updated keyboard font to [Stroke Input Keyboard v1.2.1]
- Improved "switch to symbols" label aesthetics
- Reduced space bar height
- Reduced digit row key height
- Added fillColour attribute for the keyboard as a whole
- Lightened key fill and text colours slightly
- Made parameters/variables final where possible


## [v0.3.0] (5) Basic keyboard behaviour (2021-07-08)

Implemented everything that a normal (non-stroke-input) keyboard should do.

- Improved logic for multiple pointers (e.g. two-thumb typing)
- Implemented extended press behaviour (long presses and key repeats)
- Implemented backspace, space bar, enter key behaviour
- Implemented currently pressed key behaviour and appearance
- Removed key attribute `displayIcon`


## [v0.2.0] (4) Default key behaviour (2021-07-06)

- Implemented default key behaviour (commit `valueText`)
- Updated keyboard font to [Stroke Input Keyboard v1.2.0]
- Cleaned up Java variables
- Reduced key heights
- Reduced key text font sizes
- Styled stroke key text yellow
- Cleaned up `README.md`


## [v0.1.1] (3) New keyboard font (2021-06-29)

- Updated `README.md` links given GitHub organisation move
- Changed keyboard font to [Stroke Input Keyboard v1.1.0]
- Made key text offsets inherit from Row from Keyboard


## [v0.1.0] (2) Implemented drawing (2021-06-27)

This milestone marks the successful re-implementation
of the drawing part of AOSP's `Keyboard.java` and `KeyboardView.java`.
Note that the keyboard is literally a bunch of drawn rectangles;
the actual functionality has not been implemented yet.


[Unreleased]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.4.3...HEAD
[v1.4.3]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.4.2...v1.4.3
[v1.4.2]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.4.1...v1.4.2
[v1.4.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.4.0...v1.4.1
[v1.4.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.3.1...v1.4.0
[v1.3.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.3.0...v1.3.1
[v1.3.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.10...v1.3.0
[v1.2.10]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.9...v1.2.10
[v1.2.9]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.8...v1.2.9
[v1.2.8]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.7...v1.2.8
[v1.2.7]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.6...v1.2.7
[v1.2.6]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.5...v1.2.6
[v1.2.5]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.4...v1.2.5
[v1.2.4]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.3...v1.2.4
[v1.2.3]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.2...v1.2.3
[v1.2.2]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.1-whitespace...v1.2.2
[v1.2.1-whitespace]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.1...v1.2.1-whitespace
[v1.2.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.2.0...v1.2.1
[v1.2.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.1.0...v1.2.0
[v1.1.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v1.0.0...v1.1.0
[v1.0.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.27...v1.0.0
[v0.9.27]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.26...v0.9.27
[v0.9.26]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.25...v0.9.26
[v0.9.25]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.24...v0.9.25
[v0.9.24]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.23...v0.9.24
[v0.9.23]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.22...v0.9.23
[v0.9.22]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.21...v0.9.22
[v0.9.21]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.20...v0.9.21
[v0.9.20]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.19...v0.9.20
[v0.9.19]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.18...v0.9.19
[v0.9.18]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.17...v0.9.18
[v0.9.17]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.16...v0.9.17
[v0.9.16]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.15...v0.9.16
[v0.9.15]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.14...v0.9.15
[v0.9.14]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.13...v0.9.14
[v0.9.13]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.11...v0.9.13
[v0.9.11]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.10...v0.9.11
[v0.9.10]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.8...v0.9.10
[v0.9.8]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.7...v0.9.8
[v0.9.7]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.6...v0.9.7
[v0.9.6]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.5...v0.9.6
[v0.9.5]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.4...v0.9.5
[v0.9.4]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.3...v0.9.4
[v0.9.3]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.2...v0.9.3
[v0.9.2]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.1...v0.9.2
[v0.9.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.0...v0.9.1
[v0.9.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.8.0...v0.9.0
[v0.8.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.7.5...v0.8.0
[v0.7.5]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.7.4...v0.7.5
[v0.7.4]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.7.3...v0.7.4
[v0.7.3]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.7.2...v0.7.3
[v0.7.2]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.7.1...v0.7.2
[v0.7.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.7.0...v0.7.1
[v0.7.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.6.4...v0.7.0
[v0.6.4]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.6.3...v0.6.4
[v0.6.3]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.6.2...v0.6.3
[v0.6.2]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.6.1...v0.6.2
[v0.6.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.6.0...v0.6.1
[v0.6.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.5.2...v0.6.0
[v0.5.2]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.5.1...v0.5.2
[v0.5.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.5.0...v0.5.1
[v0.5.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.4.0...v0.5.0
[v0.4.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.3.0...v0.4.0
[v0.3.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.2.0...v0.3.0
[v0.2.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.1.1...v0.2.0
[v0.1.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.1.0...v0.1.1
[v0.1.0]:
  https://github.com/stroke-input/stroke-input-android/releases/tag/v0.1.0

[Conway Stroke Data v1.34.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.34.0
[Conway Stroke Data v1.33.1]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.33.1
[Conway Stroke Data v1.32.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.32.0
[Conway Stroke Data v1.31.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.31.0
[Conway Stroke Data v1.30.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.30.0
[Conway Stroke Data v1.29.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.29.0
[Conway Stroke Data v1.28.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.28.0
[Conway Stroke Data v1.27.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.27.0
[Conway Stroke Data v1.26.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.26.0
[Conway Stroke Data v1.25.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.25.0
[Conway Stroke Data v1.24.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.24.0
[Conway Stroke Data v1.22.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.22.0
[Conway Stroke Data v1.21.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.21.0
[Conway Stroke Data v1.20.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.20.0
[Conway Stroke Data v1.19.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.19.0
[Conway Stroke Data v1.18.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.18.0
[Conway Stroke Data v1.17.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.17.0
[Conway Stroke Data v1.16.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.16.0
[Conway Stroke Data v1.15.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.15.0
[Conway Stroke Data v1.14.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.14.0
[Conway Stroke Data v1.13.1]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.13.1
[Conway Stroke Data v1.13.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.13.0
[Conway Stroke Data v1.12.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.12.0
[Conway Stroke Data v1.11.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.11.0
[Conway Stroke Data v1.10.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.10.0
[Conway Stroke Data v1.9.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.9.0
[Conway Stroke Data v1.8.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.8.0
[Conway Stroke Data v1.7.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.7.0
[Conway Stroke Data v1.6.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.6.0
[Conway Stroke Data v1.5.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.5.0
[Conway Stroke Data v1.4.1]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.4.1
[Conway Stroke Data v1.4.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.4.0
[Conway Stroke Data v1.3.0]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.3.0
[Conway Stroke Data v1.2.1]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.2.1
[Conway Stroke Data v1.0.1]:
  https://github.com/stroke-input/stroke-input-data/releases/tag/v1.0.1

[Stroke Input Font v2.0.1]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v2.0.1
[Stroke Input Keyboard v1.9.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.9.0
[Stroke Input Keyboard v1.8.1]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.8.1
[Stroke Input Keyboard v1.7.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.7.0
[Stroke Input Keyboard v1.6.1]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.6.1
[Stroke Input Keyboard v1.5.1]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.5.1
[Stroke Input Keyboard v1.4.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.4.0
[Stroke Input Keyboard v1.3.1]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.3.1
[Stroke Input Keyboard v1.2.1]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.2.1
[Stroke Input Keyboard v1.1.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.1.0
[Stroke Input Keyboard v1.2.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.2.0
