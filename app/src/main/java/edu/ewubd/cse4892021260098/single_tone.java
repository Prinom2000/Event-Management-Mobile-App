package edu.ewubd.cse4892021260098;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
public class single_tone {
    private static single_tone instance = new single_tone();
    private single_tone()
    {

    }
    public static single_tone getInstance(){
        return  instance;
    }

    public String milli_to_Date(long milli, String dateformat){
        SimpleDateFormat sdf = new SimpleDateFormat(dateformat);
        Date d= new Date(milli);
        return sdf.format(d);
    }
    public long date_to_milli(String dateString, String dateformat) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(dateformat);
        Date date = formatter.parse(dateString);
        return date.getTime();
    }


}
