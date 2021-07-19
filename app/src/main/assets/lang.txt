r{%
  (?P<link_definition_opening>
    @@\[ [!-~]+? \]
  )
%
  \g<link_definition_opening>{lang=en}
%}

Z{%
  \{ (?P<ascii_content> [!-~]+?) \}
%
  <span lang="en">\g<ascii_content></span>
%}
