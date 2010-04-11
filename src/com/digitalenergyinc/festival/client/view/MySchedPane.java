package com.digitalenergyinc.festival.client.view;

import java.util.HashMap;

import org.gwtwidgets.client.util.Location;
import org.gwtwidgets.client.util.WindowUtils;

import com.digitalenergyinc.fest.client.Constants;
import com.digitalenergyinc.fest.client.DataChangeListener;
import com.digitalenergyinc.fest.client.LogoutListener;
import com.digitalenergyinc.fest.client.ServerListener;
import com.digitalenergyinc.fest.client.control.ApptHandler;
import com.digitalenergyinc.fest.client.control.ICalHandler;
import com.digitalenergyinc.fest.client.control.MovieHandler;
import com.digitalenergyinc.fest.client.control.ScheduleHandler;
import com.digitalenergyinc.fest.client.control.SummaryHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.digitalenergyinc.fest.client.model.ApptRPC;
import com.digitalenergyinc.fest.client.model.ShowingRPC;
import com.digitalenergyinc.fest.client.model.UtilTimeRPC;
import com.digitalenergyinc.fest.client.widget.MySchedWidget;
import com.digitalenergyinc.festival.client.Sink;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.GlassPanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * This is the set of panels to manage the user's film schedule.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0 
 */ 
