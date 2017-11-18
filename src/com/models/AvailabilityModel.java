package com.models;

import com.dbConnector.dbConnector;
import com.vaadin.server.VaadinSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AvailabilityModel {

    private LocalDate dateAvailability;
    private Integer gadgetId;
    private String status;
    private Integer reservationId;

    private String errorMsg = "";
    private String successMsg = "";

    public static final String STATUS_RESERVED = "reserved";
    public static final String STATUS_AVAILABLE = "available";

    /**
     * Method to create a new User
     *
     * @return Boolean
     */
    private boolean createAvailability() {

        Connection connection = null;
        int countRow = 0;
        try {
            // get connection
            connection = dbConnector.getConnection();

            // get UserId
            String userId = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));

            // prepare and build sql insert query
            String sql = "INSERT INTO availability (date_availability, gadget_ID, status_availability, reservation_ID) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, dateAvailability);
            stmt.setInt(2, gadgetId);
            stmt.setString(3, status);
            stmt.setInt(4, reservationId);
            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "availability creating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "availability successfully created";
            return true;
        } else {
            this.errorMsg = "creating availability not successful";
            return false;
        }
    }

    /**
     * Method to update Availability
     *
     * @return Boolean
     */
    private boolean updateAvailability() {

        Connection connection = null;
        int countRow = 0;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "UPDATE availability SET status_availability = ?, reservation_ID = ? " +
                    "WHERE date_availability = ? AND gadget_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setInt(2, reservationId);
            stmt.setObject(3, dateAvailability);
            stmt.setInt(4, gadgetId);

            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "availability updating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "availability successfully updated";
            return true;
        } else {
            this.errorMsg = "updating availability not successful";
            return false;
        }
    }

    /**
     * Method that searches for a reservation by reservationID
     *
     * @param reservationId the id of reservation
     * @return AvailabilityModel availability
     */
    public static List<AvailabilityModel> getAvailabilityForReservation(Integer reservationId) {
        ArrayList<AvailabilityModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM availability WHERE reservation_ID = ?";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, reservationId);
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
     * Method that searches for a reservation by reservationID
     *
     * @param dateFrom availability date from
     * @param dateTo availability date to
     * @param gadgetId the id of gadget
     * @return AvailabilityModel avaiability
     */
    public List<AvailabilityModel> getAvailabilityForGadgetInRange(
            LocalDate dateFrom,
            LocalDate dateTo,
            Integer gadgetId
    ) {
        ArrayList<AvailabilityModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM availability WHERE gadget_ID = ? AND date_availability >= ? AND date_availability <= ?";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, gadgetId);
            stmt.setObject(2, dateFrom);
            stmt.setObject(3, dateTo);
            ResultSet result = stmt.executeQuery();

            resultData = setResultData(result, resultData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }

    public static Boolean setAvailability(
            LocalDate dateFrom,
            LocalDate dateTo,
            Integer gadgetId,
            String status,
            Integer reservationId,
            String method
    ) {

        // reset all existing availabilities with this reservation ID to status available
        List<AvailabilityModel> oldAvailability = getAvailabilityForReservation(reservationId);
        if (!oldAvailability.isEmpty()) {
            for (AvailabilityModel oldModel : oldAvailability) {
                oldModel.status = STATUS_AVAILABLE;
                oldModel.reservationId = 0;
                oldModel.updateAvailability();
            }
        }

        // set status to reserved for reservation period
        Boolean success = false;
        for (LocalDate date = dateFrom; date.isBefore(dateTo.plusDays(1)); date = date.plusDays(1)) {
            AvailabilityModel availability  = new AvailabilityModel();
            availability.dateAvailability = date;
            availability.gadgetId = gadgetId;
            availability.status = status;
            availability.reservationId = reservationId;

            if (method.equals("update")) {
                success = availability.updateAvailability();
            } else {
                success = availability.createAvailability();
            }
        }

        return success;
    }

    public static Boolean prepareAvailability() {

        Boolean updateSuccess = false;

        List<GadgetDummy> gadgets = GadgetDummy.getGadgets();

        for (GadgetDummy gadget : gadgets) {
            // get latest day of availability
            LocalDate startDate = getLatestAvailability(gadget.getGadgetId());
            LocalDate today = LocalDate.now();

            // write availability 1 year in advance
            // TODO change number of days or use constant
            LocalDate endDate = today.plusDays(10);

            // if latest day of availability is already set to 1 year in advance, continue
            if (!endDate.isAfter(startDate.plusDays(1))) {
                continue;
            }
            updateSuccess = setAvailability(
                    startDate,
                    endDate,
                    gadget.getGadgetId(),
                    STATUS_AVAILABLE,
                    0,
                    "create"
            );
        }

        return updateSuccess;
    }

    private static LocalDate getLatestAvailability(Integer gadgetId) {
        ArrayList<AvailabilityModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM availability WHERE gadget_ID = ?" +
                    " ORDER BY date_availability DESC LIMIT 1";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, gadgetId);
            ResultSet result = stmt.executeQuery();

            resultData = setResultData(result, resultData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        if (resultData.isEmpty()) {
            AvailabilityModel availability = new AvailabilityModel();
            availability.setGadgetId(gadgetId);
            availability.setDateAvailability(LocalDate.now());
            resultData.add(availability);
        }

        return resultData.get(0).dateAvailability;
    }

    /**
     * Binds query result to userModel
     *
     * @param result      the query result
     * @param returnValue the array list where we pass the result to
     * @return ArrayList
     */
    private static ArrayList setResultData(ResultSet result, ArrayList<AvailabilityModel> returnValue) {
        try {
            while (result.next()) {
                AvailabilityModel availability = new AvailabilityModel();
                Date sqlDateFrom = result.getDate("date_availability");
                availability.setDateAvailability(sqlDateFrom.toLocalDate());
                availability.setGadgetId(result.getInt("gadget_ID"));
                availability.setStatus(result.getString("status_availability"));
                availability.setReservationId(result.getInt("reservation_ID"));
                returnValue.add(availability);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public LocalDate getDateAvailability() {
        return dateAvailability;
    }

    public void setDateAvailability(LocalDate dateAvailability) {
        this.dateAvailability = dateAvailability;
    }

    public Integer getGadgetId() {
        return gadgetId;
    }

    public void setGadgetId(Integer gadgetId) {
        this.gadgetId = gadgetId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }
}
