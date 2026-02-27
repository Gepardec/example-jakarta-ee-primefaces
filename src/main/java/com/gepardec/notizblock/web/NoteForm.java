package com.gepardec.notizblock.web;

import jakarta.ws.rs.FormParam;

public class NoteForm {
    @FormParam("id")
    public Long id;

    @FormParam("title")
    public String title;

    @FormParam("content")
    public String content;
}
