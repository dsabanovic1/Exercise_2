package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import database.Database;
import enums.Courses;
import enums.UserRights;

import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class User {
    @JsonProperty
    String userId;
    @JsonProperty
    String username;
    @JsonProperty
    String password;
    @JsonProperty
    String role;
    @JsonProperty
    String firstName;
    @JsonProperty
    String lastName;
    @JsonProperty
    Integer balance;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }


    public static TokenSession logIn(String username, String password)
            throws ClassNotFoundException, InvalidKeySpecException, SQLException, NoSuchAlgorithmException, ValidationException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise2.users WHERE username = ? LIMIT 1");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        TokenSession tokenSession = null;

        if (!rs.isBeforeFirst()) {
            throw new ValidationException("User does not exist!");
        }

        if (rs.next()) {
            boolean matched = password.equals(rs.getString("password"));
            if (!matched) {
                throw new ValidationException("Invalid credentials");
            }

            tokenSession = TokenSession.generateSessionToken(rs.getString("user_id"));

        }

        return tokenSession;

    }

    public static boolean hasRequiredRights(String userId, UserRights[] allowedRights)
            throws ClassNotFoundException, SQLException {

        User user = getUser(userId);
        if (user == null) return false;
        String role = user.getRole();
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise2.roles WHERE role_name= ? LIMIT 1");
        ps.setString(1, role);
        ResultSet rs = ps.executeQuery();
        List<String> userRights = new ArrayList<>();
        while (rs.next()) {
            userRights = Arrays.asList((String[]) rs.getArray("role_rights").getArray());
        }
        List<String> userRightsList = new ArrayList<>();
        userRightsList.addAll(userRights);


        for (UserRights allowedRight : allowedRights) {

            if (userRightsList != null && userRightsList.contains(allowedRight.name())) {
                return true;
            }

        }
        return false;
    }

    public static User getUser(String userId) throws ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise2.users WHERE user_id = ? LIMIT 1");
        ps.setString(1, userId);
        ResultSet rs = ps.executeQuery();

        User user = new User();

        if (rs.next()) {
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setRole(rs.getString("role_name"));
            user.setBalance(rs.getInt("balance"));
        }

        return user;
    }


    public static List<User> getUsers() throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("SELECT * FROM exercise2.users");
        ResultSet rs = ps.executeQuery();
        List<User> users = new ArrayList<User>();
        while (rs.next()) {
            User user = new User();
            user.setUserId(rs.getString("user_id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password"));
            user.setFirstName(rs.getString("first_name"));
            user.setLastName(rs.getString("last_name"));
            user.setRole(rs.getString("role_name"));
            user.setBalance(rs.getInt("balance"));
            users.add(user);
        }
        return users;
    }

    public static void updateUser(String user_id, User user) throws ClassNotFoundException, SQLException, IOException {
        StringBuilder sql = new StringBuilder("UPDATE exercise2.users SET");
        if (null != user.getUsername()) {
            sql.append(" username = ?,");
        }
        if (null != user.getPassword()) {
            sql.append(" password = ?,");
        }
        if (null != user.getFirstName()) {
            sql.append(" firstname = ?,");
        }
        if (null != user.getLastName()) {
            sql.append(" lastname = ?,");
        }
        if (null != user.getRole()) {
            sql.append(" role = ?,");
        }
        if (null != user.getBalance()) {
            sql.append(" balance=?");
        }
        sql.append(" WHERE user_id = ?");

        PreparedStatement ps = Database.prepareStatement(sql.toString());
        int index = 1;

        if (null != user.getUsername()) {
            ps.setString(index, user.getUsername());
            index++;
        }
        if (null != user.getPassword()) {
            ps.setString(index, user.getPassword());
            index++;
        }
        if (null != user.getFirstName()) {
            ps.setString(index, user.getFirstName());
            index++;
        }
        if (null != user.getLastName()) {
            ps.setString(index, user.getLastName());
            index++;
        }
        if (null != user.getRole()) {
            ps.setString(index, user.getRole());
            index++;
        }
        if (null != user.getBalance()) {
            ps.setInt(index, user.getBalance());
            index++;
        }
        ps.setString(index, user_id);
        ps.executeUpdate();

    }

    public static void deleteUser(String user_id) throws ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("DELETE FROM exercise2.users WHERE user_id= ?");
        ps.setString(1, user_id);
        ps.executeUpdate();
    }

    public static int addStudents(ArrayList<User> users) throws ClassNotFoundException, SQLException {

        Connection c = Database.getConnection();
        c.setAutoCommit(false);
        PreparedStatement ps =
                Database.prepareStatement("INSERT INTO exercise2.users (user_id,first_name,last_name" +
                        ", username, password, role_name, balance) VALUES (?,?,?,?,?,?,?)");
        for (User user : users) {
            ps.setString(1, Database.generatorId(5));
            ps.setString(2, user.getFirstName());
            ps.setString(3, user.getLastName());
            ps.setString(4, user.getUsername());
            ps.setString(5, user.getPassword());
            ps.setString(6, user.getRole());
            ps.setInt(7, user.getBalance());
            ps.addBatch();
        }
        int[] results = ps.executeBatch();
        c.commit();
        ps.close();
        c.close();
        return results.length;

    }

    public static void courseEnroll(Courses course, String studentId) throws ValidationException, ClassNotFoundException, SQLException, IOException {

        PreparedStatement ps = Database.prepareStatement("INSERT INTO exercise2.course" +
                "(course_id, student_id, course_name) VALUES (?,?,?) ");
        ps.setString(1, Database.generatorId(5));
        ps.setString(2, studentId);
        ps.setString(3, course.toString());

        User u = getUser(studentId);
        u.setBalance(u.getBalance() - 600);
        if (u.getBalance() < 0)
            throw new ValidationException("Student doesnt have enough money for enrolling this course");
        ps.executeUpdate();
        updateUser(studentId, u);
    }

    public static void gradeStudentCourse(String courseId, Integer grade) throws
            ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("UPDATE exercise2.course SET grade = ? WHERE" +
                " course_id = ?");
        ps.setInt(1, grade);
        ps.setString(2, courseId);
        ps.executeUpdate();
    }

    public static List<String> getCoursesByStudentId(String studentId) throws ClassNotFoundException, SQLException {

        PreparedStatement ps = Database.prepareStatement("SELECT course_name FROM exercise2.course" +
                " WHERE user_id = ?");
        ps.setString(1, studentId);
        ResultSet rs = ps.executeQuery();
        List<String> coursesByStudentId = new ArrayList<>();
        while (rs.next()) {

            coursesByStudentId.add(rs.getString("course_name"));
        }
        return coursesByStudentId;
    }

    public static String getInformationByStudentId(String studentId) throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("SELECT first_name, balance FROM exercise2.users " +
                "WHERE user_id = ? LIMIT 1");
        ps.setString(1, studentId);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("first_name") + " " + rs.getInt("balance") + " " + getCoursesByStudentId(studentId);
    }

    public static List<String> getListOfStudentsAndGradesForCourse(String course_name)
            throws ClassNotFoundException, SQLException {
        PreparedStatement ps = Database.prepareStatement("SELECT user_id, grade FROM exercise2.course" +
                " WHERE course_name = ?");
        ps.setString(1, course_name);
        ResultSet rs = ps.executeQuery();
        List<String> studentsGrades = new ArrayList<>();
        while (rs.next()) {
            System.out.println(rs.getString("user_id"));
            studentsGrades.add(getUser(rs.getString("user_id")).getUsername() + rs.getString("grade"));

        }
        return studentsGrades;
    }
}
