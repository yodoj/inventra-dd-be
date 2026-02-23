package io.ibuprofen.inventra_dd_be.aset.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "aset_ruangan")
public class AsetRuangan extends Aset {

}
