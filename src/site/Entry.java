package site;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author sku202
 */
public class Entry {
	public static void main(String... ar) {
		if (ar.length <= 0) {
			System.out.println("Please provide absolute path of text file containing site urls.....");
			return;
		}
		try {
			List<String> list = FileUtils.readLines(new File(ar[0]), "UTF-8");
			for (String siteName : list) {
				System.out.println("Running For: " + siteName);
				if (siteName.toLowerCase().contains("-uat")) {
					Site BWSsite = new SiteBuilder(siteName).setUsername("wlnonproduser").setPassword("Pass@word11")
							.setTimeout(120000).build();
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go("UAT");
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
					BWSsite = new SiteBuilder(siteName).setUsername("wlnonproduser").setPassword("Pass@word11")
							.setUserAgent(
									"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
							.setTimeout(120000).setViewPortHeight(667).setViewPortWidth(375).build();
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go("UAT-Mobile");
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
				} else {
					Site BWSsite = new SiteBuilder(siteName).setTimeout(120000).build();
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go("PROD");
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
					BWSsite = new SiteBuilder(siteName)
							.setUserAgent(
									"Mozilla/5.0 (iPhone; CPU iPhone OS 9_1 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Version/9.0 Mobile/13B143 Safari/601.1")
							.setTimeout(120000).setViewPortHeight(667).setViewPortWidth(375).build();
					if (BWSsite.isRunning()) {
						new Finder(BWSsite).go("PROD-Mobile");
					} else {
						System.out.println("Site is not running. Giving " + BWSsite.getStatusCode()
								+ " status code. Please verify");
					}
				}
				System.out.println("====================================================================");
			}
		} catch (Exception e) {
			Logger.getLogger(Finder.class.getName()).log(Level.SEVERE, null, e);
		}
	}
}
