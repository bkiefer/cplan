package de.dfki.lt.tr.dialogue.cplan.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/** Small class for reading files similar to Windows .ini. Checks that
 *  no key/value pair is outside of any section, treats line comments starting
 *  with a hash mark.
 */
public class IniFileReader {

  private interface MyHandler extends IniFileHandler {
    public PairList<String, PairList<String, String>> getResult();
  }

  /** This provides a minimalistic interface for those who don't want to bother
   *  implementing a streamlined IniFileHandler.
   *
   *  The result is a hash map where the consumer can iterate over the sections
   *  in the order in which they were read. The same is true for the key/value
   *  pairs of the section data.
   */
  public static
  PairList<String, PairList<String, String>> readIniFile(File f)
  throws FileNotFoundException, IOException {
    MyHandler handler = new MyHandler() {
      private PairList<String, PairList<String, String>> _result =
          new PairList<String, PairList<String, String>>();

      private PairList<String, String> _currentSection = null;

      public PairList<String, PairList<String, String>> getResult() {
        return _result;
      }

      public void keyValuePair(String key, String value) {
        _currentSection.add(key, value);
      }

      public void sectionEnd(String name) {
        _currentSection = null;
      }

      public void sectionStart(String name) {
        _currentSection = new PairList<String, String>();
        _result.add(name, _currentSection);
      }
    };

    readFile(f, handler);

    return handler.getResult();
  }

  /** Reads the file contents using the handler given in the constructor, or
   *  set in readIniFile. After calling this method, the file is closed and this
   *  reader may not be used anymore.
   */
  public static void readFile(File f, IniFileHandler h)
  throws FileNotFoundException, IOException {
    if (h == null)
      throw new IllegalArgumentException("Handler may not be null");

    String currentSection = null;
    String nextLine = null;
    BufferedReader in = null;
    try {
      in = new BufferedReader(new FileReader(f));
      while ((nextLine = in.readLine()) != null) {
        nextLine = nextLine.trim();
        int hashPos = nextLine.indexOf('#');
        if (hashPos != -1) {
          nextLine = nextLine.substring(0, hashPos);
        }
        if (! nextLine.isEmpty()) {
          if (nextLine.charAt(0) == '[') { // begin of section
            String sectionName = nextLine.substring(1, nextLine.lastIndexOf(']'));
            sectionName = sectionName.trim();
            if (currentSection != null) {
              h.sectionEnd(currentSection);
            }
            currentSection = sectionName;
            h.sectionStart(currentSection);
          } else {
            if (currentSection == null) {
              throw new IllegalStateException(
                  "Key/value pair without active section :" +nextLine);
            }
            String key, value = null;
            int eqPos = nextLine.indexOf('=');
            if (eqPos != -1) {
              key = nextLine.substring(0, eqPos).trim();
              value = nextLine.substring(eqPos + 1).trim();
            } else {
              key = nextLine.trim();
            }
            h.keyValuePair(key, value);
          }
        }
      }
      if (currentSection != null) {
        h.sectionEnd(currentSection);
      }
    }
    finally {
      if (in != null) in.close();
    }
  }
}
