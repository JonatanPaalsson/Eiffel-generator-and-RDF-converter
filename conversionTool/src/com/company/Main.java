package com.company;

import java.io.*;
import java.nio.channels.FileChannel;
import java.sql.Array;
import java.util.*;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import org.apache.jena.Jena;
import org.apache.jena.atlas.lib.Sink;
import org.apache.jena.base.Sys;
import org.apache.jena.datatypes.xsd.impl.RDFhtml;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.WriterDatasetRIOT;
import org.apache.jena.riot.out.SinkTripleOutput;
import org.apache.jena.riot.system.PrefixMap;
import org.apache.jena.riot.system.Prologue;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.system.SyntaxLabels;
import org.apache.jena.sparql.util.NodeFactoryExtra;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.riot.out.NodeToLabel;
import sun.plugin.com.JavaClass;
import java.lang.Long;
import javax.json.Json;

import static org.apache.jena.assembler.JA.PrefixMapping;
import static org.apache.jena.assembler.JA.prefixMapping;


public class Main {
    public static int a;
    public static int b;
    public static ArrayList<String> idArray = new ArrayList<String>(30000000);
    public static ArrayList<Integer> s = new ArrayList<Integer>(30000000);
    public static ArrayList<Integer> t = new ArrayList<Integer>(30000000);
    public static ArrayList<String> type = new ArrayList<String>(30000000);
    public static NodeFactory nodeFactory = new NodeFactory();
    public static Model m = ModelFactory.createDefaultModel();



