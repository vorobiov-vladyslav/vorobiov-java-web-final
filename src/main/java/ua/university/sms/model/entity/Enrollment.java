package ua.university.sms.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ua.university.sms.model.Payable;
import ua.university.sms.model.enums.Grade;

@Entity
@Table(name = "enrollment")
public class Enrollment implements Payable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @JdbcTypeCode(SqlTypes.SMALLINT)
    @Column(nullable = false)
    private Integer semester;

    @Column(name = "study_year", nullable = false)
    private Integer studyYear;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    private Grade grade = Grade.NA;

    @Column(nullable = false)
    private boolean paid;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public Grade getGrade() {
        return grade;
    }

    public void setGrade(Grade grade) {
        this.grade = grade;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public boolean isPaid() {
        return paid;
    }

    @Override
    public void markPaid() {
        this.paid = true;
    }
}
