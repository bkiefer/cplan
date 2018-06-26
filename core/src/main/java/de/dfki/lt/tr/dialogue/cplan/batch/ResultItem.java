package de.dfki.lt.tr.dialogue.cplan.batch;

import de.dfki.lt.tr.dialogue.cplan.DagNode;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest.BatchType;
import de.dfki.lt.tr.dialogue.cplan.batch.BatchTest.Status;

/** An item representing a failed test, consisting of:
 *  @field itemStatus     if this item failed or succeeded, with or without
 *                        warning
 *  @field testItemIndex  the number of the test item
 *  @field outputLf       the logical form resulting from processing
 *  @field realized       the string resulting from passing the ouput logical
 *                        form to the CCG realizer
 */
public class ResultItem {
  public Status itemStatus;
  public boolean warning;
  public int testItemIndex;
  public DagNode outputLf;
  public String realized;
  public BatchType realizationResult;

  public ResultItem(int t, DagNode out, String r, Status s,
    boolean w, BatchType real) {
    testItemIndex = t;
    outputLf = out;
    realized = r;
    itemStatus = s;
    warning = w;
    realizationResult = real;
  }
}