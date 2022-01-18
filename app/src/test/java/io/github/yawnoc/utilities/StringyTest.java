/*
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringyTest
{
  private static final int ASCII_START_CODE_POINT = 0x0000;
  private static final int ASCII_END_CODE_POINT = 0x007F;
  
  @Test
  public void isAscii_isCorrect()
  {
    assertTrue(Stringy.isAscii(""));
    assertTrue(Stringy.isAscii("abc 123 #@% +-*/ ,:;.?!"));
    assertTrue(Stringy.isAscii("\"\\"));
    
    final StringBuilder stringBuilder = new StringBuilder(ASCII_END_CODE_POINT - ASCII_START_CODE_POINT + 1);
    for (int codePoint = ASCII_START_CODE_POINT; codePoint <= ASCII_END_CODE_POINT; codePoint++)
    {
      stringBuilder.append(Stringy.toString(codePoint));
    }
    final String fullAsciiString = stringBuilder.toString();
    assertTrue(Stringy.isAscii(fullAsciiString));
    
    assertFalse(Stringy.isAscii(Stringy.toString(ASCII_END_CODE_POINT + 1)));
    assertFalse(Stringy.isAscii(fullAsciiString + "文"));
    assertFalse(Stringy.isAscii("一"));
    assertFalse(Stringy.isAscii("U+FF0C FULLWIDTH COMMA ，"));
  }
  
  @Test
  public void removePrefixRegex_isCorrect()
  {
    assertEquals(Stringy.removePrefixRegex(".", "\n123"), "\n123");
    assertEquals(Stringy.removePrefixRegex("(?s).", "\n123"), "123");
    assertEquals(Stringy.removePrefixRegex("[a-z]+", "abc xyz"), " xyz");
    assertEquals(Stringy.removePrefixRegex("[a-z]+", "123 456"), "123 456");
  }
  
  @Test
  public void toString_isCorrect()
  {
    assertEquals("\0", Stringy.toString(0x0000));
    
    assertEquals("0", Stringy.toString(0x0030));
    assertEquals("!", Stringy.toString(0x0021));
    assertEquals("A", Stringy.toString(0x0041));
    
    assertEquals("〇", Stringy.toString(0x3007));
    assertEquals("㐀", Stringy.toString(0x3400));
    assertEquals("䶵", Stringy.toString(0x4DB5));
    assertEquals("一", Stringy.toString(0x4E00));
    assertEquals("鿐", Stringy.toString(0x9FD0));
    
    assertEquals("\uD840\uDC0B", Stringy.toString(0x2000B)); // 𠀋
    assertEquals("\uD869\uDEB2", Stringy.toString(0x2A6B2)); // 𪚲
    assertEquals("\uD869\uDFDD", Stringy.toString(0x2A7DD)); // 𪟝
    assertEquals("\uD86D\uDEED", Stringy.toString(0x2B6ED)); // 𫛭
    assertEquals("\uD86D\uDF46", Stringy.toString(0x2B746)); // 𫝆
    assertEquals("\uD86E\uDC1C", Stringy.toString(0x2B81C)); // 𫠜
    assertEquals("\uD86E\uDCB8", Stringy.toString(0x2B8B8)); // 𫢸
    assertEquals("\uD873\uDE93", Stringy.toString(0x2CE93)); // 𬺓
  }
}
