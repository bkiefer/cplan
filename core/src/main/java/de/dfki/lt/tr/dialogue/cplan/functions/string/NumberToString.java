package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.util.List;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.functions.AbstractFunction;

public class NumberToString extends AbstractFunction {

  private RuleBasedNumberFormat _numberFormatter;

  public Object apply(List<DagNode> args) {
    if (_numberFormatter == null) {
      _numberFormatter = new RuleBasedNumberFormat(
          new ULocale(_planner.getLanguage()), RuleBasedNumberFormat.SPELLOUT);
    }
    String str = toString(args.get(0));
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
    return "spell_number";
  }

  public int arity() {
    return 1;
  }
}
