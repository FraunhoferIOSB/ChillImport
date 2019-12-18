package com.chillimport.server.entities;


/**
 * describes an Entity of the SensorThings standard
 */
public class Entity {

    private String name;
    private String description;
    private String frostId;

    public Entity() {
    }

    /**
     * creates a new Entity with no frostId
     *
     * @param NAME        name of the Entity
     * @param DESCRIPTION name of the Entity
     */
    public Entity(String NAME, String DESCRIPTION) {
        this.name = NAME;
        this.description = DESCRIPTION;
        this.frostId = null;
    }

    /**
     * returns the name of the Entity
     *
     * @return name of the Entity
     */
    public String getName() {
        return name;
    }

    /**
     * returns the name of the Entity
     *
     * @return name of the Entity
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the name of the Entity
     *
     * @return name of the Entity
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    /**
     * returns the frostId of the Entity
     *
     * @return frostId of the Entity
     */
    public String getFrostId() {
        return frostId;
    }

    /**
     * sets the frostId of the Entity
     *
     * @param frostId frostId to be set
     */
    public void setFrostId(String frostId) {
        this.frostId = frostId;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Entity && this.name.equals(((Entity) obj).getName()) && this.description.equals(((Entity) obj).getDescription()));
    }
}