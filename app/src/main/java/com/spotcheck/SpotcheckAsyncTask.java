package com.spotcheck;

import android.os.AsyncTask;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.spotcheck.api.spotcheck.Spotcheck;

import java.io.IOException;

/**
 * Handles the connection with the API
 */
public abstract class SpotcheckAsyncTask extends AsyncTask<Void,Void,Boolean>
{
    final String LOCALHOST = "10.0.2.2:8080";
    final String APPSPOT = "spotcheck-3210.appspot.com";

    public Spotcheck spotcheckAPI;
    public String error = "";

    public SpotcheckAsyncTask()
    {
        super();
        initializeAPI();
    }

    public void initializeAPI()
    {
        String rootURL = "https://"+APPSPOT+"/_ah/api/";
        Spotcheck.Builder builder = new Spotcheck.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                .setRootUrl(rootURL)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer()
                {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException
                    {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                });

        spotcheckAPI = builder.build();
    }
}
