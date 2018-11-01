<body>
<header>
    <nav class="top-nav light-blue darken-2">
        <div class="head-title">
            <a href="#" data-activates="nav-main" class="button-collapse">
                <i class="material-icons">menu</i>
            </a>
            <div class="nav-wrapper">
                <a class="page-title">OpenLAP - Indicator Editor
                    <a class="modal-trigger amber-text openlap-help-icon tooltipped" id="editorHelp" data-position="right" data-delay="50" data-tooltip="Click to see help related to OpenLAP - Indicator Editor"
                       href="#editorHelpModel" >
                        <i class="material-icons">help_outline</i>
                    </a>
                </a>
                <a style="padding-right: 20px;" class="page-title pull-right tooltipped" data-position="left" data-delay="50" data-tooltip="For best user experience, click here to open OpenLAP in new tab." target="_blank" href="https://openlap.cil.rwth-aachen.de:8002/indicators/define_new"><i class="material-icons">open_in_new</i></a>
            </div>
        </div>
    </nav>
    <%--<div class="container">--%>
    <%--</div>--%>
    <ul id="nav-main" class="side-nav fixed grey darken-2">
        <li class="bold">
            <a href="#" class="brand-logo"><img src="${pageContext.request.contextPath}/images/user.png" alt="User" class="circle responsive-img valign profile-image"></a>
        </li>
        <li id="nav-sub" class="no-padding">
            <ul class="collapsible collapsible-accordion">
                <li class="bold"><a class="collapsible-header waves-effect">Indicator</a>
                    <div class="collapsible-body">
                        <ul>
                            <li><a href="/indicators/define_new" class="waves-effect">Define New</a></li>
                            <li><a href="/indicators/view_existing" class="waves-effect">View Existing</a></li>
                        </ul>
                    </div>
                </li>

                <% if (session.getAttribute("role") == "Dev" || session.getAttribute("admin_access") == "YES") {%>
                    <li class="bold"><a class="collapsible-header  waves-effect">Administration Panel</a>
                        <div class="collapsible-body">
                            <ul>
                                <li><a href="/admin/analytics_method/new" class="waves-effect">Analytics Method</a></li>
                                <li><a href="/admin/visualization/new" class="waves-effect">Visualization</a></li>
                            </ul>
                        </div>
                    </li>
                <%}%>

            </ul>
        </li>
        <li class="no-padding">
            <a class="modal-trigger collapsible-header waves-effect" href="#confirmSignoutModal">Sign Out</a>
        </li>
    </ul>

    <div id="confirmSignoutModal" class="modal modal-fixed-footer">
        <div class="modal-content">
            <h4 class="modal-title" id="myModalLabel">Are you sure you want to sign out?</h4>
        </div>
        <div class="modal-footer">
            <a href="/logoff" class="waves-effect waves-light btn light-blue darken-2">Yes</a>
            <button type="button" class="modal-close waves-effect waves-light btn light-blue darken-2" data-dismiss="modal">No</button>
        </div>
    </div>
</header>