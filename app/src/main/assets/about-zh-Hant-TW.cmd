<##
  To be compiled with Conway-Markdown (CMD),
  see <https://github.com/conway-markdown/conway-markdown>.
##>
%%
  %lang zh-Hant-TW
  %title 關於
  %resources <link rel="stylesheet" href="dark.css">
%%

Z{%
  \{ (?P<ascii_content> [!-~]+?) \}
%
  <span lang="en">\g<ascii_content></span>
%}


# 筆畫輸入法 (v0.6.0) #

----
__[筆畫輸入法]__為自由軟體，恕無擔保。
----
----{.notice}
©~2021~{Conway} \+
以 {GPL-3.0-only} 授權，見\GPL-3.0-only。 \+
----
----
當中含有從廢類 [`Keyboard.java`]、[`KeyboardView.java`] 所抄之位元組，即：
----
----{.notice}
©~2020 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----


## 建體所靠者 ##

### [Android AppCompat Library] (v1.3.0) ###
----{.notice}
©~2021 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----

### [Android ConstraintLayout] (v2.0.4) ###
----{.notice}
©~2021 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----

### [Material Components For Android] (v1.4.0) ###
----{.notice}
©~2021 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----


## 鍵盤字體 ##

### [Stroke Input Keyboard] (v1.3.1) ###
----{.notice}
©~2021 Conway \+
以 {GPL-3.0-only} 授權，見\GPL-3.0-only。 \+
----
----
修改自 [2015 版 <span class="en">Noto Sans CJK TC</span>]，即：
----
----{.notice}
©~2015 谷歌、等 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----


<footer>
  此頁之 [CMD] 源：[`about-zh-Hant-TW.cmd`]
</footer>


r{%
  (?P<link_definition_opening>
    @@\[ [!-~]+? \]
  )
%
  \g<link_definition_opening>{lang=en}
%}

@@[筆畫輸入法]
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
@@[2015 版 <span class="en">Noto Sans CJK TC</span>]
  https://github.com/googlefonts/noto-cjk/\
    tree/2663656870e92c0dcbe891590681815ebb509c05
@@

@@[CMD]
  https://github.com/conway-markdown/conway-markdown
@@
@@[`about-zh-Hant-TW.cmd`]
  https://github.com/stroke-input/stroke-input-android/\
    blob/master/\
    app/src/main/assets/about-zh-Hant-TW.cmd
@@

{: \GPL-3.0-only : b<https://www.gnu.org/licenses/> :}
{: \Apache-2.0 : b<https://www.apache.org/licenses/LICENSE-2.0.html> :}
