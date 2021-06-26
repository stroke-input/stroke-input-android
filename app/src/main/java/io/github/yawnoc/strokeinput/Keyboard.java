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

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.util.Xml;

import java.util.ArrayList;
import java.util.List;

/*
  A container that holds rows of keys, to be declared in a layout XML.
*/
public class Keyboard {
  
  float DEFAULT_KEY_ASPECT_RATIO = 1f;
  float DEFAULT_KEY_WIDTH_PROPORTION = 0.1f;
  int DEFAULT_KEY_HEIGHT_PX = 64;
  
  int DEFAULT_KEY_FILL_COLOUR = Color.BLACK;
  
  // Key properties
  private int defaultKeyWidth;
  private int defaultKeyHeight;
  private int defaultKeyFillColour;
  
  // Keyboard properties
  private int width;
  private int height;
  private ArrayList<Row> rowArrayList = new ArrayList<>();
  private List<Key> keyList;
  
  // Screen properties
  private int screenWidth;
  private int screenHeight;
  
  public Keyboard(Context context, int layoutResourceId) {
    
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    screenWidth = displayMetrics.widthPixels;
    screenHeight = displayMetrics.heightPixels;
    
    defaultKeyWidth = (int) (DEFAULT_KEY_WIDTH_PROPORTION * screenWidth);
    defaultKeyHeight = (int) (DEFAULT_KEY_ASPECT_RATIO * defaultKeyWidth);
    defaultKeyFillColour = DEFAULT_KEY_FILL_COLOUR;
    
    keyList = new ArrayList<>();
    
    loadKeyboard(context, context.getResources().getXml(layoutResourceId));
  }
  
  final void resize(int newWidth, int newHeight) {
    
    int rowCount = rowArrayList.size();
    
    for (int rowIndex = 0; rowIndex < rowCount; ++rowIndex) {
      
      Row currentRow = rowArrayList.get(rowIndex);
      int keyCount = currentRow.keyArrayList.size();
      int totalWidth = 0;
      
      for (int keyIndex = 0; keyIndex < keyCount; ++keyIndex) {
        Key currentKey = currentRow.keyArrayList.get(keyIndex);
        totalWidth += currentKey.width;
      }
      
      if (totalWidth > newWidth) {
        
        int currentX = 0;
        float correctionFactor = (float) newWidth / totalWidth;
        
        for (int keyIndex = 0; keyIndex < keyCount; ++keyIndex) {
          Key currentKey = currentRow.keyArrayList.get(keyIndex);
          currentKey.width *= correctionFactor;
          currentKey.x = currentX;
          currentX += currentKey.width;
        }
      }
    }
    
    width = newWidth;
  }
  
  public List<Key> getKeyList() {
    return keyList;
  }
  
  /*
   A container that holds keys.
  */
  public static class Row {
    
    // Key properties
    public int defaultKeyWidth;
    public int defaultKeyHeight;
    private int defaultKeyFillColour;
    
    // Row properties
    ArrayList<Key> keyArrayList = new ArrayList<>();
    private Keyboard parentKeyboard;
    
    public Row(Keyboard parentKeyboard) {
      this.parentKeyboard = parentKeyboard;
    }
    
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
      defaultKeyFillColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyFillColour,
          parentKeyboard.defaultKeyFillColour
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
    
    // Key styles
    public int keyFillColour;
    
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
  
      keyFillColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyFillColour,
          parentRow.defaultKeyFillColour
        );
      
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
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  private void loadKeyboard(
    Context context,
    XmlResourceParser xmlResourceParser
  )
  {
    boolean currentlyInKey = false;
    boolean currentlyInRow = false;
    
    int currentX = 0;
    int currentY = 0;
    Key currentKey = null;
    Row currentRow = null;
    
    int maximumX = 0;
    int maximumY = 0;
    
    Resources resources = context.getResources();
    
    try {
      int event;
      while (
        (event = xmlResourceParser.next()) != XmlResourceParser.END_DOCUMENT
      )
      {
        switch (event) {
          case XmlResourceParser.START_TAG:
            String currentTag = xmlResourceParser.getName();
            switch (currentTag) {
              case "Keyboard":
                parseKeyboardAttributes(resources, xmlResourceParser);
                break;
              case "Row":
                currentlyInRow = true;
                currentX = 0;
                currentRow = new Row(this, resources, xmlResourceParser);
                rowArrayList.add(currentRow);
                break;
              case "Key":
                currentlyInKey = true;
                currentKey =
                  new Key(
                    currentRow,
                    currentX,
                    currentY,
                    resources,
                    xmlResourceParser
                  );
                keyList.add(currentKey);
                if (currentRow != null) {
                  currentRow.keyArrayList.add(currentKey);
                }
                break;
            }
            break;
          case XmlResourceParser.END_TAG:
            if (currentlyInKey) {
              currentlyInKey = false;
              currentX += currentKey.width;
              maximumX = Math.max(currentX, maximumX);
            }
            else if (currentlyInRow) {
              currentlyInRow = false;
              currentY += currentRow.defaultKeyHeight;
              maximumY = Math.max(currentY, maximumY);
            }
            break;
        }
      }
      width = maximumX;
      height = maximumY;
    }
    catch (Exception exception) {
      Log.e("Keyboard.loadKeyboard", "Exception: " + exception);
      exception.printStackTrace();
    }
  }
  
  private void parseKeyboardAttributes(
    Resources resources,
    XmlResourceParser xmlResourceParser
  )
  {
    TypedArray attributesArray =
      resources.obtainAttributes(
        Xml.asAttributeSet(xmlResourceParser),
        R.styleable.Keyboard
      );
    
    defaultKeyWidth =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyWidth,
        screenWidth,
        (int) (DEFAULT_KEY_WIDTH_PROPORTION * screenWidth)
      );
    defaultKeyHeight =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyHeight,
        screenHeight,
        DEFAULT_KEY_HEIGHT_PX
      );
    
    attributesArray.recycle();
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
