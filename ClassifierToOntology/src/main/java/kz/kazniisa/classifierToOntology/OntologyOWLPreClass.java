package kz.kazniisa.classifierToOntology;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class OntologyOWLPreClass {
    private String qualifiedName;
    private String highestClassName;
    private String labelRU;
    private String labelEN;
    private String descriptionRU;
    private String descriptionEN;
    private String shortName;
    private String synonyms;

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getHighestClassName() {
        return highestClassName;
    }

    public void setHighestClassName(String highestClassName) {
        this.highestClassName = highestClassName;
    }

    public String getLabelRU() {
        return labelRU;
    }

    public void setLabelRU(String labelRU) {
        this.labelRU = labelRU;
    }

    public String getLabelEN() {
        return labelEN;
    }

    public void setLabelEN(String labelEN) {
        this.labelEN = labelEN;
    }

    public String getDescriptionRU() {
        return descriptionRU;
    }

    public void setDescriptionRU(String descriptionRU) {
        this.descriptionRU = descriptionRU;
    }

    public String getDescriptionEN() {
        return descriptionEN;
    }

    public void setDescriptionEN(String descriptionEN) {
        this.descriptionEN = descriptionEN;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(String synonyms) {
        this.synonyms = synonyms;
    }

    public OntologyOWLPreClass(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public OntologyOWLPreClass(String qualifiedName,/* String highestClassName,*/ String labelRU, String labelEN, String descriptionRU, String descriptionEN, String synonyms) {
        this.qualifiedName = qualifiedName;
        this.labelRU = labelRU;
        this.labelEN = labelEN;
        this.descriptionRU = descriptionRU;
        this.descriptionEN = descriptionEN;
        this.synonyms = synonyms;

        List<String> rootClasses = Arrays.asList("CO", "FS", "SP", "TS");
        if (rootClasses.contains(qualifiedName)) {
            this.highestClassName = "";
            this.shortName = qualifiedName;
        }
        else {
            this.highestClassName = qualifiedName.substring(0, 2);
            this.shortName = qualifiedName.substring(2);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OntologyOWLPreClass)) return false;

        OntologyOWLPreClass that = (OntologyOWLPreClass) o;

        return qualifiedName.equals(that.qualifiedName);
    }

    @Override
    public int hashCode() {
        return qualifiedName.hashCode();
    }

    @Override
    public String toString() {
        return "OntologyOWLClass{" +
                "qualifiedName='" + qualifiedName + '\'' +
                '}';
    }
}
