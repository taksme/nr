package me.taks.nr;

import java.util.Properties;
import java.util.Random;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.jms.TextMessage;

import me.taks.json.JSONArray;
import me.taks.json.JSONObject;
import me.taks.nr.Report.Dir;
import me.taks.nr.Report.Event;

import org.apache.activemq.ActiveMQConnectionFactory;

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
    		try { Thread.sleep(5000); } catch (InterruptedException ie) {}
    		handleReportMessage(messages[i%messages.length]);
    	}
    }
}
