package com.indicator_engine.indicator_system;

import com.indicator_engine.dao.*;
import com.indicator_engine.datamodel.GLAIndicator;
import com.indicator_engine.datamodel.GLAQueries;
import com.indicator_engine.model.indicator_system.Number.*;
import com.indicator_engine.model.indicator_system.IndicatorDefnOperationForm;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Tanmaya Mahapatra on 31-03-2015.
 */
@SuppressWarnings({"unused", "unchecked"})
public class IndicatorPreProcessing implements IndicatorPreProcessingDao {
    static Logger log = Logger.getLogger(IndicatorPreProcessingDao.class.getName());
    @Autowired
    private ApplicationContext appContext;

    @Override
    public String retrieveOperation(IndicatorDefnOperationForm indicatorDefnOperationForm) {
        return indicatorDefnOperationForm.getSelectedOperation();
    }

    @Override
    public SelectNumberParameters initSelectNumberParametersObject() {
        SelectNumberParameters obj = new SelectNumberParameters();
        //obj.getEntityValues().add(new EntityValues());
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        obj.setSource(glaEventBean.selectAll("source"));
        obj.setPlatform(glaEventBean.selectAll("platform"));
        obj.setAction(glaEventBean.selectAll("action"));
        return obj;
    }

    @Override
    public List<String> initPopulateMinors(SelectNumberParameters selectNumberParameters) {
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        GLACategoryDao glacategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        List<String> minor = null;
        long category_id = glaEventBean.findCategoryId(selectNumberParameters.getSelectedAction(), selectNumberParameters.getSelectedSource(),
                selectNumberParameters.getSelectedPlatform());
        minor = glacategoryBean.findCategoryByID(category_id, "minor");
        return minor;
    }

    @Override
    public List<String> initPopulateMajors(SelectNumberParameters selectNumberParameters) {
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        GLACategoryDao glacategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        List<String> major = null;
        long category_id = glaEventBean.findCategoryId(selectNumberParameters.getSelectedAction(), selectNumberParameters.getSelectedSource(),
                selectNumberParameters.getSelectedPlatform());
        major = glacategoryBean.findCategoryByID(category_id, "major");
        return major;
    }

    @Override
    public List<String> initPopulateTypes(SelectNumberParameters selectNumberParameters) {
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        GLACategoryDao glacategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        List<String> types = null;
        long category_id = glaEventBean.findCategoryId(selectNumberParameters.getSelectedAction(), selectNumberParameters.getSelectedSource(),
                selectNumberParameters.getSelectedPlatform());
        types = glacategoryBean.findCategoryByID(category_id, "type");
        return types;
    }

    @Override
    public IndicatorDefnOperationForm initAvailableOperations_DB() {
        IndicatorDefnOperationForm obj = new IndicatorDefnOperationForm();
        GLAOperationsDao glaOperationsBean = (GLAOperationsDao) appContext.getBean("glaOperations");
        obj.setOperation(glaOperationsBean.selectAllOperations());
        return obj;

    }

    @Override
    public List<String> initAvailableEntities_DB(String minor) {
        GLACategoryDao glacategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        long category_id = glacategoryBean.findCategoryID(minor);
        GLAEntityDao glaEntityBean = (GLAEntityDao) appContext.getBean("glaEntity");
        return glaEntityBean.loadEntitiesByCategoryID(category_id);
    }

    @Override
    public List<String> initAvailableEvents_DB(String minor) {
        GLACategoryDao glacategoryBean = (GLACategoryDao) appContext.getBean("glaCategory");
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        long category_id = glacategoryBean.findCategoryID(minor);
        return glaEventBean.loadEventByCategoryID(category_id);
    }

    @Override
    public void manageEValues(SelectNumberParameters selectNumberParameters) {

            selectNumberParameters.getEntityValues().add(new EntityValues(selectNumberParameters.getSelectedKeys(),
                    selectNumberParameters.getSelectedentityValueTypes(), selectNumberParameters.getEvalue()));
    }

    @Override
    public void addDefaultEValues(SelectNumberParameters selectNumberParameters) {
        if ((selectNumberParameters.getEntityValues().isEmpty()))
            selectNumberParameters.getEntityValues().add(new EntityValues(selectNumberParameters.getSelectedKeys(),
                    selectNumberParameters.getSelectedentityValueTypes(), "ALL"));

    }

    @Override
    public void clearEValuesSpecifications(SelectNumberParameters selectNumberParameters) {
        selectNumberParameters.getEntityValues().clear();
    }

    @Override
    public void specifyNewUser(SelectNumberParameters selectNumberParameters) {
        selectNumberParameters.getUserSpecifications().add(new UserSearchSpecifications(selectNumberParameters.getSelecteduserSearchTypes(),
                selectNumberParameters.getSelectedUserString(), selectNumberParameters.getSelectedSearchType()));
    }

    @Override
    public void clearUserSpecifications(SelectNumberParameters selectNumberParameters) {
        selectNumberParameters.getUserSpecifications().clear();
    }

