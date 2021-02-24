package com.company;

import java.util.List;

public class Data {
    private List<CustomData> customData;
    private Outcome outcome;
    private String name;
    private String executionType;
    private List<Triggers> triggers;
    private List<String> categories;
    private List<FileInformation> fileInformation;
    private Gav gav;
    private List<Locations> locations;
    private String version;
    private String value;
    private Author author;
    private Change change;
    private GitIdentifier gitIdentifier;
    private Author submitter;
    private TestCase testCase;
    private List<String> types;


    public List<CustomData> getCustomData() {
        return customData;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public String getName() {
        return name;
    }

    public String getExecutionType() {
        return executionType;
    }

    public List<Triggers> getTriggers() {
        return triggers;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<FileInformation> getFileInformation() {
        return fileInformation;
    }

    public Gav getGav() {
        return gav;
    }

    public List<Locations> getLocations() {
        return locations;
    }

    public String getVersion() {
        return version;
    }

    public String getValue() {
        return value;
    }

    public Author getAuthor() {
        return author;
    }

    public Change getChange() {
        return change;
    }

    public GitIdentifier getGitIdentifier() {
        return gitIdentifier;
    }

    public Author getSubmitter() {
        return submitter;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public List<String> getTypes() {
        return types;
    }

    public class CustomData{
        private String key;
        private String value;

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }
    }

    public class Outcome{
        private String conclusion;
        private String verdict;

        public String getConclusion() {
            return conclusion;
        }

        public String getVerdict() {
            return verdict;
        }
    }

    public class Triggers {
        private String type;

        public String getType() {
            return type;
        }
    }

    public class FileInformation{
        private String extension;
        private String classifier;

        public String getExtension() {
            return extension;
        }

        public String getClassifier() {
            return classifier;
        }
    }

    public class Gav{
        private String groupId;
        private String artifactId;
        private String version;

        public String getGroupId() {
            return groupId;
        }

        public String getArtifactId() {
            return artifactId;
        }

        public String getVersion() {
            return version;
        }
    }

    public class Locations{
        private String type;
        private String uri;

        public String getType() {
            return type;
        }

        public String getUri() {
            return uri;
        }
    }

    public class Author {
        private String name;
        private String email;
        private String id;
        private String group;

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getId() {
            return id;
        }

        public String getGroup() {
            return group;
        }
    }

    public class Change {
        private int insertions;
        private int deletions;
        private String files;

        public int getInsertions() {
            return insertions;
        }

        public int getDeletions() {
            return deletions;
        }

        public String getFiles() {
            return files;
        }
    }

    public class GitIdentifier {
        private String branch;
        private String commitId;
        private String repoUri;
        private String repoName;

        public String getBranch() {
            return branch;
        }

        public String getCommitId() {
            return commitId;
        }

        public String getRepoUri() {
            return repoUri;
        }

        public String getRepoName() {
            return repoName;
        }
    }

    public class TestCase {
        private String tracker;
        private String id;
        private String uri;

        public String getTracker() {
            return tracker;
        }

        public String getId() {
            return id;
        }

        public String getUri() {
            return uri;
        }
    }
}