    public static void main(String[] args) {
        System.out.println("Starting program");
        for (String argument : args){
            System.out.println(argument);
            try {
                try (PrintWriter p = new PrintWriter(new FileOutputStream("C:\\Users\\jonpa22\\Documents\\Exjobb\\Default reference data set\\Generator\\eiffel\\examples\\reference-data-sets\\default\\examples\\" + argument + ".ttl"))) {
                    p.println("@prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> .");
                    p.println("@prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#> .");
                    p.println("@prefix xsd:  <http://www.w3.org/2001/XMLSchema#> .");
                    p.println("@prefix eiffel: <https://w3id.org/eiffel/RDFvocab/main#> .");
                    p.println("@prefix eiffellink: <https://w3id.org/eiffel/RDFvocab/links#> .");
                    p.println("");

                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }

                InputStream fileStream = new FileInputStream("C:\\Users\\jonpa22\\Documents\\Exjobb\\Default reference data set\\Generator\\eiffel\\examples\\reference-data-sets\\default\\examples\\" + argument + ".json");
                JsonReader reader = new JsonReader(new InputStreamReader(fileStream, "UTF-8"));
                Gson gson = new GsonBuilder().create();

                reader.beginArray();
                Main.a = 0;
                System.out.println("before");

                while (reader.hasNext()) {
                    jsonFormat jsonform = gson.fromJson(reader, jsonFormat.class);
                    convertToRDF(jsonform, argument);
                    //prepareForMatlab(jsonform);
                    a++;
                }
                //writeForMatlab();
                reader.endArray();
                reader.close();


            } catch (FileNotFoundException ex) {
                System.out.println("Could not open file");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void convertToRDF(jsonFormat jsonObject, String argument) {

            ArrayList<Triple> tripleList = new ArrayList<Triple>();
            Node idNode = nodeFactory.createURI("urn:uuid:" + jsonObject.getMeta().getId());

            tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:version"), nodeFactory.createLiteral(jsonObject.getMeta().getVersion())));
            tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:domain"), nodeFactory.createLiteral(jsonObject.getMeta().getSource().getDomainId())));
            tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:iteration"), nodeFactory.createLiteral(jsonObject.getData().getCustomData().get(1).getValue())));
            tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:time"), nodeFactory.createLiteral(Long.toString(jsonObject.getMeta().getTime()))));

            tripleList.add(Triple.create(idNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:"+jsonObject.getMeta().getType().substring(6))));



            for (jsonFormat.LinkTypes link : jsonObject.getLinks()){
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffellink:" + link.getType().toLowerCase()), nodeFactory.createURI("urn:uuid:" + link.getTarget())));
            }

            ArrayList<Triple> dataTripleList = dataToTriples(idNode, jsonObject);

            tripleList.addAll(dataTripleList);
            for (Triple triple : tripleList){
                writeTripleToFile(triple, argument);

            }
            try (PrintWriter p = new PrintWriter(new FileOutputStream("C:\\Users\\jonpa22\\Documents\\Exjobb\\Default reference data set\\Generator\\eiffel\\examples\\reference-data-sets\\default\\examples\\" + argument + ".ttl", true))) {
                p.println("");

            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            }
    }

    public static void writeTripleToFile(Triple triple, String argument) {
        try (PrintWriter p = new PrintWriter(new FileOutputStream("C:\\Users\\jonpa22\\Documents\\Exjobb\\Default reference data set\\Generator\\eiffel\\examples\\reference-data-sets\\default\\examples\\" + argument + ".ttl", true))) {

            if (triple.getSubject().isBlank()){

                //Here I get the actual blankNodeID
                //p.println(triple.getSubject().getBlankNodeLabel() + " " + triple.getPredicate() + " " + triple.getObject() + " .");
                p.println("_:bl" + triple.getSubject() + " " + triple.getPredicate() + " " + triple.getObject() + " .");
            }
            else if (triple.getObject().isBlank()){
                //Here I get the actual blankNodeID
                //p.println("<" +triple.getSubject() + "> " + triple.getPredicate() + " " + triple.getObject() + " .");
                p.println("<" +triple.getSubject() + "> " + triple.getPredicate() + " _:bl" + triple.getObject() + " .");

            }
            else if (triple.getObject().isLiteral() && triple.getSubject().getURI().startsWith("urn")) {
                if (triple.getPredicate().getURI().equals("eiffel:time")){
                    p.println("<" + triple.getSubject() + "> " + triple.getPredicate() + " " + triple.getObject() + "^^xsd:long .");
                }
                else if (triple.getPredicate().getURI().equals("eiffel:iteration")){
                    p.println("<" + triple.getSubject() + "> " + triple.getPredicate() + " " + Integer.parseInt(triple.getObject().getLiteralLexicalForm()) + " .");
                }
                else {
                    p.println("<" + triple.getSubject() + "> " + triple.getPredicate() + " " + triple.getObject() + " .");
                }
            }
            else if(triple.getSubject().getURI().startsWith("urn") && triple.getObject().getURI().startsWith("urn")){
                p.println("<" + triple.getSubject() + "> " + triple.getPredicate() + " <" + triple.getObject() + "> .");
            }
            else {
                p.println("<" + triple.getSubject() + "> " + triple.getPredicate() + " " + triple.getObject().toString() + " .");
            }
            b++;

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
    }

    public static ArrayList<Triple> dataToTriples(Node idNode, jsonFormat jsonObject){
        ArrayList<Triple> tripleList = new ArrayList<Triple>();
        switch (jsonObject.getMeta().getType()) {
            case "EiffelActivityFinishedEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:outcomeConclusion"), nodeFactory.createLiteral(jsonObject.getData().getOutcome().getConclusion())));
                break;
            case "EiffelActivityStartedEvent":
                break;
            case "EiffelActivityTriggeredEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:name"), nodeFactory.createLiteral(jsonObject.getData().getName())));
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:executionType"), nodeFactory.createLiteral(jsonObject.getData().getExecutionType())));
                for(String categories: jsonObject.getData().getCategories()){
                    tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:category"), nodeFactory.createLiteral(categories)));
                }
                for(Data.Triggers triggers: jsonObject.getData().getTriggers()){
                    tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:trigger"), nodeFactory.createLiteral(triggers.getType())));
                }
                break;
            case "EiffelArtifactCreatedEvent":
                Node gavNode = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:gav"), gavNode));
                tripleList.add(Triple.create(gavNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Gav")));

                tripleList.add(Triple.create(gavNode, nodeFactory.createURI("eiffel:groupId"), nodeFactory.createLiteral(jsonObject.getData().getGav().getGroupId())));
                tripleList.add(Triple.create(gavNode, nodeFactory.createURI("eiffel:artifactId"), nodeFactory.createLiteral(jsonObject.getData().getGav().getArtifactId())));
                tripleList.add(Triple.create(gavNode, nodeFactory.createURI("eiffel:gavVersion"), nodeFactory.createLiteral(jsonObject.getData().getGav().getVersion())));

                for(Data.FileInformation fileinfo : jsonObject.getData().getFileInformation()){
                    Node fileInfoNode = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                    tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:fileinfo"), fileInfoNode));
                    tripleList.add(Triple.create(fileInfoNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:FileInfo")));
                    tripleList.add(Triple.create(fileInfoNode, nodeFactory.createURI("eiffel:classifier"), nodeFactory.createLiteral(fileinfo.getClassifier())));
                    tripleList.add(Triple.create(fileInfoNode, nodeFactory.createURI("eiffel:extension"), nodeFactory.createLiteral(fileinfo.getExtension())));
                }
               break;
            case "EiffelArtifactPublishedEvent":
                for(Data.Locations locations : jsonObject.getData().getLocations()){
                    Node locationNode = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                    tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:location"), locationNode));
                    tripleList.add(Triple.create(locationNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Location")));

                    tripleList.add(Triple.create(locationNode, nodeFactory.createURI("eiffel:locationType"), nodeFactory.createLiteral(locations.getType())));
                    tripleList.add(Triple.create(locationNode, nodeFactory.createURI("eiffel:locationURI"), nodeFactory.createLiteral(locations.getUri())));
                }
                break;
            case "EiffelCompositionDefinedEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:name"), nodeFactory.createLiteral(jsonObject.getData().getName())));
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:eventVersion"), nodeFactory.createLiteral(jsonObject.getData().getVersion())));
                break;
            case "EiffelConfidenceLevelModifiedEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:name"), nodeFactory.createLiteral(jsonObject.getData().getName())));
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:value"), nodeFactory.createLiteral(jsonObject.getData().getValue())));
                break;
            case "EiffelEnvironmentDefinedEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:name"), nodeFactory.createLiteral(jsonObject.getData().getName())));
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:eventVersion"), nodeFactory.createLiteral(jsonObject.getData().getVersion())));
                break;
            case "EiffelSourceChangeCreatedEvent":
                Node authorNode = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                Node gitNode =nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                Node changeNode =nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());

                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:author"), authorNode));

                tripleList.add(Triple.create(authorNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Author")));
                tripleList.add(Triple.create(authorNode, nodeFactory.createURI("eiffel:authorName"), nodeFactory.createLiteral(jsonObject.getData().getAuthor().getName())));
                tripleList.add(Triple.create(authorNode, nodeFactory.createURI("eiffel:authorEmail"), nodeFactory.createLiteral(jsonObject.getData().getAuthor().getEmail())));
                tripleList.add(Triple.create(authorNode, nodeFactory.createURI("eiffel:authorId"), nodeFactory.createLiteral(jsonObject.getData().getAuthor().getId())));
                tripleList.add(Triple.create(authorNode, nodeFactory.createURI("eiffel:authorGroup"), nodeFactory.createLiteral(jsonObject.getData().getAuthor().getGroup())));

                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:git"), gitNode));

                tripleList.add(Triple.create(gitNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Git")));
                tripleList.add(Triple.create(gitNode, nodeFactory.createURI("eiffel:gitBranch"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getBranch())));
                tripleList.add(Triple.create(gitNode, nodeFactory.createURI("eiffel:gitCommitId"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getCommitId())));
                tripleList.add(Triple.create(gitNode, nodeFactory.createURI("eiffel:gitRepoURI"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getRepoUri())));
                tripleList.add(Triple.create(gitNode, nodeFactory.createURI("eiffel:gitRepoName"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getRepoUri())));

                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:change"), changeNode));

                tripleList.add(Triple.create(changeNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Change")));
                tripleList.add(Triple.create(changeNode, nodeFactory.createURI("eiffel:changeInsertions"), nodeFactory.createLiteral(Integer.toString(jsonObject.getData().getChange().getInsertions()))));
                tripleList.add(Triple.create(changeNode, nodeFactory.createURI("eiffel:changeDeletions"), nodeFactory.createLiteral(Integer.toString(jsonObject.getData().getChange().getDeletions()))));
                tripleList.add(Triple.create(changeNode, nodeFactory.createURI("eiffel:changeFiles"), nodeFactory.createLiteral(jsonObject.getData().getChange().getFiles())));
                break;
            case "EiffelSourceChangeSubmittedEvent":
                Node submitterNode = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:submitter"), submitterNode));
                tripleList.add(Triple.create(submitterNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Submitter")));
                tripleList.add(Triple.create(submitterNode, nodeFactory.createURI("eiffel:submitterName"), nodeFactory.createLiteral(jsonObject.getData().getSubmitter().getName())));
                tripleList.add(Triple.create(submitterNode, nodeFactory.createURI("eiffel:submitterEmail"), nodeFactory.createLiteral(jsonObject.getData().getSubmitter().getEmail())));
                tripleList.add(Triple.create(submitterNode, nodeFactory.createURI("eiffel:submitterId"), nodeFactory.createLiteral(jsonObject.getData().getSubmitter().getId())));
                tripleList.add(Triple.create(submitterNode, nodeFactory.createURI("eiffel:submitterGroup"), nodeFactory.createLiteral(jsonObject.getData().getSubmitter().getGroup())));

                Node gitNode2 = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:git"), gitNode2));
                tripleList.add(Triple.create(gitNode2, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:Git")));
                tripleList.add(Triple.create(gitNode2, nodeFactory.createURI("eiffel:gitBranch"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getBranch())));
                tripleList.add(Triple.create(gitNode2, nodeFactory.createURI("eiffel:gitCommitId"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getCommitId())));
                tripleList.add(Triple.create(gitNode2, nodeFactory.createURI("eiffel:gitRepoURI"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getRepoUri())));
                tripleList.add(Triple.create(gitNode2, nodeFactory.createURI("eiffel:gitRepoName"), nodeFactory.createLiteral(jsonObject.getData().getGitIdentifier().getRepoUri())));

                break;
            case "EiffelTestCaseFinishedEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:outcomeConclusion"), nodeFactory.createLiteral(jsonObject.getData().getOutcome().getConclusion())));
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:outcomeVerdict"), nodeFactory.createLiteral(jsonObject.getData().getOutcome().getVerdict())));
                break;
            case "EiffelTestCaseStartedEvent":
                break;
            case "EiffelTestCaseTriggeredEvent":
                Node testCaseNode = nodeFactory.createURI("urn:uuid:" +  UUID.randomUUID());
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:testCase"), testCaseNode));
                tripleList.add(Triple.create(testCaseNode, nodeFactory.createURI("rdf:type"), nodeFactory.createURI("eiffel:TestCase")));
                tripleList.add(Triple.create(testCaseNode, nodeFactory.createURI("eiffel:testCaseTracker"), nodeFactory.createLiteral(jsonObject.getData().getTestCase().getTracker())));
                tripleList.add(Triple.create(testCaseNode, nodeFactory.createURI("eiffel:testCaseId"), nodeFactory.createLiteral(jsonObject.getData().getTestCase().getId())));
                tripleList.add(Triple.create(testCaseNode, nodeFactory.createURI("eiffel:testCaseURI"), nodeFactory.createLiteral(jsonObject.getData().getTestCase().getUri())));
                break;
            case "EiffelTestSuiteFinishedEvent":
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:outcomeVerdict"), nodeFactory.createLiteral(jsonObject.getData().getOutcome().getVerdict())));
                break;
            case "EiffelTestSuiteStartedEvent":
                for(String type : jsonObject.getData().getTypes()){
                    tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:testSuiteType"), nodeFactory.createLiteral(type)));
                }
                for(String categories: jsonObject.getData().getCategories()){
                    tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:category"), nodeFactory.createLiteral(categories)));
                }
                tripleList.add(Triple.create(idNode, nodeFactory.createURI("eiffel:name"), nodeFactory.createLiteral(jsonObject.getData().getName())));
                break;
            default:
                System.out.println(jsonObject.getMeta().getType());

        }



        return tripleList;
    }

    public static void prepareForMatlab(jsonFormat jsonObject){
            idArray.add(jsonObject.getMeta().getType());

        if (jsonObject.getLinks().size() == 0) {

        }

        for (jsonFormat.LinkTypes linkz : jsonObject.getLinks()) {
            s.add(idArray.indexOf(jsonObject.getMeta().getId())+1);
            t.add(idArray.indexOf(linkz.getTarget())+1);
            type.add(linkz.getType());
        }
    }

    public static void writeForMatlab(){
        System.out.println("let's go");
        System.out.println(idArray.size());
        ArrayList<Integer> dups = new ArrayList<Integer>(30000000);
        int length = s.size();
        for(int i = 0; i<length; i++){
            int k= i+1;
            if (k==length){
                break;
            }

            while(s.get(i).equals(s.get(k))){
                if (t.get(i).equals(t.get(k))) {
                    dups.add(0,i);
                    k++;
                } else {
                    k++;
                }
                if (k==length){
                    break;
                }
            }
        }
        System.out.println("number of nodes: " +  idArray.size());
        int kyy = 0;
        for (String element : idArray){
            System.out.println(kyy + ": " + element);
            kyy++;
        }

        System.out.println("number of edges: " + s.size());
        System.out.println("number of dups: " + dups.size());

        for (Integer duplicate : dups){
            s.remove(duplicate+0);
            t.remove(duplicate+0);
            type.remove(duplicate+0);
        }

        length = s.size();

        for(int i = 0; i<length; i++){
            int k = i+1;
            if (k==length){
                break;
            }
            while(s.get(i).equals(s.get(k))){
                if (k<length){
                    break;
                }
                if (t.get(i).equals(t.get(k))) {
                    System.out.println("DUPLICATE" + s.get(i));
                    k++;
                } else {
                    k++;
                }
            }
        }

        System.out.println(s.size() + " " + t.size() + " " + type.size());

            /*for (int i = 0; i<s.size(); i++){
                //System.out.println("pair: " + s.get(i) + " " + t.get(i));
                if (i+1 < s.size() && s.get(i).equals(s.get(i+1))) {
                    System.out.println("dup: " + s.get(i) + " " + t.get(i));
                    System.out.println("dup2: " + s.get(i+1) + " " + t.get(i+1));
                    System.out.println( " ");
                    if(t.get(i).equals(t.get(i+1))){
                        System.out.println("superdups: " + s.get(i));
                    }
                }
            }*/
        String nodes = "{";
        for (int k = 0; k<idArray.size(); k++) {
            nodes +="'" + k + "'" + ", ";
        }
        nodes = nodes.substring(0, nodes.length() - 2);
        nodes += "}";

        String typeString = "{";
        for (String typez : type){
            typeString += "'" + typez + "'" + ", ";
        }
        typeString = typeString.substring(0, typeString.length() - 2);
        typeString += "}";



        System.out.println(nodes);
        System.out.println(s);
        System.out.println(t);
        System.out.println(typeString);
    }

}