# Changelog


## [Unreleased]


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
  https://github.com/stroke-input/stroke-input-android/compare/v0.6.1...HEAD
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
