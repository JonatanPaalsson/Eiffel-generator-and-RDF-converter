package com.company;

public class Meta {
    private String id;
    private String type;
    private String version;
    private long time;
    private Source source;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
    public class Source {
        private String domainId;

        public String getDomainId() {
            return domainId;
        }

        public void setDomainID(String domainId) {
            this.domainId = domainId;
        }
    }
}
