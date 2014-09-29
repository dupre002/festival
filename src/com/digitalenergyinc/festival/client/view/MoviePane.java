package com.digitalenergyinc.festival.client.view;

import java.util.HashMap;

import com.digitalenergyinc.fest.client.Constants;
import com.digitalenergyinc.fest.client.DataChangeListener;
import com.digitalenergyinc.fest.client.ServerListener;
import com.digitalenergyinc.fest.client.control.MovieHandler;
import com.digitalenergyinc.fest.client.control.SummaryHandler;
import com.digitalenergyinc.fest.client.control.User;
import com.digitalenergyinc.fest.client.model.MovieRPC;
import com.digitalenergyinc.fest.client.model.ShowingRPC;
import com.digitalenergyinc.fest.client.model.TheaterGroup;
import com.digitalenergyinc.fest.client.model.UtilTimeRPC;
import com.digitalenergyinc.festival.client.Sink;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

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
public class MoviePane extends Sink implements ClickHandler, MouseUpHandler,
MouseOverHandler, MouseOutHandler, ChangeHandler
{
	private VerticalPanel mainVP = 
		new VerticalPanel();  				// main panel
	private HorizontalPanel waitPanel =
		new HorizontalPanel();				// wait icon panel
	private HorizontalPanel sysPanel =
		new HorizontalPanel();				// system errors + wait icon panel
	private VerticalPanel listVP = 
		new VerticalPanel();  				// film list panel
	private HorizontalPanel pagingPanel = 
        new HorizontalPanel();              // paging controls panel
	private HorizontalPanel perPagePanel = 
        new HorizontalPanel();              // films per page controls panel
	private VerticalPanel detailVP = 
		new VerticalPanel();  				// film detail panel
	private HorizontalPanel detailControlsHP = 
		new HorizontalPanel();  			// button panel
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

	private boolean isListLoaded = false;   // indicator if film list is loaded
	private boolean hasDataChanged = true;  // indicates if panels need refreshing
	private int waitCount = 0;				// counter of how many loading

	// Ranking elements
	private HorizontalPanel myRanking;  // my ranking panel
	private Image myRank1 = new Image(); // 1 star
	private Image myRank2 = new Image(); // 2 star
	private Image myRank3 = new Image(); // 3 star
	private Image myRank4 = new Image(); // 4 star
	private Image myRank5 = new Image(); // 5 star
	private String starOn = "images/smStarGold.gif";   // URL for on star
	private String starOff = "images/smStarGrey.gif";  // URL for off star
	private int curState;				// current rank state
	private int curRank;				// current rank displayed
	private HTML myRankDesc = new HTML();  // my rank description

	// auto next
	private HorizontalPanel myAutoNextHP;  // auto next panel
	private HTML autoHdg = new HTML();     // auto next heading
	private HTML autoHdg2 = new HTML();    // auto next heading
	private RadioButton rbAutoYes;		   // yes radio button for auto next
	private RadioButton rbAutoNo;		   // no radio button
	private boolean autoNext=false;        // auto next default

	// film detail controls & elements
	private Image nextFilm = new Image(); // next button
	private Image prevFilm = new Image(); // prev button
	private String nextURL = "images/goNext.gif";   // URL for next
	private String prevURL = "images/goPrev.gif";   // URL for prev
	private HTML filmTitle = new HTML();	// film title on detail panel
	private Image listNav = new Image(); // Back to List button
	private String listURL = "images/list.gif";   // URL for back to list
	private Image filmPic = new Image(); // film pic
	private FlexTable filmDetails1;				// film details table for top
	private FlexTable filmDetails2;				// film details (showing) table
	private HTML filmDescr = new HTML();	// film description
	private HTML perPageDescr = new HTML(); // per page heading
	private ListBox lbPerPage = new ListBox();   // number of films per page

	private Image myCheck; 					// scheduled icon (check mark)
	private Image myLock; 					// locked icon
	private Image mySoldOut;				// sold out icon
	private Image myTicketed;				// ticketed icon
	private String checkURL = "images/check.gif";   // URL for scheduled
	private String lockURL = "images/lock.gif";   	// URL for locked
	private String soldOutURL = "images/soldOut.gif";   // URL for sold out
	private String ticketedURL = "images/ticketsized.gif";

	private HashMap<java.lang.String, com.digitalenergyinc.fest.client.model.ShowingRPC> 
	           myShows;

	// data elements
	//private MovieRPC[] filmList;			// list of films
	private int selectedSort = 0;			// selected sort (column header)
	private int newSort = 0;				// holds new column to sort on
	private int selectedIndex = 0;			// selected film in grid
	private boolean freshlyRanked;			// indicates if first time a film is ranked
	private MovieHandler filmHandler;		// handler for movie list
	private String movieIDDisplayed = "";   // movieID currently displayed
	
	// paging elements
	private final Button pageFirst = new Button("&lt;&lt;");
	private final Button pageNext = new Button("&gt;");
	private final Button pagePrev = new Button("&lt;");
	private final HTML pagingStatus = new HTML();
	private int startRow = 0;            // starting row displayed
	private int endRow;                  // ending row displayed
	private int incrRow;                 // number of rows displayed at once
	private int requestedRow = 0;        // requested row (in overall list) to display
    private boolean showDetail = false;  // t=show film detail after paging
    private boolean showPrevDetail = false;  // t=select last one on list

	/**
	 * Every Sink needs an init that defines the menu title and description.
	 */
	public static SinkInfo init() {
		return new SinkInfo("Films",	"") 
		{
			public Sink createInstance() {
				return new MoviePane();
			}
		};
	}

	/**
	 * Basic setup for this panel.
	 */
	public MoviePane() {

		// wait icon
		sysErrors.addStyleName("film-Errors");
		waitIcon = new Image(waitURL);		
		waitIcon.setTitle("Saving/Loading Data from server");
		waitText = new HTML(" ");
		waitText.setStyleName("sched-Film-Font");	
		screenTitle = new HTML("<b>Film List</b>");	
		sysPanel.add(screenTitle);
		helpIcon = new Image(helpURL);     
		helpIcon.setTitle("Click here to see help for this screen.");
		helpIcon.setStyleName("film-Button2");
		helpIcon.addClickHandler(this);
		sysPanel.add(helpIcon);
		waitPanel.add(waitIcon);
		waitPanel.add(waitText);
		sysPanel.add(waitPanel);
		waitPanel.setVisible(false);
		sysPanel.add(sysErrors);

		// detail controls
		nextFilm.setStyleName("film_Image");
		nextFilm.setUrl(nextURL);
		nextFilm.setTitle("Display Next Film");
		prevFilm.setStyleName("film_Image");
		prevFilm.setUrl(prevURL);
		prevFilm.setTitle("Display Previous Film");
		listNav.setStyleName("film_Image");
		listNav.setUrl(listURL);
		listNav.setTitle("Return to Film List");
		filmPic.setStyleName("film_Image");

		nextFilm.addClickHandler(this);
		prevFilm.addClickHandler(this);
		listNav.addClickHandler(this);

		detailControlsHP.add(listNav);
		detailControlsHP.add(prevFilm);
		detailControlsHP.add(nextFilm);
		detailControlsHP.add(filmTitle);	
		
		// number of films per page
		lbPerPage.setTitle("The number of films to display per page.");
		perPageDescr.setHTML("Films per page:");
		perPageDescr.setStyleName("sched-Film-Font"); 
		perPageDescr.addStyleName("film-AlignRight"); 
		lbPerPage.addItem("10");
		lbPerPage.addItem("20");
		lbPerPage.addItem("50");
		lbPerPage.setVisibleItemCount(1);
		lbPerPage.setSelectedIndex(0);        
		lbPerPage.addChangeHandler(this); 
		
		perPagePanel.setStyleName("film-PerPage");
		perPagePanel.addStyleName("film-SpacerRight");
		perPagePanel.add(perPageDescr);
		perPagePanel.add(lbPerPage);

		// film detail
		filmDetails1 = new FlexTable();
		filmDetails1.setStyleName("sched-Film-Font");
		filmDetails2 = new FlexTable();
		filmDetails2.setStyleName("sched-Film-Font");

		detailVP.add(detailControlsHP);
		detailVP.add(filmDetails1);
		detailVP.add(filmDetails2);
		detailVP.setVisible(false);

		// add paging controls
        pagingPanel.setStyleName("film-Navbar");
        pagingStatus.setStyleName("sched-Film-Font");
        pagingStatus.addStyleName("film-SpacerRight");
        pagingPanel.add(pagingStatus);
        pagingPanel.add(pageFirst);
        pagingPanel.add(pagePrev);
        pagingPanel.add(pageNext);
        perPagePanel.setHorizontalAlignment(HorizontalPanel.ALIGN_RIGHT);
        pagingPanel.add(perPagePanel);
        
        pagePrev.setStyleName("film-Button2");
        pageNext.setStyleName("film-Button2");
        pageFirst.setStyleName("film-Button2");
        pagePrev.addClickHandler(this);
        pageNext.addClickHandler(this);
        pageFirst.addClickHandler(this);
        pagePrev.setEnabled(false);
        pageFirst.setEnabled(false);
        listVP.add(pagingPanel);
        
		// make column headings table
		colLabel = new Grid(1, 4);
		colLabel.setStyleName("film-FilmTable");
		colLabel.setWidth("100%");
		colLabel.setHTML(0, 0, "<b>Film Title</b>");
		colLabel.getColumnFormatter().setWidth(0, "250px");
		colLabel.setHTML(0, 1, "<b>ID</b>");
		colLabel.getColumnFormatter().setWidth(1, "60px");
		colLabel.setHTML(0, 2, "<b>Genre</b>");
		colLabel.getColumnFormatter().setWidth(2, "140px");
		setupRankColumn();
		colLabel.setTitle("Click on column heading to sort list by that heading");
		

		colLabel.getColumnFormatter().setStyleName(selectedSort, "film-List-selected");
		colLabel.addClickHandler(this);
		listVP.add(colLabel);
		listVP.setVisible(false);	
		//listVP.setStyleName("opt-Boxed");

		// add to panels
		mainVP.add(sysPanel);	
		mainVP.add(listVP);
		mainVP.add(detailVP);

		mainVP.setStyleName("film-Sink");
		mainVP.addStyleName("film-Boxed");
		mainVP.setVerticalAlignment(VerticalPanel.ALIGN_TOP);

		initWidget(mainVP);

		// listen for changes to data
		filmHandler = MovieHandler.instance();
		MovieHandler.addDataChangeListener(new mySelectionListener());
		MovieHandler.addServerListener(new myServerlistener());
		
		MovieHandler.countMovies("COUNT");
		// **Look for answer in server listener
	}	  
	
	/**
     * This sets up the column heading for rank depending if user is logged in.
     */
    private void setupRankColumn()
    {
        if (User.getTokenID() == 0)
        {
            // user not logged in, take away rank column
            colLabel.resize(1, 3);    
        }
        else
        {
            colLabel.resize(1, 4); 
            colLabel.setHTML(0, 3, "<b>My Interest</b>");
            colLabel.getColumnFormatter().setWidth(3, "75px");      
        }
    }
    
	/**
     * This loads data on the film list incrementally, letting the browser breathe.
     * @param int starting row/index to load.
     * @param int ending row/index to load.
     */
    private void loadListIncr(int inStart, int inEnd)
    {                       
        //System.out.println("in loadListIncr!"+inStart+" "+inEnd);
        // set up grid with data
        filmGrid = new Grid((inEnd-inStart), 3);
        filmGrid.setStyleName("film-FilmTable");
        filmGrid.setWidth("100%");
        filmGrid.getColumnFormatter().setWidth(0, "250px");
        filmGrid.getColumnFormatter().setWidth(1, "60px");
        filmGrid.getColumnFormatter().setWidth(2, "140px");
        if (User.getTokenID() != 0)
        {
            filmGrid.resize((inEnd-inStart), 4);
            filmGrid.getColumnFormatter().setWidth(3, "75px");
        }

        // default to first row as selected and set listener for clicks
        selectedIndex = 0;  
        filmGrid.getRowFormatter().setStyleName(selectedIndex, "film-List-selected");   
        filmGrid.addClickHandler(this);
        movieIDDisplayed = "";

        listVP.add(filmGrid);
        if (showDetail)
        {
            listVP.setVisible(false);
        }
        else
        {
            listVP.setVisible(true);
        }
        
        // now load data incrementally
        loadListData(inStart, inEnd);
    }
    
    /**
     * This loads the film list with data (don't call directly - use LoadListIncr).
     * @param int starting row/index to load.
     * @param int ending row/index to load.
     */
    private void loadListData(final int inStart, final int inEnd)
    {                       
        // do loading of list in pieces.
        Scheduler.get().scheduleIncremental(new RepeatingCommand() {

            protected int start = 0;
            protected int incr = 5;
            protected int end = incr;            
            protected int max = inEnd - inStart;
            protected int rqstRow = inStart;
            
            /*
             * (non-Javadoc)
             * @see com.google.gwt.user.client.IncrementalCommand#execute()
             */
            public boolean execute() 
            {
                // adjust ending point if needed
                if (end > max)
                    end = max;
                
                //System.out.println("incoming "+inStart+" to "+inEnd+" max:"+max);
                //System.out.println("displaying "+start+" to "+end+" rqst: "+rqstRow);
                try{
                    for (int i=start; i < end; i++)
                    {
                        filmGrid.setText(i, 0, MovieHandler.getFilm(rqstRow).getTitle());
                        filmGrid.setText(i, 1, MovieHandler.getFilm(rqstRow).getMovieCode());
                        filmGrid.setText(i, 2, MovieHandler.getFilm(rqstRow).getCategoryShortName());

                        if (User.getTokenID() != 0)
                        {
                            // user is logged in, show ranking
                            myRanking = createStars(MovieHandler.getFilmRank(MovieHandler.getFilm(rqstRow++).getMovieID()));
                            filmGrid.setWidget(i, 3, myRanking);
                        }
                        else
                        {
                            // just go to next row (user not logged in); don't show rank
                            rqstRow++;
                        }
                    }
                }
                catch (Exception e)
                {
                    sysErrors.setHTML("MPGetFilm error while displaying: "+e.getMessage());
                }

                // Is there more to do?
                if (end >= max)
                    return false;

                // move to next portion of data
                start = start + incr;
                end = end + incr;

                // if we're over the end, go one by one until done
                if (end >= max)
                {
                    incr = 1;
                    end = max;
                }
                return true;
            }
        });        
    }

	/**
	 * Display the selected film detail.
	 * If detail is not loaded, wait until server responds
	 */
	private void showFilmDetail()
	{
		// get film detail and display (if detail is available)
		MovieRPC myFilm = null;
		try
		{
			myFilm = MovieHandler.getFilmDetail(selectedIndex+startRow);
		}
		catch (Exception e)
		{
			if (e.getMessage().equalsIgnoreCase("detail"))
			{
				// if detail not loaded, then wait for server response
				return;
			}
			else
			{
				// display any internal errors
				sysErrors.setHTML(e.getMessage());
				return;
			}
		}

		// display data
		screenTitle.setHTML("<b>Film Detail</b>");
		filmTitle.setHTML("<b>"+myFilm.getTitle()+"</b>");

		String filmURL = "images/movies/" + Constants.FESTIVAL_YEAR + "/"
		                  + myFilm.getMovieCode()+".jpg";
		filmPic.setUrl(filmURL);
		filmPic.setTitle("Still Image from Film");
		filmDescr.setHTML(myFilm.getSynopsis());

		movieIDDisplayed = myFilm.getMovieCode();
		filmDetails1.setHTML(0, 0, "<b>Film Code:</b>");
		filmDetails1.setText(0, 1, movieIDDisplayed);

		filmDetails1.setHTML(1, 0, "<b>Director:</b>");
		filmDetails1.setText(1, 1, myFilm.getDirector());

		filmDetails1.setHTML(2, 0, "<b>ScreenWriter:</b>");
		filmDetails1.setText(2, 1, myFilm.getScreenwriter());

		filmDetails1.setHTML(3, 0, "<b>Ticket Type:</b>");
		filmDetails1.setText(3, 1, myFilm.getTicketTypeName());

		filmDetails1.setHTML(4, 0, "<b>Genre:</b>");
		filmDetails1.setText(4, 1, myFilm.getCategoryName());

		filmDetails1.setHTML(5, 0, "<b>Running Time (min):</b>");
		filmDetails1.setText(5, 1, String.valueOf(myFilm.getRunningMin()));

		filmDetails1.setWidget(0, 2, filmPic);
		filmDetails1.getFlexCellFormatter().setRowSpan(0, 2, 6);

		filmDetails1.setWidget(6, 0, filmDescr);
		filmDetails1.getFlexCellFormatter().setColSpan(6, 0, 3);

		// set up rank
		myRanking = new HorizontalPanel();
		myRank1 = new Image();
		myRank2 = new Image();
		myRank3 = new Image();
		myRank4 = new Image();
		myRank5 = new Image();
		myRank1.setStyleName("film_Image");
		myRank2.setStyleName("film_Image");
		myRank3.setStyleName("film_Image");
		myRank4.setStyleName("film_Image");
		myRank5.setStyleName("film_Image");
		myRank1.setUrl(starOff);
		myRank2.setUrl(starOff);
		myRank3.setUrl(starOff);
		myRank4.setUrl(starOff);
		myRank5.setUrl(starOff);
		myRank1.setTitle("Very Low");
		myRank2.setTitle("Low");
		myRank3.setTitle("Medium");
		myRank4.setTitle("High");
		myRank5.setTitle("Very High");
		HTML temp1 = new HTML("<b>Interest:</b>");
		temp1.setStyleName("sched-Film-Font");
		temp1.addStyleName("film-SpacerRight");
		myRanking.add(temp1);
		myRanking.add(myRank1);
		myRanking.add(myRank2);
		myRanking.add(myRank3);
		myRanking.add(myRank4);
		myRanking.add(myRank5);
		myRanking.setStyleName("film-SpacerTop");
		myRanking.addStyleName("film-SpacerBottom");

		// set up auto-next when ranking films
		myAutoNextHP = new HorizontalPanel();
		myAutoNextHP.setStyleName("film-SpacerLeft");
		autoHdg.setHTML("[Click on star to set interest. Automatically " +
		"advance to next film after setting interest?");
		autoHdg.setStyleName("sched-Film-Font");
		autoHdg2.setHTML(" ]");
		autoHdg2.setStyleName("sched-Film-Font");
		myAutoNextHP.add(autoHdg);
		
		rbAutoYes = new RadioButton("AutoGroup", "Yes"); 
		rbAutoNo = new RadioButton("AutoGroup", "No "); 

		if (autoNext)
		{
			rbAutoYes.setValue(true);
			rbAutoNo.setValue(false);
		}
		else
		{
			rbAutoYes.setValue(false);
			rbAutoNo.setValue(true);
		}
		rbAutoYes.setTitle("Yes, Automatically advance to next film");
		rbAutoYes.setStyleName("sched-Film-Font");
		rbAutoYes.addClickHandler(this);
		rbAutoNo.setTitle("No, Do not automatically advance to next film");
		rbAutoNo.setStyleName("sched-Film-Font");
		rbAutoNo.addClickHandler(this);
		myAutoNextHP.add(rbAutoYes);
		myAutoNextHP.add(rbAutoNo);
		myAutoNextHP.add(autoHdg2);

		myRanking.add(myAutoNextHP);
		
		// only show ranking line if user is logged in
		if (User.getTokenID() == 0)
		    myRanking.setVisible(false);
		else
		    myRanking.setVisible(true);

		myRank1.addMouseUpHandler(this);
		myRank2.addMouseUpHandler(this);
		myRank3.addMouseUpHandler(this);
		myRank4.addMouseUpHandler(this);
		myRank5.addMouseUpHandler(this);
		myRank1.addMouseOverHandler(this);
        myRank2.addMouseOverHandler(this);
        myRank3.addMouseOverHandler(this);
        myRank4.addMouseOverHandler(this);
        myRank5.addMouseOverHandler(this);
        myRank1.addMouseOutHandler(this);
        myRank2.addMouseOutHandler(this);
        myRank3.addMouseOutHandler(this);
        myRank4.addMouseOutHandler(this);
        myRank5.addMouseOutHandler(this);

		int theRank = MovieHandler.getFilmRank(myFilm.getMovieID());
		curState = theRank;
		curRank = theRank;
		freshlyRanked = false;			// assume false
		if (theRank == 5)
		{
			myRank5.setUrl(starOn);
			myRank4.setUrl(starOn);
			myRank3.setUrl(starOn);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (theRank == 4)
		{
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOn);
			myRank3.setUrl(starOn);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (theRank == 3)
		{
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOn);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (theRank == 2)
		{
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOff);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (theRank == 1)
		{
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOff);
			myRank2.setUrl(starOff);
			myRank1.setUrl(starOn);
		}
		else if (theRank == 0)
		{
			freshlyRanked = true;
		}

		filmDetails1.setWidget(8, 0, myRanking);
		filmDetails1.getFlexCellFormatter().setColSpan(8, 0, 3);

		filmDetails1.setHTML(10, 0, myFilm.getExtraInfo());
		filmDetails1.getFlexCellFormatter().setColSpan(10, 0, 3);

		// display showings
		myShows = myFilm.getShowingMap();
		
		// clear out old data
		for (int i=2; i<filmDetails2.getRowCount(); i++)
		{
		    filmDetails2.clearCell(i, 1);
		    filmDetails2.clearCell(i, 2);
		    filmDetails2.clearCell(i, 3);
		    filmDetails2.clearCell(i, 4);
		}

		filmDetails2.setHTML(0, 0, "<b>Showings:</b>");
		filmDetails2.getFlexCellFormatter().setColSpan(0, 0, 3);
		filmDetails2.setHTML(1, 1, "<b>Date</b>");
		filmDetails2.setHTML(1, 2, "<b>Time</b>");
		filmDetails2.setHTML(1, 3, "<b>Theater</b>");
		filmDetails2.setHTML(1, 4, "<b>Location</b>");
		filmDetails2.getColumnFormatter().setStyleName(0, "film-SpacerRight");
		filmDetails2.getColumnFormatter().setStyleName(1, "film-SpacerRight");
		filmDetails2.getColumnFormatter().setStyleName(2, "film-SpacerRight");
		filmDetails2.getColumnFormatter().setStyleName(3, "film-SpacerRight");

		// TODO use 0 column for sold-out, scheduled, ticketed icons

		UtilTimeRPC utilTime = new UtilTimeRPC();
		TheaterGroup utilTheater = new TheaterGroup();

		// set up display of showings
		for (int i=1; i<=myShows.size(); i++)
		{
			ShowingRPC myShow = (ShowingRPC) myShows.get(String.valueOf(i));
			filmDetails2.setHTML(i+1, 1, utilTime.convertIndexToDay(myShow.getDayIndex()));
			filmDetails2.setHTML(i+1, 2, myShow.getDisplayStartTime());
			filmDetails2.setHTML(i+1, 3, myShow.getTheaterCode());
			filmDetails2.setHTML(i+1, 4, utilTheater.convertTheaterIDtoGroup(myShow.getTheaterCode()));

			// show icons for showings
			HorizontalPanel hp3 = new HorizontalPanel();
			if (myShow.getIsSoldOut())
			{
				mySoldOut = new Image(soldOutURL);
				mySoldOut.setTitle("This showing is sold out");
				hp3.add(mySoldOut);	
			}			

			if (myShow.getTicketsRequested() >= 1)
			{
				myCheck = new Image();
				myCheck.setUrl(checkURL);
				myCheck.setTitle("This showing is on your schedule");
				hp3.add(myCheck);
			}

			if (myShow.getIsLocked())
			{
				myLock = new Image();
				myLock.setUrl(lockURL);
				myLock.setTitle("This showing is locked on your schedule");
				hp3.add(myLock);
			}

			if (myShow.getTicketsReserved() > 0)
			{
				myTicketed = new Image();
				myTicketed.setUrl(ticketedURL);
				myTicketed.setTitle("You have purchased tickets to this showing");
				hp3.add(myTicketed);
			}			

			filmDetails2.setWidget(i+1, 0, hp3);
		}

		listVP.setVisible(false);
		detailVP.setVisible(true);
	}

	/**
	 * Decides whether to show list or detail of a specific film.
	 * If a film was clicked in the guide (for example), this pane would be displayed
	 * and the MovieHandler would hold which film to show details.
	 */
	private void displayListOrDetail()
	{
		// if list isn't loaded, wait for data before proceeding
		if (isListLoaded == false)
			return;

		// see if detail for a specific film is requested
		if (MovieHandler.getFilmDetailID().equalsIgnoreCase("*LIST*"))
		{
		    System.out.println("MP: list of detail?  LIST!");
			listVP.setVisible(true);
			detailVP.setVisible(false);
		}
		else
		{
		    System.out.println("MP: list of detail?  DETAIL! "+MovieHandler.getFilmDetailID());
			// if film not already displayed, find it
		    if (!movieIDDisplayed.equalsIgnoreCase(MovieHandler.getFilmDetailID()))
		    {
		        // search for movieID in list, look for answer in listener as ID FIND.
		        System.out.println(MovieHandler.getFilmDetailID()+" not displayed. "
		                +movieIDDisplayed);
		        MovieHandler.findFilm(MovieHandler.getFilmDetailID());
		    }
		}
	}

	/**
     * Refreshes the films on the list.
     */
	private void refreshList() {
        // Disable buttons temporarily to stop the user from running off the end.
	    System.out.println("refresh List "+requestedRow);
        pageFirst.setEnabled(false);
        pagePrev.setEnabled(false);
        pageNext.setEnabled(false);

        listVP.remove(filmGrid);
        MovieHandler.getRowData(requestedRow);
        // **Look for response in server Listener
    }
	
	/**
	 * Processes selection from table list.
	 * @param int the column number to sort on.
	 */
	private void changeSortOrder(int selCol) {
		// change highlight to new column/sort
		colLabel.getColumnFormatter().setStyleName(selectedSort, "film-List-notSelected");
		selectedSort = selCol;
		colLabel.getColumnFormatter().setStyleName(selectedSort, "film-List-selected");
		//showWait("Sorting...");

		// process sort and display new list
		MovieHandler.sortList(selCol);

		listVP.remove(filmGrid);
		
		// reset counters and load top of newly sorted list
		requestedRow = 0;
		startRow = requestedRow;
        endRow = startRow + incrRow;
		//loadListIncr(0, MovieHandler.getFilmCount());
        refreshList();

		hideWait();
		//System.out.println("Selected Sort: "+ selCol+"\n");
	}

	/**
	 * Creates a panel of stars in the On or Off position.
	 * @param int the rank to set (number of stars).
	 * @return HorizontalPanel panel of star images.
	 */
	private HorizontalPanel createStars(int inRank)
	{
		HorizontalPanel answerPanel = new HorizontalPanel();
		Image listRank1 = new Image();
		Image listRank2 = new Image();
		Image listRank3 = new Image();
		Image listRank4 = new Image();
		Image listRank5 = new Image();
		listRank1.setStyleName("film_Image");
		listRank2.setStyleName("film_Image");
		listRank3.setStyleName("film_Image");
		listRank4.setStyleName("film_Image");
		listRank5.setStyleName("film_Image");
		listRank1.setUrl(starOff);
		listRank2.setUrl(starOff);
		listRank3.setUrl(starOff);
		listRank4.setUrl(starOff);
		listRank5.setUrl(starOff);
		listRank1.setTitle("Very Low");
		listRank2.setTitle("Low");
		listRank3.setTitle("Medium");
		listRank4.setTitle("High");
		listRank5.setTitle("Very High");
		answerPanel.add(listRank1);
		answerPanel.add(listRank2);
		answerPanel.add(listRank3);
		answerPanel.add(listRank4);
		answerPanel.add(listRank5);

		//myRank1.addMouseListener(this);
		//myRank2.addMouseListener(this);
		//myRank3.addMouseListener(this);
		//myRank4.addMouseListener(this);
		//myRank5.addMouseListener(this);

		if (inRank == 5)
		{
			listRank5.setUrl(starOn);
			listRank4.setUrl(starOn);
			listRank3.setUrl(starOn);
			listRank2.setUrl(starOn);
			listRank1.setUrl(starOn);
		}
		else if (inRank == 4)
		{
			listRank5.setUrl(starOff);
			listRank4.setUrl(starOn);
			listRank3.setUrl(starOn);
			listRank2.setUrl(starOn);
			listRank1.setUrl(starOn);
		}
		else if (inRank == 3)
		{
			listRank5.setUrl(starOff);
			listRank4.setUrl(starOff);
			listRank3.setUrl(starOn);
			listRank2.setUrl(starOn);
			listRank1.setUrl(starOn);
		}
		else if (inRank == 2)
		{
			listRank5.setUrl(starOff);
			listRank4.setUrl(starOff);
			listRank3.setUrl(starOff);
			listRank2.setUrl(starOn);
			listRank1.setUrl(starOn);
		}
		else if (inRank == 1)
		{
			listRank5.setUrl(starOff);
			listRank4.setUrl(starOff);
			listRank3.setUrl(starOff);
			listRank2.setUrl(starOff);
			listRank1.setUrl(starOn);
		}
		return answerPanel;
	}

	/**
	 * Changes the number of stars in the On or Off position.
	 * If ending index is greater than the starting, stars are turned on,
	 * otherwise they are turned off.
	 * @param start the starting number of stars.
	 * @param end the ending number of stars.
	 */
	private void changeStars(int start, int end)
	{
		// change stars on detail panel			
		if (end == 0)
		{
			myRankDesc.setHTML("Not Ranked");
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOff);
			myRank2.setUrl(starOff);
			myRank1.setUrl(starOff);
		}
		else if (end == 1)
		{
			myRankDesc.setHTML("Very Low");
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOff);
			myRank2.setUrl(starOff);
			myRank1.setUrl(starOn);
		}
		else if (end == 2)
		{
			myRankDesc.setHTML("Low");
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOff);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (end == 3)
		{
			myRankDesc.setHTML("Medium");
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOff);
			myRank3.setUrl(starOn);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (end == 4)
		{
			myRankDesc.setHTML("High");
			myRank5.setUrl(starOff);
			myRank4.setUrl(starOn);
			myRank3.setUrl(starOn);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}
		else if (end == 5)
		{
			myRankDesc.setHTML("Very High");
			myRank5.setUrl(starOn);
			myRank4.setUrl(starOn);
			myRank3.setUrl(starOn);
			myRank2.setUrl(starOn);
			myRank1.setUrl(starOn);
		}

		curState = end;  // set current state to ending state
	}

	/**
	 * Changes the number of stars of a film on the list based on selectedIndex.
	 * @param int the rank to change it to.
	 */
	private void changeStarsOnList(int inRank)
	{
		// change stars on list
		Widget myWidget = filmGrid.getWidget(selectedIndex, 3);
		filmGrid.remove(myWidget);
		try{
			myRanking = createStars(inRank);
		}
		catch (Exception e)
		{
			sysErrors.setHTML(e.getMessage());
		}
		filmGrid.setWidget(selectedIndex, 3, myRanking);
	}

	/**
	 * Change highlight of row selected on list.
	 * @param int the row number selected.
	 */
	private void changeFilmSelection(int selRow) {
		// change highlight to new row
		filmGrid.getRowFormatter().setStyleName(selectedIndex, "film-List-notSelected");
		selectedIndex = selRow;
		filmGrid.getRowFormatter().setStyleName(selectedIndex, "film-List-selected");
	}

	/**
	 * Advances to detail of next film.
	 */
	private void gotoNextFilm()
	{
		int newRow = selectedIndex + 1;
		
		// if asking for a film not on the current page, get next page from handler
		if (newRow >= incrRow)
		{
		    //System.out.println("Asking for new page!");
		    showDetail = true;
		    gotoNextPage();
		}
		else
		{
		    // check if at end of list, if so, go to first film
		    int tempPosn = startRow + newRow;
		    if (tempPosn >= MovieHandler.getFilmCount())
		    {
		        requestedRow = 0;
		        showDetail = true;
	            refreshList();
	            return;
		    }

		    // just show next row
		    changeFilmSelection(newRow);
		    showFilmDetail();
		}
	}

	/**
	 * Advances to detail of previous film.
	 */
	private void gotoPrevFilm()
	{
		int newRow = selectedIndex - 1;
		
		// do we have to go back a page?
		if (newRow < 0)
		{
		    // if there is a previous page, go back to last one on previous page
		    //System.out.println("start/incr "+startRow+" "+incrRow);
		    if (startRow >= incrRow)
		    {
		        showDetail = true;
		        showPrevDetail = true;
		        gotoPrevPage();
		        return;
		    }
		}

		// TODO need to decide if will go to last film if at first film
		if (newRow < 0)
		    newRow = MovieHandler.getFilmCount() - 1;
		changeFilmSelection(newRow);
		showFilmDetail();
	}
	
	/**
     * Advances to next page of films on list.
     */
    private void gotoNextPage()
    {
        requestedRow += incrRow;
        if (requestedRow >= MovieHandler.getFilmCount())
            requestedRow = MovieHandler.getFilmCount() - 1;
        refreshList();
    }
    
    /**
     * Advances to next page of films on list.
     */
    private void gotoPrevPage()
    {
        requestedRow -= incrRow;
        if (requestedRow < 0) {
            requestedRow = 0;
        }
        refreshList();
    }

	/**
	 * Handles when controls are clicked.
	 * @param event the incoming object that was clicked.
     */
    public void onClick(ClickEvent event)
	{
        Widget sender = (Widget) event.getSource();
		// check which button is pressed
	    if (sender == helpIcon)
        {
            // show help
	        HelpWidget myHelp;
	        if (screenTitle.getHTML().equalsIgnoreCase("<b>Film Detail</b>"))
	            myHelp = new HelpWidget(2); 
	        else
	            myHelp = new HelpWidget(1); 
        }
        else if (sender == listNav)
		{
			detailVP.setVisible(false);
			screenTitle.setHTML("<b>Film List</b>");
			MovieHandler.setFilmDetailID("*LIST*");
			movieIDDisplayed = "";
			listVP.setVisible(true);	
		}
		else if (sender == nextFilm)
		{
			gotoNextFilm();
		}
		else if (sender == prevFilm)
		{
			gotoPrevFilm();
		}
		else if (sender == rbAutoYes)
		{
			// auto next after ranking film
			autoNext = true;
		}	
		else if (sender == rbAutoNo)
		{
			// auto next after ranking film
			autoNext = false;
		}	
		else if (sender == pageNext) 
		{
		    gotoNextPage();
        } 
		else if (sender == pagePrev) 
		{
		    gotoPrevPage();
        } 
		else if (sender == pageFirst) 
		{
		    //System.out.println("pageFirst");
		    requestedRow = 0;
            refreshList();
        }
	    // check for table clicks
		else if (sender == colLabel)
        {
		    Cell mycell = colLabel.getCellForEvent(event);
            int col = mycell.getCellIndex();
            //System.out.println("is column header!");
            showWait("Sorting..."); 
            newSort = col;
            Scheduler.get().scheduleDeferred(new ScheduledCommand() { public void execute() {
                changeSortOrder(newSort); }});
        }
        else if (sender == filmGrid)
        {
            Cell mycell = filmGrid.getCellForEvent(event);
            int row = mycell.getRowIndex();
            //System.out.println("is NOT column header!");
            changeFilmSelection(row);
            showFilmDetail();
        }
	}
	
	/**
     * Handles when the films per page is clicked.
     * @param ChangeEvent the incoming event.
     */
    public void onChange(com.google.gwt.event.dom.client.ChangeEvent event)
    {
        Widget sender = (Widget) event.getSource();
        if (sender == lbPerPage)
        {
            // set films per page
            int perPage = Integer.parseInt(lbPerPage.getValue(lbPerPage.getSelectedIndex()));
            MovieHandler.setMaxGetRows(perPage);
            incrRow = perPage;
            System.out.println("Change perPage:"+perPage);
            
            // reset starting row if not on top of new page
            int temp = requestedRow;
            System.out.println("MP b4 startRow!:"+requestedRow);           
            int counter = 0;
            while (temp >= perPage)
            {
                temp = temp - perPage;
                counter++;
            }
            requestedRow = perPage * counter;
            System.out.println("MP new startRow!:"+requestedRow);           
            
            refreshList();
        }   
    }

	/**
	 * Listen for mouse entering the star ranking widget.
	 * @param MouseOverEvent the offending mouse event.
	 */
	public void onMouseOver(MouseOverEvent event) 
	{
	    Widget sender = (Widget) event.getSource();
		//System.out.println("Mouse Enter"+curState+" "+curRank);
		//  find how many stars we should highlight (which star they hover over)
		int desiredState = 0;		
		if (sender == myRank1)
			desiredState = 1;
		else if (sender == myRank2)
			desiredState = 2;
		else if (sender == myRank3)
			desiredState = 3;
		else if (sender == myRank4)
			desiredState = 4;
		else if (sender == myRank5)
			desiredState = 5;

		// now if a change is needed, temporarily change # of stars turned on
		if (curState != desiredState)
		{
			changeStars(curState, desiredState);	
		}
	} 

	/**
	 * Listen for mouse leaving the star ranking widget which will restore
	 * the stars showing to the original number turned on.
	 * @param MouseOutEvent the offending mouse event.
	 */
	public void onMouseOut(MouseOutEvent event) 
	{
		//System.out.println("Mouse Leave "+curState+" "+curRank);
		// restore the state to the original rank (if it has changed)
		if (curState != curRank)
		{
			changeStars(curState, curRank);				
		}
	} 


	/**
	 * Listen for mouse up click the star ranking widget which means user is updating rank.
	 * @param MouseUpEvent the offending mouse event.
	 */
	public void onMouseUp(MouseUpEvent event) 
	{
	    Widget sender = (Widget) event.getSource();
		// if film is ranked for the first time, increment number of films ranked
		if (freshlyRanked == true)
		{
			if ((sender.getTitle().equals("Very Low")) ||
					(sender.getTitle().equals("Low")) ||
					(sender.getTitle().equals("Medium")) ||
					(sender.getTitle().equals("High")) ||
					(sender.getTitle().equals("Very High")))
			{
				int ranked = SummaryHandler.getMoviesRanked() + 1;
				int unranked = SummaryHandler.getMoviesUnranked() - 1;
				SummaryHandler.setMoviesRanked(ranked);
				SummaryHandler.setMoviesUnranked(unranked);
			}
		}

		// find out which rank was pressed
		if (sender.getTitle().equals("Very Low"))
		{
			if (curRank == 1)
			{
			    // no change - already a 1
			    if (autoNext)
	                gotoNextFilm();
				return;		
			}

			// change rank
			MovieHandler.changeRank("RANK", (selectedIndex+startRow), 1, freshlyRanked);

			changeStars(curRank, 1);	// change the display
			changeStarsOnList(1);
			curRank = 1;				// set the new rank		

			// advance to next film if requested
			if (autoNext)
				gotoNextFilm();
		}
		else if (sender.getTitle().equals("Low"))
		{
			if (curRank == 2)
			{
                // no change - already a 2
                if (autoNext)
                    gotoNextFilm();
                return;     
            }

			// change rank
			MovieHandler.changeRank("RANK", (selectedIndex+startRow), 2, freshlyRanked);

			changeStars(curRank, 2);	// change the display
			changeStarsOnList(2);
			curRank = 2;				// set the new rank

			// advance to next film if requested
			if (autoNext)
				gotoNextFilm();
		}  
		else if (sender.getTitle().equals("Medium"))
		{
			if (curRank == 3)
			{
                // no change - already a 3
                if (autoNext)
                    gotoNextFilm();
                return;     
            }

			// change rank
			MovieHandler.changeRank("RANK", (selectedIndex+startRow), 3, freshlyRanked);

			changeStars(curRank, 3);	// change the display
			changeStarsOnList(3);
			curRank = 3;				// set the new rank

			// advance to next film if requested
			if (autoNext)
				gotoNextFilm();
		}  
		else if (sender.getTitle().equals("High"))
		{
			if (curRank == 4)
			{
                // no change - already a 4
                if (autoNext)
                    gotoNextFilm();
                return;     
            }

			// change rank
			MovieHandler.changeRank("RANK", (selectedIndex+startRow), 4, freshlyRanked);

			changeStars(curRank, 4);	// change the display
			changeStarsOnList(4);
			curRank = 4;				// set the new rank

			// advance to next film if requested
			if (autoNext)
				gotoNextFilm();
		}  
		else if (sender.getTitle().equals("Very High"))
		{
			if (curRank == 5)
			{
                // no change - already a 5
                if (autoNext)
                    gotoNextFilm();
                return;     
            }

			// change rank
			MovieHandler.changeRank("RANK", (selectedIndex+startRow), 5, freshlyRanked);

			changeStars(curRank, 5);	// change the display
			changeStarsOnList(5);
			curRank = 5;				// set the new rank

			// advance to next film if requested
			if (autoNext)
				gotoNextFilm();
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
     * Sets up page controls: heading and enable/disable buttons
     */
    private void setupPageControls() {
        // load data on list
        pagingStatus.setText("Displaying films "+(startRow+1)+" to "+endRow+" ");
        // enable buttons
        if (startRow != 0)
        {
            pageFirst.setEnabled(true);
            pagePrev.setEnabled(true);
        }
        if (endRow != MovieHandler.getFilmCount())
            pageNext.setEnabled(true);
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
			// indicate upon going back to this screen, data needs refreshing
			hasDataChanged = true;
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
		    //System.out.println("MPserverStart:="+inDescr);
			showWait(inDescr);
			waitCount++;
		}

		/**
		 * Called when server activity ends.  Update data on panel.
		 * @param String actionID to identify server action, no result expected.
		 * @param object result - for this listener, no result expected.
		 */
		public void onServerEnd(String actionID, Object result)
		{
			System.out.println("MPserverEnd: id="+actionID);
			// look for actionID from server to identify what happened
			if (actionID.equalsIgnoreCase("CLEAR"))
			{
			    hasDataChanged = true;   
			    listVP.remove(filmGrid);
		        filmGrid = null;
			    isListLoaded = false;
			    setupRankColumn();
			}
			else if (actionID.equalsIgnoreCase("LIST"))
			{
			    // if already loaded (i.e., reloading after login)
			    if (isListLoaded)
			    {
			        // clear list so it can be reloaded
			        listVP.remove(filmGrid);
	                filmGrid = null;
			    }
			    
			    // reload list
				loadListIncr(0, MovieHandler.getFilmCount());
				isListLoaded = true;
				// decide whether to show list or detail
				displayListOrDetail();
			}
			else if (actionID.equalsIgnoreCase("RANK"))
			{
				// rank saved, do nothing
			}
			else if (actionID.equalsIgnoreCase("DETAIL"))
			{
				// more details returned, update list and display detail
				showFilmDetail();
			}
			else if (actionID.equalsIgnoreCase("FIND"))
            {
                // detail film requested from other page (like schedule), but
			    // film is not on the current page...until now
			    
			    // if already loaded, clear it so it can be reloaded
                if (isListLoaded)
                {
                    // clear list so it can be reloaded
                    listVP.remove(filmGrid);
                    filmGrid = null;
                }
                
			    // get start/end row from page that was read
			    startRow = MovieHandler.getSaveStart();
			    requestedRow = MovieHandler.getSaveStart();
                endRow = startRow + incrRow;
                //System.out.println("Start/Req/End="+startRow+" "+
                //        requestedRow+" "+endRow);
                
			    setupPageControls();

                // show data
                loadListIncr(startRow, endRow);
                
                // reposition to correct row
                int tempSel = 0;
                try
                {
                    for (int i=startRow; i < endRow; i++)
                    {
                        String tempID = MovieHandler.getFilm(i).getMovieCode();
                        if (tempID.equalsIgnoreCase(MovieHandler.getFilmDetailID()))
                        {
                            //System.out.println("Reposition Row found."+i);
                            break;
                        }
                        else
                        {
                            tempSel++;
                        }
                    }
                }
                catch (Exception e)
                {
                    sysErrors.setHTML("MPincr: "+e.getMessage());
                } 
                //System.out.println("Reposition Row="+tempSel);
                changeFilmSelection(tempSel);
                
                // show detail of film
                showFilmDetail();
            }
			else if (actionID.equalsIgnoreCase("PAGE"))
			{
			    // page request came back - data is ready
			    isListLoaded = true;
			    // save new displayed row counters
			    startRow = requestedRow;
			    endRow = startRow + incrRow;
			    if (endRow >= MovieHandler.getFilmCount())
			    {
			        endRow = MovieHandler.getFilmCount();
			    }
			    
			    setupPageControls();
		        
			    // show data
			    loadListIncr(startRow, endRow);
			    
			    // if on detail when asked to page, return to detail
			    if (showDetail)
			    {		
			        showDetail = false;
			        
			        // if want previous page, return to last one on list
			        if (showPrevDetail)
			        {
			            changeFilmSelection(incrRow-1);
			            showPrevDetail = false;
			        }
			        
			        // show film detail
			        showFilmDetail();
			    }
			}
			else if (actionID.equalsIgnoreCase("COUNT"))
			{
			    endRow = MovieHandler.getMaxGetRows();
		        incrRow = endRow;
			}

			sysErrors.setHTML("");
			waitCount--;
			if (waitCount <= 0)
			{
				hideWait();
				waitCount = 0;
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
			// display list
			listVP.setVisible(true);
            detailVP.setVisible(false);
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
	    System.out.println("in MPon show (change/loaded?):"+
	            hasDataChanged+"/"+isListLoaded);
		// if data changed, reload data
		if (hasDataChanged)
		{
		    hasDataChanged = false;
		    
			// make sure data on panels is updated
			// TODO instead of rereading data, could updateShowing when 
			// user schedule is loaded (move processing to client)
		    if (isListLoaded == false)
		    {
		        // reset to showing list
		        listVP.setVisible(true);
	            detailVP.setVisible(false);
	            
	            // if asking for list, read list - else show detail in 
	            // call to ListOrDetail below.
	            if (MovieHandler.getFilmDetailID().equalsIgnoreCase("*LIST*"))
	            {
	                MovieHandler.getRowData(startRow);
	                // **Wait for response in server call back!
	            }
		    }
		    else
		    {
		        // some data has changed, recreate list
		        showWait("Reloading...");
		        loadListIncr(0, MovieHandler.getFilmCount());
		        hideWait();
		    }
		}

		// decide whether to show list or detail
		displayListOrDetail();
	}
}
