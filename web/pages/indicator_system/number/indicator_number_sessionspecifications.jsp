<%--
  ~ /*
  ~  * Copyright (C) 2015  Tanmaya Mahapatra
  ~  *
  ~  * This program is free software; you can redistribute it and/or
  ~  * modify it under the terms of the GNU General Public License
  ~  * as published by the Free Software Foundation; either version 2
  ~  * of the License, or (at your option) any later version.
  ~  *
  ~  * This program is distributed in the hope that it will be useful,
  ~  * but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~  * GNU General Public License for more details.
  ~  *
  ~  * You should have received a copy of the GNU General Public License
  ~  * along with this program; if not, write to the Free Software
  ~  * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  ~  */
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Tanmaya Mahapatra
  Date: 16-03-2015
  Time: 04:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%
    if ((session.getAttribute("loggedIn") == null) || (session.getAttribute("loggedIn") == ""))
        response.sendRedirect("/login");

    if ((session.getAttribute("loggedIn") != null) && (session.getAttribute("userName") != null) && (session.getAttribute("activationStatus")== "false"))
        response.sendRedirect("/activate");
    else{


%>
<html>
<head>
    <meta charset="utf-8">
    <!--[if IE]><meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1"><![endif]-->
    <title>Session Specifications</title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/templatemo_main.css">
    <script type="javascript" src="${pageContext.request.contextPath}/js/user_profile_checks.js"> </script>
    <script type="text/javascript" src="/dynamiclists/js/jquery-1.3.2.min.js"></script>

</head>
<body>
<div class="navbar navbar-inverse" role="navigation">
    <div class="navbar-header">
        <div class="logo"><h1>Session Specifications</h1></div>
        <button type="button" class="navbar-toggle" data-toggle="collapse" data-target=".navbar-collapse">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
    </div>
</div>
<div class="template-page-wrapper">
    <div class="navbar-collapse collapse templatemo-sidebar">
        <ul class="templatemo-sidebar-menu">
            <li>
                <form class="navbar-form">
                    <input type="text" class="form-control" id="templatemo_search_box" placeholder="Search...">
                    <span class="btn btn-default">Go</span>
                </form>
            </li>
            <li class="active"><a href="/home/dashboard"><i class="fa fa-home"></i>Dashboard</a></li>
            <li class="sub open">
                <a href="javascript:;">
                    <i class="fa fa-database"></i> Indicator System <div class="pull-right"><span class="caret"></span></div>
                </a>
                <ul class="templatemo-submenu">
                    <li><a href="/indicators/indicators_definition"><i class="fa fa-file"></i><span class="badge pull-right"></span>Define New</a></li>
                    <li><a href="/indicators/viewall"><i class="fa fa-th-large"></i><span class="badge pull-right"></span>View Existing</a></li>
                    <li><a href="/indicators/modify"><i class="fa fa-edit"></i><span class="badge pull-right"></span>Modify Existing</a></li>
                    <li><a href="/indicators/delete"><i class="fa fa-trash-o"></i><span class="badge pull-right"></span>Delete a Specific Indicator</a></li>
                    <li><a href="/indicators/trialrun"><i class="fa fa-play"></i><span class="badge pull-right"></span>Trial Run</a></li>
                </ul>
            </li>

            <li><a href="javascript:;" data-toggle="modal" data-target="#confirmModal"><i class="fa fa-sign-out"></i>Sign Out</a></li>
        </ul>
    </div><!--/.navbar-collapse -->

    <div class="templatemo-content-wrapper">
        <div class="templatemo-content">
            <ol class="breadcrumb">
                <li><a href="/home/dashboard">Dashboard</a></li>
                <li><a href="/indicators/home">Indicator Home</a></li>
            </ol>
            <h1>Specify Session Based Filters</h1>
            <p>Based on your previous selection, here you can choose  and
                specify upto 10 session based search criteria here.</p>
            <div class="row">
                <div class="col-md-12">
                    <form:form role="form" id="sessionSelection"  method="POST" commandName="selectNumberParameters" action="${flowExecutionUrl}">

                        <div class="col-md-6 col-sm-6 margin-bottom-30">
                            <div class="panel panel-primary">
                                <div class="panel-heading">Session Filtering Selection</div>
                                <div class="panel-body">
                                    <table class="table table-striped">
                                        <tbody>
                                        <tr>
                                            <div class="row">
                                                <div class="col-md-6 margin-bottom-15">
                                                    <label for="sessionSearchType">Select Session Search Type </label>
                                                    <form:select class="form-control margin-bottom-15" path="selectedsessionSearchType" items="${selectNumberParameters.sessionSearchType}" name ="sessionSearchType" id="sessionSearchType" />
                                                </div>
                                            </div>
                                        </tr>
                                        <tr>
                                            <div class="row">
                                                <div class="col-md-6 margin-bottom-15">
                                                    <label for="searchString" class="control-label">Filter Specification</label>
                                                    <form:input class="form-control" path="sessionSearch"  name="searchString" id ="searchString"/>
                                                </div>
                                            </div>
                                        </tr>

                                        <tr>
                                            <div class="col-md-12">
                                                <input type="submit" name="_eventId_searchSession"
                                                       value="Search" />
                                            </div>
                                        </tr>
                                        <tr>
                                            <div class="row">
                                                <div class="col-md-6 margin-bottom-15">
                                                    <label for="multipleSelect">Search Results </label>
                                                    <form:select class="form-control" multiple="true" path="selectedSearchStrings" name="multipleSelect">
                                                        <form:options items="${selectNumberParameters.searchResults}" />
                                                    </form:select>
                                                </div>
                                            </div>
                                        </tr>
                                        <tr>
                                            <div class="row templatemo-form-buttons">
                                                <div class="col-md-12">
                                                    <input type="submit" name="_eventId_specifySession"
                                                           value="Add Session Specification" />
                                                </div>
                                            </div>
                                        </tr>
                                        <tr>
                                            <div class="row templatemo-form-buttons">
                                                <div class="col-md-12">
                                                    <input type="submit" name="_eventId_clearSessionValues"
                                                           value="Delete All Specifications" />
                                                </div>
                                            </div>
                                        </tr>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-6 col-sm-6 margin-bottom-30">
                            <div class="panel panel-primary">
                                <div class="panel-heading">Entered Session Based Parameters</div>
                                <div class="panel-body">
                                    <table class="table table-striped">
                                        <thead>
                                        <tr>
                                            <th>S/L</th>
                                            <th>Session Values</th>
                                            <th>Search Type</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="SessionSpec" items="${selectNumberParameters.sessionSpecifications}"  varStatus="loop">
                                            <tr>
                                                <td>${loop.count}</td>
                                                <td><c:out value="${SessionSpec.session}"/></td>
                                                <td><c:out value="${SessionSpec.type}"/></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                        <div class="row templatemo-form-buttons">
                            <div class="col-md-12">
                                <input cclass="btn btn-default" type="submit" name="_eventId_prevScreen"
                                       value="Previous" />
                                <input class="btn btn-primary" type="submit" name="_eventId_sessionSpecified"
                                       value="Next" />
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
        </div>
    </div>
    <!-- Modal -->
    <div class="modal fade" id="confirmModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal"><span aria-hidden="true">&times;</span><span class="sr-only">Close</span></button>
                    <h4 class="modal-title" id="myModalLabel">Are you sure you want to sign out?</h4>
                </div>
                <div class="modal-footer">
                    <a href="/logoff" class="btn btn-primary">Yes</a>
                    <button type="button" class="btn btn-default" data-dismiss="modal">No</button>
                </div>
            </div>
        </div>
    </div>
    <footer class="templatemo-footer">
        <div class="templatemo-copyright">
            <p>Copyright &copy; 2015 Learning Technologies Group, RWTH</p>
        </div>
    </footer>
</div>

<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/Chart.min.js"></script>
<script src="${pageContext.request.contextPath}/js/templatemo_script.js"></script>
<script type="text/javascript">
</script>
</body>
</html>
<%
    }
%>