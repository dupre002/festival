package com.digitalenergyinc.festival.client.view;

import java.util.HashSet;
import java.util.Iterator;

import com.digitalenergyinc.fest.client.Constants;
import com.digitalenergyinc.fest.client.LogoutListener;
import com.digitalenergyinc.fest.client.ServerListener;
import com.digitalenergyinc.fest.client.control.TheaterHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.digitalenergyinc.fest.client.model.UtilTimeRPC;
import com.digitalenergyinc.fest.client.widget.DaySchedWidget;
import com.digitalenergyinc.festival.client.Sink;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is the set of panels to manage the film schedule.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class SchedPane extends Sink implements ClickListener, LogoutListener
{
	private VerticalPanel mainVP = 
		new VerticalPanel();  				// main panel
	private HTML dayHdg;					// Day displayed	
	private HorizontalPanel outerHP = 
		new HorizontalPanel();				// Outer panel containing hours and t sched
	private HorizontalPanel mainHP = 
		new HorizontalPanel();				// Main panel for theater schedules
	private HorizontalPanel dayPanel = 
		new HorizontalPanel();				// Day with scroll buttons panel
	private HorizontalPanel optPanel = 
		new HorizontalPanel();				// Options panel for filters
	private HorizontalPanel waitPanel =
		new HorizontalPanel();				// wait icon panel
	private HorizontalPanel sysPanel =
		new HorizontalPanel();				// system errors + wait icon panel
	private ScrollPanel mainScroll = new ScrollPanel();	// scroll panel for theaters 
	
	private HTML waitText;					// wait text
	
	// images
	private Image nextDay; 					// Next icon
	private Image prevDay; 					// Previous icon	
	private Image waitIcon;					// wait GIF
	private String nextURL = "images/goNext.gif";   // URL for next
	private String prevURL = "images/goPrev.gif";   // URL for previous
	private String waitURL = "images/sd08-loader.gif";   // URL for wait icon
	private Image helpIcon;                       // help GIF
    private String helpURL = "images/help.gif";   // URL for help icon
	
	// system elements
    private HTML screenTitle;               // title of screen
	private VerticalPanel hourPanel;		// hour headings panel
	private FlowPanel theaterFlow;			// flow panel for theater options
	private HTML sysErrors = new HTML("");	// system errors (from server)
	private boolean isUserDataLoaded = false; // indicates if schedule with user data loaded
	private boolean isLoaded = false;		// indicates if data has been loaded
	
	// theater schedule elements
	private TheaterHandler theaterHandler;	// handler for theater schedule

	private Grid hourTbl;					// grid list of time slots (hour headings)
	private HTML titleSpacer = new HTML("<b>_</b>");	// title bar of widget
	
	private CheckBox soldOutFilter;			// checkbox to show/hide sold out
	private int dayShown = 0;				// index of day displayed
	private int waitCount = 0;				// counter of how many loading
	private UtilTimeRPC utilTime = new UtilTimeRPC();  // time utility


	/**
	 * Every Sink needs an init that defines the menu title and description.
	 */
	public static SinkInfo init() {
		return new SinkInfo("Film Guide",	"") 
		{
			public Sink createInstance() {
				return new SchedPane();
			}
		};
	}

	/**
	 * This is the long description for the main window of this panel.
	 */
	public SchedPane() {

	    // screen title
        screenTitle = new HTML("<b>Film Guide</b>");  
        dayPanel.add(screenTitle);
        helpIcon = new Image(helpURL);     
        helpIcon.setTitle("Click here to see help for this screen.");
        helpIcon.setStyleName("film-Button2");
        helpIcon.addClickListener(this);
        dayPanel.add(helpIcon);
        
		dayHdg = new HTML("", true); 
		dayHdg.setStyleName("sched-Day-Hdg");
		
		// set up day heading with scroll buttons and display options
		nextDay = new Image(nextURL);		
		nextDay.setTitle("Click to go to next day");
		nextDay.addClickListener(this);
		prevDay = new Image(prevURL);	
		prevDay.setTitle("Click to go to previous day");	
		prevDay.addClickListener(this);
		dayPanel.add(prevDay);
		dayPanel.add(nextDay);
		dayHdg.setHTML(utilTime.convertIndexToLongDay(dayShown));
		dayPanel.add(dayHdg);
		
		// wait icon
		waitIcon = new Image(waitURL);		
		waitIcon.setTitle("Saving/Loading Data from server");
		waitText = new HTML("");
		waitText.setStyleName("sched-Film-Font");
		waitPanel.setVisible(false);
		waitPanel.add(waitIcon);
		waitPanel.add(waitText);
		sysPanel.add(waitPanel);
		sysPanel.add(sysErrors);
		dayPanel.add(sysPanel);
		
		// sold out filter
		soldOutFilter = new CheckBox("Hide Sold Out");
		soldOutFilter.setTitle("If checked, will hide showings that have no tickets");
		soldOutFilter.addClickListener(this);
		optPanel.add(soldOutFilter);
		
		mainScroll.setWidth("100%");
		mainScroll.setHeight("1050px");
		
		sysErrors.addStyleName("film-Errors");

		titleSpacer.setStyleName("sched-Theater-Hdg");
		hourPanel = new VerticalPanel();
		hourPanel.setWidth("75px");
		hourPanel.setStyleName("sched-Cal-Box");

	
		mainVP.setStyleName("film-Sink");
		mainVP.addStyleName("film-Boxed");
		
		// set up handler & listeners
		theaterHandler = TheaterHandler.instance();
		User.addLogoutListener(this);
				
		initWidget(mainVP);
	}	  


	/**
	 * This sets up the film guide with data.
	 */
	private void setupGuide()
	{		
		// set up hour headings
		int slotCount = 36;
		hourTbl = new Grid(slotCount, 1);
		hourTbl.setStyleName("sched-Cal-Cell");
		hourTbl.addStyleName("sched-Film-Font");
		hourTbl.setWidth("100%");
		hourTbl.setCellPadding(0);
		hourTbl.setCellSpacing(0);

		// set up hour table from 8 am to 11:30 pm
		int hourCnt = 8;
		String AMPM = "AM";
		for (int i=0; i < slotCount; i=i+2)
		{
			// adjust for AM/PM and non military time
			if (hourCnt == 12)
			{
				if (AMPM.equals("PM"))
					AMPM = "AM";
				else
					AMPM = "PM";
			}
			if (hourCnt > 12)
			{
				hourCnt = 1;
			}
			
			// now construct time string
			String strNum = Integer.toString(hourCnt) + ":00 "+AMPM;
			hourTbl.setText(i, 0, strNum);
			
			strNum = Integer.toString(hourCnt++) + ":30 "+AMPM;
			hourTbl.setText(i+1, 0, strNum);	
			
			hourTbl.getCellFormatter().setStyleName(i, 0, "sched-NoFilm-Cell");
			hourTbl.getCellFormatter().setStyleName(i+1, 0, "sched-NoFilm-Cell");
		}
		
		hourPanel.add(titleSpacer);
		hourPanel.add(hourTbl);
		outerHP.add(hourPanel);
		
		// create theater filters
		theaterFlow = new FlowPanel();
		Iterator i = TheaterHandler.getTheaterGroupIDs().iterator();		
		while (i.hasNext())
		{
			String gID = (String)i.next();
			//DaySchedWidget tSched = (DaySchedWidget) theaters.get(tID);
			CheckBox tFilter = new CheckBox(gID+" Theaters");
			tFilter.setTitle("Check this box to see the films at these theaters");
			tFilter.setName(gID);
			boolean isShown = TheaterHandler.showGroup(gID);
			tFilter.setChecked(isShown);
			tFilter.addClickListener(this);
			theaterFlow.add(tFilter);

			// go thru all theaters and add to panel
			HashSet myList = (HashSet)TheaterHandler.getTheaterGroups().get(gID);
			Iterator iT = myList.iterator();		
			while (iT.hasNext())
			{
				String tID = (String)iT.next();
				//System.out.println("add theater "+tID);
				DaySchedWidget tSched = (DaySchedWidget) TheaterHandler.getTheaters().get(tID);
				tSched.addServerListener(new myServerlistener());

				// depending on visibility, show data or just add to panel (as hidden)
				if (isShown == true)
					tSched.displayDay(dayShown);
				else
					tSched.hideDay();
				
				mainHP.add(tSched);
			}


		}
		optPanel.add(theaterFlow);
		mainScroll.add(mainHP);
		outerHP.add(mainScroll);
		outerHP.setWidth("100%");
		
		mainVP.add(dayPanel);
		mainVP.add(optPanel);
		mainVP.add(outerHP);
	}
	
	
	/**
	 * Handles when filters are clicked.
	 * @param Widget the incoming button clicked.
	 */
	public void onClick(Widget sender)
	{		
	    // check which button is pressed
        if (sender == helpIcon)
        {
            // show help
            HelpWidget myHelp = new HelpWidget(3);   
        }
        else if (sender == soldOutFilter)
		{
			boolean checked = ((CheckBox) sender).isChecked();
			if (checked)
			{
				// go thru all groups to get to theaters
				Iterator i = TheaterHandler.getTheaterGroupIDs().iterator();		
				while (i.hasNext())
				{
					String gID = (String)i.next();
					
					// go thru all theaters and hide all sold out showings
					HashSet myList = (HashSet)TheaterHandler.getTheaterGroups().get(gID);
					Iterator iT = myList.iterator();		
					while (iT.hasNext())
					{
						String tID = (String)iT.next();
						DaySchedWidget tSched = (DaySchedWidget) TheaterHandler.getTheaters().get(tID);

						tSched.hideSoldout();
					}	
				}				
			}
			else
			{
				// go thru all groups to get to theaters
				Iterator i = TheaterHandler.getTheaterGroupIDs().iterator();		
				while (i.hasNext())
				{
					String gID = (String)i.next();
					
					// go thru all theaters and show all sold out showings
					HashSet myList = (HashSet)TheaterHandler.getTheaterGroups().get(gID);
					Iterator iT = myList.iterator();		
					while (iT.hasNext())
					{
						String tID = (String)iT.next();
						DaySchedWidget tSched = (DaySchedWidget) TheaterHandler.getTheaters().get(tID);

						tSched.showSoldout();
					}	
				}				
			}
		}
		else if (sender == nextDay)
		{
			if (dayShown < (Constants.MAX_DAYS - 1))
			{
				dayShown++;
				dayHdg.setHTML(utilTime.convertIndexToLongDay(dayShown));
				
				// go thru all groups to get to theaters
				Iterator i = TheaterHandler.getTheaterGroupIDs().iterator();		
				while (i.hasNext())
				{
					String gID = (String)i.next();
					
					// go thru all theaters and switch visible to next day
					HashSet myList = (HashSet)TheaterHandler.getTheaterGroups().get(gID);
					Iterator iT = myList.iterator();		
					while (iT.hasNext())
					{
						String tID = (String)iT.next();
						DaySchedWidget tSched = (DaySchedWidget) TheaterHandler.getTheaters().get(tID);
							
						// only switch if currently visible
						if (tSched.getVisibility())
						{
							tSched.displayDay(dayShown);
						}
					}	
				}
			}
			
		}
		else if (sender == prevDay)
		{
			hideWait();
			if (dayShown > 0)
			{
				dayShown--;
				dayHdg.setHTML(utilTime.convertIndexToLongDay(dayShown));
				
				// go thru all groups to get to theaters
				Iterator i = TheaterHandler.getTheaterGroupIDs().iterator();	
				while (i.hasNext())
				{
					String gID = (String)i.next();
					
					// go thru all theaters and switch visible to next day
					HashSet myList = (HashSet)TheaterHandler.getTheaterGroups().get(gID);
					Iterator iT = myList.iterator();		
					while (iT.hasNext())
					{
						String tID = (String)iT.next();
						DaySchedWidget tSched = (DaySchedWidget) TheaterHandler.getTheaters().get(tID);
						
						// only switch if currently visible
						if (tSched.getVisibility())
						{
							tSched.displayDay(dayShown);
						}
					}	
				}
			}
		}
		else if (sender.getTitle().equals("Check this box to see the films at this theater"))
		{
			// show/hide individual theater
			String keyID = ((CheckBox)sender).getName();
			boolean tChecked = ((CheckBox)sender).isChecked();
			int widCount = mainHP.getWidgetCount();
			for (int loopCtr=0; loopCtr<widCount; loopCtr++)
			{
				Widget tWidget = mainHP.getWidget(loopCtr);
				if (tWidget instanceof DaySchedWidget)
				{
					DaySchedWidget tSched = (DaySchedWidget)tWidget;
					if (tSched.getTheaterID().equals(keyID))
					{
						// found correct widget, now set visibility
						if (tChecked)
							tSched.displayDay(dayShown);
						else
							tSched.hideDay();
						
						// hide sold if checked
						if (soldOutFilter.isChecked())
							tSched.hideSoldout();						
						
						break;
					}
				}
			}
		}			
		else if (sender.getTitle().equals("Check this box to see the films at these theaters"))
		{
			// show/hide group of theaters
			String groupID = ((CheckBox)sender).getName();
			boolean tChecked = ((CheckBox)sender).isChecked();			
			
			// cycle thru all widgets and see if it is selected group
			int widCount = mainHP.getWidgetCount();
			for (int loopCtr=0; loopCtr<widCount; loopCtr++)
			{
				Widget tWidget = mainHP.getWidget(loopCtr);
				if (tWidget instanceof DaySchedWidget)
				{
					DaySchedWidget tSched = (DaySchedWidget)tWidget;
					if (TheaterHandler.isTheaterInGroup(tSched.getTheaterID(), groupID))
					{
						// found correct widget, now set visibility
						if (tChecked)
							tSched.displayDay(dayShown);
						else
							tSched.hideDay();
						
						// hide sold if checked
						if (soldOutFilter.isChecked())
							tSched.hideSoldout();	
					}
				}
			}
		}			
	}
	
	/**
	 * Hide the wait icon.
	 */
	private void hideWait()
	{
		waitPanel.setVisible(false);
	}
	
	/**
	 * Show the wait icon with description.
	 * @param String descriptive text to display (loading, updating, etc).
	 */
	private void showWait(String inDescr)
	{
		waitText.setHTML(inDescr + waitCount);
		waitPanel.setVisible(true);
	}
	
	/**
	 * Listener class to handle when server activity occurs.
	 */
	class myServerlistener implements ServerListener
	{
		/**
		 * When server activity starts.
		 * @param String description of activity.
		 */
		public void onServerStart(String inDescr)
		{			
			showWait(inDescr);
			waitCount++;
			//System.out.println("server start"+inDescr+" "+waitCount);
		}
		
		/**
		 * When server activity ends.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			sysErrors.setHTML("");
			waitCount--;
			//System.out.println("    server end "+waitCount);
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
			}
		}
		
		/**
		 * When server activity has an error.
		 * @param String actionID to identify server action.
		 * @param String description of error.
		 */
		public void onServerError(String actionID, String inError)
		{
			//System.out.println("SP: error "+inError);
			sysErrors.setHTML(inError);
			waitCount--;
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
			}
		}
	}

	/**
	 * Called when user logs out - clear out data.
	 */
	public void onLogOut() {
		//Reset ALL PRIVATE DATA;
		//even if there's no way to view this widget
		//the crumbs left in memory can be inspected
		//by an enterprising hacker with a JS debugger handy.
		//clear out text boxes, settings things, lists, etc.
		isLoaded = false;
		isUserDataLoaded = false;
		hourTbl = null;		
		optPanel.remove(theaterFlow);
		mainScroll.remove(mainHP);
		outerHP.remove(mainScroll);
		mainVP.remove(dayPanel);
		mainVP.remove(optPanel);
		mainVP.remove(outerHP);
		mainHP.clear();
	}

	/**
	 * Called when user logs in.
	 */
	public void onLogIn() {
		// do nothing
	} 
	
	/**
	 * Called just after this sink is shown.
	 */
	public void onShow() 
	{
		// check that everything is loaded
		if (isLoaded == true)
		{
			// data already loaded, see if user has since logged in
			if ((isUserDataLoaded == false) && (User.getTokenID() != 0))
			{
				// user has logged in since last loaded, reload data
				isUserDataLoaded = true;
				optPanel.remove(theaterFlow);
				mainScroll.remove(mainHP);
				outerHP.remove(mainScroll);
				mainVP.remove(dayPanel);
				mainVP.remove(optPanel);
				mainVP.remove(outerHP);
				mainHP.clear();
				setupGuide();
			}
		}
		else
		{
			// data hasn't been loaded, so load it
			if (User.getTokenID() != 0)
			{
				isUserDataLoaded = true;
			}
			setupGuide();
			isLoaded = true;
		}
	}
}
