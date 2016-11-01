package org.repositoryminer.effort.persistence.handler;

import java.util.List;

import org.bson.Document;
import org.repositoryminer.persistence.Connection;
import org.repositoryminer.persistence.handler.DocumentHandler;

import com.mongodb.BasicDBObject;

public class EffortsByReferenceDocumentHandler extends DocumentHandler {

	private static final String COLLECTION_NAME = "efforts_by_reference";

	public EffortsByReferenceDocumentHandler() {
		super.collection = Connection.getInstance().getCollection(COLLECTION_NAME);
	}

	public List<Document> findByRepositoryAndReference(String repositoryId, String referenceId) {
		BasicDBObject whereClause = new BasicDBObject();
		whereClause.put("repository", repositoryId);
		whereClause.put("reference", referenceId);

		return findMany(whereClause);
	}

	public List<Document> findByRepositoryAndReferencePath(String repositoryId, String referencePath) {
		BasicDBObject whereClause = new BasicDBObject();
		whereClause.put("repository", repositoryId);
		whereClause.put("reference_path", referencePath);

		return findMany(whereClause);
	}

}
