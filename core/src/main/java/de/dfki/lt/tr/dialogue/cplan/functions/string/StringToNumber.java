package de.dfki.lt.tr.dialogue.cplan.functions.string;

import java.text.ParseException;
import java.util.List;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.ULocale;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.UtterancePlanner;
import de.dfki.lt.tr.dialogue.cplan.functions.Function;

public class StringToNumber implements Function {

  UtterancePlanner _planner;

  private RuleBasedNumberFormat _numberFormatter;

  @Override
  public Object apply(List<DagNode> args) {
    if (_numberFormatter == null) {
      _numberFormatter = new RuleBasedNumberFormat(
          new ULocale(_planner.getLanguage()), RuleBasedNumberFormat.SPELLOUT);
    }
    String str = args.get(0).asString();
    try {
      Number i = _numberFormatter.parse(str);
      return i.toString();
    }
    catch (ParseException ex) {
      return "NotANumber";
    }
  }

  /** java operation arg1.substring(arg2, arg3) */
  @Override
  public String name() {
    return "string2number";
  }

  @Override
  public int arity() {
    return 1;
  }

  @Override
  public void register(UtterancePlanner planner) {
    _planner = planner;
  }
}
