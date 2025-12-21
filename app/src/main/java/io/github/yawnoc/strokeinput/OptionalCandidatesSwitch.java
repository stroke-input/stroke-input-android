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
  final String characters;

  public OptionalCandidatesSwitch(Context context, AttributeSet attributes)
  {
    super(context, attributes);

    final String minimumCandidate;
    final String maximumCandidate;
    final String unicodeRange;

    try (final TypedArray attributesArray =
                 context.getTheme().obtainStyledAttributes(attributes, R.styleable.OptionalCandidatesSwitch, 0, 0))
    {
      minimumCandidate = attributesArray.getString(R.styleable.OptionalCandidatesSwitch_minimumCandidate);
      maximumCandidate = attributesArray.getString(R.styleable.OptionalCandidatesSwitch_maximumCandidate);
    }

    if (minimumCandidate == null || maximumCandidate == null)
    {
      characters = "";
      unicodeRange = "";
    }
    else
    {
      final int minimumCodePoint = Stringy.getFirstCodePoint(minimumCandidate);
      final int maximumCodePoint = Stringy.getFirstCodePoint(maximumCandidate);

      final List<String> charactersList = new ArrayList<>();
      for (int codePoint = minimumCodePoint; codePoint <= maximumCodePoint; codePoint++)
      {
        charactersList.add(Stringy.toString(codePoint));
      }

      characters = String.join("", charactersList);

      final String unicodeRangeStart = Stringy.toUnicodeNotation(minimumCodePoint);
      final String unicodeRangeToEnd =
              (maximumCodePoint == minimumCodePoint)
                ? ""
                : "–" + Stringy.toUnicodeNotation(maximumCodePoint); // separated by U+2013 EN DASH

      unicodeRange = unicodeRangeStart + unicodeRangeToEnd;
    }

    final String switchText = unicodeRange + "\n" + characters;

    super.setText(switchText);
  }
}
