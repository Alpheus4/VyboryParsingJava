import lrapi.lr;

import java.io.*;

import java.util.regex.*;
import java.lang.*;

import java.util.ArrayList;
	
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Actions
{

	public int init() throws Throwable {
		return 0;
	}//end of init


	public int action() throws Throwable {
		
		String url = "http://www.vybory.izbirkom.ru/region/izbirkom?action=show&root_a=1000001&vrn=100100084849062&region=0&global=true&type=0&prver=0&pronetvd=null";
        Document doc = Jsoup.connect(url).get();
       
        Elements links = doc.select("option[value~=(.*?)]"); //получение всех ссылок регионов
        
        //Массив из ссылок по регионам
        ArrayList<String> arrayList = new ArrayList<>();
        
        for (Element link : links)
        {
        	
        	String link1 = String.valueOf(link).replace("amp;",""); 
            
            Pattern pattern = Pattern.compile("\"([^\"]*)\"");
            Matcher matcher = pattern.matcher(String.valueOf(link1));
            
            if (matcher.find())
			{
            	arrayList.add(matcher.group(1));
			}
        }
         
        //Получение нижестоящих изберательных комиссий 
        
        ArrayList<String> nicArrayList = new ArrayList<>();
        
        for(String nicLink : arrayList) 
        {
        	
  			url = nicLink;
  			doc = Jsoup.connect(url).get();
  			
  			Elements nicLinks = doc.select("option[value~=(.*?)]"); //получение всех ссылок нижестоящих изберательных комиссий
  			
  			for (Element link : nicLinks) 
  			{
     
	            String nicLink1 = String.valueOf(link).replace("amp;","");
	            String nicLink2 = nicLink1.replace("region=0","region=43"); //Меняем параметр с 0 на 43 чтобы сразу получать ссылки для парсинга УИКов
	            
	            Pattern pattern = Pattern.compile("\"([^\"]*)\"");
	            Matcher matcher = pattern.matcher(String.valueOf(nicLink2));
				if (matcher.find()) 
				{
					nicArrayList.add(matcher.group(1));
				    
				}
           
        	}
		}
        
        FileWriter writer = new FileWriter("vybory.txt");
        writer.write("Регион, Нижестоящая ИК, УИК, Бабурин, Грудинин, Жириновский, Путин, Собчак, Сурайкин, Титов, Явлинский" + "\n");
        //Получение УИКов
        
        for (String uikLink : nicArrayList)
        {
        	url = uikLink;
  			doc = Jsoup.connect(url).get();
  			
  			Elements uikLinks = doc.select("option[value~=(.*?)]"); //получение всех ссылок УИКов
  			
  			for (Element link : uikLinks)
  				
  			{
  				String uikLink1 = String.valueOf(link).replace("amp;","");
  				
  				Pattern pattern = Pattern.compile("\"([^\"]*)\"");
	            Matcher matcher = pattern.matcher(String.valueOf(uikLink1));
				if (matcher.find()) 
				{
					url = matcher.group(1);
					doc = Jsoup.connect(url).get();
					
					Element lastLink = doc.select("a").last();
					String lastLink1 = String.valueOf(lastLink).replace("amp;","");
					
					//ССЫЛКА НА ТАБЛИЦУ
					Pattern pattern1 = Pattern.compile("\"([^\"]*)\"");
	           	 	Matcher matcher1 = pattern1.matcher(String.valueOf(lastLink1));
	           	 	
	           	 	if (matcher1.find())
	           	 	{
	           	 		url = matcher1.group(1);
	           	 		doc = Jsoup.connect(url).get();
	           	 		
	           	 		Elements regNicUik = doc.select("[href~=(.*region=43)]");
				        String regNicUikText = regNicUik.text();
				
				        Elements table = doc.select("html");
				        String text = table.outerHtml();
				        
				        Pattern pattern2 = Pattern.compile("(?:ч|а)<.td>\\s+<td style=\"color:black\" align=\"right\"><b>(.*?)<.b>");
				        Matcher matcher2 = pattern2.matcher(text);
				        
				        String ttt = "";
				       
				        while (matcher2.find())
				        {
				        	ttt += " " + matcher2.group(1) + " ";
				        	
				        }
				        
				        System.out.println("In while " + regNicUikText + " " + ttt + "\n");
				        writer.write(regNicUikText + " " + ttt + "\n");			       
	           	 	}
				}
  			}
        }
        writer.close(); 
    
		return 0;
	}//end of action


	public int end() throws Throwable {
		return 0;
	}//end of end

}
