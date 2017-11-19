package com.models;

import com.dbConnector.dbConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class GadgetDummy {

    private Integer gadgetId;
    private String name;
    private String category;
    private String description;
    private Integer inventoryNumber;
    private String status;

    GadgetDummy() {
        this.gadgetId = gadgetId;
        this.name = name;
        this.category = category;
        this.description = description;
        this.inventoryNumber = inventoryNumber;
        this.status = status;
    }

    public static GadgetDummy getGadgetById(Integer gadgetId) {
        GadgetDummy resultData = new GadgetDummy();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM gadget WHERE gadgetID = ?";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, gadgetId);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                GadgetDummy gadget = new GadgetDummy();
                gadget.category = result.getString("category");
                gadget.gadgetId = result.getInt("gadgetID");
                gadget.description = result.getString("description");
                gadget.inventoryNumber = result.getInt("inventory_No");
                gadget.status = result.getString("gadget_active");
                resultData = gadget;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }

    public static List<GadgetDummy> getGadgets() {
        ArrayList<GadgetDummy> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM gadget";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                GadgetDummy gadget = new GadgetDummy();
                gadget.category = result.getString("category");
                gadget.gadgetId = result.getInt("gadgetID");
                gadget.description = result.getString("description");
                gadget.inventoryNumber = result.getInt("inventory_No");
                gadget.status = result.getString("gadget_active");
                resultData.add(gadget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }

    public static List<String> getGadgetCategories() {

        // TODO add dateFrom and dateTo to input parameters to join availability table and select only available gadgets
        ArrayList<String> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT DISTINCT category FROM gadget";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                resultData.add(result.getString("category"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }

    public static List<GadgetDummy> getGadgetsForCategory(String category) {

        // TODO add dateFrom and dateTo to input parameters to join availability table and select only available gadgets
        ArrayList<GadgetDummy> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM gadget WHERE category = ?";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            stmt.setString(1, category);
            ResultSet result = stmt.executeQuery();

            while (result.next()) {
                GadgetDummy gadget = new GadgetDummy();
                gadget.gadgetId = result.getInt("gadgetID");
                gadget.description = result.getString("description");
                resultData.add(gadget);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }



    public Integer getGadgetId() {
        return gadgetId;
    }

    public void setGadgetId(Integer gadgetId) {
        this.gadgetId = gadgetId;
    }

    public String getCategory() {return category;}

    public String getDescription() {return description;}
}
