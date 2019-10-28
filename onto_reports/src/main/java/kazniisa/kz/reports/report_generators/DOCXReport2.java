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

public class DOCXReport2 {
    public static void generate(List<QuerySolution> querySolutions, String reportFile) {
        try {
            // создаем модель docx документа,
            XWPFDocument docxModel = new XWPFDocument();
            CTSectPr ctSectPr = docxModel.getDocument().getBody().addNewSectPr();
            XWPFHeaderFooterPolicy headerFooterPolicy = new XWPFHeaderFooterPolicy(docxModel, ctSectPr);

            // создаем верхний колонтитул Word файла
            CTP ctpHeaderModel = createHeaderModel("Отчет - эквивалентные классы");
            // устанавливаем сформированный верхний колонтитул в модель документа Word
            XWPFParagraph headerParagraph = new XWPFParagraph(ctpHeaderModel, docxModel);
            headerFooterPolicy.createHeader(XWPFHeaderFooterPolicy.DEFAULT, new XWPFParagraph[]{headerParagraph});

            // создаем нижний колонтитул docx файла
            CTP ctpFooterModel = createFooterModel("Нижний колонтитул");
            // устанавливаем сформированый нижний колонтитул в модель документа Word
            XWPFParagraph footerParagraph = new XWPFParagraph(ctpFooterModel, docxModel);
            headerFooterPolicy.createFooter(XWPFHeaderFooterPolicy.DEFAULT, new XWPFParagraph[]{footerParagraph});

            String termin = "";
            String terminEqu = "";
            boolean veryFirstIteration = true;
            for (QuerySolution querySolution: querySolutions) {
                String term = getURIShortForm(querySolution.getResource("c").getURI());
                String termEqu = getURIShortForm(querySolution.getResource("cls").getURI());
                String tag  = getStringLiteral(querySolution, "f");
                String text = "";

                if (!term.equals(termin)) {//New Termin

                    if (!veryFirstIteration) {//Splitting Termins by ***********
                        text = "*******************************************";
                        createParagraph(docxModel, text, false, false, 20, "00ff00", ParagraphAlignment.CENTER);
                    }

                    veryFirstIteration = false;

                    text = "Термин - " + term; //termEqu
                    createParagraph(docxModel, text, true, false, 16, "ff0000", ParagraphAlignment.RIGHT);

                    text = getStringLiteral(querySolution, "l"); //lbl
                    createParagraph(docxModel, text, false, true, 26, "06117a", ParagraphAlignment.CENTER);

                    termin = term;
                    terminEqu = "";
                }

                //if (term.equals(termEqu)) {//This is Termin's row
                if (tag.equals("a")) {//This is Termin's row
                    text = "Описание:";
                    createParagraph(docxModel, text, true, false, 16, "000000", ParagraphAlignment.LEFT);

                    text = getStringLiteral(querySolution, "dsc");
                    createParagraph(docxModel, text, false, false, 16, "000000", ParagraphAlignment.LEFT);
//                } else if (tag.equals("b")) {//This is Relation's row
//                    text = "Связь 'эквивалентен' имеет следующий комментарий:";
//                    createParagraph(docxModel, text, true, false, 14, "005500", ParagraphAlignment.LEFT);
//
//                    text = getStringLiteral(querySolution, "dsc");
//                    createParagraph(docxModel, text, false, false, 14, "005500", ParagraphAlignment.LEFT);
//
                } else if (tag.equals("b")) {//This is Equal termin's row
                    if (!termEqu.equals(terminEqu)) { //New Equal Termin
                        text = "Эквивалентный Термин - " + termEqu;
                        createParagraph(docxModel, text, true, false, 12, "ff0000", ParagraphAlignment.RIGHT);

                        text = getStringLiteral(querySolution, "lbl");
                        createParagraph(docxModel, text, false, true, 18, "0000ff", ParagraphAlignment.CENTER);

                        text = "Связь 'Эквивалентен' описывается следующим комментарием:";
                        createParagraph(docxModel, text, true, false, 12, "005500", ParagraphAlignment.RIGHT);

                        text = getStringLiteral(querySolution, "com");
                        createParagraph(docxModel, text, false, true, 18, "005500", ParagraphAlignment.CENTER);

                        terminEqu = termEqu;
                    }

                    text = "Описание эквивалентного термина:";
                    createParagraph(docxModel, text, true, false, 12, "000000", ParagraphAlignment.LEFT);

                    text = getStringLiteral(querySolution, "dsc");
                    createParagraph(docxModel, text, false, false, 12, "000000", ParagraphAlignment.LEFT);

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
