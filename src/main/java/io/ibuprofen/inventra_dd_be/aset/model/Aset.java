package io.ibuprofen.inventra_dd_be.Aset.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@Entity
@Table(name = "aset")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Aset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_aset")
    private Long id;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "kode_aset", unique = true, nullable = false, columnDefinition = "VARCHAR(255)")
    private String kodeAset;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "nama_aset", nullable = false, columnDefinition = "VARCHAR(255)")
    private String namaAset;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "gambar_url_aset", columnDefinition = "TEXT")
    private String gambarUrlAset;

    @Enumerated(EnumType.STRING)
    @Column(name = "kategori_aset", nullable = false)
    private KategoriAset kategoriAset;

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "unit", columnDefinition = "VARCHAR(255)")
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aset", nullable = false)
    private StatusAset statusAset;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "keterangan_aset", columnDefinition = "TEXT")
    private String keteranganAset;
}
