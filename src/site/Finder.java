/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package site;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpResponse;

import capture.Capture;
import sachin.spider.SpiderConfig;
import sachin.spider.WebSpider;
import sachin.spider.WebURL;

/**
 *
 * @author sku202
 */
public class Finder extends WebSpider {

	final Site site;
	SpiderConfig config;
	private Map<String, LinkInfo> map;
	private Map<String, Object> siteInfo;
	final String unique;

	public Finder(Site site) {
		this.site = site;
		map = new HashMap<>();
		siteInfo = new HashMap<>();
		unique = HelperClass.generateUniqueString();
	}

	@Override
	public boolean shouldVisit(String url) {
		String urlHost = null;
		if (null != url) {
			try {
				urlHost = new URL(url).getHost().replaceAll("www.", "");
			} catch (MalformedURLException ex) {
				Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
				System.out.println("Error in URL: " + url);
			}
		}
		return (url != null && !url.contains("recipefullpage") && !url.contains("?") && (!FILTERS.matcher(url).find())
				&& urlHost.contains(site.getHost().replaceAll("www.", "")));
	}

	@Override
	public void handleLink(WebURL webUrl, HttpResponse response, int statusCode, String statusDescription) {
		if (statusCode > 400 || statusCode == 200) {
			String url = webUrl.getUrl();
			map.put(url, new LinkInfo(url));
			System.out.println("Added: " + url);
		}
	}

	public void go(String type) {
		config = new SpiderConfig(site.getUrl().trim());
		config.setConnectionRequestTimeout(site.getTimeout());
		config.setConnectionTimeout(site.getTimeout());
		config.setSocketTimeout(site.getTimeout());
		config.setTotalSpiders(5);
		config.setAuthenticate(site.hasAuthentication());
		config.setUsername(site.getUsername());
		config.setPassword(site.getPassword());
		config.setUserAgentString(site.getUserAgent());
		try {
			if (!HelperClass.crawlingDataExists(site.getHost())) {
				config.start(this, config);
				HelperClass.saveCrawlingData(map, site.getHost());
			} else {
				map = HelperClass.readCrawlingData(site.getHost());
				HelperClass.deleteCrawlingData(site.getHost());
			}
			Thread.sleep(20000);
			siteInfo.put("unique", unique);
			String dir = System.getProperty("user.dir") + File.separator + "output" + File.separator
					+ config.getHostName() + File.separator + type + File.separator;
			String dir2 = System.getProperty("user.dir") + File.separator + "output" + File.separator
					+ config.getHostName() + File.separator + type + File.separator + unique + File.separator
					+ "screenshots" + File.separator;
			File f = new File(dir);
			f.mkdirs();
			siteInfo.put("siteInfoDirectory", dir);
			siteInfo.put("screenshotDirectory", dir2);
			siteInfo.put("map", map);
			ObjectOutputStream op = new ObjectOutputStream(
					new BufferedOutputStream(new FileOutputStream(new File(dir + File.separator + unique + ".bin"))));
			op.writeObject(siteInfo);
			op.close();
			siteInfo = null;
			config = null;
			capture(dir2, dir);
			HelperClass.writeUrlInfo(dir, map, unique);
		} catch (Exception ex) {
			Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	public void capture(String dir, String dir2) {
		new Capture(dir, site).takeScreen(map, unique, dir2);
	}

	static {
		System.setProperty("phantomjs.binary.path",
				System.getProperty("user.dir") + File.separator + "Resources" + File.separator + "phantomjs.exe");
	}
}
