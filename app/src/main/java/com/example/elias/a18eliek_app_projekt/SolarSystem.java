package com.example.elias.a18eliek_app_projekt;

import org.json.JSONException;
import org.json.JSONObject;

public class SolarSystem {
    //Member variabels
    private String name;
    private String distance;
    private int radius;
    private String auxdata;
    private String parent;
    private String category;

    //Constructor(s)
    public SolarSystem(String inName, String inDistance, int inRadius, String inParent, String inCategory, String inAuxdata) {
        name = inName;
        distance = inDistance;
        radius = inRadius;
        auxdata = inAuxdata;
        parent = inParent;
        category = inCategory;
    }

    public SolarSystem(int orThrow, int indexOrThrow, int columnIndexOrThrow, String inName) {
        name = inName;
        distance = "";
        radius = -1;
    }

    //Member methods
    public String toString() {
        return name;
    }

    public String info() {
        String str = name;
        str += " is located ";
        str += distance;

        if("Moon".equalsIgnoreCase(category)){
            str += " km away from " + parent + " and has a radius of ";
        } else {
            str += " km away from the sun and has a radius of ";
        }

        str += Integer.toString(radius);
        str += "km. ";
        return str;
    }

    public void setRadius(int newHeight) {
        radius = newHeight;
    }

    public String getDistance() {
        return distance;
    }

    public String getRadius() {
        return String.valueOf(radius);
    }

    public String getAuxdata() {
        return auxdata;
    }

    public static String splitAuxdata(String auxdata, String value) throws JSONException {
        JSONObject json = new JSONObject(auxdata);
        return json.getString(value);
    }

}
