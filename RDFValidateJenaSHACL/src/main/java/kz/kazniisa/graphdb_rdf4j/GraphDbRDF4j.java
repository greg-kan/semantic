package kz.kazniisa.graphdb_rdf4j;

import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Arrays;

public class GraphDbRDF4j {

    private static Logger logger = LoggerFactory.getLogger(GraphDbRDF4j.class);
    // Why This Failure marker
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

    // GraphDB
    private static final String GRAPHDB_SERVER = "http://localhost:7200/";
    private static final String REPOSITORY_ID = "skos_auto_core";
    private static String strQuery;

    static {
        strQuery =
                "SELECT ?name FROM DEFAULT WHERE {" +
                        "?s <http://xmlns.com/foaf/0.1/name> ?name .}";
    }

    private static String getStrInsert(int i, String conceptScheme) {
        return
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                        + "INSERT DATA {\n"
                        + "GRAPH <http://skos_auto/>{\n"
                        //+ String.format("<http://skos_auto/%dConcept> <http://www.w3.org/2004/02/skos/core#broader> <http://skos_auto/FirstConcept> .", i)
                        + String.format("<http://skos_auto/%dConcept> rdf:type <http://www.w3.org/2004/02/skos/core#Concept> .", i)

                        + String.format("<http://skos_auto/%dConcept> skos:prefLabel ", i)  + "\"" + String.format("%dConcept", i) + "\"@en ."
                        + String.format("<http://skos_auto/%dConcept> skos:altLabel ", i) + "\"" + String.format("This is %d Concepts's altLabel", i) + "\"@en ."
                        + String.format("<http://skos_auto/%dConcept> skos:definition ", i) + "\"" + String.format("This is the %d concept.", i) + "\"@en ."
                        + String.format("<http://skos_auto/%dConcept> skos:note ", i) + "\"" + String.format("%d Concept's Note", i) + "\"@en ."

                        + String.format("<http://skos_auto/%dConcept> skos:prefLabel ", i)  + "\"" + String.format("%dКонцепт", i) + "\"@ru ."
                        + String.format("<http://skos_auto/%dConcept> skos:altLabel ", i) + "\"" + String.format("Это %d Концепта altLabel", i) + "\"@ru ."
                        + String.format("<http://skos_auto/%dConcept> skos:definition ", i) + "\"" + String.format("Это %d Концепта длинное определение.", i) + "\"@ru ."
                        + String.format("<http://skos_auto/%dConcept> skos:note ", i) + "\"" + String.format("%d Концепта длинное замечание", i) + "\"@ru ."

                        + String.format("<http://skos_auto/%dConcept> skos:prefLabel ", i)  + "\"" + String.format("%dКонцепт", i) + "\"@kz ."
                        + String.format("<http://skos_auto/%dConcept> skos:altLabel ", i) + "\"" + String.format("Это %d Концепта altLabel", i) + "\"@kz ."
                        + String.format("<http://skos_auto/%dConcept> skos:definition ", i) + "\"" + String.format("Это %d Концепта длинное определение.", i) + "\"@kz ."
                        + String.format("<http://skos_auto/%dConcept> skos:note ", i) + "\"" + String.format("%d Концепта длинное замечание", i) + "\"@kz ."

                        + String.format("<http://skos_auto/%dConcept> ", i) + "skos:inScheme <http://skos_auto/" + conceptScheme + "> ."
                        + String.format("<http://skos_auto/%dConcept> ", i) + "skos:topConceptOf <http://skos_auto/" + conceptScheme + "> ."

                        + "}"
                        + "}";
    }

    private static RepositoryConnection getRepositoryConnection() {
        Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
        repository.initialize();
        return repository.getConnection();
    }

    private static void createConceptSchemas(RepositoryConnection repositoryConnection) {
        createConceptSchema(repositoryConnection, "This is the First ConceptScheme", "firstscheme");
        createConceptSchema(repositoryConnection, "This is the Second ConceptScheme", "secondscheme");
        createConceptSchema(repositoryConnection, "This is the Third ConceptScheme", "thirdscheme");
        createConceptSchema(repositoryConnection, "This is the Fourth ConceptScheme", "fourthscheme");
        createConceptSchema(repositoryConnection, "This is the Fifth ConceptScheme", "fifthscheme");
    }

    private static void createConceptSchema(RepositoryConnection repositoryConnection, String schemePrefLabel, String schemeURI) {
        String insertString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                        + "INSERT DATA {\n"
                        + "GRAPH <http://skos_auto/>{\n"
                        + "<http://skos_auto/" + schemeURI + "> rdf:type skos:ConceptScheme ."
                        + "<http://skos_auto/" + schemeURI + "> <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + schemePrefLabel + "\"@en ."
                        + "}"
                        + "}";

        repositoryConnection.begin();
        Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, insertString);
        updateOperation.execute();

        try {
            repositoryConnection.commit();
        } catch (Exception e) {
            if (repositoryConnection.isActive())
                repositoryConnection.rollback();
        }
    }

    private static void insert(RepositoryConnection repositoryConnection) {
        repositoryConnection.begin();

        for (int i = 10001; i <= 19999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "firstscheme"));
            updateOperation.execute();
        }

        for (int i = 20001; i <= 29999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "secondscheme"));
            updateOperation.execute();
        }

        for (int i = 30001; i <= 39999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "thirdscheme"));
            updateOperation.execute();
        }

        for (int i = 40001; i <= 49999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "fourthscheme"));
            updateOperation.execute();
        }

        for (int i = 50001; i <= 59999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "fifthscheme"));
            updateOperation.execute();
        }
        try {
            repositoryConnection.commit();
        } catch (Exception e) {
            if (repositoryConnection.isActive())
                repositoryConnection.rollback();
        }
    }

    private static void query(RepositoryConnection repositoryConnection) {
        TupleQuery tupleQuery = repositoryConnection.prepareTupleQuery(QueryLanguage.SPARQL, strQuery);
        try (TupleQueryResult result = tupleQuery.evaluate()) {
            while (result.hasNext()) {
                BindingSet bindingSet = result.next();
                SimpleLiteral name = (SimpleLiteral) bindingSet.getValue("name");
                logger.info("name = " + name.stringValue());
            }
        } catch (QueryEvaluationException qee) {
            logger.error(WTF_MARKER, Arrays.toString(qee.getStackTrace()), qee);
        }
    }

    public static void main(String[] args) {
        try (RepositoryConnection repositoryConnection = getRepositoryConnection()) {
            createConceptSchemas(repositoryConnection);
            insert(repositoryConnection);
            //query(repositoryConnection);
        } catch (Throwable t) {
            logger.error(WTF_MARKER, t.getMessage(), t);
        }
    }
}
