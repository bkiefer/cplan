package de.dfki.lt.tr.dialogue.cplan.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

public class IniFileWriter {
  private BufferedWriter _out;

  public IniFileWriter(File f) throws IOException {
    if (f.exists()) {
      if (! f.delete()) {
        throw new IOException("File to write can not be deleted: " + f);
      }
    }
    _out = new BufferedWriter(new FileWriter(f));
  }

  private void writeHeader(String sectionName) throws IOException {
    _out.append('[').append(sectionName).append(']');
    _out.newLine();
  }

  public void writeMap(String sectionName,
    Map<? extends Object, ? extends Object> map)
  throws IOException {
    if (map == null) return;
    writeHeader(sectionName);
    for (Entry<? extends Object, ? extends Object> keyEntry : map.entrySet()) {
      _out
      .append(keyEntry.getKey().toString())
      .append('=')
      .append(keyEntry.getValue().toString());
      _out.newLine();
    }
    _out.newLine();
  }

  public void writeList(String sectionName, Iterable<? extends Object> list)
  throws IOException {
    if (list == null) return;
    writeHeader(sectionName);
    for (Object key : list) {
      _out.append(key.toString());
      _out.newLine();
    }
    _out.newLine();
  }

  public void close() throws IOException {
    _out.close();
  }
}
