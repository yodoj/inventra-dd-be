package io.ibuprofen.inventra_dd_be.Aset.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaFixer implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("Checking and fixing database schema types...");
        try {
            // Fix columns that might be stuck as bytea from previous Hibernate
            // auto-generation
            jdbcTemplate.execute("ALTER TABLE aset ALTER COLUMN kode_aset TYPE VARCHAR(255) USING kode_aset::text");
            jdbcTemplate.execute("ALTER TABLE aset ALTER COLUMN nama_aset TYPE VARCHAR(255) USING nama_aset::text");
            jdbcTemplate.execute("ALTER TABLE aset ALTER COLUMN unit TYPE VARCHAR(255) USING unit::text");
            jdbcTemplate.execute("ALTER TABLE aset ALTER COLUMN keterangan_aset TYPE TEXT USING keterangan_aset::text");
            jdbcTemplate.execute("ALTER TABLE aset ALTER COLUMN gambar_url_aset TYPE TEXT USING gambar_url_aset::text");

            System.out.println("Database schema types fixed successfully.");
        } catch (Exception e) {
            System.err.println(
                    "Warning: Could not fix schema automatically (it might be already correct): " + e.getMessage());
        }
    }
}
