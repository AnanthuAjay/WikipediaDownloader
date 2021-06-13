package tech.codingclub.utility;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

public class WikipediaDownloader implements  Runnable{
    private  String keyword;
    private  WikipediaDownloader(){

    }
    public WikipediaDownloader(String keyword){
        this.keyword = keyword;
    }
    public void run(){

        //1.Get clean keyword
        //2.Get url for wikipedia
        //3.Make a GET request to wikipedia
        //4.Parsing the useful results using json
        //5.Showing results!

        if(this.keyword==null || this.keyword.length()==0){
           return ;
        }
        //STEP 1
        this.keyword = this.keyword.trim().replaceAll("[ ]+","_");
        //STEP 2
        String wikiUrl= getWikipediaUrlForQuery(this.keyword);
        String response="";
        String imageURL = null;

        try {
            //STEP 3
            String wikipediaResponseHTML= HttpURLConnectionExample.sendGet(wikiUrl);
            System.out.println(wikipediaResponseHTML);
            //STEP 4
            Document document = Jsoup.parse(wikipediaResponseHTML,"https://en.wikipedia.org");

            Elements childElements =document.body().select(".mw-parser-output >*");

            int state=0;

            for(Element childElement: childElements){
                if (state==0){
                    if (childElement.tagName().equals("table")){
                        state=1;
                    }
                }else if (state==1){
                    if (childElement.tagName().equals("p")){
                        state=2;
                        response= childElements.text();
                        break;
                    }
                }
                System.out.println(childElement.tagName());
            }
            try {
                imageURL = document.body().select(".infobox img").get(0).attr("src");
            }catch (Exception ex){

            }

            if(imageURL.startsWith("//")){
                imageURL = "https:"+imageURL;
            }
            WikiResult wikiResult = new WikiResult(this.keyword,response, imageURL);
            // PUSH RESULT INTO DATABASE
            Gson gson= new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(wikiResult);
            System.out.println(json);



        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    private String getWikipediaUrlForQuery(String cleanKeyword) {
        return "https://en.wikipedia.org/wiki/"+cleanKeyword;
    }

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager(20);

        String arr[]={"India","United States"};

        for (String keyword: arr){
            WikipediaDownloader wikipediaDownloader = new WikipediaDownloader(keyword);
            taskManager.waitTillQueueIsFreeAndAddTask(wikipediaDownloader);
        }


    }

}
