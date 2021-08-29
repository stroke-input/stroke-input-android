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
  
  public static String removePrefix(
    final String prefixRegex,
    final String string
  )
  {
    return string.replaceFirst("^" + prefixRegex, "");
  }
  
  public static String removeSuffix(
    final String suffixRegex,
    final String string
  )
  {
    return string.replaceFirst(suffixRegex + "$", "");
  }
  
  /*
    Convert a string to a list of (unicode) characters.
  */
  public static List<String> toCharacterList(final String string) {
    
    final List<String> characterList = new ArrayList<>();
    
    final int codePointCount = string.codePointCount(0, string.length());
    for (
      int codePointIndex = 0;
      codePointIndex < codePointCount;
      codePointIndex++
    )
    {
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
