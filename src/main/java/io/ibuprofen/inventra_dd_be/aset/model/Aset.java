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

    @Column(name = "kode_aset", unique = true, nullable = false)
    private String kodeAset;

    @Column(name = "nama_aset", nullable = false)
    private String namaAset;

    @Column(name = "gambar_url_aset")
    private String gambarUrlAset;

    @Enumerated(EnumType.STRING)
    @Column(name = "kategori_aset", nullable = false)
    private KategoriAset kategoriAset;

    @Column(name = "unit")
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_aset", nullable = false)
    private StatusAset statusAset;

    @Column(name = "keterangan_aset", columnDefinition = "TEXT")
    private String keteranganAset;
}
