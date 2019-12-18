package com.chillimport.server.entities;

public class UnitOfMeasurement {

    private String name;
    private String symbol;
    private String definition;

    public UnitOfMeasurement() {
    }

    /**
     * creates a new unit of measurement
     *
     * @param name       name of the unit
     * @param symbol     symbol of the unit
     * @param definition definition of the unit
     */
    public UnitOfMeasurement(String name, String symbol, String definition) {
        this.name = name;
        this.symbol = symbol;
        this.definition = definition;
    }

    /**
     * converts a frost standard Unit of Measurement to a chillimport standard U.o.M.
     *
     * @param unit frost standard unit
     */
    public UnitOfMeasurement(de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement unit) {
        this.name = unit.getName();
        this.definition = unit.getDefinition();
        this.symbol = unit.getSymbol();
    }

    /**
     * returns the name of the unit of measurement
     *
     * @return name of the unit
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * returns the symbol of the unit of measurement
     *
     * @return symbol of the unit
     */
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    /**
     * returns the definition of the unit of measurement
     *
     * @return definition of the unit
     */
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    /**
     * converts the chillimport standard unit of measurement to a frost standard unit of measurement
     *
     * @return frost standard unit of measurement
     */
    public de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement convertToFrostStandard() {
        return new de.fraunhofer.iosb.ilt.sta.model.ext.UnitOfMeasurement(this.name, this.symbol, this.definition);
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof UnitOfMeasurement &&
        	this.name.equals(((UnitOfMeasurement) obj).getName()) && 
        	this.symbol.equals(((UnitOfMeasurement) obj).getSymbol()) && 
        	this.definition.equals(((UnitOfMeasurement) obj).getDefinition()));
    }
}
