package com.polytech.olympic_medals.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageResponse<T> {

    private List<T> contenu;
    private int pageActuelle;
    private int totalPages;
    private long totalElements;
    private int taillePage;
    private boolean premiere;
    private boolean derniere;

    public static <T> PageResponse<T> from(Page<T> page) {
        return PageResponse.<T>builder()
                .contenu(page.getContent())
                .pageActuelle(page.getNumber())
                .totalPages(page.getTotalPages())
                .totalElements(page.getTotalElements())
                .taillePage(page.getSize())
                .premiere(page.isFirst())
                .derniere(page.isLast())
                .build();
    }
}