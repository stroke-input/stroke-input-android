<##
  To be compiled with Conway-Markdown (CMD),
  see <https://github.com/conway-markdown/conway-markdown>.
##>

{+ links.txt +}

%%
  %lang zh-Hant-HK
  %title 關於
  %resources <link rel="stylesheet" href="strokes.css">
%%

Z{%
  \{ (?P<ascii_content> [!-~]+?) \}
%
  <span lang="en">\g<ascii_content></span>
%}


# 筆畫輸入法 (\stroke-input-version) #

----
__[筆畫輸入法]__為自由軟件，恕無擔保。
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


## 建件所靠者 ##

### [Android AppCompat Library] (\appcompat-version) ###
----{.notice}
©~2021 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----

### [Android ConstraintLayout] (\constraintlayout-version) ###
----{.notice}
©~2021 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----

### [Material Components For Android] (\material-version) ###
----{.notice}
©~2021 {Android} 開源項目 \+
以 {Apache-2.0} 授權，見\Apache-2.0。 \+
----


## 鍵盤字體 ##

### [Stroke Input Keyboard] (\keyboard-font-version) ###
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
  此頁之 [CMD] 源：[`about-zh-Hant-HK.cmd`]
</footer>


r{%
  (?P<link_definition_opening>
    @@\[ [!-~]+? \]
  )
%
  \g<link_definition_opening>{lang=en}
%}

@@[`about-zh-Hant-HK.cmd`]
  https://github.com/stroke-input/stroke-input-android/\
    blob/master/\
    app/src/main/assets/about-zh-Hant-HK.cmd
@@
