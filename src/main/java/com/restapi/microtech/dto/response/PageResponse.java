package com.restapi.microtech.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int size;
    private int start; // zero-based offset
    private int page;  // zero-based page index derived from start/size
    private int numberOfElements;
    private boolean hasNext;
    private boolean hasPrevious;

    public static <T> PageResponse<T> from(Page<T> pageData, int start, int size) {
        PageResponse<T> pr = new PageResponse<>();
        pr.setContent(pageData.getContent());
        pr.setTotalElements(pageData.getTotalElements());
        pr.setTotalPages(pageData.getTotalPages());
        pr.setSize(size);
        pr.setStart(start);
        pr.setPage(start / Math.max(1, size));
        pr.setNumberOfElements(pageData.getNumberOfElements());
        pr.setHasNext(pageData.hasNext());
        pr.setHasPrevious(pageData.hasPrevious());
        return pr;
    }
}
