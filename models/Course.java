package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import database.Database;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Course {
    @JsonProperty
    String courseId;
    @JsonProperty
    String studentId;
    @JsonProperty
    String courseName;
    @JsonProperty
    Integer courseGrade;
    @JsonProperty
    Integer semester;

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getCourseGrade() {
        return courseGrade;
    }

    public void setCourseGrade(Integer courseGrade) {
        this.courseGrade = courseGrade;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public static List<String> getCoursesandGrades(String studentId)throws ClassNotFoundException, SQLException{
        PreparedStatement ps =
                Database.prepareStatement("SELECT course_name, grade FROM exercise.course WHERE" +
                        " student_id = ?");
        ps.setString(1,studentId);
        ResultSet rs = ps.executeQuery();
        List<String> coursesAndGrades = new ArrayList<>();
        while (rs.next()){
            String string = rs.getString("course_name") + rs.getString("grade");
            coursesAndGrades.add(string);
        }
        return  coursesAndGrades;

    }

}
