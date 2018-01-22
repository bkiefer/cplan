/*

 */
package de.dfki.lt.tr.dialogue.cplan.util;

/**
 * @author Bernd Kiefer, inspired by code from Andrea Sindico and AUCOM S.R.L.
 */

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class PluginLoader{

  private static Logger logger = LoggerFactory.getLogger("UtterancePlanner");

  private URLClassLoader classloader = null;

  /**
   *it takes as parameter an id representing a pluginEntryId and returns an
   * instance of that PluginEntry
   *
   */
  private Function getPlugin(String name) {
    name = name.replace(File.separatorChar, '.');
    name = name.substring(0, name.length() - 6); // remove ".class"

    Class<?> aClass;
    try {
      aClass = classloader.loadClass(name);
      Class<?> i = aClass;
      while (i != Object.class) {
        if (Arrays.asList(i.getInterfaces()).contains(Function.class)) {
          return (Function) aClass.newInstance();
        }
        i = i.getSuperclass();
      }
    } catch (ClassNotFoundException e) {
      logger.warn("{}", e);
    } catch (InstantiationException e) {
      logger.warn("{}", e);
    } catch (IllegalAccessException e) {
      logger.warn("{}", e);
    }


    return null;
  }


  /** Load all plugins in the given JAR
   */
  private void loadAllPlugins(File file, List<Function> result) {
    JarFile jarFile = null;
    try {
      jarFile = new JarFile(file);
      for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {
        JarEntry jarEntry = e.nextElement();
        String className = jarEntry.getName();
        if (className.endsWith((".class"))) {
          Function plugin = getPlugin(className);
          if (plugin != null) {
            logger.info("Added {}", plugin.getClass().getSimpleName());
            result.add(plugin);
          }
        }
      }
    } catch (IOException e) {
      logger.warn("{}", e.getMessage());
    } finally {
      if (jarFile != null) {
        try {
          jarFile.close();
        } catch (IOException ioe) {
        }
      }
    }
  }

  /** Iterate over all .jar files in pluginsPath and create plug-in objects for
   *  all subclasses of PluginType
   */
  public List<Function> loadPlugins(File pluginsPath) {
    List<Function> result = new LinkedList<Function>();
    String[] jar =
      pluginsPath.list(new java.io.FilenameFilter() {
        public boolean accept(File dir, String filename) {
          return filename.endsWith(".jar");
        }
      });

    if (jar.length == 0)
      return result;

    URL[] url = new URL[jar.length];
    for (int i = 0; i < jar.length; i++) {
      File jarFile = new File(pluginsPath, jar[i]);
      try {
        url[i] = jarFile.toURI().toURL();
      } catch (MalformedURLException ex) {
        logger.warn("{}", ex.getMessage());
      }
    }
    classloader = new URLClassLoader(url);
    for (int i = 0; i < jar.length; i++) {
      logger.info("Loading {}", jar[i]);
      loadAllPlugins(new File(pluginsPath, jar[i]), result);
    }
    return result;
  }
}
