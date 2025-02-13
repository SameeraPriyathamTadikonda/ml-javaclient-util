package com.marklogic.client.ext.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.DocumentMetadataHandle.DocumentCollections;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;

/**
 * The intent of this class is to provide some syntactic sugar on common operations using the ML Java API, which is a
 * wrapper around the ML REST API.
 */
public class ClientHelper extends LoggingObject {

    private DatabaseClient client;

    public ClientHelper(DatabaseClient client) {
        this.client = client;
    }

    public DatabaseClient getClient() {
        return this.client;
    }

    public DocumentMetadataHandle getMetadata(String uri) {
        return client.newDocumentManager().readMetadata(uri, new DocumentMetadataHandle());
    }

    public List<String> getCollections(String uri) {
        DocumentCollections colls = getMetadata(uri).getCollections();
        return Arrays.asList(colls.toArray(new String[] {}));
    }

    public long getCollectionSize(String collectionName) {
        QueryManager queryMgr = getClient().newQueryManager();
        StringQueryDefinition def = queryMgr.newStringDefinition();
        def.setCollections(collectionName);
        SearchHandle sh = queryMgr.search(def, new SearchHandle());
        return sh.getTotalResults();
    }

    public List<String> getUrisInCollection(String collectionName) {
        return getUrisInCollection(collectionName, 10);
    }

    public List<String> getUrisInCollection(String collectionName, int pageLength) {
        QueryManager mgr = getClient().newQueryManager();
        mgr.setPageLength(pageLength);
        StringQueryDefinition def = mgr.newStringDefinition();
        def.setCollections(collectionName);
        SearchHandle h = mgr.search(def, new SearchHandle());
        List<String> uris = new ArrayList<>();
        for (MatchDocumentSummary s : h.getMatchResults()) {
            uris.add(s.getUri());
        }
        return uris;
    }

    public String eval(String expr) {
        return getClient().newServerEval().xquery(expr).evalAs(String.class);
    }

}