public class MySchedPane extends Sink implements ClickHandler, ChangeHandler,
		LogoutListener
{
	private VerticalPanel mainVP = 
		new VerticalPanel();  				// main panel
	private HorizontalPanel outerHP = 
		new HorizontalPanel();				// Outer panel containing hours and sched
	private HorizontalPanel mainHP = 
		new HorizontalPanel();				// Main panel for user schedule
	private HorizontalPanel titleHP = 
		new HorizontalPanel();				// Title panel (with appt button)
	private HorizontalPanel waitPanel =
		new HorizontalPanel();				// wait icon panel
	private HorizontalPanel sysPanel =
		new HorizontalPanel();				// system errors + wait icon panel
	private ScrollPanel mainScroll = new ScrollPanel();	// scroll panel for theaters 
	
	private VerticalPanel statusVP = 
		new VerticalPanel();  				// reservation status composite panel
	private HorizontalPanel statusHP = 
		new HorizontalPanel();  			// reserve status panel
	private VerticalPanel reschedVP = 
		new VerticalPanel();  				// reschedule msg panel
	
	private HorizontalPanel apptTitleHP =
        new HorizontalPanel();              // appt title panel
	private VerticalPanel apptVP = 
		new VerticalPanel();  				// appt outer panel
	private HorizontalPanel buttonHP = 
		new HorizontalPanel();  			// button panel
	
	private VerticalPanel exportVP = 
		new VerticalPanel();  				// export description panel
	private HorizontalPanel expButtonHP = 
		new HorizontalPanel();  			// export button panel
	
	private HTML waitText;					// wait text
	
	// images
	private Image waitIcon;					// wait GIF
	private String waitURL = "images/sd08-loader.gif";   // URL for wait icon
	private Image helpIcon;                       // help GIF
	private Image helpApptIcon;                   // help appt GIF
    private String helpURL = "images/help.gif";   // URL for help icon
	
	// System elements
	private VerticalPanel hourPanel;		// hour headings panel
	private HTML screenTitle;				// title of screen
	private HTML sysErrors = new HTML("");	// system errors (from server)

	// status elements
	private Label schedNScore = new Label("");  // normalized schedule score
	private Label schedScore = new Label("");   // schedule score
	private Button reschedButton = new Button("Create Schedule");
	private HTML reschedMsg = new HTML();	// resched msgs (like which is sold out)
	
	// schedule elements
	private HashMap schedWidget;			// schedule widgets (dayKey, widget)
	private HashMap schedKeys;				// Map of keys to schedule (order#, dayKey)
	private ScheduleHandler schedHandler;	// handler
	private MySchedWidget theSched;			// widget for my schedule
	private Button apptButton = new Button("Create Appointment");

	private Grid hourTbl;					// grid list of time slots (hour headings)
	private HTML titleSpacer = new HTML("<b>_</b>");	// title bar of widget
	
	// appointment elements
	private TextBox tbApptShort = new TextBox();	// short description
	private TextBox tbApptLong = new TextBox();		// long description
	private FestDatePicker pickApptDay;             // day for appointment
	private ListBox lbStartTime = new ListBox();	// starting time
	private ListBox lbEndTime = new ListBox();		// ending time
	private TextBox tbApptLocation = new TextBox();	// location description
	private TextBox tbApptNotes = new TextBox();	// notes
	private Button saveButton = new Button("Save Appointment");
	private Button cancelButton = new Button("Cancel Changes");
	private Button deleteButton = new Button("Delete Appointment");
	private ApptRPC myAppt;							// local appointment
	
	// google ical elements
	private Button exportButton = new Button("Export Schedule");
	private Button goButton = new Button("Continue");
	private Button noGoButton = new Button("Cancel");
	private String authURL = null;			// google authSub URL
	private HTML expDescr1;					// instructions
	private HTML   icalLink;				// formed URL for authSub
	private String returnURL="festival.html";
	
	private int waitCount = 0;				// counter of how many loading
	private boolean isLoaded = false;		// indicator if data is loaded
	private boolean apptCreated = false;	// indicator if appt panel set up
	
	// progress panel for rescheduling
	private GlassPanel glassPanel;          // glass panel to block out main page
	private VerticalPanel progressVP = new VerticalPanel(); // progress panel
	private ProgressBar totalBar;           // overall progress bar
	private ProgressBar methodBar;          // progress bar for sched method
	private HTML stepDescr = new HTML("");  // step description
	private MyPopup popup;                  // popup panel for sched status
	private static class MyPopup extends PopupPanel {

        public MyPopup() {
          // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
          // If this is set, the panel closes itself automatically when the user
          // clicks outside of it.
          super(false);

          // PopupPanel is a SimplePanel, so you have to set it's widget property to
          // whatever you want its contents to be.
        }
      }

	/**
	 * Every Sink needs an init that defines the menu title and description.
	 */
	public static SinkInfo init() {
		return new SinkInfo("My Schedule",	"") 
		{
			public Sink createInstance() {
				return new MySchedPane();
			}
		};
	}

	/**
	 * Basic setup for this panel.
	 */
	public MySchedPane() 
	{		
		// wait icon
		waitIcon = new Image(waitURL);		
		waitIcon.setTitle("Saving/Loading Data from server");
		waitText = new HTML("Loading...");
		waitText.setStyleName("sched-Film-Font");
		waitPanel.setVisible(false);
		waitPanel.add(waitIcon);
		waitPanel.add(waitText);
		sysPanel.add(waitPanel);
		sysPanel.add(sysErrors);
		
		// reschedule status panel
		HTML reschedTitle = new HTML("<b>Schedule Summary</b>");
		reschedTitle.setStyleName("sched-Film-Font");
		
		Grid schedTable = new Grid(2,2);
		HTML sched1 = new HTML("Score:");
		sched1.setTitle("The strength of your schedule (on scale of 0 to 100).");
		schedScore.setText(String.valueOf(SummaryHandler.getSchedScore()));
		HTML sched2 = new HTML("Relative Score:");
		sched2.setTitle("The sum of rankings of films on your schedule.");
		schedNScore.setText(String.valueOf(SummaryHandler.getSchedNScore()));
		
		schedTable.setStyleName("sched-Film-Font");
		schedTable.setWidget(0, 0, sched2);
		schedTable.setWidget(0, 1, schedScore);
		schedTable.setWidget(1, 0, sched1);
		schedTable.setWidget(1, 1, schedNScore);
		
		reschedButton.setStyleName("film-Button");
		reschedButton.addStyleName("film-Errors");
		reschedButton.setTitle("Click to create your schedule based on My Options.");
		//reschedButton.setEnabled(false);
		reschedButton.addClickHandler(this);
		
		
		reschedMsg.setStyleName("film-Errors");
		if (SummaryHandler.isRescheduleNeeded())
		{
			reschedMsg.setText(SummaryHandler.getReschedReasonMsg());
			reschedButton.setEnabled(true);
		}
		else
		{
			reschedMsg.setText("");
		}
		
		reschedVP.setStyleName("film-Boxed");
		reschedVP.add(reschedTitle);
		reschedVP.add(schedTable);
		reschedVP.add(reschedButton);
		reschedVP.add(reschedMsg);
		
		// title pane
		apptButton.setStyleName("film-Button");
		apptButton.setTitle("Click to create a personal appointment.");
		apptButton.addClickHandler(this);
		
		exportButton.setStyleName("film-Button");
		exportButton.setTitle("Click to export your schedule to a Google calendar.");
		exportButton.addClickHandler(this);
		
		screenTitle = new HTML("<b>My Schedule</b>");	
		titleHP.add(screenTitle);
		helpIcon = new Image(helpURL);     
        helpIcon.setTitle("Click here to see help for this screen.");
        helpIcon.setStyleName("film-Button2");
        helpIcon.addClickHandler(this);
        titleHP.add(helpIcon);
		titleHP.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
		titleHP.add(apptButton);
		titleHP.add(exportButton);
		
		// construct status panel
		statusHP.add(reschedVP);
		
		statusVP.add(sysPanel);
		statusVP.add(statusHP);
		statusVP.setStyleName("film-Divide");
		
		mainScroll.setWidth("100%");
		mainScroll.setHeight("1050px");
		
		sysErrors.addStyleName("film-Errors");
		

		titleSpacer.setStyleName("sched-Theater-Hdg");
		hourPanel = new VerticalPanel();
		hourPanel.setWidth("75px");
		hourPanel.setStyleName("sched-Cal-Box");
	
		mainVP.setStyleName("film-Sink");
		mainVP.addStyleName("film-Boxed");
				
		// set up listeners
		schedHandler = ScheduleHandler.instance();
		ScheduleHandler.addServerListener(new loadSchedlistener());
		ScheduleHandler.addSchedListener(new schedlistener());
		ScheduleHandler.addDataChangeListener(new mySelectionListener());
		User.addLogoutListener(this);
		
		initWidget(mainVP);
		ICalHandler ical = ICalHandler.instance();
		ICalHandler.addServerListener(new iCalListener());
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
		//outerHP.add(hourPanel);
		
		isLoaded = true;
		
		mainVP.add(statusVP);
		mainVP.add(titleHP);
		mainVP.setHorizontalAlignment(HorizontalPanel.ALIGN_LEFT);
		
		mainScroll.add(mainHP);
		//outerHP.add(mainScroll);
		outerHP.setWidth("100%");
		mainVP.add(outerHP);
		
		// set up each day in schedule for a user (if not created already)
		if (!ScheduleHandler.isLoaded())
		{
			ScheduleHandler.loadMySchedule(true);
		}
		else
		{
			// already loaded, so make sure display is updated
			ServerListener x = new loadSchedlistener();
			x.onServerEnd(null, null);
		}
	}
	
	/**
     * This recreates the film guide (like after data is read or changed).
     */
    private void recreateGuide()
    {
        mainHP.clear();
        outerHP.clear();
        mainScroll.clear();
        sysErrors.setHTML("");
        waitCount--;
        if (waitCount <= 0)
        {
            hideWait();
            waitCount = 0;
        }
        
        // get hash of widgets
        schedKeys = ScheduleHandler.getMySchedKeys();
        schedWidget = ScheduleHandler.getMySchedHash();     
        // load up in panel
        int orderNum = schedKeys.size();        
        for (int loopCtr=0; loopCtr < orderNum; loopCtr++)
        {
            String sID = (String)schedKeys.get(String.valueOf(loopCtr));
            theSched = (MySchedWidget) schedWidget.get(sID);
            mainHP.add(theSched);
        }   
        
        // update scores
        schedScore.setText(String.valueOf(SummaryHandler.getSchedScore()));
        schedNScore.setText(String.valueOf(SummaryHandler.getSchedNScore()));
        if (SummaryHandler.isRescheduleNeeded())
        {
            reschedMsg.setText(SummaryHandler.getReschedReasonMsg());
            reschedButton.setEnabled(true);
        }
        else
        {
            reschedMsg.setText("");
            reschedButton.setEnabled(true);
        }
        
        outerHP.add(hourPanel);
        outerHP.add(mainScroll);
        mainScroll.add(mainHP);
    }
	
	/**
	 * This sets up the appointment creation panel.
	 */
	private void setupAppt()
	{
		// *******************************
		// set up meal break panel
		// *******************************
		HTML hdgAppt = new HTML("<b>My Appointment</b>");
		HTML hdgShort = new HTML("Short Description:");
		HTML hdgLong = new HTML("Long Description:");
		HTML hdgApptDay = new HTML("Date:");
		HTML hdgApptStart = new HTML("Start Time:");
		HTML hdgApptEnd = new HTML("End Time:");
		HTML hdgLocation = new HTML("Location:");
		HTML hdgNotes = new HTML("Notes:");
	    
	    // set formatting.
	    lbStartTime.setTitle("Set the start time of the appointment.");
	    lbEndTime.setTitle("Set the end time of the appointment.");
	    hdgAppt.setStyleName("sched-Film-Font");
	    hdgShort.setStyleName("sched-Film-Font");
	    hdgLong.setStyleName("sched-Film-Font");
	    hdgApptDay.setStyleName("sched-Film-Font");
	    hdgApptStart.setStyleName("sched-Film-Font");
	    hdgApptEnd.setStyleName("sched-Film-Font");
	    hdgLocation.setStyleName("sched-Film-Font");
	    hdgNotes.setStyleName("sched-Film-Font");

	    // set up Appointment Day		
		String pickTip1 = "The day of the appointment.";
        pickApptDay = new FestDatePicker(pickTip1, "start", Constants.FESTIVAL_START_DATE);
        pickApptDay.addDataChangeListener(new myApptDayListener());
		
		// set up start and end time
		String hdgArray[] = {"12:00AM", "12:30AM", "01:00AM", "01:30AM", "02:00AM", "02:30AM", "03:00AM", "03:30AM", "04:00AM", "04:30AM", 
                "05:00AM", "05:30AM", "06:00AM", "06:30AM", "07:00AM", "07:30AM", "08:00AM", "08:30AM",
                "09:00AM", "09:30AM", "10:00AM", "10:30AM", "11:00AM", "11:30AM", "12:00PM", "12:30PM",
                "01:00PM", "01:30PM", "02:00PM", "02:30PM", "03:00PM", "03:30PM", "04:00PM", "04:30PM",
                "05:00PM", "05:30PM", "06:00PM", "06:30PM", "07:00PM", "07:30PM", "08:00PM", "08:30PM",
                "09:00PM", "09:30PM", "10:00PM", "10:30PM", "11:00PM", "11:30PM"   };

		// put 8AM to 12AM in the drop down boxes
		for (int loopCtr=16; loopCtr < 48; loopCtr++)
		{
			lbStartTime.addItem(hdgArray[loopCtr]);
			lbEndTime.addItem(hdgArray[loopCtr]);
		}
		for (int loopCtr=0; loopCtr < 1; loopCtr++)
		{
			lbStartTime.addItem(hdgArray[loopCtr]);
			lbEndTime.addItem(hdgArray[loopCtr]);
		}
	    	
		// make drop down list and set initial value
		lbStartTime.setVisibleItemCount(1);
		lbEndTime.setVisibleItemCount(1);
		lbStartTime.setSelectedIndex(0);
		lbEndTime.setSelectedIndex(32);
		
		// set up listeners
		tbApptShort.addChangeHandler(this);
		tbApptLong.addChangeHandler(this);
		lbStartTime.addChangeHandler(this);
		lbEndTime.addChangeHandler(this);
		tbApptLocation.addChangeHandler(this);
		tbApptNotes.addChangeHandler(this);

		
	    // load in panels
	    FlexTable  gMeal = new FlexTable();
	    gMeal.setWidget(0, 1, hdgShort);
	    gMeal.setWidget(0, 2, tbApptShort);
	    
	    gMeal.setWidget(1, 1, hdgLong);
	    gMeal.setWidget(1, 2, tbApptLong);
	    gMeal.getFlexCellFormatter().setColSpan(1, 2, 2);
	    
	    gMeal.setWidget(2, 1, hdgApptDay);
	    gMeal.setWidget(2, 2, pickApptDay);
	    
	    gMeal.setWidget(3, 1, hdgApptStart);
	    gMeal.setWidget(3, 2, lbStartTime);
	    
	    gMeal.setWidget(4, 1, hdgApptEnd);
	    gMeal.setWidget(4, 2, lbEndTime);   
	    
	    gMeal.setWidget(5, 1, hdgLocation);
	    gMeal.setWidget(5, 2, tbApptLocation);
	    gMeal.getFlexCellFormatter().setColSpan(5, 2, 2);
	    
	    gMeal.setWidget(6, 1, hdgNotes);
	    gMeal.setWidget(6, 2, tbApptNotes);
	    gMeal.getFlexCellFormatter().setColSpan(6, 2, 2);
		
	    helpApptIcon = new Image(helpURL);     
	    helpApptIcon.setTitle("Click here to see help for this screen.");
	    helpApptIcon.setStyleName("film-Button2");
	    helpApptIcon.addClickHandler(this);
        
	    apptTitleHP.add(hdgAppt);
	    apptTitleHP.add(helpApptIcon);
		apptVP.setStyleName("opt-Boxed");
		apptVP.add(apptTitleHP);
		apptVP.add(gMeal);
		
		// buttons
		saveButton.setTitle("Save your appointment.");
		saveButton.setStyleName("film-Button");
		saveButton.setEnabled(false);  
		saveButton.addClickHandler(this);			
		buttonHP.add(saveButton);
		
		cancelButton.setTitle("Cancel appointment.");
		cancelButton.setStyleName("film-Button");
		cancelButton.setEnabled(true);  
		cancelButton.addClickHandler(this);			
		buttonHP.add(cancelButton);
		
		deleteButton.setTitle("Delete appointment.");
		deleteButton.setStyleName("film-Button");
		deleteButton.setEnabled(true);  
		deleteButton.setVisible(false);		// only show if editing
		deleteButton.addClickHandler(this);			
		buttonHP.add(deleteButton);
		
		buttonHP.setStyleName("opt-Boxed");
		buttonHP.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		apptVP.add(buttonHP);
		
		// add to main panel
		mainVP.remove(titleHP);		
		mainVP.remove(outerHP);
		mainVP.add(apptVP);
		
		apptCreated = true;
	}
	
	/**
	 * This sets up the export explanation panel.
	 */
	private void setupExport()
	{
		HTML hdgExport = new HTML("<b>Export Schedule</b>");
		expDescr1 = new HTML("You can export your schedule to a Google calendar" +
				" of your choice.  If you click Continue, you will be brought to" +
				" a page to log in to your Google calendar.  Then you will be" +
				" returned to this page after the export has been completed.");
	    
	    // set formatting
	    expDescr1.setStyleName("sched-Film-Font");		
		
	    // load in panel
		exportVP.setStyleName("opt-Boxed");
		exportVP.add(hdgExport);
		exportVP.add(expDescr1);
		
		// set up link to go to auth now (fill in later)
		icalLink = new HTML("");
		exportVP.add(icalLink);
		
		// buttons
		goButton.setTitle("Continue with Export");
		goButton.setStyleName("film-Button");
		goButton.setEnabled(true);  
		goButton.addClickHandler(this);			
		expButtonHP.add(goButton);
		
		noGoButton.setTitle("Cancel export");
		noGoButton.setStyleName("film-Button");
		noGoButton.setEnabled(true);  
		noGoButton.addClickHandler(this);			
		expButtonHP.add(noGoButton);
		
		expButtonHP.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		exportVP.add(expButtonHP);
		
		// add to main panel
		mainVP.remove(titleHP);		
		mainVP.remove(outerHP);
		mainVP.add(exportVP);
	}
	
	/**
	 * Step 1 of export is to get authorization URL (authSub).
	 */
	private void exportStep1()
	{		
		if (authURL == null)
		{
			// need token;  have user log in for us to get token
			ICalHandler.signin(returnURL);				
			// **Look for answer in call back!
		}	
		else
		{
		    // got token, go ahead with export
		    ShowingRPC myShow = new ShowingRPC();
            myShow.setMovieCode("WACK");
            myShow.setMovieTitle("The Wackness");
            myShow.setTheaterCode("UPTN");
            ICalHandler.createEntry(myShow);
		}
	}
	
	/**
	 * Step 2 of export is to open up frame to allow user to log in to google.
	 */
	private void exportStep2()
	{		
		Frame frame = new Frame(authURL);
		frame.setHeight("400px");
		frame.setWidth("80%");
		exportVP.add(frame);
	}
	
	/**
     * Setup progress panel.
     */
	private void setupProgress()
	{
	    // create VP to hold everything and then add to popup
        progressVP = new VerticalPanel();
        
        // set up progress panel and progress bar
        HTML wait = new HTML("<b>Creating Schedule, please wait.</b>");
        wait.setStyleName("sched-Film-Font");
        
        stepDescr.setHTML("Reading Showings");
        stepDescr.setStyleName("sched-Film-Font");
        progressVP.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        progressVP.setStyleName("film-Popup");
        
        // create progress bars
        totalBar = new ProgressBar(0.0, 100.0, 0.0);
        totalBar.setWidth("150px");
        totalBar.setProgress(0);
        
        methodBar = new ProgressBar(0.0, 100.0, 0.0);
        methodBar.setWidth("150px");
        methodBar.setProgress(0);
        
        // create headings
        HTML descrHdg = new HTML("<b>Action:</b>");
        descrHdg.setStyleName("sched-Film-Font");
        descrHdg.addStyleName("film-SpacerRight");
        HTML methodHdg = new HTML("<b>Step:</b>");
        methodHdg.setStyleName("sched-Film-Font");
        methodHdg.addStyleName("film-SpacerRight");
        HTML totalHdg = new HTML("<b>Overall:</b>");
        totalHdg.setStyleName("sched-Film-Font");
        totalHdg.addStyleName("film-SpacerRight");
        
        // create grid to make it pretty
        Grid barGrid = new Grid(4, 2);
        barGrid.setWidget(0, 0, descrHdg);
        barGrid.setWidget(0, 1, stepDescr);
        barGrid.setWidget(2, 0, methodHdg);
        barGrid.setWidget(2, 1, methodBar);
        barGrid.setWidget(3, 0, totalHdg);
        barGrid.setWidget(3, 1, totalBar);
        
        // construct screen
        progressVP.add(wait);
        progressVP.add(barGrid);
        popup = new MyPopup();
        popup.setWidget(progressVP);

        // Create a glass panel with `autoHide = false`
        glassPanel = new GlassPanel(false);
        glassPanel.setStyleName("gwt-GlassPanel");
        RootPanel.get().add(glassPanel, 0, 0);
        int cnt = RootPanel.get().getWidgetIndex(glassPanel);

        // Position the popup 1/3rd of the way down and across the screen, and
        // show the popup. Since the position calculation is based on the
        // offsetWidth and offsetHeight of the popup, you have to use the
        // setPopupPositionAndShow(callback) method.
        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 3;
                int top = (Window.getClientHeight() - offsetHeight) / 3;
                popup.setPopupPosition(left, top);
            }
        });        
	}
	
	/**
     * Remove progress panel.
     */
    private void hideProgress()
    {
        if (popup != null)
            popup.hide();
        if (glassPanel != null)
            glassPanel.removeFromParent();
    }
	
	/**
	 * Reschedules incrementally, letting UI breathe and showing a progress window.
	 */
	private void reschedIncr()
	{       
	    // do scheduling in pieces.
        DeferredCommand.addCommand(new IncrementalCommand() {

            protected int start = 0;
            
            /*
             * (non-Javadoc)
             * @see com.google.gwt.user.client.IncrementalCommand#execute()
             */
            public boolean execute() 
            {                
                //System.out.println("incoming "+inStart+" to "+inEnd+" max:"+max);
                //System.out.println("displaying "+start+" to "+end+" rqst: "+rqstRow);
                // call handler to create schedule
                ScheduleHandler.rescheduleIncr();

                // Is there more to do?
                if (ScheduleHandler.isMoreToSchedule() == false)
                {
                    // finish up
                    totalBar.setProgress(100);
                    return false;    // no more to do!
                    // saving will result in loadSchedListener being called
                }

                // update progress bar
                stepDescr.setHTML(ScheduleHandler.getStepDescr());
                methodBar.setProgress(ScheduleHandler.getPctComplete());
                totalBar.setProgress(ScheduleHandler.getPctCompleteOverall());
                
                // more to do!
                return true;
            }
        });              	      
    }
    
	/**
	 * Handles when buttons are clicked.
	 * @param ClickEvent the incoming object that was clicked.
     */
    public void onClick(ClickEvent event)
    {
        Widget sender = (Widget) event.getSource();
        // check which button is pressed
        if (sender == helpIcon)
        {
            // show help
            HelpWidget myHelp = new HelpWidget(4);   
        }
        else if (sender == helpApptIcon)
        {
            // show help
            HelpWidget myHelp = new HelpWidget(7);   
        }
        else if (sender == reschedButton)
		{
			reschedButton.setEnabled(false);
			setupProgress();
			ScheduleHandler.schedPrep();
			// when prep is done, schedListener is called		
		}
		else if (sender == apptButton)
		{
			if (apptCreated == false)
			{
				myAppt = new ApptRPC();
				setupAppt();
			}
			else
			{
				// clear out fields and redisplay
				myAppt.clear();
				pickApptDay.setSelectedDayIndex(0);
				lbStartTime.setSelectedIndex(0);
				lbEndTime.setSelectedIndex(32);
				tbApptShort.setText("");
				tbApptLong.setText("");
				tbApptLocation.setText("");
				tbApptNotes.setText("");
				cancelButton.setEnabled(true);
				deleteButton.setVisible(false);
				
				mainVP.remove(titleHP);		
				mainVP.remove(outerHP);
				mainVP.add(apptVP);
			}
		}
		else if (sender == exportButton)
		{
			// export schedule            
			setupExport();
			exportButton.setVisible(false);
			mainVP.remove(titleHP);		
			mainVP.remove(outerHP);
			mainVP.add(exportVP);			
		}
		else if (sender == cancelButton)
		{
			// cancel update of appt edit/create
			deleteButton.setVisible(false);
			mainVP.remove(apptVP);
			mainVP.add(titleHP);		
			mainVP.add(outerHP);			
		}
		else if (sender == saveButton)
		{
			// save appt
			saveButton.setEnabled(false);
			cancelButton.setEnabled(false);
			deleteButton.setVisible(false);
			ApptHandler apptHandler = ApptHandler.instance();
			ApptHandler.addServerListener(new saveApptListener());
			ApptHandler.saveMyAppt(myAppt);	
			// wait for result in listener!
		}
		else if (sender == deleteButton)
		{
			// delete appt
			deleteButton.setVisible(false);
			saveButton.setEnabled(false);
			cancelButton.setEnabled(false);
			ApptHandler apptHandler = ApptHandler.instance();
			ApptHandler.addServerListener(new deleteApptListener());
			ApptHandler.deleteMyAppt(myAppt.getApptID());	
			// wait for result in listener!
		}
		else if (sender == noGoButton)
		{
			// cancel the export
			noGoButton.setEnabled(false);
			exportVP.clear();
			mainVP.remove(exportVP);
			mainVP.add(titleHP);		
			mainVP.add(outerHP);			
		}
		else if (sender == goButton)
		{
			// continue with the export
			// for now, just return
			goButton.setEnabled(false);
			System.out.println("Go update ical");
			exportStep1();
		}
	}
	
	/**
	 * Handles when fields or options are changed.
	 * @param ChangeEvent the incoming event.
     */
    public void onChange(com.google.gwt.event.dom.client.ChangeEvent event)
    {
        Widget sender = (Widget) event.getSource();
		if (sender == tbApptShort)
		{
			//System.out.println("Index:"+tbApptShort.getText());
			myAppt.setShortName(tbApptShort.getText());
		}	
		else if (sender == tbApptLong)
		{
			//System.out.println("Index:"+tbApptLong.getText());
			myAppt.setLongName(tbApptLong.getText());
		}	
		else if (sender == lbStartTime)
		{
			System.out.println("Index:"+lbStartTime.getSelectedIndex());
			String tempTime = lbStartTime.getItemText(lbStartTime.getSelectedIndex());
			String strTime = "";
			if (tempTime.substring(5).equalsIgnoreCase("PM"))
			{
				int intTime = (Integer.parseInt(tempTime.substring(0, 2))) + 12;
				String tempHr = String.valueOf(intTime);
				strTime = tempHr + ":"+ tempTime.substring(3,5) + ":00";
			}
			else
			{
				strTime = tempTime.substring(0, 5) + ":00";
			}
			System.out.println("Time:"+strTime);
			myAppt.setStartTime(strTime);
			myAppt.setStartTimeIndex(lbStartTime.getSelectedIndex()+16);
			
			// check for error condition
			if (lbStartTime.getSelectedIndex() > lbEndTime.getSelectedIndex())
				sysErrors.setHTML("Start Time must be before End Time");
			else
			{
				sysErrors.setHTML("");
				
				// calculate running minutes
				int segments = (lbEndTime.getSelectedIndex() - 
						lbStartTime.getSelectedIndex()) * 30;
				myAppt.setRunningMin(segments);
			}
		}	
		else if (sender == lbEndTime)
		{
			System.out.println("Index:"+lbEndTime.getSelectedIndex());
			String tempTime = lbEndTime.getItemText(lbEndTime.getSelectedIndex());
			String strTime = "";
			if (tempTime.substring(5).equalsIgnoreCase("PM"))
			{
				int intTime = (Integer.parseInt(tempTime.substring(0, 2))) + 12;
				String tempHr = String.valueOf(intTime);
				strTime = tempHr + ":"+ tempTime.substring(3,5) + ":00";
			}
			else
			{
				strTime = tempTime.substring(0, 5) + ":00";
			}
			System.out.println("Time:"+strTime);
			myAppt.setEndTime(strTime);
			
			// check for error condition
			if (lbStartTime.getSelectedIndex() > lbEndTime.getSelectedIndex())
				sysErrors.setHTML("End Time must be after Start Time");
			else
			{
				sysErrors.setHTML("");
				
				// calculate running minutes
				int segments = (lbEndTime.getSelectedIndex() - 
						lbStartTime.getSelectedIndex()) * 30;
				myAppt.setRunningMin(segments);
			}
		}	
		else if (sender == tbApptLocation)
		{
			System.out.println("Index:"+tbApptLocation.getText());
			myAppt.setLocationText(tbApptLocation.getText());
		}
		else if (sender == tbApptNotes)
		{
			System.out.println("Index:"+tbApptNotes.getText());
			myAppt.setNotesText(tbApptNotes.getText());
		}
		
		checkRequired();
	}
	
	/**
	 * If all required fields are not blank, enable save button.
	 */
	private void checkRequired()
	{
		if ((!tbApptShort.getText().equalsIgnoreCase("")) &&
				(!tbApptLong.getText().equalsIgnoreCase("")) &&
				(!tbApptLocation.getText().equalsIgnoreCase("")) &&
				(sysErrors.getText().equalsIgnoreCase("")))
		{
			if (saveButton.isEnabled() == false)
				saveButton.setEnabled(true);
		}
		else
		{
			if (saveButton.isEnabled())
				saveButton.setEnabled(false);
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
		waitText.setHTML(inDescr);
		waitPanel.setVisible(true);
	}
	
	/**
	 * Listener class to handle when schedule is loaded.
	 */
	class loadSchedlistener implements ServerListener
	{
		/**
		 * Called when server activity starts.
		 * @param String descriptive text to display (loading, updating, etc).
		 */
		public void onServerStart(String inDescr)
		{
			showWait(inDescr);
			waitCount++;
		}
		
		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
		    recreateGuide();
		}
		
		/**
		 * Show the error message from server.
		 * @param String actionID to identify server action.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String actionID, String inError)
		{
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
     * Listener class to handle when schedule is created.
     */
    class schedlistener implements ServerListener
    {
        /**
         * Called when scheduling activity starts.
         * @param String descriptive text to display (loading, updating, etc).
         */
        public void onServerStart(String inDescr)
        {
            showWait(inDescr);
            waitCount++;
        }
        
        /**
         * Called when scheduling prep activity ends. 
         * @param String actionID to identify server action.
         * @param object result - for this listener, no result expected.
         */
        public void onServerEnd(String actionID, Object result)
        {
            // called after schedule data is loaded            
            if (actionID.equalsIgnoreCase("PREP"))
            {
                // now create schedule incrementally
                reschedIncr();
            }
            else if (actionID.equalsIgnoreCase("SAVE"))
            {
                // update data & screen
                recreateGuide();
                hideProgress();
            }
        }
        
        /**
         * Show the error message from server.
         * @param String actionID to identify server action.
         * @param String descriptive error text to display.
         */
        public void onServerError(String actionID, String inError)
        {
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
	 * Listener class to handle when saving Appointment occurs.
	 */
	class saveApptListener implements ServerListener
	{
		/**
		 * Called when server activity starts.
		 * @param String descriptive text to display (loading, updating, etc).
		 */
		public void onServerStart(String inDescr)
		{
			showWait(inDescr);
			waitCount++;
		}
		
		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			// remove appointment panels and reinstate schedule
			mainVP.remove(apptVP);
			mainVP.add(titleHP);		
			mainVP.add(outerHP);
			sysErrors.setHTML("");
			waitCount--;
			
			// if saved appointment is an update, remove it from schedule first
			boolean isNew = false;
			if (myAppt.getApptID() == 0)
				isNew = true;
			
			if (isNew == false)
				ScheduleHandler.removeAppt(myAppt);
			
			myAppt = ApptHandler.getMyAppt();
			
			// add new appt to schedule
			ScheduleHandler.addAppt(myAppt);
			// wait for result in server listener!
		}
		
		/**
		 * Show the error message from server.
		 * @param String actionID to identify server action.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String actionID, String inError)
		{
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
	 * Listener class to handle when deleting Appointment occurs.
	 */
	class deleteApptListener implements ServerListener
	{
		/**
		 * Called when server activity starts.
		 * @param String descriptive text to display (loading, updating, etc).
		 */
		public void onServerStart(String inDescr)
		{
			showWait(inDescr);
			waitCount++;
		}

		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			// remove appointment panels and reinstate schedule
			mainVP.remove(apptVP);
			mainVP.add(titleHP);		
			mainVP.add(outerHP);
			sysErrors.setHTML("");
			waitCount--;

			// add new appt to schedule
			ScheduleHandler.removeAppt(myAppt);
			// wait for result in listener!
		}

		/**
		 * Show the error message from server.
		 * @param String actionID to identify server action.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String actionID, String inError)
		{
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
	 * Listener class used to take action when data has changed or selected.
	 */
	class mySelectionListener implements DataChangeListener
	{
		/**
		 * Used to take action when data has changed .
		 */
		public void onDataChange()
		{
			// make sure data on panels is updated
			ServerListener x = new loadSchedlistener();
			x.onServerEnd(null, null);
		}
		
		/**
		 * Used to take action when data is selected.
		 * @param ShowingRPC  the item changed or selected (optional).
		 */
		public void onDataSelected(ShowingRPC inItem)
		{
			// if appointment selected, display details to edit or delete
			if (inItem.getTicketType().equalsIgnoreCase("Z"))
			{
				if (apptCreated == false)
				{
					myAppt = new ApptRPC();
					setupAppt();
				}
				else
				{
					// clear out fields and redisplay
					myAppt.clear();
				}
				
				// fill fields in Appt
				myAppt.clear();
				myAppt.setApptID(inItem.getShowingID());
				myAppt.setLocationText(inItem.getTheaterCode());
				myAppt.setLongName(inItem.getMovieTitle());
				// TODO figure out if reading from db to get notes or removing notes
				//myAppt.setNotesText(inName);
				myAppt.setRunningMin(inItem.getRunningMin());
				myAppt.setShortName(inItem.getMovieCode());
				myAppt.setStartDate(inItem.getStartDate());
				myAppt.setStartDayIndex(inItem.getDayIndex());
				myAppt.setStartTime(inItem.getStartTime());
				myAppt.setStartTimeIndex(inItem.getTimeIndex());
				myAppt.setUserID(User.getUserNum());
				
				// calc ending time (and indices for drop down)
				UtilTimeRPC utilTime = new UtilTimeRPC();
				int timeIndex = utilTime.convertTimeToIndex8(inItem.getStartTime());
				int endRow = utilTime.calcIntervals(inItem.getRunningMin()) + timeIndex;
				String tempTime = lbEndTime.getItemText(endRow);
				String strTime = "";
				if (tempTime.substring(5).equalsIgnoreCase("PM"))
				{
					int intTime = (Integer.parseInt(tempTime.substring(0, 2))) + 12;
					String tempHr = String.valueOf(intTime);
					strTime = tempHr + ":"+ tempTime.substring(3,5) + ":00";
				}
				else
				{
					strTime = tempTime.substring(0, 5) + ":00";
				}
				//System.out.println("Time:"+strTime);
				myAppt.setEndTime(strTime);
				
				// fill fields on panel
				pickApptDay.setSelectedDayIndex(inItem.getDayIndex());
				tbApptShort.setText(inItem.getMovieCode());
				tbApptLong.setText(inItem.getMovieTitle());
				tbApptLocation.setText(inItem.getTheaterCode());
				tbApptNotes.setText("");
				// TODO figure out if reading from db to get notes or removing notes
				
				lbStartTime.setSelectedIndex(timeIndex);				
				lbEndTime.setSelectedIndex(endRow);

				cancelButton.setEnabled(true);
				deleteButton.setVisible(true);
				mainVP.remove(titleHP);		
				mainVP.remove(outerHP);
				mainVP.add(apptVP);

			}
			else
			{
				// found film, set ID to view detail and send them to film list
				MovieHandler.setFilmDetailID(inItem.getMovieCode());
				History.newItem("Films");
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
		hourTbl = null;
		hourPanel.clear();
		mainVP.clear();
		mainScroll.clear();
	}

	/**
	 * Called when user logs in.
	 */
	public void onLogIn() {
		// do nothing
	} 
	
	/**
     * Listener class used to take action when appt day has changed.
     */
    class myApptDayListener implements DataChangeListener
    {
        /**
         * Used to take action when data has changed.
         */
        public void onDataChange()
        {
            // change appt date
            //System.out.println("Index:"+pickApptDay.getSelectedDayIndex());
            myAppt.setStartDayIndex(pickApptDay.getSelectedDayIndex());
            checkRequired();
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
	 * Listener class to handle after getting auth URL.
	 */
	class iCalListener implements ServerListener
	{
		/**
		 * Called when server activity starts.
		 * @param String descriptive text to display (loading, updating, etc).
		 */
		public void onServerStart(String inDescr)
		{
			showWait(inDescr);
			//waitCount++;
		}

		/**
		 * Called when server activity ends.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, String authURL.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			sysErrors.setHTML("");

			waitCount = 0;
			hideWait();		
			
			// get resulting Token from server
			authURL = (String) result;
			System.out.println("authToken="+authURL);
			expDescr1.setHTML("Found token: "+authURL);  
			//String tempStr = "<a href="+authURL+">Authorize</a>";
			//icalLink.setHTML(tempStr);
			goButton.setEnabled(true);  // temp set to true.
			//expDescr1.setHTML("Ok, click on the link below to log in to your " +
			//		"Google calendar.");
			//exportStep2();
		}

		/**
		 * Show the error message from server.
		 * @param String actionID to identify server action.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String actionID, String inError)
		{
			sysErrors.setHTML(inError);
			goButton.setEnabled(false);
			waitCount--;
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
			}
		}
	}	
	
	/**
	 * Called just after this sink is shown.
	 */
	public void onShow() 
	{	    
		// if there is an authURL, we may be returning from the authorization
		// to export to their google calendar
		//if (authURL != null)
		//{
			// check if we've returned from getting authorization 
			// to export calendar by checking for token parameter in the URL
			Location loc = WindowUtils.getLocation();
			String requestParam = loc.getParameter("token");


			if (requestParam != null)
			{
				// temporarily reshow this screen with the parm
				setupExport();
				expDescr1.setHTML("Found parm: "+requestParam);	
				mainVP.remove(titleHP);		
				mainVP.remove(outerHP);				
				mainVP.add(exportVP);		
				// token found, so remove export screen and sign in again
                // to save token to cookie
				ICalHandler.signin(returnURL);   
			}
			else
			{			    
				mainVP.remove(exportVP);
				mainVP.add(titleHP);		
				mainVP.add(outerHP);	
				exportVP.clear();			
			}
		//}
		
		// check that everything is loaded (after check that user is logged in)
		if (User.getTokenID() == 0)
		{
			User.setError("You must log in to create/view your schedule.");
			History.newItem("Home");
		}
		else if (isLoaded == false)
			setupGuide();
	}
}
