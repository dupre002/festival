package com.digitalenergyinc.festival.client.view;

import com.digitalenergyinc.festival.client.SideMenu;
import com.digitalenergyinc.festival.client.Sink;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.ui.DockPanel;

/**
 * This is the home set of panels - including login to the account.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class HomePane extends Sink implements HistoryListener {

	protected SideMenu list = new SideMenu();   // menu list
	private SinkInfo curInfo;					// currently info on selected menu item 
	private Sink curMenu;						// currently selected set of panels
	private DockPanel menuContainer;			// module panel (based on menu sel)
	
  /**
	* Every Sink needs an init that defines the menu title and description.
	*/
  public static SinkInfo init() {
    return new SinkInfo("Home",
    		"") 
    {
      public Sink createInstance() {
        return new HomePane();
      }
    };
  }
  
  /**
   * Determines what to show when history has changed.
   * @param String history token to display.
   */
  public void onHistoryChanged(String token) 
  {
	  // Find the SinkInfo associated with the history context. If one is
	  // found, show it (It may not be found, for example, when the user mis-
	  // types a URL, or on startup, when the first context will be "").
	  SinkInfo info = list.find(token);
	  if (info == null) {
		  showWelcome();
		  return;
	  }
	  show(info, false);
  }
	
  /**
	* This is the long description for the main window of this panel.
	*/
  public HomePane() {
		
	  // Load all the menus
	  loadMenus();
		
	  menuContainer = new DockPanel();
	  menuContainer.setStyleName("film-Sink");
		
	  menuContainer.add(list, DockPanel.WEST);

	  initWidget(menuContainer);
	  
	  History.addHistoryListener(this);
	  
	  showWelcome();
  }

  
  /**
   * Called just after this sink is shown.
   */
  public void onShow() {
  }

  /**
   * Show the set of panels based on menu selection.
   * @param SinkInfo the collection of panels to display.
   * @param boolean Whether or not this should affect history.
   */
  public void show(SinkInfo info, boolean affectHistory) {
	  // Don't bother re-displaying the existing sink. This can be an issue
	  // in practice, because when the history context is set, our
	  // onHistoryChanged() handler will attempt to show the currently-visible
	  // sink.
	  // ** decided to redisplay existing sink as a way to possibly refresh screen.
	  //if (info == curInfo) {
		//  return;
	  //}
	  curInfo = info;

	  // Remove the old sink from the display area.
	  if (curMenu != null) {
		  curMenu.onHide();
		  menuContainer.remove(curMenu);
	  }

	  // Get the new sink instance, and display its description in the menu list.
	  curMenu = info.getInstance();
	  list.setMenuSelection(info.getName());

	  // If affectHistory is set, create a new item on the history stack. This
	  // will ultimately result in onHistoryChanged() being called. It will call
	  // show() again, but nothing will happen because it will request the exact
	  // same sink we're already showing.
	  if (affectHistory) {
		  History.newItem(info.getName());
	  }

	  // Display the new sink.
	  menuContainer.add(curMenu, DockPanel.CENTER);
	  menuContainer.setCellWidth(curMenu, "100%");
	  menuContainer.setCellHeight(curMenu, "100%");
	  menuContainer.setCellVerticalAlignment(curMenu, DockPanel.ALIGN_TOP);
	  curMenu.onShow();
  }
  
  /**
   * Adds all menus to the list. Note that this does not create actual instances
   * of all menus yet (they are created on-demand). This can make a significant
   * difference in startup time.
   */
  protected void loadMenus() {
	  list.addMenu(WelcomePane.init());
  }
  
  /**
   * Show the Welcome set of panels.
   */
  private void showWelcome() {
	  show(list.find("Welcome"), false);
  }

}
