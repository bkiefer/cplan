package de.dfki.lt.tr.dialogue.cplan.util;

public interface IniFileHandler {
  /** Called when a new section is started, giving its name as argument */
  public abstract void sectionStart(String name);

  /** Called when a section ends, either because a new session starts, or
   *  because the file has been read completely.
   *  @param name the name of the section that ends
   */
  public abstract void sectionEnd(String name);

  /** Announce a new key/value pair in the current section. value may be null if
   *  only a key is given in the file, which may be OK.
   */
  public abstract void keyValuePair(String key, String value);
}
