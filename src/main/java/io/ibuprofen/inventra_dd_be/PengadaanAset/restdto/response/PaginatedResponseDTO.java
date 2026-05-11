package io.ibuprofen.inventra_dd_be.PengadaanAset.restdto.response;

import java.util.List;

public class PaginatedResponseDTO<T> {
    public List<T> data;
    public long total_data;
    public int total_page;
    public int current_page;
    public int limit;

    public PaginatedResponseDTO(List<T> data, long totalData, int totalPage, int currentPage, int limit) {
        this.data = data;
        this.total_data = totalData;
        this.total_page = totalPage;
        this.current_page = currentPage;
        this.limit = limit;
    }
}