    @Override
    public void searchUser(SelectNumberParameters selectNumberParameters) {
        log.info("Within Search User Method : Indicator PreProcessing");
        GLAUserDao glauserBean = (GLAUserDao) appContext.getBean("glaUser");
        selectNumberParameters.setSearchResults(glauserBean.searchSimilarUserDetails(
                selectNumberParameters.getSelecteduserSearchTypes(), selectNumberParameters.getSearchUserString()));

    }
    @Override
    public void specifyNewSession(SelectNumberParameters selectNumberParameters){
        selectNumberParameters.getSessionSpecifications().add(new SessionSpecifications(selectNumberParameters.getSelectedSearchType(),
                selectNumberParameters.getSelectedUserString()));

    }
    @Override
    public void clearSessionSpecifications(SelectNumberParameters selectNumberParameters){
        selectNumberParameters.getSessionSpecifications().clear();
    }
    @Override
    public void searchSession(SelectNumberParameters selectNumberParameters){
        log.info("Within Search Session Method : Indicator PreProcessing");
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        selectNumberParameters.setSearchResults(glaEventBean.searchSimilarSessionDetails(selectNumberParameters.getSelectedsessionSearchType(),
                selectNumberParameters.getSessionSearch()));


    }
    @Override
    public void specifyNewTime(SelectNumberParameters selectNumberParameters){
        selectNumberParameters.getTimeSpecifications().add(new TimeSearchSpecifications(selectNumberParameters.getSelectedTimeType(),
                selectNumberParameters.getSelectedSearchStrings()));
    }
    @Override
    public void clearTimeSpecifications(SelectNumberParameters selectNumberParameters){
        selectNumberParameters.getTimeSpecifications().clear();
    }
    @Override
    public void searchTime(SelectNumberParameters selectNumberParameters){
        log.info("Within Search Time Method : Indicator PreProcessing");
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        selectNumberParameters.setSearchResults(glaEventBean.searchSimilarTimeDetails(selectNumberParameters.getSelectedTimeSearchType(),
                selectNumberParameters.getTimeSearch()));

    }
    @Override
    public void clearSearchSettings(SelectNumberParameters selectNumberParameters) {
        selectNumberParameters.getSearchResults().clear();
        selectNumberParameters.setSelectedSearchType("");

    }
    @Override
    public void addQuestion(SelectNumberParameters selectNumberParameters, NumberIndicator numberIndicator){
        numberIndicator.getGenQueries().add(new GenQuery(selectNumberParameters.getHql(), selectNumberParameters.getQuestionName()));
        log.info("Dumping Questions : \n");
        for(GenQuery gQ : numberIndicator.getGenQueries()){
            log.info(gQ.getQueryID() + "\t" + gQ.getQuery()+ "\n");
        }

    }
    @Override
    public void saveIndicator(NumberIndicator numberIndicator){
        log.info("Saving Indicator and all its Questions/Queries : STARTED");
        GLAIndicatorDao glaIndicatorBean = (GLAIndicatorDao) appContext.getBean("glaIndicator");
        GLAQueriesDao glaQueriesBean = (GLAQueriesDao) appContext.getBean("glaQueries");
        GLAIndicator glaIndicator = new GLAIndicator();
        GLAQueries glaQueries = new GLAQueries();
        glaIndicator.setIndicator_name(numberIndicator.getIndicatorName());
        glaQueries.setHql(numberIndicator.getGenQueries().get(0).getQuery());
        glaQueries.setQuestion_name(numberIndicator.getGenQueries().get(0).getQuestionName());
        numberIndicator.setIndicator_id(glaIndicatorBean.add(glaIndicator, glaQueries));
        for(int i =1 ; i < numberIndicator.getGenQueries().size(); i++) {
            glaQueriesBean.addWithExistingIndicator(new GLAQueries(numberIndicator.getGenQueries().get(i).getQuery(),
                            numberIndicator.getGenQueries().get(i).getQuestionName()),glaIndicator.getId());
        }
        log.info("Saving Indicator and all its Questions/Queries : ENDED");


    }

    @Override
    public void retreiveFromDB(NumberIndicator numberIndicator){

        GLAIndicatorDao glaIndicatorBean = (GLAIndicatorDao) appContext.getBean("glaIndicator");
        List<GLAIndicator> glaIndicators = glaIndicatorBean.loadByIndicatorID(numberIndicator.getIndicator_id());
        for(GLAIndicator glaIndicator : glaIndicators){
            numberIndicator.reset();
            numberIndicator.setIndicator_id(glaIndicator.getId());
            numberIndicator.setIndicatorName(glaIndicator.getIndicator_name());
            numberIndicator.setGenIndicatorProps(new GenIndicatorProps());
            numberIndicator.getGenIndicatorProps().setLast_executionTime(glaIndicator.getGlaIndicatorProps().getLast_executionTime());
            numberIndicator.getGenIndicatorProps().setProps_id(glaIndicator.getGlaIndicatorProps().getId());
            numberIndicator.getGenIndicatorProps().setTotalExecutions(glaIndicator.getGlaIndicatorProps().getTotalExecutions());
            Set<GLAQueries> genQueries = glaIndicator.getQueries();
            for (GLAQueries gQ : genQueries) {
                numberIndicator.getGenQueries().add(new GenQuery(gQ.getHql(),gQ.getQuestion_name()));
            }
        }
    }

    @Override
    public void flushAll(NumberIndicator numberIndicator, SelectNumberParameters selectNumberParameters,
                                 IndicatorDefnOperationForm availableOperations){

        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        GLAOperationsDao glaOperationsBean = (GLAOperationsDao) appContext.getBean("glaOperations");

        numberIndicator.reset();
        selectNumberParameters.reset();
        availableOperations.reset();

        selectNumberParameters.setSource(glaEventBean.selectAll("source"));
        selectNumberParameters.setPlatform(glaEventBean.selectAll("platform"));
        selectNumberParameters.setAction(glaEventBean.selectAll("action"));
        availableOperations.setOperation(glaOperationsBean.selectAllOperations());
    }

    @Override
    public void flushPrevQnData(SelectNumberParameters selectNumberParameters){
        GLAEventDao glaEventBean = (GLAEventDao) appContext.getBean("glaEvent");
        selectNumberParameters.reset();
        selectNumberParameters.setSource(glaEventBean.selectAll("source"));
        selectNumberParameters.setPlatform(glaEventBean.selectAll("platform"));
        selectNumberParameters.setAction(glaEventBean.selectAll("action"));


    }
}