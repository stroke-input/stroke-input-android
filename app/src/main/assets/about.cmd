<##
  To be compiled with Conway-Markdown (CMD),
  see <https://github.com/conway-markdown/conway-markdown>.
##>
%%
  %title About
  %css
    body {
      font-family: sans-serif;
      padding: 0.5em;
    }
    h1 {
      font-size: 1.7em;
      margin-top: 0.3em;
    }
    h2 {
      font-size: 1.4em;
      margin-top: 1.5em;
    }
    h3 {
      font-size: 1.1em;
    }
    .notice {
      border: 1px solid black;
      padding: 0.3em;
    }
%%


# Stroke Input Method (0.4.0) #

----{.notice}
©~2021~Conway \+
Licensed under GPL-3.0-only, see \GPL-3.0-only. \+
----
----
Contains bytes copied from the deprecated
[`Keyboard.java`] and [`KeyboardView.java`],
which are:
----
----{.notice}
©~2020 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----


## Build dependencies ##

### [Android AppCompat Library] (1.3.0) ###
----{.notice}
©~2021 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----

### [Android ConstraintLayout] (2.0.4) ###
----{.notice}
©~2021 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----

### [Material Components For Android] (1.4.0) ###
----{.notice}
©~2021 The Android Open Source Project \+
Licensed under Apache-2.0, see \Apache-2.0. \+
----


<## Deprecated classes ##>
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

<## Build dependencies ##>
@@[Android AppCompat Library]
  https://mvnrepository.com/artifact/androidx.appcompat/appcompat
@@
@@[Android ConstraintLayout]
  https://mvnrepository.com/artifact/androidx.constraintlayout/constraintlayout
@@
@@[Material Components For Android]
  https://mvnrepository.com/artifact/com.google.android.material/material
@@

<## Licenses ##>
{: \GPL-3.0-only : b<https://www.gnu.org/licenses/> :}
{: \Apache-2.0 : b<https://www.apache.org/licenses/LICENSE-2.0.html> :}
