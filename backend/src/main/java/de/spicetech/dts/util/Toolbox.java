package de.spicetech.dts.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class Toolbox {

  public static final int DEFAULT_FILENAME_LENGTH = 30;

  private static String[][] WEBSAFE_REPLACEMENTS = { { " ", "_" }, { "Ä", "Ae" }, { "Ü", "Ue" }, { "Ö", "Oe" },
      { "ä", "ae" }, { "ü", "ue" }, { "ö", "oe" }, { "ß", "ss" } };

  public String replaceByWebsafeChars(String orig) throws UnsupportedEncodingException {
    return replaceByWebsafeChars(orig, DEFAULT_FILENAME_LENGTH);
  }

  public String replaceByWebsafeChars(String orig, int length) throws UnsupportedEncodingException {
    while (orig.length() > length) {
      int lastIndex = orig.lastIndexOf(" ");
      if (lastIndex == -1) {
        orig = orig.substring(0, lastIndex);
      } else {
        break;
      }
    }
    String result = orig;

    for (int i = 0; i < WEBSAFE_REPLACEMENTS.length; i++) {
      result = result.replaceAll(WEBSAFE_REPLACEMENTS[i][0], WEBSAFE_REPLACEMENTS[i][1]);
    }

    result = URLEncoder.encode(result, "UTF-8");
    return result;
  }

}
