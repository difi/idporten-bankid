<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<main>
    <section class="Box">
        <div class="Box_main bankid-box">
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
        </div>

    </section>
</main>

