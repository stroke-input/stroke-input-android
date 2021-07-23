/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import java.util.HashMap;
import java.util.Map;

public class Mappy {
  
  public static <V, K> Map<V, K> invertMap(Map<K, V> forwardMap) {
    
    Map<V, K> inverseMap = new HashMap<>();
    for (Map.Entry<K, V> entry : forwardMap.entrySet()) {
      inverseMap.put(entry.getValue(), entry.getKey());
    }
    
    return inverseMap;
  }
  
}
