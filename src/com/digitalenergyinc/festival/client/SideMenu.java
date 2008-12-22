package com.digitalenergyinc.festival.client;


import java.util.ArrayList;

import com.digitalenergyinc.festival.client.Sink.SinkInfo;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * The side menu composite panel (a variation of SinkList), 
 * along with a short description of each.
 * 
 * <p>Title: Fantasy Football Draft Aide</p>
 * <p>Description: Helps fantasy football manager draft a team.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class SideMenu extends Composite {

  private VerticalPanel list = new VerticalPanel();
  private ArrayList menuItems = new ArrayList();
  private int selectedMenu = -1;

  /**
   * Default constructor - sets name and style of menu item.
   */
  public SideMenu() {
    initWidget(list);
    setStyleName("film-List");
    this.addStyleName("film-SideMenu");
  }

  /**
   * Adds set of panels to the list.
   * @param SinkInfo the info of the menu item to add to list.
   */
public void addMenu(final SinkInfo info) {
    String name = info.getName();
    Hyperlink link = new Hyperlink(name, name);
    link.setStyleName("film-SinkItem");

    list.add(link);
    menuItems.add(info);  // can't resolve warning until GWT supports Java 5.
  }

/**
 * Finds the sink (menu item) in the list.
 * @param String name of menu item to find.
 * @return SinkInfo the sink requested;  null if not found.
 */
  public SinkInfo find(String sinkName) {
    for (int i = 0; i < menuItems.size(); ++i) {
      SinkInfo info = (SinkInfo) menuItems.get(i);
      if (info.getName().equals(sinkName)) {
        return info;
      }
    }

    return null;
  }

  /**
   * Sets the selected menu item.
   * @param String menu item name to select.
   */
  public void setMenuSelection(String name) {
    if (selectedMenu != -1) {
      list.getWidget(selectedMenu).removeStyleName("film-SinkItem-selected");
    }
    
    for (int i = 0; i < menuItems.size(); ++i) {
      SinkInfo info = (SinkInfo) menuItems.get(i);
      if (info.getName().equals(name)) {
    	  selectedMenu = i;
        list.getWidget(selectedMenu).addStyleName("film-SinkItem-selected");
        return;
      }
    }
  }
}
