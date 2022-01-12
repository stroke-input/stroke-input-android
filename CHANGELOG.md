# Changelog


## [Unreleased]

- Fixed swipe space bar for SYMBOLS_3 keyboard
- Removed restriction on phrase completion candidate count
- Made candidate order dialog style consistent with overall style
- Added fallback alert when openInBrowser fails
- Added fallback alert when WebView fails


## [v0.9.0] Phrases complete (2022-01-11)

- Updated stroke input data to [Conway Stroke Data v1.0.1]
  (officially completes the stroke/phrase data set)
- Updated keyboard font to [Stroke Input Font v2.0.1]
  (name change; adds glyphs for tone markers etc.)

- Rearranged placement of symbol keys
- Improved Main Activity layout order
- Added tone marker symbols keyboard
- Added tortoise shell bracket keys (`U+3014` and `U+3015`)
- Added Mainlandia quotation mark keys (`U+2018`, `U+2019`, `U+201C`, `U+201D`)
- Added (non-fullwidth) middle dot key (`U+00B7`)
- Added tone letter keys (`U+02E5` to `U+02E9`)
- Removed useless key `U+`

- Added stroke sequence examples to Help
- Made minor improvements to About
- Added numbering to Main Activity instructions for clarity
- Added Privacy Policy button to Main Activity

- Migrated from aaptOptions to androidResources
- Bumped Android Gradle Plugin to 7.0.4
- Bumped targetSdkVersion to 31


## [v0.8.0] Beta-ready (2021-11-29)

- Actually implemented stroke input and candidates
  (only the phrase set is incomplete)
- Refactored `InputContainer` god-class into four separate classes
  (`InputContainer`, `StrokeSequenceBar`, `CandidatesView`, `KeyboardView`)
- Updated keyboard font to [Stroke Input Keyboard v1.8.1]
  (adds right-handed magnifying glass)
- Changed search magnifying glass to right-handed


## [v0.7.5] Android 11 fix (2021-08-13)

- Fixed crash on Android 11 (API level 30)


## [v0.7.4] Enter key display (2021-08-11)

- Upgraded Android Gradle Plugin to 7.0.0
- Implemented action-dependent enter-key display
- Updated keyboard font to [Stroke Input Keyboard v1.7.0]
  (adds enter-key action glyphs)


## [v0.7.3] Symbols reshuffle (2021-08-09)

- Reshuffled and changed existing symbols keys
- Added second symbols keyboard for ideographic description characters
- Changed debug mode toggle to long press enter key
- Updated keyboard font to [Stroke Input Keyboard v1.6.1]
  (adds ideographic description characters, horizontal ellipsis,
  and white lenticular brackets, and changes fullwidth space to
  a wide version of the open box glyph)


## [v0.7.2] Clipped previews fix (2021-08-08)

- Limited keyboard height to 50% of screen
- Allocated space above keyboard for candidates bar
- Fixed key previews clipped in Android 9 (API level 28)
- Fixed key previews disappearing on change app
- Fixed key preview vertical position on devices with soft buttons


## [v0.7.1] Key previews shift fix (2021-07-30)

- Fixed key previews not updating on certain shift mode changes


## [v0.7.0] Key previews II (2021-07-30)

- Re-implemented key preview popups
  - Fixed popup lag (noticeable delay before appearing)
  - Fixed popups not appearing when rapidly alternating between keys
  - Eliminated transition animation when moving between different-sized keys
  - Implemented time delay for dismissal of key previews
- Fixed missing logic for touch event cancel action


## [v0.6.4] Key previews (2021-07-24)

- Implemented key preview popups
- Improved organisation of utility classes


## [v0.6.3] App icon (2021-07-23)

- Made Help and About HTML web views zoomable
- Updated keyboard font to [Stroke Input Keyboard v1.5.1]
  (adds private use glyph for 5-strokes logo)
- Changed launcher icon to 5-strokes logo


## [v0.6.2] Code cleanup (2021-07-21)

