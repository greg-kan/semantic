package kazniisa.kz.reports.report_generators;

import kazniisa.kz.reports.helper;
import org.apache.jena.query.QuerySolution;
import org.apache.poi.xwpf.model.XWPFHeaderFooterPolicy;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static kazniisa.kz.reports.helper.*;
import static kazniisa.kz.reports.report_generators.DOCXHelper.*;

public class DOCXReport1 {
    public static void generate(List<QuerySolution> querySolutions, String reportFile) {
        try {
            // создаем модель docx документа,
            // к которой будем прикручивать наполнение (колонтитулы, текст)
            XWPFDocument docxModel = new XWPFDocument();
            CTSectPr ctSectPr = docxModel.getDocument().getBody().addNewSectPr();
            // получаем экземпляр XWPFHeaderFooterPolicy для работы с колонтитулами
            XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(docxModel, ctSectPr);

            // создаем верхний колонтитул Word файла
            CTP ctpHeaderModel = createHeaderModel("Отчет - неоднозначные описания");
            // устанавливаем сформированный верхний колонтитул в модель документа Word
            XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeaderModel, docxModel);
            headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, new XWPFParagraph[]{headerParagraph});

            // создаем нижний колонтитул docx файла
            CTP ctpFooterModel = createFooterModel("Просто нижний колонтитул");
            // устанавливаем сформированый нижний колонтитул в модель документа Word
            XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooterModel, docxModel);
            headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, new XWPFParagraph[]{footerParagraph});

            int countDescriptions = 0;
            int currentIteration = 0;
            for (QuerySolution querySolution: querySolutions) {
                String text = "";
                if (currentIteration == 0) {
                    countDescriptions = getIntLiteral(querySolution, "cntDescr");
                    text = "Термин - " +
                            getURIShortForm(querySolution.getResource("cls").getURI()) + ", описаний: " +
                            countDescriptions;
                    createParagraph(docxModel, text, true, false, 14, "ff0000", ParagraphAlignment.RIGHT);

                    text = getStringLiteral(querySolution, "l");
                    createParagraph(docxModel, text, false, true, 25, "06357a", ParagraphAlignment.CENTER);
                }

                currentIteration++;

                text = "Описание " + currentIteration;
                createParagraph(docxModel, text, true, false, 14, "000000", ParagraphAlignment.LEFT);


                text = getStringLiteral(querySolution, "d");
                createParagraph(docxModel, text, false, false, 14, "000000", ParagraphAlignment.LEFT);

                if (currentIteration == countDescriptions) {
                    text = "*******************************************";
                    createParagraph(docxModel, text, false, false, 20, "00ff00", ParagraphAlignment.CENTER);

                    currentIteration = 0;
                }
            }

            // сохраняем модель docx документа в файл
            FileOutputStream outputStream = new FileOutputStream(reportFile);
            docxModel.write(outputStream);
            outputStream.close();

        } catch (IOException | XmlException ex) {
            Logger.getLogger(TextReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
