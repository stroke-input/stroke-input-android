< links.cmdr
< keys.cmdr
< typography.cmdr

OrdinaryDictionaryReplacement: #.boilerplate-properties-override
- queue_position: BEFORE #boilerplate-properties
- apply_mode: SIMULTANEOUS
* %title --> Help

%%%

@LINK_DEFINITIONS

# %title


## Strokes

''''
|^
//
  ; Key
  ; Stroke
|:
//
  , <kbd class="stroke-key" lang="zh-Hant">\stroke-1</kbd>
  , horizontal; raise
//
  , <kbd class="stroke-key" lang="zh-Hant">\stroke-2</kbd>
  , vertical; vertical-with-hook
//
  , <kbd class="stroke-key" lang="zh-Hant">\stroke-3</kbd>
  , left-slash
//
  , <kbd class="stroke-key" lang="zh-Hant">\stroke-4</kbd>
  , right-press; dot
//
  , <kbd class="stroke-key" lang="zh-Hant">\stroke-5</kbd>
  , turn; bend
''''


## Examples

''''
|^
//
  ; Character
  ; Stroke sequence
|:{lang=zh-Hant}
//
  , 天
  ,{.stroke-key}
    \stroke-1\stroke-1\stroke-3\stroke-4
//
  , 下
  ,{.stroke-key}
    \stroke-1\stroke-2\stroke-4
//
  , 為
  ,{.stroke-key}
    \stroke-4\stroke-3\stroke-5\stroke-5\stroke-5\
    \stroke-4\stroke-4\stroke-4\stroke-4
//
  , 公
  ,{.stroke-key}
    \stroke-3\stroke-4\stroke-5\stroke-4
''''


## Miscellaneous

''''
|^
//
  ; Function
  ; Action
|:
//
  , Change language
  , Horizontally swipe \space-bar-en
//
  , Change to other keyboard
  , Long press \space-bar-en
''''


## Retract keyboard

----
Use the system __Back Button__ (or __Back Gesture__<sup>†</sup>) to retract the keyboard.
----
----
(<sup>†</sup>If Android~13+ __Gesture Navigation__ is inconvenient,
consider reverting to __3-Button Navigation__ and using the __Back Button__.)
----


<footer>
  This page's [CMD] source: [cmd-source]
</footer>
