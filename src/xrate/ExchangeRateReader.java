package xrate;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;



/**
 * Provide access to basic currency exchange rate services.
 * 
 * @author PUT YOUR TEAM NAME HERE
 */
public class ExchangeRateReader {


    public URL url;
    public String baseUrlString;
    public InputStream xmlStream;
    public static void main(String[] args){
        String NICS_DUMMY_DATA_URL = "http://www.morris.umn.edu/~mcphee/ExchangeRateData/";
        ExchangeRateReader xrReader = new ExchangeRateReader(NICS_DUMMY_DATA_URL);
        System.out.println(xrReader.getExchangeRate("GBP", 2009, 11, 12));
        //System.out.println(xrReader.getExchangeRate("PLN","USD",2009,11,12));

    }

    /**
     * Construct an exchange rate reader using the given base URL. All requests
     * will then be relative to that URL. If, for example, your source is Xavier
     * Finance, the base URL is http://api.finance.xaviermedia.com/api/ Rates
     * for specific days will be constructed from that URL by appending the
     * year, month, and day; the URL for 25 June 2010, for example, would be
     * http://api.finance.xaviermedia.com/api/2010/06/25.xml
     * 
     * @param baseURL
     *            the base URL for requests
     */
    public ExchangeRateReader(String baseURL) {

            baseUrlString = baseURL;
    }

    /**
     * Get the exchange rate for the specified currency against the base
     * currency (the Euro) on the specified date.
     * 
     * @param currencyCode
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(String currencyCode, int year, int month, int day) {
        String monthStr = ""+month;
        String dayStr = ""+day;
        String yearStr = ""+year;
        if(month<10){
            monthStr = "0"+month;
        }
        if(day<10){
            dayStr = "0"+day;
        }

        String getExchangeURLString = baseUrlString +yearStr +"/"+monthStr+"/"+dayStr+".xml";
        System.out.println(getExchangeURLString);
        float toReturn = 0;
        try {
            this.url = new URL(getExchangeURLString);
            this.xmlStream = url.openStream();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(getExchangeURLString).openStream());
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getElementsByTagName("fx");
            Node CurCurrency =  nodeList.item(0);
            for(int i = 0; i < nodeList.getLength(); i++){
                CurCurrency = nodeList.item(i);
                // check for matching currency code by comparing the child node of current node (currency code),
                // with the currencyCode argument.
                NodeList children = CurCurrency.getChildNodes();
                //System.out.println(CurCurrency);

                if(children.item(1).getTextContent().equals(currencyCode)) {

                    toReturn = Float.parseFloat(children.item(3).getTextContent());
                    //System.out.println(toReturn);
                    break;
                }
            }
        }
        catch(MalformedURLException e){
            e.printStackTrace();
        }
        catch(IOException io){
            io.printStackTrace();
        }
        catch(ParserConfigurationException pe){
            pe.printStackTrace();
        }
        catch(SAXException SAX){
            SAX.printStackTrace();
        }
        //String url = "http://api.finance.xaviermedia.com/api/" + year + "/"+month+"/"+day+".xml";

        //throw new UnsupportedOperationException();
        return toReturn;
    }

    /**
     * Get the exchange rate of the first specified currency against the second
     * on the specified date.
     * 
     * @param fromCurrency
     *
     * @param toCurrency
     *            the currency code for the desired currency
     * @param year
     *            the year as a four digit integer
     * @param month
     *            the month as an integer (1=Jan, 12=Dec)
     * @param day
     *            the day of the month as an integer
     * @return the desired exchange rate
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public float getExchangeRate(String fromCurrency, String toCurrency, int year, int month, int day) throws ParserConfigurationException,SAXException {

        float start = this.getExchangeRate(fromCurrency, year, month, day);
        float end = this.getExchangeRate(toCurrency, year, month, day);
        if(end<=0.0001){
            return start;
        }
        
        return start/end;
        }

}