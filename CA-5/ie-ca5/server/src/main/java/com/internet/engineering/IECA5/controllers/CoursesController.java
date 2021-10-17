package com.internet.engineering.IECA5.controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.internet.engineering.IECA5.domain.CourseEnrolment.CourseEnrolment;
import com.internet.engineering.IECA5.domain.CourseEnrolment.System.Offering;

import com.internet.engineering.IECA5.domain.CourseEnrolment.Student.Student;
import com.internet.engineering.IECA5.domain.CourseEnrolment.Student.Grade;

import com.google.gson.Gson;
import com.internet.engineering.IECA5.domain.CourseEnrolment.Student.WeeklySchedule;
import com.internet.engineering.IECA5.domain.CourseEnrolment.Student.WeeklyScheduleItem;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

@RestController
@RequestMapping("/courses")
public class CoursesController {

    @GetMapping("")
    public List<Offering> getCourses(final HttpServletResponse response) throws IOException {
        try {
            if (CourseEnrolment.getInstance().getStudent() == null){
                response.sendRedirect("http://localhost:8080/login");
                return null;
            }
            else {
                String std_id =  CourseEnrolment.getInstance().getStudent().getStudentId();
                int totalSelectedUnits = CourseEnrolment.getInstance().getStudent().getTotalUnits();
                return CourseEnrolment.getInstance().searchCourses(
                        CourseEnrolment.getInstance().getStudent().getSearchFilter()
                );
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return null;
        }
    }

    @GetMapping(value = "/selectedCourses")
    public String getSelectedCourses(final HttpServletResponse response) throws IOException {
//    public List<WeeklyScheduleItem> getSelectedCourses(final HttpServletResponse response) throws IOException {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        try {
            if (CourseEnrolment.getInstance().getStudent() == null){
                response.sendRedirect("http://localhost:8080/login");
                return null;
            }
            else {
                return gson.toJson(CourseEnrolment.getInstance().getStudent().getSelectedCourses());
//                return CourseEnrolment.getInstance().getStudent().getSelectedCourses();
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return null;
        }
    }

    @GetMapping("/search")
    public List<Offering> getPresentedCoursesByName(final HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            if (CourseEnrolment.getInstance().getStudent() == null){
                response.sendRedirect("http://localhost:8080/login");
                return null;
            }
            else {
                return CourseEnrolment.getInstance().searchCourses(
                        CourseEnrolment.getInstance().getStudent().getSearchFilter()
                );
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return null;
        }
    }

    @PostMapping("/search")
    public String searchFilter(@RequestBody(required = true) String jsonString, final HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            if (CourseEnrolment.getInstance().getStudent() == null){
                response.sendRedirect("http://localhost:8080/login");
                return null;
            }
            else {
                Properties properties = gson.fromJson(jsonString, Properties.class);
                String filter = properties.getProperty("filter");
                CourseEnrolment.getInstance().getStudent().setSearchFilter(filter);
                return Config.OK_RESPONSE;
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return null;
        }
    }

    @PostMapping("/presentedCoursesByType")
    public List<Offering> getPresentedCoursesByType(@RequestBody(required = true) String jsonString, final HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            if (CourseEnrolment.getInstance().getStudent() == null){
                response.sendRedirect("http://localhost:8080/login");
                return null;
            }
            else {
                Properties properties = gson.fromJson(jsonString, Properties.class);
                String filter = properties.getProperty("filter");
                return CourseEnrolment.getInstance().searchCoursesByType(
                    filter
                );
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return null;
        }
    }

    @GetMapping("/{courseId}")
    public Offering getCourseById(@PathVariable String courseId, final HttpServletResponse response) throws IOException {
        try {
            Offering course = CourseEnrolment.getInstance().getOffering(courseId);
            return course;
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return null;
        }
    }

    @PostMapping(path = "/addCourse", consumes = "application/json")
    public String addToSelectedCourses(@RequestBody(required = true) String jsonString, final HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        try {
            Properties properties = gson.fromJson(jsonString, Properties.class);
            String std_id =  CourseEnrolment.getInstance().getStudent().getStudentId();
            String courseId = properties.getProperty("courseId");
            String classCode = properties.getProperty("classCode");
            CourseEnrolment.getInstance().addToWeeklySchedule(std_id, courseId, classCode);
            return Config.OK_RESPONSE;
        } catch (Exception e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return null;
        }
    }

    @DeleteMapping("/deleteCourse")
    public String removeFromSelectedCourses(@RequestBody String jsonString, final HttpServletResponse response) throws IOException {
        Gson gson = new Gson();
        Properties properties = gson.fromJson(jsonString, Properties.class);
        String std_id =  CourseEnrolment.getInstance().getStudent().getStudentId();
        String courseId = properties.getProperty("courseId");
        String classCode = properties.getProperty("classCode");
        try {
            CourseEnrolment.getInstance().removeFromWeeklySchedule(std_id, courseId, classCode);
            return Config.OK_RESPONSE;
        } catch (Exception e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return null;
        }
    }

    @PostMapping(path = "/submitSelected")
    public String finalizeSelectedCourses(final HttpServletResponse response) throws IOException {
        try {
                CourseEnrolment.getInstance().finalizeWeeklySchedule(
                        CourseEnrolment.getInstance().getStudent().getStudentId()
                );

                CourseEnrolment.getInstance().getStudent().setLastSubmit(
                        CourseEnrolment.getInstance().getStudent().getSelectedCourses()
                );
            return Config.OK_RESPONSE;
        } catch (Exception e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return null;
        }
    }

    @PostMapping("/resetSelected")
    public String resetToLastSelectedCourses(final HttpServletResponse response) throws IOException {
        try {
            CourseEnrolment.getInstance().getStudent().setSelectedCourses(
                    CourseEnrolment.getInstance().getStudent().getLastSubmit()
            );
            return Config.OK_RESPONSE;
        } catch (Exception e) {
            response.sendError(HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return null;
        }
    }


    @GetMapping("/getLastSubmitedCourses")
    public int getLastSubmitedCourses(final HttpServletResponse response) throws IOException {
//        Gson gson = new Gson();
        try {
            if (CourseEnrolment.getInstance().getStudent() == null){
                response.sendRedirect("http://localhost:8080/login");
                return 0;
            }
            else {
                return CourseEnrolment.getInstance().getStudent().getLastSubmitedUnits();
            }
        } catch (Exception e) {
            response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
            return 0;
        }
    }


}