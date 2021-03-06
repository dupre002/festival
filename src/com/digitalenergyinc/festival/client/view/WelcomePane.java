package com.digitalenergyinc.festival.client.view;


import org.gwtwidgets.client.util.Location;
import org.gwtwidgets.client.util.WindowUtils;
import org.mcarthur.sandy.gwt.login.client.LoginPanel;

import com.digitalenergyinc.fest.client.DataChangeListener;
import com.digitalenergyinc.fest.client.ServerListener;
import com.digitalenergyinc.fest.client.control.MovieHandler;
import com.digitalenergyinc.fest.client.control.ScheduleHandler;
import com.digitalenergyinc.fest.client.control.SummaryHandler;
import com.digitalenergyinc.fest.client.control.TheaterHandler;
import com.digitalenergyinc.fest.client.control.TicketHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.digitalenergyinc.fest.client.model.ShowingRPC;
import com.digitalenergyinc.fest.client.tickets.TicketPackageRPC;
import com.digitalenergyinc.festival.client.Sink;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * This is the welcome/login set of panels.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class WelcomePane extends Sink 
{
	private VerticalPanel vp = new VerticalPanel();		// main panel
	private FlowPanel hpWelcome = new FlowPanel();		// for text
	private LoginPanel loginPanel;						// login panel
	private HTML welcome1;								// login intro
	private HTML welcomeBack;							// logged in intro
	private HTML summary1;								// summary info
	private HTML summary2;								// summary info
	private HTML summary3;								// summary info
	private HTML tickets1;								// ticket info
	private HTML error = new HTML("");					// error message
	private boolean isBack = false;						// is welcomed back
	private boolean icalAuth = false;                   // returning from auth

	/**
	 * Every Sink needs an init that defines the menu title and description.
	 */
	public static SinkInfo init() {
		return new SinkInfo("Welcome",
		"Welcome to MyFilmFest Film Guide!") 
		{
			public Sink createInstance() {
				return new WelcomePane();
			}
		};
	}

	/**
	 * This is the the main window of this panel.
	 */
	public WelcomePane() {

		welcome1 = new HTML(
				"Welcome to the Film Guide. ", true); 


		welcomeBack = new HTML("", true); 
		summary1 = new HTML("", true); 
		summary2 = new HTML("", true); 
		summary3 = new HTML("", true); 
		tickets1 = new HTML("", true); 

		welcome1.addStyleName("film-Flow");

		welcomeBack.setStyleName("film-Info");
		summary1.setStyleName("film-Info");
		summary2.setStyleName("film-Info");
		summary3.setStyleName("film-Info");
		tickets1.setStyleName("film-Info");

		hpWelcome.setStyleName("film-Info");

		hpWelcome.setWidth("100%");
		hpWelcome.add(welcome1);


		vp.setWidth("100%");
		vp.add(hpWelcome);  
		vp.add(welcomeBack);
		
		vp.add(summary1);
		vp.add(summary2);
		vp.add(tickets1);
		vp.add(summary3);

		welcomeBack.setVisible(false);
		summary1.setVisible(false);

		error.setStyleName("film-Errors");
		
		// set up listener for results of log in
		//User.addLogoutListener(this);
		User.addServerListener(new myServerlistener());

		// load error message if present
		if (!User.getError().equalsIgnoreCase(""))
			error.setHTML(User.getError());

		// set up login panel
		LoginPanel.LoginListener loginListener = new LoginPanel.LoginListener() {
			public void onSubmit(LoginPanel loginPanel) {
				// log in user
				logIn();
				// **look for result of login in listener!
			}
		};

		loginPanel = new LoginPanel(loginListener);

		vp.add(loginPanel);	
		vp.setHorizontalAlignment(VerticalPanel.ALIGN_CENTER);

		vp.add(error);

		initWidget(vp);
		
		// initialize singletons
		MovieHandler mH = MovieHandler.instance();
		ScheduleHandler sh = ScheduleHandler.instance();
		SummaryHandler ssh = SummaryHandler.instance();
		SummaryHandler.addChangeListener(new myChangeListener());
		TheaterHandler th = TheaterHandler.instance();
		

		// check if already logged in (for those that just registered)
		if (User.getTokenID() != 0)
			displayLoggedIn();
		else
			// set focus after all is done
		    Scheduler.get().scheduleDeferred(new ScheduledCommand() { public void execute() {
				loginPanel.focus(); }});	
	}

	/**
	 * log in user.
	 */
	private void logIn() {
		User.login(loginPanel.getUsername(),loginPanel.getPassword());
	}
	
	/**
	 * Called to replace login panel with welcome panel.
	 */
	private void displayLoggedIn() {
		welcomeBack.setHTML("Welcome Back " + User.getUserName() + "!<p> "); 
		error.setHTML("");
		loginPanel.setVisible(false);
		hpWelcome.setVisible(false);
		welcomeBack.setVisible(true);
		tickets1.setVisible(true);
		summary1.setVisible(true);
		summary2.setVisible(true);
		summary3.setVisible(true);
		
		TicketPackageRPC myTix = TicketHandler.getMyTickets();
		tickets1.setHTML("You have " +myTix.getNumberOfTix()+" tickets.");
		tickets1.setVisible(true);
		
		summary1.setHTML("You have ranked " + SummaryHandler.getMoviesRanked()+" films");
		summary1.setVisible(true);
		summary1.setVisible(false);
		summary2.setHTML("You have " + SummaryHandler.getMoviesUnranked()+" films unranked.");
		summary2.setVisible(true);
		summary2.setVisible(false);
		int totalTix = SummaryHandler.getPremiereTixSched() + SummaryHandler.getRegularTixSched();
		summary3.setHTML("You have " +totalTix+" films on your schedule.");
		summary3.setVisible(true);

		isBack = true;
	}
	

	/**
	 * Called when user logs out - redisplay login panel.
	 */
	public void onLogOut() {
		//Reset ALL PRIVATE DATA;
		//even if there's no way to view this widget memory can be inspected
		loginPanel.reset();
		loginPanel.reenable();

		if (isBack)
		{
			welcomeBack.setVisible(false);
			tickets1.setVisible(false);
			summary1.setVisible(false);
			summary2.setVisible(false);
			summary3.setVisible(false);
			hpWelcome.setVisible(true);
			loginPanel.setVisible(true);
			isBack = false;
		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() { public void execute() {
			loginPanel.focus(); }});	 
	}

	/**
	 * Called when user logs in.
	 */
	public void onLogIn() {
		//Re-setup all fields you cleared in onLogout.
	} 

	/**
	 * Listener class to handle when server activity occurs.
	 */
	class myServerlistener implements ServerListener
	{
		/**
		 * Called when server activity starts.
		 * @param String descriptive text to display (loading, updating, etc).
		 */
		public void onServerStart(String inDescr)
		{
		}
		
		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action, no result expected.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			displayLoggedIn();
			
			// if relogging in after getting ical auth
			if (icalAuth)
			{
			    // clear cookies and go to my schedule
			    Cookies.removeCookie("FestUserID");
			    Cookies.removeCookie("FestUserToken");
			    History.newItem("My Schedule");
			}
		}
		
		/**
		 * Show the error message from server.
		 * @param String actionID to identify server action.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String actionID, String inError)
		{
			error.setHTML(inError); 
			loginPanel.reenable(); // allow another attempt
			loginPanel.focus();
		}
	}	
	
	  /**
	   * Listener class used to take action when data has changed or selected.
	   */
	  class myChangeListener implements DataChangeListener
	  {
	      /**
	       * Used to take action when data has changed .
	       */
	      public void onDataChange()
	      {
	          // update data on screen from Summary
	          summary1.setHTML("You have ranked " + SummaryHandler.getMoviesRanked()+" films");
	          summary1.setVisible(true);
	          summary2.setHTML("You have " + SummaryHandler.getMoviesUnranked()+" films unranked.");
	          summary2.setVisible(true);
	          int totalTix = SummaryHandler.getPremiereTixSched() + SummaryHandler.getRegularTixSched();
	          summary3.setHTML("You have " +totalTix+" films on your schedule.");
	      }

	      /**
	       * Used to take action when data is selected.
	       * @param ShowingRPC  the item changed or selected (optional).
	       */
	      public void onDataSelected(ShowingRPC inItem)
	      {

	      }
	  }

	/**
	 * Called just after this sink is shown.
	 */
	public void onShow() {	
	    // check if we've returned from getting authorization 
	    // to export calendar by checking for token parameter in the URL
	    Location loc = WindowUtils.getLocation();
	    String requestParam = loc.getParameter("token");

	    // if token found, go to My Schedule screen
	    if (requestParam != null)
	    {
	        // get userid and token from cookies and log them back in
	        String tempUser = Cookies.getCookie("FestUserID");
	        String tempToken = Cookies.getCookie("FestUserToken");
	        
	        icalAuth = true;
	        if (tempUser != null)
            {
	            User.loginAgain(tempUser,tempToken);
            }	        
	    }
	    else
	    {
	        // load error message if present
	        if (!User.getError().equalsIgnoreCase(""))
	            error.setHTML(User.getError());

	        if (User.getTokenID() == 0)
	            onLogOut();	  
	    }		
	}
}
