package ua.university.sms.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ua.university.sms.model.entity.Enrollment;

import java.util.List;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    String GRADE_POINTS = "case"
            + " when e.grade = ua.university.sms.model.enums.Grade.A then 4.0"
            + " when e.grade = ua.university.sms.model.enums.Grade.B then 3.0"
            + " when e.grade = ua.university.sms.model.enums.Grade.C then 2.0"
            + " when e.grade = ua.university.sms.model.enums.Grade.D then 1.0"
            + " else 0.0 end";

    String GRADED = "e.grade <> ua.university.sms.model.enums.Grade.NA";

    boolean existsByStudentIdAndCourseIdAndSemesterAndStudyYear(Long studentId, Long courseId,
                                                                Integer semester, Integer studyYear);

    boolean existsByCourseId(Long courseId);

    @Query("select e from Enrollment e join fetch e.course where e.student.id = :studentId"
            + " order by e.studyYear, e.semester")
    List<Enrollment> findAllByStudentIdWithCourse(@Param("studentId") Long studentId);

    @Query("select avg(" + GRADE_POINTS + ") from Enrollment e where e.course.id = :courseId and " + GRADED)
    Double findAverageGpaByCourse(@Param("courseId") Long courseId);

    @Query("select count(e) from Enrollment e where e.course.id = :courseId and " + GRADED)
    long countGradedByCourse(@Param("courseId") Long courseId);

    @Query("select avg(" + GRADE_POINTS + ") from Enrollment e"
            + " where e.studyYear = :year and e.semester = :semester and " + GRADED)
    Double findAverageGpaBySemester(@Param("year") Integer year, @Param("semester") Integer semester);

    @Query("select count(e) from Enrollment e"
            + " where e.studyYear = :year and e.semester = :semester and " + GRADED)
    long countGradedBySemester(@Param("year") Integer year, @Param("semester") Integer semester);

    @Query("select e.student.id as studentId,"
            + " concat(e.student.firstName, ' ', e.student.lastName) as fullName,"
            + " sum((" + GRADE_POINTS + ") * e.course.credits) / sum(e.course.credits) as gpa"
            + " from Enrollment e where " + GRADED
            + " group by e.student.id, e.student.firstName, e.student.lastName"
            + " order by gpa desc")
    List<TopStudentProjection> findTopStudents(Pageable pageable);
}
