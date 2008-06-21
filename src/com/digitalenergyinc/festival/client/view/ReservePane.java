package com.digitalenergyinc.festival.client.view;

import com.digitalenergyinc.fest.client.LogoutListener;
import com.digitalenergyinc.fest.client.ServerListener;
import com.digitalenergyinc.festival.client.Sink;
import com.digitalenergyinc.fest.client.control.MovieHandler;
import com.digitalenergyinc.fest.client.control.ScheduleHandler;
import com.digitalenergyinc.fest.client.control.Summary;
import com.digitalenergyinc.fest.client.control.TicketHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.digitalenergyinc.fest.client.model.ShowingRPC;
import com.digitalenergyinc.fest.client.model.TheaterGroup;
import com.digitalenergyinc.fest.client.model.UtilTimeRPC;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextBox;
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
public class ReservePane extends Sink implements ClickListener, TableListener,
			LogoutListener
{
	private VerticalPanel mainVP = 
		new VerticalPanel();  				// main panel
	private HorizontalPanel waitPanel =
		new HorizontalPanel();				// wait icon panel
	private HorizontalPanel sysPanel =
		new HorizontalPanel();				// system error + wait icon panel
	private HorizontalPanel titleHP =
        new HorizontalPanel();              // title panel
	private VerticalPanel listVP = 
		new VerticalPanel();  				// reservation list panel
	private VerticalPanel statusVP = 
		new VerticalPanel();  				// reservation status composite panel
	private VerticalPanel schedGaugeVP = 
		new VerticalPanel();  				// schedule gauge panel
	private HorizontalPanel statusHP = 
		new HorizontalPanel();  			// reserve status panel
	private VerticalPanel tixVP = 
		new VerticalPanel();  				// tickets status panel
	private VerticalPanel reschedVP = 
		new VerticalPanel();  				// reschedule msg panel
	private Grid colLabel;					// column headings
	private Grid filmGrid;					// film list table	

	// images
	private Image waitIcon;					// wait GIF
	private String waitURL = "images/sd08-loader.gif";   // URL for wait icon
	private Image helpIcon;                       // help GIF
    private String helpURL = "images/help.gif";   // URL for help icon

	// System elements
	private HTML waitText;					// wait text
	private HTML screenTitle;				// title of screen
	private HTML sysErrors = new HTML("");	// system errors (from server)
	private Button buyAllButton = new Button("Buy All");
	private boolean gaugeLoaded = false;	// indicates if sched gauge was loaded

	private boolean pass3 = false;			// indicates if separate sold out check

	// Ranking elements
	private HorizontalPanel myRanking;  // my ranking panel
	private Image myRank = new Image(); 	// star icon for rank
	private String star0 = "images/0starSmall.gif";   // URL for no star
	private String star1 = "images/1starSmall.gif";   // URL for no star
	private String star2 = "images/2starSmall.gif";   // URL for no star
	private String star3 = "images/3starSmall.gif";   // URL for no star
	private String star4 = "images/4starSmall.gif";   // URL for no star
	private String star5 = "images/5starSmall.gif";   // URL for no star

	// controls & elements
	private Image tixImage = new Image(); // ticket image
	private String tixURL = "images/ticketsized.gif";   // URL for ticket
	private Image soldOutImage = new Image(); // ticket image
	private String soldOutURL = "images/soldOut.gif";   // URL for sold out
	private String soldOutEmptyURL = "images/soldOutEmpty.gif";   // URL for available
	
	// status elements
	private Label tixAvail = new Label("");  // number of tickets available
	private Label tixPurch = new Label("");  // number of tickets purchased
	private Label tixRem = new Label("");    // number of tickets remaining
	private Label schedNScore = new Label("");  // normalized schedule score
	private Label schedScore = new Label("");   // schedule score
	private Button reschedButton = new Button("Reschedule");
	private HTML reschedMsg = new HTML();	// resched msgs (like which is sold out)
	
	// data elements
	private ShowingRPC[] filmList;			// list of films on schedule
	private ShowingRPC selectedFilm;		// film selected to buy
	private int selectedRow = 0;			// grid row of selectedFilm
	private int tixToReserve = 0;			// number of tickets to reserve/release
	private int waitCount = 0;				// counter of how many loading
	//private ScheduleHandler schedHandler;	// handler
	
	// constants for columns
	private static final int COL_COUNT = 9;      // count of columns
	private static final int COL_TITLE = 0;      // film title
	private static final int COL_ID = 1;         // film ID
	private static final int COL_DATETIME = 2;   // date/time of showing
	private static final int COL_SELLOUT = 3;    // sell out a showing
	private static final int COL_THEATER = 4;    // theater
	private static final int COL_RANK = 5;       // ranking
	private static final int COL_TICKETS = 6;    // ticket text box
	private static final int COL_IMAGE = 7;      // ticket image
	private static final int COL_BUTTON = 8;     // buy/release button

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
		return new SinkInfo("Reservations",	"") 
		{
			public Sink createInstance() {
				return new ReservePane();
			}
		};
	}

	/**
	 * Basic setup for this panel.
	 */
	public ReservePane() {

		// wait icon
		sysErrors.addStyleName("film-Errors");
		waitIcon = new Image(waitURL);		
		waitIcon.setTitle("Saving/Loading Data from server");
		waitText = new HTML(" ");
		waitText.setStyleName("sched-Film-Font");	
		HTML waitTitle = new HTML("<b>Status</b>");			
		waitPanel.add(waitIcon);
		waitPanel.add(waitText);
		waitPanel.setVisible(false);
		
		sysPanel.add(waitTitle);
		sysPanel.add(waitPanel);
		sysPanel.add(sysErrors);
		
		// detail controls
		tixImage.setStyleName("film_Image");
		tixImage.setUrl(tixURL);
		tixImage.setTitle("Ticketed");
		
		soldOutImage.setStyleName("film_Image");
		soldOutImage.setUrl(soldOutURL);
		soldOutImage.setTitle("Sold Out");	
		
		// ticket status panel
		HTML tixTitle = new HTML("<b>Tickets</b>");
		tixTitle.setStyleName("sched-Film-Font");
		HTML tix1 = new HTML("Tickets Available:");
		tix1.setTitle("Total number of tickets you are able to purchase today.");		
		HTML tix2 = new HTML("Tickets Purchased:");
		tix2.setTitle("Number of tickets you have purchased today.");
		HTML tix3 = new HTML("Tickets Remaining:");
		tix3.setTitle("Number of tickets you have left to purchase today.");			
		
		buyAllButton.setStyleName("film-Button");
		buyAllButton.setTitle("Buy tickets for all shows at one time.");
		buyAllButton.addClickListener(this);
		buyAllButton.setEnabled(false);
		
		Grid tixTable = new Grid(3,2);
		tixTable.setStyleName("sched-Film-Font");
		tixTable.setWidget(0, 0, tix1);
		tixTable.setWidget(0, 1, tixAvail);
		tixTable.setWidget(1, 0, tix2);
		tixTable.setWidget(1, 1, tixPurch);
		tixTable.setWidget(2, 0, tix3);
		tixTable.setWidget(2, 1, tixRem);
		
		tixVP.setStyleName("film-Boxed");
		tixVP.add(tixTitle);
		tixVP.add(tixTable);
		tixVP.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
		tixVP.add(buyAllButton);
		
		// reschedule status panel
		HTML reschedTitle = new HTML("<b>Schedule Summary</b>");
		reschedTitle.setStyleName("sched-Film-Font");
		
		Grid schedTable = new Grid(2,2);
		HTML sched1 = new HTML("Score:");
		sched1.setTitle("The strength of your schedule (on scale of 0 to 100).");
		schedScore.setText(String.valueOf(Summary.getSchedScore()));
		HTML sched2 = new HTML("Relative Score:");
		sched2.setTitle("The sum of rankings of films on your schedule.");
		schedNScore.setText(String.valueOf(Summary.getSchedNScore()));
		
		schedTable.setStyleName("sched-Film-Font");
		schedTable.setWidget(0, 0, sched2);
		schedTable.setWidget(0, 1, schedScore);
		schedTable.setWidget(1, 0, sched1);
		schedTable.setWidget(1, 1, schedNScore);
		
		reschedButton.setStyleName("film-Button");
		reschedButton.addStyleName("film-Errors");
		reschedButton.setTitle("Click to reschedule due to sold out showings.");
		reschedButton.setEnabled(false);
		reschedButton.addClickListener(this);
		
		reschedMsg.setStyleName("film-Errors");
		
		reschedVP.setStyleName("film-Boxed");
		reschedVP.add(reschedTitle);
		reschedVP.add(schedTable);
		reschedVP.add(reschedButton);
		reschedVP.add(reschedMsg);
		
		// construct status panel
		statusHP.add(tixVP);
		statusHP.add(reschedVP);
		
		statusVP.add(sysPanel);
		statusVP.add(statusHP);
		statusVP.setStyleName("film-Divide");
		
		// make column headings table
		colLabel = new Grid(1, COL_COUNT);
		colLabel.setStyleName("sched-Film-Font");
		colLabel.setWidth("100%");
		colLabel.setHTML(0, COL_TITLE, "<b>Film Title</b>");
		colLabel.getColumnFormatter().setWidth(COL_TITLE, "250px");
		colLabel.setHTML(0, COL_ID, "<b>ID</b>");
		colLabel.getColumnFormatter().setWidth(COL_ID, "70px");
		colLabel.setHTML(0, COL_DATETIME, "<b>Date/Time</b>");
		colLabel.getColumnFormatter().setWidth(COL_DATETIME, "160px");
		colLabel.setHTML(0, COL_SELLOUT, "<b>Avail?</b>");
		colLabel.getColumnFormatter().setWidth(COL_SELLOUT, "40px");
		colLabel.setHTML(0, COL_THEATER, "<b>Theater</b>");
		colLabel.getColumnFormatter().setWidth(COL_THEATER, "200px");
		colLabel.setHTML(0, COL_RANK, "<b>Interest</b>");
		colLabel.getColumnFormatter().setWidth(COL_RANK, "90px");	
		colLabel.setHTML(0, COL_TICKETS, "<b>Tickets</b>");
		colLabel.getColumnFormatter().setWidth(COL_TICKETS, "90px");	
		colLabel.getColumnFormatter().setWidth(COL_IMAGE, "90px");	
		colLabel.setHTML(0, COL_BUTTON, "<b>Action</b>");	
		colLabel.getColumnFormatter().setWidth(COL_BUTTON, "90px");	

		screenTitle = new HTML("<b>Reservations</b>");	
		titleHP.add(screenTitle);
		helpIcon = new Image(helpURL);     
        helpIcon.setTitle("Click here to see help for this screen.");
        helpIcon.setStyleName("film-Button2");
        helpIcon.addClickListener(this);
        titleHP.add(helpIcon);
        listVP.add(titleHP);
		
		// add to panels
		mainVP.add(statusVP);		
		mainVP.add(listVP);

		//mainVP.setStyleName("film-Sink");
		//mainVP.addStyleName("film-Boxed");
		mainVP.setVerticalAlignment(VerticalPanel.ALIGN_TOP);
		
		// set up listeners
		ScheduleHandler.addServerListener(new readSchedListener());
        ScheduleHandler.addSchedListener(new schedlistener());
		User.addLogoutListener(this);
		TicketHandler.addPurchaseListener(new purchaseListener());
		MovieHandler.addServerListener(new availabilityListener());
		
		initWidget(mainVP);
	}	  

	/**
	 * This loads the film list with data.
	 */
	private void loadList()
	{						
		listVP.clear();
		
		// get schedule and sort it for display
		filmList = ScheduleHandler.getMySchedule().getDisplayReservations();
		
		// set up grid with data
		filmGrid = new Grid(filmList.length, COL_COUNT);
		filmGrid.setStyleName("sched-Film-Font");
		filmGrid.setWidth("100%");
		filmGrid.addTableListener(this);
		filmGrid.getColumnFormatter().setWidth(COL_TITLE, "250px");
		filmGrid.getColumnFormatter().setWidth(COL_ID, "70px");
		filmGrid.getColumnFormatter().setWidth(COL_DATETIME, "160px");
		filmGrid.getColumnFormatter().setWidth(COL_SELLOUT, "40px");
		filmGrid.getColumnFormatter().setWidth(COL_THEATER, "200px");
		filmGrid.getColumnFormatter().setWidth(COL_RANK, "90px");
		filmGrid.getColumnFormatter().setWidth(COL_TICKETS, "90px");	
		filmGrid.getColumnFormatter().setWidth(COL_IMAGE, "90px");	
		filmGrid.getColumnFormatter().setWidth(COL_BUTTON, "90px");	
		UtilTimeRPC utilTime = new UtilTimeRPC();
		TheaterGroup utilTheater = new TheaterGroup();

		for (int i=0; i < filmList.length; i++)
		{
			// film title
			filmGrid.setText(i, COL_TITLE, filmList[i].getMovieTitle());
			
			// film ID
			HTML filmID = new HTML(filmList[i].getMovieID());
			filmID.setStyleName("sched-Film-Link");
			filmGrid.setWidget(i, COL_ID, filmID);
			
			// format date/time
			String datim = utilTime.convertIndexToDay(filmList[i].getDayIndex()) + 
					" - " + filmList[i].getDisplayStartTime();
			filmGrid.setText(i, COL_DATETIME, datim);
			
			// format sold-out (or available) icon
			if (filmList[i].getIsSoldOut() == false)
			{
				// available!
				soldOutImage = new Image(soldOutEmptyURL);
				soldOutImage.setStyleName("film_Image");
				soldOutImage.addStyleName("sched-Film-Link");
				soldOutImage.setTitle("Click to sell-out showing.");
				filmGrid.setWidget(i, COL_SELLOUT, soldOutImage);
			}
			else
			{
				// sold out!
				soldOutImage = new Image(soldOutURL);
				soldOutImage.setStyleName("film_Image");
				soldOutImage.addStyleName("sched-Film-Link");
				soldOutImage.setTitle("Click to make this showing available.");
				filmGrid.setWidget(i, COL_SELLOUT, soldOutImage);
			}
			
			// theater
			String myTheater = utilTheater.convertTheaterID(filmList[i].getTheaterID())
				+ " / "+utilTheater.convertTheaterIDtoGroup(filmList[i].getTheaterID());
			filmGrid.setText(i, COL_THEATER, myTheater);

			// ranking
			myRanking = createStars(filmList[i].getRank());
			filmGrid.setWidget(i, COL_RANK, myRanking);
			
			// tickets
			TextBox tixTB = new TextBox();
			tixTB.setWidth("30px");
			tixTB.setTitle("The number of tickets to purchase for this film");
			filmGrid.clearCell(i, COL_BUTTON);

			// make sure not sold out
			if (filmList[i].getIsSoldOut() == false)
			{
				if (filmList[i].getTicketsReserved() > 0)
				{
					// tickets already reserved, protect textbox and show icon
					tixTB.setText(String.valueOf(filmList[i].getTicketsReserved()));
					tixTB.setEnabled(false);

					filmGrid.setWidget(i, COL_TICKETS, tixTB);

					tixImage = new Image(tixURL);
					tixImage.setStyleName("film_Image");
					tixImage.setTitle("Ticketed");
					filmGrid.setWidget(i, COL_IMAGE, tixImage);

					Button myButton = new Button("Release");
					myButton.setStyleName("film-Button");
					myButton.setWidth("70px");
					myButton.setTitle("Release the # of tickets specified for this film.");
					filmGrid.setWidget(i, COL_BUTTON, myButton);
				}
				else
				{
					// tickets not reserved so put in # requested and buy button
					tixTB.setText(String.valueOf(filmList[i].getTicketsRequested()));
					tixTB.setEnabled(true);

					filmGrid.setWidget(i, COL_TICKETS, tixTB);
					filmGrid.clearCell(i, COL_IMAGE);

					Button myButton = new Button("Buy");
					myButton.setStyleName("film-Button");
					myButton.setWidth("70px");
					myButton.setTitle("Buy the # of tickets specified for this film.");
					filmGrid.setWidget(i, COL_BUTTON, myButton);
					
					// since one can be bought, enable button
					buyAllButton.setEnabled(true);
				}
			}
			else
			{
				// showing is sold out, but if already ticketed, show ticketed!
				if (filmList[i].getTicketsReserved() > 0)
				{
					// tickets already reserved, protect textbox and show icon
					tixTB.setText(String.valueOf(filmList[i].getTicketsReserved()));
					tixTB.setEnabled(false);

					filmGrid.setWidget(i, COL_TICKETS, tixTB);

					tixImage = new Image(tixURL);
					tixImage.setStyleName("film_Image");
					tixImage.setTitle("Ticketed");
					filmGrid.setWidget(i, COL_IMAGE, tixImage);

					Button myButton = new Button("Release");
					myButton.setStyleName("film-Button");
					myButton.setWidth("70px");
					myButton.setTitle("Release the # of tickets specified for this film.");
					filmGrid.setWidget(i, COL_BUTTON, myButton);
				}
				else
				{
					// sold out - show sold out icon instead of ticketed
					// remove button, protect textbox
					filmGrid.clearCell(i, COL_BUTTON);
					filmGrid.setHTML(i, COL_BUTTON, "<b>Sold Out</b>");
					soldOutImage = new Image(soldOutURL);
					soldOutImage.setStyleName("film_Image");
					soldOutImage.setTitle("Sold Out");
					filmGrid.setWidget(i, COL_IMAGE, soldOutImage);
					
					tixTB.setText(String.valueOf(filmList[i].getTicketsRequested()));
					filmGrid.setWidget(i, COL_TICKETS, tixTB);
					tixTB.setEnabled(false);
					
					// set reschedule needed
					Summary.rescheduleNeedUpdate(2);
				}
			}
		}
		
		// check reschedule status
		if (Summary.isRescheduleNeeded())
		{
			reschedMsg.setText(Summary.getReschedReasonMsg());
			reschedButton.setEnabled(true);
		}
		else
		{
			reschedMsg.setText("");
			reschedButton.setEnabled(false);
		}
		
		listVP.add(titleHP);
		listVP.add(colLabel);
		listVP.add(filmGrid);
		listVP.setVisible(true);
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
	 * Display status of tickets (counts).
	 */
	private void updateTicketStatus()
	{
		tixAvail.setText(String.valueOf(TicketHandler.getMyTickets().getNumberOfTix()));
		tixPurch.setText(String.valueOf(TicketHandler.getMyTickets().getNumberOfTixRes()));
		int rem = TicketHandler.getMyTickets().getNumberOfTix() - 
		TicketHandler.getMyTickets().getNumberOfTixRes();
		tixRem.setText(String.valueOf(rem));	
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
	 * Pass 1 to buy tickets - check tickets available.
	 * @return int isOK 0=found+enough, 1=found+not enough, 2=not found.
	 */
	private int buyTicketsPass1()
	{
		// make sure are enough tickets, try reserving them
		int isOK1 = 0;  // 0=found+enough; 1=found+not enough; 2=not found
		if (tixToReserve > 0)
			isOK1 = TicketHandler.updateTickets(selectedFilm.getTicketType(), tixToReserve);

		// if not enough display error and stop buy process
		if (isOK1 > 0)
		{
			System.out.println("Not enough tickets! "+ selectedFilm.getTicketType()+" "+
					tixToReserve);
			sysErrors.setHTML("Not enough tickets");
		}
		
		return isOK1;		
	}
	
	/**
	 * Pass 2 to buy tickets - check showing availability in database.
	 */
	private void buyTicketsPass2()
	{
		// TODO figure out if separate sold out check is needed or just try to book
		// temporarily just do a 2 pass check (don't do separate sold out check)
		if (pass3)
		{
			// check availability of showing being bought from database	
			MovieHandler.checkAvailability("rpChk1", selectedFilm.getMovieID(), 
					selectedFilm.getShowingID());
			// ** Look for answer in availability listener
		}
		else
		{
			// skip separate isSoldOut check
			buyTicketsPass3();
		}
	}
	
	/**
	 * Pass 3 to buy tickets - check availability & book it in database.
	 */
	private void buyTicketsPass3()
	{
		// reserve showing and change in schedule
		selectedFilm.setTicketsReserved(tixToReserve);
		ScheduleHandler.rebookMovie(selectedFilm);
		
		// save to database
		TicketHandler.purchaseTickets("rpBuy1", selectedFilm);
		// **Look for answer in server listener
	}

	/**
	 * Pass 1 to release tickets - check tickets available.
	 * @return int isOK 0=found+enough, 1=found+not enough, 2=not found.
	 */
	private int releaseTicketsPass1()
	{
		// release showing and change in schedule
		TextBox buyBox = (TextBox)filmGrid.getWidget(selectedRow, COL_TICKETS);						
		tixToReserve = Integer.parseInt(buyBox.getText());
		int tempTix = selectedFilm.getTicketsReserved() - tixToReserve;
		selectedFilm.setTicketsReserved(tempTix);
		System.out.println("release tickets... "+ tixToReserve+" "+selectedFilm.getTicketsReserved());
		ScheduleHandler.rebookMovie(selectedFilm);
		
		// make sure are enough tickets, try releasing them
		int isOK1 = 0;  // 0=found+enough; 1=found+not enough; 2=not found
		if (tixToReserve > 0)
			isOK1 = TicketHandler.releaseTickets(selectedFilm.getTicketType(), tixToReserve);

		// if not enough display error and stop buy process
		if (isOK1 > 0)
		{
			System.out.println("Not enough tickets! "+ selectedFilm.getTicketType()+" "+
					tixToReserve);
			sysErrors.setHTML("Not enough tickets");
		}
		
		return isOK1;		
	}
	
	/**
	 * Process sold out condition while trying to ticket.
	 * Updates summary, film in movie list, reservation list, ticket status, and tickets.
	 */
	private void processSoldOut()
	{
		// mark summary as needing reschedule and update status panel
		Summary.rescheduleNeedUpdate(2);
		reschedButton.setEnabled(true);
		reschedMsg.setText(Summary.getReschedReasonMsg());
		
		// mark showing on reservation list as sold out
		filmGrid.clearCell(selectedRow, COL_BUTTON);
		filmGrid.setHTML(selectedRow, COL_BUTTON, "<b>Sold Out</b>");
		soldOutImage = new Image(soldOutURL);
		soldOutImage.setStyleName("film_Image");
		soldOutImage.setTitle("Sold Out");
		filmGrid.setWidget(selectedRow, COL_IMAGE, soldOutImage);
		
		soldOutImage.setTitle("Click to make this showing available.");
		filmGrid.setWidget(selectedRow, COL_SELLOUT, soldOutImage);
		
		// set tix reserved so it can be backed out in releaseTix
		selectedFilm.setTicketsReserved(tixToReserve);
		// release tickets previously reserved & rebook on schedule
		releaseTicketsPass1();
		updateTicketStatus();
	}
	
	/**
	 * Process sold out condition flagged by user.
	 * Updates summary, film in movie list, reservation list.
	 */
	private void processUserSoldOut()
	{		
		// mark as unavailable
		soldOutImage = new Image(soldOutURL);
		soldOutImage.setStyleName("film_Image");
		soldOutImage.setTitle("Click to make this showing available.");
		filmGrid.setWidget(selectedRow, COL_SELLOUT, soldOutImage);
		
		// if already ticketed, make no further UI changes
		if ((selectedFilm.getTicketsReserved() > 0) ||
				(selectedFilm.getPassReserved() > 0))
			return;
		
		// mark summary as needing reschedule and update status panel
		Summary.rescheduleNeedUpdate(2);
		reschedButton.setEnabled(true);
		reschedMsg.setText(Summary.getReschedReasonMsg());
		
		// mark showing on reservation list as sold out
		filmGrid.clearCell(selectedRow, COL_BUTTON);
		filmGrid.setHTML(selectedRow, COL_BUTTON, "<b>Sold Out</b>");
		soldOutImage.setTitle("Sold Out");
		filmGrid.setWidget(selectedRow, COL_IMAGE, soldOutImage);	
				
		ScheduleHandler.rebookMovie(selectedFilm);
		updateTicketStatus();
	}
	
	
	/**
	 * Process available condition of a previously sold out showing.
	 * Updates summary, film in movie list, reservation list, ticket status, and tickets.
	 */
	private void processAvailable()
	{
		soldOutImage = new Image(soldOutEmptyURL);
		soldOutImage.setStyleName("film_Image");
		soldOutImage.setTitle("Click to sell-out showing.");
		filmGrid.setWidget(selectedRow, COL_SELLOUT, soldOutImage);
		
		// if already ticketed, make no further UI changes
		if ((selectedFilm.getTicketsReserved() > 0) ||
				(selectedFilm.getPassReserved() > 0))
			return;
		
		// mark summary as needing reschedule and update status panel
		Summary.rescheduleNeedUpdate(2);
		reschedButton.setEnabled(true);
		reschedMsg.setText(Summary.getReschedReasonMsg());
		
		// mark showing on reservation list as available
		filmGrid.clearCell(selectedRow, COL_BUTTON);
		filmGrid.clearCell(selectedRow, COL_IMAGE);
		
		Button myButton = new Button("Buy");
		myButton.setStyleName("film-Button");
		myButton.setWidth("70px");
		myButton.setTitle("Buy the # of tickets specified for this film.");
		filmGrid.setWidget(selectedRow, COL_BUTTON, myButton);
		
		// update ticket status
		ScheduleHandler.rebookMovie(selectedFilm);
		updateTicketStatus();
	}
	
	   /**
     * Process screen when film is ticketed.
     */
    private void processTicketed()
    {        
        // mark showing on reservation list as ticketed
        filmGrid.clearCell(selectedRow, COL_BUTTON);
        
        // tickets already reserved, protect textbox and show icon
        TextBox buyBox = (TextBox)filmGrid.getWidget(selectedRow, COL_TICKETS);
        buyBox.setEnabled(false);       
        buyBox.setText(String.valueOf(filmList[selectedRow].getTicketsReserved()));
        buyBox.setEnabled(false);

        filmGrid.setWidget(selectedRow, COL_TICKETS, buyBox);

        tixImage = new Image(tixURL);
        tixImage.setStyleName("film_Image");
        tixImage.setTitle("Ticketed");
        filmGrid.setWidget(selectedRow, COL_IMAGE, tixImage);

        Button myButton = new Button("Release");
        myButton.setStyleName("film-Button");
        myButton.setWidth("70px");
        myButton.setTitle("Release the # of tickets specified for this film.");
        filmGrid.setWidget(selectedRow, COL_BUTTON, myButton);
    }
	
	/**
	 * Creates a panel of stars in the On or Off position.
	 * @param int the rank to set (number of stars).
	 * @return HorizontalPanel panel of star images.
	 */
	private HorizontalPanel createStars(int inRank)
	{
		HorizontalPanel answerPanel = new HorizontalPanel();
		
		if (inRank == 0)
		{
			myRank = new Image(star0);
			myRank.setTitle("Not Ranked");
		}
		else if (inRank == 1)
		{
			myRank = new Image(star1);
			myRank.setTitle("Must Skip");
		} 
		else if (inRank == 2)
		{
			myRank = new Image(star2);
			myRank.setTitle("Not Much Interest");
		} 
		else if (inRank == 3)
		{
			myRank = new Image(star3);
			myRank.setTitle("Some Interest");
		} 
		else if (inRank == 4)
		{
			myRank = new Image(star4);
			myRank.setTitle("Want to See");
		} 
		else if (inRank == 5)
		{
			myRank = new Image(star5);
			myRank.setTitle("Must See");
		} 
		
		myRank.setStyleName("film_Image");
		answerPanel.add(myRank);
		
		return answerPanel;
	}

	/**
	 * Listen for clicking on cells in table.
	 * @param SourcesTableEvents the event capturing the click.
	 * @param int row number clicked.
	 * @param int col number clicked.
	 */
	public void onCellClicked(SourcesTableEvents inEvent,
			int row, int col) {

		// check which column is clicked and only take action on certain columns
		if ((col != COL_ID) && (col != COL_BUTTON) && (col != COL_SELLOUT))
			return;
		
		// find selected film (common need)
		selectedRow = row;
		boolean isFound = false;
		int loopCtr=0;
		while (isFound == false)
		{
			if (filmList[loopCtr].getMovieID().equalsIgnoreCase(filmGrid.getText(row, COL_ID)))
			{
				selectedFilm = filmList[loopCtr];
				isFound = true;
			}

			loopCtr++;
			if ((isFound == false) && (loopCtr >= filmList.length))
			{
				sysErrors.setHTML("Internal Error:Film Not Found: "+
						filmGrid.getText(row, COL_ID));
				System.out.println("Internal Error:Film Not Found: "+
						filmGrid.getText(row, COL_ID));
				return;
			}
		}	
		
		if (col == COL_ID)
		{
			// check for clicking on movie title link		
			// set ID to view detail and send them to film list
			MovieHandler.setFilmDetailID(selectedFilm.getMovieID());
			History.newItem("Films");
		}
		else if (col == COL_BUTTON)
		{
			// check for buy button being pressed
			//System.out.print("Button "+row+" pressed!");
			Button pushButton = (Button) filmGrid.getWidget(row, col);
				
			if (pushButton.getText().equalsIgnoreCase("Buy"))
			{
				// BUY Ticket
				// TODO disable all remaining buy buttons and text boxes?
				TextBox buyBox = (TextBox)filmGrid.getWidget(row, COL_TICKETS);
				buyBox.setEnabled(false);							
				
				sysErrors.setHTML("");
				System.out.println("...Buy "+buyBox.getText()+" for "+
						selectedFilm.getMovieID());	
				
				// Pass 1 - check ticket availability
				tixToReserve = Integer.parseInt(buyBox.getText());
				int isOK = buyTicketsPass1();	
				
				// abort if not enough tickets
				if (isOK != 0)
				{
					buyBox.setEnabled(true);
					System.out.println("Not enough tickets: "+isOK);
					return;
				}
				
				// Pass 2 - check showing availability in DB
				
				buyTicketsPass2();
				
				// ** DB answer will return is CallIsSoldOut
			}
			else
			{				
				// RELEASE ticket				
				releaseTicketsPass1();
				
				// save to database
				TicketHandler.unPurchaseTickets("rpRel1", selectedFilm);
				// **Look for answer in purchase listener
			}
		}
		else if (col == COL_SELLOUT)
		{
			// User wants to indicate showing is sold out (or available)			
			// figure out if sold out or avail and reverse it's state
			if (selectedFilm.getIsSoldOut())
			{
				selectedFilm.setIsSoldOut(false);
			}
			else
			{
				selectedFilm.setIsSoldOut(true);
			}
			
			// now update in database
			MovieHandler.updateShowingAvailability("rpUpd", selectedFilm.getMovieID(), 
					selectedFilm.getShowingID(), 
					selectedFilm.getIsSoldOut());
			// ** look for answer in purchase Listener!!!
		}
	} 
	  
	/**
	 * Handles when controls are clicked.
	 * @param Widget the incoming widget clicked.
	 */
	public void onClick(Widget sender)
	{
		// check which button is pressed
	    if (sender == helpIcon)
        {
            // show help
            HelpWidget myHelp = new HelpWidget(6);   
        }
        else if (sender == buyAllButton)
		{
			buyAllButton.setEnabled(false);
			// TODO should take tickets entered on screen and save in schedule showing
			
			// try purchasing all tickets
			TicketHandler.purchaseAllTickets("rpBuyAll");
			// ** look for answer in purchase listener!!!
		}
		else if (sender == reschedButton)
		{
			reschedButton.setEnabled(false);
			listVP.remove(filmGrid);
			
			setupProgress();
            ScheduleHandler.schedPrep();
            // when prep is done, schedListener is called   
		}
	}
	  
	/**
	 * Hide the wait icon.
	 */
	private void hideWait()
	{
	    //System.out.println("hide wait "+waitCount);
		waitPanel.setVisible(false);
	}

	/**
	 * Show the wait icon with description.
	 * @param String descriptive text to display (loading, updating, etc).
	 */
	private void showWait(String inDescr)
	{
	    //System.out.println("show wait "+waitCount);
		waitText.setHTML(inDescr);
		waitPanel.setVisible(true);
	}	
	
	/**
	 * Updates the screen incrementally (let breathe) after return from server.
	 * @param ShowingRPC[] list of showings to process.
	 * @param int the showing to process in this pass.
	 */
	private void buyAllIncr(final ShowingRPC[] updShowings, final int inEnd) 
	{
	    // do loading of list in pieces.
	    DeferredCommand.addCommand(new IncrementalCommand() {

	        protected int index = 0;
            protected int end = inEnd;
	        int updIndex;
	        
	        /*
	         * (non-Javadoc)
	         * @see com.google.gwt.user.client.IncrementalCommand#execute()
	         */
	        public boolean execute() 
	        {	           
	            // set message if first time through
	            if (index == 0)
	            {
	                showWait("Updating display...");
	                waitCount++;
	            }
                
	            // if film on screen is already ticketed, skip it
	            if (filmList[index].getTicketsReserved() <= 0)
	            {
	                // set as 'selected'
	                selectedFilm = filmList[index];
	                selectedRow = index;

	                // find film in updated showings
	                updIndex = index;
	                if (!filmList[index].equals(updShowings[updIndex]))
	                {
	                    for (int ii=0; ii < updShowings.length; ii++)
	                    {
	                        if (filmList[index].equals(updShowings[ii]))
	                        {
	                            updIndex = ii;
	                            break;
	                        }
	                    }
	                }

	                // see if now ticketed
	                int listTix = filmList[index].getTicketsReserved();
	                int updTix = updShowings[updIndex].getTicketsReserved();
	                if (listTix != updTix)
	                {
	                    // tickets reserved has changed, so show as purchased
	                    selectedRow = index;
	                    processTicketed();	                   
	                    
	                    System.out.println("...BuyAll "+updTix+" for "+
	                            selectedFilm.getMovieID()); 

	                    // Pass 1 - check ticket availability
	                    tixToReserve = updTix;
	                    int isOK = buyTicketsPass1();   
	                    // ignore result (already ticketed, just need to upd tickets)
	                    // TODO should error be checked and all reloaded?

	                    // updated showing
	                    filmList[index].setTicketsReserved(updTix);
	                    ScheduleHandler.rebookMovie(filmList[index]);
	                }

	                // see if sold out
	                boolean updSO = updShowings[updIndex].getIsSoldOut();
	                if (updSO)
	                {
	                    // is sold out!  
	                    filmList[index].setIsSoldOut(updSO);
	                    processSoldOut();   
	                }
	            }       // end if not ticketed  
	            
	            // Is there more to do?
	            index++;
                if (index >= end)
                {
                    // update tickets status widget
                    updateTicketStatus();
                    hideWait();
                    waitCount = 0;
                    return false;  // no
                }
                else
                    return true;   // yes!
	        }
	    });
	}
	
	/**
	 * Listener class to handle after schedule data is read or updated.
	 */
	class readSchedListener implements ServerListener
	{
		/**
		 * Called when server activity starts.
		 * @param String descriptive text to display (loading, updating, etc).
		 */
		public void onServerStart(String inDescr)
		{
			showWait(inDescr);
			waitCount++;
			//System.out.println("on server start..."+waitCount +" "+inDescr);
		}

		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			sysErrors.setHTML("");
			waitCount--;
			//System.out.println("on server end..."+waitCount);
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
			}

			if (gaugeLoaded == false)
			{
				// get schedule gauge
			    schedGaugeVP.clear();
				schedGaugeVP.add(ScheduleHandler.getMySchedGauge());
				statusHP.add(schedGaugeVP);
				gaugeLoaded = true;
			}			
			
			// Rebook will handle screen updates itself, all else rebuild list
			if (!actionID.equalsIgnoreCase("REBOOK"))
			{
			    // load up data
			    loadList();
			}
		}

		/**
		 * Show the error message from server.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String inError)
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
            System.out.println("start1:"+waitCount);
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
                if (gaugeLoaded == false)
                {
                    // get schedule gauge
                    schedGaugeVP.clear();
                    schedGaugeVP.add(ScheduleHandler.getMySchedGauge());
                    statusHP.add(schedGaugeVP);
                    gaugeLoaded = true;
                }           
                
                // update data & screen
                loadList();
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
            waitCount--;
            if (waitCount <= 0)
            {
                hideWait();
                waitCount = 0;
            }
        }
    }   
	
	/**
	 * Listener class to handle after checking availability of showing.
	 */
	class availabilityListener implements ServerListener
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
		 * @param object result - for this listener, boolean t=available.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			sysErrors.setHTML("");
			waitCount--;
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
			}	
			
			if (actionID.equalsIgnoreCase("rpChk1"))
			{
				// handle response from checking showing availability
				// load up data
				loadList();

				Boolean isSoldOut = (Boolean) result;

				if (isSoldOut.booleanValue())
				{
					processSoldOut();
				}
				else
				{
					// showing available, proceed with purchase
					buyTicketsPass3();
				}	
			}
			else if (actionID.equalsIgnoreCase("rpUpd"))
			{
				// process result from updating ticket availability
				// status updated, reflect on screen and indicate resched needed
				if (selectedFilm.getIsSoldOut() == false)
				{				
					// remember that state has already been reversed.
					processAvailable();
				}
				else
				{
					processUserSoldOut();
				}			
			}
		}

		/**
		 * Show the error message from server.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String inError)
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
	 * Listener class to handle after purchasing tickets to a showing.
	 */
	class purchaseListener implements ServerListener
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
		 * Called when server activity ends.
		 * @param String actionID to identify server action.
		 * @param object result - for this listener, int 0=ok, 1=sold out.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			sysErrors.setHTML("");
			waitCount--;
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
			}	
			
			if (actionID.equalsIgnoreCase("rpBuy1"))
			{
				// process result from purchasing single ticket
				Integer answer = (Integer) result;
				if (answer.intValue() == 1)
				{
					// showing is sold out
					selectedFilm.setIsSoldOut(true);
					processSoldOut();				
				}
				else
				{
					// showing available and purchased
					// show as ticketed
					processTicketed();

					updateTicketStatus();				
				}			
			}
			else if (actionID.equalsIgnoreCase("rpRel1"))
			{
				// process result from releasing single ticket
				Integer answer = (Integer) result;
				if (answer.intValue() == 1)
				{
					sysErrors.setHTML("Internal error releasing tickets");				
				}
				else
				{
					// ticket released
					Button pushButton = (Button) filmGrid.getWidget(selectedRow, COL_BUTTON);
					pushButton.setText("Buy");
					pushButton.setTitle("Buy the # of tickets specified for this film.");
					TextBox buyBox = (TextBox)filmGrid.getWidget(selectedRow, COL_TICKETS);
					buyBox.setEnabled(true);
					buyAllButton.setEnabled(true);
					//buyBox.setText(String.valueOf(filmList[selectedIndex].getTicketsRequested()));
					//System.out.println("...Release "+buyBox.getText()+
					//		" "+selectedFilm.getTicketsRequested()+" "+
					//		selectedFilm.getMovieID()+" "+
					//		selectedFilm.getTicketsReserved());				

					filmGrid.clearCell(selectedRow, COL_IMAGE);
					
					updateTicketStatus();
				}			
			}			
			else if (actionID.equalsIgnoreCase("rpBuyAll"))
			{
				ShowingRPC[] updShowings = (ShowingRPC[]) result;
				
				// go thru screen showings and see if any have changed
				// update as necessary
				buyAllIncr(updShowings, filmList.length);									
			}
		}

		/**
		 * Show the error message from server.
		 * @param String descriptive error text to display.
		 */
		public void onServerError(String inError)
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
	 * Called when user logs out - clear out data.
	 */
	public void onLogOut() {
		//Reset ALL PRIVATE DATA;
		//even if there's no way to view this widget
		//the crumbs left in memory can be inspected
		//by an enterprising hacker with a JS debugger handy.
		//clear out text boxes, settings things, lists, etc.
		gaugeLoaded = false;
		filmList = null;
		selectedFilm = null;
		listVP.clear();
		filmGrid = null;	
		statusHP.remove(schedGaugeVP);
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
		// make sure user is logged in.
		if (User.getTokenID() == 0)
		{
			User.setError("You must log in to reserve tickets.");
			History.newItem("Home");
			return;
		}
		
		updateTicketStatus();
		
		// set up each day in schedule for a user
		if (!ScheduleHandler.isLoaded())
		{
			//System.out.println("reading schedule data...");			
			ScheduleHandler.loadMySchedule(true);	
			// Look for answer in server listener!!
		}
		else
		{
			//System.out.println("schedule data already loaded, just display...");
			loadList();
			
			if (gaugeLoaded == false)
			{
				// get schedule gauge
			    schedGaugeVP.clear();
				schedGaugeVP.add(ScheduleHandler.getMySchedGauge());
				statusHP.add(schedGaugeVP);
				gaugeLoaded = true;
			}			
		}		
	}
}
