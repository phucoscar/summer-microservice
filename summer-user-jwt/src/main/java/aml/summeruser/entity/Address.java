package aml.summeruser.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "ward")
    private String ward;

    @OneToOne(mappedBy = "address", cascade = CascadeType.ALL)
    private SinhVien sinhVien;
}
