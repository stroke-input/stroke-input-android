/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import java.util.ArrayList;
import java.util.List;

public class Stringy {
  
  private Stringy() {
    // Do not instantiate
  }
  
  public static boolean isAscii(final String string) {
    return string.matches("\\p{ASCII}*");
  }
  
  /*
    Whether the first (unicode) character of a string
    lies in the Basic Multilingual Plane (U+0000 to U+FFFF).
  */
  public static boolean firstCharacterIsBasic(final String string) {
    
    if (string.length() == 0) {
      return true;
    }
    
    final char firstChar = string.charAt(0);
    return firstChar < Character.MIN_SURROGATE || firstChar > Character.MAX_SURROGATE;
  }
  
  public static String removePrefix(final String prefixRegex, final String string) {
    return string.replaceFirst("^" + prefixRegex, "");
  }
  
  public static String removeSuffix(final String suffixRegex, final String string) {
    return string.replaceFirst(suffixRegex + "$", "");
  }
  
  /*
    Get the first (unicode) character.
  */
  public static String getFirstCharacter(final String string) {
    return
      string.substring(
        string.offsetByCodePoints(0, 0),
        string.offsetByCodePoints(0, 1)
      );
  }
  
  /*
    Convert a string to a list of (unicode) code points
  */
  public static List<Integer> toCodepointList(final String string) {
    
    final List<Integer> codePointList = new ArrayList<>();
    
    final int charCount = string.length();
    for (int charIndex = 0; charIndex < charCount; charIndex++) {
      codePointList.add(string.codePointAt(charIndex));
    }
    
    return codePointList;
  }
  
  /*
    Convert a code point to a string
  */
  public static String toString(final int codePoint) {
    return String.valueOf(Character.toChars(codePoint));
  }
  
  /*
    Convert a string to a list of (unicode) characters.
  */
  public static List<String> toCharacterList(final String string) {
    
    final List<String> characterList = new ArrayList<>();
    
    final int codePointCount = string.codePointCount(0, string.length());
    for (int codePointIndex = 0; codePointIndex < codePointCount; codePointIndex++) {
      characterList.add(
        string.substring(
          string.offsetByCodePoints(0, codePointIndex),
          string.offsetByCodePoints(0, codePointIndex + 1)
        )
      );
    }
    
    return characterList;
  }
  
  /*
    Sunder a string into two at the first occurrence of a delimiter.
  */
  public static String[] sunder(final String string, final String delimiter) {
    
    final int delimiterIndex = string.indexOf(delimiter);
    
    final String substringBeforeDelimiter;
    final String substringAfterDelimiter;
    
    if (delimiterIndex < 0) {
      substringBeforeDelimiter = string;
      substringAfterDelimiter = "";
    }
    else {
      substringBeforeDelimiter = string.substring(0, delimiterIndex);
      substringAfterDelimiter = string.substring(delimiterIndex + 1);
    }
    
    return new String[]{substringBeforeDelimiter, substringAfterDelimiter};
  }
}
