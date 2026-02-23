package io.ibuprofen.inventra_dd_be.aset.model;

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

    @Column(name = "lokasi_aset")
    private String lokasiAset;
}
