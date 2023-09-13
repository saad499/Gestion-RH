package com.example.gestionemployes.entities;

import lombok.Data;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
public class PaginatedResponse<T> {
    private List<T> content;
    private Pageable pageable;
    private int totalPages;
    private int totalElements;
    private int page;
    private int size;
    private int pagenumber;

}
