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
  
  public void addData(final CharactersData charactersData) {
    dualCodePointSet.addAll(charactersData.dualCodePointSet);
    traditionalCodePointSet.addAll(charactersData.traditionalCodePointSet);
    simplifiedCodePointSet.addAll(charactersData.simplifiedCodePointSet);
  }
  
  public List<String> toCandidateList(final boolean traditionalIsPreferred, final Comparator<String> comparator) {
    return toCandidateList(traditionalIsPreferred, comparator, null, Integer.MAX_VALUE);
  }
  
  public List<String> toCandidateList(
    final boolean traditionalIsPreferred,
    final Comparator<String> comparator,
    final Set<String> allowedCharacterSet,
    final int maxCandidateCount
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
      final String dualCharacter = Stringy.toString(dualCodePoint);
      if (allowedCharacterSet == null || allowedCharacterSet.contains(dualCharacter)) {
        preferredList.add(dualCharacter);
      }
    }
    for (final int traditionalCodePoint : traditionalCodePointSet) {
      final String traditionalCharacter = Stringy.toString(traditionalCodePoint);
      if (allowedCharacterSet == null || allowedCharacterSet.contains(traditionalCharacter)) {
        traditionalList.add(traditionalCharacter);
      }
    }
    for (final int simplifiedCodePoint : simplifiedCodePointSet) {
      final String simplifiedCharacter = Stringy.toString(simplifiedCodePoint);
      if (allowedCharacterSet == null || allowedCharacterSet.contains(simplifiedCharacter))
      simplifiedList.add(simplifiedCharacter);
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
