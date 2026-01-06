package com.bank.redirect;

/* © Copyright 2026 Olivier Planson. All rights reserved. Reproduction prohibited. Made with IBM Bob. */

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RootRedirectServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        redirect(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        redirect(req, resp);
    }

    private void redirect(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI(); // e.g. /api/clients
        String query = req.getQueryString();
        String target = "/web" + uri;
        if (query != null && !query.isEmpty()) {
            target += "?" + query;
        }
        resp.setStatus(HttpServletResponse.SC_FOUND); // 302
        resp.setHeader("Location", target);
    }
}
