package com.react.reactmultiplayergame.helper;

/**
 * Created by gan on 6/5/17.
 */

// POJO class that is used in custom game list view to persist the state of checkbox checked
    // Becos listview constantly recycle view, we cannot change the checkbox to check/uncheck at the view level,
    // as it will reassign the checked checkbox to the wrong view when recycling.
    // hence we need this class to store the state of each list item, whether they r checked or unchecked
public class CustomGameListViewModel {

    // string name is the name to be displayed in the listView.
    // int selected is whether this mode is selected, with value >0 represent true. We dont want to use boolean so as not to store in sharedpreference twice, since
    // sequenceNumber require an int from sharedpreference too
    // sequenceNumber is the number order displayed to the user when randomize order is off
    private String name;
    private int selected;
    private int sequenceNumber;

    public CustomGameListViewModel(String name) {
        this.name = name;
        selected = 0; // first initialization all false, then later will change accordingly to sharedpreference
        sequenceNumber = 0; // first initialize all to 0
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int isSelected() {
        return selected;
    }

    public void setSelected(int selected) {
        this.selected = selected;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

}