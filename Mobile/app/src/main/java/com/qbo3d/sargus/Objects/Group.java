package com.qbo3d.sargus.Objects;

public class Group {
    private String id;
    private String entities_id;
    private String name;
    private String comment;

    public Group() {
    }

    public Group(String id, String entities_id, String name, String comment) {
        this.id = id;
        this.entities_id = entities_id;
        this.name = name;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntities_id() {
        return entities_id;
    }

    public void setEntities_id(String entities_id) {
        this.entities_id = entities_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
