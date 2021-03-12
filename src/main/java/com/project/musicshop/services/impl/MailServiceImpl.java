package com.project.musicshop.services.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.DownloadErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.project.musicshop.services.MailService;

@Service
public class MailServiceImpl implements MailService {
	@Value("${spring.mail.aws.smp.user}")
	String user;
	@Value("${spring.mail.aws.smp.password}")
	String password;
	@Value("${spring.mail.aws.smp.port}")
	String port;
	@Value("${spring.mail.aws.smp.email}")
	String email;
	@Value("${spring.mail.aws.smp.starttls.enable}")
	String starttks;
	@Value("${spring.mail.aws.smp.host}")
	String host;
	@Value("${spring.dropbox.directory.reports}")
	String pathReports;
	
	@Override
	public Response sendEmail(DbxClientV2 dbxClientV2, String recipient, String client, String orderID) {
		Properties properties = System.getProperties();
		properties.setProperty("mail.smtp.host", this.host);
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.smtp.starttls.enable", this.starttks);
		properties.put("mail.smtp.port", this.port);
		Session session = Session.getDefaultInstance(properties, new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(user, password);
			}
		});
		MimeMessage message = new MimeMessage(session);
		try {
			message.setFrom(new InternetAddress(this.email));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
			message.setSubject("Compra realizada exitósamente - " + orderID);
			ByteArrayOutputStream fileBytes = new ByteArrayOutputStream();
			DbxDownloader<FileMetadata> downloader = dbxClientV2.files().download(this.pathReports + "/" + client + "/" + orderID + ".pdf");
			downloader.download(fileBytes);
			BodyPart bodyPartText = new MimeBodyPart();
			bodyPartText.setText("Has realizado tu compra de Manera Exitosa, adjunto a este correo podrás encontrar tu comprobante de pago para reclamar tus productos");
			byte[] bytes = fileBytes.toByteArray();
			InputStream inputStream = new ByteArrayInputStream(bytes);
			ByteArrayDataSource byteArrayDataSource = new ByteArrayDataSource(inputStream, "application/pdf");
			BodyPart bodyPartFile = new MimeBodyPart();
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(bodyPartText);
			bodyPartFile.setDataHandler(new DataHandler(byteArrayDataSource));
			bodyPartFile.setFileName("Comprobante-" + orderID + ".pdf");
			multipart.addBodyPart(bodyPartFile);
			message.setContent(multipart);
			AmazonSimpleEmailService amazonSimpleEmailService = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.US_EAST_2).build();
			PrintStream printStream = System.out;
			message.writeTo(printStream);
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
			SendRawEmailRequest emailRequest = new SendRawEmailRequest(rawMessage);
			amazonSimpleEmailService.sendRawEmail(emailRequest);
			return Response.ok().build();
		} catch (AddressException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (MessagingException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DownloadErrorException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (DbxException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		} catch (IOException e) {
			System.out.println(e.getCause());
			e.printStackTrace();
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
		}
	}
}
