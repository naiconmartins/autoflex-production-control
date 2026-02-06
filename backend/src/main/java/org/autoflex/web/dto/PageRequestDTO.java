package org.autoflex.web.dto;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PageRequestDTO {
    @QueryParam("page")
    @DefaultValue("0")
    public int page;

    @QueryParam("size")
    @DefaultValue("10")
    public int size;

    @QueryParam("sort")
    @DefaultValue("name")
    public String sortBy;

    @QueryParam("dir")
    @DefaultValue("asc")
    public String direction;

    public PageRequestDTO(int page, int size, String sortBy, String direction) {
        this.page = page;
        this.size = size;
        this.sortBy = sortBy;
        this.direction = direction;
    }
}
