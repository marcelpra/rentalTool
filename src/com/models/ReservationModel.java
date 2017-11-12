package com.models;

import com.dbConnector.dbConnector;
import com.vaadin.server.VaadinSession;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationModel {

    private Integer reservationId = null;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private ArrayList<Integer> gadgets = new ArrayList<>();
    private Integer userId;
    private Boolean status;
    private Timestamp createdTimestamp;

    private String errorMsg = "";
    private String successMsg = "";

    /**
     * Method to create a new User
     *
     * @param reservationModel the userModel to be saved in database
     * @return Boolean
     */
    public boolean createReservation(ReservationModel reservationModel) {
        Connection connection = null;
        int countRow = 0;
        try {
            // get connection
            connection = dbConnector.getConnection();

            // get UserId
            String userId = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));

            // prepare and build sql insert query
            String sql = "INSERT INTO reservation (date_from, date_to, gadgets, user_ID, status_active) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, reservationModel.dateFrom);
            stmt.setObject(2, reservationModel.dateTo);
            stmt.setString(3, prepareGadgetString(reservationModel.gadgets));
            stmt.setInt(4, Integer.valueOf(userId));
            stmt.setBoolean(5, reservationModel.status);
            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "user creating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "reservation successfully created";
            return true;
        } else {
            this.errorMsg = "creating reservation not successful";
            return false;
        }
    }

    /**
     * Method to update a Reservation
     *
     * @param reservationModel the reservationModel to be saved in database
     * @return Boolean
     */
    public boolean updateReservation(ReservationModel reservationModel) {
        Connection connection = null;
        int countRow = 0;

        // only allow Admins to update different User then the own
        // TODO update during login is not possible and during login we need to allow to update own user
//        String sessionUser = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));
//        if (!validateAccessControl(ROLE_ADMIN) && !sessionUser.equals(userModel.getUserID().toString())) {
//            this.errorMsg = "not allowed to update this user";
//            return false;
//        }

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "UPDATE reservation SET date_from = ?, date_to = ?, gadgets = ?, status_active = ? " +
                    "WHERE reservation_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setObject(1, reservationModel.dateFrom);
            stmt.setObject(2, reservationModel.dateTo);
            stmt.setString(3, prepareGadgetString(reservationModel.gadgets));
            stmt.setBoolean(4, reservationModel.getStatus());
            stmt.setInt(5, reservationModel.getReservationId());

            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "reservation updating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "reservation successfully updated";
            return true;
        } else {
            this.errorMsg = "updating reservation not successful";
            return false;
        }
    }

    /**
     * Method that searches for a reservation by reservationID
     *
     * @param reservationID the id of the reservation
     * @return ReservationModel reservation
     */
    public ReservationModel getReservation(Integer reservationID) {
        return queryReservation("reservation_ID", reservationID.toString());
    }

    /**
     * Method that gets all Reservations saved in DB-table
     *
     * @return List of ReservationModels
     */
    public static List<ReservationModel> getReservations() {
        ArrayList<ReservationModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM reservation";
            PreparedStatement stmt;
            stmt = connection.prepareStatement(sql);
            ResultSet result = stmt.executeQuery();

            resultData = setResultData(result, resultData);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        return resultData;
    }

    private String prepareGadgetString(ArrayList<Integer> list) {
        // prepare gadgetString
        Boolean processedFirst = false;
        StringBuilder gadgetsString = new StringBuilder();
        for(Integer record: list) {
            if (processedFirst) {
                gadgetsString.append(",");
            }
            gadgetsString.append(record);
            processedFirst = true;
        }
        return gadgetsString.toString();
    }

    /**
     * General method to query for a single user by specific field
     *
     * @param searchField    the column of database table to search for argument
     * @param searchArgument the criteria for the search
     * @return ReservationModel
     */
    private ReservationModel queryReservation(String searchField, String searchArgument) {
        Connection connection = null;
        ArrayList<ReservationModel> resultData = new ArrayList<>();

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM reservation WHERE " + searchField.toString() + " = ? LIMIT 1";

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
            this.errorMsg = "reservation not found";
            resultData.add(0, new ReservationModel());
        }
        return resultData.get(0);
    }

    /**
     * Binds query result to userModel
     *
     * @param result      the query result
     * @param returnValue the array list where we pass the result to
     * @return ArrayList
     */
    private static ArrayList setResultData(ResultSet result, ArrayList<ReservationModel> returnValue) {
        try {
            while (result.next()) {
                ReservationModel reservation = new ReservationModel();
                reservation.setReservationId(result.getInt("reservation_ID"));
                Date sqlDateFrom = result.getDate("date_from");
                reservation.setDateFrom(sqlDateFrom.toLocalDate());
                Date sqlDateTo = result.getDate("date_to");
                reservation.setDateTo(sqlDateTo.toLocalDate());
                String gadgetString = result.getString("gadgets");
                String[] gadgets = gadgetString.split(",");
                for (String gadget : gadgets) {
                    if (gadget.isEmpty()) {
                        continue;
                    }
                    reservation.gadgets.add(Integer.valueOf(gadget));
                }
                reservation.setUserId(result.getInt("user_ID"));
                reservation.setStatus(result.getBoolean("status_active"));
                returnValue.add(reservation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public void setSuccessMsg(String successMsg) {
        this.successMsg = successMsg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public ArrayList<Integer> getGadgets() {
        return gadgets;
    }

    public void setGadgets(ArrayList<Integer> gadgets) {
        this.gadgets = gadgets;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    public void setDateTo(LocalDate dateTo) {
        this.dateTo = dateTo;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(LocalDate dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
