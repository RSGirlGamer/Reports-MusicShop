package com.project.musicshop.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.project.musicshop.services.DropboxAPIService;
import com.project.musicshop.services.MailService;

@Component
@Path("/reportsWS")
public class ReportsWS {
	
	@Value("${spring.dropbox.access.token}")
	String ACCESS_TOKEN;
	@Autowired
	private DropboxAPIService dropboxAPIServiceImpl;
	
	@Autowired
	private MailService mailServiceImpl;
	
	@GET
	@Path("/testWS")
	public String testWS() {
		return "Ingrensando al WebService";
	}
	@POST
	@Path("/generateReport")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response generateReport(@FormParam("orderID") String orderID, @FormParam("client") String client, @FormParam("recipient") String recipient) {
		DbxRequestConfig dbxRequestConfig = DbxRequestConfig.newBuilder("dropbox/project").build();
		DbxClientV2 dbxClientV2 = new DbxClientV2(dbxRequestConfig, ACCESS_TOKEN);
		Response response = this.dropboxAPIServiceImpl.downloadReport(dbxClientV2, orderID, client);
		this.mailServiceImpl.sendEmail(dbxClientV2, recipient, client, orderID);
		return response;
	}
}
