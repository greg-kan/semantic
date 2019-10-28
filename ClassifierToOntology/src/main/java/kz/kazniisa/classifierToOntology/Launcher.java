package kz.kazniisa.classifierToOntology;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.io.*;
import java.util.*;

public class Launcher {
    private static String strOntologyIRI = "http://kazniisa.kz/ontologies/classifier_ontology.ttl";
    private static String strOntologyPath = "D:\\projects\\semantic\\ClassifierToOntology\\data_out\\classifier_ontology.ttl";
    private static String strExcelFile = "D:\\projects\\semantic\\ClassifierToOntology\\data_from\\Components_2019.xlsx";

    public static void main(String[] args) {
        System.out.println("Begin creating Classifier ontology...");

        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        OWLOntology ontology;
        IRI ontologyIRI = IRI.create(strOntologyIRI);
        try {
            ontology = manager.createOntology(ontologyIRI);
        } catch (OWLOntologyCreationException e) {
            System.out.println("Error creating ontology: " + e.getLocalizedMessage());
            return;
        }

        //Setting an ontology format
        TurtleDocumentFormat turtleFormat = new TurtleDocumentFormat();
        String defaultPrefix = Objects.requireNonNull(ontology.getOntologyID().getOntologyIRI().orElse(null)).getIRIString() + "#";
        turtleFormat.setPrefix(":", defaultPrefix);
        //turtleFormat.setDefaultPrefix(defaultPrefix);
        manager.setOntologyFormat(ontology, turtleFormat);

        OWLDataFactory factory = manager.getOWLDataFactory();

        //Creating an Annotation "ShortName"
        OWLAnnotationProperty annotation =  factory.getOWLAnnotationProperty("ShortName", turtleFormat);
        OWLDeclarationAxiom da = factory.getOWLDeclarationAxiom(annotation);
        ontology.add(da);

        //Creating an Annotation "Synonyms"
        annotation =  factory.getOWLAnnotationProperty("Synonyms", turtleFormat);
        da = factory.getOWLDeclarationAxiom(annotation);
        ontology.add(da);

        //Processing Filling an Ontology
        processFillingOntology(ontology, factory, turtleFormat);

        saveOntology(strOntologyPath, ontology, turtleFormat);

        //Showing an ontology info
        System.out.println("****************Ontology Info:******************");
        System.out.println("Ontology file            : " + strOntologyPath);
        System.out.println("Ontology Loaded          : " /*+ ontology*/);
        System.out.println("Ontology ID              : " + ontology.getOntologyID());
        System.out.println("Ontology IRI             : " + ontologyIRI);
        System.out.println("Format                   : " + ontology.getOWLOntologyManager().getOntologyFormat(ontology));
        System.out.println("Axioms                   : " + ontology.getAxiomCount());

        System.out.println("End of creating Classifier ontology!");
    }

    private static void createOWLClass(OntologyOWLPreClass ontologyOWLPreClass, OWLOntology ontology,
                                       OWLDataFactory factory, TurtleDocumentFormat turtleFormat) {

        String name = ontologyOWLPreClass.getQualifiedName();
        OWLClass item = factory.getOWLClass(name, turtleFormat);
        OWLDeclarationAxiom da = factory.getOWLDeclarationAxiom(item);
        ontology.add(da);

        setEntityAttributes(ontology, factory, ontologyOWLPreClass, item);

//        OWLClass person = factory.getOWLClass("Person");
//        OWLClass woman = factory.getOWLClass("Woman");
//        OWLSubClassOfAxiom w_sub_p = factory.getOWLSubClassOfAxiom(woman, person);
//        ontology.add(w_sub_p);
    }

    private static void setEntityAttributes(OWLOntology ontology, OWLDataFactory factory,
                                            OntologyOWLPreClass ontologyOWLPreClass, OWLEntity entityToChange) {
        List<OWLOntologyChange> changes = new ArrayList<>();

        OWLOntologyChange change = setEntityAnnotation("LABEL", ontology, factory, entityToChange,
                                                       ontologyOWLPreClass.getLabelRU(), "RU");
        if (change != null)
            changes.add(change);

        change = setEntityAnnotation("LABEL", ontology, factory, entityToChange,
                                     ontologyOWLPreClass.getLabelEN(), "EN");
        if (change != null)
            changes.add(change);

        change = setEntityAnnotation("COMMENT", ontology, factory, entityToChange,
                                     ontologyOWLPreClass.getDescriptionRU(), "RU");
        if (change != null)
            changes.add(change);

        change = setEntityAnnotation("COMMENT", ontology, factory, entityToChange,
                                     ontologyOWLPreClass.getDescriptionEN(), "EN");
        if (change != null)
            changes.add(change);

        change = setEntityAnnotation("ShortName", ontology, factory, entityToChange,
                                     ontologyOWLPreClass.getShortName(), "EN");
        if (change != null)
            changes.add(change);

        change = setEntityAnnotation("SYNONYMS", ontology, factory, entityToChange,
                ontologyOWLPreClass.getSynonyms(), "NA");
        if (change != null)
            changes.add(change);

        ontology.applyChanges(changes);
    }

