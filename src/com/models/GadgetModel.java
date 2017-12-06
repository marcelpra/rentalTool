package com.models;

import com.dbConnector.dbConnector;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class GadgetModel {

    private String category;
    private Integer gadgetID = null;
    private String description;
    private String inventory_No;
    private Boolean gadget_active = true;
    private Date createdTimestamp;

    private String errorMsg = "";
    private String successMsg = "";

    private Boolean validate() {
        if (category == null || category.isEmpty()) {
            errorMsg = "Category not set";
            return false;
        }
        if (description == null || description.isEmpty()) {
            errorMsg = "Description not set";
            return false;
        }
        if (inventory_No == null || inventory_No.isEmpty()) {
            errorMsg = "Inventory_No not set";
            return false;
        }
        return true;
    }

    /**
     * Method to create a new Gadget
     *
     * @return Boolean
     */
    public boolean addGadget() {

        if (!validate()) {
            return false;
        }

        Connection connection = null;
        int countRow = 0;

        try {
            // get connection
            connection = dbConnector.getConnection();
            // prepare and build sql insert query
            String sql = "INSERT INTO gadget (category, description, inventory_No, gadget_active) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, category);
            stmt.setString(2, description);
            stmt.setString(3, inventory_No);
            stmt.setBoolean(4, gadget_active);
            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "gadget creating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "gadget successfully created";
            return true;
        } else {
            this.errorMsg = "creating gadget not successful";
            return false;
        }
    }

    /**
     * Method to update a new Gadget
     *
     * @return Boolean
     */
    public boolean updateGadget() {

        if (!validate()) {
            return false;
        }

        Connection connection = null;
        int countRow = 0;

        // only allow Admins to update different User then the own
        // TODO add validation

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "UPDATE gadget SET category = ?, description = ?, inventory_No = ?, gadget_active = ? WHERE gadgetID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, category);
            stmt.setString(2, description);
            stmt.setString(3, inventory_No);
            stmt.setBoolean(4, gadget_active);
            stmt.setInt(5, gadgetID);
            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "gadget updating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "gadget successfully updated";
            return true;
        } else {
            this.errorMsg = "updating gadget not successful";
            return false;
        }
    }

    /**
     * Method that searches for a gadget by gadgetID
     *
     * @param gadgetID the id of the gaget
     * @return GadgetModel gaget
     */
    public GadgetModel getGadget(Integer gadgetID) {
        return queryGadget("gadgetID", gadgetID.toString());
    }

    /**
     * Method that gets all Gagets saved in DB-table
     *
     * @return List of GadgetModels
     */
    public static ArrayList<GadgetModel> getGadgets() {
        ArrayList<GadgetModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM gadget";
            PreparedStatement stmt = connection.prepareStatement(sql);
            ResultSet result = stmt.executeQuery();

            resultData = setResultData(result, resultData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }

    /**
     * Binds query result to userModel
     *
     * @param result      the query result
     * @param returnValue the array list where we pass the result to
     * @return ArrayList
     */
    private static ArrayList setResultData(ResultSet result, ArrayList<GadgetModel> returnValue) {
        try {
            while (result.next()) {
                GadgetModel gadget = new GadgetModel();
                gadget.setCategory(result.getString("category"));
                gadget.setGadgetID(result.getInt("gadgetID"));
                gadget.setDescription(result.getString("description"));
                gadget.setInventory_No(result.getString("inventory_No"));
                gadget.setGadget_active(result.getBoolean("gadget_active"));
                gadget.setCreatedTimestamp(result.getDate("created_timestamp"));
                returnValue.add(gadget);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * General method to query for a single user by specific field
     *
     * @param searchField    the column of database table to search for argument
     * @param searchArgument the criteria for the search
     * @return UserModel
     */
    private GadgetModel queryGadget(String searchField, String searchArgument) {
        Connection connection = null;
        ArrayList<GadgetModel> resultData = new ArrayList<>();

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM gadget WHERE " + searchField.toString() + " = ? LIMIT 1";

            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, searchArgument);
            ResultSet result = stmt.executeQuery();

            resultData = setResultData(result, resultData);
        } catch (Exception e) {
            this.errorMsg = "query not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // add error msg and empty model if empty result
        if (resultData.isEmpty()) {
            this.errorMsg = "gaget not found";
            resultData.add(0, new GadgetModel());
        }
        return resultData.get(0);
    }

    /**
     * GETTERS / SETTERS
     */

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getGadgetID() {
        return gadgetID;
    }

    public void setGadgetID(Integer gadgetID) {
        this.gadgetID = gadgetID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInventory_No() {
        return inventory_No;
    }

    public void setInventory_No(String inventory_No) {
        this.inventory_No = inventory_No;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Boolean getGadget_active() {
        return gadget_active;
    }

    public void setGadget_active(boolean gadget_active) {
        this.gadget_active = gadget_active;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getSuccessMsg() {
        return successMsg;
    }
}


