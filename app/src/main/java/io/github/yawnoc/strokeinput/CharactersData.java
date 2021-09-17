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
  
  private final Set<Integer> goodlyCodePointSet;
  private final Set<Integer> abominableCodePointSet;
  
  CharactersData(final String commaSeparatedCharacters) {
    
    final String goodlyCharacters;
    final String abominableCharacters;
    
    if (commaSeparatedCharacters == null) {
      goodlyCharacters = "";
      abominableCharacters = "";
    }
    else {
      final String[] sunderedCharactersArray = Stringy.sunder(commaSeparatedCharacters, ",");
      goodlyCharacters = sunderedCharactersArray[0];
      abominableCharacters = sunderedCharactersArray[1];
    }
    
    goodlyCodePointSet = new HashSet<>(Stringy.toCodePointList(goodlyCharacters));
    abominableCodePointSet = new HashSet<>(Stringy.toCodePointList(abominableCharacters));
  }
  
  public void addData(final CharactersData charactersData) {
    goodlyCodePointSet.addAll(charactersData.goodlyCodePointSet);
    abominableCodePointSet.addAll(charactersData.abominableCodePointSet);
  }
  
  public List<String> toCandidateList(final Comparator<String> comparator) {
    return toCandidateList(comparator, Integer.MAX_VALUE);
  }
  
  public List<String> toCandidateList(final Comparator<String> comparator, final int maxCandidateCount) {
    
    final List<String> goodlyList = new ArrayList<>();
    final List<String> abominableList = new ArrayList<>();
    
    for (final int goodlyCodePoint : goodlyCodePointSet) {
      goodlyList.add(Stringy.toString(goodlyCodePoint));
    }
    for (final int abominableCodePoint : abominableCodePointSet) {
      abominableList.add(Stringy.toString(abominableCodePoint));
    }
    
    Collections.sort(goodlyList, comparator);
    Collections.sort(abominableList, comparator);
    
    final List<String> candidateList = new ArrayList<>();
    candidateList.addAll(goodlyList);
    candidateList.addAll(abominableList);
    
    final int candidateCount = Math.min(candidateList.size(), maxCandidateCount);
    
    return new ArrayList<>(candidateList.subList(0, candidateCount));
  }
  
}
