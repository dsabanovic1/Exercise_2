package controllers;

import enums.Courses;
import enums.UserRights;
import models.Course;
import models.User;
import org.graalvm.compiler.core.common.type.ArithmeticOpTable;
import security.AuthenticationFilter;
import security.Secured;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.bind.ValidationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

public class ApiController {

    @Path("/students")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Secured(UserRights.administrator)
    public Response addStudents(ArrayList<User> students) {
        try {

            User.addStudents(students);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Path("/courseenroll")
    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Secured(UserRights.student)
    public Response CourseEnroll(Courses courses, @Context SecurityContext securityContext) {
        try {
            String studentId = securityContext.getUserPrincipal().getName();
            User.courseEnroll(courses, studentId);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException | IOException | ValidationException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    @Path("/grade/{id}")
    @PUT
    @Produces("application/json")
    @Consumes("application/json")
    @Secured(UserRights.administrator)
    public Response addGrades(@PathParam("id") String courseId, Integer grade) {
        try {
            User.gradeStudentCourse(courseId, grade);
            return Response.status(200).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Path("/courses/{id}")
    @GET
    @Produces("application/json")
    @Consumes("application/json")
    @Secured(UserRights.student)
    public Response getCoursesAndGrades(@PathParam("id") String studentId) {
        try {
            return Response.status(200).entity(Course.getCoursesandGrades(studentId)).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }

    }

    @Path("/status/{id}")
    @GET
    @Secured(UserRights.administrator)
    @Produces("application/json")
    @Consumes("application/json")
    public Response getStudentStatusInfo(@PathParam("id") String studentId) {
        try {
            return Response.status(200).entity(User.getInformationByStudentId(studentId)).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }

    @Path("/courses/{id}")
    @GET
    @Secured(UserRights.administrator)
    @Produces("application/json")
    @Consumes("application/json")
    public Response getListOfStudentsGradesForCourse(@PathParam("id") String courseId) {
        try {
            return Response.status(200).entity(User.getListOfStudentsAndGradesForCourse(courseId)).build();
        } catch (ClassNotFoundException | SQLException e) {
            return Response.status(500).entity(e.getMessage()).build();
        }
    }
}
