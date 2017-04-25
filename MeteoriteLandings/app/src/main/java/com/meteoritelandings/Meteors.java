package com.meteoritelandings;


import io.realm.RealmObject;

/**
 * Created by Peter Miklánek
 *
 * Class represent Meteors in RealmObject
 */

public class Meteors extends RealmObject {

    private String name;
    private String year;
    private String mass;
    private String id;
    private String nametype;
    private String recclass;
    private String fall;
    private String reclat;
    private String reclong;
    private String type;
    private String coordinateA;
    private String coordinateB;

    public Meteors()
    {
    }

    /**
     * @return name - name of meteor
     */
    public String getName() {
        return name;
    }

    /**
     * @param name - set name of meteor
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return year - year of meteor
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year - set year of meteor
     */
    public void setYear(String year) {
        this.year = year;
    }

    /**
     * @return mass - name of meteor
     */
    public String getMass() {
        return mass;
    }

    /**
     * @param mass - set mass of meteor
     */
    public void setMass(String mass) {
        this.mass = mass;
    }

    /**
     * @return id - id of meteor
     */
    public String getId() {
        return id;
    }

    /**
     * @param id - set id of meteor
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return nametype - nametype of meteor
     */
    public String getNametype() {
        return nametype;
    }

    /**
     * @param nametype - set nametype of meteor
     */
    public void setNametype(String nametype) {
        this.nametype = nametype;
    }

    /**
     * @return recclass - recclass of meteor
     */
    public String getRecclass() {
        return recclass;
    }

    /**
     * @param recclass - set recclass of meteor
     */
    public void setRecclass(String recclass) {
        this.recclass = recclass;
    }

    /**
     * @return fall - fall of meteor
     */
    public String getFall() {
        return fall;
    }

    /**
     * @param fall - set fall of meteor
     */
    public void setFall(String fall) {
        this.fall = fall;
    }

    /**
     * @return reclat - reclat of meteor
     */
    public String getReclat() {
        return reclat;
    }

    /**
     * @param reclat - set reclat of meteor
     */
    public void setReclat(String reclat) {this.reclat = reclat;}

    /**
     * @return reclong - reclong of meteor
     */
    public String getReclong() {
        return reclong;
    }

    /**
     * @param reclong - set reclong of meteor
     */
    public void setReclong(String reclong) {
        this.reclong = reclong;
    }

    /**
     * @return type - type of meteor
     */
    public String getType() {
        return type;
    }

    /**
     * @param type - set type of meteor
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return coordinateA - coordinateA of meteor
     */
    public String getCoordinateA() {
        return coordinateA;
    }

    /**
     * @param coordinateA - set coordinateA of meteor
     */
    public void setCoordinateA(String coordinateA) {
        this.coordinateA = coordinateA;
    }

    /**
     * @return coordinateB - coordinateB of meteor
     */
    public String getCoordinateB() {
        return coordinateB;
    }

    /**
     * @param coordinateB - set coordinateB of meteor
     */
    public void setCoordinateB(String coordinateB) {
        this.coordinateB = coordinateB;
    }

}
