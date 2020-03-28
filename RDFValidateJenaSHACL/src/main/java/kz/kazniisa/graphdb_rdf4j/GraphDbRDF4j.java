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
    private static final String REPOSITORY_ID = "skos_auto_core";
    private static final String PROJECT_IRI = "http://skos_auto/";
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
        else if (c == 'k') {
            int ix = rand.nextInt(38);
            res = dict_kz.charAt(ix);
        }
        return res;
    }

    private static String getStrInsert(int i, String conceptScheme) {
        char p_e = generate_prefix('e');
        char p_r = generate_prefix('r');
        char p_k = generate_prefix('k');

        return
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                        + "INSERT DATA {\n"
                        + "GRAPH <" + PROJECT_IRI + ">{\n"
                        //+ String.format("<http://skos_auto_s/%dConcept> <http://www.w3.org/2004/02/skos/core#broader> <http://skos_auto_s/FirstConcept> .", i)
                        + String.format("<" + PROJECT_IRI + "%dConcept> rdf:type <http://www.w3.org/2004/02/skos/core#Concept> .", i)

                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:prefLabel ", i)  + "\"" + String.format("%c %d Concept", p_e, i) + "\"@en ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:altLabel ", i) + "\"" + String.format("This is %c %d Concepts's synonym", p_e, i) + "\"@en ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:definition ", i) + "\"" + String.format("Definition of the %c %d concept.", p_e, i) + "\"@en ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:note ", i) + "\"" + String.format("%c %d Concept's Note",  p_e, i) + "\"@en ."

                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:prefLabel ", i)  + "\"" + String.format("%c %d Понятие", p_r, i) + "\"@ru ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:altLabel ", i) + "\"" + String.format("Синоним %c %d Понятия", p_r, i) + "\"@ru ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:definition ", i) + "\"" + String.format("Определение %c %d Понятия", p_r, i) + "\"@ru ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:note ", i) + "\"" + String.format("Замечание к %c %d Понятию", p_r, i) + "\"@ru ."

                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:prefLabel ", i)  + "\"" + String.format("%c %d Ұғым", p_k, i) + "\"@kz ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:altLabel ", i) + "\"" + String.format("%c %d Ұғымының синонимі", p_k, i) + "\"@kz ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:definition ", i) + "\"" + String.format("%c %d Үғымның анықтамасы", p_k, i) + "\"@kz ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> skos:note ", i) + "\"" + String.format("%c %d Үғымның жазба нота", p_k, i) + "\"@kz ."

                        + String.format("<" + PROJECT_IRI + "%dConcept> ", i) + "skos:inScheme <" + PROJECT_IRI + conceptScheme + "> ."
                        + String.format("<" + PROJECT_IRI + "%dConcept> ", i) + "skos:topConceptOf <" + PROJECT_IRI + conceptScheme + "> ."

                        + "}"
                        + "}";
    }

    private static RepositoryConnection getRepositoryConnection() {
        Repository repository = new HTTPRepository(GRAPHDB_SERVER, REPOSITORY_ID);
        repository.initialize();
        return repository.getConnection();
    }

    private static void createConceptSchemas(RepositoryConnection repositoryConnection) {
        createConceptSchema(repositoryConnection, "First ConceptScheme",
                "Предметная область 1", "Пәндік аймақ 1", "firstscheme");
        createConceptSchema(repositoryConnection, "Second ConceptScheme",
                "Предметная область 2", "Пәндік аймақ 2", "secondscheme");
        createConceptSchema(repositoryConnection, "Third ConceptScheme",
                "Предметная область 3", "Пәндік аймақ 3", "thirdscheme");
        createConceptSchema(repositoryConnection, "Fourth ConceptScheme",
                "Предметная область 4", "Пәндік аймақ 4", "fourthscheme");
        createConceptSchema(repositoryConnection, "Fifth ConceptScheme",
                "Предметная область 5", "Пәндік аймақ 5", "fifthscheme");
    }

    private static void createConceptSchema(RepositoryConnection repositoryConnection, String schemePrefLabel_en,
                                            String schemePrefLabel_ru, String schemePrefLabel_kz, String schemeURI) {
        String insertString =
                "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                        + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
                        + "INSERT DATA {\n"
                        + "GRAPH <" + PROJECT_IRI + ">{\n"
                        + "<" + PROJECT_IRI + schemeURI + "> rdf:type skos:ConceptScheme ."
                        + "<" + PROJECT_IRI + schemeURI + "> <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + schemePrefLabel_en + "\"@en ."
                        + "<" + PROJECT_IRI + schemeURI + "> <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + schemePrefLabel_ru + "\"@ru ."
                        + "<" + PROJECT_IRI + schemeURI + "> <http://www.w3.org/2004/02/skos/core#prefLabel> \"" + schemePrefLabel_kz + "\"@kz ."
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
