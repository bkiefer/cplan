package de.dfki.lt.tr.dialogue.cplan.gui;

import de.dfki.lt.loot.gui.layouts.AbstractLayout;
import de.dfki.lt.loot.gui.layouts.BasicAtomLayout;
import de.dfki.lt.loot.gui.layouts.CompactConsLayout;
import de.dfki.lt.loot.gui.layouts.SimpleTreeLayout;
import de.dfki.lt.tr.dialogue.cplan.Environment;

public class LFLayout extends AbstractLayout {
  public LFLayout() {
    addLayout(new LFMapFacetLayout());
    addLayout(new CompactConsLayout());
    addLayout(new SimpleTreeLayout());
    addLayout(new BasicAtomLayout());
  }
}
