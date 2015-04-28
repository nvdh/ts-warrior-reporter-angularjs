package be.nvdh.ts.reporter;

import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.apache.commons.io.FileUtils.readFileToString;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import be.nvdh.ts.domain.FetchResult;
import be.nvdh.ts.exception.ReportException;
import be.nvdh.ts.report.Reporter;

public class AngularJSReporter implements Reporter {

	private static final String REPORTER_SHORTNAME = "AngularJS";
	
	public static final String CONFIG_LOCATION = "location";
	public static final String CONFIG_AUTOOPEN = "autoopen";
	public static final String CONFIG_TEMPLATE = "template";
	
	public static final String JSON_CONTEXT_KEY = "JSON";
	
	private static final String FILE_ROOT = "/";
	private static final String FILE_JAVASCRIPT = "ts.js";
	private static final String FILE_CSS = "styles.css";
	private static final String FILE_TEMPLATE = "template.html";
	private static final String JSON_PLACEHOLDER = "%JSON%";
	
	private String location = ".";
	private boolean autoopen = false;
	
	private boolean customTemplate = false;
	private String customTemplateLocation = "";

	private DateTimeFormatter monthFormat = DateTimeFormat.forPattern("MMMM YYYY");

	private static final Logger log = LogManager.getLogger(AngularJSReporter.class);
	
	public void publish(FetchResult fetchResult, Map<String, String> context) throws ReportException {
		try {
			prepareOutputFolder();
			writeResources();
			writeReport(fetchResult, context);
		} catch (IOException e) {
			e.printStackTrace();
			throw new ReportException("Error while writing report", e);
		}
	}
	
	public void init(Map<String, String> config) {
		location = config.get(CONFIG_LOCATION);
		if (StringUtils.isEmpty(location)) {
			location = ".";
		}
		String autoopenAsString = config.get(CONFIG_AUTOOPEN);
		if (!StringUtils.isEmpty(autoopenAsString)){
			autoopen = Boolean.parseBoolean(autoopenAsString);
		}
		String customTemplateLocationAsString = config.get(CONFIG_TEMPLATE);
		if (!StringUtils.isEmpty(customTemplateLocationAsString) && new File(customTemplateLocationAsString).exists()){
			customTemplate = true;
			customTemplateLocation = customTemplateLocationAsString;
		}
	}

	public String getName() {
		return REPORTER_SHORTNAME;
	}

	private void writeReport(FetchResult fetchResult, Map<String, String> context) throws IOException {
		copyTemplate();
		File report = writeReport(fetchResult, template().replace(JSON_PLACEHOLDER, getJSON(context)));
		removeTemplate();
		openReport(report);
	}

	private void removeTemplate() {
		FileUtils.deleteQuietly(new File(location + File.separator + FILE_TEMPLATE));
	}

	private File writeReport(FetchResult fetchResult, String reportAsString) throws IOException {
		File report = new File(fileName(fetchResult));
		report.createNewFile();
		FileUtils.writeStringToFile(report, reportAsString);
		log.info("Report created : " + report.getAbsolutePath());
		return report;
	}
	
	private void openReport(File report) throws IOException {
		if (autoopen && Desktop.isDesktopSupported()) {
			Desktop.getDesktop().browse(report.toURI());
		}
	}

	private String template() throws IOException {
		return readFileToString(new File(location + File.separator + FILE_TEMPLATE));
	}

	private void copyTemplate() throws IOException {
		if (customTemplate){
			FileUtils.copyFile(new File(customTemplateLocation), new File(location + File.separator +FILE_TEMPLATE));
		}
		copyURLToFile(getClass().getResource(FILE_ROOT + FILE_TEMPLATE), new File(location + File.separator +FILE_TEMPLATE));
	}

	private void writeResources() throws IOException {
		copyURLToFile(getClass().getResource(FILE_ROOT + FILE_JAVASCRIPT), new File(location + File.separator + FILE_JAVASCRIPT));
		copyURLToFile(getClass().getResource(FILE_ROOT + FILE_CSS), new File(location + File.separator + FILE_CSS));
	}
	
	private String getJSON(Map<String, String> context) {
		String jsonString = context.get(JSON_CONTEXT_KEY);
		return jsonString != null ? jsonString : "";
	}

	private String fileName(FetchResult fetchResult) {
		return location + File.separator + "Timesheet - " + monthFormat.print(fetchResult.getFetchDate()) + " - " + new LocalDateTime().toString("YYYMMDDmmss") + ".html";
	}
	
	private void prepareOutputFolder() {
		File outputFolder = new File(location);
		if (!outputFolder.exists()){
			outputFolder.mkdir();
		}
	}
	
	public String toString(){
		return REPORTER_SHORTNAME;
	}
	
}
