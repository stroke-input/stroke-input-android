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
  
}
