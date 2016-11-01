package org.repositoryminer.persistence.handler;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.repositoryminer.persistence.Connection;
import org.repositoryminer.scm.hostingservice.StatusType;

import com.mongodb.BasicDBObject;

public class IssueDocumentHandler extends DocumentHandler{

	private static final String COLLECTION_NAME = "rm_issues";

	public IssueDocumentHandler() {
		super.collection = Connection.getInstance().getCollection(COLLECTION_NAME);
	}
	
	public List<Document> findAllClosedByCommit(String commitHash) {
		BasicDBObject whereClause = new BasicDBObject();
		whereClause.put("events.commit", commitHash);
		whereClause.put("status", StatusType.CLOSED.toString());

		return super.findMany(whereClause);
	}
	
	public void deleteByRepository(String id) {
		BasicDBObject where = new BasicDBObject("repository", new ObjectId(id));
		deleteMany(where);
	}
	
}