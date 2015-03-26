package com.github.ronanquillevere.client;

import com.github.ronanquillevere.generator.BuildInfo;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.Window;


public class exportcodestring implements EntryPoint {
 
  public void onModuleLoad() {
      BuildInfo buildInfo = GWT.create(BuildInfo.class);
      Window.alert("Build Timestamp = " + buildInfo.getBuildTimestamp());
  }
}
