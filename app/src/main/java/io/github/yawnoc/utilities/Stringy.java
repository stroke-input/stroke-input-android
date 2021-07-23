/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

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
  
}
