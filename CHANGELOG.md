# Changelog


## [Unreleased]

- Added symbols keyboard and implemented switching to it
- Fixed NullPointerException for `onSinglePointerTouchEvent` of null key
- Fixed key press colour change dependent on activity background
- Implemented abort on move outside keyboard (by adding 1 px gutter)
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

This milestone marks the successful re-implmentation
of the drawing part of AOSP's `Keyboard.java` and `KeyboardView.java`.
Note that the keyboard is literally a bunch of drawn rectangles;
the actual functionality has not been implemented yet.


[Unreleased]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.3.0...HEAD
[v0.3.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.2.0...v0.3.0
[v0.2.0]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.1.1...v0.2.0
[v0.1.1]:
  https://github.com/stroke-input/stroke-input-android/compare/v0.1.0...v0.1.1
[v0.1.0]:
  https://github.com/stroke-input/stroke-input-android/releases/tag/v0.1.0

[Stroke Input Keyboard v1.1.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.1.0
[Stroke Input Keyboard v1.2.0]:
  https://github.com/stroke-input/stroke-input-font/releases/tag/v1.2.0
