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

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.util.Xml;

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
    private Keyboard parentKeyboard;
    
    public Row(
      Keyboard parentKeyboard,
      Resources resources,
      XmlResourceParser xmlResourceParser
    )
    {
      this.parentKeyboard = parentKeyboard;
      
      TypedArray attributesArray =
        resources.obtainAttributes(
          Xml.asAttributeSet(xmlResourceParser),
          R.styleable.Keyboard
        );
      defaultKeyWidth =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyWidth,
          parentKeyboard.screenWidth,
          parentKeyboard.defaultKeyWidth
        );
      defaultKeyHeight =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyHeight,
          parentKeyboard.screenHeight,
          parentKeyboard.defaultKeyHeight
        );
      attributesArray.recycle();
    }
    
  }
  
  /*
    An individual key.
  */
  public static class Key {
    
    // Key behaviour
    public String valueText;
    public String displayText; // overrides valueText
    public Drawable displayIcon; // overrides displayText
    public boolean isRepeatable;
    
    // Key dimensions
    public int width;
    public int height;
    
    // Key position
    public int x;
    public int y;
    
    // Key meta-properties
    private Keyboard grandparentKeyboard;
    
    public Key(Row parentRow) {
      grandparentKeyboard = parentRow.parentKeyboard;
      width = parentRow.defaultKeyWidth;
      height = parentRow.defaultKeyHeight;
    }
    
    public Key(
      Row parentRow,
      int x,
      int y,
      Resources resources,
      XmlResourceParser xmlResourceParser
    )
    {
      this(parentRow);
      
      this.x = x;
      this.y = y;
  
      TypedArray attributesArray =
        resources.obtainAttributes(
          Xml.asAttributeSet(xmlResourceParser),
          R.styleable.Keyboard
        );
      
      valueText =
        attributesArray.getString(R.styleable.Keyboard_valueText);
      displayText =
        attributesArray.getString(R.styleable.Keyboard_displayText);
      displayIcon =
        attributesArray.getDrawable(R.styleable.Keyboard_displayIcon);
      isRepeatable =
        attributesArray.getBoolean(R.styleable.Keyboard_isRepeatable, false);
      
      width =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyWidth,
          grandparentKeyboard.screenWidth,
          parentRow.defaultKeyWidth
        );
      height =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyHeight,
          grandparentKeyboard.screenHeight,
          parentRow.defaultKeyHeight
        );
      
      attributesArray.recycle();
    }
  }
  
  static int getDimensionOrFraction(
    TypedArray array,
    int attributeIndex,
    int baseValue,
    int defaultValue
  )
  {
    TypedValue value = array.peekValue(attributeIndex);
    if (value == null) {
      return defaultValue;
    }
    switch (value.type) {
      case TypedValue.TYPE_DIMENSION:
        return array.getDimensionPixelOffset(attributeIndex, defaultValue);
      case TypedValue.TYPE_FRACTION:
        return Math.round(
          array.getFraction(attributeIndex, baseValue, baseValue, defaultValue)
        );
      default:
        return defaultValue;
    }
  }
  
}
