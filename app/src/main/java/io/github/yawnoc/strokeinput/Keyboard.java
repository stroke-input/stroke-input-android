/*
  Copyright 2021 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/
/*
  This is a re-implementation of the deprecated `Keyboard` class,
  which is licensed under the Apache License 2.0,
  see <https://www.apache.org/licenses/LICENSE-2.0.html>.
*/

package io.github.yawnoc.strokeinput;

import java.util.ArrayList;
import java.util.List;

/*
  A container that holds rows of keys, to be declared in a layout XML.
*/
public class Keyboard {
  
  // Key properties
  private int defaultKeyWidth;
  private int defaultKeyHeight;
  
  // Keyboard properties
  private int width;
  private int height;
  private ArrayList<Row> rowArrayList = new ArrayList<Row>();
  private List<Key> keyList;
  
  // Variables for loading the keyboard
  private int currentKeyWidth;
  private int currentKeyHeight;
  
  // Screen properties
  private int screenWidth;
  private int screenHeight;
  
  /*
   A container that holds keys.
  */
  public static class Row {
    
    // Key properties
    public int defaultKeyWidth;
    public int defaultKeyHeight;
    
    // Row properties
    ArrayList<Key> keyArrayList = new ArrayList<>();
    private Keyboard parent;
    
  }
  
  /*
    An individual key.
  */
  public static class Key {
  }
  
}
