package com.models;

import com.dbConnector.dbConnector;
import com.vaadin.server.VaadinSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class UserModel {

    private Integer userID = null;
    private String email;
    private String firstname;
    private String lastname;
    private String department;
    private String password;
    private String userRole;
    private Boolean status;
    private Date createdTimestamp;
    private String accessToken;
    private Timestamp expiry;

    private String errorMsg = "";
    private String successMsg = "";

    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_EMPLOYEE = "Employee";
    public static final String ROLE_USER = "User";

    private static final Integer LOGIN_EXPIRY = 30;

    private Boolean validate() {
        if (email == null || email.isEmpty()) {
            errorMsg = "Email not set";
            return false;
        }
        if (firstname == null || firstname.isEmpty()) {
            errorMsg = "Firstname not set";
            return false;
        }
        if (lastname == null || lastname.isEmpty()) {
            errorMsg = "Lastname not set";
            return false;
        }
        if (department == null || department.isEmpty()) {
            errorMsg = "Department not set";
            return false;
        }
        if (status == null) {
            errorMsg = "Status not set";
            return false;
        }
        if (userRole == null || userRole.isEmpty()) {
            errorMsg = "User-Role not set";
            return false;
        }
        return true;
    }

    /**
     * Method to create a new User
     *
     * @return Boolean
     */
    public boolean createUser() {

        if (!validate()) {
            return false;
        }

        Connection connection = null;
        int countRow = 0;

        final String tempPassword = RandomStringUtils.randomAlphanumeric(6);

        // only allow admins to create user
        if (!validateAccessControl(ROLE_ADMIN)) {
            this.errorMsg = "not allowed to create user";
            return false;
        }

        try {
            // get connection
            connection = dbConnector.getConnection();

            password = hashPassword(tempPassword);
            // prepare and build sql insert query
            String sql = "INSERT INTO user (email, firstname, lastname, department, user_role, status_active, password) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, firstname);
            stmt.setString(3, lastname);
            stmt.setString(4, department);
            stmt.setString(5, userRole);
            stmt.setBoolean(6, status);
            stmt.setString(7, password);
            countRow = stmt.executeUpdate();
        } catch (Exception e) {
            this.errorMsg = "user creating not successful";
            e.printStackTrace();
        } finally {
            dbConnector.closeConnection(connection);
        }

        // check result
        if (countRow > 0) {
            this.successMsg = "user successfully created with Password: " + tempPassword;
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
    public boolean updateUser() {

        if (!validate()) {
            return false;
        }

        Connection connection = null;
        int countRow = 0;

        // only allow Admins to update different User then the own
//        String sessionUser = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));
//        if (!validateAccessControl(ROLE_ADMIN) && !sessionUser.equals(userModel.getUserID().toString())) {
//            this.errorMsg = "not allowed to update this user";
//            return false;
//        }

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "UPDATE user SET email = ?, firstname = ?, lastname = ?, department = ?, user_role = ?, " +
                    "status_active = ?, access_token = ?, expiry = ? " +
                    "WHERE user_ID = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, firstname);
            stmt.setString(3, lastname);
            stmt.setString(4, department);
            stmt.setString(5, userRole);
            stmt.setBoolean(6,status);
            stmt.setString(7, accessToken);
            stmt.setTimestamp(8, expiry);
            stmt.setInt(9, userID);

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
     *
     * @param username given username
     * @param password given password
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
        user.expiry = new Timestamp(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(LOGIN_EXPIRY));

        Boolean updateOk = user.updateUser();
        if (!updateOk) {
            this.errorMsg = "could not set token or expiry";
            return false;
        }

        // create new session and save user id into it
        VaadinSession.getCurrent().setAttribute("userID", user.userID);
        VaadinSession.getCurrent().setAttribute("accessToken", user.accessToken);

        return true;
    }

    public static void logout() {
        String sessionUser = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));

        // set access token expiry to now
        UserModel user = new UserModel();
        user = user.getUser(Integer.valueOf(sessionUser));
        user.expiry = new Timestamp(System.currentTimeMillis());
        user.updateUser();

        // delete session variables
        VaadinSession.getCurrent().setAttribute("userID", null);
        VaadinSession.getCurrent().setAttribute("accessToken", null);
    }

    /**
     * Method that validates if current access token for user id is valid
     *
     * @param userId the userId from Session
     * @param accessToken the accessToken from Session
     * @return Boolean true - if valid, false if not valid
     */
    public Boolean validateSession(Integer userId, String accessToken) {
        UserModel user = queryUser("access_token", accessToken);

        // get current timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        // check userId and expiry
        return user.userID.equals(userId) && user.expiry.after(timestamp);
    }

    /**
     * Method that validates if current user has required UserRole
     *
     * @param requiredUserRole the userRole we require to be checked
     * @return Boolean true - if saved userRole matches required Role, false - if not
     */
    public static Boolean validateAccessControl(String requiredUserRole) {
        String userId = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));
        String accessToken = String.valueOf(VaadinSession.getCurrent().getAttribute("accessToken"));

        UserModel user = new UserModel();
        user = user.queryUser("access_token", accessToken);
        if (user.getUserID() == null) {
            return false;
        }

        // get current timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        // check userId, expiry and required userRole
        return user.getUserID().equals(Integer.valueOf(userId)) && user.getExpiry().after(timestamp) && user.getUserRole().equals(requiredUserRole);
    }

    /**
     * Method that searches for a user by userID
     *
     * @param userID the id of the user
     * @return UserModel user
     */
    public UserModel getUser(Integer userID) {
        return queryUser("user_ID", userID.toString());
    }

    /**
     * Method that gets all Users saved in DB-table
     *
     * @return List of UserModels
     */
    public static List<UserModel> getUsers() {
        ArrayList<UserModel> resultData = new ArrayList<>();
        Connection connection = null;

        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "SELECT * FROM user";
            PreparedStatement stmt;

            // only allow admins to see all Users
            if (!validateAccessControl(ROLE_ADMIN)) {
                sql = sql + " WHERE user_ID = ?";
                stmt = connection.prepareStatement(sql);
                stmt.setString(1, String.valueOf(VaadinSession.getCurrent().getAttribute("userID")));
            } else {
                stmt = connection.prepareStatement(sql);
            }

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
     * Method that creates additional condition for a DB-Query for defined userRoles
     *
     * @return String with additional condition for a query
     * @throws Exception if user was not found
     */
    private String accessControl() throws Exception {
        String userId = String.valueOf(VaadinSession.getCurrent().getAttribute("userID"));

        String queryCondition = "";
        UserModel user = new UserModel();
        user = user.queryUser("user_ID", userId);
        if (user.getUserID() == null) {
            throw new Exception("User not found");
        }

        if (!user.getUserRole().equals(ROLE_ADMIN)) {
            queryCondition = " AND user_ID = " + user.getUserID().toString();
        }

        return queryCondition;
    }

    /**
     * Validates given password with stored password
     *
     * @param userPassword   the given user password
     * @param storedPassword the stored user password
     * @return Boolean
     */
    private static Boolean checkPassword(String userPassword, String storedPassword) {
        return BCrypt.checkpw(userPassword, storedPassword);
    }

    /**
     * Binds query result to userModel
     *
     * @param result      the query result
     * @param returnValue the array list where we pass the result to
     * @return ArrayList
     */
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
                user.setStatus(result.getBoolean("status_active"));
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
     * @param searchField    the column of database table to search for argument
     * @param searchArgument the criteria for the search
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

            // add access control condition to query
//            sql = sql + accessControl();

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

    /**
     * Function that hashes a given string
     *
     * @param plaintextPassword the given plaintext password
     * @return String
     */
    private static String hashPassword(String plaintextPassword) {
        Integer workload = 12;
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(plaintextPassword, salt);
    }

    /**
     * GETTERS / SETTERS
     *
     */
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

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Boolean getStatus() {
        return status;
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
}
