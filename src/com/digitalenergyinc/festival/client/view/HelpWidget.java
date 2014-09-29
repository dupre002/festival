package com.digitalenergyinc.festival.client.view;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the set of help panels.
 * 
 * <p>Title: Film Festival scheduler.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class HelpWidget 
{
    private VerticalPanel helpVP;           // vertical panel for help text
    private MyPopup popup;                  // popup panel for help
    private MyDialog popup2;                // panel for help
    
    // images to display in help
    private Image myInfo;                   // info icon    
    private Image myCheck;                  // scheduled icon (check mark)
    private Image myCheck2;                 // scheduled icon (check mark)
    private Image myLock;                   // locked icon
    private Image mySoldOut;                // sold out icon
    private Image myTicketed;               // ticketed icon
    private Image nextFilm = new Image(); // next button
    private Image prevFilm = new Image(); // prev button
    private Image listNav = new Image(); // Back to List button
    
    private String infoURL = "images/info.gif";     // URL for info icon    
    private String checkURL = "images/check.gif";   // URL for scheduled
    private String noCheckURL = "images/checkempty.gif"; // URL for unscheduled
    private String lockURL = "images/lock.gif";     // URL for locked
    private String unlockURL = "images/lockempty.gif";   // URL for unlocked
    private String soldOutURL = "images/soldOut.gif";   // URL for sold out
    private String ticketedURL = "images/ticketsized.gif";
    private String nextURL = "images/goNext.gif";   // URL for next
    private String prevURL = "images/goPrev.gif";   // URL for prev
    private String listURL = "images/list.gif";   // URL for back to list
    
    private static class MyPopup extends PopupPanel {

        public MyPopup() {
          // PopupPanel's constructor takes 'auto-hide' as its boolean parameter.
          // If this is set, the panel closes itself automatically when the user
          // clicks outside of it.
          super(true);

          // PopupPanel is a SimplePanel, so you have to set it's widget property to
          // whatever you want its contents to be.
        }
      }
    
    private static class MyDialog extends DialogBox {

        public MyDialog() {
          // Set the dialog box's caption.
          setText("Help");

          // DialogBox is a SimplePanel, so you have to set its widget property to
          // whatever you want its contents to be.
          Button ok = new Button("OK");
          ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent sender) {
              MyDialog.this.hide();
            }
          });
          //setWidget(ok);
        }
      }
    
    /**
     * Constructor to build the generic help screen.
     */
    public HelpWidget()
    {
        setupHelp(0);
    }
    
    /**
     * Constructor to build the generic help screen.
     * @param int helpID indicates which help topic to display.
     * Help ID 0 = generic help screen
     * Help ID 1 = Movie List
     * Help ID 2 = Movie Detail
     * Help ID 3 = Schedule/Grid
     * Help ID 4 = My Schedule
     * Help ID 5 = My Options
     * Help ID 6 = Reservations
     */
    public HelpWidget(int helpID)
    {
        setupHelp(helpID);
    }
    
    /**
     * Remove help panel.
     */
    private void hideHelp()
    {
        if (popup != null)
            popup.hide();
        
        if (popup2 != null)
            popup2.hide();
    }
    
    /**
     * Setup progress panel.
     */
    private void setupHelp(int helpID)
    {
        // create VP to hold everything and then add to popup
        helpVP = new VerticalPanel();
        helpVP.setStyleName("film-Help");
        ScrollPanel scroll = new ScrollPanel(helpVP);     
        
        double hght = (Window.getClientHeight() * .75);
        double wid = (Window.getClientWidth() * .60);
        //System.out.println("width "+(int)wid);
        scroll.setWidth((int)wid+"px");
        
        // set up close button
        Button closeButton = new Button("Close");
        closeButton.setStyleName("film-Button");
        closeButton.setTitle("Click to close Help window.");
        closeButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent sender) {
            // hide popup
              hideHelp();
          }
        });
        
        // set up text.
        HTML help1 = null;
        
        // based on helpID, create the right text
        if (helpID == 1)
        {
            help1 = movieList();
        }
        else if (helpID == 2)
        {
            help1 = movieDetail();
        }
        else if (helpID == 3)
        {
            scroll.setHeight((int)hght+"px");
            help1 = filmGuide();
        }
        else if (helpID == 4)
        {
            help1 = mySchedule();
        }
        else if (helpID == 5)
        {
            scroll.setHeight((int)hght+"px");
            help1 = options();
        }
        else if (helpID == 6)
        {
            help1 = reserve();
        }
        else if (helpID == 7)
        {
            help1 = appt();
        }
        else
        {
            help1 = generic();
        }
        
        help1.setStyleName("sched-Film-Font");
        
        helpVP.add(help1);
        
        helpVP.setHorizontalAlignment(HorizontalPanel.ALIGN_CENTER);
        helpVP.add(closeButton);
        popup = new MyPopup();
        //popup.setWidget(helpVP);
        
        popup2 = new MyDialog();
        popup2.setWidget(scroll);
        
        // Position the popup 1/3rd of the way down and across the screen, and
        // show the popup. Since the position calculation is based on the
        // offsetWidth and offsetHeight of the popup, you have to use the
        // setPopupPositionAndShow(callback) method.

        popup2.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 3;
                int top = (Window.getClientHeight() - offsetHeight) / 3;
                popup2.setPopupPosition(left, top);
            }
        }); 

        //popup2.show();
    }
    
    /**
     * Create help text for generic screen.
     * @return HTML returns HTML help text.
     */
    private HTML generic()
    {
        HTML helpText = new HTML(
                "<h3>Film Festival Scheduling</h3>" +
                "<p><b>First</b>, Rank your interest in seeing a film. " +               
                "<p><b>Second</b>, Tell the system some information and " +
                "it will automatically create a schedule for you," +
                "<p><b>Third</b>, When it is your time to purchase your " +
                "tickets, use the Reservation page to order them and " +
                "easily reschedule if desired showings are sold out. ",
                true); 
        
        return helpText;
    }
    
    /**
     * Create help text for the movie list.
     * @return HTML returns HTML help text.
     */
    private HTML movieList()
    {
        HTML helpText = new HTML(
                "<h3>Film List</h3>" +
                "<p>The Film List shows the list of films showing at the"+
                " festival." +
                "<p><h4>Film Detail</h4>" +
                "<p>To see the detail about a film, click on its title." +
                "<p><h4>Paging</h4>" +
                "<p>Click on the navigation icons above the film list to" +
                " scroll to the next page of films." +
                "<p><h4>Sorting</h4>" +
                "<p>You can sort the list in different orders by clicking " +
                "on any column heading.  The default order is by Film Title."+
                "<p><h4>Films Per Page</h4>" +
                "<p>Changing this will display a different number of films " +
                "on your list.  Note that the more films you display at one " +
                "time, the longer the list may take to load.",
                true); 
        
        return helpText;
    }
    
    /**
     * Create help text for the movie detail.
     * @return HTML returns HTML help text.
     */
    private HTML movieDetail()
    {
        nextFilm = new Image();
        nextFilm.setUrl(nextURL);
        nextFilm.setStyleName("film-Button2");
        
        prevFilm = new Image();
        prevFilm.setUrl(prevURL);
        prevFilm.setStyleName("film-Button2");
        
        listNav = new Image();
        listNav.setUrl(listURL);
        listNav.setStyleName("film-Button2");
        
        HTML helpText = new HTML(
                "<h3>Film Detail</h3>" +
                "This is the detail of a film.  Read a synopsis of the film" +
                "and find out where & when the film is showing." +                
                "<p><h4>Previous/Next Film</h4>" +
                "<p>Click on the arrows near the top of the screen to " +
                "navigate to the next (" +
                nextFilm +
                ") or previous film (" +
                prevFilm+
                ")." +
                "<p><h4>Return to List</h4>" +
                "<p>Click on the list icon (" +
                listNav +
                ") in order to return to the film list." +
                "<p><h4>Ranking Interest</h4>" +
                "<p>Once you are logged on, you are able to rank your " +
                "interest in seeing this film by clicking on the row of " +
                "stars." +
                "<p><h4>Auto Advance</h4>" +
                "<p>For your convenience, you can have the system automatically" +
                " advance to the next film after you have picked an interest level" +
                " (clicked on a star).  Just set the Auto Advance to Yes.",
                true); 
        
        return helpText;
    }
        
    /**
     * Create help text for film guide screen.
     * @return HTML returns HTML help text.
     */
    private HTML filmGuide()
    {
        myInfo = new Image();
        myInfo.setUrl(infoURL);
        myInfo.setStyleName("film-Button2");
        
        nextFilm = new Image();
        nextFilm.setUrl(nextURL);
        nextFilm.setStyleName("film-Button2");
        
        prevFilm = new Image();
        prevFilm.setUrl(prevURL);
        prevFilm.setStyleName("film-Button2");
        
        mySoldOut = new Image();
        mySoldOut.setUrl(soldOutURL);
        mySoldOut.setStyleName("film-Button2");
        
        myCheck = new Image();
        myCheck.setUrl(noCheckURL);
        myCheck.setStyleName("film-Button2");
        
        myCheck2 = new Image();
        myCheck2.setUrl(checkURL);
        myCheck2.setStyleName("film-Button2");
        
        myLock = new Image();
        myLock.setUrl(lockURL);
        myLock.setStyleName("film-Button2");
        
        myTicketed = new Image();
        myTicketed.setUrl(ticketedURL);
        myTicketed.setStyleName("film-Button2");
        
        HTML helpText = new HTML(
                "<h3>Film Guide</h3>" +
                "<p>The Film Guide shows the films playing at the festival " +
                "in a grid format with theaters across the top and times " +
                "down the side." +
                "<p><h4>Film Information</h4>" +
                "<p>Each film is represented by a colored square on the film " +
                "grid at the time it is shown at a specific theater.  The " +
                "film ID (5 character abbreviation) is shown.  Clicking on " +
                "the film ID will take you to the detail for that film." +
                "<p>Holding your mouse over the " + myInfo + 
                " will show more information about the film." +
                "<p>Icons you may see:" +
                "<p><TABLE border=1 class=sched-Film-FontLarge>" +
                "<TR><TH><i>Icon</i></TH><TH><i>Meaning</i></TH></TR>" +
                "<TH>" + mySoldOut +
                "</TH><TD>Indicates that the showing " +
                "has wait-listed for tickets.  " +
                "If you don't already have tickets, you should click on " +
                "the reschedule button to create a new schedule.</TD></TR>"+
                "<p><TR><TD>" + myCheck2 +
                "</TD><TD>Indicates that the showing is on your schedule and " +
                "ready to be purchased (on the Reservation page).</TD></TR>"+
                "<p><TR><TD>" + myLock +
                "</TD><TD>Indicates that the showing is locked on the schedule " +
                "and it won't be moved if you reschedule.</TD></TR>"+
                "<p><TR><TD>" + myTicketed +
                "</TD><TD>Indicates that a ticket for the showing has been " +
                "purchased.</TH></TR></TABLE>"+ 
                "<p><h4>Scrolling</h4>" +
                "<p>You can navigate to the next day by using the previous (" +
                prevFilm +
                ") and next buttons (" +
                nextFilm +
                ")." +
                "<p><h4>Theaters/Sold Out</h4>" +
                "<p>You can select which theaters to display in the grid " +
                "by checking the theater groupings at the top of the screen. " +
                "You can also hide or show the sold out showings." +
                "<p><h4>Buying Individual Tickets</h4>" +
                "<p>If you choose not to use the automatic scheduling " +
                "capability of the system, you can manually add showings to " +
                "your schedule and then go to the reservation screen to buy " +
                "them." +
                "<p>After logging in, you can add a film to your schedule by " +
                "clicking on the empty checkmark (" +
                myCheck +
                ").  The filled-in checkmark (" +
                myCheck2 +
                ") indicates it is on your schedule.  Note that you must have " +
                "tickets remaining in order add it to your schedule." +
                "<p>If you want to remove a showing from your schedule, you can " +
                "simply uncheck the showing (click on the filled-in checkmark).  " +
                "A showing can be locked on the schedule.  "+
                "This means this showing will not "+
                "be moved when rescheduling happens "+
                "(for instance, when a showing is sold out)."+  
                "If you want to make sure you get a specific "+
                "showing for a film, lock it. </p>",
                true); 
        
        return helpText;
    }
    
    /**
     * Create help text for my schedule screen.
     * @return HTML returns HTML help text.
     */
    private HTML mySchedule()
    {
        myInfo = new Image();
        myInfo.setUrl(infoURL);
        myInfo.setStyleName("film-Button2");
        
        mySoldOut = new Image();
        mySoldOut.setUrl(soldOutURL);
        mySoldOut.setStyleName("film-Button2");
        
        myCheck2 = new Image();
        myCheck2.setUrl(checkURL);
        myCheck2.setStyleName("film-Button2");
        
        myLock = new Image();
        myLock.setUrl(lockURL);
        myLock.setStyleName("film-Button2");
        
        myTicketed = new Image();
        myTicketed.setUrl(ticketedURL);
        myTicketed.setStyleName("film-Button2");
        
        HTML helpText = new HTML(
                "<h3>My Schedule</h3>"+
                "<p>My Schedule shows the films that are currently on your " +
                "schedule in a grid format with days across the top and times " +
                "down the side." +
                "<p>Allows you to manually change which films appear "+
                "<p><h4>Film Information</h4>" +
                "<p>Each film is represented by a colored square on the film " +
                "grid at the time it is shown at a specific theater.  The " +
                "film ID (5 character abbreviation) is shown.  Clicking on " +
                "the film ID will take you to the detail for that film." +
                "<p>Holding your mouse over the " + myInfo + 
                " will show more information about the film." +
                "<p>Icons you may see:" +
                "<p><TABLE border=1 class=sched-Film-FontLarge>" +
                "<TR><TH><i>Icon</i></TH><TH><i>Meaning</i></TH></TR>" +
                "<TH>" + mySoldOut +
                "</TH><TD>Indicates that the showing " +
                "has wait-listed for tickets.  " +
                "If you don't already have tickets, you should click on " +
                "the reschedule button to create a new schedule.</TD></TR>"+
                "<p><TR><TD>" + myCheck2 +
                "</TD><TD>Indicates that the showing is on your schedule and " +
                "ready to be purchased (on the Reservation page).</TD></TR>"+
                "<p><TR><TD>" + myLock +
                "</TD><TD>Indicates that the showing is locked on the schedule " +
                "and it won't be moved if you reschedule.</TD></TR>"+
                "<p><TR><TD>" + myTicketed +
                "</TD><TD>Indicates that a ticket for the showing has been " +
                "purchased.</TH></TR></TABLE>"+                
                "<p><h4>Create Appointment</h4>" +
                "<p>You can create an appointment on your schedule by " +
                "clicking on the Create Appointment Button.  An appointment " +
                "will reserve time on your schedule and no films will be " +
                "scheduled at that time.  Use appointments to hold dinner " +
                "reservations or skiing time." +
                "<p><h4>Export Schedule</h4>" +
                "<p>Clicking on the Export Schedule button will allow you to " +
                "export their calendar to a Google calendar.  You will be asked " +
                "to log in to your google account prior to the export. ",
                true); 
        
        return helpText;
    }
    
    /**
     * Create help text for appointment screen.
     * @return HTML returns HTML help text.
     */
    private HTML appt()
    {
        HTML helpText = new HTML(
                "<h3>Appointment Help </h3>"+
                "<p>Appointments allow you to reserve time on your " +
                "calendar and will prevent any"+
                "showings from being scheduled during the appointment.  " +
                "This is a great way to"+
                "keep a specific time open for dinner reservations " +
                "or meetings you may have."+
                "<br><br>"+
                "<h4>Short Description</h4>"+
                "<p>The short description of the appointment.  " +
                "This will identify the appointment"+
                "on the calendar. (required) </p>"+
                "<h4>Long Description</h4>"+
                "<p>The long description of the appointment.  " +
                "More room for you to be descriptive"+
                "appointment.</p>"+
                "<h4>Date</h4>"+
                "<p>The date of your appointment.  This is limited to "+
                "dates within the festival dates.</p>"+
                "<h4>Start Time</h4>"+
                "<p>The time your appointment starts.</p>"+
                "<p>Be sure to include travel time before and"+
                "after your appointment to restrict showings from " +
                "being scheduled right up to the "+
                "start of your appointment.</p>"+
                "<h4>End Time</h4>"+
                "<p>The ending time of your appointment. </p>"+
                "<h4>Location</h4>"+
                "<p>The location of your appointment. </p>"+
                "<h4>Notes</h4>"+
                "<p>A place for you to record any notes about the appointment. </p>",
                true); 
        
        return helpText;
    }    

    /**
     * Create help text for options screen.
     * @return HTML returns HTML help text.
     */
    private HTML options()
    {
        HTML helpText = new HTML(
                "<h3>Scheduling Options Help </h3>"+
                "<h4>Days</h4>"+
                "<p>  This is the first & last date that you will " +
                "be attending films. "+
                "For package holders, "+
                "it should match the dates of your package. </p>"+

                "<h4>Time</h4>"+
                "<p>  This is the first/last time of the day to schedule a film. "+
                "Set the starting time later if you want to sleep in or if you "+
                "set the ending time to 10:00 PM, you would not be scheduled for "+
                "any films that start at 10:00 PM "+
                "or later.</p>"+

                "<h4>Film Limit Per Day</h4>"+
                "<p>Indicate the maximum number of films to schedule each day. "+
                "&nbsp;Enter a 0 (zero) if you do not want&nbsp;any&nbsp;"+
                "film limit when scheduling. &nbsp;Note: Specifying a limit "+
                "may result in a schedule that does not use all of your tickets. "+
                "&nbsp;For example, if you are at festival for 5 "+
                "days&nbsp;with&nbsp;a&nbsp;package&nbsp;of&nbsp;20&nbsp;"+
                "tickets and limit films per day to 3, only 15 films "+
                "will be scheduled.</p>"+

                "<h4>Red Eye Rule</h4>"+
                "<p>  Setting this to No would not allow "+
                "the first showing of a day to be scheduled if the "+
                "previous night had a late showing. "+
                "Hard core filmgoers who don&rsquo;t need sleep can " +
                "set this to Yes.</p>"+

                "<h4>Extra&nbsp;Time Between Films</h4>"+
                "<p>Travel time between theaters (see table below)" +
                " is already accounted "+
                "for in your schedule. &nbsp;This is the number of extra "+
                "minutes to leave before and after a showing if you want "+
                "extra time between films. &nbsp;This is useful if you "+
                "want to add in time to listen to the Q&amp;A " +
                "sessions after a film.  No extra time if you are at " +
                "the same theater for the next showing.</p>"+
                
                "<p><TABLE border=1 class=sched-Film-FontLarge>" +
                "<TR><TH><b>Travel Times</b></TH>" +
                "<TH><i>Park City</i></TH>" +
                "<TH><i>Salt Lake City</i></TH>" +
                "<TH><i>Sundance Village</i></TH>" +
                "<TH><i>Odgen</i></TH>" +
                "</TR>" +
                "<TH>Park City</TH>" +
                "<TD class=film-List-notSelected>+45 min</TD>"+
                "<TD>+60 min</TD>"+
                "<TD>+60 min</TD>"+
                "<TD>+90 min</TD></TR>"+
                "<TH><i>Salt Lake City</i></TH>" +
                "<TD>+60 min</TD>"+
                "<TD class=film-List-notSelected>+0 min</TD>"+
                "<TD>+60 min</TD>"+
                "<TD>+60 min</TD></TR>"+
                "<TH><i>Sundance Village</i></TH>" +
                "<TD>+60 min</TD>"+
                "<TD>+60 min</TD>"+
                "<TD class=film-List-notSelected>+0 min</TD>"+
                "<TD>+90 min</TD></TR>"+
                "<TH><i>Odgen</i></TH>" +
                "<TD>+90 min</TD>"+
                "<TD>+60 min</TD>"+
                "<TD>+90 min</TD>"+
                "<TD class=film-List-notSelected>+0 min</TD></TR></TABLE>"+

                "<h4>Meal Break</h4>"+
                "<p>  Set this to Yes if you want to make sure "+
                "there is room in your schedule to take a "+
                "meal break each day. For the most flexibility, "+
                "set a wide time range. For example, a "+
                "90 minute meal break sometime between 4 PM and 8 PM.</p>" +
                "<p>If you want to reserve time at a specific time, create " +
                "an appointment (from the My Schedule screen)."+

                "<h4>Number of Tickets to Schedule for each show</h4>"+
                "<p>  This is the number of tickets to use for each " +
                "show on the schedule."+ 
                "The system will schedule as many shows as you have tickets. "+
                "So if you have a package of "+
                "20 tickets, and set this value at 1, "+
                "you will have 20 shows scheduled. If you want to"+ 
                "share your package with a friend, set this value at 2 "+
                "and only 10 shows will be scheduled.<br>"+
                "</p>"+

                "<h4>Theaters to Schedule</h4>"+
                "<p>Check the theaters that you want to be included "+
                "when your schedule is created. &nbsp;For example, "+
                "if you just want to stay within the Park City, "+
                "the&nbsp;Park&nbsp;City&nbsp;Theaters should "+
                "be the ony box checked.</p>",
                true); 
        
        return helpText;
    }
    
    /**
     * Create help text for reservation screen.
     * @return HTML returns HTML help text.
     */
    private HTML reserve()
    {
        HTML helpText = new HTML(
                "<h3>Reservation List Help </h3>"+
                "<p>This list will allow you to reserve the tickets for the "+
                "specific showings on your schedule."+
                "<br><br>"+
                "</p>"+
                "<h4>The List </h4>"+
                "<p>This list shows the films on your personalized schedule.  "+
                "It is in order by your ranking so"+
                "that you can get the tickets for the films you want to see the "+
                "most first.  If a showing is sold"+
                "out, you can reschedule.  Rescheduling will preserve "+
                "the tickets you have already ordered (or"+
                "manually locked) and create the next best schedule for you.</p>"+
                "<p>Note that if you have already reserved tickets "+
                "to a showing, it can later indicate if this"+
                "showing is sold out (Congratulations!  You got "+
                "tickets to a popular showing!).</p>"+
                "<h4>Reserve</h4>"+
                "<p>Click on the Reserve link to reserve tickets for "+
                "the specific showing.</p>"+
                "<h4>Release</h4>"+
                "<p>If for some reason you need to \"unreserve\" tickets"+
                "(if you made a mistake), clicking on the"+
                "release link will allow you to release the previously "+
                "reserved tickets.</p>",
                true); 
        
        return helpText;
    }
}
