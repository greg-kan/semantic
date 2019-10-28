package kazniisa.kz.reports;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Literal;

public class helper {

    public static final char delimiter = '#';

    final static Integer numberOfReports = 3;

    public static String url = "";
    public static String user = "";
    public static String password = "";

    public static String getURIShortForm(String str) { return str.substring(str.lastIndexOf(delimiter) + 1); }

    public static String getStringLiteral(QuerySolution querySolution, String varName) {
        return (querySolution.getLiteral(varName) != null ? querySolution.getLiteral(varName).getString() : "");
    }

    public static int getIntLiteral(QuerySolution querySolution, String varName) {
        return (querySolution.getLiteral(varName) != null ? querySolution.getLiteral(varName).getInt() : 0);
    }

}
