package com.models;

import com.dbConnector.dbConnector;
import com.vaadin.server.VaadinSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserModel {

    private Integer userID;
    private String email;
    private String firstname;
    private String lastname;
    private String department;
    private String password;
    private String userRole;
    private Date createdTimestamp;
    private String accessToken;
    private Timestamp expiry;

    private String errorMsg = "";
    private String successMsg = "";

    private static final Integer LOGIN_EXPIRYY = 30;

    private static Integer workload = 12;

    /**
     * Method to create a new User
     *
     * @return Boolean
     */
    public boolean createUser(UserModel userModel) {
        Connection connection = null;
        int countRow = 0;
        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql insert query
            String sql = "INSERT INTO user (username, firstname, lastname, department, user_role, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, userModel.email);
            stmt.setString(2, userModel.firstname);
            stmt.setString(3, userModel.lastname);
            stmt.setString(4, userModel.department);
            stmt.setString(5, userModel.userRole);
            stmt.setString(6, userModel.password);
            countRow = stmt.executeUpdate();

            // check result
            if (countRow > 0) {
                this.successMsg = "user created successful";
            } else {
                this.errorMsg = "user creating not successful";
            }
        } catch (Exception e) {
            this.errorMsg = "user creating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "user successfully created";
            return true;
        } else {
            this.errorMsg = "creating user not successful";
            return false;
        }
    }

    /**
     * Method to update a new User
     *
     * @return Boolean
     */
    public boolean updateUser(UserModel userModel) {
        Connection connection = null;
        int countRow = 0;
        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "UPDATE user SET email = ?, firstname = ?, lastname = ?, department = ?, user_role = ?, " +
                    "access_token = ?, expiry = ? " +
                    "WHERE user_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, userModel.email);
            stmt.setString(2, userModel.firstname);
            stmt.setString(3, userModel.lastname);
            stmt.setString(4, userModel.department);
            stmt.setString(5, userModel.userRole);
            stmt.setString(6, userModel.accessToken);
            stmt.setTimestamp(7, userModel.expiry);
            stmt.setInt(8, userModel.userID);
            // TODO don't update password with this method as we do not know if it is already hashed or not
            // stmt.setString(6, userModel.password);

            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "user updating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "user successfully updated";
            return true;
        } else {
            this.errorMsg = "updating user not successful";
            return false;
        }
    }

    /**
     * Method to check password, returning true or false
     * depending if given password matches saved password for given email
     */
    public Boolean login(String username, String password) {

        UserModel user = queryUser("email", username);

        // if no user was found, the password was not set
        if (user.password == null) {
            this.errorMsg = "no password set";
            return false;
        }

        // check passwords
        if (!checkPassword(password, user.password)) {
            this.errorMsg = "email or password incorrect";
            return false;
        }

        // generate access token
        user.accessToken = RandomStringUtils.randomAlphanumeric(32);
        user.expiry = new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(LOGIN_EXPIRYY));

        Boolean updateOk = updateUser(user);
        if (!updateOk) {
            this.errorMsg = "could not set token or expiry";
            return false;
        }

        // create new session and save user id into it
        VaadinSession.getCurrent().setAttribute("userID", user.userID);
        VaadinSession.getCurrent().setAttribute("accessToken", user.accessToken);

        return true;
    }

    public Boolean validateSession(Integer userId, String accessToken) {
        UserModel user = queryUser("access_token", accessToken);

        // get current timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // check userId and expiry
        if (!user.userID.equals(userId) || !user.expiry.after(timestamp)) {
            return false;
        } else {
            return true;
        }
    }

    public static List<UserModel> getUsers() {
        ArrayList<UserModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM user";
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

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getSuccessMsg() {
        return successMsg;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Date getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Date createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Timestamp getExpiry() {
        return expiry;
    }

    public void setExpiry(Timestamp expiry) {
        this.expiry = expiry;
    }

    private static String hashPassword(String plaintextPassword) {
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(plaintextPassword, salt);
    }

    private static Boolean checkPassword(String userPassword, String storedPassword) {
        return BCrypt.checkpw(userPassword, storedPassword);
    }

    private static ArrayList setResultData(ResultSet result, ArrayList<UserModel> returnValue) {
        try {
            while (result.next()) {
                UserModel user = new UserModel();
                user.setUserID(result.getInt("user_ID"));
                user.setEmail(result.getString("email"));
                user.setFirstname(result.getString("firstname"));
                user.setLastname(result.getString("lastname"));
                user.setDepartment(result.getString("department"));
                user.setUserRole(result.getString("user_role"));
                user.password = result.getString("password");
                user.accessToken = result.getString("access_token");
                user.expiry = result.getTimestamp("expiry");
                returnValue.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return returnValue;
    }

    /**
     * General method to query for a single user by specific field
     *
     * @return UserModel
     */
    private UserModel queryUser(String searchField, String searchArgument) {
        Connection connection = null;
        ArrayList<UserModel> resultData = new ArrayList<>();
        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM user WHERE " + searchField.toString() + " = ? LIMIT 1";
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
            this.errorMsg = "user not found";
            resultData.add(0, new UserModel());
        }
        return resultData.get(0);
    }
}
