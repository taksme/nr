package me.taks.nr;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import au.com.bytecode.opencsv.CSVReader;

public class NaptanImporter {
	private Locations locations;
	
	public NaptanImporter(Locations locations) {
		this.locations = locations;
	}
	
	public int intOrDefault(String in, int defaultValue) {
		try { 
			return Integer.parseInt(in); 
		} catch (NumberFormatException e) {
			return defaultValue;
		}
		
	}
	
	public void process(Reader in) throws IOException {
		CSVReader reader = new CSVReader(in);
		String[] s;
		while (null!=(s=reader.readNext())) {
			locations.addNaptanItem(s[1], s[3], intOrDefault(s[6],0), intOrDefault(s[7], 0));
		}
		reader.close();
	}

	public void process(File in) throws IOException {
		process(new FileReader(in));
	}

	public void process(String filename) throws IOException {
		process(new FileReader(filename));
	}
}
