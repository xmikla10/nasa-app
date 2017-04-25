package com.meteoritelandings;

import android.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Peter Miklánek
 *
 * Class represent download action from url
 */
public class HttpHandler
{

    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler()
    {
    }

    /**
     * Method get .json
     * @param reqUrl url
     * @return response
     */
    public String makeServiceCall(String reqUrl)
    {
        String response = null;
        try
        {
            URL url = new URL(reqUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = streamToString(in);

        }
        catch (ProtocolException e)
        {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        }
        catch (Exception e)
        {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return response;
    }

    /**
     * Method convert inputstream to string
     * @param is inputstream
     * @return string (response)
     */
    private String streamToString(InputStream is)
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try
        {
            while ((line = reader.readLine()) != null)
            {
                sb.append(line).append('\n');
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                Log.e(TAG, "IOException: " + e.getMessage());
            }
        }
        return sb.toString();
    }
}