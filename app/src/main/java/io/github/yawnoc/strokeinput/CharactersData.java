/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.strokeinput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.yawnoc.utilities.Stringy;

/*
  A tuple that holds sets of dual, traditional, and simplified characters.
*/
public class CharactersData {
  
  private static final int CODE_POINT_COMMA = ",".codePointAt(0);
  
  private final Set<Integer> dualCodePointSet;
  private final Set<Integer> traditionalCodePointSet;
  private final Set<Integer> simplifiedCodePointSet;
  
  CharactersData(final String commaSeparatedCharacters) {
    
    dualCodePointSet = new HashSet<>();
    traditionalCodePointSet = new HashSet<>();
    simplifiedCodePointSet = new HashSet<>();
    
    Set<Integer> currentCodePointSet = dualCodePointSet;
    for (final int codePoint : Stringy.toCodePointList(commaSeparatedCharacters)) {
      if (codePoint == CODE_POINT_COMMA) {
        if (currentCodePointSet == dualCodePointSet) {
          currentCodePointSet = traditionalCodePointSet;
        }
        else if (currentCodePointSet == traditionalCodePointSet) {
          currentCodePointSet = simplifiedCodePointSet;
        }
      }
      else {
        currentCodePointSet.add(codePoint);
      }
    }
    
  }
  
}
