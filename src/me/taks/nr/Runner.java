package me.taks.nr;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import me.taks.nr.location.CorpusImporter;
import me.taks.nr.location.Locations;
import me.taks.nr.location.NaptanImporter;
import me.taks.nr.subs.Subscriptions;
import me.taks.nr.webserver.WebsocketServer;

public class Runner {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws Exception {
		Locations locations = new Locations();
		Reports reports = new Reports(locations);
		Properties props = new Properties();
		props.load(new FileReader("../nrdata/nr.props"));
		Subscriptions subs = new Subscriptions(reports, props);
		new CorpusImporter(locations).process(args[0]);
		new NaptanImporter(locations).process(args[1]);
//		for (Locations.Location l: locations)
//			System.out.println(l.getAtco()+", "+l.getDescription()+", "+l.getEasting()+", "+l.getNorthing()+", "+l.getShortDescription()+", "+l.getStanox()+", "+l.getTiploc()+", "+l.getTla());
		new Thread(null, new DataReader(reports, props), "data reader").start();
		//new Thread(null, new TestDataReader(reports, props), "data reader").start();
		new WebsocketServer(reports, locations, props, subs).run();
	}

}
