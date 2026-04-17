package io.ibuprofen.inventra_dd_be.Aset.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aset_barang")
public class AsetBarang extends Aset {

    @Column(name = "merk_aset")
    private String merkAset;

    @Column(name = "qty_aset", nullable = false)
    private Integer qtyAset;

    @Column(name = "qty_tersedia")
    private Integer qtyTersedia = 0;

    @Column(name = "qty_rusak")
    private Integer qtyRusak = 0;

    @Column(name = "qty_perbaikan")
    private Integer qtyPerbaikan = 0;

    @Column(name = "qty_dimusnahkan")
    private Integer qtyDimusnahkan = 0;

    @Column(name = "qty_dipinjam")
    private Integer qtyDipinjam = 0;

    @Column(name = "lokasi_aset")
    private String lokasiAset;
}
