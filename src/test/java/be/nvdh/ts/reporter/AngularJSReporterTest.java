package be.nvdh.ts.reporter;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import be.nvdh.ts.domain.FetchResult;
import be.nvdh.ts.domain.Prestation;
import be.nvdh.ts.exception.ReportException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

public class AngularJSReporterTest {
	
	@Test
	public void testHtmlReporter() throws IOException, URISyntaxException, ReportException{
		FetchResult fetchResult = new FetchResult(new LocalDate(), new LocalDate(), prestations(), null, null, null);
		AngularJSReporter reporter = new AngularJSReporter();
		reporter.init(config());
		reporter.publish(fetchResult, context());
	}
	
	private Map<String, String> config() {
		Map<String, String> config = new HashMap<String, String>();
		config.put(AngularJSReporter.CONFIG_LOCATION, "c:\\temp\\prdcertotest");
		config.put(AngularJSReporter.CONFIG_AUTOOPEN, "true");
		config.put(AngularJSReporter.CONFIG_TEMPLATE, "c:\\temp\\prdcertotest\\template_nl.html");
		return config;
	}

	private Map<String, String> context() throws JsonProcessingException {
		Map<String, String> context = new HashMap<String, String>();
		context.put(AngularJSReporter.JSON_CONTEXT_KEY, json());
		return context;
	}

	private ArrayList<Prestation> prestations() {
		ArrayList<Prestation> prestations = new ArrayList<Prestation>();
		
		prestations.add(new Prestation(new LocalDate().minusDays(10), 
			   new Duration(1000*60*60*4 + 1000*60*24), 
			   new Duration(1000*60*60*8), 
			   Duration.ZERO, 
			   null, 
			   localTimes(new LocalTime(9,5,23), new LocalTime(12, 05, 20)), 
			   localTimes(new LocalTime(13,5,43), new LocalTime(17, 03, 12)), 
			   "FEE",
			   "Some irregularity", "Some comment"));

		prestations.add(new Prestation(new LocalDate().minusDays(9), 
				new Duration(1000*60*60*6 + 1000*60*14), 
				new Duration(-1000*60*60*8), 
				Duration.ZERO, 
				null, 
				localTimes(new LocalTime(12, 05, 20)), 
				localTimes(new LocalTime(9,5,23), new LocalTime(13,5,43), new LocalTime(17, 03, 12)), 
				"",
				"", "Some other comment"));

		Prestation prestation = new Prestation(new LocalDate().minusDays(9), 
				new Duration(1000*60*60*6 + 1000*60*14), 
				new Duration(1000*60*60*8), 
				Duration.ZERO, 
				new Duration(1000*60*60*8), 
				localTimes(new LocalTime(12, 05, 20)), 
				localTimes(new LocalTime(9,5,23), new LocalTime(13,5,43), new LocalTime(17, 03, 12)), 
				"",
				"Yet another irregularity", "");
		
		prestation.setLastDayOfWeek(true);
		
		prestations.add(prestation);

		prestations.add(new Prestation(new LocalDate(), 
				new Duration(1000*60*60*6 + 1000*60*14), 
				new Duration(1000*60*60*8), 
				new Duration(-780000), 
				new Duration(1000*60*60*8), 
				localTimes(new LocalTime(06, 30, 20)), 
				localTimes(new LocalTime(9,5,23)), 
				"",
				"Yet another irregularity", ""));
	
		return prestations;
	}
	
	private List<LocalTime> localTimes(LocalTime... localtimes){
		List<LocalTime> times = new ArrayList<LocalTime>();
		for (LocalTime localTime : localtimes) {
			times.add(localTime);
		}
		return times;
	}
	
	private String json() throws JsonProcessingException {
		LocalTime timeToGoHome = new LocalTime().plusHours(1);
		FetchResult fetchResult = new FetchResult(new LocalDate(), new LocalDate(), prestations(),  new Duration(1000*60*60*8),  new Duration(1000*60*60*6), timeToGoHome);
		return asJSON(fetchResult);
	}

	private String asJSON(FetchResult fetchResult) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JodaModule());
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.enable(SerializationFeature.INDENT_OUTPUT);
		mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
		return mapper.writeValueAsString(fetchResult);
	}
}

