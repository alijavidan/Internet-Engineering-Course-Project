package com.internet.engineering.IECA7.repository.Grade;

import com.internet.engineering.IECA7.domain.CourseEnrolment.Student.Grade;
import com.internet.engineering.IECA7.repository.IMapper;
import com.internet.engineering.IECA7.utils.CustomPair;

import java.sql.SQLException;
import java.util.List;

public interface IGradeMapper extends IMapper<Grade, CustomPair> {
        List<Grade> getAllGradesByStudentId(String student_id) throws SQLException;
        List<Grade> getAll() throws SQLException;
}