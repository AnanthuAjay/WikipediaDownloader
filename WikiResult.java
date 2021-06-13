package tech.codingclub.utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WikiResult {
    private  String query;
    private  String text_result;
    private  String image_url;

    public WikiResult(String query, String text_result, String image_url){
        this.query= query;
        this.text_result=text_result;
        this.image_url=image_url;
    }

    public String getText_result(){return  text_result;}
}