    private static OWLOntologyChange setEntityAnnotation(String annotationType, OWLOntology ontology, OWLDataFactory factory,
                                                    OWLEntity entityToChange, String annotationText, String language) {
        if (entityToChange == null)
            return null;

        if (annotationText == null)
            return null;

        IRI entityIRI = entityToChange.getIRI();

        OWLAnnotationProperty owlAnnotationProperty = null;
        switch (annotationType) {
            case ("LABEL"):
                owlAnnotationProperty = factory.getRDFSLabel();
                break;
            case ("COMMENT"):
                owlAnnotationProperty = factory.getRDFSComment();
                break;
            case ("ShortName"):
                owlAnnotationProperty = factory.getOWLAnnotationProperty("ShortName");
                break;
            case ("SYNONYMS"):
                owlAnnotationProperty = factory.getOWLAnnotationProperty("SYNONYMS");
                break;

            default:
                break;
        }

        if (owlAnnotationProperty == null)
            return null;

        OWLAnnotation owlAnnotation = factory.getOWLAnnotation(owlAnnotationProperty, factory.getOWLLiteral(annotationText, language));

        OWLAxiom ax = factory.getOWLAnnotationAssertionAxiom(entityIRI, owlAnnotation);
        return new AddAxiom(ontology, ax);
    }

