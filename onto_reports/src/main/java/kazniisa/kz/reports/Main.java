package kazniisa.kz.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import kazniisa.kz.reports.DBUtils.DBUtil;
import kazniisa.kz.reports.report_generators.DOCXReport1;
import kazniisa.kz.reports.report_generators.DOCXReport2;
import kazniisa.kz.reports.report_generators.DOCXReport3;
import kazniisa.kz.reports.report_generators.TextReport;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

public class Main {

    public static void main(String[] args) {

        Properties props = readProperties();

        helper.url = props.getProperty("db.url");
        helper.user = props.getProperty("db.user");
        helper.password = props.getProperty("db.password");

        String ontologyFile = props.getProperty("onto.file");

        String rqFile1 =  props.getProperty("rpt.rqFile1");
        String txtReportFile1 = props.getProperty("rpt.txtFile1");
        String docxReportFile1 = props.getProperty("rpt.docxFile1");

        String rqFile2 =  props.getProperty("rpt.rqFile2");
        String txtReportFile2 = props.getProperty("rpt.txtFile2");
        String docxReportFile2 = props.getProperty("rpt.docxFile2");

        String rqFile3 =  props.getProperty("rpt.rqFile3");
        String txtReportFile3 = props.getProperty("rpt.txtFile3");
        String docxReportFile3 = props.getProperty("rpt.docxFile3");
        //JenaSystem.init();
        org.apache.jena.query.ARQ.init();

        //Load ontology from file
        Model fileModel = ModelFactory.createDefaultModel();
        try (InputStream is = FileManager.get().open(ontologyFile)) {
            fileModel.read(is, "", "Turtle");//RDF/XML
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        String sparql1 = "";
        String sparql2 = "";
        String sparql3 = "";

        //Load SPARQL Query text from file and execute
        try {
            List<String> lines1 = Files.readAllLines(Paths.get(rqFile1), StandardCharsets.UTF_8);
            List<String> lines2 = Files.readAllLines(Paths.get(rqFile2), StandardCharsets.UTF_8);
            List<String> lines3 = Files.readAllLines(Paths.get(rqFile3), StandardCharsets.UTF_8);

            for(String line: lines1)
                sparql1 += (line + "\n");

            for(String line: lines2)
                sparql2 += (line + "\n");

            for(String line: lines3)
                sparql3 += (line + "\n");
        }
        catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        Query querySPARQL1 = QueryFactory.create(sparql1);
        QueryExecution qexec1 = QueryExecutionFactory.create(querySPARQL1, fileModel/*dbModel*/);
        ResultSet results1 = qexec1.execSelect();

        //Fill 'querySolutions' with querySPARQL results
        final List<QuerySolution> querySolutions1 = new ArrayList<>();
        while (results1.hasNext()) {
            querySolutions1.add(results1.nextSolution());
        }

        Query querySPARQL2 = QueryFactory.create(sparql2);
        QueryExecution qexec2 = QueryExecutionFactory.create(querySPARQL2, fileModel/*dbModel*/);
        ResultSet results2 = qexec2.execSelect();

        //ResultSetFormatter.out(System.out, results2, querySPARQL2);

        //Fill 'querySolutions' with querySPARQL results
        final List<QuerySolution> querySolutions2 = new ArrayList<>();
        while (results2.hasNext()) {
            querySolutions2.add(results2.nextSolution());
        }


        Query querySPARQL3 = QueryFactory.create(sparql3);
        QueryExecution qexec3 = QueryExecutionFactory.create(querySPARQL3, fileModel/*dbModel*/);
        ResultSet results3 = qexec3.execSelect();

        //Fill 'querySolutions' with querySPARQL results
        final List<QuerySolution> querySolutions3 = new ArrayList<>();
        while (results3.hasNext()) {
            querySolutions3.add(results3.nextSolution());
        }

        fileModel.close();

        //Generates text reports
//        TextReport.generate(querySolutions, txtReportFile1);
//        System.out.println("Text report: '" + txtReportFile1 + "' created.");

        //Generate docx reports
        DOCXReport1.generate(querySolutions1, docxReportFile1);
        System.out.println("DOCX report1: '" + docxReportFile1 + "' created.");

        DOCXReport2.generate(querySolutions2, docxReportFile2);
        System.out.println("DOCX report2: '" + docxReportFile2 + "' created.");

        DOCXReport3.generate(querySolutions3, docxReportFile3);
        System.out.println("DOCX report3  '" + docxReportFile3 + "' created.");

        //Write querySPARQL results to PostgreSQL DB
//        System.out.println("Results've written to DB.\nCommitted " + DBUtil.writeTo(querySolutions) + " updates.");
    }

    private static Properties readProperties() {

        Properties props = new Properties();
        Path myPath = Paths.get("src/main/resources/application.properties");
        //myPath = Paths.get("E:\\Docs\\test_jar\\src\\main\\resources\\application.properties");

        try {
            BufferedReader bf = Files.newBufferedReader(myPath,
                    StandardCharsets.UTF_8);

            props.load(bf);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(
                    Level.SEVERE, null, ex);
        }

        return props;
    }
}

/*
        try (Connection con = DriverManager.getConnection(url, user, password);
             Statement st = con.createStatement();
             //ResultSet rs = st.executeQuery("SELECT VERSION()")) {
            ResultSet rs = st.executeQuery("select termin_id from public.tb_ambiguous_descriptions")) {
            if (rs.next()) {
                System.out.println(rs.getString(1));
            }

        } catch (SQLException ex) {

            Logger lgr = Logger.getLogger("Error occurred");
            lgr.log(Level.SEVERE, ex.getMessage(), ex);
        }
*/
