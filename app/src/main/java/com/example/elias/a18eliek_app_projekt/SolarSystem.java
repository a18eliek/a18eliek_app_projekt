package com.example.elias.a18eliek_app_projekt;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.NumberFormat;
import java.util.Locale;

public class SolarSystem {
    private String parent;
    //Member variabels
    private String name;
    private String distance;
    private int radius;
    private String auxdata;
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

    //Member methods
    public String toString() {
        return name;
    }

    public String info() {
        String str = name;
        str += " is located ";
        str += getFormattedNumber(distance);

        if("Moon".equalsIgnoreCase(category)){
            str += " km away from " + parent + " and has a radius of ";
        } else {
            str += " km away from the sun and has a radius of ";
        }

        str += getFormattedNumber(Integer.toString(radius));
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

    public String getCategory() {
        return category;
    }

    public String getParent() {
        return parent;
    }

    public static String splitAuxdata(String auxdata, String value) throws JSONException {
        JSONObject json = new JSONObject(auxdata);
        return json.getString(value);
    }


    /**
     * Returnera ett formaterat nummer, speciellt bra för stora siffror.
     * Anväder Locale.GERMAN för att separera med punkter istället för kommatecken.
     * @param number
     * @return string
     */
    public static String getFormattedNumber(String number){
        if(!number.isEmpty()) {
            double val = Double.parseDouble(number);
            return NumberFormat.getNumberInstance(Locale.GERMAN).format(val);
        }else{
            return "0";
        }
    }

}
