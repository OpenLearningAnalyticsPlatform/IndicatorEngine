
/*
 * Open Platform Learning Analytics : Indicator Engine
 * Copyright (C) 2015  Learning Technologies Group, RWTH
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.indicator_engine.controller;

import com.google.gson.*;
import com.indicator_engine.dao.*;
import com.indicator_engine.datamodel.*;
import com.indicator_engine.graphgenerator.cewolf.PageViewCountData;
import com.indicator_engine.indicator_system.IndicatorPreProcessing;
import com.indicator_engine.indicator_system.Number.OperationNumberProcessorDao;
import com.indicator_engine.misc.NumberChecks;
import com.indicator_engine.model.app.QuestionRun;
import com.indicator_engine.model.app.SearchIndicatorForm;
import com.indicator_engine.model.indicator_system.IndicatorDeletionForm;
import com.indicator_engine.model.indicator_system.Number.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by Tanmaya Mahapatra on 23-03-2015.
 */
@Controller
@Scope("session")
@SessionAttributes({"loggedIn", "userName", "sid", "activationStatus","role", "admin_access"})
@RequestMapping(value="/indicators")
@SuppressWarnings({"unused", "unchecked"})
public class GAIndicatorSystemController {

    @Autowired
    private ApplicationContext appContext;
    @Autowired
    private PageViewCountData PageViews;
    static Logger log = Logger.getLogger(GAIndicatorSystemController.class.getName());

    @RequestMapping(value = "/home", method = RequestMethod.GET)
    public ModelAndView getIndicatorsHome() {
        return new ModelAndView("indicator_system/indicators_home");
    }

    @RequestMapping(value = "/indicators_definition", method = RequestMethod.GET)
    public String getNewIndicatorDefinitionHome(Map<String, Object> model) {
        IndicatorPreProcessing indicatorPreProcessor = (IndicatorPreProcessing) appContext.getBean("indicatorPreProcessor");
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        SelectNumberParameters selectNumberParameters = indicatorPreProcessor.initSelectNumberParametersObject();
        model.put("selectNumberParameters",selectNumberParameters);
        return "indicator_system/number/question_indicator_editor";
    }

