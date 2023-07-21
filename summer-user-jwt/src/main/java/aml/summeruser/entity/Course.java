package aml.summeruser.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "course")
@Data
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String description;

    @Column
    private String startDate;

    @Column
    private String endDate;

    @ManyToMany(mappedBy = "courses", fetch = FetchType.LAZY)
    private Set<SinhVien> sinhViens;
}
