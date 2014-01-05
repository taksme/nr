package me.taks.nr;

import java.util.Properties;

public class TestDataReader extends DataReader {

	public TestDataReader(Reports reports, Properties props) {
		super(reports, props);
	}
	
    public void run() {
    	String[] messages = {
        		"[{\"body\":{\"next_report_stanox\":\"67195\",\"loc_stanox\":\"67116\"" +
        	    		",\"train_id\":\"121C131133\",\"direction_ind\":\"UP\"" +
        	    		",\"event_type\":\"ARRIVAL\"" +
        	    		",\"planned_timestamp\":\"600000\",\"actual_timestamp\":\"675000\"}}]",
        		"[{\"body\":{\"next_report_stanox\":\"67195\",\"loc_stanox\":\"67116\"" +
        	    		",\"train_id\":\"135G456665\",\"direction_ind\":\"\"" +
        	    		",\"event_type\":\"\"" +
        	    		",\"planned_timestamp\":\"\",\"actual_timestamp\":\"\"}}]",
        		"[{\"body\":{\"next_report_stanox\":\"67195\",\"loc_stanox\":\"67116\"" +
        	    		",\"train_id\":\"135X456665\",\"direction_ind\":\"\"" +
        	    		",\"event_type\":\"\"" +
        	    		",\"planned_timestamp\":\"\",\"actual_timestamp\":\"\"}}]",
    	};
    	for (int i=0; i<10000; i++) {
    		try { Thread.sleep(10000); } catch (InterruptedException ie) {}
    		handleReportMessage(messages[i%messages.length]);
    	}
    }
}
