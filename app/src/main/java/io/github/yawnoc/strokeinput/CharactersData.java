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
  
  private final Set<String> goodlySet;
  private final Set<String> abominableSet;
  
  CharactersData(final String commaSeparatedCharacters) {
    
    final String goodlyCharacters;
    final String abominableCharacters;
    
    if (commaSeparatedCharacters == null) {
      goodlyCharacters = "";
      abominableCharacters = "";
    }
    else {
      final String[] splitCharactersList = commaSeparatedCharacters.split(",");
      goodlyCharacters = splitCharactersList[0];
      abominableCharacters = (
        splitCharactersList.length > 1
          ? splitCharactersList[1]
          : ""
      );
    }
    
    goodlySet = new HashSet<>();
    abominableSet = new HashSet<>();
    
    goodlySet.addAll(Stringy.toCharacterList(goodlyCharacters));
    abominableSet.addAll(Stringy.toCharacterList(abominableCharacters));
  }
  
  public void addData(final CharactersData charactersData) {
    goodlySet.addAll(charactersData.goodlySet);
    abominableSet.addAll(charactersData.abominableSet);
  }
  
  public List<String> toCandidateList(final Comparator<String> comparator) {
    return toCandidateList(comparator, Integer.MAX_VALUE);
  }
  
  public List<String> toCandidateList(
    final Comparator<String> comparator,
    final int maxCandidateCount
  )
  {
    final List<String> goodlyList = new ArrayList<>(goodlySet);
    final List<String> abominableList = new ArrayList<>(abominableSet);
    
    Collections.sort(goodlyList, comparator);
    Collections.sort(abominableList, comparator);
    
    final List<String> candidateList = new ArrayList<>();
    candidateList.addAll(goodlyList);
    candidateList.addAll(abominableList);
    
    final int candidateCount =
      Math.min(candidateList.size(), maxCandidateCount);
    
    return new ArrayList<>(candidateList.subList(0, candidateCount));
  }
  
}
