package eu.geoc.application.services;

import eu.geoc.application.model.CE.CEAreasList;
import eu.geoc.application.model.SC.SCAreasList;
import eu.geoc.application.model.SOP.SOPAreasList;
import eu.geoc.application.model.UserEntry;
import eu.geoc.application.persistence.FPGsonBuilder;
import eu.geoc.application.persistence.PersistenceBuilder;
import eu.geoc.application.services.model.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

@Consumes({APPLICATION_JSON})
@Produces({APPLICATION_JSON})
@Path("post")
public class PostManagementServices extends BaseManagementServices{

	public PostManagementServices() {
		super();
		PersistenceBuilder.getInstance().postInit();
		this.dao = new MongoDAO(PersistenceBuilder.getInstance().getMongoDatabaseManager());
	}

	@POST
	public String pong(LayersResult layerResult) {
		return "OK";
	}

	@POST
	@Path("all")
	@Consumes(TEXT_PLAIN)
	@Produces(TEXT_PLAIN)
	public String putItAll(String data) {
		UserEntry[] entries = FPGsonBuilder.getNewGson().fromJson(data, UserEntry[].class);
		for (UserEntry entry : entries) {
			FirstData firstData = FirstData.getFromUserEntry(entry);
			String id = dao.setHome(firstData, false);
			entry.setId(id);
			SOPAreasList sopAreaList = SOPFiller.getFromUserEntry(entry);
			if (sopAreaList != null) {dao.setSOP(id, sopAreaList, false);}
			SCAreasList scAreasList = SCFiller.getFromUserEntry(entry);
			if (scAreasList != null) {dao.setSC(id, scAreasList, false);}
			CEAreasList ceAreasList = CEFiller.getFromUserEntry(entry);
			if (ceAreasList != null) {dao.setCE(id, ceAreasList, false);}
			UserDetails userDetails = UserDetails.getFromUserEntry(entry);
			if (userDetails != null) {dao.finish(id, userDetails, false);}
			LastData lastData = LastData.getFromUserEntry(entry);
			if (lastData != null) {dao.setFinalDetails(id, lastData, false);}
			FinalComments finalComments = FinalComments.getFromUserEntry(entry);
			if (finalComments != null) {dao.setFinalComments(id, finalComments, false);}
		}
		dao.close();
		return "OK";
	}
}