    private static void processFillingOntology(OWLOntology ontology, OWLDataFactory factory, TurtleDocumentFormat turtleFormat) {
        List<OntologyOWLPreClass> ontologyOWLPreClasses = new ArrayList<>();
        System.out.print("Creating root classes... ");
        createRootClasses(ontologyOWLPreClasses, ontology, factory, turtleFormat);
        System.out.println("root classes have been created. ");

        XSSFWorkbook myExcelBook;
        try {
            myExcelBook = new XSSFWorkbook(new FileInputStream(strExcelFile));
        } catch (IOException e) {
            System.out.println("Error reading Excel file: " + e.getLocalizedMessage());
            return;
        }

        XSSFSheet excelSheet = myExcelBook.getSheet("ALL2");
        int firstRowNum = excelSheet.getFirstRowNum();
        int lastRowNum = excelSheet.getLastRowNum();
        int rowCount = lastRowNum - firstRowNum;//Excluding title in 1-st row

        System.out.println(rowCount + " rows found, from " + (firstRowNum + 2) + " to " + (lastRowNum + 1));

        System.out.print("Reading rows and creating PreClasses... ");

        for (int i = firstRowNum + 1; i <= /*firstRowNum + 4*/lastRowNum; i++) {
            XSSFRow row = excelSheet.getRow(i);
            String name = null;
            //String hiClassName = null;
            String labels = null;
            String descriptions = null;
            String synonyms = null;
            //String shortName = null;

            XSSFCell cell = row.getCell(0);
            if (!isCellEmpty(cell) && cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                name = cell.getStringCellValue().trim();

//            cell = row.getCell(4);
//            if (!isCellEmpty(cell) && cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
//                hiClassName = cell.getStringCellValue().trim();

            cell = row.getCell(4);
            if (!isCellEmpty(cell) && cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                labels = cell.getStringCellValue().trim();

            cell = row.getCell(6);
            if (!isCellEmpty(cell) && cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                descriptions = cell.getStringCellValue().trim();

            cell = row.getCell(5);
            if (!isCellEmpty(cell) && cell.getCellType() == XSSFCell.CELL_TYPE_STRING)
                synonyms = cell.getStringCellValue().trim();

            TwoLangString twoLabels = parseProperties(labels);
            TwoLangString twoDescriptions = parseProperties(descriptions);

            OntologyOWLPreClass ontologyOWLPreClass = new OntologyOWLPreClass(name/*, hiClassName*/, twoLabels.ru, twoLabels.en,
                                                                              twoDescriptions.ru, twoDescriptions.en, synonyms);
            ontologyOWLPreClasses.add(ontologyOWLPreClass);

            createOWLClass(ontologyOWLPreClass, ontology, factory, turtleFormat);
        }
        System.out.println("creating PreClasses has finished.");

        System.out.print("Parsing PreClasses and creating Classes with relations 'SuperClass - SubClass'... ");
        for (OntologyOWLPreClass ontologyOWLPreClass : ontologyOWLPreClasses) {
            String qualifiedName = ontologyOWLPreClass.getQualifiedName();
            String superClassName = calculateSuperClassName(ontologyOWLPreClass);
            if (!superClassName.equals("")) {
                OWLClass superClass = factory.getOWLClass(superClassName, turtleFormat);
                OWLClass originalClass = factory.getOWLClass(qualifiedName, turtleFormat);
                OWLSubClassOfAxiom m_sub_w = factory.getOWLSubClassOfAxiom(originalClass, superClass);
                ontology.add(m_sub_w);
            }
        }
        System.out.println("creating Classes has finished.");

        System.out.print("Parsing PreClasses and adding relation 'Class - is a synonym of - Class'... ");
        for (OntologyOWLPreClass ontologyOWLPreClass : ontologyOWLPreClasses) {
            String qualifiedName = ontologyOWLPreClass.getQualifiedName();
            String synonyms = ontologyOWLPreClass.getSynonyms();
        }
        System.out.println("adding relation has finished.");
    }

    private static String calculateSuperClassName(OntologyOWLPreClass ontologyOWLPreClass) {

        String highestClassName = ontologyOWLPreClass.getHighestClassName();

        if (highestClassName.equals(""))
            return "";

        String result = "";
        String qualifiedName = ontologyOWLPreClass.getQualifiedName();
        int len = qualifiedName.length();
        switch (highestClassName) {
            case "FS":
                result = "FS";
                break;
            case "CO":
                //result = "CO";
                char c = qualifiedName.charAt(4);
                if (c != '_') {
                    result = qualifiedName.substring(0, 4) + "_";
                } else {
                    c = qualifiedName.charAt(3);
                    if (c != '_') {
                        result = qualifiedName.substring(0, 3) + "__";
                    } else {
                        result = "CO";
                    }
                }
                break;
            case "TS":
                //result = "TS";
                if (len == 4) {
                    if (qualifiedName.charAt(3) == '_')
                        result = "TS";
                    else
                        result = qualifiedName.substring(0, 3) + "_";
                }
                else if (len > 4) {
                    result = "TS" + qualifiedName.substring(3, 5);
                }
                break;
            case "SP":
                //result = "SP";
                if (len == 5) {
                    result = qualifiedName.substring(0, 4);
                }
                else if (len == 4) {
                    result = qualifiedName.substring(0, 3);
                }
                else if (len == 3) {
                    result = "SP";
                }
                break;
        }
        return result;
    }

    private static void createRootClasses(List<OntologyOWLPreClass> ontologyOWLPreClasses, OWLOntology ontology, OWLDataFactory factory,
                                          TurtleDocumentFormat turtleFormat) {
        OntologyOWLPreClass ontologyOWLPreClass = new OntologyOWLPreClass("CO"/*, ""*/,"Компоненты", "Components",
                "Это компоненты", "These are components", null);
        ontologyOWLPreClasses.add(ontologyOWLPreClass);
        createOWLClass(ontologyOWLPreClass, ontology, factory, turtleFormat);

        ontologyOWLPreClass = new OntologyOWLPreClass("TS"/*, ""*/, "Технические системы", "Technical Systems",
                "Это Технические системы", "These are Technical Systems", null);
        ontologyOWLPreClasses.add(ontologyOWLPreClass);
        createOWLClass(ontologyOWLPreClass, ontology, factory, turtleFormat);

        ontologyOWLPreClass = new OntologyOWLPreClass("SP"/*, ""*/, "Пространства", "Spaces",
                "Это Пространства", "These are Spaces", null);
        ontologyOWLPreClasses.add(ontologyOWLPreClass);
        createOWLClass(ontologyOWLPreClass, ontology, factory, turtleFormat);

        ontologyOWLPreClass = new OntologyOWLPreClass("FS"/*, ""*/, "Функциональные системы", "Functional Systems",
                "Это Функциональные системы", "These are Functional Systems", null);
        ontologyOWLPreClasses.add(ontologyOWLPreClass);
        createOWLClass(ontologyOWLPreClass, ontology, factory, turtleFormat);
    }

    private static boolean isCellEmpty(final XSSFCell cell) {
        if (cell == null) { // use row.getCell(x, Row.CREATE_NULL_AS_BLANK) to avoid null cells
            return true;
        }

        if (cell.getCellType() == Cell.CELL_TYPE_BLANK) {
            return true;
        }

        return cell.getCellType() == Cell.CELL_TYPE_STRING && cell.getStringCellValue().trim().isEmpty();
    }

    private static class TwoLangString
    {
        private String ru;
        private String en;
    }

    private static TwoLangString parseProperties(String str) {
        TwoLangString outObject = new TwoLangString();
        if (str != null) {
            int pos1 = str.indexOf("(");
            int pos2 = str.indexOf(")");
            String strRU = "";
            String strEN = "";
            if (pos1 != -1)
                strRU = str.substring(0, pos1 - 1).trim();
            if (pos1 != -1 && pos2 != -1)
                strEN = str.substring(pos1 + 1, pos2).trim();

            if (!strRU.equals(""))
                outObject.ru = strRU;
            if (!strEN.equals(""))
                outObject.en = strEN;
        }
        return outObject;
    }

    private static void saveOntology(String ontologyPath, @Nonnull OWLOntology o, TurtleDocumentFormat turtleFormat) {
        File fileOut = new File(ontologyPath);
        FileOutputStream oFile;
        try {
            oFile = new FileOutputStream(fileOut, false);
            o.getOWLOntologyManager().saveOntology(o, turtleFormat, oFile);
        } catch (OWLOntologyStorageException e) {
            System.out.println("Error saving ontology: " + e.getLocalizedMessage());
        } catch (FileNotFoundException e) {
            System.out.println("Error creating an ontology file: " + e.getLocalizedMessage());
        }
    }
}

