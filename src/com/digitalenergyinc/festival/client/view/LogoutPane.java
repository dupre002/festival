package com.digitalenergyinc.festival.client.view;

import com.digitalenergyinc.festival.client.Sink;
import com.digitalenergyinc.fest.client.control.MovieHandler;
import com.digitalenergyinc.fest.client.control.ScheduleHandler;
import com.digitalenergyinc.fest.client.control.TheaterHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the logout panel.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class LogoutPane extends Sink {

	private VerticalPanel myPanel = new VerticalPanel();	// main panel
	
  /**
	* Every Sink needs an init that defines the menu title and description.
	*/
  public static SinkInfo init() {
    return new SinkInfo("Logout",
    		"") 
    {
      public Sink createInstance() {
        return new LogoutPane();
      }
    };
  }
  
  /**
	* This is the the main window of this panel.
	*/
  public LogoutPane() {
	  
	  HTML welcome = new HTML(
			  "You are now logged out! "
			  + "<p>Goodbye!  ",
			  true); 
	  
	  myPanel.setStyleName("film-Sink");
	  myPanel.add(welcome);
	  
	  welcome.setStyleName("film-Info");

	  initWidget(myPanel);
  }

  
  /**
   * Called just after this sink is shown.
   */
  public void onShow() {
	  // clear all saved data
	  User.logout();
	  ScheduleHandler.clear();
	  TheaterHandler.clear();
	  MovieHandler.clear();
  }
}
