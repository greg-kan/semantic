package kazniisa.kz.reports.report_generators;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;

public class DOCXHelper {

    public static void createParagraph(XWPFDocument docxModel, String text, boolean bold, boolean italic, int fontSize, String color, ParagraphAlignment align) {
        XWPFParagraph bodyParagraph = docxModel.createParagraph();
        bodyParagraph.setAlignment(align);
        XWPFRun paragraphConfig = bodyParagraph.createRun();
        paragraphConfig.setBold(bold);
        paragraphConfig.setItalic(italic);
        paragraphConfig.setFontSize(fontSize);
        paragraphConfig.setColor(color);
        paragraphConfig.setText(text);
    }

    public static CTP createFooterModel(String footerContent) {
        // создаем футер или нижний колонтитул
        CTP ctpFooterModel = CTP.Factory.newInstance();
        CTR ctrFooterModel = ctpFooterModel.addNewR();
        CTText cttFooter = ctrFooterModel.addNewT();

        cttFooter.setStringValue(footerContent);
        return ctpFooterModel;
    }

    public static CTP createHeaderModel(String headerContent) {
        // создаем хедер или верхний колонтитул
        CTP ctpHeaderModel = CTP.Factory.newInstance();
        CTR ctrHeaderModel = ctpHeaderModel.addNewR();
        CTText cttHeader = ctrHeaderModel.addNewT();

        cttHeader.setStringValue(headerContent);
        return ctpHeaderModel;
    }
}
