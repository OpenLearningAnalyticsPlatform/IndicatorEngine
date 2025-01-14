<%--
  ~ Open Platform Learning Analytics : Indicator Engine
  ~ Copyright (C) 2015  Learning Technologies Group, RWTH
  ~
  ~ This program is free software; you can redistribute it and/or
  ~ modify it under the terms of the GNU General Public License
  ~ as published by the Free Software Foundation; either version 2
  ~ of the License, or (at your option) any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
  --%>

<%--
  Created by IntelliJ IDEA.
  User: Tanmaya Mahapatra
  Date: 11-05-2015
  Time: 11:18
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Indicator Execution</title>
    <meta name="keywords" content="" />
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/templatemo_main.css">
</head>
<body>
<div class="navbar navbar-inverse" role="navigation">
    <div class="navbar-header">
        <div class="logo"><h1>Execution Results</h1></div>
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
            <h1>Execution Results</h1>
            <p>Here is the result of execution.</p>
            <div class="table-responsive">
                <h4 class="margin-bottom-15">Question Details</h4>
                <table class="table table-striped table-hover table-bordered">
                    <thead>
                    <tr>
                        <th>Question ID</th>
                        <th>Name</th>
                        <th>Number of Indicators</th>
                        <th>Last Executed on</th>
                        <th>Execution Counter</th>
                        <th>Owner</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td><c:out value="${question.questionId}"/></td>
                        <td><c:out value="${question.questionName}"/></td>
                        <td><c:out value="${question.numIndicators}"/></td>
                        <td><c:out value="${question.last_executionTime}"/></td>
                        <td><c:out value="${question.totalExecutions}"/></td>
                        <td><c:out value="${question.userName}"/></td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="table-responsive">
                <h4 class="margin-bottom-15">Associated Indicators Visualization</h4>
                <table class="table table-striped table-hover table-bordered">
                    <thead>
                    <tr>
                        <th>Indicator ID</th>
                        <th>Indicator Name</th>
                        <th>Last Executed on</th>
                        <th>Execution Counter</th>
                        <th>Chart Engine</th>
                        <th>Chart Type</th>
                        <th>Owner</th>
                        <th>Indicator Visualization</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="entityVal" items="${question.genQueries}"  varStatus="loop">
                        <tr>
                            <td><c:out value="${entityVal.queryID}"/></td>
                            <td><c:out value="${entityVal.indicatorName}"/></td>
                            <td><c:out value="${entityVal.genIndicatorProps.last_executionTime}"/></td>
                            <td><c:out value="${entityVal.genIndicatorProps.totalExecutions}"/></td>
                            <td><c:out value="${entityVal.genIndicatorProps.chartEngine}"/></td>
                            <td><c:out value="${entityVal.genIndicatorProps.chartType}"/></td>
                            <td><c:out value="${entityVal.genIndicatorProps.userName}"/></td>
                            <td><img src="/graphs/jgraph?indicator=${entityVal.indicatorName}" /><c:out value="${entityVal.queryID}"/></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
            <div class="margin-bottom-30">
                <div class="row">
                    <div class="col-md-12">
                        <ul class="nav nav-pills">
                            <li class="active"><a href="/indicators/viewall">Back<span class="badge"></span></a></li>
                        </ul>
                    </div>
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
    </div>


<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
<script src="${pageContext.request.contextPath}/js/Chart.min.js"></script>
<script src="${pageContext.request.contextPath}/js/templatemo_script.js"></script>
</body>
</html>
<%
    }
%>