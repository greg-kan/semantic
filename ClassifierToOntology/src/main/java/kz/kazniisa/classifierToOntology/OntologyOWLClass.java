package kz.kazniisa.classifierToOntology;

import java.util.Set;

public class OntologyOWLClass {
    private String qualifiedName;
    private String labelRU;
    private String labelEN;
    private String descriptionRU;
    private String descriptionEN;
    private Set<OntologyOWLClass> directSubClassOf;
    private Set<OntologyOWLClass> synonyms;
    private Set<OntologyOWLClass> antonyms;
    private Set<OntologyOWLClass> directSubClasses;

    public String getQualifiedName() {
        return qualifiedName;
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
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

    public Set<OntologyOWLClass> getDirectSubClassOf() {
        return directSubClassOf;
    }

    public void setDirectSubClassOf(Set<OntologyOWLClass> directSubClassOf) {
        this.directSubClassOf = directSubClassOf;
    }

    public Set<OntologyOWLClass> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(Set<OntologyOWLClass> synonyms) {
        this.synonyms = synonyms;
    }

    public Set<OntologyOWLClass> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(Set<OntologyOWLClass> antonyms) {
        this.antonyms = antonyms;
    }

    public Set<OntologyOWLClass> getDirectSubClasses() {
        return directSubClasses;
    }

    public void setDirectSubClasses(Set<OntologyOWLClass> directSubClasses) {
        this.directSubClasses = directSubClasses;
    }

    public OntologyOWLClass(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public OntologyOWLClass(String qualifiedName, String labelRU) {
        this.qualifiedName = qualifiedName;
        this.labelRU = labelRU;
    }

    public OntologyOWLClass(String qualifiedName, String labelRU, String labelEN) {
        this.qualifiedName = qualifiedName;
        this.labelRU = labelRU;
        this.labelEN = labelEN;
    }

    public OntologyOWLClass(String qualifiedName, String labelRU, String labelEN, String descriptionRU) {
        this.qualifiedName = qualifiedName;
        this.labelRU = labelRU;
        this.labelEN = labelEN;
        this.descriptionRU = descriptionRU;
    }

    public OntologyOWLClass(String qualifiedName, String labelRU, String labelEN, String descriptionRU, String descriptionEN) {
        this.qualifiedName = qualifiedName;
        this.labelRU = labelRU;
        this.labelEN = labelEN;
        this.descriptionRU = descriptionRU;
        this.descriptionEN = descriptionEN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OntologyOWLClass)) return false;

        OntologyOWLClass that = (OntologyOWLClass) o;

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
