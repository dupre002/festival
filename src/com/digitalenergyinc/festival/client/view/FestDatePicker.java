package com.digitalenergyinc.festival.client.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import com.digitalenergyinc.fest.client.Constants;
import com.digitalenergyinc.fest.client.DataChangeListener;
import com.digitalenergyinc.fest.client.model.ShowingRPC;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.event.ChangeEvent;
import com.google.gwt.widgetideas.client.event.ChangeHandler;
import com.google.gwt.widgetideas.datepicker.client.DatePicker;

/**
 * This is a widget that combines a list box and date picker for festival dates.
 * 
 * <p>Title: Film Festival calendar/guide.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class FestDatePicker extends Composite implements ClickListener, 
ChangeListener
{
    private HorizontalPanel mainHP = 
        new HorizontalPanel();         // Main panel for this widget
    private String pickerName;         // name (identifier) of this widget
    private int selectedDayIndex = 0;  // day index of selected day
    private DatePicker myPicker = new DatePicker();  // date picker
    private ListBox lbDate = new ListBox();          // drop down for date
    
    private Image pickerPic = new Image();           // picker button
    private String pickerURL = "images/calendar_icon2.gif";  // URL for picker
    private Date festStart = null;                   // festival start date
    private Date festEnd = null;                     // festival end date
    private final MyPopup popup = new MyPopup();     // popup panel for calendar
    private String defaultTime;                      // default time (all zeros)
    private ArrayList<DataChangeListener> dataChangeListeners;   // list of listeners to this object

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
    
    /**
     * Constructor used to set up widget.
     * @param String inToolTip the tool tip for this widget.
     * @param String Name of date for this widget.
     * @param String default date to use in picker/text box (YYYY-MM-DD).
     */
    public FestDatePicker(String inToolTip, String inName, String inDefault)
    {
        // NOTE must use deprecated methods until GWT supports Calendar.
        initWidget(mainHP);
        dataChangeListeners = new ArrayList<DataChangeListener>();
        DateTimeFormat format = DateTimeFormat.getFormat("yyyy-MM-dd hh:mm:ss"); 
        
        // set up defaults in picker
        pickerName = inName;
        myPicker.setTitle(inToolTip);

        myPicker.addChangeHandler(new datePickerListener());
        defaultTime = " 00:00:00";
        
        Date myStartDate = null;
        try
        {             
            myStartDate = format.parse(inDefault+defaultTime);
            festStart = format.parse(Constants.FESTIVAL_START_DATE+defaultTime);
            festEnd = format.parse(Constants.FESTIVAL_END_DATE+defaultTime);
        }
        catch (Exception e)
        {
            myStartDate = format.parse(Constants.FESTIVAL_START_DATE+defaultTime);
            System.out.println("Error in date! Use Fest Start Date");
        }
        
        // set a default date
        myPicker.setSelectedDate(myStartDate, false);
        
        // set highlighting for festival week
        Vector<Date> festDatesList = new Vector<Date>(Constants.MAX_DAYS+2);        
        for (int i=0; i<Constants.MAX_DAYS; i++)
        {
            Date tempDate = new Date();
            tempDate.setDate(festStart.getDate()+i);
            tempDate.setMonth(festStart.getMonth());
            tempDate.setYear(festStart.getYear());

            festDatesList.add(tempDate);            
        }
        myPicker.addVisibleDateStyles(festDatesList, "festival-cells");
        
        // set up listbox
        lbDate.setTitle(inToolTip);
        
        // convert 2007-01-20 to 01/20/2007
        String strTemp = Constants.FESTIVAL_START_DATE;
        int day = Integer.parseInt(strTemp.substring(8, 10));
        String strMonth = strTemp.substring(5, 7);
        String strYear = strTemp.substring(0, 4);
        String strDay = strTemp.substring(8, 10);
        String strStartDay = strMonth+"/"+strDay+"/"+ strYear;
        
        // put 11 days in the drop down boxes
        for (int loopCtr=0; loopCtr<11; loopCtr++)
        {
            strDay = String.valueOf(day++);
              if (strDay.length() == 1)
                  strDay = "0" + strDay;
            strStartDay = strMonth+"/"+strDay+"/"+ strYear;
            String tempStr = strYear +"-"+ strMonth +"-"+ strDay;
            
            lbDate.addItem(strStartDay);
            
            // set list box default
            if (tempStr.equalsIgnoreCase(inDefault))
            {
                selectedDayIndex = loopCtr;
            }
        }
        
        // make drop down list and set initial value
        lbDate.setVisibleItemCount(1);
        lbDate.setSelectedIndex(selectedDayIndex);
        lbDate.addChangeListener(this);
        
        // set up date picker icon
        pickerPic.setStyleName("film_Image");
        pickerPic.setUrl(pickerURL);
        pickerPic.setTitle("Select date from calendar");
        pickerPic.addClickListener(this);        
        
        // add it to the panel
        mainHP.add(lbDate);
        mainHP.add(pickerPic);
    }
    
    /**
     * Sets the default date in the calendar to match incoming drop down index.
     * @param int the index of the list box to use as default date.
     */
    private void setDefaultDate(int inIndex)
    {
        DateTimeFormat format = DateTimeFormat.getFormat("MM/dd/yyyy hh:mm:ss");
        Date myStartDate = null;
        String myDate = lbDate.getItemText(inIndex);
        try
        {             
            myStartDate = format.parse(myDate+defaultTime);            
        }
        catch (Exception e)
        {
            myStartDate = format.parse(Constants.FESTIVAL_START_DATE+defaultTime);
            System.out.println("Error in default date! Use Fest Start Date "+myDate);
        }
        
        // set a default date
        myPicker.setSelectedDate(myStartDate, false);
    }
    
    /**
     * Handles when controls are clicked.
     * @param Widget the incoming widget clicked.
     */
    public void onClick(Widget sender)
    {
        // look if date picker icon clicked
        if (sender == pickerPic)
        {
            // make popup for calendar
            
            popup.setWidget(myPicker);
            popup.setWidth("150px");
            
            // Position the popup 1/3rd of the way down and across the screen, and
            // show the popup. Since the position calculation is based on the
            // offsetWidth and offsetHeight of the popup, you have to use the
            // setPopupPositionAndShow(callback) method.
            popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                public void setPosition(int offsetWidth, int offsetHeight) {
                  int left = pickerPic.getAbsoluteLeft();
                  int top = pickerPic.getAbsoluteTop();
                  popup.setPopupPosition(left, top);
                }
            });

            //System.out.println("picker picked!");
        }
    }
    
    /**
     * Handles when date picker is clicked.
     * @param Widget the incoming button clicked.
     */
    public void onChange(Widget sender)
    {
        // if date is changed in list box, save result, reset datepicker default
        if (sender == lbDate)
        {
            System.out.println("date changed! ");
            selectedDayIndex = lbDate.getSelectedIndex();
            setDefaultDate(selectedDayIndex);
            fireDataChangeListeners();
        }
    }
    
    /**
     * Listener class to handle when server activity occurs.
     */
    class datePickerListener implements ChangeHandler<Date>
    {
        /**
         * Handles when date picker is clicked.
         * @param ChangeEvent the event that occurred.
         */
        public void onChange(ChangeEvent event)
        {
            // NOTE: must use deprecated methods until GWT supports Calendar.
            
            // some error checking to make sure it is within festival dates
            Date myDate = myPicker.getHighlightedDate();
            //System.out.println("in Onchange! "+myDate+" "+festStart);
            if (myDate.compareTo(festStart) < 0)
                return;
            
            if (myDate.compareTo(festEnd) > 0)
                return;
            
            // is within festival dates
            int dayPicked = myDate.getDate();
            int myStartDay = Integer.parseInt(Constants.FESTIVAL_START_DATE.substring(8));
            selectedDayIndex = dayPicked - myStartDay;
            lbDate.setSelectedIndex(selectedDayIndex);
            popup.hide();
            fireDataChangeListeners();
        }
    }

    /**
     * Gets the pickerName.
     * @return String the pickerName.
     */
    public String getPickerName() {
        return pickerName;
    }

    /**
     * Sets the pickerName.
     * @param pickerName the pickerName to set.
     */
    public void setPickerName(String pickerName) {
        this.pickerName = pickerName;
    }

    /**
     * Gets the selectedDayIndex.
     * @return int the selectedDayIndex.
     */
    public int getSelectedDayIndex() {
        return selectedDayIndex;
    }

    /**
     * Sets the selectedDayIndex.
     * @param selectedDayIndex the selectedDayIndex to set.
     */
    public void setSelectedDayIndex(int selectedDayIndex) {
        this.selectedDayIndex = selectedDayIndex;
        lbDate.setSelectedIndex(selectedDayIndex);
        setDefaultDate(selectedDayIndex);
    }
    
    /**
     * Adds a listener when server activity occurs.
     * @param ServerListener the listener to communicate when server activity occurs.
     */
    public void addDataChangeListener(DataChangeListener listener)
    {
        dataChangeListeners.add(listener);
    }

    /**
     * Removes a listener when server activity occurs.
     * @param ServerListener the listener to communicate when server activity occurs.
     */
    public void removeDataChangeListener(DataChangeListener listener)
    {
        dataChangeListeners.remove(listener);
    }
    
    /**
     * Notifies listeners that data has changed.
     */
    protected void fireDataChangeListeners()
    {
        for (int loopCtr=0; loopCtr < dataChangeListeners.size(); loopCtr++)
        {
            DataChangeListener myWidget = (DataChangeListener) dataChangeListeners.get(loopCtr);
            myWidget.onDataChange();
        }
    }

    /**
     * Notifies listeners that data was selected - not used for movies.
     * @param ShowingRPC  the item changed or selected (optional).
     */
    protected void fireDataSelectedListeners(ShowingRPC myShow)
    {
        for (int loopCtr=0; loopCtr < dataChangeListeners.size(); loopCtr++)
        {
            DataChangeListener myWidget = (DataChangeListener) dataChangeListeners.get(loopCtr);
            myWidget.onDataSelected(myShow);
        }
    }
}
