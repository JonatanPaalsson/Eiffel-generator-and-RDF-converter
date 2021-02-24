package com.company;


import java.util.List;

public class jsonFormat {
    private Meta meta;
    private List<LinkTypes> links;
    private Data data;

    public Meta getMeta() {
        return meta;
    }

    public List<LinkTypes> getLinks() {
        return links;
    }

    public Data getData() {
        return data;
    }

    public class LinkTypes {
        private String type;
        private String target;

        public String getType() {
            return type;
        }

        public String getTarget() {
            return target;
        }
    }
}
