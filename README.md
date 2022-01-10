# Stroke Input Method (Chinese keyboard) for Android

A minimalist keyboard for Chinese-language enthusiasts
who want to use the stroke input method (筆畫輸入法).


## License

This repository is licensed under GPL-3.0-only, see [LICENSE].

For detailed copyright information, see [app/src/main/assets/about.html].


## Assets

### `*.cmd` (CMD)

These are [Conway-Markdown (CMD)] files,
used to compile the HTML files by running Conway-Markdown
whilst in the root directory of this repository.

### `*.html` (HTML)

These are the actual About and Help files that get served in the app.

### `*.inc` (Inclusions)

These are files containing common [Conway-Markdown (CMD)]
for inclusion in the CMD files.

### `*.txt` (Text)

These are data files used by the input method.
Taken from Conway Stroke Data ([CC-BY-4.0] / [Public Domain]),
see <<https://github.com/stroke-input/stroke-input-data>>.

### `StrokeInputFont.ttf`

This is the font used for the keyboard.
Taken from Stroke Input Font ([GPL-3.0-only]),
see <<https://github.com/stroke-input/stroke-input-font>>.

### `webview.css`

This is the stylesheet for the HTML files that get served in the app.


[LICENSE]: LICENSE
[GPL-3.0-only]: https://www.gnu.org/licenses/
[CC-BY-4.0]: https://creativecommons.org/licenses/by/4.0/
[Public Domain]: https://creativecommons.org/publicdomain/zero/1.0/
[app/src/main/assets/about.html]:
  https://htmlpreview.github.io/?https://github.com/stroke-input/stroke-input-android/blob/master/app/src/main/assets/about.html
[Conway-Markdown (CMD)]:
  https://github.com/conway-markdown/conway-markdown
