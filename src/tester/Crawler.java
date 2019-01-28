package tester;

import java.io.BufferedWriter;
import java.io.Console;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.logging.ConsoleHandler;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

public class Crawler {
	
	static ArrayList<ArrayList<String>> prevUrls = new ArrayList<ArrayList<String>>(); //trasforma in array
	static ArrayList<String> urls = new ArrayList<String>();
	static HashSet<String> visitedUrls = new HashSet<String>();
	static HashSet<String> domains = new HashSet<String>();	
	static BufferedWriter bw ;
	static PrintWriter out;

	public static void main(String[] args) {

		long start = System.currentTimeMillis();

		try {
			bw = new BufferedWriter(new FileWriter("crawler.txt"));
			out = new PrintWriter(bw);
			Document d = Jsoup.connect("http://www.bbc.co.uk/news").get();
			do {
				d = crawl(d);
			}while(d!=null);

			System.out.println("fine");
			System.out.println("durata "+ (System.currentTimeMillis() - start));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static Document crawl(Document doc) throws IOException {

		visitedUrls.add(doc.location());
		
		try {
			Elements newsHeadlines = doc.select("a");
			for (Element headline : newsHeadlines) {
				urls.add(headline.absUrl("href"));	
			}


			Document s = getRandomUrl(urls);			
			urls.clear();
			return s;

		} catch (Exception e) {
			
		}
		
		return null;

	}
	
	private static Document getRandomUrl(ArrayList<String> xs) {
		
		ArrayList<String> xs2 = (ArrayList<String>) xs.clone();
		
		
		while(!xs2.isEmpty()) {
			String x = null;
			Random random = new Random();
			x=xs2.get(random.nextInt(xs2.size()));
			xs2.remove(x);
			try {
			String domain = x.split("/")[2];
			if(!domains.contains(domain) && !domain.contains("facebook") && !domain.contains("google") && !domain.contains("twitter")) {
				Document d = Jsoup.connect(x).get();
				System.out.println("new " + domain);

				if(domain.contains("porn") || domain.contains("xxx") || domain.contains("brazzer") || domain.contains("sex")){
					return null;
				}

				domains.add(domain);
				visitedUrls.add(x);
				

				out.println(domain);
				out.flush();
				
				
				prevUrls.add((ArrayList<String>) urls.clone());
				if(prevUrls.size()>100) {
					prevUrls.remove(0);
				}
				
				
				return d; 
			}
				} catch (Exception e) {
					// TODO: handle exception
				}	
			}
	
		while(!xs.isEmpty()) {	
			String x = null;
			Random random = new Random();
			x=xs.get(random.nextInt(xs.size()));
			xs.remove(x);
			if(!visitedUrls.contains(x) && !x.contains("facebook") && !x.contains("google") && !x.contains("twitter")) {
				try {
					Document d = Jsoup.connect(x).get();	
					System.out.println("visito " + x);
					visitedUrls.add(x);
					return d;
				} catch (Exception e) {
					// TODO: handle exception
				}	
			}
		}
		
		for(int i = prevUrls.size()-1; i>=0; i--) {
			
			ArrayList<String> xs3 = (ArrayList<String>) prevUrls.get(i).clone();
			
			while(!xs3.isEmpty()) {
				String x = null;
				Random random = new Random();
				x=xs3.get(random.nextInt(xs3.size()));
				xs3.remove(x);
				if(!visitedUrls.contains(x) && !x.contains("facebook") && !x.contains("google") && !x.contains("twitter")) {
					try {
						Document d = Jsoup.connect(x).get();	
						System.out.println("visito " + x);
						visitedUrls.add(x);
						return d;		
					} catch (Exception e) {
						// TODO: handle exception
					}	
				}
			}
			
		}
		
		return null;
	}

}
