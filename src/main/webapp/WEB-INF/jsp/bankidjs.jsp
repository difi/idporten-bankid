<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@include file="includes_idporten/Imports.jspf" %>
<%@include file="includes_idporten/PreHeaderChainStart.jspf" %>

<jsp:include page="includes_idporten/header.jsp"/>

<main>

    <section class="Box">
        <jsp:include page="includes_idporten/serviceprovider.jsp"/>

        <div class="Box_header">
            <h1 class="Box_header-title with-logo logo-eid-gray">
                Logg inn med BankID</h1>
        </div>

        <div class="Box_main bankid-box">
            <p>Cid: <%= request.getAttribute("cid")%></p>
            <c:choose>
                <c:when test="${not empty helperURI}">
                    <div id="iframewrapper" class="bankid-inner-frame"></div>
                    <script type="text/javascript" src="<%= request.getAttribute("helperURI") %>"></script>
                    <script type="text/javascript">
                        bankidhelper.init({
                            frameMode: 'iframe',
                            cid: '<%= request.getAttribute("cid") %>',
                            containerID: 'iframewrapper',
                            width: '100%',
                            height: '100%'
                        });
                    </script>
                </c:when>
                <c:otherwise>
                    <div class="bankid-inner-frame bankid-black-list-margin">

                        <div class="Box_Section">
                            <p><strong>Nettleseren du bruker er utdatert</strong>
                            </p>
                            <p>Nettleseren du bruker er utdatert og st\u00F8tter ikke BankID 2.0. Dette er fordi den har kjente, alvorlige sikkerhetshull. Oppdater nettleseren din og pr\u00F8v igjen, eller velg en annen elektronisk ID (eID) i ID-porten.</p>
                        </div>
                        <div class="fm-Controls with-Action">
                            <a role="button" class="btn btn-Action" href="<idporten:closeUrl/>" onKeyPress="handleButtonKeyPress(event)" tabindex="1">
                                <span>Velg ny eID</span>
                            </a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>


        <div class="Box_footer">
            <div class="Box_footer-links">
                <ul>
                    <li>
                        <a target="_blank" href="https://www.bankid.no/Dette-er-BankID/Dette-er-BankID/">
                            <span>Slik skaffer du deg BankID p\u00E5 mobil</span>
                        </a>
                    </li>
                </ul>
            </div>

        </div>
    </section>
</main>

<%
    String pageid = "BID";
%>
<jsp:include page="includes_idporten/footerlinks.jsp">
    <jsp:param name="pageid" value="<%=pageid%>"/>
</jsp:include>
<jsp:include page="includes_idporten/javascripts.jsp"/>
<jsp:include page="includes_idporten/footer.jsp"/>
