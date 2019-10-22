package kz.kazniisa;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.InputStream;
import java.util.*;

public class Launcher {

    private enum Modificator { CAPITALISE, DECAPITALISE, DONT_TOUCH }

    private static char delimiter = '#';

    private static OWLOntologyManager manager;
    private static OWLDataFactory factory;
    private static OWLOntology ontology;
    private static int changesPerformed = 0;
    private static int currentID = 100001000;

    public static void main(String[] args) throws OWLException,InstantiationException, IllegalAccessException, ClassNotFoundException {
        System.out.println("Begin...");

        manager = OWLManager.createOWLOntologyManager();

        @Nonnull
        String ontologyFilePath = "E:\\owl_data\\Онтология НПА_в5.owl";//args[0];
        System.out.println("Ontology file            : " + ontologyFilePath);

        File inFile = new File(ontologyFilePath);
        IRI documentIRI = IRI.create(inFile);
        //OWLOntology ontology = manager.loadOntologyFromOntologyDocument(inFile);
        ontology = manager.loadOntologyFromOntologyDocument(documentIRI);
        IRI ontologyIRI = ontology.getOntologyID().getOntologyIRI().get();

        System.out.println("Ontology Loaded          : " /*+ ontology*/);
        System.out.println("Document IRI             : " + documentIRI);
        System.out.println("Ontology                 : " + ontology.getOntologyID());
        System.out.println("Ontology IRI             : " + ontologyIRI);
        System.out.println("Format                   : " + manager.getOntologyFormat(ontology));
        System.out.println("Axioms                   : " + ontology.getAxiomCount());

        factory = manager.getOWLDataFactory();

        final Map<OWLEntity, IRI> entity2IRIMap = new HashMap<>();
        final List<OWLEntity> entitiesToChange = new ArrayList<>();


//        OWLEntity entityToRename = factory.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#авторское_право11"));
//        changesPerformed += renameEntity(entityToRename, IRI.create(ontologyIRI + "#авторское_право"));

//        OWLEntity entityToChange = factory.getOWLEntity(EntityType.CLASS, IRI.create(ontologyIRI + "#авторское_право"));
//        changesPerformed += setLabelWithIDValue(entityToChange, "ru");

//*****Modifying Individuals******************************************************************
        System.out.println("Individuals quantity     : " + ontology.individualsInSignature().count());
        ontology.individualsInSignature().forEach(toChange ->
        {
            final IRI iri = IRI.create(ontologyIRI.getIRIString() + delimiter + composeNewID("NPA_"));
            entity2IRIMap.put(toChange, iri);
            entitiesToChange.add(toChange);
        });

        changesPerformed += setLabelsWithIDValues(entitiesToChange, "ru", Modificator.DECAPITALISE, true);
        changesPerformed += renameEntities(entity2IRIMap);
//*****Modifying Individuals******************************************************************

//*****Modifying ObjectProperties*************************************************************
        System.out.println("ObjectProperties quantity: " + ontology.objectPropertiesInSignature().count());
        entity2IRIMap.clear();
        entitiesToChange.clear();
        ontology.objectPropertiesInSignature().forEach(toChange ->
        {
            final IRI iri = IRI.create(ontologyIRI.getIRIString() + delimiter + composeNewID("NPA_"));
            entity2IRIMap.put(toChange, iri);
            entitiesToChange.add(toChange);
        });

        changesPerformed += setLabelsWithIDValues(entitiesToChange, "ru", Modificator.DECAPITALISE, true);
        changesPerformed += renameEntities(entity2IRIMap);
//*****Modifying ObjectProperties*************************************************************

//*****Modifying Classes**********************************************************************
        System.out.println("Classes quantity         : " + ontology.classesInSignature().count());
        entity2IRIMap.clear();
        entitiesToChange.clear();
        ontology.classesInSignature().forEach(toChange ->
        {
            final IRI iri = IRI.create(ontologyIRI.getIRIString() + delimiter + composeNewID("NPA_"));
            entity2IRIMap.put(toChange, iri);
            entitiesToChange.add(toChange);
        });

        changesPerformed += setLabelsWithIDValues(entitiesToChange, "ru", Modificator.CAPITALISE, true);
        changesPerformed += renameEntities(entity2IRIMap);
//*****Modifying Classes**********************************************************************

        System.out.println("Performed changes        : " + changesPerformed);
        System.out.println("The lasi ID was          : " + currentID);
        if (changesPerformed > 0) {
            manager.saveOntology(ontology);
            System.out.println("Ontology Saved!");

        }

    }


