package com.models;

import com.dbConnector.dbConnector;
import com.vaadin.server.VaadinService;
import com.vaadin.server.VaadinSession;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

public class UserModel {

    private Integer userId;
    private String username;
    private String firstname;
    private String lastname;
    private String department;
    private String password;
    private String userRole;

    private String errorMsg = "";
    private String successMsg = "";

    private static Integer workload = 12;

    /**
     * Method to create a new User
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
            stmt.setString(1, userModel.username);
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
     * @return Boolean
     */
    public boolean updateUser(UserModel userModel) {
        Connection connection = null;
        int countRow = 0;
        try {
            // get connection
            connection = dbConnector.getConnection();

            // prepare and build sql update query
            String sql = "UPDATE user SET username = ?, firstname = ?, lastname = ?, department = ?, user_role = ? " +
                    "WHERE iduser = ?";
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, userModel.username);
            stmt.setString(2, userModel.firstname);
            stmt.setString(3, userModel.lastname);
            stmt.setString(4, userModel.department);
            stmt.setString(5, userModel.userRole);
            stmt.setInt(6, userModel.userId);
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
     * Method to get a user by id
     * @return UserModel
     */
    public UserModel getUserById(Integer userId) {
        return queryUser("userId", userId.toString());
    }

    /**
     * Method to get a user by username
     * @return UserModel
     */
    public UserModel getUserByUsername(String username) {
        return queryUser("username", username);
    }

    /**
     * Method to check password, returning true or false
     * depending if given password matches saved password for given username
     */
    public Boolean login(String username, String password) {

        UserModel user = getUserByUsername(username);

        // if no user was found, the password was not set
        if (user.password == null) {
            this.errorMsg = "username or password incorrect";
            return false;
        }

        // check passwords
        if (!checkPassword(password, user.password)) {
            this.errorMsg = "username or password incorrect";
            return false;
        }

        // create new session and save user id into it
        VaadinSession.getCurrent().setAttribute("userId", user.userId);

        // TODO generate token and store it in table and in session

        // TODO create method to check token in some before method of every model class or view class

        return true;
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

            while (result.next()) {
                UserModel user = new UserModel();
                user.setUserId(result.getInt("iduser"));
                user.setUsername(result.getString("username"));
                user.setFirstname(result.getString("firstname"));
                user.setLastname(result.getString("lastname"));
                user.setDepartment(result.getString("department"));
                user.setUserRole(result.getString("user_role"));
                user.password = result.getString("password");
                resultData.add(user);
            }
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

    public String getErrorMsg() {
        return this.errorMsg;
    }

    public String getSuccessMsg() {
        return this.successMsg;
    }

    public Integer getUserId() { return this.userId; }

    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = hashPassword(password);
    }

    private static String hashPassword(String plaintextPassword) {
        String salt = BCrypt.gensalt(workload);
        return BCrypt.hashpw(plaintextPassword, salt);
    }

    private static Boolean checkPassword(String userPassword, String storedPassword) {
        return BCrypt.checkpw(userPassword, storedPassword);
    }

    public String getUserRole() {
        return this.userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
