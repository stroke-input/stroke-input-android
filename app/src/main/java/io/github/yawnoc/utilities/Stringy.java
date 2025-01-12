/*
  Copyright 2021--2023 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public final class Stringy
{
  private Stringy()
  {
    // Do not instantiate
  }

  public static boolean isAscii(final String string)
  {
    return string.matches("\\p{ASCII}*");
  }

  public static String removePrefixRegex(final String prefixRegex, final String string)
  {
    return string.replaceFirst("^" + prefixRegex, "");
  }

  public static String removeSuffixRegex(final String suffixRegex, final String string)
  {
    return string.replaceFirst(suffixRegex + "$", "");
  }

  public static String removePrefix(final String prefix, final String string)
  {
    return removePrefixRegex(Pattern.quote(prefix), string);
  }

  /*
    Get the first (unicode) code point.
  */
  public static int getFirstCodePoint(final String string)
  {
    return string.codePointAt(0);
  }

  /*
    Convert a string to a list of (unicode) code points.
  */
  public static List<Integer> toCodePointList(final String string)
  {
    final List<Integer> codePoints = new ArrayList<>();

    final int charCount = string.length();
    for (int charIndex = 0; charIndex < charCount;)
    {
      final int codePoint = string.codePointAt(charIndex);
      codePoints.add(codePoint);
      charIndex += Character.charCount(codePoint);
    }

    return codePoints;
  }

  /*
    Convert a string to a set of (unicode) code points.
  */
  public static Set<Integer> toCodePointSet(final String string)
  {
    final Set<Integer> codePoints = new HashSet<>();

    final int charCount = string.length();
    for (int charIndex = 0; charIndex < charCount;)
    {
      final int codePoint = string.codePointAt(charIndex);
      codePoints.add(codePoint);
      charIndex += Character.charCount(codePoint);
    }

    return codePoints;
  }

  /*
    Convert a collection of strings to a set of (unicode) code points.
  */
  public static Set<Integer> toCodePointSet(final Collection<String> stringCollection)
  {
    final Set<Integer> codePoints = new HashSet<>();

    for (final String string : stringCollection)
    {
      codePoints.addAll(toCodePointSet(string));
    }

    return codePoints;
  }

  /*
    Convert a code point to a string
  */
  public static String toString(final int codePoint)
  {
    return String.valueOf(Character.toChars(codePoint));
  }

  /*
    Convert a string to a list of (unicode) characters.
  */
  public static List<String> toCharacterList(final String string)
  {
    final List<String> characters = new ArrayList<>();

    final int codePointCount = string.codePointCount(0, string.length());
    for (int codePointIndex = 0; codePointIndex < codePointCount; codePointIndex++)
    {
      final int startIndex = string.offsetByCodePoints(0, codePointIndex);
      final int endIndex = string.offsetByCodePoints(0, codePointIndex + 1);
      final String character = string.substring(startIndex, endIndex);
      characters.add(character);
    }

    return characters;
  }

  /*
    Sunder a string into two at the first occurrence of a delimiter.
  */
  public static String[] sunder(final String string, final String delimiter)
  {
    final int delimiterIndex = string.indexOf(delimiter);
    final int delimiterLength = delimiter.length();

    final String substringBeforeDelimiter;
    final String substringAfterDelimiter;

    if (delimiterIndex < 0)
    {
      substringBeforeDelimiter = string;
      substringAfterDelimiter = "";
    }
    else
    {
      substringBeforeDelimiter = string.substring(0, delimiterIndex);
      substringAfterDelimiter = string.substring(delimiterIndex + delimiterLength);
    }

    return new String[]{substringBeforeDelimiter, substringAfterDelimiter};
  }
}
