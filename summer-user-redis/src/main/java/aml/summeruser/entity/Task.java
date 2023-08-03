package aml.summeruser.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String nameTask;

    @Column
    private String startDate;

    @Column
    private String endDate;

    @ManyToOne
    @JoinColumn(name = "sinhvien_id", nullable = false)
    private SinhVien sinhVien;
}
