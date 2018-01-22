package de.dfki.lt.tr.dialogue.cplan.functions;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.math.*;
import de.dfki.lt.tr.dialogue.cplan.functions.string.*;
import de.dfki.lt.tr.dialogue.cplan.util.PluginLoader;


public class FunctionFactory {

  private static HashMap<String, Function> _registeredFunctions =
    new HashMap<String, Function>();

  public static Function get(String name) {
    return _registeredFunctions.get(name);
  }

  public static void register(Function function, UtterancePlanner planner) {
    _registeredFunctions.put(function.name(), function);
    function.register(planner);
  }

  /** Initialize only built-in functions */
  public static void init(UtterancePlanner planner) {
    register(new DiscreteRandomFunction(), planner);
    register(new ConstantFunction("0"), planner);
    register(new ConstantFunction("1"), planner);
    register(new CloneNode(), planner);
    register(new IdentityFunction(), planner);
    register(new Equal(), planner);
    register(new Bound(), planner);
    register(new ThrowExceptionFunction(), planner);
    register(new WarningFunction(), planner);
    // math functions
    register(new Add(), planner);
    register(new Sub(), planner);
    register(new Div(), planner);
    register(new Mult(), planner);
    register(new Gt(), planner);
    register(new GtEq(), planner);
    register(new Lt(), planner);
    register(new LtEq(), planner);
    register(new Eq(), planner);
    register(new Not(), planner);
    register(new Neg(), planner);
    // string functions
    register(new Concatenate(), planner);
    register(new WordCount(), planner);
    register(new Length(), planner);
    register(new Contains(), planner);
    register(new Endswith(), planner);
    register(new Substring(), planner);
    register(new NumberToString(), planner);
    register(new OrdinalToString(), planner);
  }

  public static void registerPlugins(File pluginDirectory,
      UtterancePlanner planner) {
    if (pluginDirectory == null)
      return;
    if (! pluginDirectory.isDirectory()) {
      throw new IllegalArgumentException("Not a directory: " + pluginDirectory);
    }
    List<Function> pluginFunctions =
      new PluginLoader().loadPlugins(pluginDirectory);
    for (Function f : pluginFunctions) {
      register(f, planner);
    }
  }
}
