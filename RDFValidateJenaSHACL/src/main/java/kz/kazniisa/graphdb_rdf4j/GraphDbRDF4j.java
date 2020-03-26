package kz.kazniisa.graphdb_rdf4j;

import org.eclipse.rdf4j.model.impl.SimpleLiteral;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.query.QueryLanguage;
import org.eclipse.rdf4j.query.TupleQuery;
import org.eclipse.rdf4j.query.TupleQueryResult;
import org.eclipse.rdf4j.query.Update;
import org.eclipse.rdf4j.query.algebra.Str;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.repository.RepositoryConnection;
import org.eclipse.rdf4j.repository.http.HTTPRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Arrays;

import java.util.Random;

public class GraphDbRDF4j {

    private static Logger logger = LoggerFactory.getLogger(GraphDbRDF4j.class);
    // Why This Failure marker
    private static final Marker WTF_MARKER = MarkerFactory.getMarker("WTF");

    // GraphDB
    private static final String GRAPHDB_SERVER = "http://192.168.10.101:7200/";
    private static final String REPOSITORY_ID = "skos_auto_s_core";
    private static String strQuery;

    static {
        strQuery =
                "SELECT ?name FROM DEFAULT WHERE {" +
                        "?s <http://xmlns.com/foaf/0.1/name> ?name .}";
    }

    private static char generate_prefix(char c) {
        char res = '0';

        String dict_en = "abcdefghijklmnopqrstuvwxyz"; //26
        String dict_ru = "абвгдежзиклмнопрстуфхцчшщыэюя"; // 29
        String dict_kz = "аәбвгғдежзикқлмнңоөпрстуұүфхһцчшщыіэюя"; // 38

        Random rand = new Random();

        if (c == 'e') {
            int ix = rand.nextInt(26);
            res = dict_en.charAt(ix);
        }
        else if (c == 'r') {
            int ix = rand.nextInt(29);
            res = dict_ru.charAt(ix);
        }

        return res;
    }

    private static String getStrInsert(int i, String conceptScheme) {
        return
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                        + "INSERT DATA {\n"
                        + "GRAPH <http://skos_auto_s/>{\n"
                        //+ String.format("<http://skos_auto_s/%dConcept> <http://www.w3.org/2004/02/skos/core#broader> <http://skos_auto_s/FirstConcept> .", i)
                        + String.format("<http://skos_auto_s/%dConcept> rdf:type <http://www.w3.org/2004/02/skos/core#Concept> .", i)

                        + String.format("<http://skos_auto_s/%dConcept> skos:prefLabel ", i)  + "\"" + generate_prefix('e') + String.format("%dConcept", i) + "\"@en ."
                        + String.format("<http://skos_auto_s/%dConcept> skos:altLabel ", i) + "\"" + String.format("This is %d Concepts's altLabel", i) + "\"@en ."
                        + String.format("<http://skos_auto_s/%dConcept> skos:definition ", i) + "\"" + String.format("This is the %d concept.", i) + "\"@en ."
                        + String.format("<http://skos_auto_s/%dConcept> skos:note ", i) + "\"" + String.format("%d Concept's Note", i) + "\"@en ."

                        + String.format("<http://skos_auto_s/%dConcept> skos:prefLabel ", i)  + "\"" + generate_prefix('r') + String.format("%dКонцепт", i) + "\"@ru ."
                        + String.format("<http://skos_auto_s/%dConcept> skos:altLabel ", i) + "\"" + String.format("Это %d Концепта altLabel", i) + "\"@ru ."
                        + String.format("<http://skos_auto_s/%dConcept> skos:definition ", i) + "\"" + String.format("Это %d Концепта длинное определение.", i) + "\"@ru ."
                        + String.format("<http://skos_auto_s/%dConcept> skos:note ", i) + "\"" + String.format("%d Концепта длинное замечание", i) + "\"@ru ."

//                        + String.format("<http://skos_auto_s/%dConcept> skos:prefLabel ", i)  + "\"" +  generate_prefix('r') + String.format("%dКонцепт", i) + "\"@kz ."
//                        + String.format("<http://skos_auto_s/%dConcept> skos:altLabel ", i) + "\"" + String.format("Это %d Концепта altLabel", i) + "\"@kz ."
//                        + String.format("<http://skos_auto_s/%dConcept> skos:definition ", i) + "\"" + String.format("Это %d Концепта длинное определение.", i) + "\"@kz ."
//                        + String.format("<http://skos_auto_s/%dConcept> skos:note ", i) + "\"" + String.format("%d Концепта длинное замечание", i) + "\"@kz ."

                        + String.format("<http://skos_auto_s/%dConcept> ", i) + "skos:inScheme <http://skos_auto_s/" + conceptScheme + "> ."
                        + String.format("<http://skos_auto_s/%dConcept> ", i) + "skos:topConceptOf <http://skos_auto_s/" + conceptScheme + "> ."

                        + "}"
                        + "}";
    }

    private static RepositoryConnection getRepositoryConnection() {
        Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
        repository.initialize();
        return repository.getConnection();
    }

    private static void createConceptSchemas(RepositoryConnection repositoryConnection) {
        createConceptSchema(repositoryConnection, "First ConceptScheme", "Предметная область 1", "firstscheme");
        createConceptSchema(repositoryConnection, "Second ConceptScheme","Предметная область 2",  "secondscheme");
        createConceptSchema(repositoryConnection, "Third ConceptScheme","Предметная область 3",  "thirdscheme");
        createConceptSchema(repositoryConnection, "Fourth ConceptScheme","Предметная область 4",  "fourthscheme");
        createConceptSchema(repositoryConnection, "Fifth ConceptScheme","Предметная область 5",  "fifthscheme");
    }

    private static void createConceptSchema(RepositoryConnection repositoryConnection, String schemePrefLabel_en, String schemePrefLabel_ru, String schemeURI) {
        String insertString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                        + "INSERT DATA {\n"
                        + "GRAPH <http://skos_auto_s/>{\n"
                        + "<http://skos_auto_s/" + schemeURI + "> rdf:type skos:ConceptScheme ."
                        + "<http://skos_auto_s/" + schemeURI + "> <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + schemePrefLabel_en + "\"@en ."
                        + "<http://skos_auto_s/" + schemeURI + "> <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + schemePrefLabel_ru + "\"@ru ."
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

        for (int i = 1001; i <= 1999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "firstscheme"));
            updateOperation.execute();
        }

        for (int i = 2001; i <= 2999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "secondscheme"));
            updateOperation.execute();
        }

        for (int i = 3001; i <= 3999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "thirdscheme"));
            updateOperation.execute();
        }

        for (int i = 4001; i <= 4999; i++) {
            Update updateOperation = repositoryConnection.prepareUpdate(QueryLanguage.SPARQL, getStrInsert(i, "fourthscheme"));
            updateOperation.execute();
        }

        for (int i = 5001; i <= 5999; i++) {
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
