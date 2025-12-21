/*
  Copyright 2025 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.List;

import io.github.yawnoc.utilities.Stringy;

/*
  A switch for toggling optional candidates.
*/
public class OptionalCandidatesSwitch
  extends SwitchMaterial
{
  final int minimumCodePoint;
  final int maximumCodePoint;

  public OptionalCandidatesSwitch(Context context, AttributeSet attributes)
  {
    super(context, attributes);

    try (final TypedArray attributesArray =
                 context.getTheme().obtainStyledAttributes(attributes, R.styleable.OptionalCandidatesSwitch, 0, 0))
    {
      final String minimumCandidate = attributesArray.getString(R.styleable.OptionalCandidatesSwitch_minimumCandidate);
      final String maximumCandidate = attributesArray.getString(R.styleable.OptionalCandidatesSwitch_maximumCandidate);

      if (minimumCandidate == null || maximumCandidate == null)
      {
        minimumCodePoint = -1;
        maximumCodePoint = -1;
      }
      else
      {
        minimumCodePoint = Stringy.getFirstCodePoint(minimumCandidate);
        maximumCodePoint = Stringy.getFirstCodePoint(maximumCandidate);
      }
    }

    final String rangeTextStart =
            (minimumCodePoint < 0)
              ? ""
              : Stringy.toUnicodeNotation(minimumCodePoint);
    final String rangeTextToEnd =
            (maximumCodePoint == minimumCodePoint)
              ? ""
              : "–" + Stringy.toUnicodeNotation(maximumCodePoint); // separated by U+2013 EN DASH

    final List<String> characters = new ArrayList<>();
    for (int codePoint = minimumCodePoint; codePoint <= maximumCodePoint; codePoint++)
    {
      characters.add(Stringy.toString(codePoint));
    }

    final String rangeText = rangeTextStart + rangeTextToEnd;
    final String charactersText = String.join("", characters);
    final String switchText = rangeText + "\n" + charactersText;

    super.setText(switchText);
  }
}
