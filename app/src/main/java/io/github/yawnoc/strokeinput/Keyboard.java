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
  
  float DEFAULT_KEY_WIDTH_PROPORTION = 0.1f;
  int DEFAULT_KEY_HEIGHT_DP = 64;
  int default_key_height_px;
  
  int DEFAULT_KEY_FILL_COLOUR = Color.BLACK;
  int DEFAULT_KEY_BORDER_COLOUR = Color.GRAY;
  int DEFAULT_KEY_BORDER_THICKNESS_DP = 2;
  int default_key_border_thickness_px;
  int DEFAULT_KEY_TEXT_COLOUR = Color.WHITE;
  int DEFAULT_KEY_TEXT_SIZE = 48;
  
  // Key properties
  private int keyWidth;
  private int keyHeight;
  private int keyFillColour;
  private int keyBorderColour;
  private int keyBorderThickness;
  private int keyTextColour;
  private int keyTextSize;
  
  // Keyboard properties
  private int width;
  private int height;
  private final List<Key> keyList;
  
  // Screen properties
  private final int screenWidth;
  private final int screenHeight;
  
  public Keyboard(Context context, int layoutResourceId) {
    
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    
    screenWidth = displayMetrics.widthPixels;
    screenHeight = displayMetrics.heightPixels;
    
    default_key_height_px =
      (int) (DEFAULT_KEY_HEIGHT_DP * displayMetrics.density);
    default_key_border_thickness_px =
      (int) (DEFAULT_KEY_BORDER_THICKNESS_DP * displayMetrics.density);
    
    keyList = new ArrayList<>();
    
    loadKeyboard(context, context.getResources().getXml(layoutResourceId));
  }
  
  public List<Key> getKeyList() {
    return keyList;
  }
  
  /*
   A container that holds keys.
  */
  public static class Row {
    
    // Key properties
    public int keyWidth;
    public int keyHeight;
    private final int keyFillColour;
    private final int keyBorderColour;
    private final int keyBorderThickness;
    private final int keyTextColour;
    private final int keyTextSize;
    
    // Row properties
    ArrayList<Key> keyArrayList = new ArrayList<>();
    private final Keyboard parentKeyboard;
    
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
      
      keyWidth =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyWidth,
          parentKeyboard.screenWidth,
          parentKeyboard.keyWidth
        );
      keyHeight =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyHeight,
          parentKeyboard.screenHeight,
          parentKeyboard.keyHeight
        );
      
      keyFillColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyFillColour,
          parentKeyboard.keyFillColour
        );
      keyBorderColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyBorderColour,
          parentKeyboard.keyBorderColour
        );
      keyBorderThickness =
        attributesArray.getDimensionPixelSize(
          R.styleable.Keyboard_keyBorderThickness,
          parentKeyboard.keyBorderThickness
        );
      
      keyTextColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyTextColour,
          parentKeyboard.keyTextColour
        );
      keyTextSize =
        attributesArray.getDimensionPixelSize(
          R.styleable.Keyboard_keyTextSize,
          parentKeyboard.keyTextSize
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
    public int keyBorderColour;
    public int keyBorderThickness;
    public int keyTextColour;
    public int keyTextSize;
    
    // Key dimensions
    public int width;
    public int height;
    
    // Key position
    public int x;
    public int y;
    
    // Key meta-properties
    private final Keyboard grandparentKeyboard;
    
    public Key(Row parentRow) {
      grandparentKeyboard = parentRow.parentKeyboard;
      width = parentRow.keyWidth;
      height = parentRow.keyHeight;
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
          parentRow.keyFillColour
        );
      keyBorderColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyBorderColour,
          parentRow.keyBorderColour
        );
      keyBorderThickness =
        attributesArray.getDimensionPixelSize(
          R.styleable.Keyboard_keyBorderThickness,
          parentRow.keyBorderThickness
        );
      
      keyTextColour =
        attributesArray.getColor(
          R.styleable.Keyboard_keyTextColour,
          parentRow.keyTextColour
        );
      keyTextSize =
        attributesArray.getDimensionPixelSize(
          R.styleable.Keyboard_keyTextSize,
          parentRow.keyTextSize
        );
      
      width =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyWidth,
          grandparentKeyboard.screenWidth,
          parentRow.keyWidth
        );
      height =
        getDimensionOrFraction(
          attributesArray,
          R.styleable.Keyboard_keyHeight,
          grandparentKeyboard.screenHeight,
          parentRow.keyHeight
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
    try {
      
      boolean inKey = false;
      boolean inRow = false;
      
      int x = 0;
      int y = 0;
      Key key = null;
      Row row = null;
      
      int maximumX = 0;
      int maximumY = 0;
      
      Resources resources = context.getResources();
      
      int event;
      
      while (
        (event = xmlResourceParser.next()) != XmlResourceParser.END_DOCUMENT
      )
      {
        switch (event) {
          case XmlResourceParser.START_TAG:
            String xmlTag = xmlResourceParser.getName();
            switch (xmlTag) {
              case "Keyboard":
                parseKeyboardAttributes(resources, xmlResourceParser);
                break;
              case "Row":
                inRow = true;
                x = 0;
                row = new Row(this, resources, xmlResourceParser);
                break;
              case "Key":
                inKey = true;
                key = new Key(row, x, y, resources, xmlResourceParser);
                keyList.add(key);
                if (row != null) {
                  row.keyArrayList.add(key);
                }
                break;
            }
            break;
          case XmlResourceParser.END_TAG:
            if (inKey) {
              inKey = false;
              x += key.width;
              maximumX = Math.max(x, maximumX);
            }
            else if (inRow) {
              inRow = false;
              y += row.keyHeight;
              maximumY = Math.max(y, maximumY);
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
    
    keyWidth =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyWidth,
        screenWidth,
        (int) (DEFAULT_KEY_WIDTH_PROPORTION * screenWidth)
      );
    keyHeight =
      getDimensionOrFraction(
        attributesArray,
        R.styleable.Keyboard_keyHeight,
        screenHeight,
        default_key_height_px
      );
    
    keyFillColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyFillColour,
        DEFAULT_KEY_FILL_COLOUR
      );
    keyBorderColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyBorderColour,
        DEFAULT_KEY_BORDER_COLOUR
      );
    keyBorderThickness =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyBorderThickness,
        default_key_border_thickness_px
      );
    
    keyTextColour =
      attributesArray.getColor(
        R.styleable.Keyboard_keyTextColour,
        DEFAULT_KEY_TEXT_COLOUR
      );
    keyTextSize =
      attributesArray.getDimensionPixelSize(
        R.styleable.Keyboard_keyTextSize,
        DEFAULT_KEY_TEXT_SIZE
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
