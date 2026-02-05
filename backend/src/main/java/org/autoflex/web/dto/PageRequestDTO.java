package org.autoflex.web.dto;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.QueryParam;

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
}
