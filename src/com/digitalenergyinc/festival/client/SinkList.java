package com.digitalenergyinc.festival.client;


import java.util.ArrayList;

import com.digitalenergyinc.festival.client.Sink.SinkInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;

/**
 * The top menu panel that contains all of the sinks, 
 * along with a short description of each.
 * 
 * <p>Title: Fantasy Football Draft Aide</p>
 * <p>Description: Helps fantasy football manager draft a team.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class SinkList extends Composite {

  private HorizontalPanel list = new HorizontalPanel();
  private ArrayList sinks = new ArrayList();
  private int selectedSink = -1;

  /**
   * Default constructor - sets name and style of menu item.
   */
  public SinkList() {
    initWidget(list);
    setStyleName("film-List");
  }

  /**
   * Adds set of panels to the list.
   * @param SinkInfo the info of the menu item to add to list.
   */
public void addSink(final SinkInfo info) {
    String name = info.getName();
    Hyperlink link = new Hyperlink(name, name);
    link.setStyleName("film-SinkItem");

    list.add(link);
    sinks.add(info);  // can't resolve warning until GWT supports Java 5.
  }

/**
 * Finds the sink (menu item) in the list.
 * @param String name of menu item to find.
 * @return SinkInfo the sink requested;  null if not found.
 */
  public SinkInfo find(String sinkName) {
    for (int i = 0; i < sinks.size(); ++i) {
      SinkInfo info = (SinkInfo) sinks.get(i);
      if (info.getName().equals(sinkName)) {
        return info;
      }
    }

    return null;
  }

  /**
   * Sets the selected sink (menu item).
   * @param String menu item name to select.
   */
  public void setSinkSelection(String name) {
    if (selectedSink != -1) {
      list.getWidget(selectedSink).removeStyleName("film-SinkItem-selected");
    }
    
    for (int i = 0; i < sinks.size(); ++i) {
      SinkInfo info = (SinkInfo) sinks.get(i);
      if (info.getName().equals(name)) {
        selectedSink = i;
        list.getWidget(selectedSink).addStyleName("film-SinkItem-selected");
        return;
      }
    }
  }
}
