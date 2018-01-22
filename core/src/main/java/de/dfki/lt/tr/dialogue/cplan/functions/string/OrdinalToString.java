package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class OrdinalToString implements Function {

  UtterancePlanner _planner;

  private RuleBasedNumberFormat _numberFormatter;

  public Object apply(List<DagNode> args) {
    if (_numberFormatter == null) {
      _numberFormatter = new RuleBasedNumberFormat(
          new ULocale(_planner.getLanguage()), RuleBasedNumberFormat.ORDINAL);
    }
    String str = args.get(0).toString(false);
    try {
      int i = Integer.parseInt(str);
      String s = _numberFormatter.format(i);
      s = s.replaceAll("\u00AD", ""); // soft hyphen, in dutch numbers :(
      return s;
    }
    catch (NumberFormatException ex) {
      return "NotANumber";
    }
  }

  /** java operation arg1.substring(arg2, arg3) */
  public String name() {
    return "spell_ordinal";
  }

  public int arity() {
    return 1;
  }

  public void register(UtterancePlanner planner) {
    _planner = planner;
  }
}
