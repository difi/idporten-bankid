<%@ page language="java" pageEncoding="utf-8" contentType="text/html;charset=utf-8" %>
<%
String service = request.getParameter("service");
String pageid = request.getParameter("pageid");
%>

<footer class="f-Main">
    <div class="f-Main_Content">
        <div class="f-Main_Logo" aria-hidden="true"></div>
        <div class="f-Main_Info">
            <a class="f-Main_Link" href="/opensso/support.jsp?service=<%= service%>&amp;pageid=<%= pageid%>" tabindex="200">Kontaktskjema</a>
            <a href="tel:+4780030300" tabindex="201" class="f-Main_Link"><span>Tlf: 800 30 300</span></a>
            <a target="_blank" class="f-Main_Link" href="http://eid.difi.no/nb/id-porten" tabindex="202">Hjelp til innlogging</a>
            <a target="_blank" class="f-Main_Link" href="http://eid.difi.no/nb/sikkerhet-og-personvern" tabindex="203">Sikkerhet og personvern</a>
            <div id="pageid" style="display:none"><%= pageid%></div>
	        <div id="version" style="display:none">Versjon: Under utvikling</div>
            <p>
                Driftet av
                <a target="_blank" id="difilink" href="http://www.difi.no" class="f-Main_Link" tabindex="204">Direktoratet for forvaltning og IKT (Difi)</a>
            </p>
        </div>
    </div>
</footer>