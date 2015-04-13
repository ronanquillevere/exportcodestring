package com.github.ronanquillevere.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;

public class exportcodestring implements EntryPoint
{

    public void onModuleLoad()
    {
        Export export = GWT.create(Export.class);

        Window.alert("Method code = " + export.getMethodCode());
    }
}