    private static int setLabelsWithIDValues(List<OWLEntity> entitiesToChange, String language,  Modificator modyficator, boolean replace_) {
        if ((entitiesToChange == null) || (entitiesToChange.size() == 0))
            return 0;

        int performedChanges = 0;
        List<OWLOntologyChange> changes = new ArrayList<>();

        for (OWLEntity entity : entitiesToChange) {
            IRI entityIRI = entity.getIRI();
            String stringLabel = entityIRI.getIRIString();
            stringLabel = transformLabel(stringLabel, modyficator, replace_);

            OWLAnnotation labelAnnotation = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(stringLabel, language));
            OWLAxiom ax1 = factory.getOWLAnnotationAssertionAxiom(entityIRI, labelAnnotation);
            changes.add(new AddAxiom(ontology, ax1));
            performedChanges++;
        }

        manager.applyChanges(changes);

        return performedChanges;
    }

    private static int setLabelWithIDValue(OWLEntity entityToChange, String language,  Modificator modyficator, boolean replace_) {
        if (entityToChange == null)
            return 0;

        IRI entityIRI = entityToChange.getIRI();
        String stringLabel = entityIRI.getIRIString();
        stringLabel = transformLabel(stringLabel, modyficator, replace_);

        OWLAnnotation labelAnnotation = factory.getOWLAnnotation(factory.getRDFSLabel(), factory.getOWLLiteral(stringLabel, language));
        OWLAxiom ax1 = factory.getOWLAnnotationAssertionAxiom(entityIRI, labelAnnotation);
        manager.applyChange(new AddAxiom(ontology, ax1));

        return 1;
    }

    private static int renameEntities(Map<OWLEntity, IRI> entity2IRIMap) {
        if ((entity2IRIMap == null) || (entity2IRIMap.size() == 0))
            return 0;

        OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));

        final List<OWLOntologyChange> changes;

        changes = owlEntityRenamer.changeIRI(entity2IRIMap);
        int changesSize = changes.size();
        if (changesSize > 0)
            manager.applyChanges(changes);
            //ontology.applyChanges(changes);

        return  changesSize;
    }

    private static int renameEntity(OWLEntity entityToRename, IRI newNameIRI) {
        if ((newNameIRI == null) | (entityToRename == null))
            return 0;

        OWLEntityRenamer owlEntityRenamer = new OWLEntityRenamer(manager, Collections.singleton(ontology));
        final List<OWLOntologyChange> changes;
        changes = owlEntityRenamer.changeIRI(entityToRename.getIRI(), newNameIRI);
        int changesSize = changes.size();
        if (changesSize > 0)
            manager.applyChanges(changes);

        return  changesSize;
    }


    private static String transformLabel(String strLabel, Modificator modyficator, boolean replace_) {
        String stringLabel = getShortForm(strLabel);

        if (replace_)
            stringLabel = stringLabel.replace('_', ' ');

        stringLabel = stringLabel.trim();

        switch (modyficator) {
            case CAPITALISE:
                stringLabel = stringLabel.substring(0, 1).toUpperCase() + stringLabel.substring(1);
                break;

            case DECAPITALISE:
                stringLabel = stringLabel.substring(0, 1).toLowerCase() + stringLabel.substring(1);
                break;

            case DONT_TOUCH:
            default:
                break;
        }

        return stringLabel;
    }

    private static boolean owlClassNameStartsWithP(OWLEntity e) {
        return !e.isBuiltIn()&&e.getIRI().getRemainder().orElse("").startsWith("P");
    }

    private static void increaseChangesPerformed(int value) {
        changesPerformed += value;
    }

    private static String composeNewID(String prefix) {
        return prefix + (++currentID);
    }

    private static String getShortForm(String str) {
        return str.substring(str.lastIndexOf(delimiter) + 1);
    }

/*
        EntityType.CLASS.OBJECT_PROPERTY.ANNOTATION_PROPERTY.DATA_PROPERTY.DATATYPE.NAMED_INDIVIDUAL
        ontology.getClassesInSignature(); //deprecated
        ontology.individualsInSignature().forEach(System.out::println);

        for(OWLEntity entity : ontology.getIndividualsInSignature()) {
            System.out.println(entity.getEntityType() + " - " + entity.getIRI().getShortForm());
        }


        ontology.individualsInSignature().forEach(toChangeLabel ->
        {
            final int changes = setLabelWithIDValue(toChangeLabel, "ru", Modificator.DECAPITALISE, true);
            increaseChangesPerformed(changes);
        });
*/
}

