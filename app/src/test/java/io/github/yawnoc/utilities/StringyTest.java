/*
  Copyright 2022 Conway
  Licensed under the GNU General Public License v3.0 (GPL-3.0-only).
  This is free software with NO WARRANTY etc. etc.,
  see LICENSE or <https://www.gnu.org/licenses/>.
*/

package io.github.yawnoc.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringyTest
{
  private static final int ASCII_CODE_POINT_START = 0x0000;
  private static final int ASCII_CODE_POINT_END = 0x007F;
  private static final List<Integer> ASCII_CODE_POINT_RANGE =
          IntStream.rangeClosed(ASCII_CODE_POINT_START, ASCII_CODE_POINT_END)
            .boxed()
            .collect(Collectors.toList());
  private static final String ASCII_FULL_STRING =
          IntStream.rangeClosed(ASCII_CODE_POINT_START, ASCII_CODE_POINT_END)
            .mapToObj(codePoint -> Character.toString((char) codePoint))
            .collect(Collectors.joining());
  
  @Test
  public void isAscii_isCorrect()
  {
    assertTrue(Stringy.isAscii(""));
    assertTrue(Stringy.isAscii("abc 123 #@% +-*/ ,:;.?!"));
    assertTrue(Stringy.isAscii("\"\\"));
    assertTrue(Stringy.isAscii(ASCII_FULL_STRING));
    
    assertFalse(Stringy.isAscii(Stringy.toString(ASCII_CODE_POINT_END + 1)));
    assertFalse(Stringy.isAscii(ASCII_FULL_STRING + "文"));
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
  public void removeSuffixRegex_isCorrect()
  {
    assertEquals(Stringy.removeSuffixRegex(".", "12345"), "1234");
    assertEquals(Stringy.removeSuffixRegex("[a-z]+", "abc xyz"), "abc ");
    assertEquals(Stringy.removeSuffixRegex("[a-z]+", "123 456"), "123 456");
  }
  
  @Test
  public void removePrefix_isCorrect()
  {
    assertEquals(Stringy.removePrefix("[a-z]+", "abc xyz"), "abc xyz");
    assertEquals(Stringy.removePrefix("1", "234"), "234");
    assertEquals(Stringy.removePrefix("2", "234"), "34");
    assertEquals(Stringy.removePrefix("WELL_", "WELL_SCREW_YOU"), "SCREW_YOU");
  }
  
  @Test
  public void getFirstCodePoint_isCorrect()
  {
    assertEquals(Stringy.getFirstCodePoint("ABC"), 0x0041);
    assertEquals(Stringy.getFirstCodePoint("天下為公"), 0x5929);
  }
  
  @Test
  public void toCodePointList_isCorrect()
  {
    assertEquals(Stringy.toCodePointList(ASCII_FULL_STRING), ASCII_CODE_POINT_RANGE);
    assertEquals(Stringy.toCodePointList("天下為公"), Arrays.asList(0x5929, 0x4E0B, 0x70BA, 0x516C));
  }
  
  @Test
  public void addCodePointsToSet_isCorrect()
  {
    final int SOME_CODE_POINT = 0x1234;
    final Set<Integer> commonsCodePointSet = new HashSet<>(Collections.singletonList(SOME_CODE_POINT));
    Stringy.addCodePointsToSet("天下為公", commonsCodePointSet);
    assertEquals(commonsCodePointSet, new HashSet<>(Arrays.asList(SOME_CODE_POINT, 0x5929, 0x4E0B, 0x70BA, 0x516C)));
    
    final Set<Integer> asciiFullCodePointSet = new HashSet<>();
    Stringy.addCodePointsToSet(ASCII_FULL_STRING, asciiFullCodePointSet);
    assertEquals(asciiFullCodePointSet, new HashSet<>(ASCII_CODE_POINT_RANGE));
  }
  
  @Test
  public void toString_isCorrect()
  {
    assertEquals(Stringy.toString(0x0000), "\0");
    
    assertEquals(Stringy.toString(0x0030), "0");
    assertEquals("!", Stringy.toString(0x0021));
    assertEquals("A", Stringy.toString(0x0041));
    
    assertEquals(Stringy.toString(0x3007), "〇");
    assertEquals(Stringy.toString(0x3400), "㐀");
    assertEquals(Stringy.toString(0x4DB5), "䶵");
    assertEquals(Stringy.toString(0x4E00), "一");
    assertEquals(Stringy.toString(0x9FD0), "鿐");
    
    assertEquals(Stringy.toString(0x2000B), "\uD840\uDC0B"); // 𠀋
    assertEquals(Stringy.toString(0x2A6B2), "\uD869\uDEB2"); // 𪚲
    assertEquals(Stringy.toString(0x2A7DD), "\uD869\uDFDD"); // 𪟝
    assertEquals(Stringy.toString(0x2B6ED), "\uD86D\uDEED"); // 𫛭
    assertEquals(Stringy.toString(0x2B746), "\uD86D\uDF46"); // 𫝆
    assertEquals(Stringy.toString(0x2B81C), "\uD86E\uDC1C"); // 𫠜
    assertEquals(Stringy.toString(0x2B8B8), "\uD86E\uDCB8"); // 𫢸
    assertEquals(Stringy.toString(0x2CE93), "\uD873\uDE93"); // 𬺓
  }
  
  @Test
  public void toCharacterList_isCorrect()
  {
    assertEquals(Stringy.toCharacterList("ABC"), Arrays.asList("A", "B", "C"));
    assertEquals(Stringy.toCharacterList("天下為公"), Arrays.asList("天", "下", "為", "公"));
  }
  
  @Test
  public void sunder_isCorrect()
  {
    assertArrayEquals(Stringy.sunder("abc 123", " "), new String[]{"abc", "123"});
    assertArrayEquals(Stringy.sunder("abc\t123", "\t"), new String[]{"abc", "123"});
    assertArrayEquals(Stringy.sunder("abc:::123", ":::"), new String[]{"abc", "123"});
    
    assertArrayEquals(Stringy.sunder("abc123", " "), new String[]{"abc123", ""});
    assertArrayEquals(Stringy.sunder(" abc123", " "), new String[]{"", "abc123"});
  }
}