- Simplified structure of Main Activity layout XML
- Removed unused dependencies (AppCompat, ConstraintLayout)
- Fixed shift pointer not unset on 3 or more pointers
- Moved Keyboard.Row and Keyboard.Key to their own classes


## [v0.6.1] User interface improvements (2021-07-20)

- Updated keyboard font to [Stroke Input Keyboard v1.4.0]
  (adds glyph `U+82F1 英` for localised space bar)
- Added proper localisation (instead of parallel text)
- Added Help to main activity
- Fixed flash of white background when loading About
- Fixed keyboard not persistent on screen rotate or change app
- Made change keyboard go to main rather than symbols
- Made About key (circled information) launch Main Activity
- Implemented extended keys to left/right edge (`a`, `l`, `¢`)


## [v0.6.0] Touch logic rewrite (2021-07-17)

- Rewrote touch logic:
  - Fixed broken logic for second moving pointer
  - Reimplemented shift key logic to allow for:
    - Held mode
    - Move to shift key
    - Move from shift key
- Reset shift mode on change keyboard (unless persistent)
- Added toggleable debug mode showing currently pressed key and active pointer


## [v0.5.2] Dark theme (2021-07-14)

- Implemented dark theme for main activity and About
- Made About show full screen on mobile
- Made About have `max-width` on desktop
- Made stroke keys text colour match theme (umbrella yellow)


## [v0.5.1] Visual improvements (2021-07-13)

- Updated keyboard font to [Stroke Input Keyboard v1.3.1]
  (adds glyphs for qwerty symbols etc.)
- Reduced key label font sizes
- Fixed InputContainer background not transparent
- Fixed keyboard background bounds (fill only the keyboard, not the whole view)


## [v0.5.0] Implemented qwerty (2021-07-12)

- Implemented qwerty keyboard (swipe space bar to switch)
- Implemented horizontal swiping of a key
- Fixed backspace not working in Termux
- Increased backspace repeat speed for ASCII
- Prevented keyboard from showing on main activity startup
- Added Source code (link) and About to main activity
- Improved main activity information text
- Reduced main activity padding sizes


## [v0.4.0] Symbols keyboard and fixes (2021-07-09)

- Added symbols keyboard and implemented switching to it
- Fixed NullPointerException for `onSinglePointerTouchEvent` of null key
- Fixed key press colour change dependent on activity background
- Implemented abort on move outside keyboard (by adding 1 px gutter)
- Update keyboard font to [Stroke Input Keyboard v1.2.1]
- Improved "switch to symbols" label aesthetics
- Reduced space bar height
- Reduced digit row key height
- Added fillColour attribute for the keyboard as a whole
- Lightened key fill and text colours slightly
- Made parameters/variables final where possible


## [v0.3.0] Basic keyboard behaviour (2021-07-08)

Implemented everything that a normal (non-stroke-input) keyboard should do.

- Improved logic for multiple pointers (e.g. two-thumb typing)
- Implemented extended press behaviour (long presses and key repeats)
- Implemented backspace, space bar, enter key behaviour
- Implemented currently pressed key behaviour and appearance
- Removed key attribute `displayIcon`


## [v0.2.0] Default key behaviour (2021-07-06)

- Implemented default key behaviour (commit `valueText`)
- Updated keyboard font to [Stroke Input Keyboard v1.2.0]
- Cleaned up Java variables
- Reduced key heights
- Reduced key text font sizes
- Styled stroke key text yellow
- Cleaned up `README.md`


## [v0.1.1] New keyboard font (2021-06-29)

- Updated `README.md` links given GitHub organisation move
- Changed keyboard font to [Stroke Input Keyboard v1.1.0]
- Made key text offsets inherit from Row from Keyboard


## [v0.1.0] Implemented drawing (2021-06-27)

This milestone marks the successful re-implementation
of the drawing part of AOSP's `Keyboard.java` and `KeyboardView.java`.
Note that the keyboard is literally a bunch of drawn rectangles;
the actual functionality has not been implemented yet.


[Unreleased]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.9.0...HEAD
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
