package me.taks.nr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.Scanner;

import me.taks.json.JSONArray;
import me.taks.json.JSONException;
import me.taks.json.JSONObject;
import me.taks.json.JSONTokener;

public class CorpusImporter {
	private Locations locations;
	
	public CorpusImporter(Locations locations) {
		this.locations = locations;
	}

	public String jsonToEditedCSV(Reader in) {
		StringBuffer out = new StringBuffer();
		JSONTokener tokener = new JSONTokener(in);
		JSONObject json = new JSONObject(tokener);
		JSONArray array = json.getJSONArray("TIPLOCDATA");
		for (int i=array.length()-1; i>=0; i--) {
			try {
				JSONObject data = array.getJSONObject(i);
				
				if (data.getString("STANOX").trim().equals("")) continue;
				
				out.append( data.getString("STANOX") + ","
						+ data.getString("TIPLOC") + ","
						+ data.getString("3ALPHA") + ","
						+ data.getString("NLCDESC16") + ","
						+ data.getString("NLCDESC")
						+ "\n"
				);
			} catch (JSONException je) {
				System.out.println("Failed to process entry "+i);
			}
		}
		return out.toString();
	}
	
	public void importFromCSV(String data) {
		for (String line : data.split("\n")) {
			String[] row = line.split(",");
			locations.addCorpusItem(row[0], row[1], row[2], row[3], row[4]); 
		}
	}
	
	public void process(Reader in) throws IOException {
		File csv = new File("../nrdata/corpus.csv");
		String csvData;
		
		if (csv.exists()) {
			csvData = new Scanner(csv).useDelimiter("\\A").next();
		} else {
			csvData = jsonToEditedCSV(in);
			new BufferedWriter(new FileWriter(csv)).write(csvData);
		}
		importFromCSV(csvData);
	}

	public void process(File in) throws IOException {
		process(new FileReader(in));
	}

	public void process(String filename) throws IOException {
		process(new FileReader(filename));
	}
}
