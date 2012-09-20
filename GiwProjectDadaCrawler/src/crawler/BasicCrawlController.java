
package crawler;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class BasicCrawlController {
	
		public static String site = "";

		public void startCrawler(String site, int numberOfCrawlers, int depth, int maxPages) throws Exception {
			BasicCrawlController.site = site;
			String crawlStorageFolder = site;

			CrawlConfig config = new CrawlConfig();

			config.setCrawlStorageFolder(crawlStorageFolder);
			config.setPolitenessDelay(1);
			config.setMaxDepthOfCrawling(depth);
			config.setMaxPagesToFetch(maxPages);

			/*
			 * Do you need to set a proxy? If so, you can use:
			 * config.setProxyHost("proxyserver.example.com");
			 * config.setProxyPort(8080);
			 * 
			 * If your proxy also needs authentication:
			 * config.setProxyUsername(username); config.getProxyPassword(password);
			 */

			/*
			 * This config parameter can be used to set your crawl to be resumable
			 * (meaning that you can resume the crawl from a previously
			 * interrupted/crashed crawl). Note: if you enable resuming feature and
			 * want to start a fresh crawl, you need to delete the contents of
			 * rootFolder manually.
			 */
			config.setResumableCrawling(true);

			PageFetcher pageFetcher = new PageFetcher(config);
			RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
			RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
			CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

			controller.addSeed("http://"+BasicCrawlController.site+"/");
			controller.start(BasicCrawler.class, numberOfCrawlers);
		}
}