package com.payflipwallet.android.http;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {

    public interface Handler {
        void response(HttpResponse response);
    }

    public final static String GET = "GET";
    public final static String POST = "POST";

    private final String url;
    private final String method;
    private final String parameters;
    private final String authorization;


    public HttpRequest(String url, String method) {
        this(url, method, null, null);
    }
    public HttpRequest(String url, String method, String parameters) {
        this(url, method, parameters, null);
    }

    public HttpRequest(String url, String method, String parameters, String authorization) {
        this.url = url;
        this.method = method;
        this.parameters = parameters;
        this.authorization = authorization;
    }

    public HttpResponse request()  {

        HttpResponse response = new HttpResponse();

        HttpURLConnection con;
        try {
            con = (HttpURLConnection) new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            return response;
        }

        try {

            if(method != null){
                con.setRequestMethod(method);
            }

            if(authorization != null) {
                con.setRequestProperty("Authorization", this.authorization);
            }

            if(parameters != null){
                con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                con.setRequestProperty("charset", "utf-8");
                con.setUseCaches(false);
                con.setDoOutput(true);
                con.setDoInput(true);
                IO.PostData(con,parameters);

            }

            response.code = con.getResponseCode();
            response.body = IO.read(con.getInputStream());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            con.disconnect();
        }
        return response;
    }

}
