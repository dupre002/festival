package com.digitalenergyinc.festival.client;

import com.digitalenergyinc.festival.client.Sink.SinkInfo;
import com.digitalenergyinc.festival.client.view.HomePane;
import com.digitalenergyinc.festival.client.view.LogoutPane;
import com.digitalenergyinc.festival.client.view.MoviePane;
import com.digitalenergyinc.festival.client.view.MyOptPane;
import com.digitalenergyinc.festival.client.view.MySchedPane;
import com.digitalenergyinc.festival.client.view.ReservePane;
import com.digitalenergyinc.festival.client.view.SchedPane;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * This is the main entry point for the application.
 * 
 * <p>Title: Film calendar/guide.</p>
 * <p>Description: Film Festival scheduler.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: Digital Energy, Inc.</p>
 * @author Rene Dupre
 * @version 1.0
 */ 
public class festival implements EntryPoint
{
    protected SinkList list = new SinkList();   // menu list
    private SinkInfo curInfo;                   // currently info on selected menu item 
    private Sink curSink;                       // currently selected set of panels
    private HTML description = new HTML();      // description of module shown
    private DockPanel panel = new DockPanel();  // main panel of app
    private DockPanel sinkContainer;            // module panel (based on menu sel)
    
    /**
     * Determines what to show when history has changed.
     * @param String history token to display.
     */
    public void onHistoryChanged(String token) {
        //System.out.print("\nonHistory change "+token);
        // Find the SinkInfo associated with the history context. If one is
        // found, show it (It may not be found, for example, when the user mis-
        // types a URL, or on startup, when the first context will be "").
        SinkInfo info = list.find(token);
        if (info == null) {
            showHome();
            return;
        }
        show(info, false);
    }
    
  /**
   * This is the entry point method.
   */
  public void onModuleLoad() {
        // clear out waiting on html page
        RootPanel.get("loading1").setVisible(false); 
      
        //    Load all the sinks.
        loadSinks();

        // Put the sink list on the left, and add the outer dock panel to the
        // root.
        sinkContainer = new DockPanel();
        sinkContainer.setStyleName("film-Sink");

        VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.add(description);
        vp.add(sinkContainer);
        
        // copyright pane
        HorizontalPanel copyPane = new HorizontalPanel();
        copyPane.setStyleName("film-Copy");
        HTML copy = new HTML("Copyright 2007");
        Hyperlink linkPrivacy = new Hyperlink("Privacy Policy", "Policies#Privacy");
        Hyperlink linkTerms = new Hyperlink("Terms of Service", "Policies#Terms");
        copyPane.add(linkPrivacy);
        copyPane.add(copy);
        copyPane.add(linkTerms);

        description.setStyleName("film-Info");

        panel.add(list, DockPanel.NORTH);
        panel.add(vp, DockPanel.CENTER);
        //panel.add(advPanel, DockPanel.EAST);
        panel.add(copyPane, DockPanel.SOUTH);

        panel.setCellHorizontalAlignment(list, HasAlignment.ALIGN_LEFT);
        panel.setCellWidth(vp, "100%");
        panel.setWidth("100%");
        
        // Setup a history handler 
        final ValueChangeHandler<String> historyHandler = new ValueChangeHandler<String>() {
            public void onValueChange(ValueChangeEvent<String> event) {
             // Find the SinkInfo associated with the history context. If one is
                // found, show it (It may not be found, for example, when the user mis-
                // types a URL, or on startup, when the first context will be "").
                SinkInfo info = list.find(event.getValue());
                if (info == null) {
                    showHome();
                    return;
                }
                show(info, false);
            }
          };
        History.addValueChangeHandler(historyHandler);

        RootPanel.get().add(panel);

        // Show the initial screen.
        String initToken = History.getToken();
        if (initToken.length() > 0) {
            onHistoryChanged(initToken);
        } else {
            showHome();
        }
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
        if (curSink != null) {
            curSink.onHide();
            sinkContainer.remove(curSink);
        }

        // Get the new sink instance, and display its description in the
        // sink list.
        curSink = info.getInstance();
        list.setSinkSelection(info.getName());
        description.setHTML(info.getDescription());

        // If affectHistory is set, create a new item on the history stack. This
        // will ultimately result in onHistoryChanged() being called. It will call
        // show() again, but nothing will happen because it will request the exact
        // same sink we're already showing.
        if (affectHistory) {
            History.newItem(info.getName());
        }

        // Display the new sink.
        sinkContainer.add(curSink, DockPanel.CENTER);
        sinkContainer.setCellWidth(curSink, "100%");
        sinkContainer.setCellHeight(curSink, "100%");
        sinkContainer.setCellVerticalAlignment(curSink, DockPanel.ALIGN_TOP);
        curSink.onShow();
    }

    /**
     * Adds all sinks to the list. Note that this does not create actual instances
     * of all sinks yet (they are created on-demand). This can make a significant
     * difference in startup time.
     */
    protected void loadSinks() {
        list.addSink(HomePane.init());
        list.addSink(MoviePane.init());
        list.addSink(SchedPane.init());
        list.addSink(MySchedPane.init());
        list.addSink(MyOptPane.init());
        list.addSink(ReservePane.init());
        list.addSink(LogoutPane.init());
    }

    /**
     * Show the Home set of panels.
     */
    private void showHome() {
        show(list.find("Home"), false);
    }
}
