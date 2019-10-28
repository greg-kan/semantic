package kazniisa.kz.reports.report_generators;

import kazniisa.kz.reports.helper;
import org.apache.jena.query.QuerySolution;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextReport {

    public static void generate(List<QuerySolution> querySolutions, String reportFile)  {
        List<String> lines = new ArrayList<>();
        int countDescriptions = 0;
        int currentIteration = 0;
        for (QuerySolution querySolution: querySolutions) {
            String text = "";
            if (currentIteration == 0) {
                countDescriptions = querySolution.getLiteral("cntDescr").getInt();
                text = "Термин - " +
                        helper.getURIShortForm(querySolution.getResource("cls").getURI()) + ", описаний: " +
                        countDescriptions;

                lines.add(text);

                text = querySolution.getLiteral("l").getString();

                lines.add(text);
            }

            currentIteration++;

            lines.add("");
            text = "Описание " + currentIteration;
            lines.add(text);

            text = querySolution.getLiteral("d").getString();
            lines.add(text);

            if (currentIteration == countDescriptions) {
                lines.add("");
                lines.add("*******************************************");
                lines.add("");

                currentIteration = 0;
            }
        }

        try {
            Files.write(Paths.get(reportFile), lines, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(TextReport.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
