package com.models;

import java.util.ArrayList;
import java.util.List;

public class GadgetDummy {

    private Integer gadgetId;
    private String name;
    private String category;
    private String description;
    private Integer inventoryNumber;
    private String status;

    GadgetDummy(Integer gadgetId, String name, String category, String description, Integer inventoryNumber, String status) {
        this.gadgetId = gadgetId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.inventoryNumber = inventoryNumber;
        this.status = status;
    }

    public static List<GadgetDummy> getGadgets() {
        ArrayList<GadgetDummy> list = new ArrayList<>();
        list.add(new GadgetDummy(1, "gadget 1", "category 1", "description", 1, "active"));
        list.add(new GadgetDummy(2, "gadget 2", "category 1", "description", 2, "active"));
        list.add(new GadgetDummy(3, "gadget 3", "category 1", "description", 3, "active"));
        list.add(new GadgetDummy(4, "gadget 4", "category 2", "description", 4, "active"));
        list.add(new GadgetDummy(5, "gadget 5", "category 2", "description", 5, "active"));
        list.add(new GadgetDummy(6, "gadget 6", "category 2", "description", 6, "active"));

        return list;
    }

    public Integer getGadgetId() {
        return gadgetId;
    }

    public void setGadgetId(Integer gadgetId) {
        this.gadgetId = gadgetId;
    }
}
