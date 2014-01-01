package me.taks.nr;

import java.util.Properties;

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

public class DataReader implements ExceptionListener, Runnable {

	private Reports reports;
	private Properties props;
	
	public DataReader(Reports reports, Properties props) {
		this.reports = reports;
		this.props = props;
	}
	public long longOr0(String in) {
		return longOrDefault(in, 0);
	}
	public long longOrDefault(String in, long defaultValue) {
		try { 
			return Long.parseLong(in); 
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	protected void handleReportMessage(String message) {
		JSONArray array = new JSONArray(message);
		for (int j=array.length()-1; j>=0; j--) try {
			JSONObject o = array.getJSONObject(j);
			o = o.getJSONObject("body");
			Report r = new Report(reports);
			if (o.has("next_report_stanox")) {
				r.setNextStanox(o.getString("next_report_stanox"));
			}
			if (o.has("direction_ind")) {
				String d = o.getString("direction_ind");
				r.setDirection("DOWN".equals(d) ? Dir.DOWN : "UP".equals(d) ? Dir.UP : Dir.NONE);
			}
			if (o.has("event_type")) {
				String e = o.getString("event_type");
				r.setEvent("ARRIVAL".equals(e) ? Event.ARRIVAL :
							"DEPARTURE".equals(e) ? Event.DEPARTURE:
							"PASS".equals(e) ? Event.PASS:
								Event.NONE
				);
			}
			r.setTimes(o.has("planned_timestamp") ? longOr0(o.getString("planned_timestamp")) : 0, 
						o.has("actual_timestamp") ? longOr0(o.getString("actual_timestamp")) : 0
			);
			if (o.has("train_id")) {
				r.setTrainId(o.getString("train_id"));
			}
			if (o.has("loc_stanox")) {
				r.setLocationStanox(o.getString("loc_stanox"));
			}
			
			r.ready();
			
			System.out.println(new ReportViewer(r).getSummary());
		} catch (Exception e) { 
			System.err.println("Error "+e.getMessage()+array.getJSONObject(j));
			e.printStackTrace();
		}
	}
	
    public void run() {
    	Connection conn;
    	Session session;
    	MessageConsumer consumer;
    	
        try {

        	conn = new ActiveMQConnectionFactory(
        					props.getProperty("nrUsername"), 
							props.getProperty("nrPassword"), 
							props.getProperty("nrURI", "tcp://datafeeds.networkrail.co.uk:61619")
        			).createConnection();
        	
            conn.start();

            conn.setExceptionListener(this);

            session = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);
            consumer = session.createConsumer(session.createTopic("TRAIN_MVT_ALL_TOC"));

for (int i=0; i<100000; i++) {
            
            Message message = consumer.receive(10000); // Wait for a message

            if (message instanceof TextMessage) {
                handleReportMessage(((TextMessage) message).getText());
            } else {
                System.out.println("Received: " + message);
            }
}
            consumer.close();
            session.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Caught: " + e);
            e.printStackTrace();
        }
    }

    public synchronized void onException(JMSException ex) {
        System.out.println("JMS Exception occured.  Shutting down client.");
    }

}
