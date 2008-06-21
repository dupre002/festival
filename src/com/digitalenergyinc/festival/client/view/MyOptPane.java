package com.digitalenergyinc.festival.client.view;

import com.digitalenergyinc.fest.client.Constants;
import com.digitalenergyinc.fest.client.DataChangeListener;
import com.digitalenergyinc.fest.client.LogoutListener;
import com.digitalenergyinc.fest.client.ServerListener;
import com.digitalenergyinc.festival.client.Sink;
import com.digitalenergyinc.fest.client.control.SchedPolicy;
import com.digitalenergyinc.fest.client.control.ScheduleHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.digitalenergyinc.fest.client.model.ShowingRPC;
import com.digitalenergyinc.fest.client.model.UtilTimeRPC;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.GlassPanel;
import com.google.gwt.widgetideas.client.ProgressBar;

/**
 * This is the set of panels to manage the user's schedule options.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class MyOptPane extends Sink implements ClickListener, ChangeListener,
	LogoutListener
		
{
	private VerticalPanel mainOptVP = 
		new VerticalPanel();  				// main panel
	private HorizontalPanel mainOptHP = 
		new HorizontalPanel();				// Main panel for user schedule
	private HorizontalPanel waitPanel =
		new HorizontalPanel();				// wait icon panel
	private HorizontalPanel sysPanel =
		new HorizontalPanel();				// system errors + wait icon panel
	private VerticalPanel leftVP = 
		new VerticalPanel();  				// left side panel
	private VerticalPanel rightVP = 
		new VerticalPanel();  				// right side panel
	private VerticalPanel daysVP = 
		new VerticalPanel();  				// start/end days outer panel
	private VerticalPanel timeVP = 
		new VerticalPanel();  				// start/end time outer panel
	private VerticalPanel filmsVP = 
		new VerticalPanel();  				// films per day outer panel
	private VerticalPanel redeyeVP = 
		new VerticalPanel();  				// red eye outer panel
	private VerticalPanel btwnVP = 
		new VerticalPanel();  				// between minutes outer panel
	private VerticalPanel mealVP = 
		new VerticalPanel();  				// meal break outer panel
	private VerticalPanel tixVP = 
		new VerticalPanel();  				// tickets per film outer panel
	private VerticalPanel theatersVP = 
		new VerticalPanel();  				// theaters outer panel
	private HorizontalPanel buttonHP = 
		new HorizontalPanel();  			// button panel

	private HTML waitText;					// wait text

	// images
	private Image waitIcon;					// wait GIF
	private String waitURL = "images/sd08-loader.gif";   // URL for wait icon
	private Image helpIcon;                       // help GIF
	private String helpURL = "images/help.gif";   // URL for help icon

	// System elements
	private HTML screenTitle;               // title of screen
	private HTML sysErrors = new HTML("");	// system errors (from server)
	private Button schedButton = new Button("Create Schedule");
	private Button saveButton = new Button("Save Options");
	private Button cancelButton = new Button("Cancel Changes");
	
	// Options
	private FestDatePicker pickStart;               // starting day
	private FestDatePicker pickEnd;                 // ending day
	private ListBox lbStartTime = new ListBox();	// starting time
	private ListBox lbEndTime = new ListBox();		// ending time
	private RadioButton rbRedEyeYes = new RadioButton("RedEyeGroup", "Yes");
    private RadioButton rbRedEyeNo = new RadioButton("RedEyeGroup", "No");
    private RadioButton rbMealYes = new RadioButton("MealGroup", "Yes");
    private RadioButton rbMealNo = new RadioButton("MealGroup", "No");
    private ListBox lbMealMin = new ListBox();		// minutes for meal
    private ListBox lbMealStart = new ListBox();	// meal start time
    private ListBox lbMealEnd = new ListBox();		// meal end time
    private ListBox lbTix = new ListBox();			// tickets per show
    private ListBox lbBetween = new ListBox();		// between time
    private ListBox lbFilms = new ListBox();		// film per day limit
    
    private CheckBox cbPC = new CheckBox("Park City Theaters");
    private CheckBox cbSLC = new CheckBox("Salt Lake City Theaters");
    private CheckBox cbSV = new CheckBox("Sundance Village Theaters");
    private CheckBox cbOG = new CheckBox("Ogden Theaters");

	// schedule elements
	ScheduleHandler schedHandler;			// handler

	private boolean isLoaded = false;		// indicator if data is loaded
	private boolean optChanged = false;		// indicator if any options changed
	private boolean readNew = false;		// t=expecting new data; f=update action

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
		return new SinkInfo("My Options",	"") 
		{
			public Sink createInstance() {
				return new MyOptPane();
			}
		};
	}

	/**
	 * Basic setup for this panel.
	 */
	public MyOptPane() {

	    // screen title
	    screenTitle = new HTML("<b>My Options</b>");  
	    sysPanel.add(screenTitle);
	    helpIcon = new Image(helpURL);     
        helpIcon.setTitle("Click here to see help for this screen.");
        helpIcon.setStyleName("film-Button2");
        helpIcon.addClickListener(this);
        sysPanel.add(helpIcon);
        
		// wait icon
		sysErrors.addStyleName("film-Errors");
		waitIcon = new Image(waitURL);		
		waitIcon.setTitle("Saving/Loading Data from server");
		waitText = new HTML("");
		waitText.setStyleName("sched-Film-Font");
		waitIcon.setVisible(false);
		waitPanel.add(waitIcon);
		waitPanel.add(waitText);
		sysPanel.add(waitPanel);
		sysPanel.add(sysErrors);
		
		// buttons
		schedButton.setTitle("Create your schedule.");
		schedButton.setEnabled(false);  
		schedButton.addClickListener(this);	
		
		saveButton.setTitle("Save your scheduling options.");
		saveButton.setStyleName("film-Button");
		saveButton.setEnabled(false);  
		saveButton.addClickListener(this);			
		buttonHP.add(saveButton);
		
		cancelButton.setTitle("Cancel changes your scheduling options.");
		cancelButton.setStyleName("film-Button");
		cancelButton.setEnabled(false);  
		cancelButton.addClickListener(this);			
		buttonHP.add(cancelButton);
		buttonHP.setStyleName("opt-Boxed");
		buttonHP.setHorizontalAlignment(VerticalPanel.ALIGN_LEFT);
		
		mainOptVP.add(sysPanel);	
		mainOptVP.add(schedButton);	
		mainOptVP.add(mainOptHP);
		mainOptVP.add(buttonHP);
		//mainHP.setStyleName("film-Sink");
		//mainHP.addStyleName("film-Boxed");
		mainOptVP.setStyleName("film-Sink");
		mainOptVP.addStyleName("film-Boxed");

		// set up listener
		User.addLogoutListener(this);
		SchedPolicy.addServerListener(new myServerlistener());
		
		// set up each day in schedule for a user (if not created already)
		ScheduleHandler schedHandler = ScheduleHandler.instance();
		ScheduleHandler.addServerListener(new loadSchedlistener());
        ScheduleHandler.addSchedListener(new schedlistener());
        if (!ScheduleHandler.isLoaded())
        {
            ScheduleHandler.loadMySchedule(true);
        }
        else
        {
            schedButton.setEnabled(true);
        }
		
		initWidget(mainOptVP);
		setupOptions();
	}	  
	
	/**
	 * This loads the existing options for a user.
	 */
	private void loadOptions()
	{
		showWait("Loading...");
		pickStart.setSelectedDayIndex(SchedPolicy.getFirstMovieIndex());
		pickEnd.setSelectedDayIndex(SchedPolicy.getLastMovieIndex());
		UtilTimeRPC utilTime = new UtilTimeRPC();
		
		int timeIndex = utilTime.convertTimeToIndex8(SchedPolicy.getFirstMovieTime());
		lbStartTime.setSelectedIndex(timeIndex);

		timeIndex = utilTime.convertTimeToIndex8(SchedPolicy.getLastMovieTime());
		lbEndTime.setSelectedIndex(timeIndex);

		
		if (SchedPolicy.isRedEyeMovieOK())
		{
			rbRedEyeYes.setChecked(true);
			rbRedEyeNo.setChecked(false);
		}
		else
		{
			rbRedEyeYes.setChecked(false);
			rbRedEyeNo.setChecked(true);
		}

        int tempMin = (SchedPolicy.getMealMinutes() / 30) - 1;
        //System.out.println("tempMin "+tempMin+" mealMin "+SchedPolicy.getMealMinutes());
        lbMealMin.setSelectedIndex(tempMin);
        
        timeIndex = utilTime.convertTimeToIndex8(SchedPolicy.getStartMealTime());
        lbMealStart.setSelectedIndex(timeIndex);
        
        timeIndex = utilTime.convertTimeToIndex8(SchedPolicy.getEndMealTime());
        lbMealEnd.setSelectedIndex(timeIndex);
        
		if (SchedPolicy.isHaveMealBreak())
		{
			rbMealYes.setChecked(true);
			rbMealNo.setChecked(false);
			lbMealStart.setEnabled(true);
			lbMealEnd.setEnabled(true);
			lbMealMin.setEnabled(true);
		}
		else
		{
			rbMealYes.setChecked(false);
			rbMealNo.setChecked(true);
			lbMealStart.setEnabled(false);
			lbMealEnd.setEnabled(false);
			lbMealMin.setEnabled(false);
		}
	    
	    lbTix.setSelectedIndex(SchedPolicy.getTicketsRequested() - 1);
	    
	    tempMin = (SchedPolicy.getBetweenMinutes() / 5);
		//System.out.println("between "+tempMin+" mealMin "+SchedPolicy.getBetweenMinutes());
		lbBetween.setSelectedIndex(tempMin);

		if (SchedPolicy.getMoviesPerDay() == 0)
			lbFilms.setSelectedIndex(5);
		else
			lbFilms.setSelectedIndex(SchedPolicy.getMoviesPerDay()); 
		
		setTheaterOption(SchedPolicy.getTheaterMap());
		
		hideWait();
	}
	
	/**
	 * This sets up the options for scheduling.
	 */
	private void setupOptions()
	{				
		// *******************************
		// set up start and end day panel
		// *******************************
		HTML hdgDay = new HTML("<b>Days...</b>");
		HTML hdgStartDay = new HTML("Start on this day:");
		HTML hdgEndDay = new HTML("End on this day:");		
		
		// format elements
		hdgDay.setStyleName("sched-Film-Font");
		hdgStartDay.setStyleName("sched-Film-Font");
		hdgEndDay.setStyleName("sched-Film-Font");
		
		// set up starting and ending date pickers
		String pickTip1 = "The first day to schedule films.";
        pickStart = new FestDatePicker(pickTip1, "start", Constants.FESTIVAL_START_DATE);
        pickStart.addDataChangeListener(new myStartListener());
        
        String pickTip2 = "The last day to schedule films.";
        pickEnd = new FestDatePicker(pickTip2, "end", Constants.FESTIVAL_END_DATE);
        pickEnd.addDataChangeListener(new myEndListener());
        
		// load in panels
		Grid gDays = new Grid(2, 3);
		gDays.setWidget(0, 0, hdgDay);
		gDays.setWidget(0, 1, hdgStartDay);
		gDays.setWidget(0, 2, pickStart);
		gDays.setWidget(1, 1, hdgEndDay);
		gDays.setWidget(1, 2, pickEnd);
		
		daysVP.setStyleName("opt-Boxed");
		daysVP.add(gDays);
		
		
		// *******************************
		// set up start and end time panel
		// *******************************
		HTML hdgTime = new HTML("<b>Time...</b>");
		HTML hdgStartTime = new HTML("Start at this time:");
		HTML hdgEndTime = new HTML("End at this time:");		
		
		// format elements
		lbStartTime.setTitle("Start scheduling films at this time.");
		lbEndTime.setTitle("Schedule no films after at this time.");
		hdgTime.setStyleName("sched-Film-Font");
		hdgStartTime.setStyleName("sched-Film-Font");
		hdgEndTime.setStyleName("sched-Film-Font");
		
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
		
		// set up listener
		lbStartTime.addChangeListener(this);
		lbEndTime.addChangeListener(this);
		
		// load in panels
		Grid gTime = new Grid(2, 3);
		gTime.setWidget(0, 0, hdgTime);
		gTime.setWidget(0, 1, hdgStartTime);
		gTime.setWidget(0, 2, lbStartTime);
		gTime.setWidget(1, 1, hdgEndTime);
		gTime.setWidget(1, 2, lbEndTime);
		
		timeVP.setStyleName("opt-Boxed");
		timeVP.add(gTime);
		
		// *******************************
		// set up Films per Day panel
		// *******************************
		HTML hdgFilms = new HTML("<b>Films...</b>");
		HTML hdgFilms1 = new HTML("Film Limit Per Day:");
		
		// format elements
		lbFilms.setTitle("Indicate the maximum number of films to schedule each day");
		hdgFilms.setStyleName("sched-Film-Font");
		hdgFilms1.setStyleName("sched-Film-Font");
		
		String filmsArray[] = {"1", "2", "3", "4", 
				"5", "6"   };

		// put tickets in the drop down boxes
		for (int loopCtr=0; loopCtr < 6; loopCtr++)
		{
			lbFilms.addItem(filmsArray[loopCtr]);
		}
		
		// make drop down list and set initial value
		lbFilms.setVisibleItemCount(1);
		lbFilms.setSelectedIndex(5);
		
		// set up listener
		lbFilms.addChangeListener(this);
		
		// load in panels
		Grid gFilms = new Grid(1, 3);
		gFilms.setWidget(0, 0, hdgFilms);
		gFilms.setWidget(0, 1, hdgFilms1);
		gFilms.setWidget(0, 2, lbFilms);
		
		filmsVP.setStyleName("opt-Boxed");
		filmsVP.add(gFilms);
		
		// *******************************
		// set up red eye panel
		// *******************************
		HTML hdgRed = new HTML("<b>Red Eye...</b>");
		HTML hdgRed1 = new HTML("Allow an early film to be scheduled after a late film?");
	    
	    // Check 'yes' by default.
		rbRedEyeYes.setChecked(true);
		rbRedEyeYes.setTitle("Yes, schedule an early morning film after a late show the previous night");
		rbRedEyeNo.setTitle("No, do not schedule an early morning film after a late show the previous night");
	    hdgRed.setStyleName("sched-Film-Font");
	    hdgRed1.setStyleName("sched-Film-Font");
	    rbRedEyeYes.setStyleName("sched-Film-Font");
	    rbRedEyeNo.setStyleName("sched-Film-Font");
	    
	    // set listeners
	    rbRedEyeYes.addClickListener(this);
	    rbRedEyeNo.addClickListener(this);
	    
	    // load in panels
	    Grid  gRedEye = new Grid (1,4);
		gRedEye.setWidget(0, 0, hdgRed);
		gRedEye.setWidget(0, 1, hdgRed1);
		gRedEye.setWidget(0, 2, rbRedEyeYes);
		gRedEye.setWidget(0, 3, rbRedEyeNo);
		
		redeyeVP.setStyleName("opt-Boxed");
		redeyeVP.add(gRedEye);
		
		// *******************************
		// set up Between time panel
		// *******************************
		HTML hdgBetween = new HTML("<b>Between...</b>");
		HTML hdgBetween1 = new HTML("Allow additional time between films:");
		
		// format elements
		lbBetween.setTitle("Leave at least this amount of time between films"+
				"(travel time is already included)");
		hdgBetween.setStyleName("sched-Film-Font");
		hdgBetween1.setStyleName("sched-Film-Font");
		
		String btwnArray[] = {"0 minutes", "5 minutes", "10 minutes", "15 minutes", 
				"20 minutes", "25 minutes", "30 minutes", "35 minutes", "40 minutes", 
				"45 minutes", "50 minutes", "55 minutes", "60 minutes"   };

		// put minutes in the drop down boxes
		for (int loopCtr=0; loopCtr < 13; loopCtr++)
		{
			lbBetween.addItem(btwnArray[loopCtr]);
		}
		
		// make drop down list and set initial value
		lbBetween.setVisibleItemCount(1);
		lbBetween.setSelectedIndex(0);
		
		// set up listener
		lbBetween.addChangeListener(this);
		
		// load in panels
		Grid gBtwn = new Grid(1, 3);
		gBtwn.setWidget(0, 0, hdgBetween);
		gBtwn.setWidget(0, 1, hdgBetween1);
		gBtwn.setWidget(0, 2, lbBetween);
		
		btwnVP.setStyleName("opt-Boxed");
		btwnVP.add(gBtwn);
		
		// *******************************
		// set up meal break panel
		// *******************************
		HTML hdgMeal = new HTML("<b>Meal...</b>");
		HTML hdgMealAllow = new HTML("Allow a meal break?");
		HTML hdgMealMin = new HTML("Number of minutes?");
		HTML hdgMealStart = new HTML("Schedule meal between:");
		HTML hdgMealEnd = new HTML("and:");
	    
	    // set formatting.
		rbMealYes.setChecked(true);
		rbMealYes.setTitle("Yes, Build in a meal break each day");
		rbMealNo.setTitle("No, do not schedule any time for a meal break");
	    lbMealMin.setTitle("Leave at least this amount of time for a meal"+
		" break during each day");
	    String startString = "Schedule a meal break somewhere between"+
        " these two times (make range larger for the best schedule)";
	    String endString = "Schedule a meal break somewhere between"+
        " these two times (make range larger for the best schedule)";
	    lbMealStart.setTitle(startString);
	    hdgMealStart.setTitle(startString);
	    lbMealEnd.setTitle(endString);
	    hdgMealEnd.setTitle(endString);
	    hdgMeal.setStyleName("sched-Film-Font");
	    hdgMealAllow.setStyleName("sched-Film-Font");
	    hdgMealMin.setStyleName("sched-Film-Font");
	    hdgMealStart.setStyleName("sched-Film-Font");
	    hdgMealEnd.setStyleName("sched-Film-Font");
	    rbMealYes.setStyleName("sched-Film-Font");
	    rbMealNo.setStyleName("sched-Film-Font");
	    
	    String mealMinArray[] = {"30 minutes", "60 minutes", "90 minutes", "120 minutes"};

		// put minutes in the drop down boxes
		for (int loopCtr=0; loopCtr < 4; loopCtr++)
		{
			lbMealMin.addItem(mealMinArray[loopCtr]);
		}
		
		// put 8AM to 12AM in the drop down boxes
		for (int loopCtr=16; loopCtr < 48; loopCtr++)
		{
			lbMealStart.addItem(hdgArray[loopCtr]);
			lbMealEnd.addItem(hdgArray[loopCtr]);
		}
		for (int loopCtr=0; loopCtr < 1; loopCtr++)
		{
			lbMealStart.addItem(hdgArray[loopCtr]);
			lbMealEnd.addItem(hdgArray[loopCtr]);
		}
		
		// make drop down list and set initial value
		lbMealMin.setVisibleItemCount(1);
		lbMealMin.setSelectedIndex(1);
		lbMealStart.setVisibleItemCount(1);
		lbMealEnd.setVisibleItemCount(1);
		lbMealStart.setSelectedIndex(16);
		lbMealEnd.setSelectedIndex(26);
		
		// set up listener
		lbMealMin.addChangeListener(this);
		lbMealStart.addChangeListener(this);
		lbMealEnd.addChangeListener(this);
	    rbMealYes.addClickListener(this);
	    rbMealNo.addClickListener(this);
		
	    // load in panels
	    FlexTable  gMeal = new FlexTable();
	    gMeal.setWidget(0, 0, hdgMeal);
	    gMeal.setWidget(0, 1, hdgMealAllow);
	    gMeal.setWidget(0, 2, rbMealYes);
	    gMeal.setWidget(0, 3, rbMealNo);
	    
	    gMeal.setWidget(1, 1, hdgMealMin);
	    gMeal.setWidget(1, 2, lbMealMin);
	    gMeal.getFlexCellFormatter().setColSpan(1, 2, 2);
	    
	    gMeal.setWidget(2, 1, hdgMealStart);
	    gMeal.setWidget(2, 2, lbMealStart);
	    gMeal.getFlexCellFormatter().setColSpan(2, 2, 2);
	    
	    gMeal.setWidget(3, 1, hdgMealEnd);
	    gMeal.setWidget(3, 2, lbMealEnd);
	    gMeal.getFlexCellFormatter().setColSpan(2, 2, 2);	    
		
		mealVP.setStyleName("opt-Boxed");
		mealVP.add(gMeal);
		
		// *******************************
		// set up Tickets per Film panel
		// *******************************
		HTML hdgTix = new HTML("<b>Tickets...</b>");
		HTML hdgTix1 = new HTML("Number of tickets to reserve for each film:");
		
		// format elements
		lbTix.setTitle("When scheduling showings, use this many tickets "+
				"for each film on the schedule");
		hdgTix.setStyleName("sched-Film-Font");
		hdgTix1.setStyleName("sched-Film-Font");
		
		String tixArray[] = {"1 ticket", "2 tickets", "3 tickets", "4 tickets", 
				"5 tickets"   };

		// put tickets in the drop down boxes
		for (int loopCtr=0; loopCtr < 5; loopCtr++)
		{
			lbTix.addItem(tixArray[loopCtr]);
		}
		
		// make drop down list and set initial value
		lbTix.setVisibleItemCount(1);
		lbTix.setSelectedIndex(0);
		
		// set up listener
		lbTix.addChangeListener(this);
		
		// load in panels
		Grid gTix = new Grid(1, 3);
		gTix.setWidget(0, 0, hdgTix);
		gTix.setWidget(0, 1, hdgTix1);
		gTix.setWidget(0, 2, lbTix);
		
		tixVP.setStyleName("opt-Boxed");
		tixVP.add(gTix);
		
		// *******************************
		// set up Theaters panel
		// *******************************
		HTML hdgTheater = new HTML("<b>Theaters...</b>");
		HTML hdgTheater1 = new HTML("Schedule films at these theaters:");
		
		// format elements
		cbPC.setTitle("Check this box if you want films shown in Park City");
		cbSLC.setTitle("Check this box if you want films shown in Salt Lake City");
		cbSV.setTitle("Check this box if you want films shown in Sundance Village");
		cbOG.setTitle("Check this box if you want films shown in Ogden");
		hdgTheater.setStyleName("sched-Film-Font");
		hdgTheater1.setStyleName("sched-Film-Font");
		
		// set initial value
		cbPC.setChecked(false);
		cbSLC.setChecked(false);
		cbSV.setChecked(false);
		cbOG.setChecked(false);
		
		// set up listener
		cbPC.addClickListener(this);
		cbSLC.addClickListener(this);
		cbSV.addClickListener(this);
		cbOG.addClickListener(this);
		
		// load in panels
		Grid gTheaters = new Grid(4, 3);
		gTheaters.setWidget(0, 0, hdgTheater);
		gTheaters.setWidget(0, 1, hdgTheater1);
		gTheaters.setWidget(0, 2, cbPC);
		gTheaters.setWidget(1, 2, cbSLC);
		gTheaters.setWidget(2, 2, cbSV);
		gTheaters.setWidget(3, 2, cbOG);
		
		theatersVP.setStyleName("opt-Boxed");
		theatersVP.add(gTheaters);		
		
	    //******************
		// add to main panel
		leftVP.add(daysVP);
		leftVP.add(timeVP);
		leftVP.add(filmsVP);
		leftVP.add(redeyeVP);
		leftVP.add(btwnVP);
		//leftVP.setStyleName("opt-OuterBox");
		leftVP.setWidth("300px");
		
		rightVP.add(mealVP);
		rightVP.add(tixVP);
		rightVP.add(theatersVP);
		rightVP.setStyleName("opt-OuterBox");
		
		mainOptHP.add(leftVP);
		mainOptHP.add(rightVP);		
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
	 * Handles when filters are clicked.
	 * @param Widget the incoming button clicked.
	 */
	public void onClick(Widget sender)
	{
		// check which button is pressed
	    if (sender == helpIcon)
        {
            // show help
            HelpWidget myHelp = new HelpWidget(5);   
        }
	    else if (sender == schedButton)
		{
			// disable buttons
			schedButton.setEnabled(false);  

			setupProgress();
            ScheduleHandler.schedPrep();
            // when prep is done, schedListener is called   
		}
		else if (sender == saveButton)
		{
			// disable button
			saveButton.setEnabled(false);  

			// update options in database	
			readNew = false;
			SchedPolicy.updatePolicy();
			// **Look for answer in serverListener
		}
		else if (sender == cancelButton)
		{
			// disable button
			saveButton.setEnabled(false);  

			// read options from database	
			readNew = true;
			SchedPolicy.loadPolicy();
			// **Look for answer in serverListener
		}
		else if (sender == rbRedEyeYes)
		{
			SchedPolicy.setRedEyeMovieOK(true);
			setOptChanged();
			//System.out.println("red eye Yes");
		}	
		else if (sender == rbRedEyeNo)
		{
			SchedPolicy.setRedEyeMovieOK(false);
			setOptChanged();
			//System.out.println("red eye No");
		}	
		else if (sender == rbMealYes)
		{
			SchedPolicy.setHaveMealBreak(true);
			setOptChanged();
			lbMealStart.setEnabled(true);
			lbMealEnd.setEnabled(true);
			lbMealMin.setEnabled(true);
			//System.out.println("meal Yes");
		}	
		else if (sender == rbMealNo)
		{
			SchedPolicy.setHaveMealBreak(false);
			setOptChanged();
			lbMealStart.setEnabled(false);
			lbMealEnd.setEnabled(false);
			lbMealMin.setEnabled(false);
			//System.out.println("Meal No");
		}	
		else if (sender == cbPC)
		{
			setTheaterMap();
		}
		else if (sender == cbSLC)
		{
			setTheaterMap();
		}
		else if (sender == cbSV)
		{
			setTheaterMap();
		}
		else if (sender == cbOG)
		{
			setTheaterMap();
		}
	}

	/**
	 * Handles when start or pause button is clicked.
	 * @param Widget the incoming button clicked.
	 */
	public void onChange(Widget sender)
	{
		if (sender == lbStartTime)
		{
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
			//System.out.println("Time:"+strTime);
			SchedPolicy.setFirstMovieTime(strTime);
			setOptChanged();
			
			// check for error condition
			if (lbStartTime.getSelectedIndex() >= lbEndTime.getSelectedIndex())
				sysErrors.setHTML("Start Time must be before End Time");
			else
				sysErrors.setHTML("");
		}	
		else if (sender == lbEndTime)
		{
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
			//System.out.println("Time:"+strTime);
			SchedPolicy.setLastMovieTime(strTime);
			setOptChanged();
			
			// check for error condition
			if (lbStartTime.getSelectedIndex() >= lbEndTime.getSelectedIndex())
				sysErrors.setHTML("End Time must be after Start Time");
			else
				sysErrors.setHTML("");
		}	
		else if (sender == lbFilms)
		{
			int tempFilms = lbFilms.getSelectedIndex() + 1;
			// if max of 6, use zero to indicate no limit
			if (tempFilms == 6)
				tempFilms = 0;
			//System.out.println("Films:"+tempFilms);
			SchedPolicy.setMoviesPerDay(tempFilms);
			setOptChanged();
		}
		else if (sender == lbBetween)
		{
			int tempMin = 5 * lbBetween.getSelectedIndex();
			//System.out.println("BtwnMin:"+tempMin);
			SchedPolicy.setBetweenMinutes(tempMin);
			setOptChanged();
		}	
		else if (sender == lbMealMin)
		{
			int tempMin = 30 + (30* lbMealMin.getSelectedIndex());
			//System.out.println("MealMin:"+tempMin);
			SchedPolicy.setMealMinutes(tempMin);
			setOptChanged();
		}	
		else if (sender == lbMealStart)
		{
			String tempTime = lbMealStart.getItemText(lbMealStart.getSelectedIndex());
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
			SchedPolicy.setStartMealTime(strTime);
			setOptChanged();
			
			// check for error condition
			if (lbMealStart.getSelectedIndex() > lbMealEnd.getSelectedIndex())
				sysErrors.setHTML("Starting Meal Time must be before Ending Meal Time");
			else
				sysErrors.setHTML("");
		}	
		else if (sender == lbMealEnd)
		{
			String tempTime = lbMealEnd.getItemText(lbMealEnd.getSelectedIndex());
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
			SchedPolicy.setEndMealTime(strTime);
			setOptChanged();
			
			// check for error condition
			if (lbMealStart.getSelectedIndex() > lbMealEnd.getSelectedIndex())
				sysErrors.setHTML("Ending Meal Time must be after Starting Meal Time");
			else
				sysErrors.setHTML("");
		}	
		else if (sender == lbTix)
		{
			int tempTix = lbTix.getSelectedIndex()+1;
			SchedPolicy.setTicketsRequested(tempTix);
			setOptChanged();
			//System.out.println("Tix:"+tempTix);
		}	
	}

	/**
	 * Sets the theater map based on check boxes.
	 */
	private void setTheaterMap()
	{
		// convert check boxes for theaters to a bitmap
		int theaters = 0;
		if (cbPC.isChecked())
			theaters = theaters | Constants.THEATER_PC;

		if (cbSLC.isChecked())
			theaters = theaters | Constants.THEATER_SLC;

		if (cbSV.isChecked())
			theaters = theaters | Constants.THEATER_SV;

		if (cbOG.isChecked())
			theaters = theaters | Constants.THEATER_OG;

		SchedPolicy.setTheaterMap(theaters);
		setOptChanged();
		//System.out.println("theaterMap:"+cbPC.isChecked()+" "+cbSLC.isChecked()+
		//		" "+cbSV.isChecked()+" "+cbOG.isChecked()+" "+theaters);
	}

	/**
	 * Sets the option for which theaters to use in scheduling.
	 * @param int incoming theater bitmap.
	 */
	public void setTheaterOption(int theaterOption)
	{
		// based on incoming bit map, set the appropriate check boxes
		int trialMask = theaterOption & Constants.THEATER_PC;
		if (trialMask == Constants.THEATER_PC)
			cbPC.setChecked(true);
		else
			cbPC.setChecked(false);

		trialMask = theaterOption & Constants.THEATER_SLC;
		if (trialMask == Constants.THEATER_SLC)
			cbSLC.setChecked(true);
		else
			cbSLC.setChecked(false);

		trialMask = theaterOption & Constants.THEATER_SV;
		if (trialMask == Constants.THEATER_SV)
			cbSV.setChecked(true);
		else
			cbSV.setChecked(false);

		trialMask = theaterOption & Constants.THEATER_OG;
		if (trialMask == Constants.THEATER_OG)
			cbOG.setChecked(true);
		else
			cbOG.setChecked(false);
	}
	

	
	/**
	 * Sets indicator saying options have changed.
	 */
	private void setOptChanged()
	{
		// only change if it hasn't already been changed and no errors!
		if ((optChanged == false) && (sysErrors.getHTML().equalsIgnoreCase("")))
		{
			optChanged = true;
			saveButton.setEnabled(true);
			cancelButton.setEnabled(true);
		}	
		
		// if error, take away save option until error corrected
		if (!sysErrors.getHTML().equalsIgnoreCase(""))
		{
		    optChanged = false;
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
     * Listener class used to take action when start date has changed or selected.
     */
    class myStartListener implements DataChangeListener
    {
        /**
         * Used to take action when data has changed.
         */
        public void onDataChange()
        {
            // check for error condition
            if (pickStart.getSelectedDayIndex() > pickEnd.getSelectedDayIndex())
                sysErrors.setHTML("Start Day must be before End Day");
            else
                sysErrors.setHTML("");
            
            // change start date
            SchedPolicy.setFirstMovieIndex(pickStart.getSelectedDayIndex());
            setOptChanged();
            //System.out.println("Index:"+pickStart.getSelectedDayIndex());
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
     * Listener class used to take action when end date has changed or selected.
     */
    class myEndListener implements DataChangeListener
    {
        /**
         * Used to take action when data has changed .
         */
        public void onDataChange()
        {
            // check for error condition
            if (pickStart.getSelectedDayIndex() > pickEnd.getSelectedDayIndex())
                sysErrors.setHTML("End Day must be after Start Day");
            else
                sysErrors.setHTML("");
            
            // change end date
            SchedPolicy.setLastMovieIndex(pickEnd.getSelectedDayIndex());
            setOptChanged();            
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
			showWait(inDescr);
		}
		
		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action, no result expected.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			sysErrors.setHTML("");
			hideWait();
			sysErrors.setHTML("");
			optChanged = false;
			
			// if expecting new data to be read
			if (readNew)
			{
				loadOptions();
				saveButton.setEnabled(false);
				cancelButton.setEnabled(false);
			}
		}
		
		/**
		 * Show the error message from server.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String inError)
		{
			sysErrors.setHTML(inError);
		}
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

        }
        
        /**
         * Called when server activity ends.  
         * @param String actionID to identify server action.
         * @param object result - for this listener, no result expected.
         */
        public void onServerEnd(String actionID, Object result)
        {
            // enable button to allow rescheduling
            schedButton.setEnabled(true); 
        }
        
        /**
         * Show the error message from server.
         * @param String descriptive error text to display.
         */
        public void onServerError(String inError)
        {
            
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
                schedButton.setEnabled(true); 
                hideProgress();
            }
        }
        
        /**
         * Show the error message from server.
         * @param String descriptive error text to display.
         */
        public void onServerError(String inError)
        {
            sysErrors.setHTML(inError);
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
		// check that everything is loaded (after check that user is logged in)
		if (User.getTokenID() == 0)
		{
			isLoaded = false;
			User.setError("You must log in to use this feature.");
			History.newItem("Home");
		}
		else if (isLoaded == false)
		{
			isLoaded = true;			
			loadOptions();
		}
	}
}
