package me.taks.nr.subs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import me.taks.json.JSONArray;
import me.taks.json.JSONObject;
import me.taks.json.JSONTokener;
import me.taks.nr.Report;
import me.taks.nr.Reports;

public class Subscriptions extends ArrayList<Subscription> {
	private Reports reports;
	private Properties props;
	private boolean populating = false;
	
	public Subscriptions(Reports r, Properties props) {
		reports = r;
		this.props = props;
		populateFromFile();
		reports.addListDataListener(new ListDataListener() {
			public void intervalRemoved(ListDataEvent arg0) {}
			public void intervalAdded(ListDataEvent event) {
				System.out.println("trying "+size()+" subscribers. reports size:"+reports.size()+", "+event.getIndex0()+", "+event.getIndex1());
				for (int i=event.getIndex0(); i<=event.getIndex1(); i++) {
					processReport(reports.get(i));
				}
			}
			public void contentsChanged(ListDataEvent arg0) {}
		});
	}
	
	private void populateFromFile() {
		populating = true;
		try {
			JSONArray array = new JSONArray(new JSONTokener(new FileReader("../nrdata/reports.sub")));
			for (int i=array.length()-1; i>=0; i--) {
				add(Subscription.fromJSON(array.getJSONArray(i)));
			}
		} catch (IOException ie) {
			System.out.println("failed to load subscriptions from file");
		}
		populating = false;
	}
	
	private void writeToFile() {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("../nrdata/reports.sub"));
			writer.write("[");
			for (Subscription s: this)
				writer.write(s.toJSONString()+",");
			writer.write("]");
			writer.close();
		} catch (IOException ie) { System.out.println("Failed to write subscription file"); }
	}
	
	public boolean add(Subscription sub) {
		boolean result = super.add(sub);
		if (!populating) writeToFile();
		return result;
	}

	public boolean remove(Subscription sub) {
		boolean result = super.remove(sub);
		if (!populating) writeToFile();
		return result;
	}

	private String sendToAndroid(String payload) {
		System.out.println("sendToAndroid "+payload);
		HttpURLConnection conn = null;  
		try {
			String url = "https://android.googleapis.com/gcm/send";
			conn = (HttpURLConnection)new URL(url).openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setRequestProperty("Authorization", "key="+props.getProperty("nrGoogleApiKey"));
			conn.setDoOutput(true);
			conn.setDoInput(true);
			DataOutputStream wr = new DataOutputStream (conn.getOutputStream ());
			wr.writeBytes (payload);
			wr.flush ();
			wr.close ();
			if (conn.getResponseCode()!=200)
				return Integer.toString(conn.getResponseCode()) + ": " + conn.getResponseMessage();
			//Get Response	
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			return rd.readLine();
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if(conn != null) {
			  conn.disconnect(); 
			}
		}
			
	}
	
	private String sendToAndroid(Subscription sub, Report r) {
		return sendToAndroid(String.format(
				"{\"registration_ids\":[%s],\"data\":{\"report\":%s,\"filter\":%s}}", 
				JSONObject.valueToString(sub.getClientId()),
				JSONObject.valueToString(r.toJSONString()),
				JSONObject.valueToString(sub.getFilter())
		));
	}
	
	public void processReport(Report r) {
		for (Subscription s : Subscriptions.this) {
			if (s.matches(r)) {
				switch (s.getType()) {
				case android:
					String result = sendToAndroid(s, r);
					System.out.println("Google returned: "+result);
				}
			}
		}
	}
}