    @RequestMapping(value = "/initSources", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_initSources(Model model) {

        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        Gson gson = new Gson();
        return gson.toJson(glaEventBean.selectAll("source"));
    }

    @RequestMapping(value = "/initAction", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_initAction(Model model) {

        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        Gson gson = new Gson();
        return gson.toJson(glaEventBean.selectAll("action"));
    }
    @RequestMapping(value = "/initPlatform", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_initPlatform(Model model) {

        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        Gson gson = new Gson();
        return gson.toJson(glaEventBean.selectAll("platform"));
    }

    @RequestMapping(value = "/validateQName", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_validateQuestionName(
            @RequestParam(value="qname", required = true) String questionName, Model model) {

        String status = null;
        GLAQuestionDao glaQuestionBean = (GLAQuestionDao) appContext.getBean("glaQuestions");
        List<GLAQuestion> glaQuestions = glaQuestionBean.displayAll(null, null, false);
        if (questionName == null)
           status = "null";
        else{
            if(questionName.length() < 6) {
                status = "short";
            }
            else {

                for (GLAQuestion gQuestion : glaQuestions) {
                    if (questionName.equals(gQuestion.getQuestion_name())) {
                        status = "exists";
                        break;
                    }
                }
            }
        }
        return  status;
    }

    @RequestMapping(value = "/validateIndName", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_validateIndicatorName(
            @RequestParam(value="indname", required = true) String indicatorName, Model model) {

        String status = null;
        GLAIndicatorDao glaIndicatorBean = (GLAIndicatorDao) appContext.getBean("glaIndicator");
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        List<GLAIndicator> glaIndicatorList = glaIndicatorBean.displayall(null, null, false);
        if (indicatorName.isEmpty())
            status = "null";
        else{
            if(indicatorName.length() < 6) {
                status = "short";
            }
            else {
                for (GLAIndicator glaIndicator : glaIndicatorList) {
                    if (indicatorName.equals(glaIndicator.getIndicator_name())) {
                        status = "exists";
                        break;
                    }
                }
                for(GenQuery genQuery : entitySpecificationBean.getQuestionsContainer().getGenQueries()) {
                    if(genQuery.getIndicatorName().equals(indicatorName)) {
                        status = "exists";
                        break;
                    }
                }
            }
        }
        return status;
    }

    @RequestMapping(value = "/populateCategories", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_populateCategoryTypes(@RequestParam(value="action", required = true) String action,
                                                      @RequestParam(value="platform", required = true) String platform,
                                                      @RequestParam(value="sources", required = true) List<String> sources,
                                                      Model model) {

        IndicatorPreProcessing indicatorPreProcessor = (IndicatorPreProcessing)
                appContext.getBean("indicatorPreProcessor");
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        List<Categories> categoriesList = new ArrayList<>();
        Gson gson = new Gson();

        entitySpecificationBean.setSelectedAction(action);
        entitySpecificationBean.setSelectedPlatform(platform);
        entitySpecificationBean.setSelectedSource(sources);

        List<String> types = indicatorPreProcessor.initPopulateTypes(sources, action, platform);
        List<String> majors = indicatorPreProcessor.initPopulateMajors(sources, action, platform);
        List<String> minors = indicatorPreProcessor.initPopulateMinors(sources, action, platform);

        for ( int i =0 ; i < minors.size();i++ ){
            categoriesList.add(new Categories(types.get(i),majors.get(i), minors.get(i)));
        }
        return gson.toJson(categoriesList);
    }

    @RequestMapping(value = "/populateEntities", method = RequestMethod.GET)
    public @ResponseBody
    String processAJAXRequest_populateEntities(@RequestParam(value="minor", required = true) String minor,
                                                    Model model) {
        IndicatorPreProcessing indicatorPreProcessor = (IndicatorPreProcessing)
                appContext.getBean("indicatorPreProcessor");
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.setSelectedMinor(minor);
        List<String> keys = indicatorPreProcessor.initAvailableEntities_DB(minor);
        return gson.toJson(keys);
    }

        @RequestMapping(value = "/addEntity", method = RequestMethod.GET)
        public @ResponseBody
        String  addEntity(@RequestParam(value="key", required = true) String key,
                          @RequestParam(value="search", required = true) String search,
                          @RequestParam(value="value", required = true) String value,
                          Model model) {
            Gson gson = new Gson();
            EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
            entitySpecificationBean.getEntityValues().add(new EntityValues(key,
                    search, value));
            return gson.toJson(entitySpecificationBean.getEntityValues());

    }

    @RequestMapping(value = "/getEntities", method = RequestMethod.GET)
    public @ResponseBody
    String  getEntities(Model model, @RequestParam(value="size", required = false) String size) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        if(size != null && size.equals("Y"))
            return gson.toJson(entitySpecificationBean.getEntityValues().size());
        return gson.toJson(entitySpecificationBean.getEntityValues());
    }
    @RequestMapping(value = "/deleteEntities", method = RequestMethod.GET)
    public @ResponseBody
    String  deleteEntities(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.getEntityValues().clear();
        return gson.toJson(entitySpecificationBean.getEntityValues());
    }

    @RequestMapping(value = "/searchUser", method = RequestMethod.GET)
    public @ResponseBody
    String  searchUsers(@RequestParam(value="keyword", required = true) String keyword,
                      @RequestParam(value="searchtype", required = true) String searchtype,
                      Model model) {
        GLAUserDao glaUserBean = (GLAUserDao) appContext.getBean("glaUser");
        Gson gson = new Gson();
        List<String> searchResults = glaUserBean.searchSimilarUserDetails(searchtype, keyword);
        return gson.toJson(searchResults);
    }

    @RequestMapping(value = "/addUserFilter", method = RequestMethod.GET)
    public @ResponseBody
    String  addUserFilter(@RequestParam(value="userdata", required = true) String userdata,
                        @RequestParam(value="searchType", required = true) String searchType,
                          @RequestParam(value="userType", required = true) String userType,
                        Model model) {
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        Gson gson = new Gson();
        entitySpecificationBean.getUserSpecifications().add(new UserSearchSpecifications(userType, userdata, searchType));
        return gson.toJson(entitySpecificationBean.getUserSpecifications());

    }

    @RequestMapping(value = "/getUserFilters", method = RequestMethod.GET)
    public @ResponseBody
    String  getUserFilters(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        return gson.toJson(entitySpecificationBean.getUserSpecifications());

    }

    @RequestMapping(value = "/deleteUserFilters", method = RequestMethod.GET)
    public @ResponseBody
    String  deleteUserFilters(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.getUserSpecifications().clear();
        return gson.toJson(entitySpecificationBean.getUserSpecifications());
    }

    @RequestMapping(value = "/searchTime", method = RequestMethod.GET)
    public @ResponseBody
    String  searchTime(@RequestParam(value="searchTime", required = true) String time,
                        @RequestParam(value="timeType", required = true) String timeType,
                        Model model) {
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        Gson gson = new Gson();
        List<String> searchResults = glaEventBean.searchSimilarTimeDetails(timeType, time);
        return gson.toJson(searchResults);
    }

    @RequestMapping(value = "/addTimeFilter", method = RequestMethod.GET)
    public @ResponseBody
    String  addTimeFilter(@RequestParam(value="time", required = true) List<String> time,
                          @RequestParam(value="timeType", required = true) String timeType,
                          Model model) {
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        Gson gson = new Gson();
        entitySpecificationBean.getTimeSpecifications().add(new TimeSearchSpecifications(timeType,time));
        return gson.toJson(entitySpecificationBean.getTimeSpecifications());
    }

    @RequestMapping(value = "/getTimeFilters", method = RequestMethod.GET)
    public @ResponseBody
    String  getTimeFilters(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        return gson.toJson(entitySpecificationBean.getTimeSpecifications());

    }

    @RequestMapping(value = "/deleteTimeFilters", method = RequestMethod.GET)
    public @ResponseBody
    String  deleteTimeFilters(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.getTimeSpecifications().clear();
        return gson.toJson(entitySpecificationBean.getTimeSpecifications());
    }

    @RequestMapping(value = "/searchSession", method = RequestMethod.GET)
    public @ResponseBody
    String  searchSessions(@RequestParam(value="keyword", required = true) String keyword,
                        @RequestParam(value="searchType", required = true) String searchType,
                        Model model) {
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        Gson gson = new Gson();
        List<String> searchResults = glaEventBean.searchSimilarSessionDetails(searchType, keyword);
        return gson.toJson(searchResults);
    }


    @RequestMapping(value = "/addSessionFilter", method = RequestMethod.GET)
    public @ResponseBody
    String  addSessionFilter(@RequestParam(value="sessionData", required = true) String sessionData,
                          @RequestParam(value="searchType", required = true) String searchType,

                          Model model) {
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        Gson gson = new Gson();
        entitySpecificationBean.getSessionSpecifications().add(new SessionSpecifications(searchType, sessionData));
        return gson.toJson(entitySpecificationBean.getUserSpecifications());

    }
    @RequestMapping(value = "/getSessionFilters", method = RequestMethod.GET)
    public @ResponseBody
    String  getSessionFilters(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        return gson.toJson(entitySpecificationBean.getSessionSpecifications());

    }
    @RequestMapping(value = "/deleteSessionFilters", method = RequestMethod.GET)
    public @ResponseBody
    String  deleteSessionFilters(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.getSessionSpecifications().clear();
        return gson.toJson(entitySpecificationBean.getSessionSpecifications());
    }

    @RequestMapping(value = "/viewall", method = RequestMethod.GET)
    public String getIndicatorsViewAll(Map<String, Object> model) {
        SearchIndicatorForm searchIndicatorForm = new SearchIndicatorForm();
        model.put("searchIndicatorForm", searchIndicatorForm);
        return  "indicator_system/viewall_indicators";
    }

    @RequestMapping(value = "/refreshGraph", method = RequestMethod.GET)
    public @ResponseBody
    String  refreshGraph(@RequestParam(value="questionName", required = true) String questionName,
                         @RequestParam(value="indicatorName", required = true) String indicatorName,
                         @RequestParam(value="graphType", required = true) String graphType,
                         @RequestParam(value="graphEngine", required = true) String graphEngine,
                         Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        GLAEntityDao glaEntityBean = (GLAEntityDao) appContext.getBean("glaEntity");
        GLACategoryDao glaCategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        OperationNumberProcessorDao operationNumberProcessorBean =  (OperationNumberProcessorDao) appContext.getBean("operationNumberProcessor");
        entitySpecificationBean.setSelectedChartType(graphType);
        entitySpecificationBean.setSelectedChartEngine(graphEngine);
        entitySpecificationBean.setQuestionName(questionName);
        entitySpecificationBean.setIndicatorName(indicatorName);
        GLACategory glaCategory = glaCategoryBean.loadCategoryByName(entitySpecificationBean.getSelectedMinor());
        entitySpecificationBean.setSelectedMajor(glaCategory.getMajor());
        entitySpecificationBean.setSelectedType(glaCategory.getType());
        operationNumberProcessorBean.computeResult(entitySpecificationBean);
        long result = glaEntityBean.findNumber(entitySpecificationBean.getHql());
        log.info("Dumping Result \n" + result);
        return gson.toJson("true");
    }
    @RequestMapping(value = "/finalize", method = RequestMethod.GET)
    public @ResponseBody
    String  finalizeIndicator(@RequestParam(value="questionName", required = true) String questionName,
                              @RequestParam(value="indicatorName", required = true) String indicatorName,
                              @RequestParam(value="graphType", required = true) String graphType,
                              @RequestParam(value="graphEngine", required = true) String graphEngine,
                              Model model) {
        Gson gson = new Gson();
        GLACategoryDao glaCategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        OperationNumberProcessorDao operationNumberProcessorBean =  (OperationNumberProcessorDao) appContext.getBean("operationNumberProcessor");
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.setSelectedChartType(graphType);
        entitySpecificationBean.setSelectedChartEngine(graphEngine);
        //entitySpecificationBean.setQuestionName(questionName);
        entitySpecificationBean.setIndicatorName(indicatorName);
        //If user Directly finalizes the Indicator, then we have to implicitly generate the HQL
        if(entitySpecificationBean.getHql() == null ) {
            entitySpecificationBean.setQuestionName(questionName);
            GLACategory glaCategory = glaCategoryBean.loadCategoryByName(entitySpecificationBean.getSelectedMinor());
            entitySpecificationBean.setSelectedMajor(glaCategory.getMajor());
            entitySpecificationBean.setSelectedType(glaCategory.getType());
            operationNumberProcessorBean.computeResult(entitySpecificationBean);
        }

        List<EntityValues>  xMLentityValues = new ArrayList<EntityValues>(entitySpecificationBean.getEntityValues().size());
        Iterator<EntityValues> entityIterator = entitySpecificationBean.getEntityValues().iterator();
        while(entityIterator.hasNext()){
            xMLentityValues.add(entityIterator.next().clone());
        }
        List<UserSearchSpecifications>  xMlUserSpecifications = new ArrayList<UserSearchSpecifications>(entitySpecificationBean.getUserSpecifications().size());
        Iterator<UserSearchSpecifications> userSpecIterator = entitySpecificationBean.getUserSpecifications().iterator();
        while(userSpecIterator.hasNext()){
            xMlUserSpecifications.add(userSpecIterator.next().clone());
        }

        List<SessionSpecifications>  xMLSessionSpecifications = new ArrayList<SessionSpecifications>(entitySpecificationBean.getSessionSpecifications().size());
        Iterator<SessionSpecifications> sessionSpecIterator = entitySpecificationBean.getSessionSpecifications().iterator();
        while(sessionSpecIterator.hasNext()){
            xMLSessionSpecifications.add(sessionSpecIterator.next().clone());
        }
        List<TimeSearchSpecifications>  xMLTimeSpecifications = new ArrayList<TimeSearchSpecifications>(entitySpecificationBean.getTimeSpecifications().size());
        Iterator<TimeSearchSpecifications> timeSpecIterator = entitySpecificationBean.getTimeSpecifications().iterator();
        while(timeSpecIterator.hasNext()){
            xMLTimeSpecifications.add(timeSpecIterator.next().clone());
        }


        if(entitySpecificationBean.getQuestionsContainer().getGenQueries().size() == 0 ) {
            Questions questions = new Questions();
            GenIndicatorProps genIndicatorProps = new GenIndicatorProps();
            genIndicatorProps.setChartEngine(entitySpecificationBean.getSelectedChartEngine());
            genIndicatorProps.setChartType(entitySpecificationBean.getSelectedChartType());
            IndicatorXMLData indicatorXMLData = new IndicatorXMLData(entitySpecificationBean.getSelectedSource(), entitySpecificationBean.getSelectedAction(),
                    entitySpecificationBean.getSelectedPlatform(), entitySpecificationBean.getSelectedMajor(), entitySpecificationBean.getSelectedMinor(),
                    entitySpecificationBean.getFilteringType(),xMLentityValues, xMlUserSpecifications, xMLSessionSpecifications, xMLTimeSpecifications, entitySpecificationBean.getSelectedChartType(),
                    entitySpecificationBean.getSelectedChartEngine());
            questions.setQuestionName(entitySpecificationBean.getQuestionName());
            questions.getGenQueries().add(new GenQuery(entitySpecificationBean.getHql(),entitySpecificationBean.getIndicatorName(),1, indicatorXMLData, genIndicatorProps));
            entitySpecificationBean.setQuestionsContainer(questions);
        }
        else if(entitySpecificationBean.getQuestionsContainer().getGenQueries().size() >= 1) {
            IndicatorXMLData indicatorXMLData = new IndicatorXMLData(entitySpecificationBean.getSelectedSource(), entitySpecificationBean.getSelectedAction(),
                    entitySpecificationBean.getSelectedPlatform(), entitySpecificationBean.getSelectedMajor(), entitySpecificationBean.getSelectedMinor(),
                    entitySpecificationBean.getFilteringType(), xMLentityValues, xMlUserSpecifications, xMLSessionSpecifications, xMLTimeSpecifications, entitySpecificationBean.getSelectedChartType(),
                    entitySpecificationBean.getSelectedChartEngine());
            GenIndicatorProps genIndicatorProps = new GenIndicatorProps();
            genIndicatorProps.setChartEngine(entitySpecificationBean.getSelectedChartEngine());
            genIndicatorProps.setChartType(entitySpecificationBean.getSelectedChartType());
            entitySpecificationBean.getQuestionsContainer().getGenQueries().add(new GenQuery(entitySpecificationBean.getHql(),entitySpecificationBean.getIndicatorName(),1, indicatorXMLData, genIndicatorProps));

        }
        return gson.toJson(entitySpecificationBean.getQuestionsContainer());
    }

    @RequestMapping(value = "/addNewIndicator", method = RequestMethod.GET)
    public @ResponseBody
    String  addNewIndicator(Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        entitySpecificationBean.reset();
        return gson.toJson(entitySpecificationBean.getQuestionsContainer());
    }

    @RequestMapping(value = "/refreshQuestionSummary", method = RequestMethod.GET)
    public @ResponseBody
    String  refreshQuestionSummary(@RequestParam(value="indName" ,required = false)String indicatorName, Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        if(indicatorName == null) {
            return gson.toJson(entitySpecificationBean.getQuestionsContainer());
        }
        else {
            Questions questions = entitySpecificationBean.getQuestionsContainer();
            for(GenQuery genQuery : questions.getGenQueries()){
                if(genQuery.getIndicatorName().equals(indicatorName))
                    return gson.toJson(genQuery);
            }
        }
        return null;
    }
    @RequestMapping(value = "/refreshCurrentIndicator", method = RequestMethod.GET)
    public @ResponseBody
    String  refreshCurrentIndicator(Model model) {

        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        CurrentIndicatorSummary currentIndicatorSummary = new CurrentIndicatorSummary(entitySpecificationBean.getIndicatorName(), entitySpecificationBean.getSelectedPlatform(),
                entitySpecificationBean.getSelectedAction(), entitySpecificationBean.getSelectedChartType(), entitySpecificationBean.getSelectedChartEngine(), entitySpecificationBean.getHql(),
                entitySpecificationBean.getEntityValues().size(), entitySpecificationBean.getUserSpecifications().size(), entitySpecificationBean.getSessionSpecifications().size(),
                entitySpecificationBean.getTimeSpecifications().size());
        return gson.toJson(currentIndicatorSummary);

    }

    @RequestMapping(value = "/deleteIndFromQn", method = RequestMethod.GET)
    public @ResponseBody
    String  deleteIndicatorFromQn(@RequestParam(value="indName" ,required = false)String indicatorName, Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        String msg = null;
        if (indicatorName == null) {
           msg = "Error Deletion Indicator. No Indicator Specified";
        } else {
            for (Iterator<GenQuery> genQuery = entitySpecificationBean.getQuestionsContainer().getGenQueries().iterator(); genQuery.hasNext(); ) {
                GenQuery agenQuery = genQuery.next();
                if (agenQuery.getIndicatorName().equals(indicatorName)) {
                    msg=agenQuery.getIndicatorName()+" Successfully Deleted from Current Question Set. Please press the Refesh Button";
                    genQuery.remove();
                }
            }
        }
        return gson.toJson(msg);
    }

    @RequestMapping(value = "/loadIndFromQnSetToEditor", method = RequestMethod.GET)
    public @ResponseBody
    String  loadIndicatorFromQn(@RequestParam(value="indName" ,required = false)String indicatorName, Model model) {
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        String msg = null;
        if (indicatorName != null) {
            for (Iterator<GenQuery> genQuery = entitySpecificationBean.getQuestionsContainer().getGenQueries().iterator(); genQuery.hasNext(); ) {
                GenQuery agenQuery = genQuery.next();
                if (agenQuery.getIndicatorName().equals(indicatorName)) {
                    entitySpecificationBean.setEntityValues(new ArrayList<EntityValues>(agenQuery.getIndicatorXMLData().getEntityValues().size()));
                    Iterator<EntityValues> entityIterator = agenQuery.getIndicatorXMLData().getEntityValues().iterator();
                    while(entityIterator.hasNext()) {
                        entitySpecificationBean.getEntityValues().add(entityIterator.next().clone());
                    }
                    entitySpecificationBean.setUserSpecifications(new ArrayList<UserSearchSpecifications>(entitySpecificationBean.getUserSpecifications().size()));
                    Iterator<UserSearchSpecifications> userSpecIterator = agenQuery.getIndicatorXMLData().getUserSpecifications().iterator();
                    while(userSpecIterator.hasNext()){
                        entitySpecificationBean.getUserSpecifications().add(userSpecIterator.next().clone());
                    }
                    entitySpecificationBean.setSessionSpecifications(new ArrayList<SessionSpecifications>(entitySpecificationBean.getSessionSpecifications().size()));
                    Iterator<SessionSpecifications> sessionSpecIterator = agenQuery.getIndicatorXMLData().getSessionSpecifications().iterator();
                    while(sessionSpecIterator.hasNext()){
                        entitySpecificationBean.getSessionSpecifications().add(sessionSpecIterator.next().clone());
                    }
                    entitySpecificationBean.setTimeSpecifications(new ArrayList<TimeSearchSpecifications>(entitySpecificationBean.getTimeSpecifications().size()));
                    Iterator<TimeSearchSpecifications> timeSpecIterator = agenQuery.getIndicatorXMLData().getTimeSpecifications().iterator();
                    while(timeSpecIterator.hasNext()){
                        entitySpecificationBean.getTimeSpecifications().add(timeSpecIterator.next().clone());
                    }
                    genQuery.remove();
                    return gson.toJson(agenQuery);
                }
            }
        }
        return null;
    }

    @RequestMapping(value = "/saveQuestionDB", method = RequestMethod.GET)
    public @ResponseBody
    String  saveQnToDB(@RequestParam(value="userName" ,required = true)String userName,
                       Model model) {

        log.info("Saving Indicator and all its Questions/Queries : STARTED " );
        Gson gson = new Gson();
        EntitySpecification entitySpecificationBean = (EntitySpecification) appContext.getBean("entitySpecifications");
        GLAQuestionDao glaQuestionBean = (GLAQuestionDao) appContext.getBean("glaQuestions");
        Calendar calendar = Calendar.getInstance();
        java.util.Date now = calendar.getTime();
        Set<GLAIndicator> glaIndicatorHashSet = new HashSet<GLAIndicator>();
        //Create the Question
        GLAQuestion glaQuestion = new GLAQuestion();
        glaQuestion.setIndicators_num(entitySpecificationBean.getQuestionsContainer().getGenQueries().size());
        glaQuestion.setQuestion_name(entitySpecificationBean.getQuestionsContainer().getQuestionName());
        //Create the Question Properties
        GLAQuestionProps glaQuestionProps = new GLAQuestionProps();
        glaQuestionProps.setUserName(userName);
        glaQuestionProps.setGlaQuestion(glaQuestion);
        glaQuestionProps.setLast_executionTime(new java.sql.Timestamp(now.getTime()));
        glaQuestionProps.setTotalExecutions(1);
        glaQuestion.setGlaQuestionProps(glaQuestionProps);
        for(GenQuery genQuery : entitySpecificationBean.getQuestionsContainer().getGenQueries())
        {
            //Creating & Settings a Indicator
            GLAIndicator glaIndicator = new GLAIndicator();
            glaIndicator.setIndicator_name(genQuery.getIndicatorName());
            glaIndicator.setHql(genQuery.getQuery());
            //Creating  &  Setting its Properties
            GLAIndicatorProps glaIndicatorProps = new GLAIndicatorProps();
            glaIndicatorProps.setComposite(false);
            glaIndicatorProps.setTotalExecutions(1);
            glaIndicatorProps.setLast_executionTime(new java.sql.Timestamp(now.getTime()));
            glaIndicatorProps.setUserName(userName);
            glaIndicatorProps.setJson_data(gson.toJson(genQuery.getIndicatorXMLData()));
            glaIndicatorProps.setChartEngine(genQuery.getGenIndicatorProps().getChartEngine());
            glaIndicatorProps.setChartType(genQuery.getGenIndicatorProps().getChartType());
            glaIndicatorProps.setGlaIndicator(glaIndicator);
            // Pushing this property set to the Indicator
            glaIndicator.setGlaIndicatorProps(glaIndicatorProps);
            //Adding to the Hashset
            glaIndicatorHashSet.add(glaIndicator);        }

        glaQuestionBean.add(glaQuestion, glaIndicatorHashSet);

        return null;

    }

    @RequestMapping(value = "/viewall", method = RequestMethod.POST)
    public ModelAndView processIndicatorSearchForm( @RequestParam String action, @Valid @ModelAttribute("searchIndicatorForm") SearchIndicatorForm searchIndicatorForm, BindingResult bindingResult, HttpSession session) {

        ModelAndView model = null;
        if (bindingResult.hasErrors()) {
            return new ModelAndView("indicator_system/viewall_indicators");
        }
        if(action.equals("search")){

            processSearchParams(searchIndicatorForm);
            model = new ModelAndView("indicator_system/viewall_indicators");
            model.addObject("searchIndicatorForm", searchIndicatorForm);
        }
        else if(action.equals("load")){
            if(searchIndicatorForm.getSelectedQuestionName() == null || searchIndicatorForm.getSelectedQuestionName().isEmpty()) {
                model = new ModelAndView("indicator_system/viewall_indicators");
                model.addObject("searchIndicatorForm", searchIndicatorForm);
            }
            else{
                model = new ModelAndView("indicator_system/view_indicator_details");
                model.addObject("numberIndicator", retrieveQuestion(searchIndicatorForm.getSelectedQuestionName()));
            }
        }
        return model;
    }
    @RequestMapping(value = "/modify", method = RequestMethod.GET)
    public String getIndicatorsModify(Map<String, Object> model) {
        SearchIndicatorForm searchIndicatorForm = new SearchIndicatorForm();
        model.put("searchIndicatorForm", searchIndicatorForm);
        return  "indicator_system/modify_indicator";
    }
    @RequestMapping(value = "/delete", method = RequestMethod.GET)
    public String getIndicatorsDelete(Map<String, Object> model) {
        SearchIndicatorForm searchIndicatorForm = new SearchIndicatorForm();
        model.put("searchIndicatorForm", searchIndicatorForm);
        return  "indicator_system/delete_indicator";

    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ModelAndView processQuestionDeleteForm(@RequestParam String action,
                                                  @Valid @ModelAttribute("searchIndicatorForm") SearchIndicatorForm searchIndicatorForm,
                                                  BindingResult bindingResult,
                                                  HttpSession session) {

        ModelAndView model = null;
        if (bindingResult.hasErrors()) {
            return new ModelAndView("indicator_system/delete_indicator");
        }
        if (action.equals("search")) {
            processSearchParams(searchIndicatorForm);
            model = new ModelAndView("indicator_system/delete_indicator");
            model.addObject("searchIndicatorForm", searchIndicatorForm);
        }
        else if(action.equals("load")) {
            if (searchIndicatorForm.getSelectedQuestionName() == null || searchIndicatorForm.getSelectedQuestionName().isEmpty()) {
                model = new ModelAndView("indicator_system/delete_indicator");
                model.addObject("searchIndicatorForm", searchIndicatorForm);
            }
            else {

                Questions questions = retrieveQuestion(searchIndicatorForm.getSelectedQuestionName());
                IndicatorDeletionForm indicatorDeletionForm = new IndicatorDeletionForm();
                indicatorDeletionForm.getDeletionList().add(questions.getQuestionName());
                indicatorDeletionForm.setIndName(questions.getQuestionName());
                for(GenQuery gQ  : questions.getGenQueries()) {
                    indicatorDeletionForm.getDeletionList().add(gQ.getIndicatorName());
                }
                model = new ModelAndView("indicator_system/delete_indicator_details");
                model.addObject("indicatorDeletionForm", indicatorDeletionForm);
                model.addObject("numberIndicator", questions);
            }
        }
        return model;
    }

    @RequestMapping(value = "/processdelete", method = RequestMethod.POST)
    public ModelAndView processDeletion( @ModelAttribute("indicatorDeletionForm") IndicatorDeletionForm indicatorDeletionForm) {

        ModelAndView model = null;
        log.info("processDeletion : STARTED \n");
        if (indicatorDeletionForm.getSelectedList()== null ){
            Questions questions = retrieveQuestion(indicatorDeletionForm.getIndName());
            indicatorDeletionForm.getDeletionList().add(questions.getQuestionName());
            for(GenQuery gQ  : questions.getGenQueries()) {
                indicatorDeletionForm.getDeletionList().add(gQ.getIndicatorName());
            }
            model = new ModelAndView("indicator_system/delete_indicator_details");
            model.addObject("indicatorDeletionForm", indicatorDeletionForm);
            model.addObject("numberIndicator", questions);
            return model;
        }
        else{
            log.info("processDeletion : Working With Deletion Logic \n");
            GLAIndicatorDao glaIndicatorBean = (GLAIndicatorDao) appContext.getBean("glaIndicator");
            for(String name : indicatorDeletionForm.getSelectedList()){
                log.info("Selected Deletion Name :in  indicatorDeletionForm.getSelectedList()\t" + name+ "\n");
                List<GLAIndicator> glaIndicatorList = glaIndicatorBean.searchIndicatorsName(name,true, null, null, false);
                log.info("Searching for Name : in  Indicator List\t" + glaIndicatorList.size()+ "\n");
                if(glaIndicatorList.size() > 0){
                    log.info("Dumping Name : in  Indicator List\t" + glaIndicatorList.get(0).getIndicator_name()+ "\n");
                    long indicator_id = glaIndicatorBean.findIndicatorID(glaIndicatorList.get(0).getIndicator_name());
                    if(indicator_id != 0 )
                        glaIndicatorBean.deleteIndicator(indicator_id);
                }
                /*else{
                    GLAQueriesDao glaQueriesBean = (GLAQueriesDao) appContext.getBean("glaQueries");
                    List<GLAQueries> glaQueriesList = glaQueriesBean.searchQuestionsName(name, true);
                    if(glaQueriesList.size() > 0){
                        long question_id = glaQueriesBean.findQuestionID(glaQueriesList.get(0).getQuestion_name());
                        if(question_id != 0 )
                            glaQueriesBean.deleteQuestion(question_id);

                    }
                }*/
            }
        }
        return model;
    }
    @RequestMapping(value = "/trialrun", method = RequestMethod.GET)
    public String getQuestionsTrialRun(Map<String, Object> model) {
        QuestionRun questionRun = new QuestionRun();
        GLAQuestionDao glaQuestionsBean = (GLAQuestionDao) appContext.getBean("glaQuestions");
        List<GLAQuestion> glaQuestionList = glaQuestionsBean.displayAll(null, null, false);
        for(GLAQuestion gQ : glaQuestionList){
            questionRun.getAvailableQuestions().add(gQ.getQuestion_name());
        }
        model.put("questionRun", questionRun);
        return "indicator_system/trial_run";
    }

    @RequestMapping(value = "/trialrun", method = RequestMethod.POST)
    public String processTrialRun( @Valid @ModelAttribute("questionRun") QuestionRun questionRun,
                                         BindingResult bindingResult,
                                         Map<String, Object> model) {

        if (bindingResult.hasErrors()) {
            return "indicator_system/trial_run";
        }

        if(questionRun.getSelectedChartEngine().equals("JFreeGraph")) {
            model.put("chartType", questionRun.getSelectedChartType());
            model.put("questionName", questionRun.getSelectedQuestion());
            return "indicator_system/run_results";
        }
        else if (questionRun.getSelectedChartEngine().equals("CEWOLF")) {
            model.put("pageViews", PageViews);
            return "indicator_system/run_results_cewolf";
        }

       return null;
    }

    @RequestMapping(value = "/fetchExistingIndicatorsData.web", method = RequestMethod.GET, produces = "application/json")
    public @ResponseBody
    String fetchIndicatorData(HttpServletRequest request) throws IOException {

        GLAIndicatorDao glaIndicatorBean = (GLAIndicatorDao) appContext.getBean("glaIndicator");
        List<GLAIndicator> glaIndicatorList = null;
        List<GLAIndicator> pageGLAindicatorList = new ArrayList<>();
        Integer idisplayStart = 0;
        Integer iSortingCols =0;
        if(null != request.getParameter("iSortingCols"))
            iSortingCols = Integer.valueOf(request.getParameter("iSortingCols"));
        GLAIndicatorJsonObject glaIndicatorJsonObject = new GLAIndicatorJsonObject();
        if (null != request.getParameter("iDisplayStart")) {
            idisplayStart = Integer.valueOf(request.getParameter("iDisplayStart"));
            log.info("iDisplayStart : \t" + request.getParameter("iDisplayStart") + "\n");
        }
        //Fetch search parameter
        String searchParameter = request.getParameter("sSearch");
        log.info("sSearch : \t"+ searchParameter+"\n");
        //Fetch Page display length
        Integer pageDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
        log.info("iDisplayLength : \t"+ pageDisplayLength+"\n");
        //Create page list data
        if(searchParameter == null || searchParameter.isEmpty()) {
            String colName = null;
            String sortDirection =null;
            if(iSortingCols == 1 ) {
                Integer isortCol = Integer.valueOf(request.getParameter("iSortCol_0"));
                sortDirection = request.getParameter("sSortDir_0");
                if (isortCol == 0)
                    colName = "id";
                else if (isortCol == 1)
                    colName = "indicator_name";
                else if (isortCol == 2)
                    colName = "short_name";
                glaIndicatorList = glaIndicatorBean.displayall(colName,sortDirection,true);
            }
            else
                glaIndicatorList = glaIndicatorBean.displayall(colName,sortDirection,false);
            if(idisplayStart != -1){
                Integer endRange = idisplayStart+pageDisplayLength;
                if(endRange >glaIndicatorList.size())
                    endRange = glaIndicatorList.size();
                for(int i=idisplayStart; i<endRange; i++){
                    pageGLAindicatorList.add(glaIndicatorList.get(i));
                }
            }
            //Set Total display record
            glaIndicatorJsonObject.setiTotalDisplayRecords(glaIndicatorBean.getTotalIndicators());
            //Set Total record
            glaIndicatorJsonObject.setiTotalRecords(glaIndicatorBean.getTotalIndicators());
            glaIndicatorJsonObject.setAaData(pageGLAindicatorList);
        }
        else {
            String colName = null;
            String sortDirection =null;
            if(iSortingCols == 1 ) {
                Integer isortCol = Integer.valueOf(request.getParameter("iSortCol_0"));
                sortDirection = request.getParameter("sSortDir_0");
                if (isortCol == 0)
                    colName = "id";
                else if (isortCol == 1)
                    colName = "indicator_name";
                else if (isortCol == 2)
                    colName = "short_name";
                glaIndicatorList = glaIndicatorBean.searchIndicatorsName(searchParameter, false,colName,sortDirection,true);
            }
            else
                glaIndicatorList = glaIndicatorBean.searchIndicatorsName(searchParameter, false,colName,sortDirection,false);
            if(idisplayStart != -1) {
                Integer endRange = idisplayStart+pageDisplayLength;
                Integer startRange = idisplayStart;
                if(startRange > glaIndicatorList.size())
                    startRange = 0;
                if (endRange > glaIndicatorList.size())
                    endRange = glaIndicatorList.size();
                for (int i = startRange; i <endRange; i++) {
                    pageGLAindicatorList.add(glaIndicatorList.get(i));
                }
            }
            //Set Total display record
            glaIndicatorJsonObject.setiTotalDisplayRecords(glaIndicatorList.size());
            //Set Total record
            glaIndicatorJsonObject.setiTotalRecords(glaIndicatorList.size());
            glaIndicatorJsonObject.setAaData(pageGLAindicatorList);
        }
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(glaIndicatorJsonObject);
    }

    private Questions retrieveQuestion(String questionName){
        log.info("reteriveQuestion : STARTED \n");
        GLAQuestionDao glaQuestionsBean = (GLAQuestionDao) appContext.getBean("glaQuestions");
        log.info("Retreive From DB : STARTED \n");
        log.info("Name : \t"+ questionName);
        long question_id = glaQuestionsBean.findQuestionID(questionName);
        GLAQuestion glaQuestion = glaQuestionsBean.loadByQuestionID(question_id);
        Questions questions = new Questions();
        questions.setQuestionId(glaQuestion.getId());
        questions.setQuestionName(glaQuestion.getQuestion_name());
        log.info("GLA QUESTION FROM DB : ID : \t"+ glaQuestion.getId());
        log.info("GLA QUESTION FROM DB : Name : \t"+ glaQuestion.getQuestion_name());
        for( GLAIndicator glaIndicators : glaQuestion.getGlaIndicators()){
            log.info("GLA Indicator FROM DB : Name : \t"+ glaIndicators.getIndicator_name());
            log.info("GLA Indicator FROM DB : ID : \t"+ glaIndicators.getId());
            log.info("GLA Indicator FROM DB : HQL : \t"+ glaIndicators.getHql());
            log.info("GLA Indicator FROM DB : Short Name : \t"+ glaIndicators.getShort_name());
            log.info("GLA Indicator FROM DB : PROPS ID : \t"+ glaIndicators.getGlaIndicatorProps().getId());
            log.info("GLA INDICATOR FROM DB : LEX TIME : \t"+ glaIndicators.getGlaIndicatorProps().getLast_executionTime());
            log.info("GLA INDICATOR FROM DB : EXEC COUNTER : \t"+ glaIndicators.getGlaIndicatorProps().getTotalExecutions());
            GenQuery genQuery = new GenQuery(glaIndicators.getHql(),glaIndicators.getIndicator_name(),
                    glaIndicators.getId());
            genQuery.setGenIndicatorProps(glaIndicators.getGlaIndicatorProps().getId(),glaIndicators.getGlaIndicatorProps().getLast_executionTime(),
                    glaIndicators.getGlaIndicatorProps().getTotalExecutions());
            questions.getGenQueries().add(genQuery);
        }
        log.info("reteriveQuestion : ENDED \n");
        return questions;

    }
    private void processSearchParams(SearchIndicatorForm searchIndicatorForm){
        log.info("processSearchParams : STARTED \n");
        GLAIndicatorDao glaIndicatorBean = (GLAIndicatorDao) appContext.getBean("glaIndicator");
        GLAIndicator glaIndicator = null;
        List<GLAIndicator> glaIndicatorList;
        if(NumberChecks.isNumeric(searchIndicatorForm.getSearchField()) && searchIndicatorForm.getSelectedSearchType().equals("ID")) {
            glaIndicator = glaIndicatorBean.loadByIndicatorID(Long.parseLong(searchIndicatorForm.getSearchField()));
            if(glaIndicator != null) {
                log.info("GLA INDICATOR FROM DB SEARCH: ID : \t"+ glaIndicator.getId());
                searchIndicatorForm.getSearchResults().add(glaIndicator.getIndicator_name());
            }
        }
        else if (!NumberChecks.isNumeric(searchIndicatorForm.getSearchField()) && searchIndicatorForm.getSelectedSearchType().equals("Indicator Name")) {
            glaIndicatorList = glaIndicatorBean.loadByIndicatorByName(searchIndicatorForm.getSearchField(),false);
            if(glaIndicatorList != null) {
                for(GLAIndicator gI : glaIndicatorList){
                    log.info("GLA INDICATOR FROM DB SEARCH: NAME : \t"+ gI.getIndicator_name());
                    searchIndicatorForm.getSearchResults().add(gI.getIndicator_name());
                }
            }
        }
        log.info("processSearchParams : ENDED \n");
    }
}

class Categories {

    String type;
    String major;
    String minor;

    Categories(String type, String major, String minor) {
        this.major = major;
        this.minor = minor;
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }
}

class CurrentIndicatorSummary {

    String name;
    String platform;
    String action;
    String chartType;
    String chartEngine;
    String hql;
    int entityFilters;
    int userFilters;
    int sessionFilters;
    int timeFilters;

    CurrentIndicatorSummary() {}
    CurrentIndicatorSummary(String name, String platform, String action, String chartType, String chartEngine,
                            String hql, int entityFilters, int userFilters, int sessionFilters, int timeFilters) {
        this.name = name;
        this.action = action;
        this.platform = platform;
        this.chartEngine = chartEngine;
        this.chartType = chartType;
        this.hql = hql;
        this.entityFilters = entityFilters;
        this.userFilters = userFilters;
        this.sessionFilters = sessionFilters;
        this.timeFilters = timeFilters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getChartType() {
        return chartType;
    }

    public void setChartType(String chartType) {
        this.chartType = chartType;
    }

    public String getChartEngine() {
        return chartEngine;
    }

    public void setChartEngine(String chartEngine) {
        this.chartEngine = chartEngine;
    }

    public String getHql() {
        return hql;
    }

    public void setHql(String hql) {
        this.hql = hql;
    }

    public int getEntityFilters() {
        return entityFilters;
    }

    public void setEntityFilters(int entityFilters) {
        this.entityFilters = entityFilters;
    }

    public int getUserFilters() {
        return userFilters;
    }

    public void setUserFilters(int userFilters) {
        this.userFilters = userFilters;
    }

    public int getSessionFilters() {
        return sessionFilters;
    }

    public void setSessionFilters(int sessionFilters) {
        this.sessionFilters = sessionFilters;
    }

    public int getTimeFilters() {
        return timeFilters;
    }

    public void setTimeFilters(int timeFilters) {
        this.timeFilters = timeFilters;
    }
}