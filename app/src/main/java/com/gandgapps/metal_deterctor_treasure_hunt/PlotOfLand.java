package com.gandgapps.metal_deterctor_treasure_hunt;

public class PlotOfLand {

    public int treasure = 0;
    private int myX = 0;
    private int myY = 0;
    private int myWidth = 0;
    private int myHeight = 0;
    private PlotOfLand myNeighbours[];
    public boolean active = false;
    public boolean dugUp = false;

    public int getMyHeight() {
        return myHeight;
    }

    public boolean isDugUp() {
        return dugUp;
    }

    public void setMyHeight(int myHeight) {
        this.myHeight = myHeight;
    }

    public int getMyWidth() {

        return myWidth;
    }

    public void setMyWidth(int myWidth) {
        this.myWidth = myWidth;
    }

    // has to set/get traesure

    public int getTreasure() {
        return treasure;
    }

    public void setTreasure(int treasure) {
        this.treasure = treasure;
    }

    // has to have coordinates and a click suface (button?)

    public int getMyX() {
        return myX;
    }

    public void setMyX(int myX) {
        this.myX = myX;
    }

    public int getMyY() {
        return myY;
    }

    public void setMyY(int myY) {
        this.myY = myY;
    }

    public PlotOfLand[] getMyNeighbours() {
        return myNeighbours;
    }
    // has to identify its neighbours
    public void setMyNeighbours(PlotOfLand[] myNeighbours) {
        this.myNeighbours = myNeighbours;
    }

    // can be inactif

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean Active) {
        this.active = active;
        // todo put in image of terrain
    }


    // must change its image if inactiv or if dug up

    // can be dug up

    public void setDugUp(boolean dugUp) {
        this.dugUp = dugUp;
        // todo put in image of holes
    }
}
