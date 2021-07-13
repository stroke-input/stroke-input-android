<##
  To be compiled with Conway-Markdown (CMD),
  see <https://github.com/conway-markdown/conway-markdown>.
##>
%%
  %title About
  %css
    {: @color/umbrella_orange : #FFCE73 :}
    {: @color/key_fill : #181818 :}
    body {
      background: @color/key_fill;
      color: white;
      font-family: sans-serif;
      font-size: 15px;
      margin: 0;
      overflow-wrap: break-word;
      padding: 1em;
    }
    a {
      color: @color/umbrella_orange;
    }
    footer {
      border-top: 2px solid white;
      margin-top: 1.5em;
      padding-top: 0.75em;
    }
    h1 {
      font-size: 1.6em;
      margin-top: 0.3em;
    }
    h2 {
      font-size: 1.4em;
      margin-top: 1.5em;
    }
    h3 {
      font-size: 1.1em;
    }
    html {
      background: grey;
      margin: 0 auto;
      max-width: 44em;
    }
    .notice {
      border: 1px solid white;
      padding: 0.5em;
    }
%%


# Stroke Input Method (v0.5.1) #

----
__[Stroke Input Method (<span class="zh-Hant">筆畫輸入法</span>)][stroke]__
is free software with NO WARRANTY etc. etc.
----
----{.notice}
©~2021~Conway \+
Licensed under GPL-3.0-only, see \GPL-3.0-only. \+
----
----
It contains bytes copied from the deprecated classes
[`Keyboard.java`] and [`KeyboardView.java`],
which are:
----
----{.notice}
©~2020 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----


## Build dependencies ##

### [Android AppCompat Library] (v1.3.0) ###
----{.notice}
©~2021 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----

### [Android ConstraintLayout] (v2.0.4) ###
----{.notice}
©~2021 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----

### [Material Components For Android] (v1.4.0) ###
----{.notice}
©~2021 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----


## Keyboard font ##

### [Stroke Input Keyboard] (v1.3.1) ###
----{.notice}
©~2021 Conway \+
Licensed under GPL-3.0-only, see \GPL-3.0-only. \+
----
----
Modified from a [2015 version of Noto Sans CJK TC],
which is:
----
----{.notice}
©~2015 Google and others \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----


<footer>
  This page's [CMD] source: [`about.cmd`]
</footer>


@@[stroke]
  https://github.com/stroke-input/stroke-input-android
@@

@@[`Keyboard.java`]
  https://android.googlesource.com/platform/frameworks/base/+/\
    33f921769531968a3ba9bc73fb2410f95868cb8d/\
    core/java/android/inputmethodservice/Keyboard.java
@@
@@[`KeyboardView.java`]
  https://android.googlesource.com/platform/frameworks/base/+/\
    33f921769531968a3ba9bc73fb2410f95868cb8d/\
    core/java/android/inputmethodservice/KeyboardView.java
@@

@@[Android AppCompat Library]
  https://mvnrepository.com/artifact/androidx.appcompat/appcompat
@@
@@[Android ConstraintLayout]
  https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
@@
@@[Material Components For Android]
  https://mvnrepository.com/artifact/com.google.android.material/material
@@

@@[Stroke Input Keyboard]
  https://github.com/stroke-input/stroke-input-font
@@
@@[2015 version of Noto Sans CJK TC]
  https://github.com/googlefonts/noto-cjk/\
    tree/2663656870e92c0dcbe891590681815ebb509c05
@@

@@[CMD]
  https://github.com/conway-markdown/conway-markdown
@@
@@[`about.cmd`]
  https://github.com/stroke-input/stroke-input-android/\
    blob/master/\
    app/src/main/assets/about.cmd
@@

{: \GPL-3.0-only : b<https://www.gnu.org/licenses/> :}
{: \Apache-2.0 : b<https://www.apache.org/licenses/LICENSE-2.0.html> :}
