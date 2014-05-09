package com.project.fipu;

// This sample uses the Apache http client from HTTP Components (http://hc.apache.org/httpcomponents-client-ga/)
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.URI;
  
public class TestWS {
  
  public static void main(String[] args) {
//	  HttpClient client = HttpClientBuilder.create().build();
//    try
//    {
//        // Specify values for path parameters (shown as {...})
//        URIBuilder builder = new URIBuilder("http://api.flightlookup.com/otatimetable/v1/TimeTable/");
//        // Specify your developer key
//        builder.setParameter("key", "8D7FE248-2333-4E12-89C9-90F633C9F6F4");
//        // Specify values for the following required parameters
//        builder.setParameter("From", "MIL");
//        builder.setParameter("To", "ROM");
//        builder.setParameter("Date", "05/02/14");
//        // Specify values for optional parameters, as needed
//        //builder.setParameter("Sort", "");
//        //builder.setParameter("Airline", "");
//        //builder.setParameter("Connection", "");
//        //builder.setParameter("Count", "");
//        //builder.setParameter("Interline", "");
//        //builder.setParameter("7Day", "");
//        //builder.setParameter("Language", "");
//        //builder.setParameter("Nofilter", "");
//        URI uri = builder.build();
//        HttpGet request = new HttpGet(uri);
//        HttpResponse response = client.execute(request);
//        HttpEntity entity = response.getEntity();
//        if (entity != null) {
//            System.out.println(EntityUtils.toString(entity));
//          
//        }
//    }
//    catch(Exception e)
//    {
//        System.out.println(e.getMessage());
//    }
  }
}