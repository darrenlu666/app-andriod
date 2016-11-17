package com.codyy.erpsportal.commons.widgets;

public interface AwareView {

     void setHolder(Object object);

     Object getHolder();

     void setText(String text);

     DisplayListener getDisplayListener();

     void setDisplayListener(DisplayListener listener);
}
