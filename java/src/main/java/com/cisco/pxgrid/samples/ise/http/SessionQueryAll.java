package com.cisco.pxgrid.samples.ise.http;

import java.io.IOException;
import java.text.ParseException;
import java.time.OffsetDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cisco.pxgrid.model.AccountState;
import com.cisco.pxgrid.model.Service;

/**
 * Demonstrates how to use query all sessions from ISE
 */
public class SessionQueryAll {
	private static Logger logger = LoggerFactory.getLogger(SessionQueryAll.class);
	
	private static class SessionQueryRequest {
		OffsetDateTime startTime;
	}

	private static void downloadUsingAccessSecret(SampleConfiguration config) throws IOException, ParseException {
		OffsetDateTime startTime = SampleHelper.promptDate("Enter start time (ex. '2015-01-31T13:00:00-07:00' or <enter> for no start time): ");

		
		PxgridControl https = new PxgridControl(config);
		Service[] services = https.lookupService("com.cisco.ise.session");
		if (services == null || services.length == 0) {
			logger.warn("Service unavailabe");
			return;
		}
		
		Service service = services[0];
		String url = service.getProperties().get("restBaseURL") + "/getSessions";
		logger.info("url={}", url);
		
		String secret = https.getAccessSecret(service.getNodeName());
		SessionQueryRequest request = new SessionQueryRequest();
		request.startTime = startTime;
		SampleHelper.postObjectAndPrint(url, config.getNodeName(), secret, config.getSSLContext().getSocketFactory(), request);
	}

	public static void main(String [] args) throws Exception {
		SampleConfiguration config = new SampleConfiguration();
		PxgridControl control = new PxgridControl(config);
		
		while (control.accountActivate() != AccountState.ENABLED)
			Thread.sleep(60000);
		logger.info("pxGrid controller version=" + control.getControllerVersion());

		downloadUsingAccessSecret(config);
	}
}
