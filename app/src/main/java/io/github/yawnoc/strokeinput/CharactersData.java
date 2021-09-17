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
  A tuple that holds sets of goodly and abominable characters.
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
  
  public void addData(final CharactersData charactersData) {
    dualCodePointSet.addAll(charactersData.dualCodePointSet);
    traditionalCodePointSet.addAll(charactersData.traditionalCodePointSet);
    simplifiedCodePointSet.addAll(charactersData.simplifiedCodePointSet);
  }
  
  public List<String> toCandidateList(final Comparator<String> comparator) {
    return toCandidateList(comparator, Integer.MAX_VALUE);
  }
  
  public List<String> toCandidateList(final Comparator<String> comparator, final int maxCandidateCount) {
    return toCandidateList(comparator, maxCandidateCount, true);
  }
  
  public List<String> toCandidateList(
    final Comparator<String> comparator,
    final int maxCandidateCount,
    final boolean traditionalIsPreferred
  )
  {
    final List<String> preferredList = new ArrayList<>();
    final List<String> dislikedList = new ArrayList<>();
    
    final List<String> traditionalList;
    final List<String> simplifiedList;
    if (traditionalIsPreferred) {
      traditionalList = preferredList;
      simplifiedList = dislikedList;
    }
    else {
      traditionalList = dislikedList;
      simplifiedList = preferredList;
    }
    
    for (final int dualCodePoint : dualCodePointSet) {
      preferredList.add(Stringy.toString(dualCodePoint));
    }
    for (final int traditionalCodePoint : traditionalCodePointSet) {
      traditionalList.add(Stringy.toString(traditionalCodePoint));
    }
    for (final int simplifiedCodePoint : simplifiedCodePointSet) {
      simplifiedList.add(Stringy.toString(simplifiedCodePoint));
    }
    
    Collections.sort(preferredList, comparator);
    Collections.sort(dislikedList, comparator);
    
    final List<String> candidateList = new ArrayList<>();
    candidateList.addAll(preferredList);
    candidateList.addAll(dislikedList);
    
    final int candidateCount = Math.min(candidateList.size(), maxCandidateCount);
    
    return new ArrayList<>(candidateList.subList(0, candidateCount));
  }
  
}
